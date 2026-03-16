package de.rocketcraft.tnt.model;

import de.rocketcraft.tnt.type.CustomTntType;

public record CustomTntSpec(
    CustomTntType type,
    String displayName,
    int fuseTicks,
    float blastRadius,
    String description
) {
}
