> **Languages:** [English](../BUILDING.md) · [العربية](../ar/BUILDING.md) · [Čeština](../cs/BUILDING.md) · [Deutsch](../de/BUILDING.md) · [Español](../es/BUILDING.md) · [فارسی](../fa/BUILDING.md) · [Français](../fr/BUILDING.md) · [हिन्दी](../hi/BUILDING.md) · [Bahasa Indonesia](../id/BUILDING.md) · [Italiano](../it/BUILDING.md) · [日本語](../ja/BUILDING.md) · [한국어](../ko/BUILDING.md) · [Nederlands](../nl/BUILDING.md) · Polski · [Português](../pt/BUILDING.md) · [Русский](../ru/BUILDING.md) · [Svenska](../sv/BUILDING.md) · [ไทย](../th/BUILDING.md) · [Türkçe](../tr/BUILDING.md) · [Українська](../uk/BUILDING.md) · [Tiếng Việt](../vi/BUILDING.md) · [中文](../zh/BUILDING.md)

---

# Budowanie VD Infos

VD Infos to standardowy projekt Android Gradle: Kotlin i Jetpack Compose, z małą
natywną soczewką w C++ budowaną przez NDK. CI potrafi go zbudować, a ty możesz odtworzyć go
lokalnie.

## Wymagania

- JDK (17 do 21) i Android SDK.
- Android NDK i CMake, dla natywnej soczewki (wtyczka Android do Gradle pobiera
  pasujący CMake, jeśli masz komponent `cmake` z SDK).
- `minSdk` 26, więc kompilacje działają na Androidzie 8.0 i nowszym.

## Szybki start

```sh
# build both flavors, release
./gradlew assembleRelease
```

To produkuje **dwa pliki APK**, po jednym na flavor:

- **modern** (`SDK_35`) - ścisła nowoczesna piaskownica, kompilacja do użycia.
- **compat** (`SDK_27`) - starszy cel API, wyłącznie do porównania obok siebie.

Wyniki lądują w:

```
app/build/outputs/apk/modern/release/VDInfos-v<ver>-SDK_35-release.apk
app/build/outputs/apk/compat/release/VDInfos-v<ver>-SDK_27-release.apk
```

Aby zbudować tylko jeden flavor podczas iterowania:

```sh
./gradlew assembleModernRelease   # SDK_35 only
./gradlew assembleCompatRelease   # SDK_27 only
```

## Podpisywanie

Kompilacje release są podpisywane kluczem, który jest **lokalny i niezacommitowany** (keystore
i jego właściwości są ignorowane przez git). Bez niego zadanie release nadal buduje, ale
APK wychodzi niepodpisane. Dostarcz własny keystore, aby podpisać własne kompilacje.

## Dwa flavory

Aplikacja dostarcza dwa flavory celowo. Celują w różne poziomy API i budują z tego
samego źródła; różni się tylko cel i kilka strzeżonych ścieżek kodu. Zmiana,
która dotyka kodu natywnego lub ścieżki specyficznej dla flavora, powinna być zbudowana i sprawdzona w
**obu** flavorach, nie tylko w tym, nad którym iterujesz.

## Natywna soczewka

Natywna soczewka to pojedyncza, pozbawiona zależności jednostka translacji C++ budowana przez NDK jako
jedno małe `.so`, związane po nazwie przez `RegisterNatives`. Jest kompilowana jako część
normalnej kompilacji Gradle; nie ma oddzielnego kroku. Jest utrzymana celowo maleńka i prosta.

## Gdzie co się mieści

```
app/src/main/java/ru/vd171/vdinfos/   Kotlin: model, probes, engine, data, ui
app/src/main/cpp/                     the native lens
app/src/main/assets/data/             the plain-text detection lists (see Contributing)
```

Zobacz [Architektura](ARCHITECTURE.md), aby dowiedzieć się, jak elementy do siebie pasują, oraz
[Współtworzenie](CONTRIBUTING.md), aby dowiedzieć się, jak dodać cel wykrywania bez dotykania kodu.

<!-- doc-footer -->

---

> _Ta dokumentacja została wygenerowana za pomocą Claude (Anthropic) i jest utrzymywana przez VD171. Poprawki i ulepszenia są mile widziane; zobacz [CONTRIBUTING.md](CONTRIBUTING.md)._
