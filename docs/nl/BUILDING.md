> **Languages:** [English](../BUILDING.md) · [العربية](../ar/BUILDING.md) · [Čeština](../cs/BUILDING.md) · [Deutsch](../de/BUILDING.md) · [Español](../es/BUILDING.md) · [فارسی](../fa/BUILDING.md) · [Français](../fr/BUILDING.md) · [हिन्दी](../hi/BUILDING.md) · [Bahasa Indonesia](../id/BUILDING.md) · [Italiano](../it/BUILDING.md) · [日本語](../ja/BUILDING.md) · [한국어](../ko/BUILDING.md) · Nederlands · [Polski](../pl/BUILDING.md) · [Português](../pt/BUILDING.md) · [Русский](../ru/BUILDING.md) · [Svenska](../sv/BUILDING.md) · [ไทย](../th/BUILDING.md) · [Türkçe](../tr/BUILDING.md) · [Українська](../uk/BUILDING.md) · [Tiếng Việt](../vi/BUILDING.md) · [中文](../zh/BUILDING.md)

---

# VD Infos bouwen

VD Infos is een standaard Android Gradle-project: Kotlin en Jetpack Compose, met een
kleine native lens in C++ gebouwd via de NDK. CI kan het bouwen, en je kunt het lokaal
reproduceren.

## Vereisten

- Een JDK (17 tot 21) en de Android SDK.
- De Android NDK en CMake, voor de native lens (de Gradle Android-plugin haalt een
  passende CMake op als je de `cmake`-component van de SDK hebt).
- `minSdk` 26, dus builds draaien op Android 8.0 en nieuwer.

## Snelle start

```sh
# build both flavors, release
./gradlew assembleRelease
```

Dit produceert **twee APK's**, een per flavor:

- **modern** (`SDK_35`) - de strikte moderne sandbox, de build om te gebruiken.
- **compat** (`SDK_27`) - een ouder doel met een lossere SELinux-domein, alleen voor
  vergelijking naast elkaar.

De uitvoer belandt in:

```
app/build/outputs/apk/modern/release/VDInfos-v<ver>-SDK_35-release.apk
app/build/outputs/apk/compat/release/VDInfos-v<ver>-SDK_27-release.apk
```

Om tijdens het itereren slechts een flavor te bouwen:

```sh
./gradlew assembleModernRelease   # SDK_35 only
./gradlew assembleCompatRelease   # SDK_27 only
```

## Ondertekenen

Release-builds worden ondertekend met een sleutel die **lokaal is en niet gecommit** (de
keystore en zijn eigenschappen worden door git genegeerd). Zonder deze bouwt de
release-taak nog steeds, maar komt de APK ongetekend uit. Lever je eigen keystore om je
eigen builds te ondertekenen.

## De twee flavors

De app levert bewust twee flavors uit. Ze richten zich op verschillende API-niveaus en
vallen daardoor in verschillende Android-sandboxdomeinen, wat zelf nuttig is voor
vergelijking. Beide bouwen vanuit dezelfde broncode; alleen het doel en een paar bewaakte
codepaden verschillen. Een wijziging die native code of een flavorspecifiek pad raakt,
zou in **beide** flavors gebouwd en gecontroleerd moeten worden, niet alleen die waarop je
itereert.

## De native lens

De native lens is een enkele, afhankelijkheidsvrije C++-vertaaleenheid die door de NDK als
een kleine `.so` wordt gebouwd, per naam gebonden via `RegisterNatives`. Hij wordt
gecompileerd als deel van de normale Gradle-build; er is geen aparte stap. Hij wordt
bewust piepklein gehouden omdat het het deel is dat moeilijk te misleiden moet zijn.

## Waar de dingen leven

```
app/src/main/java/ru/vd171/vdinfos/   Kotlin: model, probes, engine, data, ui
app/src/main/cpp/                     the native lens
app/src/main/assets/data/             the plain-text detection lists (see Contributing)
```

Zie [Architectuur](ARCHITECTURE.md) voor hoe de stukken in elkaar passen, en
[Bijdragen](CONTRIBUTING.md) voor hoe je een detectiedoel toevoegt zonder code aan te
raken.

<!-- doc-footer -->

---

> _Deze documentatie is gegenereerd met Claude (Anthropic) en wordt onderhouden door VD171. Correcties en verbeteringen zijn welkom; zie [CONTRIBUTING.md](CONTRIBUTING.md)._
