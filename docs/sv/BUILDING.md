> **Languages:** [English](../BUILDING.md) · [العربية](../ar/BUILDING.md) · [Čeština](../cs/BUILDING.md) · [Deutsch](../de/BUILDING.md) · [Español](../es/BUILDING.md) · [فارسی](../fa/BUILDING.md) · [Français](../fr/BUILDING.md) · [हिन्दी](../hi/BUILDING.md) · [Bahasa Indonesia](../id/BUILDING.md) · [Italiano](../it/BUILDING.md) · [日本語](../ja/BUILDING.md) · [한국어](../ko/BUILDING.md) · [Nederlands](../nl/BUILDING.md) · [Polski](../pl/BUILDING.md) · [Português](../pt/BUILDING.md) · [Русский](../ru/BUILDING.md) · Svenska · [ไทย](../th/BUILDING.md) · [Türkçe](../tr/BUILDING.md) · [Українська](../uk/BUILDING.md) · [Tiếng Việt](../vi/BUILDING.md) · [中文](../zh/BUILDING.md)

---

# Bygga VD Infos

VD Infos är ett vanligt Android Gradle-projekt: Kotlin och Jetpack Compose, med en liten nativ lins i
C++ byggd genom NDK. CI kan bygga det, och du kan återskapa det lokalt.

## Krav

- En JDK (17 till 21) och Android SDK.
- Android NDK och CMake, för den nativa linsen (Gradle Android-plugin hämtar en matchande CMake om du
  har SDK:ns `cmake`-komponent).
- `minSdk` 26, så bygg körs på Android 8.0 och nyare.

## Snabbstart

```sh
# build both flavors, release
./gradlew assembleRelease
```

Detta producerar **två APK:er**, en per flavor:

- **modern** (`SDK_35`) - den strikta moderna sandlådan, bygget att använda.
- **compat** (`SDK_27`) - ett äldre API-mål, endast för jämförelse sida vid
  sida.

Utdata landar i:

```
app/build/outputs/apk/modern/release/VDInfos-v<ver>-SDK_35-release.apk
app/build/outputs/apk/compat/release/VDInfos-v<ver>-SDK_27-release.apk
```

För att bygga bara en flavor medan du itererar:

```sh
./gradlew assembleModernRelease   # SDK_35 only
./gradlew assembleCompatRelease   # SDK_27 only
```

## Signering

Release-bygg signeras med en nyckel som är **lokal och inte committad** (keystore:n och dess
egenskaper ignoreras av git). Utan den bygger release-uppgiften fortfarande men APK:n kommer ut
osignerad. Tillhandahåll din egen keystore för att signera dina egna bygg.

## De två flavorerna

Appen levererar två flavorer med avsikt. De riktar sig mot olika API-nivåer och bygger från samma
källa; endast målet och några få bevakade kodvägar skiljer sig. En ändring som rör nativ kod eller en
flavor-specifik väg bör byggas och kontrolleras i **båda** flavorerna, inte bara den du itererar på.

## Den nativa linsen

Den nativa linsen är en enda, beroendefri C++-översättningsenhet byggd av NDK som en liten `.so`,
bunden med namn genom `RegisterNatives`. Den kompileras som en del av det normala Gradle-bygget; det
finns inget separat steg. Den hålls medvetet pytteliten och enkel.

## Var saker bor

```
app/src/main/java/ru/vd171/vdinfos/   Kotlin: model, probes, engine, data, ui
app/src/main/cpp/                     the native lens
app/src/main/assets/data/             the plain-text detection lists (see Contributing)
```

Se [Arkitektur](ARCHITECTURE.md) för hur delarna passar ihop, och [Bidra](CONTRIBUTING.md) för hur
man lägger till ett detekteringsmål utan att röra kod.

<!-- doc-footer -->

---

> _Denna dokumentation genererades med Claude (Anthropic) och underhålls av VD171. Rättelser och förbättringar välkomnas; se [CONTRIBUTING.md](CONTRIBUTING.md)._
