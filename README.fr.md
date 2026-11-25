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
  <b>Débogueur de méthodes.</b><br>
  Lis chaque valeur de l'appareil par toutes les méthodes capables de la lire, et aligne-les pour que tu compares.
</p>

<p align="center">
  <a href="https://github.com/VD171/VD-Infos/releases">Releases</a> ·
  <a href="https://github.com/VD171/VD-Infos/wiki">Wiki</a> ·
  <a href="https://github.com/VD171/VD-Infos/wiki/USER-GUIDE">Guide d'utilisation</a> ·
  <a href="https://github.com/VD171/VD-Infos/wiki/AI-AGENTS">Agents IA</a> ·
  <a href="https://github.com/VD171/VD-Infos/wiki/EXPORT-FORMAT">Format d'export</a> ·
  <a href="https://github.com/VD171/VD-Infos/wiki/ARCHITECTURE">Architecture</a> ·
  <a href="CATALOG.md">Catalogue</a> ·
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

Android est un système d'exploitation extrêmement puissant et polyvalent ; ce que personne ne te dit, c'est que tous tes détails personnels et tes informations confidentielles sont accessibles à chaque application que tu installes, et te protéger contre ces atteintes à la vie privée est une obligation. VD Infos te montre un exemple de ce qui peut être capturé sur ton appareil, et le fait comme un *débogueur de méthodes* : pour chaque information il lit la valeur par **toutes les méthodes capables de la lire**: les API du framework, les interfaces natives, les commandes shell, les managers système, les content providers, les fichiers, les syscalls et l'attestation de clé matérielle (TEE), et les aligne pour que tu compares. Quand une lecture diverge des autres, quelque chose au milieu réécrit cette surface : un framework de hooking, un spoofer, un shim de résolution. **AUCUNE INFORMATION N'EST STOCKÉE, ENVOYÉE NI TRANSMISE À UN QUELCONQUE FICHIER¹ OU SERVEUR**: tout s'exécute sur l'appareil, les valeurs porteuses d'identité sont masquées jusqu'à ce que tu les révèles, et un rapport ne quitte l'appareil que lorsque tu le partages ou l'enregistres explicitement ; si tu veux, bloque l'accès à internet avec un pare-feu ou coupe-le tout simplement.

> **¹ Exception:** chaque analyse conserve son rapport complet dans le stockage privé de l'app (`files/last_snapshot.json`). Seuls l'app elle-même ou root peuvent le lire, l'analyse suivante l'écrase et il ne quitte jamais l'appareil. Il existe pour que les agents IA puissent lire le résultat complet ; voir **Pour les agents IA : le rapport complet en JSON**.

## Ce qu'il inspecte

Chaque élément est lu par toutes les méthodes capables de le lire (SDK Java / natif / shell), comparées côte à côte :

* **~488 propriétés système** lues par plusieurs chemins indépendants : l'API Java, l'interface native des propriétés et la commande shell `getprop` atteinte de différentes manières, puis alignées et comparées. Comme ces chemins ne passent pas tous par la même couche, une valeur réécrite sur l'un mais pas sur l'autre apparaît comme une divergence.
* **Identité de l'appareil** : modèle, fabricant, marque, device, product, board, hardware, fingerprint, bootloader, build id/tags/type, chaque champ `Build.*` face à toutes ses variantes `ro.product.*` (system/vendor/odm), natif et shell.
* **Identifiants** : serial (plusieurs getters), Android ID (settings et provider), GSF ID, advertising ID, IMEI/primary IMEI/MEID/IMSI/ICCID, user serial.
* **Téléphonie** : la surface de TelephonyManager (opérateur, SIM, réseau, roaming...), SubscriptionManager (multi-SIM), cell info.
* **Réseau** : **MAC** par interface (`NetworkInterface` vs `/sys/class/net` vs shell), Wi-Fi (SSID/BSSID/IP/MAC), Bluetooth, DNS.
* **Noyau / processus / système** : `uname`, boot id, uptime, UID/PID, SELinux, fuseau horaire, locale, environnement, services/processus en cours.
* **Matériel / médias** : CPU, mémoire, capteurs, écran, caméras, system features, GPU, ID DRM Widevine.
* **Paquets / comptes / WebView** : liste et nombre de paquets installés, empreintes de signature par app, signature de cette app, auto-inspection de son propre paquet, énumérations de résolution d'intents, comptes, nombre d'entrées du journal d'appels, user agent.
* **Conformité du sandbox** : surfaces d'identité de bas niveau et surfaces noyau, plus un ensemble de services `dumpsys`, qu'une application correctement isolée ne devrait pas pouvoir atteindre. Pour celles-ci l'application vérifie si l'accès est correctement refusé ; une surface accessible alors qu'elle ne devrait pas l'être est signalée pour examen.
* **Identifiants publicitaires** : le GAID depuis le binder de Play Services, plus toute la famille de clés (AAID, OAID/VAID et les `pps_*` de Huawei) balayée dans les trois settings stores.
* **Détecteurs de root / hook / émulateur** : artefacts et signaux laissés par les gestionnaires de root, frameworks de hooking et émulateurs courants (Magisk, KernelSU, APatch, Xposed/LSPosed, Riru et similaires), plus le verified boot state.

## Téléchargement et support

Chaque release publie deux APK : **SDK_35** vise le bac à sable moderne et strict, c'est celui à utiliser ; **SDK_27** vise un niveau d'API plus ancien, utile pour comparer. Les deux s'installent sur Android 8.0 et plus.

* Release (APK): https://github.com/VD171/VD-Infos/releases
* https://github.com/VD171/VD-Infos
* https://xdaforums.com/t/VD-Infos.4097379/
* https://t.me/RootDetected
* https://t.me/BlankAssistance

## Pour les agents IA : le rapport complet en JSON

Conçu pour les agents IA et les outils LLM qui travaillent sur l'appareil via un shell (assistants de programmation, scripts d'analyse). Au lieu de lire des captures d'écran, l'agent lit tout le résultat en une commande :

```sh
su -c cat /data/data/ru.vd171.vdinfos/files/last_snapshot.json
```

* **Contenu :** chaque sonde avec son verdict et la valeur de toutes les lentilles, dans le même JSON que le bouton d'export. L'écran n'affiche que la première lecture d'un élément replié ; le fichier les contient toutes.
* **Mise à jour :** à la fin de chaque analyse, lancez donc une analyse d'abord ; le fichier contient toujours la plus récente.
* **Prérequis :** root, car le fichier se trouve dans le stockage privé de l'app.

## Architecture

```
core/model      domaine immuable : Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          NativeBridge (JNI), SystemProps (réflexion), PropCatalog (données)
                SemanticProbes, IntegrityProbes, ProbeRegistry
engine/         ProbeEngine: fan-out en coroutines, diffuse les résultats en Flow
data/           SnapshotStore (persistance + diff), Exporter (partage JSON/texte)
ui/             Jetpack Compose, Material 3, couleur dynamique, progression en direct
cpp/            native_probes.cpp: la lentille native, sans dépendances
```

* **Parallélisme** : Les sondes se déploient sur le dispatcher par défaut avec un nombre de permis borné ; les résultats affluent dans l'UI au fur et à mesure.
* **Rien en arrière-plan** : aucun service ni analyse planifiée ; l'app ne s'exécute que lorsqu'elle est ouverte et se ferme d'elle-même lorsqu'elle reste inactive.
* **Couche native** : un petit `.so` sans dépendances, lié par nom via `RegisterNatives`, gardé délibérément minuscule et simple.

## Contribuer

La plupart des contributions ne demandent pas de Kotlin : les listes sont du texte brut dans `VDInfos/app/src/main/assets/data/`. Ajoutez ou retirez une ligne et ouvrez une pull request.

* `*_apps.txt`: un nom de paquet par ligne
* `props.txt`: le catalogue des propriétés système, `CATÉGORIE<tab>clé`
* `spoof_keys.txt`: la matrice de spoof des settings, `clé:TYPE`
* `known_certs.txt`: le catalogue des certificats de signature, `SHA-256<tab>propriétaire` (hash complet, tiré d'un vrai APK avec `apksigner verify --print-certs`)
* autres fichiers `.txt`: une entrée par ligne également (fragments de nom de modules du noyau, noms dans `/data/local/tmp`)

Un `#` commence un commentaire ; les lignes vides sont ignorées. Certains fichiers contiennent plusieurs champs séparés par une espace ou une tabulation par ligne, ou des marqueurs optionnels (`S` et `@field` dans `props.txt`) ; conservez la forme des lignes déjà présentes dans le fichier. Les contributions de code sont aussi bienvenues. En contribuant, vous acceptez que votre travail soit diffusé sous l'AGPL-3.0-or-later de ce projet.

## Catalogue

Les références de dissimulation et de détection du root (guides, modules, frameworks, détecteurs) se trouvent dans un catalogue dédié : [CATALOG.md](CATALOG.md).

## Remerciements

Un merci tout particulier à **[frknkrc44](https://github.com/frknkrc44)**, développeur de **[HMA-OSS](https://github.com/frknkrc44/HMA-OSS)**, pour le soin qui y est mis. HMA-OSS est là où plusieurs des correctifs que cette app suggère sont appliqués (masquer les apps cibles et définir les presets de spoof), et chaque mention de lui dans l'app renvoie à sa source.

Ses liens, ses canaux et comment le soutenir : [HMA-OSS.md](HMA-OSS.md).

## Documentation

- **[Wiki](https://github.com/VD171/VD-Infos/wiki)** : toute la documentation, consultable en ligne en 22 langues.
- **[Guide d'utilisation](https://github.com/VD171/VD-Infos/wiki/USER-GUIDE)** : installer, lancer un scan, lire le résultat et partager un rapport.
- **[Pour les agents IA](https://github.com/VD171/VD-Infos/wiki/AI-AGENTS)** et **[Format d'export](https://github.com/VD171/VD-Infos/wiki/EXPORT-FORMAT)** : le rapport lisible par machine et son schéma.
- **[Architecture](https://github.com/VD171/VD-Infos/wiki/ARCHITECTURE)** et **[Compilation](https://github.com/VD171/VD-Infos/wiki/BUILDING)** : comment ça marche et comment le compiler.
- **[Confidentialité](https://github.com/VD171/VD-Infos/wiki/PRIVACY)**, **[Sécurité](https://github.com/VD171/VD-Infos/wiki/SECURITY)** et **[Code de conduite](https://github.com/VD171/VD-Infos/wiki/CODE_OF_CONDUCT)** : la déclaration de confidentialité et les documents de gouvernance.
- **[Catalogue](CATALOG.md)** : références sur le masquage du root et la détection (guides, modules, frameworks et détecteurs).
- **[Journal des modifications](CHANGELOG.md)** : ce qui a changé dans chaque version.
- **[HMA-OSS](https://github.com/VD171/VD-Infos/wiki/HMA-OSS)** : le module de masquage d'applications sur lequel reposent les correctifs de l'app (ses liens, ses canaux et comment le soutenir).
- **[Contribuer](https://github.com/VD171/VD-Infos/wiki/CONTRIBUTING)** : comment ajouter des cibles de détection (surtout des listes en texte brut, sans Kotlin).

<!-- /vd-docs -->

## Voir aussi

- **[COPG-VD](https://github.com/VD171/COPG-VD)** : usurpation du fingerprint global de l'appareil / Play Integrity, sur tout l'appareil avec une portée par application et une WebUI.
- **[VDBackup](https://github.com/VD171/VD-Backup)** : sauvegarde et restauration complètes des applications et des données en root, pilotées entièrement depuis une WebUI.
- **[VD-dropbear](https://github.com/VD171/VD-dropbear)** : un démon SSH autonome en root (Dropbear).
- **[VDGoogle](https://github.com/VD171/VD-Google)** : vos services Google (RCS, Wallet, attestations Play Integrity), lus sur votre appareil sans internet.
- **[VDInstaller](https://github.com/VD171/VD-Installer)** : un installateur sideload en root et sans friction pour les APK uniques, par lots et splittés, avec un interrupteur pour le vérificateur Play Protect et l'usurpation de la source d'installation.
- **[VDLiveUpdate](https://github.com/VD171/VDLiveUpdate)** : un moteur d'auto-mise à jour incrémentale et sans flashage, intégré aux modules KernelSU/APatch/Magisk.
- **[VD-Modules](https://github.com/VD171/VD-Modules)** : notre dépôt de modules pour MMRL/WebUI-X (installez tout ceci depuis votre gestionnaire root).
- **[@VictorGabriel7Dev/build.prop](https://github.com/VictorGabriel7Dev/build.prop)** : un jeu de données de fingerprints `build.prop` de Pixel (fork de @T3SL4).
- **[@VictorGabriel7Dev/mtproto-store](https://github.com/VictorGabriel7Dev/mtproto-store)** : utilisez Telegram comme stockage d'objets depuis une petite CLI en Go.

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

## Licence

**GNU AGPL-3.0-or-later**: voir [LICENSE](LICENSE). Copyleft, y compris la clause réseau : quiconque exécute une version modifiée (même en tant que service) doit en offrir le code source. Choisie délibérément pour un outil de recherche/anti-détection, afin de garder les forks ouverts.

## Marques

VD Infos est un outil indépendant et non officiel de recherche et de diagnostic. Il n'est ni affilié, ni approuvé, ni sponsorisé par aucun des fournisseurs dont il lit, détecte ou nomme les logiciels, services ou marques (y compris Google LLC et l'Android Open Source Project). "Android", "Google", "Google Play", "Play Integrity", "Widevine" et tous les autres noms de produits, logos et marques commerciales sont la propriété de leurs détenteurs respectifs. VD Infos ne lit que ce qui se trouve déjà sur ton appareil et n'envoie rien nulle part.

## Étiquettes

`android` `privacy` `security` `root-detection` `hook-detection` `spoof-detection` `anti-detection` `tamper-detection` `device-fingerprint` `magisk` `kernelsu` `lsposed` `play-integrity` `attestation` `tee` `keystore` `scanner` `diagnostics` `reverse-engineering` `kotlin`
