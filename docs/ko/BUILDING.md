> **Languages:** [English](../BUILDING.md) · [العربية](../ar/BUILDING.md) · [Čeština](../cs/BUILDING.md) · [Deutsch](../de/BUILDING.md) · [Español](../es/BUILDING.md) · [فارسی](../fa/BUILDING.md) · [Français](../fr/BUILDING.md) · [हिन्दी](../hi/BUILDING.md) · [Bahasa Indonesia](../id/BUILDING.md) · [Italiano](../it/BUILDING.md) · [日本語](../ja/BUILDING.md) · 한국어 · [Nederlands](../nl/BUILDING.md) · [Polski](../pl/BUILDING.md) · [Português](../pt/BUILDING.md) · [Русский](../ru/BUILDING.md) · [Svenska](../sv/BUILDING.md) · [ไทย](../th/BUILDING.md) · [Türkçe](../tr/BUILDING.md) · [Українська](../uk/BUILDING.md) · [Tiếng Việt](../vi/BUILDING.md) · [中文](../zh/BUILDING.md)

---

# VD Infos 빌드하기

VD Infos는 표준 Android Gradle 프로젝트입니다. Kotlin과 Jetpack Compose에, NDK를 통해 빌드되는
작은 C++ 네이티브 렌즈를 더한 것입니다. CI가 빌드할 수 있고, 로컬에서도 재현할 수 있습니다.

## 요구 사항

- JDK (17에서 21) 와 Android SDK.
- 네이티브 렌즈를 위한 Android NDK와 CMake (SDK의 `cmake` 컴포넌트가 있으면 Gradle의 Android
  플러그인이 맞는 CMake를 가져옵니다).
- `minSdk` 26, 그래서 빌드는 Android 8.0 이상에서 동작합니다.

## 빠른 시작

```sh
# build both flavors, release
./gradlew assembleRelease
```

이것은 플레이버마다 하나씩, **두 개의 APK** 를 생성합니다.

- **modern** (`SDK_35`) - 엄격한 현대 샌드박스, 사용할 빌드.
- **compat** (`SDK_27`) - 더 느슨한 SELinux 도메인을 가진 더 오래된 타겟, 나란히 비교하는
  용도로만.

출력은 다음 위치에 생성됩니다.

```
app/build/outputs/apk/modern/release/VDInfos-v<ver>-SDK_35-release.apk
app/build/outputs/apk/compat/release/VDInfos-v<ver>-SDK_27-release.apk
```

반복 작업 중에 한 플레이버만 빌드하려면:

```sh
./gradlew assembleModernRelease   # SDK_35 only
./gradlew assembleCompatRelease   # SDK_27 only
```

## 서명

release 빌드는 **로컬에 있고 커밋되지 않는** 키로 서명됩니다 (키스토어와 그 속성은 git이
무시합니다). 그것이 없어도 release 작업은 여전히 빌드되지만 APK는 서명되지 않은 채로 나옵니다.
자신의 빌드에 서명하려면 자신의 키스토어를 마련하세요.

## 두 플레이버

앱은 의도적으로 두 플레이버를 출시합니다. 서로 다른 API 레벨을 대상으로 하므로 서로 다른
Android 샌드박스 도메인에 들어가며, 그것 자체가 비교에 유용합니다. 둘 다 같은 소스에서
빌드됩니다. 다른 것은 타겟과 몇 개의 가드된 코드 경로뿐입니다. 네이티브 코드나 플레이버별
경로를 건드리는 변경은, 반복하고 있는 한쪽만이 아니라 **양쪽** 플레이버에서 빌드하고 확인해야
합니다.

## 네이티브 렌즈

네이티브 렌즈는 NDK가 하나의 작은 `.so` 로 빌드하는 단일한 의존성 없는 C++ 번역 단위이며,
`RegisterNatives` 를 통해 이름으로 바인딩됩니다. 일반 Gradle 빌드의 일부로 컴파일됩니다.
별도의 단계는 없습니다. 속이기 어려워야 하는 부분이므로 의도적으로 아주 작게 유지됩니다.

## 무엇이 어디에 있는가

```
app/src/main/java/ru/vd171/vdinfos/   Kotlin: model, probes, engine, data, ui
app/src/main/cpp/                     the native lens
app/src/main/assets/data/             the plain-text detection lists (see Contributing)
```

각 조각이 어떻게 맞물리는지는 [아키텍처](ARCHITECTURE.md) 를, 코드를 건드리지 않고 탐지 대상을
추가하는 법은 [기여하기](CONTRIBUTING.md) 를 보세요.

<!-- doc-footer -->

---

> _이 문서는 Claude (Anthropic)로 생성되었으며 VD171이 유지 관리합니다. 수정 및 개선을 환영합니다. [CONTRIBUTING.md](CONTRIBUTING.md)를 참조하세요._
