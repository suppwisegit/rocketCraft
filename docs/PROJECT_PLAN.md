# RocketCraft Projektplan

## Vision
RocketCraft erweitert Minecraft Java Edition (Paper) im Vanilla-Look um spielbare Raketen-Archetypen:
von verspielten Fun-Raketen bis zu taktisch-militaerischen Varianten fuer Duelle mit Freunden.

## Leitprinzipien
1. **Vanilla-first**: keine Resourcepacks voraussetzen.
2. **Skill + Chaos**: einfache Bedienung, hoher Skill-Ceiling.
3. **Fairness**: Cooldowns, Counterplay, klar kommunizierte Effekte.
4. **Performance**: keine Tick-Lags durch exzessive Partikel/Entity-Last.

## Entwicklungsphasen

### Phase 1 — Foundation (dieses Setup)
- Technisches Plugin-Grundgeruest.
- Raketenmodell (`RocketSpec`), Registry und Basiskommando.
- Dokumentation, Feature-Schnitt und Zielbild.

### Phase 2 — Core Gameplay Loop
- `/rocket give <typ> [spieler]`.
- Crafting-/Drop-System fuer Raketen.
- Erstes Live-Verhalten fuer 3 Raketen (`SPARK`, `BOOST`, `THUNDERSTRIKE`).
- Serverseitige Cooldowns + Actionbar-Feedback.

### Phase 3 — Duel Systems
- Arena-Modus (optional WorldGuard-freundlich).
- Teammodus (2v2 / 3v3), Friendly-Fire-Option.
- Match-Flow: Warmup -> Fight -> Endscreen -> Reset.

### Phase 4 — Progression & Balancing
- XP/Coins fuer Matches.
- Freischaltbare Varianten / kosmetische Trails (vanilla particles).
- Konfigurierbares Balancing in `config.yml`.
- Telemetrie-Logs (Nutzung pro Raketentyp).

### Phase 5 — Polishing
- Lokalisierung (DE/EN).
- Ausfuehrliche Admin-Doku.
- Feinschliff bei Sounds/Particles.
- Release Candidate + Performance-Profiling.

## Risiko-Liste
- Power-Creep durch militaerische Raketen.
- PvP-Unfairness bei hoher Tickrate-Latenz.
- Ueberladung mit zu vielen Effekten gleichzeitig.

## Erfolgskriterien (MVP)
- Spieler koennen mindestens 3 Raketentypen craften/erhalten und aktiv nutzen.
- Duelle sind ohne externe Mods/Texturepacks klar spielbar.
- Performance bleibt bei 20 TPS auf einem typischen Paper-Testserver mit 10+ Spielern stabil.
