> **Languages:** English · [العربية](ar/AI-AGENTS.md) · [Čeština](cs/AI-AGENTS.md) · [Deutsch](de/AI-AGENTS.md) · [Español](es/AI-AGENTS.md) · [فارسی](fa/AI-AGENTS.md) · [Français](fr/AI-AGENTS.md) · [हिन्दी](hi/AI-AGENTS.md) · [Bahasa Indonesia](id/AI-AGENTS.md) · [Italiano](it/AI-AGENTS.md) · [日本語](ja/AI-AGENTS.md) · [한국어](ko/AI-AGENTS.md) · [Nederlands](nl/AI-AGENTS.md) · [Polski](pl/AI-AGENTS.md) · [Português](pt/AI-AGENTS.md) · [Русский](ru/AI-AGENTS.md) · [Svenska](sv/AI-AGENTS.md) · [ไทย](th/AI-AGENTS.md) · [Türkçe](tr/AI-AGENTS.md) · [Українська](uk/AI-AGENTS.md) · [Tiếng Việt](vi/AI-AGENTS.md) · [中文](zh/AI-AGENTS.md)

---

# VD Infos for AI agents

VD Infos is **built to be read by machines, not only by people**. A device method
debugger produces a lot of structured evidence - every value, read through every
method, lined up for comparison - and that evidence is far more useful to an agent as
data than as a screenshot. So the whole result is available as one machine-readable
document, and the app is designed around that from the ground up.

If you are an AI coding assistant, an LLM tool, an analysis script or any automation
that reaches the device through a shell, this page is for you.

## Why it is agent-friendly

- **One document, not a screen.** The entire scan - every probe, every lens, every
  verdict - is a single JSON object. No OCR, no screen scraping, no tapping through
  collapsed cards.
- **The same data the UI has.** The file is not a summary. It is exactly what the app
  holds internally: the screen shows the first reading of a collapsed item, the file
  has all of them.
- **Deterministic shape.** The structure is stable and documented (see
  [EXPORT-FORMAT](EXPORT-FORMAT.md)), so a parser written once keeps working.
- **No network, no account, no state to manage.** The app reads on-device and writes a
  file. Your agent reads the file. That is the whole contract.
- **Offline by design.** It needs no internet, so it runs the same inside a locked-down
  or air-gapped environment an agent might be operating in.

## The one-command read

Every scan writes its full report to the app's private storage. After a scan has run,
an agent reads the complete result with a single command:

```sh
su -c cat /data/data/ru.vd171.vdinfos/files/last_snapshot.json
```

- **What it contains:** every probe with its verdict and the value from every lens,
  in the same JSON as the export button.
- **When it updates:** at the end of every scan. Run a scan first; the file always
  holds the latest one.
- **Requirement:** root, because the file lives in the app's private storage. Without
  root, use the in-app **share / save as JSON** action instead and read the file the
  agent is handed.

## A typical agent flow

1. Make sure the app is installed (the **SDK_35** APK from
   [Releases](https://github.com/VD171/VD-Infos/releases)).
2. Trigger a scan - a person taps scan, or an automation launches the app's scan
   screen - and wait for it to finish.
3. Read `files/last_snapshot.json` with the command above.
4. Parse it with the [export format](EXPORT-FORMAT.md) as your schema: iterate the
   probes, read each one's `verdict`, and line up the per-lens `values`.
5. Act on the structured result in your own workflow.

The agent decides what the readings mean for the task at hand; VD Infos supplies the
evidence, cleanly and completely.

## What the agent gets, and what it does not

- The report carries **every reading and every verdict**, so an agent has the full
  picture the app built.
- Identity-bearing values (serials, IMEI, MAC addresses, advertising IDs and the like)
  are **masked in the shared/exported report**, so a report handed around stays free of
  personal identifiers while keeping its verdicts and structure. The **on-device**
  `last_snapshot.json`, readable only by the app or root, keeps the real values for a
  local agent that needs them.

## Documentation, written to be parsed

This wiki is structured for machine reading as much as for people: short sections,
stable headings, explicit field names, and a dedicated [export format](EXPORT-FORMAT.md)
page an agent can treat as a schema. If you are building a tool on top of VD Infos,
start there.

## Related pages

- [Export format](EXPORT-FORMAT.md) - the JSON schema, field by field.
- [User guide](USER-GUIDE.md) - installing, scanning and sharing.
- [Architecture](ARCHITECTURE.md) - how the app is put together.

<!-- doc-footer -->

---

> _This documentation was generated with Claude (Anthropic) and is maintained by VD171. Corrections and improvements are welcome; see [CONTRIBUTING.md](CONTRIBUTING.md)._
