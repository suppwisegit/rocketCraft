package de.rocketcraft.rocket.service;

import de.rocketcraft.rocket.model.RocketSpec;
import de.rocketcraft.rocket.type.RocketType;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class RocketItemService {

    private static final String ROCKET_TAG = "rocket_type";

    private final NamespacedKey rocketTypeKey;

    public RocketItemService(JavaPlugin plugin) {
        this.rocketTypeKey = new NamespacedKey(plugin, ROCKET_TAG);
    }

    public ItemStack createRocketItem(RocketSpec spec, int amount) {
        ItemStack item = new ItemStack(org.bukkit.Material.FIREWORK_ROCKET, Math.max(1, amount));
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("🚀 " + spec.displayName(), NamedTextColor.GOLD));

        List<Component> lore = Arrays.asList(
            Component.text(spec.description(), NamedTextColor.GRAY),
            Component.text("Typ: " + spec.type().name(), NamedTextColor.YELLOW),
            Component.text("Power: " + spec.power(), NamedTextColor.RED),
            Component.text("Cooldown: " + spec.cooldownSeconds() + "s", NamedTextColor.AQUA)
        );
        meta.lore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        meta.getPersistentDataContainer().set(rocketTypeKey, PersistentDataType.STRING, spec.type().name());

        item.setItemMeta(meta);
        return item;
    }

    public Optional<RocketType> getRocketType(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return Optional.empty();
        }

        PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
        String typeName = data.get(rocketTypeKey, PersistentDataType.STRING);
        if (typeName == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(RocketType.valueOf(typeName));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}
