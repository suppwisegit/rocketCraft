package de.rocketcraft.command;

import de.rocketcraft.rocket.model.RocketSpec;
import de.rocketcraft.rocket.service.RocketItemService;
import de.rocketcraft.rocket.service.RocketRegistry;
import de.rocketcraft.rocket.type.RocketType;
import de.rocketcraft.tnt.model.CustomTntSpec;
import de.rocketcraft.tnt.service.CustomTntItemService;
import de.rocketcraft.tnt.service.CustomTntRegistry;
import de.rocketcraft.tnt.type.CustomTntType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class RocketCommand implements CommandExecutor, TabCompleter {

    private final RocketRegistry rocketRegistry;
    private final RocketItemService rocketItemService;
    private final CustomTntRegistry customTntRegistry;
    private final CustomTntItemService customTntItemService;

    public RocketCommand(RocketRegistry rocketRegistry, RocketItemService rocketItemService,
                         CustomTntRegistry customTntRegistry, CustomTntItemService customTntItemService) {
        this.rocketRegistry = rocketRegistry;
        this.rocketItemService = rocketItemService;
        this.customTntRegistry = customTntRegistry;
        this.customTntItemService = customTntItemService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || "list".equalsIgnoreCase(args[0])) {
            sender.sendMessage("§6[RocketCraft] §fVerfuegbare Raketen: §e" +
                rocketRegistry.getAll().stream()
                    .map(spec -> spec.type().name().toLowerCase(Locale.ROOT))
                    .collect(Collectors.joining(", ")));
            sender.sendMessage("§6[RocketCraft] §fVerfuegbare CustomTNTs: §e" +
                customTntRegistry.getAll().stream()
                    .map(spec -> spec.type().name().toLowerCase(Locale.ROOT))
                    .collect(Collectors.joining(", ")));
            sender.sendMessage("§7Nutze /rocket give <typ> [spieler] [anzahl].");
            return true;
        }

        if (!"give".equalsIgnoreCase(args[0])) {
            sender.sendMessage("§cUnbekanntes Subkommando. Nutze /rocket list oder /rocket give.");
            return true;
        }

        if (!sender.hasPermission("rocketcraft.command.rocket.give")) {
            sender.sendMessage("§cDu hast keine Berechtigung fuer /rocket give.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§eUsage: /rocket give <typ> [spieler] [anzahl]");
            return true;
        }

        String requestedType = args[1].toUpperCase(Locale.ROOT);

        Player target;
        if (args.length >= 3) {
            target = Bukkit.getPlayerExact(args[2]);
            if (target == null) {
                sender.sendMessage("§cSpieler nicht gefunden: " + args[2]);
                return true;
            }
        } else if (sender instanceof Player playerSender) {
            target = playerSender;
        } else {
            sender.sendMessage("§cBitte gib als Konsole einen Spieler an.");
            return true;
        }

        int amount = 1;
        if (args.length >= 4) {
            try {
                amount = Integer.parseInt(args[3]);
            } catch (NumberFormatException ex) {
                sender.sendMessage("§cAnzahl muss eine ganze Zahl sein.");
                return true;
            }
        }

        if (amount < 1 || amount > 64) {
            sender.sendMessage("§cAnzahl muss zwischen 1 und 64 liegen.");
            return true;
        }

        RocketType rocketType = parseRocketType(requestedType);
        if (rocketType != null) {
            RocketSpec spec = rocketRegistry.getByType(rocketType).orElse(null);
            if (spec == null) {
                sender.sendMessage("§cDieser Raketentyp ist aktuell nicht registriert.");
                return true;
            }
            target.getInventory().addItem(rocketItemService.createRocketItem(spec, amount));
            sender.sendMessage("§a" + amount + "x " + rocketType.name() + " an " + target.getName() + " gegeben.");
            if (!target.equals(sender)) {
                target.sendMessage("§6[RocketCraft] §fDu hast §e" + amount + "x " + rocketType.name() + " §ferhalten.");
            }
            return true;
        }

        CustomTntType customTntType = parseCustomTntType(requestedType);
        if (customTntType != null) {
            CustomTntSpec spec = customTntRegistry.getByType(customTntType).orElse(null);
            if (spec == null) {
                sender.sendMessage("§cDieser CustomTNT-Typ ist aktuell nicht registriert.");
                return true;
            }
            target.getInventory().addItem(customTntItemService.createCustomTntItem(spec, amount));
            sender.sendMessage("§a" + amount + "x " + customTntType.name() + " an " + target.getName() + " gegeben.");
            if (!target.equals(sender)) {
                target.sendMessage("§6[RocketCraft] §fDu hast §e" + amount + "x " + customTntType.name() + " §ferhalten.");
            }
            return true;
        }

        sender.sendMessage("§cUnbekannter Typ. Nutze /rocket list.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return filterByPrefix(Arrays.asList("list", "give"), args[0]);
        }
        if (args.length == 2 && "give".equalsIgnoreCase(args[0])) {
            return filterByPrefix(
                allTypeSuggestions(),
                args[1]
            );
        }
        if (args.length == 3 && "give".equalsIgnoreCase(args[0])) {
            return filterByPrefix(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList(), args[2]);
        }
        return List.of();
    }

    private List<String> allTypeSuggestions() {
        List<String> suggestions = new ArrayList<>();
        suggestions.addAll(rocketRegistry.getAll().stream()
            .map(spec -> spec.type().name().toLowerCase(Locale.ROOT))
            .toList());
        suggestions.addAll(customTntRegistry.getAll().stream()
            .map(spec -> spec.type().name().toLowerCase(Locale.ROOT))
            .toList());
        return suggestions;
    }

    private RocketType parseRocketType(String requestedType) {
        try {
            return RocketType.valueOf(requestedType);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private CustomTntType parseCustomTntType(String requestedType) {
        try {
            return CustomTntType.valueOf(requestedType);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private List<String> filterByPrefix(List<String> options, String prefix) {
        String loweredPrefix = prefix.toLowerCase(Locale.ROOT);
        List<String> filtered = new ArrayList<>();
        for (String option : options) {
            if (option.toLowerCase(Locale.ROOT).startsWith(loweredPrefix)) {
                filtered.add(option);
            }
        }
        return filtered;
    }
}
