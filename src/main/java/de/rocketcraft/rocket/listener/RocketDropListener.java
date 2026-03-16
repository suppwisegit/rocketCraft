package de.rocketcraft.rocket.listener;

import de.rocketcraft.rocket.model.RocketSpec;
import de.rocketcraft.rocket.service.RocketItemService;
import de.rocketcraft.rocket.service.RocketRegistry;
import de.rocketcraft.rocket.type.RocketType;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class RocketDropListener implements Listener {

    private final RocketRegistry rocketRegistry;
    private final RocketItemService rocketItemService;
    private final FileConfiguration config;

    public RocketDropListener(RocketRegistry rocketRegistry, RocketItemService rocketItemService,
                              FileConfiguration config) {
        this.rocketRegistry = rocketRegistry;
        this.rocketItemService = rocketItemService;
        this.config = config;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) {
            return;
        }

        EntityType type = event.getEntityType();
        double chance;
        RocketType dropType;

        if (type == EntityType.CREEPER) {
            chance = config.getDouble("drops.creeper-spark", 0.20D);
            dropType = RocketType.SPARK;
        } else if (type == EntityType.PHANTOM) {
            chance = config.getDouble("drops.phantom-boost", 0.18D);
            dropType = RocketType.BOOST;
        } else if (type == EntityType.WITCH) {
            chance = config.getDouble("drops.witch-thunderstrike", 0.15D);
            dropType = RocketType.THUNDERSTRIKE;
        } else {
            return;
        }

        if (ThreadLocalRandom.current().nextDouble() > chance) {
            return;
        }

        RocketSpec spec = rocketRegistry.getByType(dropType).orElse(null);
        if (spec == null) {
            return;
        }

        event.getDrops().add(rocketItemService.createRocketItem(spec, 1));
    }
}
