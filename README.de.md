[English](README.md) | [Português](README.pt.md) | [Español](README.es.md) | [Italiano](README.it.md) | **Deutsch** | [Français](README.fr.md) | [Русский](README.ru.md) | [Bahasa Indonesia](README.id.md) | [Türkçe](README.tr.md) | [Polski](README.pl.md) | [Nederlands](README.nl.md) | [Svenska](README.sv.md) | [Čeština](README.cs.md) | [Tiếng Việt](README.vi.md) | [中文](README.zh.md) | [日本語](README.ja.md) | [한국어](README.ko.md) | [فارسی](README.fa.md) | [हिन्दी](README.hi.md) | [العربية](README.ar.md) | [ไทย](README.th.md)

# VD Infos

*Methoden-Debugger*

[![License: AGPL-3.0-or-later](https://img.shields.io/badge/license-AGPL--3.0--or--later-blue.svg)](LICENSE)
[![Release](https://img.shields.io/github/v/release/VD171/VD-Infos)](https://github.com/VD171/VD-Infos/releases)
[![Downloads](https://img.shields.io/github/downloads/VD171/VD-Infos/total)](https://github.com/VD171/VD-Infos/releases)
[![Android 8.0+](https://img.shields.io/badge/Android-8.0%2B-3DDC84.svg)](https://github.com/VD171/VD-Infos/releases)
[![Languages](https://img.shields.io/badge/languages-21-orange.svg)](README.md)

<img src="images/vdinfos-01.png" height="420"/> <img src="images/vdinfos-02.png" height="420"/>

Android ist ein enorm leistungsfähiges und vielseitiges Betriebssystem; was dir niemand sagt: All deine persönlichen Daten und vertraulichen Informationen stehen jeder App zur Verfügung, die du installierst, und dich gegen diese Eingriffe in die Privatsphäre zu schützen ist eine Pflicht. VD Infos zeigt dir ein Beispiel dessen, was von deinem Gerät erfasst werden kann, und tut das als *Methoden-Debugger*: Für jede Information liest es den Wert über **jede Methode, die ihn lesen kann** - `Build.*`, `SystemProperties`, `getprop`, das native `__system_property_get`, System-Manager, Content Provider, Dateien, Syscalls und Hardware-Key-Attestation (TEE) - und stellt sie zum Vergleich nebeneinander. Wenn eine Methode den anderen widerspricht, schreibt etwas dazwischen diese Oberfläche um: ein Hooking-Framework, ein Spoofer, ein Resolver-Shim. **ES WERDEN KEINE INFORMATIONEN GESPEICHERT, GESENDET ODER AN IRGENDEINE DATEI ODER IRGENDEINEN SERVER ÜBERTRAGEN** - alles läuft auf dem Gerät, identitätstragende Werte sind maskiert, bis du sie aufdeckst, und ein Bericht verlässt das Gerät nur, wenn du ihn ausdrücklich teilst oder speicherst; wenn du willst, blockiere den Internetzugang mit einer Firewall oder schalte ihn einfach ab.

## Was es prüft

Jeder Punkt wird über jede Methode gelesen, die ihn lesen kann (Java-SDK / nativ / Shell), nebeneinander verglichen:

* **~488 Systemeigenschaften** auf FÜNF Wegen gelesen: `SystemProperties.get`, beide Einstiegspunkte von bionic (`__system_property_read_callback` und das 92-Byte-`__system_property_get`), von der JVM gestartetes `getprop` und aus nativem Code über `popen` gestartetes `getprop` - ein von `ProcessBuilder` ausgeführter Shell-Befehl und derselbe Befehl ohne JVM sind nicht derselbe Blickwinkel, denn Ersteres ist eine Oberfläche, die ein Root-Verbergungs-Framework umschreibt, Letzteres nicht.
* **Geräteidentität**: Modell, Hersteller, Marke, device, product, board, hardware, fingerprint, bootloader, build id/tags/type - jedes `Build.*`-Feld gegen alle seine `ro.product.*`-Varianten (system/vendor/odm), nativ und Shell.
* **Kennungen**: serial (viele Getter), Android ID (settings und provider), GSF ID, advertising ID, IMEI/primary IMEI/MEID/IMSI/ICCID, user serial.
* **Telefonie**: die TelephonyManager-Oberfläche (Betreiber, SIM, Netz, Roaming...), SubscriptionManager (Multi-SIM), cell info.
* **Netzwerk**: **MAC** pro Schnittstelle (`NetworkInterface` vs `/sys/class/net` vs Shell), Wi-Fi (SSID/BSSID/IP/MAC), Bluetooth, DNS.
* **Kernel / Prozess / System**: `uname`, boot id, uptime, UID/PID, SELinux, Zeitzone, locale, Umgebung, laufende Dienste/Prozesse.
* **Hardware / Medien**: CPU, Speicher, Sensoren, Display, Kameras, system features, GPU, Widevine-DRM-ID.
* **Pakete / Konten / WebView**: Liste und Anzahl installierter Pakete, Signatur-Digests pro App, Signatur dieser App, Selbstinspektion des eigenen Pakets, Intent-Auflösungs-Enumerationen, Konten, Anzahl der Anrufliste, user agent.
* **Sandbox-Konformität**: Oberflächen, die die Sandbox SCHLIESSEN soll (eMMC CID/serial, UFS- und SoC-Identität, `/proc/cmdline`, `/proc/1/*`, `/dev/kmsg`, `/system/build.prop`) und 22 `dumpsys`-Dienste. Hier ist die Verweigerung die Antwort: `EACCES`/`DENIED` ist die konforme Lesung, und ein Gerät, das den Inhalt stattdessen herausgibt, ist nicht konform.
* **Werbe-Kennungen**: die GAID vom Play-Services-Binder plus die ganze Schlüsselfamilie (AAID, OAID/VAID und Huaweis `pps_*`), abgesucht über die drei settings stores.
* **Root- / Hook- / Emulator-Detektoren**: Artefakte von `su`/Magisk/KernelSU/APatch/Xposed/LSPosed/Riru, injizierte Libs in `/proc/self/maps`, bekannte Pakete, gefährliche und Emulator-Eigenschaften, verified boot state.

## Download und Support

Jedes Release veröffentlicht zwei APKs: **SDK_35** zielt auf die strikte moderne Sandbox und ist das zu verwendende; **SDK_27** zielt auf ein älteres API-Level mit lockererer SELinux-Domäne, nützlich zum Vergleich. Beide lassen sich ab Android 8.0 installieren.

* Release (APK): https://github.com/VD171/VD-Infos/releases
* https://github.com/VD171/VD-Infos
* https://xdaforums.com/t/VD-Infos.4097379/
* https://t.me/RootDetected
* https://t.me/BlankAssistance

## Sprachen

21 UI-Sprachen: Englisch, Portugiesisch, Spanisch, Italienisch, Deutsch, Französisch, Russisch, Indonesisch, Türkisch, Polnisch, Niederländisch, Schwedisch, Tschechisch, Vietnamesisch, Chinesisch, Japanisch, Koreanisch, Persisch, Hindi, Arabisch und Thailändisch. Die App bietet beim ersten Start eine einmalige Sprachauswahl (mit der Option "Systemstandard") und folgt ansonsten der Systemsprache.

## Architektur

```
core/model      unveränderliche Domäne: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          NativeBridge (JNI) - SystemProps (Reflexion) - PropCatalog (Daten)
                SemanticProbes - IntegrityProbes - ProbeRegistry
engine/         ProbeEngine - Coroutine-Fan-out, streamt Ergebnisse als Flow
data/           SnapshotStore (Persistenz + Diff) - Exporter (JSON-/Text-Freigabe)
ui/             Jetpack Compose, Material 3, dynamische Farbe, Live-Fortschritt
cpp/            native_probes.cpp - die native Linse, ohne Abhängigkeiten
```

* **Parallelität**: 885 Sonden fächern über den Standard-Dispatcher mit begrenzter Permit-Anzahl auf; die Ergebnisse strömen in die UI, sobald sie eintreffen.
* **Nichts im Hintergrund**: keine Dienste und keine geplanten Scans; die App läuft nur, solange sie geöffnet ist, und schließt sich selbst, wenn sie untätig bleibt.
* **Native Schicht**: eine kleine `.so`, per Name über `RegisterNatives` gebunden, bewusst winzig gehalten, weil sie der Teil ist, der schwer zu täuschen sein muss.

## Mitwirken

Die meisten Beiträge brauchen kein Kotlin: die Listen liegen als reiner Text unter `VDInfos/app/src/main/assets/data/`. Füge eine Zeile hinzu oder entferne sie und öffne einen Pull Request.

* `*_apps.txt` - ein Paketname pro Zeile
* `props.txt` - der Katalog der Systemeigenschaften, `KATEGORIE<tab>Schlüssel`
* `spoof_keys.txt` - die Settings-Spoof-Matrix, `Schlüssel:TYP`
* weitere `.txt`-Dateien - ebenfalls ein Eintrag pro Zeile (Kernelmodul-Namensfragmente, Namen in `/data/local/tmp`)

Ein `#` beginnt einen Kommentar; leere Zeilen werden ignoriert. Code-Beiträge sind ebenfalls willkommen. Mit deinem Beitrag stimmst du zu, dass er unter der AGPL-3.0-or-later dieses Projekts erscheint.

## Katalog

Referenzen zum Verbergen und Erkennen von Root (Anleitungen, Module, Frameworks, Detektoren) liegen in einem eigenen Katalog: [CATALOG.md](CATALOG.md).

## Danksagungen

Besonderer Dank an **[frknkrc44](https://github.com/frknkrc44)**, Entwickler von **[HMA-OSS](https://github.com/frknkrc44/HMA-OSS)**, für die investierte Sorgfalt. HMA-OSS ist der Ort, an dem mehrere der von dieser App vorgeschlagenen Korrekturen angewendet werden - Ziel-Apps verbergen und Spoof-Presets setzen - und jede Erwähnung davon in der App verlinkt zurück auf die Quelle.

Seine Links, Kanäle und wie man es unterstützt: [HMA-OSS.md](HMA-OSS.md).

## Kontakte

* https://vd171.ru
* https://vd.priv8.ru
* **Telegram:** @VD_Priv8 https://t.me/VD_Priv8
* **Discord:** @VD.Priv8 https://discord.com/users/1296831918989639721
* **E-Mail:** vd.priv8@pm.me
* **XDA-Developers:** @VD171 https://xdaforums.com/m/vd171.4699873/
* **GitHub:** @VD171 https://github.com/VD171

## Lizenz

**GNU AGPL-3.0-or-later** - siehe [LICENSE](LICENSE). Copyleft, einschließlich der Netzwerkklausel: Wer eine geänderte Version ausführt (auch als Dienst), muss ihren Quellcode anbieten. Bewusst für ein Forschungs-/Anti-Erkennungs-Tool gewählt, um Forks offen zu halten.

---

`android` `privacy` `security` `root-detection` `hook-detection` `spoof-detection` `anti-detection` `tamper-detection` `device-fingerprint` `magisk` `kernelsu` `lsposed` `play-integrity` `attestation` `tee` `keystore` `scanner` `diagnostics` `reverse-engineering` `kotlin`
