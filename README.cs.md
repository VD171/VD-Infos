<!--
  VD INFOS - device method debugger: read every device info by every method, then compare.
  Copyright (C) 2026  VD171 - SPDX-License-Identifier: AGPL-3.0-or-later
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
  <b>Debugger metod.</b><br>
  Přečti každou hodnotu zařízení každou metodou, která ji dokáže přečíst, a seřaď je vedle sebe k porovnání.
</p>

<p align="center">
  <a href="https://github.com/VD171/VD-Infos/releases">Releases</a> ·
  <a href="CATALOG.md">Catalog</a> ·
  <a href="#contributing">Contributing</a> ·
  <a href="#for-ai-agents-the-full-report-as-json">AI agents</a> ·
  <a href="CHANGELOG.md">Changelog</a> ·
  <a href="https://xdaforums.com/t/VD-Infos.4097379/">XDA thread</a>
</p>

> **Languages:** [English](README.md) · [العربية](README.ar.md) · [Čeština](README.cs.md) · [Deutsch](README.de.md) · [Español](README.es.md) · [فارسی](README.fa.md) · [Français](README.fr.md) · [हिन्दी](README.hi.md) · [Bahasa Indonesia](README.id.md) · [Italiano](README.it.md) · [日本語](README.ja.md) · [한국어](README.ko.md) · [Nederlands](README.nl.md) · [Polski](README.pl.md) · [Português](README.pt.md) · [Русский](README.ru.md) · [Svenska](README.sv.md) · [ไทย](README.th.md) · [Türkçe](README.tr.md) · [Українська](README.uk.md) · [Tiếng Việt](README.vi.md) · [中文](README.zh.md)

<p align="center">
  <img src="images/vdinfos-01.png" height="420"/>
  <img src="images/vdinfos-02.png" height="420"/>
</p>

---

Android je nesmírně výkonný a všestranný operační systém; co vám nikdo neřekne, je, že všechny vaše osobní údaje a důvěrné informace jsou dostupné každé aplikaci, kterou nainstalujete, a chránit se proti takovému narušování soukromí je povinnost. VD Infos vám ukazuje příklad toho, co lze z vašeho zařízení zachytit, a dělá to jako *debugger metod*: pro každou informaci čte hodnotu **každou metodou, která ji dokáže přečíst** - `Build.*`, `SystemProperties`, `getprop`, nativní `__system_property_get`, systémové managery, content providery, soubory, syscally a hardwarový attestation klíče (TEE) - a seřadí je vedle sebe, abyste mohli porovnat. Když se jedna metoda liší od ostatních, něco mezi tím tu plochu přepisuje: hookovací framework, spoofer, resolver shim. **ŽÁDNÉ INFORMACE SE NEUKLÁDAJÍ, NEODESÍLAJÍ ANI NEPŘENÁŠEJÍ DO ŽÁDNÉHO SOUBORU¹ ČI SERVERU** - vše běží na zařízení, hodnoty nesoucí identitu jsou maskovány, dokud je neodhalíte, a report opustí zařízení jen tehdy, když jej výslovně sdílíte nebo uložíte; chcete-li, zablokujte přístup k internetu firewallem nebo jej prostě vypněte.

> **¹ Výjimka:** každé skenování uloží úplnou zprávu do soukromého úložiště aplikace (`files/last_snapshot.json`). Číst ji může jen samotná aplikace nebo root, další skenování ji přepíše a zařízení nikdy neopustí. Existuje proto, aby AI agenti mohli přečíst úplný výsledek; viz **Pro AI agenty: úplná zpráva v JSON**.

## Co zkoumá

Každá položka se čte každou metodou, která ji dokáže přečíst (Java SDK / nativně / shell), a porovnává vedle sebe:

* **~488 systémových vlastností** čtených PĚTI způsoby: `SystemProperties.get`, oba vstupní body bionicu (`__system_property_read_callback` a 92bajtový `__system_property_get`), `getprop` spuštěný JVM a `getprop` spuštěný z nativního kódu přes `popen` - shellový příkaz spuštěný přes `ProcessBuilder` a tentýž příkaz spuštěný bez JVM nejsou stejný úhel pohledu, protože první je plocha, kterou framework skrývající root přepisuje, a druhý ne.
* **Identita zařízení**: model, výrobce, značka, device, product, board, hardware, fingerprint, bootloader, build id/tags/type - každé pole `Build.*` proti všem jeho variantám `ro.product.*` (system/vendor/odm), nativně a přes shell.
* **Identifikátory**: serial (mnoho getterů), Android ID (settings a provider), GSF ID, advertising ID, IMEI/primary IMEI/MEID/IMSI/ICCID, user serial.
* **Telefonie**: plocha TelephonyManageru (operátor, SIM, síť, roaming...), SubscriptionManager (multi-SIM), cell info.
* **Síť**: **MAC** na rozhraní (`NetworkInterface` vs `/sys/class/net` vs shell), Wi-Fi (SSID/BSSID/IP/MAC), Bluetooth, DNS.
* **Jádro / proces / systém**: `uname`, boot id, uptime, UID/PID, SELinux, časové pásmo, locale, prostředí, běžící služby/procesy.
* **Hardware / média**: CPU, paměť, senzory, displej, fotoaparáty, system features, GPU, Widevine DRM ID.
* **Balíčky / účty / WebView**: seznam a počet nainstalovaných balíčků, digesty podpisů podle aplikací, podpis této aplikace, introspekce vlastního balíčku, výčty rozlišení intentů, účty, počet záznamů v protokolu hovorů, user agent.
* **Shoda se sandboxem**: plochy, které má sandbox ZAVŘÍT (CID/serial eMMC, identita UFS a SoC, `/proc/cmdline`, `/proc/1/*`, `/dev/kmsg`, `/system/build.prop`) a 22 služeb `dumpsys`. Zde je odmítnutí odpovědí: `EACCES`/`DENIED` je odpovídající čtení a zařízení, které místo toho obsah vydá, není ve shodě.
* **Reklamní identifikátory**: GAID z binderu Play Services plus celá rodina klíčů (AAID, OAID/VAID a `pps_*` od Huawei) prohledaná ve třech settings store.
* **Detektory root / hook / emulátoru**: artefakty `su`/Magisk/KernelSU/APatch/Xposed/LSPosed/Riru, injektované knihovny v `/proc/self/maps`, známé balíčky, nebezpečné a emulátorové vlastnosti, verified boot state.

## Stažení a podpora

Každé vydání publikuje dva APK: **SDK_35** cílí na přísný moderní sandbox a je ten, který se má použít; **SDK_27** cílí na starší úroveň API s volnější doménou SELinux, užitečné pro porovnání. Oba se instalují na Android 8.0 a novější.

* Release (APK): https://github.com/VD171/VD-Infos/releases
* https://github.com/VD171/VD-Infos
* https://xdaforums.com/t/VD-Infos.4097379/
* https://t.me/RootDetected
* https://t.me/BlankAssistance

## Pro AI agenty: úplná zpráva v JSON

Určeno pro AI agenty a nástroje LLM, které na zařízení pracují přes shell (programovací asistenti, analytické skripty). Místo čtení snímků obrazovky přečte agent celý výsledek jedním příkazem:

```sh
su -c cat /data/data/ru.vd171.vdinfos/files/last_snapshot.json
```

* **Co obsahuje:** každou sondu s verdiktem a hodnotou všech čoček, ve stejném JSON jako tlačítko exportu. Obrazovka u sbalené položky ukazuje jen první čtení; soubor obsahuje všechna.
* **Kdy se aktualizuje:** na konci každého skenování, takže nejdřív spusťte skenování; soubor vždy obsahuje to nejnovější.
* **Požadavek:** root, protože soubor leží v soukromém úložišti aplikace.

## Architektura

```
core/model      neměnná doména: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          NativeBridge (JNI) - SystemProps (reflexe) - PropCatalog (data)
                SemanticProbes - IntegrityProbes - ProbeRegistry
engine/         ProbeEngine - fan-out na korutinách, streamuje výsledky jako Flow
data/           SnapshotStore (perzistence + diff) - Exporter (sdílení JSON/text)
ui/             Jetpack Compose, Material 3, dynamická barva, živý průběh
cpp/            native_probes.cpp - nativní čočka, bez závislostí
```

* **Paralelismus**: 991 sond se rozvětví na výchozím dispatcheru s omezeným počtem povolení; výsledky proudí do UI, jakmile dorazí.
* **Nic na pozadí**: žádné služby ani plánované skeny; aplikace běží jen když je otevřená a sama se zavře, když zůstane nečinná.
* **Nativní vrstva**: jedna malá `.so`, vázaná podle jména přes `RegisterNatives`, záměrně nepatrná, protože je to část, kterou musí být těžké oklamat.

## Přispívání

Většina příspěvků nevyžaduje Kotlin: seznamy jsou prostý text v `VDInfos/app/src/main/assets/data/`. Přidejte nebo odeberte jeden řádek a otevřete pull request.

* `*_apps.txt` - jedno jméno balíčku na řádek
* `props.txt` - katalog systémových vlastností, `KATEGORIE<tab>klíč`
* `spoof_keys.txt` - matice spoof nastavení, `klíč:TYP`
* `known_certs.txt` - katalog podpisových certifikátů, `SHA-256<tab>vlastník` (celý hash, ze skutečného APK přes `apksigner verify --print-certs`)
* ostatní soubory `.txt` - také jeden záznam na řádek (fragmenty názvů modulů jádra, názvy v `/data/local/tmp`)

`#` začíná komentář; prázdné řádky se ignorují. Některé soubory obsahují na řádku několik polí oddělených mezerou nebo tabulátorem, případně volitelné značky (`S` a `@field` v `props.txt`); zachovejte podobu řádků, které už v souboru jsou. Příspěvky v kódu jsou také vítány. Přispěním souhlasíte, že vaše práce bude šířena pod AGPL-3.0-or-later tohoto projektu.

## Katalog

Reference ke skrývání a detekci rootu (návody, moduly, frameworky, detektory) jsou v samostatném katalogu: [CATALOG.md](CATALOG.md).

## Poděkování

Zvláštní poděkování patří **[frknkrc44](https://github.com/frknkrc44)**, vývojáři **[HMA-OSS](https://github.com/frknkrc44/HMA-OSS)**, za péči do něj vloženou. HMA-OSS je místo, kde se aplikuje několik oprav, které tato aplikace navrhuje - skrytí cílových aplikací a nastavení presetů spoof - a každá zmínka o něm v aplikaci odkazuje zpět na jeho zdroj.

Jeho odkazy, kanály a jak jej podpořit: [HMA-OSS.md](HMA-OSS.md).

## Dokumentace

- **[Katalog](CATALOG.md)**: reference ke skrývání rootu a detekci - průvodci, moduly, frameworky a detektory.
- **[Seznam změn](CHANGELOG.md)**: co se změnilo v každém vydání.
- **[HMA-OSS](HMA-OSS.md)**: modul pro skrývání aplikací, na kterém stojí opravy této aplikace - jeho odkazy, kanály a jak jej podpořit.
- **[Přispívání](README.md#contributing)**: jak přidat cíle detekce (většinou prosté textové seznamy, bez potřeby Kotlinu).

<!-- /vd-docs -->

## Viz také

Sourozenecké projekty téhož autora:

- **[VDGoogle](https://github.com/VD171/VD-Google)**: vaše služby Google (RCS, Wallet, atestace Play Integrity), čtené na vašem zařízení bez internetu.
- **[VDBackup](https://github.com/VD171/VD-Backup)**: úplné zálohování a obnova aplikací a dat jako root, řízené celé z WebUI.
- **[COPG-VD](https://github.com/VD171/COPG-VD)**: spoofer zařízení, který z čerstvého fingerprintu udělá koherentní, konzistentní profil.

Pixel `build.prop`: **[@VictorGabriel7Dev/build.prop](https://github.com/VictorGabriel7Dev/build.prop)** (fork od @T3SL4).

## Kontakty

* https://vd171.ru
* https://vd.priv8.ru
* **Telegram:** @VD_Priv8 https://t.me/VD_Priv8
* **Channel:** @Priv8_VD https://t.me/Priv8_VD
* **Discord:** @VD.Priv8 https://discord.com/users/1296831918989639721
* **E-mail:** vd.priv8@pm.me
* **XDA-Developers:** @VD171 https://xdaforums.com/m/vd171.4699873/
* **XDA thread:** https://xdaforums.com/t/VD-Infos.4097379/
* **GitHub:** @VD171 https://github.com/VD171

## Licence

**GNU AGPL-3.0-or-later** - viz [LICENSE](LICENSE). Copyleft včetně síťové klauzule: kdokoli spustí upravenou verzi (i jako službu), musí nabídnout její zdrojový kód. Zvoleno záměrně pro výzkumný/anti-detekční nástroj, aby forky zůstaly otevřené.

## Ochranné známky

VD Infos je nezávislý, neoficiální výzkumný a diagnostický nástroj. Není spojen s žádným z dodavatelů, jejichž software, služby nebo značky čte, detekuje nebo jmenuje, ani jimi není podporován či sponzorován (včetně Google LLC a Android Open Source Project). "Android", "Google", "Google Play", "Play Integrity", "Widevine" a všechny ostatní názvy produktů, loga a ochranné známky jsou majetkem příslušných vlastníků. VD Infos čte pouze to, co už na vašem zařízení je, a nikam nic neodesílá.

## Tags

`android` `privacy` `security` `root-detection` `hook-detection` `spoof-detection` `anti-detection` `tamper-detection` `device-fingerprint` `magisk` `kernelsu` `lsposed` `play-integrity` `attestation` `tee` `keystore` `scanner` `diagnostics` `reverse-engineering` `kotlin`

<!-- doc-footer -->

---

> _Tato dokumentace byla vytvořena pomocí Claude (Anthropic) a spravuje ji VD171. Opravy a vylepšení jsou vítány; viz [Contributing](README.md#contributing)._
