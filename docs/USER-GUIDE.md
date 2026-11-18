> **Languages:** English · [العربية](ar/USER-GUIDE.md) · [Čeština](cs/USER-GUIDE.md) · [Deutsch](de/USER-GUIDE.md) · [Español](es/USER-GUIDE.md) · [فارسی](fa/USER-GUIDE.md) · [Français](fr/USER-GUIDE.md) · [हिन्दी](hi/USER-GUIDE.md) · [Bahasa Indonesia](id/USER-GUIDE.md) · [Italiano](it/USER-GUIDE.md) · [日本語](ja/USER-GUIDE.md) · [한국어](ko/USER-GUIDE.md) · [Nederlands](nl/USER-GUIDE.md) · [Polski](pl/USER-GUIDE.md) · [Português](pt/USER-GUIDE.md) · [Русский](ru/USER-GUIDE.md) · [Svenska](sv/USER-GUIDE.md) · [ไทย](th/USER-GUIDE.md) · [Türkçe](tr/USER-GUIDE.md) · [Українська](uk/USER-GUIDE.md) · [Tiếng Việt](vi/USER-GUIDE.md) · [中文](zh/USER-GUIDE.md)

---

# VD Infos user guide

VD Infos is a **device method debugger**. It reads every device value through every
method that can read it, and lines the readings up side by side so you can compare
them. This guide covers installing the app, running a scan, reading the result and
sharing a report. It does not interpret any specific finding for you: the comparison
is yours to read, on your own device.

## Table of contents

1. [What VD Infos is](#1-what-vd-infos-is)
2. [What it is not](#2-what-it-is-not)
3. [Requirements](#3-requirements)
4. [Which APK to install](#4-which-apk-to-install)
5. [Install and first run](#5-install-and-first-run)
6. [Running a scan](#6-running-a-scan)
7. [Reading the result](#7-reading-the-result)
8. [Export your report](#8-export-your-report)
9. [For AI agents](#9-for-ai-agents)
10. [Language and theme](#10-language-and-theme)
11. [Privacy at a glance](#11-privacy-at-a-glance)
12. [Frequently asked questions](#12-frequently-asked-questions)

## 1. What VD Infos is

Android hands a lot of your device's information to every app you install. VD Infos
shows you an example of what an ordinary app can read, and it does so as a *method
debugger*: for each piece of information it reads the value through **every method
that can read it** - `Build.*`, `SystemProperties`, `getprop`, the native
`__system_property_get`, system managers, content providers, files, syscalls, and
hardware key attestation (TEE) - and lines them up so you can compare. It is a lens,
not a verdict machine: it surfaces the readings and leaves the reading of them to you.

## 2. What it is not

- It is **not** a cleaner, a booster or a hiding tool. It changes nothing on your
  system; it only reads and compares.
- It is **not** a pass/fail score. It shows you the raw readings; what they mean for
  your setup is yours to judge, on your device.
- It does **not** need the internet, and it works fully with the internet blocked.

## 3. Requirements

- Android 8.0 (API 26) or newer.
- No special permission is required to start. Some readings become available only if
  you grant the matching permission (for example usage access), and some only exist
  on a rooted device; where a reading is unavailable, the app simply shows that it was
  not available.

## 4. Which APK to install

Each release publishes **two APKs**:

- **SDK_35** - targets the strict modern sandbox. This is the one to use.
- **SDK_27** - targets an older API level, with a looser SELinux domain. It is useful
  only for side-by-side comparison; most people do not need it.

Both install on Android 8.0 and newer. Download them from the
[Releases](https://github.com/VD171/VD-Infos/releases) page.

## 5. Install and first run

1. Download the **SDK_35** APK from Releases and install it (you may need to allow
   installs from your browser or file manager).
2. Open the app. It opens straight into the scan screen; there is no account, no
   sign-in and no setup.
3. Optionally grant a permission the app asks about (such as usage access) to unlock
   the readings that depend on it. This is entirely optional.

## 6. Running a scan

Tap the scan action. The app fans its probes out in parallel and streams results into
the list as they land, with a live progress indicator. A scan completes on its own; it
reads only while the app is open, keeps nothing running in the background, and closes
itself when left idle.

To run it again, scan again: each scan replaces the previous result.

## 7. Reading the result

Each item is a card that lines up the readings from every lens (the Java SDK method,
the native method, the shell, and so on). The card's colour tells you, at a glance,
how those readings relate to each other:

- **Match** - every lens agreed. The methods returned the same value.
- **Divergence** - the lenses did not agree. One method returned something different
  from the others.
- **Information** - a reading shown for reference, not voted on.
- **Single / unavailable** - only one lens could read it, or none could.

The card always shows the full matrix of readings, so you can see exactly which lens
said what. A collapsed card shows its first reading; expand it to see them all.

That is as far as the app speaks for you. **What a given divergence means on your
device, and what to do about it, is yours to investigate** - that is the whole point
of a debugger: it puts the evidence in front of you and lets you read it. Use the
search box to filter by property or value, and the filter chips to narrow the list.

## 8. Export your report

The share and save actions hand you the **whole result**, not just what is on screen:

- **Share / save as JSON** - the complete report in machine-readable form, the same
  data the app holds internally. This is the format to keep or to feed to a tool.
- **Share as text** - a compact human-readable summary of the divergences.

Identity-bearing values (serials, IMEI, MAC addresses, advertising IDs and the like)
are **masked in the exported report** so that a report you paste into a public thread
does not leak your own identifiers; the verdicts and the structure are preserved. The
on-device copy that only you (or root) can read keeps the real values.

## 9. For AI agents

VD Infos is built to be read by tools, not only by people. Every scan writes its full
report to the app's private storage, and the export is the same machine-readable JSON.
An AI coding assistant or analysis script running on the device can read the whole
result with one command instead of parsing screenshots. See the dedicated
[AI agents guide](AI-AGENTS.md) and the [export format](EXPORT-FORMAT.md) reference.

## 10. Language and theme

The app follows your system language where a translation exists, and you can pick one
manually. It uses Material 3 with dynamic colour and follows your light/dark setting.

## 11. Privacy at a glance

Everything runs on-device. No reading is sent anywhere, and a report leaves the device
only when you explicitly share or save it. Each scan keeps its full report in the app's
private storage (`files/last_snapshot.json`) so tools can read it; only the app itself
or root can read that file, the next scan overwrites it, and it never leaves the device.
See [Privacy](PRIVACY.md) for the full statement.

## 12. Frequently asked questions

**Does it change anything on my device?** No. It only reads and compares.

**Does it need root?** No. The app runs without root. A handful of readings only exist
on a rooted device, and the private report file can only be read back by root or the
app itself; everything else works unrooted.

**Does it need the internet?** No. It works fully offline; block its network access if
you like.

**Which APK should I use?** SDK_35. The SDK_27 build exists only for comparison.

**Why do some items say unavailable?** Because that method could not read that value on
your device - a missing permission, a sandbox rule, or hardware that is not present.
That is itself part of the comparison.

**Where do I report an issue or suggest a detection target?** See
[Contributing](CONTRIBUTING.md); most detection lists are plain text and need no code.

<!-- doc-footer -->

---

> _This documentation was generated with Claude (Anthropic) and is maintained by VD171. Corrections and improvements are welcome; see [CONTRIBUTING.md](CONTRIBUTING.md)._
