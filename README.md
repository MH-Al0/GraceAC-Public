# Grace Anticheat

> Advanced, free anticheat for Java Minecraft servers (1.19+). Built to catch what others miss.

Grace targets **closet cheating** and **ghost cheating** these are the subtle, hard-to-detect cheats that plague competitive servers. Blatant hacking is handled too, but it isn't the focus. 

Unlike Watchdog, Sentinel, and Grim, Grace is free. Unlike NCP and other aging open-source anticheats, Grace is built for modern Minecraft with a rules-based detection pipeline and a statistical behavioral analysis layer.

---

## Architecture

Grace runs as a **Velocity proxy plugin** with a **Paper companion plugin** on the backend server.
```
[Client] → [Grace / Velocity Proxy] → [Paper Server + GraceCompanion]
```

The proxy intercepts raw packets. The companion feeds server-confirmed truth : hit registration, movement validation, TPS. Together they give Grace a complete picture of player behavior that neither layer could achieve alone.

---

## Requirements

- **Velocity** 3.3.0+ (proxy)
- **Paper** 1.19+ (backend server)
- **Java** 21+

---

## Installation

**1 — Proxy side**

Drop `grace-velocity.jar` into your Velocity `plugins/` folder.

On first boot Grace generates `plugins/grace/config.yml`. Configure your bridge port, alert settings, and optionally a Discord webhook URL for staff alerts.

**2 — Backend server**

Drop `grace-companion.jar` into your Paper server `plugins/` folder.

Grace Companion connects back to the Velocity proxy over a local socket. Make sure `bridge.port` matches in both configs. Default is `27016`.

**3 — Firewall**

If your proxy and backend server are on separate machines, make sure port `27016` (or your configured port) is open between them. If they're on the same machine, no firewall changes are needed.

---

## Configuration
```yaml
grace:
  bridge:
    port: 27016 # make sure this port is open / not blocked by firewall

  alerts:
    ingame-enabled: true  # alert staff in-game
    discord-enabled: false # send alerts to Discord
    webhook-url: ""        # paste your Discord webhook URL here
    min-level: "HARD_ALERT" # SOFT_ALERT / HARD_ALERT / ACTION

  punishment:
    enabled: true
    rubber-band-on-hard-alert: true # teleport player back on HARD_ALERT
    kick-on-action: true            # kick on ACTION tier
    ban-on-action: false            # auto-ban (disabled by default)
    ban-threshold: 3                # ACTION flags before ban
    kick-message: "Kicked by Grace Anticheat."
    ban-message: "Banned by Grace Anticheat."

  spoof:
    brand: "vanilla"  # spoof server brand to hide Grace from cheat clients
    enabled: true
```

---

## Commands

| Command | Permission | Description |
|---|---|---|
| `/grace status` | `grace.command` | Engine status, check count, tracked players |
| `/grace player <name>` | `grace.command` | Player evidence summary |
| `/grace alerts` | `grace.command` | Toggle your personal alert notifications |
| `/grace inspect <name>` | `grace.admin` | Full violation history for a player |
| `/grace reload` | `grace.admin` | Reload config without restart |

---

## Discord Alerts

Grace can send formatted embeds to a Discord channel when a player is flagged.

1. Go to your staff channel → **Edit Channel** → **Integrations** → **Webhooks** → **New Webhook** → **Copy Webhook URL**
2. Paste the URL into `config.yml` under `webhook-url`
3. Set `discord-enabled: true`

---

## Open Source

Grace's **framework** - the API, event pipeline, data model, and companion plugin is open source.

Grace's **detection logic** - check implementations, model weights, and threshold tuning is closed source. This is intentional. Open detection logic is a bypass roadmap for cheat developers.

---

## Status

**Beta 0.1.0** - active development. Expect bugs. Thresholds are conservative by default to minimize false positives on first deployment. If Grace is missing cheaters on your server, thresholds can be tuned in config.

Bug reports and feedback welcome via GitHub Issues.

---

## Roadmap


- **0.1.0** — ML sidecar (grace-ml) first iteration, behavioral analysis layer, complete checks and advaned setup help for early servers.
- **0.2.0** — Dashboard, per-server threshold tuning, ban history UI
