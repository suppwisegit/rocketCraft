package de.rocketcraft.rocket.model;

import de.rocketcraft.rocket.type.RocketType;

public record RocketSpec(
    RocketType type,
    String displayName,
    int power,
    int cooldownSeconds,
    String description
) {
}
