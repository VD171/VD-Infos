> **Languages:** [English](../BUILDING.md) · [العربية](../ar/BUILDING.md) · [Čeština](../cs/BUILDING.md) · [Deutsch](../de/BUILDING.md) · Español · [فارسی](../fa/BUILDING.md) · [Français](../fr/BUILDING.md) · [हिन्दी](../hi/BUILDING.md) · [Bahasa Indonesia](../id/BUILDING.md) · [Italiano](../it/BUILDING.md) · [日本語](../ja/BUILDING.md) · [한국어](../ko/BUILDING.md) · [Nederlands](../nl/BUILDING.md) · [Polski](../pl/BUILDING.md) · [Português](../pt/BUILDING.md) · [Русский](../ru/BUILDING.md) · [Svenska](../sv/BUILDING.md) · [ไทย](../th/BUILDING.md) · [Türkçe](../tr/BUILDING.md) · [Українська](../uk/BUILDING.md) · [Tiếng Việt](../vi/BUILDING.md) · [中文](../zh/BUILDING.md)

---

# Compilar VD Infos

VD Infos es un proyecto Android Gradle estándar: Kotlin y Jetpack Compose, con una pequeña
lente nativa en C++ compilada a través del NDK. CI puede compilarlo, y puedes reproducirlo
localmente.

## Requisitos

- Un JDK (17 a 21) y el Android SDK.
- El Android NDK y CMake, para la lente nativa (el plugin Android de Gradle obtiene un
  CMake compatible si tienes el componente `cmake` del SDK).
- `minSdk` 26, así que las compilaciones corren en Android 8.0 y más reciente.

## Inicio rápido

```sh
# build both flavors, release
./gradlew assembleRelease
```

Esto produce **dos APK**, uno por flavor:

- **modern** (`SDK_35`) - la sandbox moderna y estricta, la compilación a usar.
- **compat** (`SDK_27`) - un objetivo de API más antiguo, solo para comparación lado a lado.

Las salidas aterrizan en:

```
app/build/outputs/apk/modern/release/VDInfos-v<ver>-SDK_35-release.apk
app/build/outputs/apk/compat/release/VDInfos-v<ver>-SDK_27-release.apk
```

Para compilar solo un flavor mientras iteras:

```sh
./gradlew assembleModernRelease   # SDK_35 only
./gradlew assembleCompatRelease   # SDK_27 only
```

## Firma

Las compilaciones release se firman con una clave que es **local y no versionada** (el keystore
y sus propiedades están ignorados por git). Sin ella, la tarea release de todos modos compila pero
el APK sale sin firmar. Provee tu propio keystore para firmar tus propias compilaciones.

## Los dos flavors

La aplicación distribuye dos flavors a propósito. Apuntan a distintos niveles de API y
compilan desde la misma fuente; solo difieren el objetivo y unas pocas rutas de código protegidas. Un
cambio que toca código nativo o una ruta específica de un flavor debe compilarse y verificarse en
**ambos** flavors, no solo en el que estás iterando.

## La lente nativa

La lente nativa es una única unidad de traducción C++ sin dependencias compilada por el NDK como
un pequeño `.so`, vinculada por nombre a través de `RegisterNatives`. Se compila como parte de la
compilación normal de Gradle; no hay un paso separado. Se mantiene deliberadamente diminuta y simple.

## Dónde viven las cosas

```
app/src/main/java/ru/vd171/vdinfos/   Kotlin: model, probes, engine, data, ui
app/src/main/cpp/                     the native lens
app/src/main/assets/data/             the plain-text detection lists (see Contributing)
```

Consulta [Arquitectura](ARCHITECTURE.md) para saber cómo encajan las piezas, y
[Contribuir](CONTRIBUTING.md) para saber cómo añadir un objetivo de detección sin tocar código.

<!-- doc-footer -->

---

> _Esta documentación se generó con Claude (Anthropic) y la mantiene VD171. Las correcciones y mejoras son bienvenidas; consulta [CONTRIBUTING.md](CONTRIBUTING.md)._
