package de.rocketcraft.rocket.service;

import de.rocketcraft.rocket.model.RocketSpec;
import de.rocketcraft.rocket.type.RocketType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class RocketRecipeService {

    private final JavaPlugin plugin;
    private final RocketRegistry rocketRegistry;
    private final RocketItemService rocketItemService;

    public RocketRecipeService(JavaPlugin plugin, RocketRegistry rocketRegistry, RocketItemService rocketItemService) {
        this.plugin = plugin;
        this.rocketRegistry = rocketRegistry;
        this.rocketItemService = rocketItemService;
    }

    public void registerDefaultRecipes() {
        RocketSpec spark = rocketRegistry.getByType(RocketType.SPARK).orElse(null);
        if (spark != null) {
            ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "spark_rocket"), rocketItemService.createRocketItem(spark, 1));
            recipe.shape("PGP", "GRG", "PGP");
            recipe.setIngredient('P', Material.PAPER);
            recipe.setIngredient('G', Material.GLOWSTONE_DUST);
            recipe.setIngredient('R', Material.REDSTONE);
            Bukkit.addRecipe(recipe);
        }

        RocketSpec boost = rocketRegistry.getByType(RocketType.BOOST).orElse(null);
        if (boost != null) {
            ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "boost_rocket"), rocketItemService.createRocketItem(boost, 1));
            recipe.shape("PFP", "FEF", "PFP");
            recipe.setIngredient('P', Material.PAPER);
            recipe.setIngredient('F', Material.FEATHER);
            recipe.setIngredient('E', Material.ENDER_PEARL);
            Bukkit.addRecipe(recipe);
        }

        RocketSpec thunder = rocketRegistry.getByType(RocketType.THUNDERSTRIKE).orElse(null);
        if (thunder != null) {
            ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "thunderstrike_rocket"), rocketItemService.createRocketItem(thunder, 1));
            recipe.shape("PCP", "CRC", "PWP");
            recipe.setIngredient('P', Material.PAPER);
            recipe.setIngredient('C', Material.COPPER_INGOT);
            recipe.setIngredient('R', Material.REDSTONE_BLOCK);
            recipe.setIngredient('W', Material.LIGHTNING_ROD);
            Bukkit.addRecipe(recipe);
        }
    }
}
