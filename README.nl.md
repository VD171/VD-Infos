[English](README.md) | [Português](README.pt.md) | [Español](README.es.md) | [Italiano](README.it.md) | [Deutsch](README.de.md) | [Français](README.fr.md) | [Русский](README.ru.md) | [Bahasa Indonesia](README.id.md) | [Türkçe](README.tr.md) | [Polski](README.pl.md) | **Nederlands** | [Svenska](README.sv.md) | [Čeština](README.cs.md) | [Tiếng Việt](README.vi.md) | [中文](README.zh.md) | [日本語](README.ja.md) | [한국어](README.ko.md) | [فارسی](README.fa.md) | [हिन्दी](README.hi.md) | [العربية](README.ar.md) | [ไทย](README.th.md)

# VD Infos

*Methode-debugger*

[![License: AGPL-3.0-or-later](https://img.shields.io/badge/license-AGPL--3.0--or--later-blue.svg)](LICENSE)
[![Release](https://img.shields.io/github/v/release/VD171/VD-Infos)](https://github.com/VD171/VD-Infos/releases)
[![Downloads](https://img.shields.io/github/downloads/VD171/VD-Infos/total)](https://github.com/VD171/VD-Infos/releases)
[![Android 8.0+](https://img.shields.io/badge/Android-8.0%2B-3DDC84.svg)](https://github.com/VD171/VD-Infos/releases)
[![Languages](https://img.shields.io/badge/languages-21-orange.svg)](README.md)

<img src="images/vdinfos-01.png" height="420"/> <img src="images/vdinfos-02.png" height="420"/>

Android is een enorm krachtig en veelzijdig besturingssysteem; wat niemand je vertelt, is dat al je persoonlijke gegevens en vertrouwelijke informatie beschikbaar zijn voor elke app die je installeert, en jezelf beschermen tegen deze inbreuken op je privacy is een verplichting. VD Infos toont je een voorbeeld van wat er van je toestel kan worden vastgelegd, en doet dat als een *methode-debugger*: voor elk stukje informatie leest het de waarde via **elke methode die het kan lezen** - `Build.*`, `SystemProperties`, `getprop`, de native `__system_property_get`, systeemmanagers, content providers, bestanden, syscalls en hardware-sleutel-attestation (TEE) - en zet ze naast elkaar zodat je kunt vergelijken. Wanneer één methode van de andere afwijkt, herschrijft iets ertussenin dat oppervlak: een hooking-framework, een spoofer, een resolver-shim. **ER WORDT GEEN INFORMATIE OPGESLAGEN, VERZONDEN OF DOORGEGEVEN AAN ENIG BESTAND OF SERVER** - alles draait op het toestel, identiteitsdragende waarden zijn gemaskeerd tot je ze onthult, en een rapport verlaat het toestel alleen wanneer je het uitdrukkelijk deelt of opslaat; wil je, blokkeer dan internettoegang met een firewall of zet het gewoon uit.

## Wat het inspecteert

Elk item wordt gelezen via elke methode die het kan lezen (Java-SDK / native / shell), naast elkaar vergeleken:

* **~493 systeemeigenschappen** op VIJF manieren gelezen: `SystemProperties.get`, beide toegangspunten van bionic (`__system_property_read_callback` en de 92-byte `__system_property_get`), `getprop` gestart door de JVM, en `getprop` gestart vanuit native code via `popen` - een shell-commando uitgevoerd door `ProcessBuilder` en hetzelfde commando zonder de JVM zijn niet hetzelfde gezichtspunt, want het eerste is een oppervlak dat een root-verbergframework herschrijft en het tweede niet.
* **Toestelidentiteit**: model, fabrikant, merk, device, product, board, hardware, fingerprint, bootloader, build id/tags/type - elk `Build.*`-veld tegen al zijn `ro.product.*`-varianten (system/vendor/odm), native en shell.
* **Identifiers**: serial (veel getters), Android ID (settings en provider), GSF ID, advertising ID, IMEI/primary IMEI/MEID/IMSI/ICCID, user serial.
* **Telefonie**: het TelephonyManager-oppervlak (operator, SIM, netwerk, roaming...), SubscriptionManager (multi-SIM), cell info.
* **Netwerk**: **MAC** per interface (`NetworkInterface` vs `/sys/class/net` vs shell), Wi-Fi (SSID/BSSID/IP/MAC), Bluetooth, DNS.
* **Kernel / proces / systeem**: `uname`, boot id, uptime, UID/PID, SELinux, tijdzone, locale, omgeving, draaiende services/processen.
* **Hardware / media**: CPU, geheugen, sensoren, scherm, camera's, system features, GPU, Widevine-DRM-ID.
* **Pakketten / accounts / WebView**: lijst en aantal geïnstalleerde pakketten, ondertekeningsdigests per app, handtekening van deze app, introspectie van het eigen pakket, intent-resolutie-enumeraties, accounts, aantal oproeplogboek-items, user agent.
* **Sandbox-conformiteit**: oppervlakken die de sandbox moet SLUITEN (eMMC CID/serial, UFS- en SoC-identiteit, `/proc/cmdline`, `/proc/1/*`, `/dev/kmsg`, `/system/build.prop`) en 22 `dumpsys`-services. Hier is de weigering het antwoord: `EACCES`/`DENIED` is de conforme lezing, en een toestel dat de inhoud in plaats daarvan afgeeft, is niet-conform.
* **Advertentie-identifiers**: de GAID via de Play Services-binder, plus de hele sleutelfamilie (AAID, OAID/VAID en Huawei's `pps_*`) doorzocht in de drie settings stores.
* **Root- / hook- / emulatordetectoren**: artefacten van `su`/Magisk/KernelSU/APatch/Xposed/LSPosed/Riru, geïnjecteerde libs in `/proc/self/maps`, bekende pakketten, gevaarlijke en emulator-eigenschappen, verified boot state.

## Downloaden en ondersteuning

Elke release publiceert twee APK's: **SDK_35** richt zich op de strikte moderne sandbox en is degene om te gebruiken; **SDK_27** richt zich op een ouder API-niveau, met een lossere SELinux-domein, handig ter vergelijking. Beide installeren op Android 8.0 en nieuwer.

* Release (APK): https://github.com/VD171/VD-Infos/releases
* https://github.com/VD171/VD-Infos
* https://xdaforums.com/t/VD-Infos.4097379/
* https://t.me/RootDetected
* https://t.me/BlankAssistance

## Talen

21 UI-talen: Engels, Portugees, Spaans, Italiaans, Duits, Frans, Russisch, Indonesisch, Turks, Pools, Nederlands, Zweeds, Tsjechisch, Vietnamees, Chinees, Japans, Koreaans, Perzisch, Hindi, Arabisch en Thais. De app biedt bij de eerste start een eenmalige taalkeuze (met een optie "Systeemstandaard") en volgt anders de systeemtaal.

## Architectuur

```
core/model      onveranderlijk domein: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          NativeBridge (JNI) - SystemProps (reflectie) - PropCatalog (data)
                SemanticProbes - IntegrityProbes - ProbeRegistry
engine/         ProbeEngine - coroutine fan-out, streamt resultaten als Flow
data/           SnapshotStore (opslaan + diff) - Exporter (JSON-/tekstdeling)
ui/             Jetpack Compose, Material 3, dynamische kleur, live voortgang
cpp/            native_probes.cpp - de native lens, zonder afhankelijkheden
```

* **Parallellisme**: 885 probes waaieren uit over de standaard-dispatcher met een begrensd aantal permits; resultaten stromen de UI in zodra ze binnenkomen.
* **Niets op de achtergrond**: geen services en geen geplande scans; de app draait alleen zolang hij open is en sluit zichzelf wanneer hij inactief blijft.
* **Native laag**: één kleine `.so`, op naam gebonden via `RegisterNatives`, bewust piepklein gehouden omdat dit het deel is dat moeilijk te misleiden moet zijn.

## Bijdragen

De meeste bijdragen hebben geen Kotlin nodig: de lijsten staan als platte tekst in `VDInfos/app/src/main/assets/data/`. Voeg een regel toe of verwijder er een en open een pull request.

* `*_apps.txt` - één pakketnaam per regel
* `props.txt` - de catalogus van systeemeigenschappen, `CATEGORIE<tab>sleutel`
* `spoof_keys.txt` - de settings-spoofmatrix, `sleutel:TYPE`

Een `#` begint een opmerking; lege regels worden genegeerd. Codebijdragen zijn ook welkom. Door bij te dragen ga je ermee akkoord dat je werk onder de AGPL-3.0-or-later van dit project valt.

## Catalogus

Referenties voor root-verbergen en -detectie (gidsen, modules, frameworks, detectoren) staan in een aparte catalogus: [CATALOG.md](CATALOG.md).

## Dankbetuigingen

Speciale dank aan **[frknkrc44](https://github.com/frknkrc44)**, ontwikkelaar van **[HMA-OSS](https://github.com/frknkrc44/HMA-OSS)**, voor de zorg die erin is gestoken. HMA-OSS is waar verschillende van de fixes die deze app voorstelt worden toegepast - doel-apps verbergen en spoof-presets instellen - en elke vermelding ervan in de app linkt terug naar de bron.

Zijn links, kanalen en hoe je het steunt: [HMA-OSS.md](HMA-OSS.md).

## Contact

* https://vd171.ru
* https://vd.priv8.ru
* **Telegram:** @VD_Priv8 https://t.me/VD_Priv8
* **Discord:** @VD.Priv8 https://discord.com/users/1296831918989639721
* **E-mail:** vd.priv8@pm.me
* **XDA-Developers:** @VD171 https://xdaforums.com/m/vd171.4699873/
* **GitHub:** @VD171 https://github.com/VD171

## Licentie

**GNU AGPL-3.0-or-later** - zie [LICENSE](LICENSE). Copyleft, inclusief de netwerkclausule: iedereen die een gewijzigde versie draait (zelfs als dienst) moet de broncode ervan aanbieden. Bewust gekozen voor een onderzoeks-/anti-detectietool, om forks open te houden.
