package de.rocketcraft.tnt.service;

import de.rocketcraft.tnt.model.CustomTntSpec;
import de.rocketcraft.tnt.type.CustomTntType;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomTntItemService {

    private static final String CUSTOM_TNT_TAG = "custom_tnt_type";

    private final NamespacedKey customTntTypeKey;

    public CustomTntItemService(JavaPlugin plugin) {
        this.customTntTypeKey = new NamespacedKey(plugin, CUSTOM_TNT_TAG);
    }

    public ItemStack createCustomTntItem(CustomTntSpec spec, int amount) {
        ItemStack item = new ItemStack(Material.TNT, Math.max(1, amount));
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("🧨 " + spec.displayName(), NamedTextColor.RED));

        List<Component> lore = Arrays.asList(
            Component.text(spec.description(), NamedTextColor.GRAY),
            Component.text("Typ: " + spec.type().name(), NamedTextColor.YELLOW),
            Component.text("Zuender: " + (spec.fuseTicks() / 20.0) + "s", NamedTextColor.AQUA),
            Component.text("Blast-Radius: " + spec.blastRadius(), NamedTextColor.GOLD)
        );
        meta.lore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        meta.getPersistentDataContainer().set(customTntTypeKey, PersistentDataType.STRING, spec.type().name());

        item.setItemMeta(meta);
        return item;
    }

    public Optional<CustomTntType> getCustomTntType(ItemStack item) {
        if (item == null || item.getType() != Material.TNT || !item.hasItemMeta()) {
            return Optional.empty();
        }

        PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
        String typeName = data.get(customTntTypeKey, PersistentDataType.STRING);
        if (typeName == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(CustomTntType.valueOf(typeName));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    public NamespacedKey getCustomTntTypeKey() {
        return customTntTypeKey;
    }
}
