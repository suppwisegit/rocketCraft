# RocketCraft – Techstack & erstes Implementierungskonzept

> Ziel: Ein Minecraft-Java-Plugin im **Vanilla-Stil** (keine Resourcepacks), das verschiedene Raketen-Typen von „Fun“ bis „militärisch“ bereitstellt.

## 1) Empfohlener Techstack

## Laufzeit & API
- **Server-Plattform:** [Paper](https://papermc.io/software/paper) (Bukkit/Spigot-kompatibel, performant, sehr gut dokumentiert).
- **Minecraft-Version (Start):** Fokus auf **eine** Zielversion (**1.20.6**), um NMS-/Kompatibilitätsaufwand gering zu halten.
- **Plugin-API:** Paper API (Events, Entities, Scheduler, Commands, Permissions).

## Sprache & Build
- **Java:** **Java 21** (aktueller Standard für moderne Paper-Versionen).
- **Build-Tool:** **Gradle (Groovy DSL)**.
- **Dependencies:**
  - `io.papermc.paper:paper-api` als `compileOnly`.
  - Optional später: Config-Library (falls notwendig), sonst zunächst Bukkit/Paper-Config.

## Persistenz & Konfiguration
- **Konfiguration:** YAML (`config.yml`) für globales Balancing.
- **Raketen-Definitionen:** `rockets.yml` (Schaden, Radius, Cooldown, Effekte, Kosten).
- **Spielerzustand (optional):** `playerdata.yml` oder später SQLite, falls Progression/Währungen ergänzt werden.

## Test/Qualität
- **Code-Style:** Spotless/Checkstyle (optional in Phase 1).
- **CI:** GitHub Actions für Build + statische Checks.
- **Manuelle Tests:** Lokaler Paper-Testserver + zwei Testaccounts für Duell-Szenarien.

---

## 2) Architekturvorschlag (MVP-fähig)

## Paketstruktur
- `de.rocketcraft` (Root)
  - `RocketCraftPlugin` – `JavaPlugin`-Startklasse, Dependency-Wiring
  - `command` – Commands (`/rocket`)
  - `rocket.model` – Raketenmodell (`RocketSpec`)
  - `rocket.service` – Registry (`RocketRegistry`)
  - `rocket.type` – Typisierung (`RocketType`)

> Geplante Erweiterungen (`combat`, `effect`, `config`, `cooldown`, `arena`) folgen in spaeteren Phasen der Roadmap.

## Domänenmodell (Kern)
- `RocketType`
  - `id`, `displayName`, `category` (FUN/MILITARY/UTILITY)
  - `fuseTicks`, `speed`, `maxLifetimeTicks`
  - `explosionPower`, `damage`, `damageRadius`
  - `knockback`, `fireTicks`, `effects[]`
  - `cooldownSeconds`, `permission`
- `RocketInstance`
  - Runtime-Objekt mit Shooter, Spawnzeit, Entity-Referenz
- `RocketRegistry`
  - lädt Raketen aus YAML und validiert Wertebereiche

## Auslösung & Steuerung
- Spieler nutzt definierte Item-Form (z. B. modifizierte Feuerwerksrakete) oder Command.
- `PlayerInteractEvent`: prüft Permission, Cooldown, Modus (Duell/World-Regeln).
- Spawn z. B. über Projectile/Firework-Mechanik; Tracking via Listener + Scheduler.
- Beim Einschlag/Timeout: Effektpipeline ausführen (Schaden, Knockback, Feuer, Partikel, Sound).

---

## 3) Gameplay-Konzept für erste Raketen (MVP)

## Fun-Raketen
1. **Confetti Rocket**
   - kaum Schaden, viele Partikel/Farben, leichter Knockback.
2. **Slap Rocket**
   - mittlerer Knockback, minimaler Schaden.

## Militärische Raketen
1. **HE Rocket**
   - solider AoE-Schaden, moderater Radius.
2. **Incendiary Rocket**
   - weniger Initialschaden, dafür Feuer-/DoT-Charakter.

## Utility-Raketen
1. **Smoke Rocket**
   - Sichtbehinderung via Partikelwolke (Vanilla-Partikel), kein direkter Schaden.
2. **Jump Rocket**
   - Rückstoß für Mobility/Trickjumps.

---

## 4) Sicherheits- und Balance-Regeln

- **Cooldowns pro Raketen-Typ** + globales Spam-Limit.
- **WorldGuard/Region-Hooks (optional später)**, um Spawn/Hub zu schützen.
- **Friendly-Fire-Policy** konfigurierbar pro Welt/Team.
- **Explosionsschäden an Blöcken** standardmäßig aus (Vanilla-Stil, griefarm).
- **Permission-Nodes** pro Raketenklasse (`rocketcraft.rocket.he`, etc.).

---

## 5) Umsetzungsschritte (Roadmap)

## Phase 0 – Projektgerüst
- Gradle-Paper-Projekt erstellen
- `plugin.yml` + Main-Klasse
- Basis-Command `/rockets`

## Phase 1 – MVP-Kampfsystem
- `RocketRegistry` + YAML-Loader
- 3–4 Raketen implementieren
- Cooldown-Service
- Einheitliche Damage-/Knockback-Berechnung

## Phase 2 – Duel-Experience
- Queue/Match-Start leichtgewichtig (oder simple Teamlogik)
- Scoreboard/Killfeed (optional)
- Balancing-Pass auf Schaden/Cooldown

## Phase 3 – Hardening
- Edge Cases (Logout, Weltwechsel, Chunk unload)
- Performance-Profiling bei Partikelintensität
- Konfig-Migrationen und Versionierung

---

## 6) Orientierung an offizieller Dokumentation (Quellen)

Für die Implementierung sollten primär diese offiziellen Dokumentationen genutzt werden:

1. Paper Developer Documentation: https://docs.papermc.io/
2. Paper API Javadocs: https://jd.papermc.io/paper/
3. Bukkit/Spigot Plugin-Konzept & `plugin.yml` Referenzen:
   - https://www.spigotmc.org/wiki/plugin-yml/
   - https://hub.spigotmc.org/javadocs/spigot/
4. Adventure (Text-Komponenten, von Paper genutzt): https://docs.advntr.dev/

> Hinweis: In dieser Umgebung war direkter externer Zugriff beim Abrufen der Dokumentationsseiten blockiert (HTTP 403 via Tunnel). Das Konzept basiert daher auf etablierten Best Practices für aktuelle Paper-Plugins und verweist auf die offiziellen Quellen zur direkten Umsetzung im nächsten Schritt.

---

## 7) Konkreter Startpunkt (empfohlen)

Als nächstes direkt umsetzen:
1. Gradle-Setup mit Paper API
2. `plugin.yml` + Startklasse
3. `rockets.yml` mit 3 Beispielraketen
4. Listener für Rechtsklick-Launch + Cooldown
5. Einschlaglogik für Schaden/Knockback/Partikel

Damit habt ihr sehr schnell einen spielbaren Prototyp für „lustige Duelle“, der technisch sauber auf Paper aufsetzt und danach einfach erweitert werden kann.
