<!--
  VD INFOS: device method debugger: read every device info by every method, then compare.
  Copyright (C) 2026  VD171. SPDX-License-Identifier: AGPL-3.0-or-later
-->

# VD Infos

<p align="center">
  <img src="images/logo-wordmark.png" alt="VD Infos" width="320">
</p>

<p align="center">
  <a href="https://github.com/VD171/VD-Infos/releases/latest"><img src="https://img.shields.io/github/v/release/VD171/VD-Infos?label=release&color=0D9488" alt="Release"></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/license-AGPL--3.0--or--later-1a7f37" alt="License"></a>
  <a href="https://github.com/VD171/VD-Infos/releases"><img src="https://img.shields.io/github/downloads/VD171/VD-Infos/total?label=downloads&color=8250df" alt="Downloads"></a>
  <a href="https://github.com/VD171/VD-Infos/stargazers"><img src="https://img.shields.io/github/stars/VD171/VD-Infos?color=d29922" alt="Stars"></a>
</p>

<p align="center">
  <b>Device method debugger.</b><br>
  Read every device value through every method that can read it, and line them up so you can compare.
</p>

<p align="center">
  <a href="https://github.com/VD171/VD-Infos/releases">Releases</a> ·
  <a href="https://github.com/VD171/VD-Infos/wiki">Wiki</a> ·
  <a href="https://github.com/VD171/VD-Infos/wiki/USER-GUIDE">User guide</a> ·
  <a href="https://github.com/VD171/VD-Infos/wiki/AI-AGENTS">AI agents</a> ·
  <a href="https://github.com/VD171/VD-Infos/wiki/EXPORT-FORMAT">Export format</a> ·
  <a href="https://github.com/VD171/VD-Infos/wiki/ARCHITECTURE">Architecture</a> ·
  <a href="CATALOG.md">Catalog</a> ·
  <a href="CHANGELOG.md">Changelog</a> ·
  <a href="https://xdaforums.com/t/VD-Infos.4097379/">XDA thread</a>
</p>

> **Languages:** [English](README.md) · [العربية](README.ar.md) · [Čeština](README.cs.md) · [Deutsch](README.de.md) · [Español](README.es.md) · [فارسی](README.fa.md) · [Français](README.fr.md) · [हिन्दी](README.hi.md) · [Bahasa Indonesia](README.id.md) · [Italiano](README.it.md) · [日本語](README.ja.md) · [한국어](README.ko.md) · [Nederlands](README.nl.md) · [Polski](README.pl.md) · [Português](README.pt.md) · [Русский](README.ru.md) · [Svenska](README.sv.md) · [ไทย](README.th.md) · [Türkçe](README.tr.md) · [Українська](README.uk.md) · [Tiếng Việt](README.vi.md) · [中文](README.zh.md)

<p align="center">
  <img src="images/vdinfos-01.png" height="420"/>
  <img src="images/vdinfos-02.png" height="420"/>
</p>

---

Android is a super powerful and versatile operating system; what nobody tells you
is that all your personal details and confidential information are available to
every application you install, and protecting yourself against these invasions of
privacy is an obligation. VD Infos shows you an example of what can be captured from
your device, and it does so as a *method debugger*: for each piece of information it
reads the value through **every method that can read it**: `Build.*`,
`SystemProperties`, `getprop`, the native `__system_property_get`, system managers,
content providers, files, syscalls, and hardware key attestation (TEE), and lines
them up so you can compare. When one method disagrees with the others, something in
between is rewriting that surface: a hooking framework, a spoofer, a resolver shim.
**NO INFORMATION IS STORED, SENT OR TRANSMITTED TO ANY FILE¹ OR SERVER**: everything
runs on-device, identity-bearing values are masked until you reveal them, and a
report only leaves the device when you explicitly share or save it; if you want,
block internet access with a firewall or just turn it off.

> **¹ Exception:** each scan keeps its full report in the app's private storage (`files/last_snapshot.json`). Only the app itself or root can read it, the next scan overwrites it, and it never leaves the device. It exists so AI agents can read the complete result; see **For AI agents: the full report as JSON**.

## What it inspects

Each item is read through every method that can read it (Java SDK / native / shell),
compared side by side:

* **~488 system properties** read FIVE ways: `SystemProperties.get`, both of
  bionic's entry points (`__system_property_read_callback` and the 92-byte
  `__system_property_get`), `getprop` spawned by the JVM, and `getprop` spawned
  from native code through `popen`; a shell command run by `ProcessBuilder` and
  the same command run without the JVM are not the same vantage point, because
  the first is a surface a root-hiding framework rewrites and the second is not.
* **Device identity**: model, manufacturer, brand, device, product, board, hardware,
  fingerprint, bootloader, build id/tags/type, each `Build.*` field against all its
  `ro.product.*` (system/vendor/odm) variants, native, and shell.
* **Identifiers**: serial (many getters), Android ID (settings and provider), GSF
  ID, advertising ID, IMEI/primary IMEI/MEID/IMSI/ICCID, user serial.
* **Telephony**: the TelephonyManager surface (operator, SIM, network, roaming...),
  SubscriptionManager (multi-SIM), cell info.
* **Network**: per-interface **MAC** (`NetworkInterface` vs `/sys/class/net` vs
  shell), Wi-Fi (SSID/BSSID/IP/MAC), Bluetooth, DNS.
* **Kernel / process / system**: `uname`, boot id, uptime, UID/PID, SELinux,
  timezone, locale, environment, running services/processes.
* **Hardware / media**: CPU, memory, sensors, display, cameras, system features,
  GPU, Widevine DRM ID.
* **Packages / accounts / WebView**: installed package list and count, per-app
  signing digests, this-app signature, self-package introspection, intent-resolution enumerations, accounts, call-log count, user agent.
* **Sandbox conformance**: surfaces the sandbox is meant to CLOSE (eMMC CID/serial,
  UFS and SoC identity, `/proc/cmdline`, `/proc/1/*`, `/dev/kmsg`,
  `/system/build.prop`) and 22 `dumpsys` services. Here the refusal is the answer:
  `EACCES`/`DENIED` is the conformant reading, and a device that hands the content
  over instead is out of conformance.
* **Advertising identifiers**: the GAID from the Play Services binder, plus the whole
  key family (AAID, OAID/VAID and Huawei's `pps_*`) swept across the three settings
  stores.
* **Root / hook / emulator detectors**: `su`/Magisk/KernelSU/APatch/Xposed/LSPosed/
  Riru artifacts, injected libs in `/proc/self/maps`, known packages, dangerous and
  emulator properties, verified boot state.

## Download and support

Each release publishes two APKs: **SDK_35** targets the strict modern sandbox and is the one to use; **SDK_27** targets an older API level, with a looser SELinux domain, useful for comparison. Both install on Android 8.0 and newer.

* Release (APK): https://github.com/VD171/VD-Infos/releases
* https://github.com/VD171/VD-Infos
* https://xdaforums.com/t/VD-Infos.4097379/
* https://t.me/RootDetected
* https://t.me/BlankAssistance

## For AI agents: the full report as JSON

Made for AI agents and LLM tools that work on the device through a shell (coding assistants, analysis scripts). Instead of reading screenshots, the agent reads the whole result with one command:

```sh
su -c cat /data/data/ru.vd171.vdinfos/files/last_snapshot.json
```

* **What it contains:** every probe with its verdict and the value from every lens, in the same JSON as the export button. The screen shows only the first reading of a collapsed item; the file has all of them.
* **When it updates:** at the end of every scan, so run a scan first; the file always holds the latest one.
* **Requirement:** root, because the file lives in the app's private storage.

## Architecture

```
core/model      immutable domain: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          NativeBridge (JNI), SystemProps (reflection), PropCatalog (data)
                SemanticProbes, IntegrityProbes, ProbeRegistry
engine/         ProbeEngine: coroutine fan-out, streams results as a Flow
data/           SnapshotStore (persist + diff), Exporter (JSON/text share)
ui/             Jetpack Compose, Material 3, dynamic colour, live progress
cpp/            native_probes.cpp: the native lens, dependency-free
```

* **Parallelism**: 996 probes fan out across the default dispatcher with a bounded
  permit count; results stream into the UI as they land.
* **Nothing in the background**: no services and no scheduled scans; the app runs only
  while it is open and closes itself when left idle.
* **Native layer**: one small `.so`, bound by name via `RegisterNatives`, kept
  deliberately tiny because it is the part that must be hard to fool.

## Contributing

Most contributions need no Kotlin: the lists live as plain text under `VDInfos/app/src/main/assets/data/`. Add or remove one line and open a pull request.

* `*_apps.txt`: one package name per line
* `props.txt`: the system-property catalog, `CATEGORY<tab>key`
* `spoof_keys.txt`: the settings-spoof matrix, `key:TYPE`
* `known_certs.txt`: the signing-certificate catalog, `SHA-256<tab>owner` (full hash, taken from a real APK with `apksigner verify --print-certs`)
* other `.txt` files: one entry per line too (kernel-module name fragments, `/data/local/tmp` file names)

A `#` starts a comment; blank lines are ignored. Some files pack a few space- or tab-separated fields per line, or optional markers (`S` and `@field` in `props.txt`); keep the shape of the lines already in the file. Code contributions are welcome too. By contributing you agree your work ships under this project's AGPL-3.0-or-later.

## Catalog

Root hiding and detection references (guides, modules, frameworks, detectors) live
in a dedicated catalog: [CATALOG.md](CATALOG.md).

## Acknowledgements

Special thanks to **[frknkrc44](https://github.com/frknkrc44)**, developer of
**[HMA-OSS](https://github.com/frknkrc44/HMA-OSS)**, for the care put into it.
HMA-OSS is where several of the fixes this app suggests are
applied (hiding target apps and setting spoof presets), and every in-app mention of it
links back to its source.

Its links, channels and how to support it: [HMA-OSS.md](HMA-OSS.md).

## Documentation

- **[Wiki](https://github.com/VD171/VD-Infos/wiki)**: all the docs, browsable online in 22 languages.
- **[User guide](https://github.com/VD171/VD-Infos/wiki/USER-GUIDE)**: installing, running a scan, reading the result and sharing a report.
- **[For AI agents](https://github.com/VD171/VD-Infos/wiki/AI-AGENTS)** and **[Export format](https://github.com/VD171/VD-Infos/wiki/EXPORT-FORMAT)**: the machine-readable report and its schema.
- **[Architecture](https://github.com/VD171/VD-Infos/wiki/ARCHITECTURE)** and **[Building](https://github.com/VD171/VD-Infos/wiki/BUILDING)**: how it works and how to build it.
- **[Privacy](https://github.com/VD171/VD-Infos/wiki/PRIVACY)**, **[Security](https://github.com/VD171/VD-Infos/wiki/SECURITY)** and **[Code of Conduct](https://github.com/VD171/VD-Infos/wiki/CODE_OF_CONDUCT)**: the privacy statement and the governance docs.
- **[Catalog](CATALOG.md)**: root-hiding and detection references (guides, modules, frameworks and detectors).
- **[Changelog](CHANGELOG.md)**: what changed in each release.
- **[HMA-OSS](https://github.com/VD171/VD-Infos/wiki/HMA-OSS)**: the hide-apps module the app's fixes build on (its links, channels and how to support it).
- **[Contributing](https://github.com/VD171/VD-Infos/wiki/CONTRIBUTING)**: how to add detection targets (mostly plain-text lists, no Kotlin needed).

<!-- /vd-docs -->

## See also

Sibling projects by the same author:

- **[VDGoogle](https://github.com/VD171/VD-Google)**: your Google services (RCS, Wallet, Play Integrity attestations), read on your device with no internet.
- **[VDBackup](https://github.com/VD171/VD-Backup)**: full app and data backup and restore as root, driven entirely from a WebUI.
- **[COPG-VD](https://github.com/VD171/COPG-VD)**: a device spoofer that turns a fresh fingerprint into a coherent, consistent profile.
- **[VD-dropbear](https://github.com/VD171/VD-dropbear)**: a standalone rooted SSH daemon (Dropbear).

Pixel `build.prop`: **[@VictorGabriel7Dev/build.prop](https://github.com/VictorGabriel7Dev/build.prop)** (fork of @T3SL4).

## Contacts

- https://vd171.ru
- https://vd.priv8.ru
- **Telegram:** @VD_Priv8 https://t.me/VD_Priv8
- **Channel:** @Priv8_VD https://t.me/Priv8_VD
- **Discord:** @VD.Priv8 https://discord.com/users/1296831918989639721
- **E-mail:** vd.priv8@pm.me
- **XDA-Developers:** @VD171 https://xdaforums.com/m/vd171.4699873/
- **XDA thread:** https://xdaforums.com/t/VD-Infos.4097379/
- **GitHub:** @VD171 https://github.com/VD171

## License

**GNU AGPL-3.0-or-later**: see [LICENSE](LICENSE). Copyleft, including the network clause: anyone who runs a modified version (even as a service) must offer its source. Chosen deliberately for a research/anti-detection tool, to keep forks open.

## Trademarks

VD Infos is an independent, unofficial research and diagnostics tool. It is not affiliated with, endorsed by, or sponsored by any of the vendors whose software, services or marks it reads, detects or names (including Google LLC and the Android Open Source Project). "Android", "Google", "Google Play", "Play Integrity", "Widevine" and all other product names, logos and trademarks are the property of their respective owners. VD Infos only reads what is already on your device and sends nothing anywhere.

## Tags

`android` `privacy` `security` `root-detection` `hook-detection` `spoof-detection` `anti-detection` `tamper-detection` `device-fingerprint` `magisk` `kernelsu` `lsposed` `play-integrity` `attestation` `tee` `keystore` `scanner` `diagnostics` `reverse-engineering` `kotlin`
