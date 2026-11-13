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
  <b>Debugger di metodi del dispositivo.</b><br>
  Leggi ogni valore del dispositivo con ogni metodo in grado di leggerlo e allineali così da poterli confrontare.
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

Android è un sistema operativo potentissimo e versatile; ciò che nessuno ti dice è che tutti i tuoi dati personali e le informazioni riservate sono a disposizione di ogni applicazione che installi, e proteggerti da queste invasioni della privacy è un obbligo. VD Infos ti mostra un esempio di ciò che può essere catturato dal tuo dispositivo, e lo fa come un *debugger di metodi*: per ogni informazione legge il valore con **ogni metodo in grado di leggerlo**: `Build.*`, `SystemProperties`, `getprop`, il nativo `__system_property_get`, i manager di sistema, i content provider, i file, le syscall e l'attestation della chiave hardware (TEE), e li allinea perché tu possa confrontarli. Quando un metodo è in disaccordo con gli altri, qualcosa nel mezzo sta riscrivendo quella superficie: un framework di hooking, uno spoofer, uno shim del resolver. **NESSUNA INFORMAZIONE VIENE MEMORIZZATA, INVIATA O TRASMESSA AD ALCUN FILE¹ O SERVER**: tutto gira sul dispositivo, i valori che portano identità sono mascherati finché non li riveli, e un report lascia il dispositivo solo quando lo condividi o lo salvi esplicitamente; se vuoi, blocca l'accesso a internet con un firewall o semplicemente spegnilo.

> **¹ Eccezione:** ogni scansione conserva il report completo nella memoria privata dell'app (`files/last_snapshot.json`). Solo l'app stessa o root possono leggerlo, la scansione successiva lo sovrascrive e non lascia mai il dispositivo. Esiste perché gli agenti IA possano leggere il risultato completo; vedi **Per gli agenti IA: il report completo in JSON**.

## Cosa ispeziona

Ogni elemento è letto con ogni metodo in grado di leggerlo (SDK Java / nativo / shell), confrontati fianco a fianco:

* **~488 proprietà di sistema** lette in CINQUE modi: `SystemProperties.get`, entrambi i punti di ingresso di bionic (`__system_property_read_callback` e quello da 92 byte `__system_property_get`), `getprop` avviato dalla JVM e `getprop` avviato da codice nativo tramite `popen`; un comando di shell eseguito da `ProcessBuilder` e lo stesso comando eseguito senza la JVM non sono lo stesso punto di vista, perché il primo è una superficie che un framework di occultamento riscrive e il secondo no.
* **Identità del dispositivo**: modello, produttore, marca, device, product, board, hardware, fingerprint, bootloader, build id/tags/type, ogni campo `Build.*` contro tutte le sue varianti `ro.product.*` (system/vendor/odm), nativo e shell.
* **Identificatori**: serial (vari getter), Android ID (settings e provider), GSF ID, advertising ID, IMEI/primary IMEI/MEID/IMSI/ICCID, user serial.
* **Telefonia**: la superficie di TelephonyManager (operatore, SIM, rete, roaming...), SubscriptionManager (multi-SIM), cell info.
* **Rete**: **MAC** per interfaccia (`NetworkInterface` vs `/sys/class/net` vs shell), Wi-Fi (SSID/BSSID/IP/MAC), Bluetooth, DNS.
* **Kernel / processo / sistema**: `uname`, boot id, uptime, UID/PID, SELinux, fuso orario, locale, ambiente, servizi/processi in esecuzione.
* **Hardware / media**: CPU, memoria, sensori, display, fotocamere, system features, GPU, ID DRM Widevine.
* **Pacchetti / account / WebView**: elenco e conteggio dei pacchetti installati, digest di firma per app, firma di questa app, auto-ispezione del proprio pacchetto, enumerazioni di risoluzione degli intent, account, conteggio del registro chiamate, user agent.
* **Conformità del sandbox**: superfici che il sandbox dovrebbe CHIUDERE (CID/serial dell'eMMC, identità di UFS e SoC, `/proc/cmdline`, `/proc/1/*`, `/dev/kmsg`, `/system/build.prop`) e 22 servizi `dumpsys`. Qui il rifiuto è la risposta: `EACCES`/`DENIED` è la lettura conforme, e un dispositivo che invece consegna il contenuto è fuori conformità.
* **Identificatori pubblicitari**: il GAID dal binder di Play Services, più l'intera famiglia di chiavi (AAID, OAID/VAID e i `pps_*` di Huawei) scandagliata nei tre settings store.
* **Rilevatori di root / hook / emulatore**: artefatti di `su`/Magisk/KernelSU/APatch/Xposed/LSPosed/Riru, lib iniettate in `/proc/self/maps`, pacchetti noti, proprietà pericolose e da emulatore, verified boot state.

## Download e supporto

Ogni release pubblica due APK: **SDK_35** punta al sandbox moderno e rigoroso ed è quello da usare; **SDK_27** punta a un livello API più vecchio, con un dominio SELinux più permissivo, utile per il confronto. Entrambi si installano su Android 8.0 e successivi.

* Release (APK): https://github.com/VD171/VD-Infos/releases
* https://github.com/VD171/VD-Infos
* https://xdaforums.com/t/VD-Infos.4097379/
* https://t.me/RootDetected
* https://t.me/BlankAssistance

## Per gli agenti IA: il report completo in JSON

Pensato per agenti IA e strumenti LLM che lavorano sul dispositivo tramite una shell (assistenti di programmazione, script di analisi). Invece di leggere screenshot, l'agente legge l'intero risultato con un solo comando:

```sh
su -c cat /data/data/ru.vd171.vdinfos/files/last_snapshot.json
```

* **Cosa contiene:** ogni sonda con il suo verdetto e il valore di tutte le lenti, nello stesso JSON del pulsante di esportazione. Lo schermo mostra solo la prima lettura di un elemento compresso; il file le contiene tutte.
* **Quando si aggiorna:** alla fine di ogni scansione, quindi esegui prima una scansione; il file contiene sempre la più recente.
* **Requisito:** root, perché il file si trova nella memoria privata dell'app.

## Architettura

```
core/model      dominio immutabile: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          NativeBridge (JNI), SystemProps (riflessione), PropCatalog (dati)
                SemanticProbes, IntegrityProbes, ProbeRegistry
engine/         ProbeEngine: fan-out con coroutine, trasmette i risultati come Flow
data/           SnapshotStore (persistenza + diff), Exporter (condivisione JSON/testo)
ui/             Jetpack Compose, Material 3, colore dinamico, progresso live
cpp/            native_probes.cpp: la lente nativa, senza dipendenze
```

* **Parallelismo**: 991 sonde si diramano sul dispatcher predefinito con un numero di permessi limitato; i risultati confluiscono nella UI man mano che arrivano.
* **Niente in background**: nessun servizio né scansioni pianificate; l'app gira solo mentre è aperta e si chiude da sola quando resta inattiva.
* **Livello nativo**: un piccolo `.so`, collegato per nome tramite `RegisterNatives`, tenuto deliberatamente minuscolo perché è la parte che deve essere difficile da ingannare.

## Contribuire

La maggior parte dei contributi non richiede Kotlin: gli elenchi sono testo semplice in `VDInfos/app/src/main/assets/data/`. Aggiungi o rimuovi una riga e apri una pull request.

* `*_apps.txt`: un nome di pacchetto per riga
* `props.txt`: il catalogo delle proprietà di sistema, `CATEGORIA<tab>chiave`
* `spoof_keys.txt`: la matrice di spoof delle settings, `chiave:TIPO`
* `known_certs.txt`: il catalogo dei certificati di firma, `SHA-256<tab>proprietario` (hash completo, preso da un APK reale con `apksigner verify --print-certs`)
* altri file `.txt`: anche una voce per riga (frammenti di nome dei moduli del kernel, nomi in `/data/local/tmp`)

Un `#` inizia un commento; le righe vuote sono ignorate. Alcuni file contengono più campi separati da spazio o tab per riga, o marcatori opzionali (`S` e `@field` in `props.txt`); mantieni la forma delle righe già presenti nel file. Anche i contributi di codice sono benvenuti. Contribuendo accetti che il tuo lavoro sia distribuito sotto la AGPL-3.0-or-later di questo progetto.

## Catalogo

I riferimenti su occultamento e rilevamento del root (guide, moduli, framework, rilevatori) stanno in un catalogo dedicato: [CATALOG.md](CATALOG.md).

## Ringraziamenti

Un ringraziamento speciale a **[frknkrc44](https://github.com/frknkrc44)**, sviluppatore di **[HMA-OSS](https://github.com/frknkrc44/HMA-OSS)**, per la cura che vi ha dedicato. HMA-OSS è dove vengono applicate diverse delle correzioni che questa app suggerisce (nascondere le app bersaglio e impostare i preset di spoof), e ogni menzione ad esso nell'app rimanda alla sua fonte.

I suoi link, i canali e come sostenerlo: [HMA-OSS.md](HMA-OSS.md).

## Documentazione

- **[Catalogo](CATALOG.md)**: riferimenti su occultamento del root e rilevamento (guide, moduli, framework e rilevatori).
- **[Changelog](CHANGELOG.md)**: cosa è cambiato in ogni versione.
- **[HMA-OSS](HMA-OSS.md)**: il modulo per nascondere le app su cui si basano le correzioni dell'app (i suoi link, i canali e come sostenerlo).
- **[Contribuire](README.md#contributing)**: come aggiungere obiettivi di rilevamento (per lo più elenchi di testo semplice, senza Kotlin).

<!-- /vd-docs -->

## Vedi anche

Progetti fratelli dello stesso autore:

- **[VDGoogle](https://github.com/VD171/VD-Google)**: i tuoi servizi Google (RCS, Wallet, attestation di Play Integrity), letti sul tuo dispositivo senza internet.
- **[VDBackup](https://github.com/VD171/VD-Backup)**: backup e ripristino completi di app e dati come root, gestiti interamente da una WebUI.
- **[COPG-VD](https://github.com/VD171/COPG-VD)**: uno spoofer di dispositivo che trasforma un fingerprint nuovo in un profilo coerente e consistente.
- **[VD-dropbear](https://github.com/VD171/VD-dropbear)**: un demone SSH autonomo con permessi di root (Dropbear).

Pixel `build.prop`: **[@VictorGabriel7Dev/build.prop](https://github.com/VictorGabriel7Dev/build.prop)** (fork of @T3SL4).

## Contacts

* https://vd171.ru
* https://vd.priv8.ru
* **Telegram:** @VD_Priv8 https://t.me/VD_Priv8
* **Channel:** @Priv8_VD https://t.me/Priv8_VD
* **Discord:** @VD.Priv8 https://discord.com/users/1296831918989639721
* **E-mail:** vd.priv8@pm.me
* **XDA-Developers:** @VD171 https://xdaforums.com/m/vd171.4699873/
* **XDA thread:** https://xdaforums.com/t/VD-Infos.4097379/
* **GitHub:** @VD171 https://github.com/VD171

## Licenza

**GNU AGPL-3.0-or-later**: vedi [LICENSE](LICENSE). Copyleft, inclusa la clausola di rete: chiunque esegua una versione modificata (anche come servizio) deve offrirne il codice sorgente. Scelta deliberatamente per uno strumento di ricerca/anti-rilevamento, per mantenere i fork aperti.

## Marchi

VD Infos è uno strumento di ricerca e diagnostica indipendente e non ufficiale. Non è affiliato, approvato o sponsorizzato da alcuno dei fornitori di cui legge, rileva o nomina software, servizi o marchi (inclusi Google LLC e l'Android Open Source Project). "Android", "Google", "Google Play", "Play Integrity", "Widevine" e tutti gli altri nomi di prodotto, loghi e marchi sono proprietà dei rispettivi titolari. VD Infos legge solo ciò che è già sul tuo dispositivo e non invia nulla da nessuna parte.

## Tags

`android` `privacy` `security` `root-detection` `hook-detection` `spoof-detection` `anti-detection` `tamper-detection` `device-fingerprint` `magisk` `kernelsu` `lsposed` `play-integrity` `attestation` `tee` `keystore` `scanner` `diagnostics` `reverse-engineering` `kotlin`

<!-- doc-footer -->

---

> _Questa documentazione è stata generata con Claude (Anthropic) ed è gestita da VD171. Correzioni e miglioramenti sono benvenuti; vedi [Contributing](README.md#contributing)._
