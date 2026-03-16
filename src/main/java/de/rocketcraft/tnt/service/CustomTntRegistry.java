package de.rocketcraft.tnt.service;

import de.rocketcraft.tnt.model.CustomTntSpec;
import de.rocketcraft.tnt.type.CustomTntType;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public class CustomTntRegistry {

    private final Map<CustomTntType, CustomTntSpec> tnts = new EnumMap<>(CustomTntType.class);

    public void registerDefaults() {
        register(new CustomTntSpec(CustomTntType.METEOR, "Meteorbrecher", 55, 5.5F,
            "Ruft einen Feuersturm herbei und entzuendet die Einschlagszone."));
        register(new CustomTntSpec(CustomTntType.CRYO, "Frostkern", 45, 3.5F,
            "Friert Gegner ein, legt Eis auf dem Boden und stoppt Pushes."));
        register(new CustomTntSpec(CustomTntType.VORTEX, "Vortex-Singularity", 50, 3.0F,
            "Zieht Gegner in die Mitte und zerstoert Formationen."));
        register(new CustomTntSpec(CustomTntType.PHOTON, "Photonenblitz", 35, 2.4F,
            "Blendung + Leuchteffekt fuer aggressive Push-Calls."));
        register(new CustomTntSpec(CustomTntType.QUAKE, "Bebenanker", 60, 4.0F,
            "Laesst die Erde beben, schleudert Gegner hoch und verlangsamt sie."));
        register(new CustomTntSpec(CustomTntType.TOXIC, "Toxinnebel", 50, 3.0F,
            "Vergiftet + schwaecht Gegner in einer verseuchten Zone."));
        register(new CustomTntSpec(CustomTntType.GUARDIAN, "Waechterkern", 40, 2.5F,
            "Support-TNT: heilt Verbuendete und entfernt Debuffs im Einschlag."));
        register(new CustomTntSpec(CustomTntType.ANTI_GRAV, "Null-G-Anker", 45, 3.0F,
            "Katapultiert Gegner nach oben und laesst sie hart abstuerzen."));
        register(new CustomTntSpec(CustomTntType.CHAIN_REACTION, "Kettenreaktor", 30, 2.0F,
            "Mehrstufige Explosionen fuer Area-Denial und Nachdruck."));
        register(new CustomTntSpec(CustomTntType.STASIS, "Stasisfeld", 45, 2.8F,
            "Haelt Gegner in der Luft fest und macht sie berechenbar."));
        register(new CustomTntSpec(CustomTntType.SHOCKWAVE, "Impulsramme", 25, 2.5F,
            "Massiver Knockback-Ring, ideal an Kanten und Bruecken."));
        register(new CustomTntSpec(CustomTntType.WEBTRAP, "Netzwerfer", 40, 1.8F,
            "Errichtet kurzlebige Spinnenweben und rooted Gegner."));
        register(new CustomTntSpec(CustomTntType.LOOTBURST, "Lootknall", 35, 2.0F,
            "Droppt Utility-Loot fuer schnelle Re-Engages."));
        register(new CustomTntSpec(CustomTntType.WARP, "Warpbruch", 35, 2.2F,
            "Teleportiert Gegner zufaellig in einen unguenstigen Radius."));
        register(new CustomTntSpec(CustomTntType.DOOM_CLOCK, "Doom Clock", 70, 6.0F,
            "Langzuender mit brachialem Finale und Finisher-Druck."));
    }

    public void register(CustomTntSpec spec) {
        tnts.put(spec.type(), spec);
    }

    public Collection<CustomTntSpec> getAll() {
        return Collections.unmodifiableCollection(tnts.values());
    }

    public Optional<CustomTntSpec> getByType(CustomTntType type) {
        return Optional.ofNullable(tnts.get(type));
    }
}
