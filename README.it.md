[English](README.md) | [Português](README.pt.md) | [Español](README.es.md) | **Italiano** | [Deutsch](README.de.md) | [Français](README.fr.md) | [Русский](README.ru.md) | [Bahasa Indonesia](README.id.md) | [Türkçe](README.tr.md) | [Polski](README.pl.md) | [Nederlands](README.nl.md) | [Svenska](README.sv.md) | [Čeština](README.cs.md) | [Tiếng Việt](README.vi.md) | [中文](README.zh.md) | [日本語](README.ja.md) | [한국어](README.ko.md) | [فارسی](README.fa.md) | [हिन्दी](README.hi.md) | [العربية](README.ar.md) | [ไทย](README.th.md)

# VD Infos

*Debugger di metodi*

[![License: AGPL-3.0-or-later](https://img.shields.io/badge/license-AGPL--3.0--or--later-blue.svg)](LICENSE)
[![Release](https://img.shields.io/github/v/release/VD171/VD-Infos)](https://github.com/VD171/VD-Infos/releases)
[![Downloads](https://img.shields.io/github/downloads/VD171/VD-Infos/total)](https://github.com/VD171/VD-Infos/releases)
[![Android 8.0+](https://img.shields.io/badge/Android-8.0%2B-3DDC84.svg)](https://github.com/VD171/VD-Infos/releases)
[![Languages](https://img.shields.io/badge/languages-21-orange.svg)](README.md)

<img src="images/vdinfos-01.png" height="420"/> <img src="images/vdinfos-02.png" height="420"/>

Android è un sistema operativo potentissimo e versatile; ciò che nessuno ti dice è che tutti i tuoi dati personali e le informazioni riservate sono a disposizione di ogni applicazione che installi, e proteggerti da queste invasioni della privacy è un obbligo. VD Infos ti mostra un esempio di ciò che può essere catturato dal tuo dispositivo, e lo fa come un *debugger di metodi*: per ogni informazione legge il valore con **ogni metodo in grado di leggerlo** - `Build.*`, `SystemProperties`, `getprop`, il nativo `__system_property_get`, i manager di sistema, i content provider, i file, le syscall e l'attestation della chiave hardware (TEE) - e li allinea perché tu possa confrontarli. Quando un metodo è in disaccordo con gli altri, qualcosa nel mezzo sta riscrivendo quella superficie: un framework di hooking, uno spoofer, uno shim del resolver. **NESSUNA INFORMAZIONE VIENE MEMORIZZATA, INVIATA O TRASMESSA AD ALCUN FILE O SERVER** - tutto gira sul dispositivo, i valori che portano identità sono mascherati finché non li riveli, e un report lascia il dispositivo solo quando lo condividi o lo salvi esplicitamente; se vuoi, blocca l'accesso a internet con un firewall o semplicemente spegnilo.

## Cosa ispeziona

Ogni elemento è letto con ogni metodo in grado di leggerlo (SDK Java / nativo / shell), confrontati fianco a fianco:

* **~493 proprietà di sistema** lette in CINQUE modi: `SystemProperties.get`, entrambi i punti di ingresso di bionic (`__system_property_read_callback` e quello da 92 byte `__system_property_get`), `getprop` avviato dalla JVM e `getprop` avviato da codice nativo tramite `popen` - un comando di shell eseguito da `ProcessBuilder` e lo stesso comando eseguito senza la JVM non sono lo stesso punto di vista, perché il primo è una superficie che un framework di occultamento riscrive e il secondo no.
* **Identità del dispositivo**: modello, produttore, marca, device, product, board, hardware, fingerprint, bootloader, build id/tags/type - ogni campo `Build.*` contro tutte le sue varianti `ro.product.*` (system/vendor/odm), nativo e shell.
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

## Lingue

21 lingue dell'interfaccia: Inglese, Portoghese, Spagnolo, Italiano, Tedesco, Francese, Russo, Indonesiano, Turco, Polacco, Olandese, Svedese, Ceco, Vietnamita, Cinese, Giapponese, Coreano, Persiano, Hindi, Arabo e Thailandese. L'app offre una scelta della lingua una tantum al primo avvio (con l'opzione "Predefinita di sistema") e, altrimenti, segue la lingua del sistema.

## Architettura

```
core/model      dominio immutabile: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          NativeBridge (JNI) - SystemProps (riflessione) - PropCatalog (dati)
                SemanticProbes - IntegrityProbes - ProbeRegistry
engine/         ProbeEngine - fan-out con coroutine, trasmette i risultati come Flow
data/           SnapshotStore (persistenza + diff) - Exporter (condivisione JSON/testo)
ui/             Jetpack Compose, Material 3, colore dinamico, progresso live
cpp/            native_probes.cpp - la lente nativa, senza dipendenze
```

* **Parallelismo**: 885 sonde si diramano sul dispatcher predefinito con un numero di permessi limitato; i risultati confluiscono nella UI man mano che arrivano.
* **Niente in background**: nessun servizio né scansioni pianificate; l'app gira solo mentre è aperta e si chiude da sola quando resta inattiva.
* **Livello nativo**: un piccolo `.so`, collegato per nome tramite `RegisterNatives`, tenuto deliberatamente minuscolo perché è la parte che deve essere difficile da ingannare.

## Contribuire

La maggior parte dei contributi non richiede Kotlin: gli elenchi sono testo semplice in `VDInfos/app/src/main/assets/data/`. Aggiungi o rimuovi una riga e apri una pull request.

* `*_apps.txt` - un nome di pacchetto per riga
* `props.txt` - il catalogo delle proprietà di sistema, `CATEGORIA<tab>chiave`
* `spoof_keys.txt` - la matrice di spoof delle settings, `chiave:TIPO`
* altri file `.txt` - anche una voce per riga (frammenti di nome dei moduli del kernel, nomi in `/data/local/tmp`)

Un `#` inizia un commento; le righe vuote sono ignorate. Anche i contributi di codice sono benvenuti. Contribuendo accetti che il tuo lavoro sia distribuito sotto la AGPL-3.0-or-later di questo progetto.

## Catalogo

I riferimenti su occultamento e rilevamento del root (guide, moduli, framework, rilevatori) stanno in un catalogo dedicato: [CATALOG.md](CATALOG.md).

## Ringraziamenti

Un ringraziamento speciale a **[frknkrc44](https://github.com/frknkrc44)**, sviluppatore di **[HMA-OSS](https://github.com/frknkrc44/HMA-OSS)**, per la cura che vi ha dedicato. HMA-OSS è dove vengono applicate diverse delle correzioni che questa app suggerisce - nascondere le app bersaglio e impostare i preset di spoof - e ogni menzione ad esso nell'app rimanda alla sua fonte.

I suoi link, i canali e come sostenerlo: [HMA-OSS.md](HMA-OSS.md).

## Contatti

* https://vd171.ru
* https://vd.priv8.ru
* **Telegram:** @VD_Priv8 https://t.me/VD_Priv8
* **Discord:** @VD.Priv8 https://discord.com/users/1296831918989639721
* **E-mail:** vd.priv8@pm.me
* **XDA-Developers:** @VD171 https://xdaforums.com/m/vd171.4699873/
* **GitHub:** @VD171 https://github.com/VD171

## Licenza

**GNU AGPL-3.0-or-later** - vedi [LICENSE](LICENSE). Copyleft, inclusa la clausola di rete: chiunque esegua una versione modificata (anche come servizio) deve offrirne il codice sorgente. Scelta deliberatamente per uno strumento di ricerca/anti-rilevamento, per mantenere i fork aperti.

---

`android` `privacy` `security` `root-detection` `hook-detection` `spoof-detection` `anti-detection` `tamper-detection` `device-fingerprint` `magisk` `kernelsu` `lsposed` `play-integrity` `attestation` `tee` `keystore` `scanner` `diagnostics` `reverse-engineering` `kotlin`
