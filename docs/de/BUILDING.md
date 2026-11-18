> **Languages:** [English](../BUILDING.md) · [العربية](../ar/BUILDING.md) · [Čeština](../cs/BUILDING.md) · Deutsch · [Español](../es/BUILDING.md) · [فارسی](../fa/BUILDING.md) · [Français](../fr/BUILDING.md) · [हिन्दी](../hi/BUILDING.md) · [Bahasa Indonesia](../id/BUILDING.md) · [Italiano](../it/BUILDING.md) · [日本語](../ja/BUILDING.md) · [한국어](../ko/BUILDING.md) · [Nederlands](../nl/BUILDING.md) · [Polski](../pl/BUILDING.md) · [Português](../pt/BUILDING.md) · [Русский](../ru/BUILDING.md) · [Svenska](../sv/BUILDING.md) · [ไทย](../th/BUILDING.md) · [Türkçe](../tr/BUILDING.md) · [Українська](../uk/BUILDING.md) · [Tiếng Việt](../vi/BUILDING.md) · [中文](../zh/BUILDING.md)

---

# VD Infos bauen

VD Infos ist ein Standard-Android-Gradle-Projekt: Kotlin und Jetpack Compose, mit einer
kleinen nativen Linse in C++, die über das NDK gebaut wird. CI kann es bauen, und Sie
können es lokal reproduzieren.

## Voraussetzungen

- Ein JDK (17 bis 21) und das Android SDK.
- Das Android NDK und CMake für die native Linse (das Gradle-Android-Plugin holt ein
  passendes CMake, wenn Sie die `cmake`-Komponente des SDK haben).
- `minSdk` 26, sodass Builds auf Android 8.0 und neuer laufen.

## Schnellstart

```sh
# build both flavors, release
./gradlew assembleRelease
```

Dies erzeugt **zwei APKs**, eines pro Flavor:

- **modern** (`SDK_35`) - die strikte moderne Sandbox, der zu verwendende Build.
- **compat** (`SDK_27`) - ein älteres Ziel mit einer lockereren SELinux-Domäne, nur für
  den direkten Vergleich.

Die Ausgaben landen in:

```
app/build/outputs/apk/modern/release/VDInfos-v<ver>-SDK_35-release.apk
app/build/outputs/apk/compat/release/VDInfos-v<ver>-SDK_27-release.apk
```

Um beim Iterieren nur einen Flavor zu bauen:

```sh
./gradlew assembleModernRelease   # SDK_35 only
./gradlew assembleCompatRelease   # SDK_27 only
```

## Signierung

Release-Builds werden mit einem Schlüssel signiert, der **lokal und nicht eingecheckt**
ist (der Keystore und seine Eigenschaften werden von git ignoriert). Ohne ihn baut die
Release-Aufgabe trotzdem, aber das APK kommt unsigniert heraus. Stellen Sie Ihren eigenen
Keystore bereit, um Ihre eigenen Builds zu signieren.

## Die zwei Flavors

Die App liefert absichtlich zwei Flavors aus. Sie zielen auf unterschiedliche API-Ebenen
und fallen daher in verschiedene Android-Sandbox-Domänen, was selbst für den Vergleich
nützlich ist. Beide bauen aus derselben Quelle; nur das Ziel und einige wenige
abgesicherte Codepfade unterscheiden sich. Eine Änderung, die nativen Code oder einen
flavor-spezifischen Pfad berührt, sollte in **beiden** Flavors gebaut und geprüft werden,
nicht nur in dem, in dem Sie iterieren.

## Die native Linse

Die native Linse ist eine einzige, abhängigkeitsfreie C++-Übersetzungseinheit, die vom
NDK als eine kleine `.so` gebaut wird, per Name über `RegisterNatives` gebunden. Sie wird
als Teil des normalen Gradle-Builds kompiliert; es gibt keinen separaten Schritt. Sie
wird bewusst winzig gehalten, weil sie der Teil ist, der schwer zu täuschen sein muss.

## Wo die Dinge liegen

```
app/src/main/java/ru/vd171/vdinfos/   Kotlin: model, probes, engine, data, ui
app/src/main/cpp/                     the native lens
app/src/main/assets/data/             the plain-text detection lists (see Contributing)
```

Siehe [Architektur](ARCHITECTURE.md) dafür, wie die Teile zusammenpassen, und
[Mitwirken](CONTRIBUTING.md) dafür, wie man ein Erkennungsziel hinzufügt, ohne Code zu
berühren.

<!-- doc-footer -->

---

> _Diese Dokumentation wurde mit Claude (Anthropic) erstellt und wird von VD171 gepflegt. Korrekturen und Verbesserungen sind willkommen; siehe [CONTRIBUTING.md](CONTRIBUTING.md)._
