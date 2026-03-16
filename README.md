# 🚀 RocketCraft

RocketCraft ist ein **Paper-Plugin fuer Minecraft Java Edition**, das neue Raketen im **Vanilla-Stil** einfuehrt:
von verrueckten Fun-Launches bis zu taktischen/militaerischen Duel-Werkzeugen.

## Projektstatus
> Aktuell: **Foundation-Setup** (Grundstruktur + Konzept + Roadmap)

## Ziele
- Vanilla-nahe Experience ohne Texturepack-Zwang.
- Raketen mit klarer Identitaet (Fun, Tactical, Military).
- Duell-fokussiertes Gameplay fuer Freundesgruppen.
- Modulare Architektur fuer spaetere Erweiterungen.

## Geplante Raketen-Typen (erste Iteration)
- `SPARK` – schneller Mini-Schub mit Funken.
- `BOOST` – starker Luft-Boost fuer Mobility.
- `CHAFF` – Sichtstoerung/Counter-Tool.
- `THUNDERSTRIKE` – praeziser Damage-Hit.
- `SIEGEBREAKER` – schwerer, langsamer Impact.
- `PARTY_COMET` – Event/Fun-Rakete mit Showeffekt.

## Technischer Stack
- Java 21
- Paper API 1.20.6
- Gradle (Kotlin DSL)

## Projektstruktur
```text
rocketCraft/
├─ build.gradle
├─ settings.gradle
├─ src/main/java/de/rocketcraft/
│  ├─ RocketCraftPlugin.java
│  ├─ command/RocketCommand.java
│  └─ rocket/
│     ├─ model/RocketSpec.java
│     ├─ service/RocketRegistry.java
│     └─ type/RocketType.java
├─ src/main/resources/plugin.yml
└─ docs/
   ├─ PROJECT_PLAN.md
   └─ ROCKET_CONCEPTS.md
```

## Quickstart (lokal)
1. Java 21 installieren.
2. Projekt klonen.
3. Build ausfuehren:
   ```bash
   gradle build
   ```
4. Entstehende JAR in den `plugins/`-Ordner eines Paper-Servers legen.

## Nächste sinnvolle Implementierungen
1. `/rocket give <typ>` inkl. Permission-Checks.
2. ItemBuilder fuer Raketenitems (Lore, Rarity, Cooldown-Infos).
3. Impact-System (Projectile + Hit-Resolution).
4. Config-basierte Balancing-Werte in `config.yml`.
5. Duel-Mode mit Matchflow.

## Planung & Konzepte
- Gesamtplan: [`docs/PROJECT_PLAN.md`](docs/PROJECT_PLAN.md)
- Kreative Raketenideen: [`docs/ROCKET_CONCEPTS.md`](docs/ROCKET_CONCEPTS.md)

---
Wenn du willst, kann ich im naechsten Schritt direkt den ersten spielbaren Loop bauen:
**Rakete bekommen -> abschiessen -> Effekt + Cooldown + Trefferlogik**.
