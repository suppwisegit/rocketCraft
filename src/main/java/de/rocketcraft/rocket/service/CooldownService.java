package de.rocketcraft.rocket.service;

import de.rocketcraft.rocket.type.RocketType;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownService {

    private final Map<UUID, Map<RocketType, Long>> cooldowns = new ConcurrentHashMap<>();

    public long getRemainingSeconds(UUID playerId, RocketType type, long nowMillis) {
        Map<RocketType, Long> perType = cooldowns.get(playerId);
        if (perType == null) {
            return 0L;
        }
        Long expiry = perType.get(type);
        if (expiry == null || expiry <= nowMillis) {
            return 0L;
        }
        return (long) Math.ceil((expiry - nowMillis) / 1000.0D);
    }

    public void startCooldown(UUID playerId, RocketType type, int cooldownSeconds, long nowMillis) {
        cooldowns.computeIfAbsent(playerId, ignored -> new EnumMap<>(RocketType.class))
            .put(type, nowMillis + cooldownSeconds * 1000L);
    }
}
