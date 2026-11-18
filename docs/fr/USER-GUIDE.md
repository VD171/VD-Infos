> **Languages:** [English](../USER-GUIDE.md) · [العربية](../ar/USER-GUIDE.md) · [Čeština](../cs/USER-GUIDE.md) · [Deutsch](../de/USER-GUIDE.md) · [Español](../es/USER-GUIDE.md) · [فارسی](../fa/USER-GUIDE.md) · Français · [हिन्दी](../hi/USER-GUIDE.md) · [Bahasa Indonesia](../id/USER-GUIDE.md) · [Italiano](../it/USER-GUIDE.md) · [日本語](../ja/USER-GUIDE.md) · [한국어](../ko/USER-GUIDE.md) · [Nederlands](../nl/USER-GUIDE.md) · [Polski](../pl/USER-GUIDE.md) · [Português](../pt/USER-GUIDE.md) · [Русский](../ru/USER-GUIDE.md) · [Svenska](../sv/USER-GUIDE.md) · [ไทย](../th/USER-GUIDE.md) · [Türkçe](../tr/USER-GUIDE.md) · [Українська](../uk/USER-GUIDE.md) · [Tiếng Việt](../vi/USER-GUIDE.md) · [中文](../zh/USER-GUIDE.md)

---

# Guide d'utilisation de VD Infos

VD Infos est un **débogueur de méthodes de l'appareil**. Il lit chaque valeur de l'appareil à travers chaque
méthode capable de la lire, et aligne les lectures côte à côte pour que vous puissiez les comparer.
Ce guide couvre l'installation de l'application, le lancement d'une analyse, la lecture du résultat et
le partage d'un rapport. Il n'interprète aucun constat précis à votre place : la comparaison
est à vous de la lire, sur votre propre appareil.

## Table des matières

1. [Ce qu'est VD Infos](#1-ce-quest-vd-infos)
2. [Ce qu'il n'est pas](#2-ce-quil-nest-pas)
3. [Prérequis](#3-prérequis)
4. [Quel APK installer](#4-quel-apk-installer)
5. [Installation et premier lancement](#5-installation-et-premier-lancement)
6. [Lancer une analyse](#6-lancer-une-analyse)
7. [Lire le résultat](#7-lire-le-résultat)
8. [Exporter votre rapport](#8-exporter-votre-rapport)
9. [Pour les agents IA](#9-pour-les-agents-ia)
10. [Langue et thème](#10-langue-et-thème)
11. [La confidentialité en un coup d'œil](#11-la-confidentialité-en-un-coup-dœil)
12. [Foire aux questions](#12-foire-aux-questions)

## 1. Ce qu'est VD Infos

Android remet une grande partie des informations de votre appareil à chaque application que vous installez. VD Infos
vous montre un exemple de ce qu'une application ordinaire peut lire, et il le fait en tant que *débogueur
de méthodes* : pour chaque élément d'information il lit la valeur à travers **chaque méthode
capable de la lire** - `Build.*`, `SystemProperties`, `getprop`, le natif
`__system_property_get`, les gestionnaires système, les fournisseurs de contenu, les fichiers, les appels système, et
l'attestation de clés matérielle (TEE) - et les aligne pour que vous puissiez comparer. C'est une lentille,
pas une machine à verdicts : il fait remonter les lectures et vous en laisse la lecture.

## 2. Ce qu'il n'est pas

- Ce n'est **pas** un nettoyeur, un booster ni un outil de dissimulation. Il ne change rien sur votre
  système ; il ne fait que lire et comparer.
- Ce n'est **pas** un score réussi/échoué. Il vous montre les lectures brutes ; ce qu'elles signifient pour
  votre configuration est à vous d'en juger, sur votre appareil.
- Il n'a **pas** besoin d'internet, et il fonctionne pleinement avec internet bloqué.

## 3. Prérequis

- Android 8.0 (API 26) ou plus récent.
- Aucune permission spéciale n'est requise pour démarrer. Certaines lectures ne deviennent disponibles que si
  vous accordez la permission correspondante (par exemple l'accès à l'utilisation), et certaines n'existent que
  sur un appareil rooté ; là où une lecture est indisponible, l'application indique simplement qu'elle n'était
  pas disponible.

## 4. Quel APK installer

Chaque version publie **deux APK** :

- **SDK_35** - vise le bac à sable moderne et strict. C'est celui à utiliser.
- **SDK_27** - vise un niveau d'API plus ancien, avec un domaine SELinux plus laxiste. Il n'est utile
  que pour la comparaison côte à côte ; la plupart des gens n'en ont pas besoin.

Les deux s'installent sur Android 8.0 et plus récent. Téléchargez-les depuis la page des
[Versions](https://github.com/VD171/VD-Infos/releases).

## 5. Installation et premier lancement

1. Téléchargez l'APK **SDK_35** depuis les Versions et installez-le (vous devrez peut-être autoriser
   les installations depuis votre navigateur ou votre gestionnaire de fichiers).
2. Ouvrez l'application. Elle s'ouvre directement sur l'écran d'analyse ; pas de compte, pas
   de connexion ni de configuration.
3. Accordez éventuellement une permission que l'application demande (comme l'accès à l'utilisation) pour débloquer
   les lectures qui en dépendent. C'est entièrement facultatif.

## 6. Lancer une analyse

Touchez l'action d'analyse. L'application déploie ses sondes en parallèle et diffuse les résultats dans
la liste au fur et à mesure qu'ils arrivent, avec un indicateur de progression en direct. Une analyse se termine d'elle-même ; elle
lit seulement pendant que l'application est ouverte, ne laisse rien tourner en arrière-plan, et se ferme
d'elle-même lorsqu'on la laisse inactive.

Pour la relancer, analysez à nouveau : chaque analyse remplace le résultat précédent.

## 7. Lire le résultat

Chaque élément est une carte qui aligne les lectures de chaque lentille (la méthode Java SDK,
la méthode native, le shell, et ainsi de suite). La couleur de la carte vous dit, en un coup d'œil,
comment ces lectures se rapportent les unes aux autres :

- **Correspondance** - toutes les lentilles sont d'accord. Les méthodes ont renvoyé la même valeur.
- **Divergence** - les lentilles ne sont pas d'accord. Une méthode a renvoyé quelque chose de différent
  des autres.
- **Information** - une lecture affichée pour référence, non soumise au vote.
- **Unique / indisponible** - une seule lentille a pu la lire, ou aucune n'a pu.

La carte montre toujours la matrice complète des lectures, pour que vous puissiez voir exactement quelle lentille
a dit quoi. Une carte repliée montre sa première lecture ; dépliez-la pour toutes les voir.

C'est aussi loin que l'application parle à votre place. **Ce qu'une divergence donnée signifie sur votre
appareil, et quoi en faire, est à vous de l'investiguer** - c'est tout l'intérêt
d'un débogueur : il met la preuve devant vous et vous laisse la lire. Utilisez le
champ de recherche pour filtrer par propriété ou valeur, et les puces de filtre pour affiner la liste.

## 8. Exporter votre rapport

Les actions de partage et d'enregistrement vous remettent le **résultat complet**, pas seulement ce qui est à l'écran :

- **Partager / enregistrer en JSON** - le rapport complet sous forme lisible par machine, les mêmes
  données que l'application conserve en interne. C'est le format à conserver ou à fournir à un outil.
- **Partager en texte** - un résumé compact et lisible par un humain des divergences.

Les valeurs porteuses d'identité (numéros de série, IMEI, adresses MAC, identifiants publicitaires et autres)
sont **masquées dans le rapport exporté** pour qu'un rapport que vous collez dans un fil public
ne divulgue pas vos propres identifiants ; les verdicts et la structure sont préservés. La
copie sur l'appareil que vous seul (ou root) pouvez lire conserve les valeurs réelles.

## 9. Pour les agents IA

VD Infos est conçu pour être lu par des outils, pas seulement par des personnes. Chaque analyse écrit son
rapport complet dans le stockage privé de l'application, et l'export est le même JSON lisible par machine.
Un assistant de codage IA ou un script d'analyse s'exécutant sur l'appareil peut lire le résultat
complet avec une seule commande au lieu d'analyser des captures d'écran. Voir le
[guide pour agents IA](AI-AGENTS.md) dédié et la référence du [format d'export](EXPORT-FORMAT.md).

## 10. Langue et thème

L'application suit la langue de votre système là où une traduction existe, et vous pouvez en choisir une
manuellement. Elle utilise Material 3 avec couleur dynamique et suit votre réglage clair/sombre.

## 11. La confidentialité en un coup d'œil

Tout s'exécute sur l'appareil. Aucune lecture n'est envoyée où que ce soit, et un rapport ne quitte l'appareil
que lorsque vous le partagez ou l'enregistrez explicitement. Chaque analyse conserve son rapport complet dans le
stockage privé de l'application (`files/last_snapshot.json`) pour que les outils puissent le lire ; seule l'application elle-même
ou root peuvent lire ce fichier, l'analyse suivante l'écrase, et il ne quitte jamais l'appareil.
Voir [Confidentialité](PRIVACY.md) pour la déclaration complète.

## 12. Foire aux questions

**Est-ce que ça change quoi que ce soit sur mon appareil ?** Non. Il ne fait que lire et comparer.

**A-t-il besoin de root ?** Non. L'application fonctionne sans root. Une poignée de lectures n'existent que
sur un appareil rooté, et le fichier de rapport privé ne peut être relu que par root ou par
l'application elle-même ; tout le reste fonctionne sans root.

**A-t-il besoin d'internet ?** Non. Il fonctionne pleinement hors ligne ; bloquez son accès réseau si
vous le souhaitez.

**Quel APK dois-je utiliser ?** SDK_35. La version SDK_27 n'existe que pour la comparaison.

**Pourquoi certains éléments disent indisponible ?** Parce que cette méthode n'a pas pu lire cette valeur sur
votre appareil - une permission manquante, une règle du bac à sable, ou du matériel qui n'est pas présent.
Cela fait en soi partie de la comparaison.

**Où signaler un problème ou suggérer une cible de détection ?** Voir
[Contribuer](CONTRIBUTING.md) ; la plupart des listes de détection sont du texte brut et ne nécessitent aucun code.

<!-- doc-footer -->

---

> _Cette documentation a été générée avec Claude (Anthropic) et est maintenue par VD171. Les corrections et améliorations sont les bienvenues ; voir [CONTRIBUTING.md](CONTRIBUTING.md)._
