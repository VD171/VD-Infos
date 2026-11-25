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
  <b>Enhetens metod-debugger.</b><br>
  Läs varje enhetsvärde via varje metod som kan läsa det, och rada upp dem så att du kan jämföra.
</p>

<p align="center">
  <a href="https://github.com/VD171/VD-Infos/releases">Releases</a> ·
  <a href="https://github.com/VD171/VD-Infos/wiki">Wiki</a> ·
  <a href="https://github.com/VD171/VD-Infos/wiki/USER-GUIDE">Användarguide</a> ·
  <a href="https://github.com/VD171/VD-Infos/wiki/AI-AGENTS">AI-agenter</a> ·
  <a href="https://github.com/VD171/VD-Infos/wiki/EXPORT-FORMAT">Exportformat</a> ·
  <a href="https://github.com/VD171/VD-Infos/wiki/ARCHITECTURE">Arkitektur</a> ·
  <a href="CATALOG.md">Katalog</a> ·
  <a href="CHANGELOG.md">Changelog</a> ·
  <a href="https://xdaforums.com/t/VD-Infos.4097379/">XDA thread</a>
</p>

> **Languages:** [English](README.md) · [العربية](README.ar.md) · [Čeština](README.cs.md) · [Deutsch](README.de.md) · [Español](README.es.md) · [فارسی](README.fa.md) · [Français](README.fr.md) · [हिन्दी](README.hi.md) · [Bahasa Indonesia](README.id.md) · [Italiano](README.it.md) · [日本語](README.ja.md) · [한국어](README.ko.md) · [Nederlands](README.nl.md) · [Polski](README.pl.md) · [Português](README.pt.md) · [Русский](README.ru.md) · [Svenska](README.sv.md) · [ไทย](README.th.md) · [Türkçe](README.tr.md) · [Українська](README.uk.md) · [Tiếng Việt](README.vi.md) · [中文](README.zh.md)

<p align="center">
  <img src="images/vdinfos-01.png" height="420"/>
  <img src="images/vdinfos-02.png" height="420"/>
  <img src="images/vdinfos-03.png" height="420"/>
</p>

---

Android är ett oerhört kraftfullt och mångsidigt operativsystem; vad ingen berättar är att alla dina personliga uppgifter och känsliga uppgifter är tillgängliga för varje app du installerar, och att skydda dig mot dessa integritetsintrång är en skyldighet. VD Infos visar dig ett exempel på vad som kan fångas från din enhet, och gör det som en *metod-debugger*: för varje uppgift läser den värdet via **varje metod som kan läsa det**: ramverks-API:er, nativa gränssnitt, shell-kommandon, systemhanterare, content providers, filer, syscalls och attestering av hårdvarunyckel (TEE), och radar upp dem så att du kan jämföra. När en avläsning avviker från de andra skriver något däremellan om den ytan: ett hooking-ramverk, en spoofer, en resolver-shim. **INGEN INFORMATION LAGRAS, SKICKAS ELLER ÖVERFÖRS TILL NÅGON FIL¹ ELLER SERVER**: allt körs på enheten, identitetsbärande värden är maskerade tills du avslöjar dem, och en rapport lämnar enheten endast när du uttryckligen delar eller sparar den; vill du, blockera internetåtkomst med en brandvägg eller stäng bara av den.

> **¹ Undantag:** varje skanning sparar hela rapporten i appens privata lagring (`files/last_snapshot.json`). Bara appen själv eller root kan läsa den, nästa skanning skriver över den och den lämnar aldrig enheten. Den finns för att AI-agenter ska kunna läsa hela resultatet; se **För AI-agenter: hela rapporten som JSON**.

## Vad den granskar

Varje post läses via varje metod som kan läsa den (Java-SDK / nativ / shell), jämförda sida vid sida:

* **~488 systemegenskaper** lästa via flera oberoende vägar: Java-API:et, det nativa egenskapsgränssnittet och shell-kommandot `getprop` nått på olika sätt, sedan uppradade och jämförda. Eftersom dessa vägar inte alla går genom samma lager visar sig ett värde som skrivits om på en väg men inte en annan som en avvikelse.
* **Enhetsidentitet**: modell, tillverkare, märke, device, product, board, hardware, fingerprint, bootloader, build id/tags/type, varje `Build.*`-fält mot alla dess `ro.product.*`-varianter (system/vendor/odm), nativt och shell.
* **Identifierare**: serial (många getters), Android ID (settings och provider), GSF ID, advertising ID, IMEI/primary IMEI/MEID/IMSI/ICCID, user serial.
* **Telefoni**: TelephonyManager-ytan (operatör, SIM, nätverk, roaming...), SubscriptionManager (multi-SIM), cell info.
* **Nätverk**: **MAC** per gränssnitt (`NetworkInterface` vs `/sys/class/net` vs shell), Wi-Fi (SSID/BSSID/IP/MAC), Bluetooth, DNS.
* **Kärna / process / system**: `uname`, boot id, uptime, UID/PID, SELinux, tidszon, locale, miljö, körande tjänster/processer.
* **Hårdvara / media**: CPU, minne, sensorer, skärm, kameror, system features, GPU, Widevine-DRM-ID.
* **Paket / konton / WebView**: lista och antal installerade paket, signeringsdigester per app, denna apps signatur, introspektion av det egna paketet, intent-upplösningsuppräkningar, konton, antal samtalsloggposter, user agent.
* **Sandbox-överensstämmelse**: lågnivåidentitet och kärnytor, plus en uppsättning `dumpsys`-tjänster, som en korrekt sandboxad app inte ska kunna nå. För dessa kontrollerar appen om åtkomst nekas korrekt; en yta som går att nå när den inte borde flaggas för granskning.
* **Annonsidentifierare**: GAID från Play Services-bindern, plus hela nyckelfamiljen (AAID, OAID/VAID och Huaweis `pps_*`) svept över de tre settings store.
* **Root- / hook- / emulatordetektorer**: artefakter och signaler som lämnats av vanliga root-hanterare, hooking-ramverk och emulatorer (Magisk, KernelSU, APatch, Xposed/LSPosed, Riru med flera), plus verified boot state.

## Nedladdning och support

Varje release publicerar två APK-filer: **SDK_35** riktar sig mot den strikta moderna sandlådan och är den att använda; **SDK_27** riktar sig mot en äldre API-nivå, användbar för jämförelse. Båda installeras på Android 8.0 och senare.

* Release (APK): https://github.com/VD171/VD-Infos/releases
* https://github.com/VD171/VD-Infos
* https://xdaforums.com/t/VD-Infos.4097379/
* https://t.me/RootDetected
* https://t.me/BlankAssistance

## För AI-agenter: hela rapporten som JSON

Gjord för AI-agenter och LLM-verktyg som arbetar på enheten via ett skal (kodassistenter, analysskript). I stället för att läsa skärmbilder läser agenten hela resultatet med ett kommando:

```sh
su -c cat /data/data/ru.vd171.vdinfos/files/last_snapshot.json
```

* **Innehåll:** varje sond med sitt utlåtande och värdet från alla linser, i samma JSON som exportknappen. Skärmen visar bara den första avläsningen för ett hopfällt objekt; filen har alla.
* **När den uppdateras:** i slutet av varje skanning, så kör en skanning först; filen innehåller alltid den senaste.
* **Krav:** root, eftersom filen ligger i appens privata lagring.

## Arkitektur

```
core/model      oföränderlig domän: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          NativeBridge (JNI), SystemProps (reflektion), PropCatalog (data)
                SemanticProbes, IntegrityProbes, ProbeRegistry
engine/         ProbeEngine: coroutine-fan-out, strömmar resultat som en Flow
data/           SnapshotStore (beständighet + diff), Exporter (JSON-/textdelning)
ui/             Jetpack Compose, Material 3, dynamisk färg, live-förlopp
cpp/            native_probes.cpp: den nativa linsen, beroendefri
```

* **Parallellism**: Sonderna fördelas över standard-dispatchern med ett begränsat antal tillstånd; resultaten strömmar in i UI:t allteftersom de landar.
* **Inget i bakgrunden**: inga tjänster och inga schemalagda skanningar; appen körs bara medan den är öppen och stänger sig själv när den lämnas inaktiv.
* **Nativt lager**: en liten, beroendefri `.so`, bunden vid namn via `RegisterNatives`, avsiktligt pytteliten och enkel.

## Bidra

De flesta bidrag kräver ingen Kotlin: listorna ligger som ren text under `VDInfos/app/src/main/assets/data/`. Lägg till eller ta bort en rad och öppna en pull request.

* `*_apps.txt`: ett paketnamn per rad
* `props.txt`: katalogen över systemegenskaper, `KATEGORI<tab>nyckel`
* `spoof_keys.txt`: spoof-matrisen för settings, `nyckel:TYP`
* `known_certs.txt`: katalogen över signeringscertifikat, `SHA-256<tab>ägare` (hel hash, från en riktig APK med `apksigner verify --print-certs`)
* övriga `.txt`-filer: även en post per rad (namnfragment för kärnmoduler, namn i `/data/local/tmp`)

Ett `#` inleder en kommentar; tomma rader ignoreras. Vissa filer har flera fält per rad åtskilda med mellanslag eller tabb, eller valfria markörer (`S` och `@field` i `props.txt`); behåll formen på raderna som redan finns i filen. Kodbidrag är också välkomna. Genom att bidra godtar du att ditt arbete sprids under projektets AGPL-3.0-or-later.

## Katalog

Referenser för root-döljande och -detektering (guider, moduler, ramverk, detektorer) finns i en egen katalog: [CATALOG.md](CATALOG.md).

## Tack

Ett särskilt tack till **[frknkrc44](https://github.com/frknkrc44)**, utvecklaren av **[HMA-OSS](https://github.com/frknkrc44/HMA-OSS)**, för omsorgen som lagts ned. HMA-OSS är där flera av de fixar som den här appen föreslår tillämpas (dölja målappar och sätta spoof-förinställningar), och varje omnämnande av det i appen länkar tillbaka till källan.

Dess länkar, kanaler och hur du stöder det: [HMA-OSS.md](HMA-OSS.md).

## Dokumentation

- **[Wiki](https://github.com/VD171/VD-Infos/wiki)**: all dokumentation, läsbar online på 22 språk.
- **[Användarguide](https://github.com/VD171/VD-Infos/wiki/USER-GUIDE)**: installera, köra en skanning, läsa resultatet och dela en rapport.
- **[För AI-agenter](https://github.com/VD171/VD-Infos/wiki/AI-AGENTS)** och **[Exportformat](https://github.com/VD171/VD-Infos/wiki/EXPORT-FORMAT)**: den maskinläsbara rapporten och dess schema.
- **[Arkitektur](https://github.com/VD171/VD-Infos/wiki/ARCHITECTURE)** och **[Bygga](https://github.com/VD171/VD-Infos/wiki/BUILDING)**: hur den fungerar och hur du bygger den.
- **[Integritet](https://github.com/VD171/VD-Infos/wiki/PRIVACY)**, **[Säkerhet](https://github.com/VD171/VD-Infos/wiki/SECURITY)** och **[Uppförandekod](https://github.com/VD171/VD-Infos/wiki/CODE_OF_CONDUCT)**: integritetsförklaringen och styrningsdokumenten.
- **[Katalog](CATALOG.md)**: referenser för att dölja root och för detektering (guider, moduler, ramverk och detektorer).
- **[Ändringslogg](CHANGELOG.md)**: vad som ändrats i varje version.
- **[HMA-OSS](https://github.com/VD171/VD-Infos/wiki/HMA-OSS)**: appdöljningsmodulen som appens fixar bygger på (dess länkar, kanaler och hur du stöder den).
- **[Bidra](https://github.com/VD171/VD-Infos/wiki/CONTRIBUTING)**: hur du lägger till detekteringsmål (mest vanliga textlistor, ingen Kotlin behövs).

<!-- /vd-docs -->

## Se även

- **[COPG-VD](https://github.com/VD171/COPG-VD)**: spoofing av enhetens globala fingerprint / Play Integrity, för hela enheten med omfattning per app och ett WebUI.
- **[VDBackup](https://github.com/VD171/VD-Backup)**: fullständig säkerhetskopiering och återställning av appar och data som root, helt styrd från ett WebUI.
- **[VD-dropbear](https://github.com/VD171/VD-dropbear)**: en fristående rootad SSH-daemon (Dropbear).
- **[VDGoogle](https://github.com/VD171/VD-Google)**: dina Google-tjänster (RCS, Wallet, Play Integrity-attesteringar), lästa på din enhet utan internet.
- **[VDInstaller](https://github.com/VD171/VD-Installer)**: en friktionsfri root-sideload-installerare för enskilda, batch- och split-APK:er, med en växel för Play Protect-verifieraren och förfalskning av installationskällan.
- **[VDLiveUpdate](https://github.com/VD171/VDLiveUpdate)**: en flashfri, inkrementell självuppdateringsmotor, inbäddad i KernelSU/APatch/Magisk-moduler.
- **[VD-Modules](https://github.com/VD171/VD-Modules)**: vårt modulrepositorium för MMRL/WebUI-X (installera allt detta från din root-hanterare).
- **[@VictorGabriel7Dev/build.prop](https://github.com/VictorGabriel7Dev/build.prop)**: en datamängd med Pixel-`build.prop`-fingerprints (fork av @T3SL4).
- **[@VictorGabriel7Dev/mtproto-store](https://github.com/VictorGabriel7Dev/mtproto-store)**: använd Telegram som ett objektlager från ett litet Go-CLI.

## Kontakt

- https://vd171.ru
- https://vd.priv8.ru
- **Telegram:** @VD_Priv8 https://t.me/VD_Priv8
- **Channel:** @Priv8_VD https://t.me/Priv8_VD
- **Discord:** @VD.Priv8 https://discord.com/users/1296831918989639721
- **E-mail:** vd.priv8@pm.me
- **XDA-Developers:** @VD171 https://xdaforums.com/m/vd171.4699873/
- **XDA thread:** https://xdaforums.com/t/VD-Infos.4097379/
- **GitHub:** @VD171 https://github.com/VD171

## Licens

**GNU AGPL-3.0-or-later**: se [LICENSE](LICENSE). Copyleft, inklusive nätverksklausulen: den som kör en modifierad version (även som tjänst) måste erbjuda dess källkod. Vald medvetet för ett forsknings-/anti-detektionsverktyg, för att hålla forkar öppna.

## Varumärken

VD Infos är ett oberoende, inofficiellt forsknings- och diagnostikverktyg. Det är inte anslutet till, stött av eller sponsrat av någon av de leverantörer vars programvara, tjänster eller märken det läser, upptäcker eller nämner (inklusive Google LLC och Android Open Source Project). "Android", "Google", "Google Play", "Play Integrity", "Widevine" och alla andra produktnamn, logotyper och varumärken tillhör sina respektive ägare. VD Infos läser bara det som redan finns på din enhet och skickar ingenting någonstans.

## Tags

`android` `privacy` `security` `root-detection` `hook-detection` `spoof-detection` `anti-detection` `tamper-detection` `device-fingerprint` `magisk` `kernelsu` `lsposed` `play-integrity` `attestation` `tee` `keystore` `scanner` `diagnostics` `reverse-engineering` `kotlin`
