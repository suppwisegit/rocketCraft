package de.rocketcraft.rocket.listener;

import de.rocketcraft.rocket.model.RocketSpec;
import de.rocketcraft.rocket.service.CooldownService;
import de.rocketcraft.rocket.service.RocketItemService;
import de.rocketcraft.rocket.service.RocketRegistry;
import de.rocketcraft.rocket.type.RocketType;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class RocketUseListener implements Listener {

    private static final String PROJECTILE_TAG = "rocket_projectile_type";

    private final RocketRegistry rocketRegistry;
    private final RocketItemService rocketItemService;
    private final CooldownService cooldownService;
    private final org.bukkit.NamespacedKey projectileTypeKey;
    private final double cooldownScale;

    public RocketUseListener(JavaPlugin plugin, RocketRegistry rocketRegistry, RocketItemService rocketItemService,
                             CooldownService cooldownService, double cooldownScale) {
        this.rocketRegistry = rocketRegistry;
        this.rocketItemService = rocketItemService;
        this.cooldownService = cooldownService;
        this.projectileTypeKey = new org.bukkit.NamespacedKey(plugin, PROJECTILE_TAG);
        this.cooldownScale = Math.max(0.1D, cooldownScale);
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
        Optional<RocketType> rocketType = rocketItemService.getRocketType(item);
        if (rocketType.isEmpty()) {
            return;
        }

        Player player = event.getPlayer();
        RocketType type = rocketType.get();
        RocketSpec spec = rocketRegistry.getByType(type).orElse(null);
        if (spec == null) {
            player.sendMessage("§cDiese Rakete ist nicht registriert.");
            return;
        }

        long now = System.currentTimeMillis();
        long remaining = cooldownService.getRemainingSeconds(player.getUniqueId(), type, now);
        if (remaining > 0) {
            player.sendActionBar(Component.text("Cooldown: " + type.name() + " noch " + remaining + "s", NamedTextColor.RED));
            event.setCancelled(true);
            return;
        }

        launchRocket(player, type);
        int scaledCooldown = (int) Math.ceil(spec.cooldownSeconds() * cooldownScale);
        cooldownService.startCooldown(player.getUniqueId(), type, scaledCooldown, now);
        player.sendActionBar(Component.text(type.name() + " gestartet", NamedTextColor.GREEN));

        item.setAmount(item.getAmount() - 1);
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow)) {
            return;
        }

        String typeName = arrow.getPersistentDataContainer().get(projectileTypeKey, PersistentDataType.STRING);
        if (typeName == null) {
            return;
        }

        RocketType type;
        try {
            type = RocketType.valueOf(typeName);
        } catch (IllegalArgumentException ex) {
            return;
        }

        applyImpact(type, arrow.getLocation(), arrow.getShooter() instanceof Player shooter ? shooter : null);
        arrow.remove();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFriendlyProjectileDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Arrow arrow)) {
            return;
        }
        String typeName = arrow.getPersistentDataContainer().get(projectileTypeKey, PersistentDataType.STRING);
        if (typeName != null) {
            event.setCancelled(true);
        }
    }

    private void launchRocket(Player player, RocketType type) {
        Arrow projectile = player.launchProjectile(Arrow.class);
        projectile.setVelocity(player.getLocation().getDirection().multiply(2.0));
        projectile.setGravity(false);
        projectile.setCritical(true);
        projectile.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
        projectile.getPersistentDataContainer().set(projectileTypeKey, PersistentDataType.STRING, type.name());

        player.getWorld().spawnParticle(Particle.FIREWORK, player.getEyeLocation(), 16, 0.2, 0.2, 0.2, 0.04);
        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0F, 1.1F);
    }

    private void applyImpact(RocketType type, Location location, Player shooter) {
        switch (type) {
            case SPARK -> {
                location.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, location, 40, 0.6, 0.6, 0.6, 0.2);
                location.getWorld().playSound(location, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1.0F, 1.4F);
                if (shooter != null) {
                    Vector boost = shooter.getLocation().getDirection().normalize().multiply(0.55).setY(0.35);
                    shooter.setVelocity(shooter.getVelocity().add(boost));
                }
                damageNearby(location, shooter, 1.5, 2.0, 0.35);
            }
            case BOOST -> {
                location.getWorld().spawnParticle(Particle.CLOUD, location, 60, 1.0, 0.4, 1.0, 0.03);
                location.getWorld().playSound(location, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.0F, 0.9F);
                if (shooter != null) {
                    Vector jump = shooter.getLocation().getDirection().normalize().multiply(0.8).setY(1.0);
                    shooter.setVelocity(shooter.getVelocity().add(jump));
                }
                damageNearby(location, shooter, 2.0, 3.0, 0.8);
            }
            case THUNDERSTRIKE -> {
                location.getWorld().strikeLightningEffect(location);
                location.getWorld().spawnParticle(Particle.END_ROD, location, 20, 0.35, 0.35, 0.35, 0.05);
                location.getWorld().playSound(location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0F, 1.0F);
                damageNearby(location, shooter, 3.2, 7.0, 1.1);
            }
            default -> {
                location.getWorld().spawnParticle(Particle.SMOKE, location, 25, 0.6, 0.6, 0.6, 0.01);
                damageNearby(location, shooter, 1.6, 1.0, 0.2);
            }
        }
    }

    private void damageNearby(Location center, Player shooter, double radius, double damage, double knockbackStrength) {
        for (Entity entity : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (!(entity instanceof LivingEntity living)) {
                continue;
            }
            if (shooter != null && shooter.getUniqueId().equals(living.getUniqueId())) {
                continue;
            }

            living.damage(damage, shooter);
            Vector knockback = living.getLocation().toVector().subtract(center.toVector()).normalize().multiply(knockbackStrength).setY(0.25);
            living.setVelocity(living.getVelocity().add(knockback));
        }
    }
}
