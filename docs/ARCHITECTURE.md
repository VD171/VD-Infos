> **Languages:** English · [العربية](ar/ARCHITECTURE.md) · [Čeština](cs/ARCHITECTURE.md) · [Deutsch](de/ARCHITECTURE.md) · [Español](es/ARCHITECTURE.md) · [فارسی](fa/ARCHITECTURE.md) · [Français](fr/ARCHITECTURE.md) · [हिन्दी](hi/ARCHITECTURE.md) · [Bahasa Indonesia](id/ARCHITECTURE.md) · [Italiano](it/ARCHITECTURE.md) · [日本語](ja/ARCHITECTURE.md) · [한국어](ko/ARCHITECTURE.md) · [Nederlands](nl/ARCHITECTURE.md) · [Polski](pl/ARCHITECTURE.md) · [Português](pt/ARCHITECTURE.md) · [Русский](ru/ARCHITECTURE.md) · [Svenska](sv/ARCHITECTURE.md) · [ไทย](th/ARCHITECTURE.md) · [Türkçe](tr/ARCHITECTURE.md) · [Українська](uk/ARCHITECTURE.md) · [Tiếng Việt](vi/ARCHITECTURE.md) · [中文](zh/ARCHITECTURE.md)

---

# Architecture

This is a high-level map of how VD Infos is put together. It describes the shape of the
app, not the catalogue of what it reads: the point of a method debugger is to put every
reading in front of you, and reading them is yours to do on your own device.

## Design goals

- **One value, every method.** For each piece of information, read it through every
  method that can read it and line the readings up. A debugger surfaces evidence; it
  does not pass judgement.
- **On-device and quiet.** No network, no account, no background work. The app runs
  only while it is open.
- **Readable by machines.** The whole result is one machine-readable document, so a
  tool gets the same picture a person does. See [AI agents](AI-AGENTS.md).
- **Small where it matters.** The part that must be hard to fool is kept deliberately
  tiny.

## The lenses

A *lens* is one way of reading a value. The same value is read through several so they
can be compared:

| Lens | What it is |
|---|---|
| **Java / SDK** | The platform API an ordinary app calls. |
| **Native / JNI** | The same value read from native code. |
| **Shell via JVM** | A shell command spawned by the JVM. |
| **Shell via JNI** | The same command spawned from native code, a different vantage point. |
| **TEE / Attestation** | Hardware-backed key attestation, where it applies. |

Lining the lenses up is the whole idea: when they disagree, something between the app
and the value is answering differently to different callers. What that means for a
given value, on a given device, is yours to read.

## How a scan runs

```
core/model      immutable domain: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          the probes and the bridges they use (JNI, reflection, data lists)
engine/         ProbeEngine: parallel fan-out, streams results as they land
data/           SnapshotStore (persist the report), Exporter (share as JSON / text)
ui/             Jetpack Compose, Material 3, dynamic colour, live progress
cpp/            native_probes.cpp: the native lens, dependency-free
```

- **Parallelism.** The probes fan out across a dispatcher with a bounded permit count;
  results stream into the UI as they complete, with a live progress indicator.
- **Nothing in the background.** No services and no scheduled scans. The app reads only
  while open and closes itself when left idle.
- **The native layer.** One small `.so`, bound by name through `RegisterNatives`, kept
  deliberately tiny because it is the part that must be hard to fool.

## The report

Every scan produces a `Snapshot`: the time it was taken, the app version, and a list of
results, one per probe, each with the readings from every lens and a verdict for how
they relate. The app persists this to private storage after each scan and can export
the same data. The structure is documented as the [export format](EXPORT-FORMAT.md).

## Extending it

Most of what the app looks for lives as **plain-text lists** under
`assets/data/`, not as code. Adding or removing a target is editing one line. See
[Contributing](CONTRIBUTING.md).

## What VD Infos never does

- It never changes anything on your system. It only reads and compares.
- It never talks to the network. It works fully offline.
- It never runs in the background, schedules a scan, or keeps a service alive.
- It never sends a reading anywhere. A report leaves the device only when you share or
  save it, with identity-bearing values masked.

<!-- doc-footer -->

---

> _This documentation was generated with Claude (Anthropic) and is maintained by VD171. Corrections and improvements are welcome; see [CONTRIBUTING.md](CONTRIBUTING.md)._
