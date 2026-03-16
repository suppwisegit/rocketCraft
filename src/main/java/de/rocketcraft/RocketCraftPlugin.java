package de.rocketcraft;

import de.rocketcraft.command.RocketCommand;
import de.rocketcraft.rocket.service.RocketRegistry;
import org.bukkit.plugin.java.JavaPlugin;

public final class RocketCraftPlugin extends JavaPlugin {

    private RocketRegistry rocketRegistry;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        rocketRegistry = new RocketRegistry();
        rocketRegistry.registerDefaults();

        if (getCommand("rocket") != null) {
            getCommand("rocket").setExecutor(new RocketCommand(rocketRegistry));
        }

        getLogger().info("RocketCraft gestartet. Bereit fuer Launches und Duelle!");
    }

    @Override
    public void onDisable() {
        getLogger().info("RocketCraft wird heruntergefahren.");
    }
}
