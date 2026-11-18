> **Languages:** English · [العربية](ar/PRIVACY.md) · [Čeština](cs/PRIVACY.md) · [Deutsch](de/PRIVACY.md) · [Español](es/PRIVACY.md) · [فارسی](fa/PRIVACY.md) · [Français](fr/PRIVACY.md) · [हिन्दी](hi/PRIVACY.md) · [Bahasa Indonesia](id/PRIVACY.md) · [Italiano](it/PRIVACY.md) · [日本語](ja/PRIVACY.md) · [한국어](ko/PRIVACY.md) · [Nederlands](nl/PRIVACY.md) · [Polski](pl/PRIVACY.md) · [Português](pt/PRIVACY.md) · [Русский](ru/PRIVACY.md) · [Svenska](sv/PRIVACY.md) · [ไทย](th/PRIVACY.md) · [Türkçe](tr/PRIVACY.md) · [Українська](uk/PRIVACY.md) · [Tiếng Việt](vi/PRIVACY.md) · [中文](zh/PRIVACY.md)

---

# Privacy

Privacy is not a feature of VD Infos. It is the whole point. This page states exactly
what the app does and does not do with your data, in plain terms you can verify.

## The promise

- **Everything runs on-device.** No reading is sent, uploaded or transmitted to any
  server. The app works fully with the internet blocked.
- **No account, no sign-in, no background work.** The app reads only while it is open,
  keeps nothing running in the background, and closes itself when left idle.
- **Nothing changes on your system.** VD Infos only reads and compares; it never writes
  to your device's state.

## The report stays with you

A report leaves the device **only when you explicitly share or save it**. Until then it
exists in two places, both on-device:

- **On screen**, while the app is open.
- In the app's **private storage** (`files/last_snapshot.json`), which only the app
  itself or root can read. The next scan overwrites it, and it never leaves the device.
  It exists so that tools and AI agents can read the full result; see the
  [AI agents guide](AI-AGENTS.md).

## Exports are masked

When you share or save a report, identity-bearing values - serials, IMEI, MAC
addresses, advertising IDs and the like - are **masked** before the report is written,
so a report you paste into a public thread does not leak your own identifiers. The
verdicts and the structure are preserved; only the sensitive values are redacted. The
private on-device copy keeps the real values for your own use.

## Permissions

The app starts with no permission required. Some readings become available only if you
grant the matching permission (for example usage access), and granting it is entirely
your choice; where a reading is unavailable, the app simply says so.

## Verify it yourself

You do not have to take any of this on trust:

- The source is public: <https://github.com/VD171/VD-Infos>.
- The manifest is in the source; confirm which permissions it declares.
- Block the app's network access with a firewall, or turn the internet off, and it keeps
  working the same.

See the [user guide](USER-GUIDE.md) for how to read a report and what the export
contains.

<!-- doc-footer -->

---

> _This documentation was generated with Claude (Anthropic) and is maintained by VD171. Corrections and improvements are welcome; see [CONTRIBUTING.md](CONTRIBUTING.md)._
