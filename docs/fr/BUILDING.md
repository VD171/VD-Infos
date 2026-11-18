> **Languages:** [English](../BUILDING.md) · [العربية](../ar/BUILDING.md) · [Čeština](../cs/BUILDING.md) · [Deutsch](../de/BUILDING.md) · [Español](../es/BUILDING.md) · [فارسی](../fa/BUILDING.md) · Français · [हिन्दी](../hi/BUILDING.md) · [Bahasa Indonesia](../id/BUILDING.md) · [Italiano](../it/BUILDING.md) · [日本語](../ja/BUILDING.md) · [한국어](../ko/BUILDING.md) · [Nederlands](../nl/BUILDING.md) · [Polski](../pl/BUILDING.md) · [Português](../pt/BUILDING.md) · [Русский](../ru/BUILDING.md) · [Svenska](../sv/BUILDING.md) · [ไทย](../th/BUILDING.md) · [Türkçe](../tr/BUILDING.md) · [Українська](../uk/BUILDING.md) · [Tiếng Việt](../vi/BUILDING.md) · [中文](../zh/BUILDING.md)

---

# Compiler VD Infos

VD Infos est un projet Android Gradle standard : Kotlin et Jetpack Compose, avec une petite
lentille native en C++ compilée via le NDK. La CI peut le compiler, et vous pouvez le reproduire
localement.

## Prérequis

- Un JDK (17 à 21) et le Android SDK.
- Le Android NDK et CMake, pour la lentille native (le plugin Android de Gradle récupère un
  CMake compatible si vous avez le composant `cmake` du SDK).
- `minSdk` 26, si bien que les builds tournent sur Android 8.0 et plus récent.

## Démarrage rapide

```sh
# build both flavors, release
./gradlew assembleRelease
```

Cela produit **deux APK**, un par flavor :

- **modern** (`SDK_35`) - le bac à sable moderne et strict, le build à utiliser.
- **compat** (`SDK_27`) - une cible d'API plus ancienne, pour la comparaison côte à côte seulement.

Les sorties atterrissent dans :

```
app/build/outputs/apk/modern/release/VDInfos-v<ver>-SDK_35-release.apk
app/build/outputs/apk/compat/release/VDInfos-v<ver>-SDK_27-release.apk
```

Pour compiler un seul flavor pendant que vous itérez :

```sh
./gradlew assembleModernRelease   # SDK_35 only
./gradlew assembleCompatRelease   # SDK_27 only
```

## Signature

Les builds release sont signés avec une clé qui est **locale et non versionnée** (le keystore
et ses propriétés sont ignorés par git). Sans elle, la tâche release compile tout de même mais
l'APK sort non signé. Fournissez votre propre keystore pour signer vos propres builds.

## Les deux flavors

L'application distribue deux flavors à dessein. Ils visent différents niveaux d'API et
compilent à partir de la même source ; seuls la cible et quelques chemins de code protégés diffèrent. Un
changement qui touche du code natif ou un chemin spécifique à un flavor devrait être compilé et vérifié dans
les **deux** flavors, pas seulement celui sur lequel vous itérez.

## La lentille native

La lentille native est une unique unité de traduction C++ sans dépendance compilée par le NDK en
un petit `.so`, liée par nom à travers `RegisterNatives`. Elle est compilée dans le cadre du
build Gradle normal ; il n'y a pas d'étape séparée. Elle est gardée délibérément minuscule et simple.

## Où vivent les choses

```
app/src/main/java/ru/vd171/vdinfos/   Kotlin: model, probes, engine, data, ui
app/src/main/cpp/                     the native lens
app/src/main/assets/data/             the plain-text detection lists (see Contributing)
```

Voir [Architecture](ARCHITECTURE.md) pour savoir comment les pièces s'assemblent, et
[Contribuer](CONTRIBUTING.md) pour savoir comment ajouter une cible de détection sans toucher au code.

<!-- doc-footer -->

---

> _Cette documentation a été générée avec Claude (Anthropic) et est maintenue par VD171. Les corrections et améliorations sont les bienvenues ; voir [CONTRIBUTING.md](CONTRIBUTING.md)._
