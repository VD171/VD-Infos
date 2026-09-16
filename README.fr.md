[English](README.md) | [Português](README.pt.md) | [Español](README.es.md) | [Italiano](README.it.md) | [Deutsch](README.de.md) | **Français** | [Русский](README.ru.md) | [Bahasa Indonesia](README.id.md) | [Türkçe](README.tr.md) | [Polski](README.pl.md) | [Nederlands](README.nl.md) | [Svenska](README.sv.md) | [Čeština](README.cs.md) | [Tiếng Việt](README.vi.md) | [中文](README.zh.md) | [日本語](README.ja.md) | [한국어](README.ko.md) | [فارسی](README.fa.md) | [हिन्दी](README.hi.md) | [العربية](README.ar.md) | [ไทย](README.th.md)

# VD Infos

*Débogueur de méthodes*

[![License: AGPL-3.0-or-later](https://img.shields.io/badge/license-AGPL--3.0--or--later-blue.svg)](LICENSE)
[![Release](https://img.shields.io/github/v/release/VD171/VD-Infos)](https://github.com/VD171/VD-Infos/releases)
[![Downloads](https://img.shields.io/github/downloads/VD171/VD-Infos/total)](https://github.com/VD171/VD-Infos/releases)
[![Android 8.0+](https://img.shields.io/badge/Android-8.0%2B-3DDC84.svg)](https://github.com/VD171/VD-Infos/releases)
[![Languages](https://img.shields.io/badge/languages-21-orange.svg)](README.md)

<img src="images/vdinfos-01.png" height="420"/> <img src="images/vdinfos-02.png" height="420"/>

Android est un système d'exploitation extrêmement puissant et polyvalent ; ce que personne ne te dit, c'est que tous tes détails personnels et tes informations confidentielles sont accessibles à chaque application que tu installes, et te protéger contre ces atteintes à la vie privée est une obligation. VD Infos te montre un exemple de ce qui peut être capturé sur ton appareil, et le fait comme un *débogueur de méthodes* : pour chaque information il lit la valeur par **toutes les méthodes capables de la lire** - `Build.*`, `SystemProperties`, `getprop`, le natif `__system_property_get`, les managers système, les content providers, les fichiers, les syscalls et l'attestation de clé matérielle (TEE) - et les aligne pour que tu compares. Quand une méthode diverge des autres, quelque chose au milieu réécrit cette surface : un framework de hooking, un spoofer, un shim de résolution. **AUCUNE INFORMATION N'EST STOCKÉE, ENVOYÉE NI TRANSMISE À UN QUELCONQUE FICHIER OU SERVEUR** - tout s'exécute sur l'appareil, les valeurs porteuses d'identité sont masquées jusqu'à ce que tu les révèles, et un rapport ne quitte l'appareil que lorsque tu le partages ou l'enregistres explicitement ; si tu veux, bloque l'accès à internet avec un pare-feu ou coupe-le tout simplement.

## Ce qu'il inspecte

Chaque élément est lu par toutes les méthodes capables de le lire (SDK Java / natif / shell), comparées côte à côte :

* **~493 propriétés système** lues de CINQ façons : `SystemProperties.get`, les deux points d'entrée de bionic (`__system_property_read_callback` et celui de 92 octets `__system_property_get`), `getprop` lancé par la JVM, et `getprop` lancé depuis du code natif via `popen` - une commande shell exécutée par `ProcessBuilder` et la même commande exécutée sans la JVM ne sont pas le même point de vue, car la première est une surface qu'un framework de dissimulation réécrit et la seconde non.
* **Identité de l'appareil** : modèle, fabricant, marque, device, product, board, hardware, fingerprint, bootloader, build id/tags/type - chaque champ `Build.*` face à toutes ses variantes `ro.product.*` (system/vendor/odm), natif et shell.
* **Identifiants** : serial (plusieurs getters), Android ID (settings et provider), GSF ID, advertising ID, IMEI/primary IMEI/MEID/IMSI/ICCID, user serial.
* **Téléphonie** : la surface de TelephonyManager (opérateur, SIM, réseau, roaming...), SubscriptionManager (multi-SIM), cell info.
* **Réseau** : **MAC** par interface (`NetworkInterface` vs `/sys/class/net` vs shell), Wi-Fi (SSID/BSSID/IP/MAC), Bluetooth, DNS.
* **Noyau / processus / système** : `uname`, boot id, uptime, UID/PID, SELinux, fuseau horaire, locale, environnement, services/processus en cours.
* **Matériel / médias** : CPU, mémoire, capteurs, écran, caméras, system features, GPU, ID DRM Widevine.
* **Paquets / comptes / WebView** : liste et nombre de paquets installés, empreintes de signature par app, signature de cette app, auto-inspection de son propre paquet, énumérations de résolution d'intents, comptes, nombre d'entrées du journal d'appels, user agent.
* **Conformité du sandbox** : surfaces que le sandbox est censé FERMER (CID/serial de l'eMMC, identité UFS et SoC, `/proc/cmdline`, `/proc/1/*`, `/dev/kmsg`, `/system/build.prop`) et 22 services `dumpsys`. Ici le refus est la réponse : `EACCES`/`DENIED` est la lecture conforme, et un appareil qui livre plutôt le contenu est hors conformité.
* **Identifiants publicitaires** : le GAID depuis le binder de Play Services, plus toute la famille de clés (AAID, OAID/VAID et les `pps_*` de Huawei) balayée dans les trois settings stores.
* **Détecteurs de root / hook / émulateur** : artefacts de `su`/Magisk/KernelSU/APatch/Xposed/LSPosed/Riru, libs injectées dans `/proc/self/maps`, paquets connus, propriétés dangereuses et d'émulateur, verified boot state.

## Téléchargement et support

Chaque release publie deux APK : **SDK_35** vise le bac à sable moderne et strict, c'est celui à utiliser ; **SDK_27** vise un niveau d'API plus ancien, avec un domaine SELinux plus permissif, utile pour comparer. Les deux s'installent sur Android 8.0 et plus.

* Release (APK): https://github.com/VD171/VD-Infos/releases
* https://github.com/VD171/VD-Infos
* https://xdaforums.com/t/VD-Infos.4097379/
* https://t.me/RootDetected
* https://t.me/BlankAssistance

## Langues

21 langues d'interface : anglais, portugais, espagnol, italien, allemand, français, russe, indonésien, turc, polonais, néerlandais, suédois, tchèque, vietnamien, chinois, japonais, coréen, persan, hindi, arabe et thaï. L'app propose un choix de langue unique au premier lancement (avec une option "Par défaut du système") et suit sinon la langue du système.

## Architecture

```
core/model      domaine immuable : Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          NativeBridge (JNI) - SystemProps (réflexion) - PropCatalog (données)
                SemanticProbes - IntegrityProbes - ProbeRegistry
engine/         ProbeEngine - fan-out en coroutines, diffuse les résultats en Flow
data/           SnapshotStore (persistance + diff) - Exporter (partage JSON/texte)
ui/             Jetpack Compose, Material 3, couleur dynamique, progression en direct
cpp/            native_probes.cpp - la lentille native, sans dépendances
```

* **Parallélisme** : 885 sondes se déploient sur le dispatcher par défaut avec un nombre de permis borné ; les résultats affluent dans l'UI au fur et à mesure.
* **Rien en arrière-plan** : aucun service ni analyse planifiée ; l'app ne s'exécute que lorsqu'elle est ouverte et se ferme d'elle-même lorsqu'elle reste inactive.
* **Couche native** : un petit `.so`, lié par nom via `RegisterNatives`, gardé délibérément minuscule car c'est la partie qui doit être difficile à tromper.

## Contribuer

La plupart des contributions ne demandent pas de Kotlin : les listes sont du texte brut dans `VDInfos/app/src/main/assets/data/`. Ajoutez ou retirez une ligne et ouvrez une pull request.

* `*_apps.txt` - un nom de paquet par ligne
* `props.txt` - le catalogue des propriétés système, `CATÉGORIE<tab>clé`
* `spoof_keys.txt` - la matrice de spoof des settings, `clé:TYPE`

Un `#` commence un commentaire ; les lignes vides sont ignorées. Les contributions de code sont aussi bienvenues. En contribuant, vous acceptez que votre travail soit diffusé sous l'AGPL-3.0-or-later de ce projet.

## Catalogue

Les références de dissimulation et de détection du root (guides, modules, frameworks, détecteurs) se trouvent dans un catalogue dédié : [CATALOG.md](CATALOG.md).

## Remerciements

Un merci tout particulier à **[frknkrc44](https://github.com/frknkrc44)**, développeur de **[HMA-OSS](https://github.com/frknkrc44/HMA-OSS)**, pour le soin qui y est mis. HMA-OSS est là où plusieurs des correctifs que cette app suggère sont appliqués - masquer les apps cibles et définir les presets de spoof - et chaque mention de lui dans l'app renvoie à sa source.

Ses liens, ses canaux et comment le soutenir : [HMA-OSS.md](HMA-OSS.md).

## Contacts

* https://vd171.ru
* https://vd.priv8.ru
* **Telegram:** @VD_Priv8 https://t.me/VD_Priv8
* **Discord:** @VD.Priv8 https://discord.com/users/1296831918989639721
* **E-mail:** vd.priv8@pm.me
* **XDA-Developers:** @VD171 https://xdaforums.com/m/vd171.4699873/
* **GitHub:** @VD171 https://github.com/VD171

## Licence

**GNU AGPL-3.0-or-later** - voir [LICENSE](LICENSE). Copyleft, y compris la clause réseau : quiconque exécute une version modifiée (même en tant que service) doit en offrir le code source. Choisie délibérément pour un outil de recherche/anti-détection, afin de garder les forks ouverts.
