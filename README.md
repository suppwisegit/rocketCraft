# 🚀 RocketCraft

RocketCraft ist ein **Paper-Plugin fuer Minecraft Java Edition**, das neue Raketen im **Vanilla-Stil** einfuehrt:
von verrueckten Fun-Launches bis zu taktischen/militaerischen Duel-Werkzeugen.

## Projektstatus
> Aktuell: **Core Gameplay Loop implementiert**

Enthalten sind jetzt:
- `/rocket list` und `/rocket give <typ> [spieler] [anzahl]`
- spielbare Raketen-Effekte fuer `SPARK`, `BOOST`, `THUNDERSTRIKE`
- serverseitige Cooldowns mit Actionbar-Feedback
- Crafting-Rezepte fuer die drei Kernraketen
- mobbasierte Drop-Chancen (Creeper/Phantom/Witch)

## Ziele
- Vanilla-nahe Experience ohne Texturepack-Zwang.
- Raketen mit klarer Identitaet (Fun, Tactical, Military).
- Duell-fokussiertes Gameplay fuer Freundesgruppen.
- Modulare Architektur fuer spaetere Erweiterungen.

## Verfuegbare Raketen-Typen (erste Iteration)
- `SPARK` – schneller Mini-Schub mit Funken.
- `BOOST` – starker Luft-Boost fuer Mobility.
- `CHAFF` – Sichtstoerung/Counter-Tool.
- `THUNDERSTRIKE` – praeziser Damage-Hit.
- `SIEGEBREAKER` – schwerer, langsamer Impact.
- `PARTY_COMET` – Event/Fun-Rakete mit Showeffekt.

## Technischer Stack
- Java 21
- Paper API 1.20.6
- Gradle (Groovy DSL)

## Installation Guide (Minecraft / Paper)

### 1) Voraussetzungen
- **Minecraft Java Edition Server**
- **Paper 1.20.6** (oder kompatibel)
- **Java 21** auf dem Server

### 2) Plugin bauen
Im Projektordner ausfuehren:
```bash
./gradlew build
```

Danach liegt die Plugin-Datei hier:
```text
build/libs/rocketCraft-0.1.0-SNAPSHOT.jar
```

### 3) Auf den Server installieren
1. Paper-Server einmal starten und wieder stoppen (damit `plugins/` vorhanden ist).
2. `rocketCraft-0.1.0-SNAPSHOT.jar` nach `<dein-server>/plugins/` kopieren.
3. Server neu starten.

### 4) Funktionstest im Spiel
Als OP/Admin im Chat testen:
```text
/rocket list
/rocket give spark
/rocket give boost <Spielername> 3
/rocket give thunderstrike <Spielername> 1
```

### 5) Konfiguration
Nach dem ersten Start wird `plugins/RocketCraft/config.yml` angelegt.
Dort kannst du u. a. anpassen:
- `cooldown-scale`
- `drops.creeper-spark`
- `drops.phantom-boost`
- `drops.witch-thunderstrike`

Nach Aenderungen: Server neu starten oder Plugin neu laden.

## Projektstruktur
```text
rocketCraft/
├─ build.gradle
├─ settings.gradle
├─ src/main/java/de/rocketcraft/
│  ├─ RocketCraftPlugin.java
│  ├─ command/RocketCommand.java
│  └─ rocket/
│     ├─ listener/
│     ├─ model/
│     ├─ service/
│     └─ type/
├─ src/main/resources/
│  ├─ config.yml
│  └─ plugin.yml
└─ docs/
   ├─ PROJECT_PLAN.md
   └─ ROCKET_CONCEPTS.md
```

## Planung & Konzepte
- Gesamtplan: [`docs/PROJECT_PLAN.md`](docs/PROJECT_PLAN.md)
- Kreative Raketenideen: [`docs/ROCKET_CONCEPTS.md`](docs/ROCKET_CONCEPTS.md)
