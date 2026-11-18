> **Languages:** [English](../BUILDING.md) · [العربية](../ar/BUILDING.md) · Čeština · [Deutsch](../de/BUILDING.md) · [Español](../es/BUILDING.md) · [فارسی](../fa/BUILDING.md) · [Français](../fr/BUILDING.md) · [हिन्दी](../hi/BUILDING.md) · [Bahasa Indonesia](../id/BUILDING.md) · [Italiano](../it/BUILDING.md) · [日本語](../ja/BUILDING.md) · [한국어](../ko/BUILDING.md) · [Nederlands](../nl/BUILDING.md) · [Polski](../pl/BUILDING.md) · [Português](../pt/BUILDING.md) · [Русский](../ru/BUILDING.md) · [Svenska](../sv/BUILDING.md) · [ไทย](../th/BUILDING.md) · [Türkçe](../tr/BUILDING.md) · [Українська](../uk/BUILDING.md) · [Tiếng Việt](../vi/BUILDING.md) · [中文](../zh/BUILDING.md)

---

# Sestavení VD Infos

VD Infos je standardní Android Gradle projekt: Kotlin a Jetpack Compose, s malou nativní
čočkou v C++ sestavenou přes NDK. CI ho umí sestavit a vy ho můžete reprodukovat lokálně.

## Požadavky

- JDK (17 až 21) a Android SDK.
- Android NDK a CMake pro nativní čočku (Gradle Android plugin stáhne odpovídající CMake,
  pokud máte komponentu `cmake` ze SDK).
- `minSdk` 26, takže buildy běží na Androidu 8.0 a novějším.

## Rychlý start

```sh
# build both flavors, release
./gradlew assembleRelease
```

Toto produkuje **dvě APK**, jedno na flavor:

- **modern** (`SDK_35`) - striktní moderní sandbox, build, který se má použít.
- **compat** (`SDK_27`) - starší cíl API, pouze pro srovnání bok po boku.

Výstupy přistanou v:

```
app/build/outputs/apk/modern/release/VDInfos-v<ver>-SDK_35-release.apk
app/build/outputs/apk/compat/release/VDInfos-v<ver>-SDK_27-release.apk
```

Chcete-li při iterování sestavit jen jeden flavor:

```sh
./gradlew assembleModernRelease   # SDK_35 only
./gradlew assembleCompatRelease   # SDK_27 only
```

## Podepisování

Release buildy jsou podepsány klíčem, který je **lokální a není zapsán** (keystore a jeho
vlastnosti jsou git ignorovány). Bez něj se release úloha přesto sestaví, ale APK vyjde
nepodepsané. Poskytněte svůj vlastní keystore k podepsání svých vlastních buildů.

## Dva flavory

Aplikace dodává dva flavory záměrně. Cílí na různé úrovně API a sestavují se ze
stejného zdroje; liší se jen cíl a pár ošetřených cest kódu. Změna, která se dotýká
nativního kódu nebo cesty specifické pro flavor, by měla být sestavena a zkontrolována v
**obou** flavorech, ne jen v tom, ve kterém iterujete.

## Nativní čočka

Nativní čočka je jedna, na závislostech nezávislá překladová jednotka C++ sestavená NDK
jako jedna malá `.so`, vázaná podle názvu přes `RegisterNatives`. Kompiluje se jako
součást normálního Gradle buildu; není zde žádný samostatný krok. Je držena záměrně
drobná a jednoduchá.

## Kde věci žijí

```
app/src/main/java/ru/vd171/vdinfos/   Kotlin: model, probes, engine, data, ui
app/src/main/cpp/                     the native lens
app/src/main/assets/data/             the plain-text detection lists (see Contributing)
```

Viz [Architektura](ARCHITECTURE.md) pro to, jak do sebe díly zapadají, a
[Přispívání](CONTRIBUTING.md) pro to, jak přidat cíl detekce bez dotýkání se kódu.

<!-- doc-footer -->

---

> _Tato dokumentace byla vytvořena pomocí Claude (Anthropic) a spravuje ji VD171. Opravy a vylepšení jsou vítány; viz [CONTRIBUTING.md](CONTRIBUTING.md)._
