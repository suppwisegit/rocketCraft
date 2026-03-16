package de.rocketcraft;

import de.rocketcraft.command.RocketCommand;
import de.rocketcraft.rocket.listener.RocketDropListener;
import de.rocketcraft.rocket.listener.RocketUseListener;
import de.rocketcraft.rocket.service.CooldownService;
import de.rocketcraft.rocket.service.RocketItemService;
import de.rocketcraft.rocket.service.RocketRecipeService;
import de.rocketcraft.rocket.service.RocketRegistry;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class RocketCraftPlugin extends JavaPlugin {

    private RocketRegistry rocketRegistry;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        rocketRegistry = new RocketRegistry();
        rocketRegistry.registerDefaults();

        RocketItemService rocketItemService = new RocketItemService(this);
        CooldownService cooldownService = new CooldownService();
        RocketRecipeService recipeService = new RocketRecipeService(this, rocketRegistry, rocketItemService);

        recipeService.registerDefaultRecipes();

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new RocketUseListener(
            this,
            rocketRegistry,
            rocketItemService,
            cooldownService,
            getConfig().getDouble("cooldown-scale", 1.0D)
        ), this);
        pluginManager.registerEvents(new RocketDropListener(rocketRegistry, rocketItemService, getConfig()), this);

        PluginCommand rocket = getCommand("rocket");
        if (rocket != null) {
            RocketCommand rocketCommand = new RocketCommand(rocketRegistry, rocketItemService);
            rocket.setExecutor(rocketCommand);
            rocket.setTabCompleter(rocketCommand);
        }

        getLogger().info("RocketCraft gestartet. Core Gameplay Loop aktiv!");
    }

    @Override
    public void onDisable() {
        getLogger().info("RocketCraft wird heruntergefahren.");
    }
}
