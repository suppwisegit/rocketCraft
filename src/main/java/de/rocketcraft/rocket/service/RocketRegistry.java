package de.rocketcraft.rocket.service;

import de.rocketcraft.rocket.model.RocketSpec;
import de.rocketcraft.rocket.type.RocketType;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class RocketRegistry {

    private final Map<RocketType, RocketSpec> rockets = new EnumMap<>(RocketType.class);

    public void registerDefaults() {
        register(new RocketSpec(RocketType.SPARK, "Funkenfeger", 2, 1,
            "Kurzer Schub mit Funken-Spur fuer schnelle Mobility."));
        register(new RocketSpec(RocketType.BOOST, "Sky-Booster", 4, 5,
            "Starker Vorwaertsschub fuer riskante Luftmanoever."));
        register(new RocketSpec(RocketType.CHAFF, "Ablenkwolke", 0, 12,
            "Dichter Partikel-Nebel, der Sichtlinien kurz unterbricht."));
        register(new RocketSpec(RocketType.THUNDERSTRIKE, "Donnerkeil", 7, 18,
            "Praezisionsschlag mit kleinem Splash-Schaden."));
        register(new RocketSpec(RocketType.SIEGEBREAKER, "Siegebrecher", 10, 30,
            "Langsame High-Impact-Rakete fuer Teamfights."));
        register(new RocketSpec(RocketType.PARTY_COMET, "Party-Komet", 1, 6,
            "Farbenfrohe Rakete mit Konfetti-Explosion statt harter Gewalt."));
    }

    public void register(RocketSpec spec) {
        rockets.put(spec.type(), spec);
    }

    public Collection<RocketSpec> getAll() {
        return Collections.unmodifiableCollection(rockets.values());
    }
}
