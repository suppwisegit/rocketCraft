package de.rocketcraft.command;

import de.rocketcraft.rocket.service.RocketRegistry;
import java.util.stream.Collectors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RocketCommand implements CommandExecutor {

    private final RocketRegistry rocketRegistry;

    public RocketCommand(RocketRegistry rocketRegistry) {
        this.rocketRegistry = rocketRegistry;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§6[RocketCraft] §fVerfuegbare Raketen: §e" +
                rocketRegistry.getAll().stream()
                    .map(spec -> spec.type().name().toLowerCase())
                    .collect(Collectors.joining(", ")));
            sender.sendMessage("§7Nutze spaeter /rocket give <typ> fuer Spawns.");
            return true;
        }

        sender.sendMessage("§6[RocketCraft] §fWIP: Der Kommando-Handler wird im naechsten Schritt erweitert.");
        return true;
    }
}
