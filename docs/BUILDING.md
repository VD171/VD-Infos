> **Languages:** English · [العربية](ar/BUILDING.md) · [Čeština](cs/BUILDING.md) · [Deutsch](de/BUILDING.md) · [Español](es/BUILDING.md) · [فارسی](fa/BUILDING.md) · [Français](fr/BUILDING.md) · [हिन्दी](hi/BUILDING.md) · [Bahasa Indonesia](id/BUILDING.md) · [Italiano](it/BUILDING.md) · [日本語](ja/BUILDING.md) · [한국어](ko/BUILDING.md) · [Nederlands](nl/BUILDING.md) · [Polski](pl/BUILDING.md) · [Português](pt/BUILDING.md) · [Русский](ru/BUILDING.md) · [Svenska](sv/BUILDING.md) · [ไทย](th/BUILDING.md) · [Türkçe](tr/BUILDING.md) · [Українська](uk/BUILDING.md) · [Tiếng Việt](vi/BUILDING.md) · [中文](zh/BUILDING.md)

---

# Building VD Infos

VD Infos is a standard Android Gradle project: Kotlin and Jetpack Compose, with a small
native lens in C++ built through the NDK. CI can build it, and you can reproduce it
locally.

## Requirements

- A JDK (17 to 21) and the Android SDK.
- The Android NDK and CMake, for the native lens (the Gradle Android plugin fetches a
  matching CMake if you have the SDK's `cmake` component).
- `minSdk` 26, so builds run on Android 8.0 and newer.

## Quick start

```sh
# build both flavors, release
./gradlew assembleRelease
```

This produces **two APKs**, one per flavor:

- **modern** (`SDK_35`) - the strict modern sandbox, the build to use.
- **compat** (`SDK_27`) - an older target with a looser SELinux domain, for
  side-by-side comparison only.

The outputs land in:

```
app/build/outputs/apk/modern/release/VDInfos-v<ver>-SDK_35-release.apk
app/build/outputs/apk/compat/release/VDInfos-v<ver>-SDK_27-release.apk
```

To build just one flavor while iterating:

```sh
./gradlew assembleModernRelease   # SDK_35 only
./gradlew assembleCompatRelease   # SDK_27 only
```

## Signing

Release builds are signed with a key that is **local and not committed** (the keystore
and its properties are ignored by git). Without it, the release task still builds but
the APK comes out unsigned. Provide your own keystore to sign your own builds.

## The two flavors

The app ships two flavors on purpose. They target different API levels and therefore
fall into different Android sandbox domains, which is itself useful for comparison. Both
build from the same source; only the target and a few guarded code paths differ. A
change that touches native code or a flavor-specific path should be built and checked in
**both** flavors, not just the one you are iterating on.

## The native lens

The native lens is a single, dependency-free C++ translation unit built by the NDK as
one small `.so`, bound by name through `RegisterNatives`. It is compiled as part of the
normal Gradle build; there is no separate step. It is kept deliberately tiny because it
is the part that must be hard to fool.

## Where things live

```
app/src/main/java/ru/vd171/vdinfos/   Kotlin: model, probes, engine, data, ui
app/src/main/cpp/                     the native lens
app/src/main/assets/data/             the plain-text detection lists (see Contributing)
```

See [Architecture](ARCHITECTURE.md) for how the pieces fit together, and
[Contributing](CONTRIBUTING.md) for how to add a detection target without touching code.

<!-- doc-footer -->

---

> _This documentation was generated with Claude (Anthropic) and is maintained by VD171. Corrections and improvements are welcome; see [CONTRIBUTING.md](CONTRIBUTING.md)._
