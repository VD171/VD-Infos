[English](README.md) | [Português](README.pt.md) | [Español](README.es.md) | [Italiano](README.it.md) | [Deutsch](README.de.md) | [Français](README.fr.md) | [Русский](README.ru.md) | [Bahasa Indonesia](README.id.md) | [Türkçe](README.tr.md) | [Polski](README.pl.md) | [Nederlands](README.nl.md) | **Svenska** | [Čeština](README.cs.md) | [Tiếng Việt](README.vi.md) | [中文](README.zh.md) | [日本語](README.ja.md) | [한국어](README.ko.md) | [فارسی](README.fa.md) | [हिन्दी](README.hi.md) | [العربية](README.ar.md) | [ไทย](README.th.md)

# VD Infos

*Metod-debugger*

[![License: AGPL-3.0-or-later](https://img.shields.io/badge/license-AGPL--3.0--or--later-blue.svg)](LICENSE)
[![Release](https://img.shields.io/github/v/release/VD171/VD-Infos)](https://github.com/VD171/VD-Infos/releases)
[![Downloads](https://img.shields.io/github/downloads/VD171/VD-Infos/total)](https://github.com/VD171/VD-Infos/releases)
[![Android 8.0+](https://img.shields.io/badge/Android-8.0%2B-3DDC84.svg)](https://github.com/VD171/VD-Infos/releases)
[![Languages](https://img.shields.io/badge/languages-21-orange.svg)](README.md)

<img src="images/vdinfos-01.png" height="420"/> <img src="images/vdinfos-02.png" height="420"/>

Android är ett oerhört kraftfullt och mångsidigt operativsystem; vad ingen berättar är att alla dina personliga uppgifter och känsliga uppgifter är tillgängliga för varje app du installerar, och att skydda dig mot dessa integritetsintrång är en skyldighet. VD Infos visar dig ett exempel på vad som kan fångas från din enhet, och gör det som en *metod-debugger*: för varje uppgift läser den värdet via **varje metod som kan läsa det** - `Build.*`, `SystemProperties`, `getprop`, den nativa `__system_property_get`, systemhanterare, content providers, filer, syscalls och attestering av hårdvarunyckel (TEE) - och radar upp dem så att du kan jämföra. När en metod avviker från de andra skriver något däremellan om den ytan: ett hooking-ramverk, en spoofer, en resolver-shim. **INGEN INFORMATION LAGRAS, SKICKAS ELLER ÖVERFÖRS TILL NÅGON FIL ELLER SERVER** - allt körs på enheten, identitetsbärande värden är maskerade tills du avslöjar dem, och en rapport lämnar enheten endast när du uttryckligen delar eller sparar den; vill du, blockera internetåtkomst med en brandvägg eller stäng bara av den.

## Vad den granskar

Varje post läses via varje metod som kan läsa den (Java-SDK / nativ / shell), jämförda sida vid sida:

* **~488 systemegenskaper** lästa på FEM sätt: `SystemProperties.get`, båda bionics ingångspunkter (`__system_property_read_callback` och den 92-byte stora `__system_property_get`), `getprop` startad av JVM:en och `getprop` startad från nativ kod via `popen` - ett shell-kommando kört av `ProcessBuilder` och samma kommando kört utan JVM är inte samma synvinkel, eftersom det första är en yta som ett root-döljande ramverk skriver om och det andra inte.
* **Enhetsidentitet**: modell, tillverkare, märke, device, product, board, hardware, fingerprint, bootloader, build id/tags/type - varje `Build.*`-fält mot alla dess `ro.product.*`-varianter (system/vendor/odm), nativt och shell.
* **Identifierare**: serial (många getters), Android ID (settings och provider), GSF ID, advertising ID, IMEI/primary IMEI/MEID/IMSI/ICCID, user serial.
* **Telefoni**: TelephonyManager-ytan (operatör, SIM, nätverk, roaming...), SubscriptionManager (multi-SIM), cell info.
* **Nätverk**: **MAC** per gränssnitt (`NetworkInterface` vs `/sys/class/net` vs shell), Wi-Fi (SSID/BSSID/IP/MAC), Bluetooth, DNS.
* **Kärna / process / system**: `uname`, boot id, uptime, UID/PID, SELinux, tidszon, locale, miljö, körande tjänster/processer.
* **Hårdvara / media**: CPU, minne, sensorer, skärm, kameror, system features, GPU, Widevine-DRM-ID.
* **Paket / konton / WebView**: lista och antal installerade paket, signeringsdigester per app, denna apps signatur, introspektion av det egna paketet, intent-upplösningsuppräkningar, konton, antal samtalsloggposter, user agent.
* **Sandbox-överensstämmelse**: ytor som sandboxen ska STÄNGA (eMMC CID/serial, UFS- och SoC-identitet, `/proc/cmdline`, `/proc/1/*`, `/dev/kmsg`, `/system/build.prop`) och 22 `dumpsys`-tjänster. Här är vägran svaret: `EACCES`/`DENIED` är den överensstämmande läsningen, och en enhet som istället lämnar ut innehållet är inte överensstämmande.
* **Annonsidentifierare**: GAID från Play Services-bindern, plus hela nyckelfamiljen (AAID, OAID/VAID och Huaweis `pps_*`) svept över de tre settings store.
* **Root- / hook- / emulatordetektorer**: artefakter av `su`/Magisk/KernelSU/APatch/Xposed/LSPosed/Riru, injicerade libbar i `/proc/self/maps`, kända paket, farliga och emulatoregenskaper, verified boot state.

## Nedladdning och support

Varje release publicerar två APK-filer: **SDK_35** riktar sig mot den strikta moderna sandlådan och är den att använda; **SDK_27** riktar sig mot en äldre API-nivå, med en lösare SELinux-domän, användbar för jämförelse. Båda installeras på Android 8.0 och senare.

* Release (APK): https://github.com/VD171/VD-Infos/releases
* https://github.com/VD171/VD-Infos
* https://xdaforums.com/t/VD-Infos.4097379/
* https://t.me/RootDetected
* https://t.me/BlankAssistance

## Språk

21 gränssnittsspråk: engelska, portugisiska, spanska, italienska, tyska, franska, ryska, indonesiska, turkiska, polska, nederländska, svenska, tjeckiska, vietnamesiska, kinesiska, japanska, koreanska, persiska, hindi, arabiska och thailändska. Appen erbjuder ett engångsval av språk vid första starten (med alternativet "Systemstandard") och följer annars systemspråket.

## Arkitektur

```
core/model      oföränderlig domän: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          NativeBridge (JNI) - SystemProps (reflektion) - PropCatalog (data)
                SemanticProbes - IntegrityProbes - ProbeRegistry
engine/         ProbeEngine - coroutine-fan-out, strömmar resultat som en Flow
data/           SnapshotStore (beständighet + diff) - Exporter (JSON-/textdelning)
ui/             Jetpack Compose, Material 3, dynamisk färg, live-förlopp
cpp/            native_probes.cpp - den nativa linsen, beroendefri
```

* **Parallellism**: 885 sonder fördelas över standard-dispatchern med ett begränsat antal tillstånd; resultaten strömmar in i UI:t allteftersom de landar.
* **Inget i bakgrunden**: inga tjänster och inga schemalagda skanningar; appen körs bara medan den är öppen och stänger sig själv när den lämnas inaktiv.
* **Nativt lager**: en liten `.so`, bunden vid namn via `RegisterNatives`, avsiktligt pytteliten eftersom det är den del som måste vara svår att lura.

## Bidra

De flesta bidrag kräver ingen Kotlin: listorna ligger som ren text under `VDInfos/app/src/main/assets/data/`. Lägg till eller ta bort en rad och öppna en pull request.

* `*_apps.txt` - ett paketnamn per rad
* `props.txt` - katalogen över systemegenskaper, `KATEGORI<tab>nyckel`
* `spoof_keys.txt` - spoof-matrisen för settings, `nyckel:TYP`
* övriga `.txt`-filer - även en post per rad (namnfragment för kärnmoduler, namn i `/data/local/tmp`)

Ett `#` inleder en kommentar; tomma rader ignoreras. Kodbidrag är också välkomna. Genom att bidra godtar du att ditt arbete sprids under projektets AGPL-3.0-or-later.

## Katalog

Referenser för root-döljande och -detektering (guider, moduler, ramverk, detektorer) finns i en egen katalog: [CATALOG.md](CATALOG.md).

## Tack

Ett särskilt tack till **[frknkrc44](https://github.com/frknkrc44)**, utvecklaren av **[HMA-OSS](https://github.com/frknkrc44/HMA-OSS)**, för omsorgen som lagts ned. HMA-OSS är där flera av de fixar som den här appen föreslår tillämpas - dölja målappar och sätta spoof-förinställningar - och varje omnämnande av det i appen länkar tillbaka till källan.

Dess länkar, kanaler och hur du stöder det: [HMA-OSS.md](HMA-OSS.md).

## Kontakt

* https://vd171.ru
* https://vd.priv8.ru
* **Telegram:** @VD_Priv8 https://t.me/VD_Priv8
* **Discord:** @VD.Priv8 https://discord.com/users/1296831918989639721
* **E-post:** vd.priv8@pm.me
* **XDA-Developers:** @VD171 https://xdaforums.com/m/vd171.4699873/
* **GitHub:** @VD171 https://github.com/VD171

## Licens

**GNU AGPL-3.0-or-later** - se [LICENSE](LICENSE). Copyleft, inklusive nätverksklausulen: den som kör en modifierad version (även som tjänst) måste erbjuda dess källkod. Vald medvetet för ett forsknings-/anti-detektionsverktyg, för att hålla forkar öppna.

---

`android` `privacy` `security` `root-detection` `hook-detection` `spoof-detection` `anti-detection` `tamper-detection` `device-fingerprint` `magisk` `kernelsu` `lsposed` `play-integrity` `attestation` `tee` `keystore` `scanner` `diagnostics` `reverse-engineering` `kotlin`
