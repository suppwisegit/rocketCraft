package de.rocketcraft.tnt.listener;

import de.rocketcraft.tnt.model.CustomTntSpec;
import de.rocketcraft.tnt.service.CustomTntItemService;
import de.rocketcraft.tnt.service.CustomTntRegistry;
import de.rocketcraft.tnt.type.CustomTntType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class CustomTntUseListener implements Listener {

    private static final String PRIMED_TNT_TYPE_KEY = "custom_tnt_primed_type";
    private static final String PRIMED_TNT_OWNER_KEY = "custom_tnt_owner";

    private final JavaPlugin plugin;
    private final CustomTntRegistry customTntRegistry;
    private final CustomTntItemService customTntItemService;
    private final NamespacedKey primedTntTypeKey;
    private final NamespacedKey primedTntOwnerKey;

    public CustomTntUseListener(JavaPlugin plugin, CustomTntRegistry customTntRegistry, CustomTntItemService customTntItemService) {
        this.plugin = plugin;
        this.customTntRegistry = customTntRegistry;
        this.customTntItemService = customTntItemService;
        this.primedTntTypeKey = new NamespacedKey(plugin, PRIMED_TNT_TYPE_KEY);
        this.primedTntOwnerKey = new NamespacedKey(plugin, PRIMED_TNT_OWNER_KEY);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        Optional<CustomTntType> customTntType = customTntItemService.getCustomTntType(item);
        if (customTntType.isEmpty()) {
            return;
        }

        Player player = event.getPlayer();
        CustomTntSpec spec = customTntRegistry.getByType(customTntType.get()).orElse(null);
        if (spec == null) {
            player.sendMessage("§cDieses TNT ist nicht registriert.");
            return;
        }

        spawnPrimedTnt(player, spec);
        item.setAmount(item.getAmount() - 1);
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCustomTntExplode(EntityExplodeEvent event) {
        if (!(event.getEntity() instanceof TNTPrimed primed)) {
            return;
        }

        PersistentDataContainer data = primed.getPersistentDataContainer();
        String typeName = data.get(primedTntTypeKey, PersistentDataType.STRING);
        if (typeName == null) {
            return;
        }

        CustomTntType type;
        try {
            type = CustomTntType.valueOf(typeName);
        } catch (IllegalArgumentException ex) {
            return;
        }

        event.blockList().clear();
        event.setYield(0);

        UUID owner = null;
        String ownerString = data.get(primedTntOwnerKey, PersistentDataType.STRING);
        if (ownerString != null) {
            try {
                owner = UUID.fromString(ownerString);
            } catch (IllegalArgumentException ignored) {
                owner = null;
            }
        }

        applyEffect(type, primed.getLocation(), owner);
    }

    private void spawnPrimedTnt(Player player, CustomTntSpec spec) {
        TNTPrimed primed = player.getWorld().spawn(player.getEyeLocation().add(player.getLocation().getDirection().multiply(0.6)), TNTPrimed.class);
        primed.setFuseTicks(spec.fuseTicks());
        primed.setYield(spec.blastRadius());
        primed.setVelocity(player.getLocation().getDirection().normalize().multiply(0.9).setY(0.35));

        PersistentDataContainer data = primed.getPersistentDataContainer();
        data.set(primedTntTypeKey, PersistentDataType.STRING, spec.type().name());
        data.set(primedTntOwnerKey, PersistentDataType.STRING, player.getUniqueId().toString());

        player.getWorld().spawnParticle(Particle.SMOKE, player.getEyeLocation(), 20, 0.2, 0.2, 0.2, 0.02);
        player.playSound(player.getLocation(), Sound.ENTITY_TNT_PRIMED, 1.0F, 1.05F);
    }

    private void applyEffect(CustomTntType type, Location location, UUID ownerUuid) {
        switch (type) {
            case METEOR -> meteor(location, ownerUuid);
            case CRYO -> cryo(location, ownerUuid);
            case VORTEX -> vortex(location, ownerUuid);
            case PHOTON -> photon(location, ownerUuid);
            case QUAKE -> quake(location, ownerUuid);
            case TOXIC -> toxic(location, ownerUuid);
            case GUARDIAN -> guardian(location, ownerUuid);
            case ANTI_GRAV -> antiGrav(location, ownerUuid);
            case CHAIN_REACTION -> chainReaction(location, ownerUuid);
            case STASIS -> stasis(location, ownerUuid);
            case SHOCKWAVE -> shockwave(location, ownerUuid);
            case WEBTRAP -> webtrap(location, ownerUuid);
            case LOOTBURST -> lootburst(location);
            case WARP -> warp(location, ownerUuid);
            case DOOM_CLOCK -> doomClock(location, ownerUuid);
        }
    }

    private void meteor(Location center, UUID ownerUuid) {
        center.getWorld().createExplosion(center, 4.2F, true, false);
        center.getWorld().spawnParticle(Particle.FLAME, center, 130, 1.7, 1.2, 1.7, 0.04);
        center.getWorld().playSound(center, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1.2F, 0.8F);

        for (Block block : blocksInRadius(center, 3)) {
            if (block.getType().isAir()) {
                continue;
            }
            if (block.getRelative(0, 1, 0).getType().isAir()) {
                block.getRelative(0, 1, 0).setType(Material.FIRE);
            }
        }

        damageAround(center, ownerUuid, 4.2, 9.0, 1.0);
    }

    private void cryo(Location center, UUID ownerUuid) {
        center.getWorld().spawnParticle(Particle.SNOWFLAKE, center, 200, 2, 1, 2, 0.03);
        center.getWorld().playSound(center, Sound.BLOCK_GLASS_BREAK, 1.0F, 0.5F);

        for (LivingEntity living : nearbyLiving(center, 4.0, ownerUuid)) {
            living.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 120, 3));
            living.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 80, 1));
            living.setFreezeTicks(Math.max(living.getFreezeTicks(), 120));
            living.damage(4.0);
        }

        for (Block block : blocksInRadius(center, 2)) {
            if (block.getType().isAir() && block.getRelative(0, -1, 0).getType().isSolid()) {
                block.setType(Material.ICE);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (block.getType() == Material.ICE) {
                        block.setType(Material.AIR);
                    }
                }, 60L);
            }
        }
    }

    private void vortex(Location center, UUID ownerUuid) {
        center.getWorld().spawnParticle(Particle.PORTAL, center, 250, 1.5, 1.5, 1.5, 0.8);
        center.getWorld().playSound(center, Sound.BLOCK_BEACON_POWER_SELECT, 1.0F, 0.6F);

        for (LivingEntity living : nearbyLiving(center, 6.0, ownerUuid)) {
            Vector pull = center.toVector().subtract(living.getLocation().toVector()).normalize().multiply(1.2).setY(0.28);
            living.setVelocity(living.getVelocity().add(pull));
            living.damage(5.0);
        }
    }

    private void photon(Location center, UUID ownerUuid) {
        center.getWorld().spawnParticle(Particle.FLASH, center, 5, 0.3, 0.3, 0.3, 0);
        center.getWorld().spawnParticle(Particle.END_ROD, center, 120, 1.2, 1.2, 1.2, 0.04);
        center.getWorld().playSound(center, Sound.ENTITY_FIREWORK_ROCKET_TWINKLE_FAR, 1.0F, 1.7F);

        for (LivingEntity living : nearbyLiving(center, 5.0, ownerUuid)) {
            living.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 0));
            living.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 140, 0));
            living.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 80, 0));
            living.damage(3.5);
        }
    }

    private void quake(Location center, UUID ownerUuid) {
        center.getWorld().playSound(center, Sound.ENTITY_WARDEN_SONIC_BOOM, 1.0F, 0.75F);
        center.getWorld().spawnParticle(Particle.BLOCK, center, 180, 2, 0.4, 2, Material.DIRT.createBlockData());

        for (LivingEntity living : nearbyLiving(center, 5.0, ownerUuid)) {
            Vector knock = living.getLocation().toVector().subtract(center.toVector()).normalize().multiply(0.8).setY(1.0);
            living.setVelocity(living.getVelocity().add(knock));
            living.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 2));
            living.damage(6.0);
        }
    }

    private void toxic(Location center, UUID ownerUuid) {
        center.getWorld().spawnParticle(Particle.SNEEZE, center, 220, 1.8, 1.2, 1.8, 0.04);
        center.getWorld().playSound(center, Sound.BLOCK_BREWING_STAND_BREW, 1.0F, 0.8F);

        for (LivingEntity living : nearbyLiving(center, 5.0, ownerUuid)) {
            living.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 160, 1));
            living.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 160, 1));
            living.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 60, 0));
            living.damage(2.0);
        }
    }

    private void guardian(Location center, UUID ownerUuid) {
        center.getWorld().spawnParticle(Particle.HEART, center, 60, 1.3, 1.0, 1.3, 0.02);
        center.getWorld().playSound(center, Sound.BLOCK_BEACON_ACTIVATE, 1.0F, 1.2F);

        for (Entity entity : center.getWorld().getNearbyEntities(center, 5.0, 5.0, 5.0)) {
            if (!(entity instanceof Player player)) {
                continue;
            }
            if (ownerUuid != null && !player.getUniqueId().equals(ownerUuid)) {
                continue;
            }
            player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + 8.0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 120, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 100, 0));
            player.setFireTicks(0);
        }
    }

    private void antiGrav(Location center, UUID ownerUuid) {
        center.getWorld().spawnParticle(Particle.CLOUD, center, 200, 1.4, 1.0, 1.4, 0.03);
        center.getWorld().playSound(center, Sound.ENTITY_SHULKER_SHOOT, 1.0F, 0.65F);

        for (LivingEntity living : nearbyLiving(center, 5.0, ownerUuid)) {
            living.setVelocity(living.getVelocity().add(new Vector(0, 1.35, 0)));
            living.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 80, 0));
            living.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 0));
        }
    }

    private void chainReaction(Location center, UUID ownerUuid) {
        center.getWorld().playSound(center, Sound.ENTITY_TNT_PRIMED, 1.0F, 1.3F);
        for (int i = 0; i < 3; i++) {
            int delay = i * 10;
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Location waveCenter = center.clone().add((Math.random() - 0.5) * 2.0, 0, (Math.random() - 0.5) * 2.0);
                waveCenter.getWorld().createExplosion(waveCenter, 2.6F, false, false);
                waveCenter.getWorld().spawnParticle(Particle.EXPLOSION, waveCenter, 10, 0.3, 0.3, 0.3, 0.01);
                damageAround(waveCenter, ownerUuid, 3.2, 4.0, 0.7);
            }, delay);
        }
    }

    private void stasis(Location center, UUID ownerUuid) {
        center.getWorld().spawnParticle(Particle.ENCHANT, center, 260, 1.7, 1.0, 1.7, 0.6);
        center.getWorld().playSound(center, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.0F, 0.9F);

        for (LivingEntity living : nearbyLiving(center, 4.5, ownerUuid)) {
            living.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 40, 1));
            living.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 80, 4));
            living.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 120, 2));
            living.damage(3.0);
        }
    }

    private void shockwave(Location center, UUID ownerUuid) {
        center.getWorld().spawnParticle(Particle.SONIC_BOOM, center, 2, 0, 0, 0, 0);
        center.getWorld().playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1.2F, 1.1F);

        for (LivingEntity living : nearbyLiving(center, 7.0, ownerUuid)) {
            Vector push = living.getLocation().toVector().subtract(center.toVector()).normalize().multiply(1.8).setY(0.55);
            living.setVelocity(living.getVelocity().add(push));
            living.damage(4.0);
        }
    }

    private void webtrap(Location center, UUID ownerUuid) {
        center.getWorld().spawnParticle(Particle.WAX_ON, center, 120, 1.4, 0.6, 1.4, 0.01);
        center.getWorld().playSound(center, Sound.ENTITY_SPIDER_DEATH, 1.0F, 0.8F);

        List<Block> placedWebs = new ArrayList<>();
        for (Block block : blocksInRadius(center, 2)) {
            if (block.getType().isAir() && block.getRelative(0, -1, 0).getType().isSolid()) {
                block.setType(Material.COBWEB);
                placedWebs.add(block);
            }
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Block web : placedWebs) {
                if (web.getType() == Material.COBWEB) {
                    web.setType(Material.AIR);
                }
            }
        }, 90L);

        for (LivingEntity living : nearbyLiving(center, 4.0, ownerUuid)) {
            living.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 140, 5));
            living.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 1));
            living.damage(3.0);
        }
    }

    private void lootburst(Location center) {
        center.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, center, 80, 1.2, 0.8, 1.2, 0.03);
        center.getWorld().playSound(center, Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.2F);

        drop(center, Material.GOLDEN_APPLE, 1 + (int) (Math.random() * 2));
        drop(center, Material.ENDER_PEARL, 1 + (int) (Math.random() * 3));
        drop(center, Material.COOKED_BEEF, 4 + (int) (Math.random() * 5));
        if (Math.random() < 0.35) {
            drop(center, Material.EXPERIENCE_BOTTLE, 3 + (int) (Math.random() * 5));
        }
    }

    private void warp(Location center, UUID ownerUuid) {
        center.getWorld().spawnParticle(Particle.REVERSE_PORTAL, center, 200, 1.8, 1.0, 1.8, 0.08);
        center.getWorld().playSound(center, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.9F);

        for (LivingEntity living : nearbyLiving(center, 5.5, ownerUuid)) {
            Location destination = center.clone().add((Math.random() - 0.5) * 12.0, 0, (Math.random() - 0.5) * 12.0);
            destination.setY(destination.getWorld().getHighestBlockYAt(destination) + 1.0);
            living.teleport(destination);
            living.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 1));
            living.damage(2.5);
        }
    }

    private void doomClock(Location center, UUID ownerUuid) {
        center.getWorld().spawnParticle(Particle.LAVA, center, 80, 1.2, 0.2, 1.2, 0.04);
        center.getWorld().playSound(center, Sound.BLOCK_NOTE_BLOCK_BASS, 1.2F, 0.5F);
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= 60) {
                    center.getWorld().createExplosion(center, 6.5F, false, false);
                    center.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, center, 1, 0, 0, 0, 0);
                    center.getWorld().playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 2.0F, 0.6F);
                    damageAround(center, ownerUuid, 7.0, 14.0, 1.3);
                    cancel();
                    return;
                }

                center.getWorld().spawnParticle(Particle.SMOKE, center, 12, 0.5, 0.2, 0.5, 0.01);
                center.getWorld().playSound(center, Sound.BLOCK_NOTE_BLOCK_HAT, 0.5F, 1.0F + (ticks / 120F));
                ticks += 10;
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }

    private void drop(Location location, Material material, int amount) {
        ItemStack stack = new ItemStack(material, Math.max(1, amount));
        Item item = location.getWorld().dropItemNaturally(location, stack);
        item.setVelocity(new Vector((Math.random() - 0.5) * 0.2, 0.25, (Math.random() - 0.5) * 0.2));
    }

    private void damageAround(Location center, UUID ownerUuid, double radius, double damage, double knockbackStrength) {
        for (LivingEntity living : nearbyLiving(center, radius, ownerUuid)) {
            living.damage(damage);
            Vector knock = living.getLocation().toVector().subtract(center.toVector()).normalize().multiply(knockbackStrength).setY(0.3);
            living.setVelocity(living.getVelocity().add(knock));
        }
    }

    private List<LivingEntity> nearbyLiving(Location center, double radius, UUID ownerUuid) {
        List<LivingEntity> entities = new ArrayList<>();
        for (Entity entity : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (!(entity instanceof LivingEntity living)) {
                continue;
            }
            if (ownerUuid != null && ownerUuid.equals(living.getUniqueId())) {
                continue;
            }
            entities.add(living);
        }
        return entities;
    }

    private List<Block> blocksInRadius(Location center, int radius) {
        List<Block> blocks = new ArrayList<>();
        int baseX = center.getBlockX();
        int baseY = center.getBlockY();
        int baseZ = center.getBlockZ();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if ((x * x) + (z * z) > (radius * radius)) {
                        continue;
                    }
                    blocks.add(center.getWorld().getBlockAt(baseX + x, baseY + y, baseZ + z));
                }
            }
        }
        return blocks;
    }
}
