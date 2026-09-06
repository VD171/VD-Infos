# Changelog

All notable changes to VD Infos. Dates are ISO (YYYY-MM-DD).

The 2.x line is a ground-up rewrite; the last public 1.x release was
[v1.11-beta6](https://github.com/VD171/VD-Infos/releases/tag/v1.11-beta6)
(2024-12-02). Everything between it and 2.00 is the rewrite described below.

## [2.11]

- **About dialog.** Who wrote the app, where the source lives, the licence, and the
  contacts (Telegram, e-mail, XDA, GitHub), one tap away in the top bar. The 2.x
  rewrite had dropped this, so the information existed only in the README.
- **An unexpected error now opens a copyable dialog instead of killing the app.**
  The uncaught-exception handler writes the trace to the app's private files, the
  app relaunches, and the next start shows the full stack trace in a scrollable
  dialog with a copy button, so it can be pasted into a report. A guard stops a
  start-up failure from looping the relaunch. Nothing is transmitted: there is no
  crash reporting service and the app holds no INTERNET permission, so the report
  only leaves the device if you copy it out yourself. The dialog also links straight
  to the contacts, so the report can actually be sent somewhere.
- Native readings in the attestation items now name the property they read
  (`__system_property_get ro.boot.verifiedbootstate` instead of a bare
  `__system_property_get`), matching how the Java readings are labelled.

## [2.10]

- **Fewer false positives on identity items.** The verdict now compares only readings
  of the *same* field. Each `Build.*` is compared against its canonical property
  (`ro.product.model`, `ro.build.fingerprint` ...) through Java/native/shell; the
  per-partition variants (`ro.product.vendor.*`, `.system.*`, `.odm.*`), which Android
  deliberately lets differ, stay visible but are marked as "context" and no longer
  raise a MISMATCH. The TEE tag only joins the comparison when it follows the same
  naming convention as `Build.*` (brand/device/manufacturer); for model/product it
  uses the codename, so it is shown as context only. Model, product and fingerprint
  are back to MATCH, while a real `Build.*` hook (diverging from the canonical
  property) is still caught and the TEE security-patch divergence stays red.
- **Fixed a crash when expanding a category.** A property present both in the raw
  catalog and in a domain module that re-categorises it (emulator markers, integrity
  flags, DNS, GPU) produced two probes sharing one id; the duplicate lazy-list key
  crashed the screen as soon as both were drawn, which showed up first on the
  Emulator section. The registry now keeps a single reading per id (the domain one,
  which carries the more meaningful category).

## [2.09]

- **Deeper SELinux, input methods, and a native second opinion.** New comparison
  items, each still read through several methods: SELinux context (`/proc/self/attr/current`
  via Java, native open/read, and `id -Z`), policy version and MLS flag, the enabled
  and installed input methods (IMM, Settings.Secure, `settings`/`ime`), and boot time
  (`/proc/stat btime`). TracerPid and mount markers (magisk/overlay/ksu) are read once
  through the JVM/File and again through the native raw `open()`/`read()` so a
  Java-only hook of `/proc` files diverges from the native reading.
- **More surfaces per item.** Serial now also reads `androidboot.serialno` from
  `/proc/cmdline`; MAC adds an `ip link` reading alongside sysfs and native; install
  source adds `updateOwnerPackageName` (API 34).

## [2.08]

- Closed the gap against COPG/PIF-style Zygisk spoofers: `Build.VERSION.*` (release,
  SDK int, codename, incremental, security patch) and build time are now compared
  against their `ro.build.version.*` / `ro.build.date.utc` properties (they were
  single-method before), so a `SetStaticObjectField` spoof of those fields shows up.

## [2.07]

- **TEE / attestation lens.** Generates a hardware-attested KeyStore key and parses
  the key-attestation record (verified boot state, device-locked, patch levels,
  root-of-trust, and - when supported - brand/device/model from the secure
  hardware), placing each next to the property/Build reading. The attestation record
  is signed by the TEE, so a spoofer that rewrites Build.* and properties cannot
  rewrite it: any mismatch is a strong tell. Offline only (no network).

## [2.06]

- **Tail collectors** ported for full parity with 1.x: "this package" self-inspection
  through PackageManager (uid, gids, installer, install source, install/update times,
  target SDK, enabled/instant/suspended state, whitelisted restricted permissions,
  module info, own Xposed metadata...) and the intent-resolution enumerations
  (`queryIntentActivities`/`Services`/`BroadcastReceivers`/`ContentProviders`,
  `resolveActivity`, `queryInstrumentation`), plus `/dev/ptmx` and `stat`. ~577 items.

## [2.05]

- **Bulk collectors**: full installed-package list, per-app signing digests, and
  running services/processes (the last two return only this app on modern Android -
  itself informative).
- App header now shows the version dynamically (from `BuildConfig`), so it always
  matches the build.
- **Package renamed** to `ru.vd171.vdinfos`.
- README's "What it inspects" now carries the full coverage (a separate coverage
  document was folded in and removed).

## [2.04]

- Wider device-identity/hardware surface, each read through every method:
  SubscriptionManager (multi-SIM: iccid/number/carrier/mcc/mnc/country per sub),
  primary IMEI, user serial, Android ID via the secure provider, cell info,
  cameras, system features, and more Settings keys (wifi_mac, device_name,
  boot_count, adb_enabled, development_settings_enabled, data_roaming, http_proxy).

## [2.03]

- **Method debugger.** Reworked so each information item is read through *every*
  method that can read it (e.g. `Build.SERIAL` / `Build.getSerial()` /
  `SystemProperties` / `getprop` / native), compared side by side; each reading
  shows its exact source. Every system property is read three ways
  (SystemProperties / native / getprop).
- **Wide coverage**: device identity, identifiers, the TelephonyManager surface,
  network (per-interface MAC, Wi-Fi, Bluetooth, DNS), kernel/process/system,
  hardware (CPU/memory/sensors/display/GPU), Widevine DRM, packages, accounts,
  WebView, and expanded root/hook/emulator detectors.
- **UI** grouped into collapsible per-category sections with counts, defaulting open
  where a divergence exists; results stream in as the scan runs.
- **Engine** hardened: the shell lens can never stall the scan (bounded,
  kill-then-read), probes run on the IO dispatcher with a per-probe timeout, and
  results are no longer dropped when the buffer is full.

## [2.02]

- **Save to file** via the Storage Access Framework: a modern document picker
  where you choose the folder and can rename the file (a name is only suggested).
  The previous share sheet stays as a separate action.
- **Screen capture policy probe** (category *Security*): surfaces
  `DevicePolicyManager.getScreenCaptureDisabled`, the value that FLAG_SECURE /
  screenshot-unblocking modules typically force. Java-lens only, since there is no
  native/syscall counterpart to cross-check it against.
- Typography: purged em/en dashes from the whole source tree and added a guard so
  they cannot creep back in.
- Docs: unified the legacy GitHub README/LEIAME with the 2.x READMEs (English and
  pt-BR); moved the root-hiding and root-detection lists into a dedicated catalog
  (`CATALOG.md`); refreshed the contacts and download sections.

## [2.01]

- **Bilingual app**: every user-facing string moved to resources - English default
  (`values/`) plus pt-BR (`values-pt/`); the app follows the device language.
- **Bilingual docs**: `README.md` (English) and `README.pt-BR.md`.
- **License**: GNU **AGPL-3.0-or-later** (was MIT). Copyleft with the network
  clause, chosen to keep forks of a research/anti-detection tool open.

## [2.00] - rewrite

A ground-up rewrite around a single idea: read every fact twice (Java SDK vs
native/JNI) and surface the divergences. Divergence means a likely hook
(XPrivacyLua / Xposed), which was the original 1.x use case.

### Added
- **Dual-lens verification.** Every probe is read through the Java SDK *and*
  through libc/syscalls via JNI; a **verdict** flags where they disagree.
- **Native lens (JNI).** `__system_property_get`, `uname(2)`, raw `open()/read()`
  of `/proc` and `/sys`, per-interface MAC from `/sys/class/net`, `getuid`,
  `statvfs`, and an `access()`-based root/hook artifact scan - no shell process.
- **Parallelism.** ~380 probes fan out over coroutines with bounded concurrency
  and stream into the UI as they complete (was a single serial `AsyncTask`).
- **Background action.** A WorkManager job re-scans on a schedule, diffs against
  the previous capture, and notifies when any value drifts between runs.
- **Modern UI.** Jetpack Compose + Material 3, dynamic colour, dark/light, live
  progress, search, category filters, "only divergent" view, PII masking, and
  JSON/text export.

### Changed
- Kotlin + Compose replace Java + `ExpandableListView`.
- `minSdk` 21 -> 26, `targetSdk` 35, Gradle Kotlin DSL + version catalog.
- The ~360-property catalog from 1.x is preserved as pure data.
- Dropped string obfuscation (lsparanoid): the source is meant to be readable.

---

## 1.x (legacy, Java)

The 1.x series was a single-Activity Java app that dumped device information into
an expandable list, used to debug XPrivacyLua. Full history and APKs live on the
[GitHub releases page](https://github.com/VD171/VD-Infos/releases).

### [1.11-beta6] - 2024-12-02

Last public 1.x release. Highlights:

- Documented how each piece of info is gathered; split reading into sections with
  per-section elapsed time.
- Removed the root detector, toybox binaries, and Memory info.
- Added a lot of Android 13 (TIRAMISU) content.
- Added a VPN-state detector and a simple LSPosed detector.
- Added export of configs to XPL-EX (XPrivacyLua Extended).
- Added WebView info, Location info, TimeZone/Locale info, GPU info, DRM ID,
  Boot ID, and keychain-file stat info.
- Added the whole PackageManager and TelephonyManager surfaces, Connection and
  network info, and Environment info.
- Added Google/Android/Amazon advertising IDs and raw GSF ID.
- Added CPU info (`cat` and file), Bluetooth name, app signatures, and a
  "This Package" view showing how the app sees itself.
- More Build/SIM/system props; simpler exception and build-date handling.

### 1.10 and earlier - 2024-03-22 and before

`1.10`, `1.09`, `1.08`, `1.06` and the rest: see the
[releases page](https://github.com/VD171/VD-Infos/releases) for their notes and
signed APKs.
