> **Languages:** [English](../BUILDING.md) · [العربية](../ar/BUILDING.md) · [Čeština](../cs/BUILDING.md) · [Deutsch](../de/BUILDING.md) · [Español](../es/BUILDING.md) · [فارسی](../fa/BUILDING.md) · [Français](../fr/BUILDING.md) · [हिन्दी](../hi/BUILDING.md) · [Bahasa Indonesia](../id/BUILDING.md) · [Italiano](../it/BUILDING.md) · [日本語](../ja/BUILDING.md) · [한국어](../ko/BUILDING.md) · [Nederlands](../nl/BUILDING.md) · [Polski](../pl/BUILDING.md) · [Português](../pt/BUILDING.md) · [Русский](../ru/BUILDING.md) · [Svenska](../sv/BUILDING.md) · [ไทย](../th/BUILDING.md) · [Türkçe](../tr/BUILDING.md) · [Українська](../uk/BUILDING.md) · [Tiếng Việt](../vi/BUILDING.md) · 中文

---

# 构建 VD Infos

VD Infos 是一个标准的 Android Gradle 项目：Kotlin 和 Jetpack Compose，带有一个通过 NDK 构建的小型
C++ 原生镜头。CI 能构建它，你也能在本地复现它。

## 要求

- 一个 JDK（17 到 21）和 Android SDK。
- Android NDK 和 CMake，用于原生镜头（如果你有 SDK 的 `cmake` 组件，Gradle Android 插件会取得一个匹配
  的 CMake）。
- `minSdk` 26，因此构建产物运行在 Android 8.0 及更新版本上。

## 快速开始

```sh
# build both flavors, release
./gradlew assembleRelease
```

这会产生**两个 APK**，每个 flavor 一个：

- **modern**（`SDK_35`） - 严格的现代沙箱，该用的构建。
- **compat**（`SDK_27`） - 一个更旧的目标，具有更宽松的 SELinux 域，仅用于并排比较。

输出产物落在：

```
app/build/outputs/apk/modern/release/VDInfos-v<ver>-SDK_35-release.apk
app/build/outputs/apk/compat/release/VDInfos-v<ver>-SDK_27-release.apk
```

要在迭代时只构建一个 flavor：

```sh
./gradlew assembleModernRelease   # SDK_35 only
./gradlew assembleCompatRelease   # SDK_27 only
```

## 签名

release 构建用一个**本地且未提交**的密钥签名（keystore 及其属性被 git 忽略）。没有它，release 任务仍会
构建，但 APK 出来是未签名的。请提供你自己的 keystore 来给你自己的构建签名。

## 两个 flavor

应用有意地随附两个 flavor。它们面向不同的 API 级别，因此落入不同的 Android 沙箱域，这本身就对比较有用。
两者都从同一份源码构建；只有目标和少数受保护的代码路径不同。一个触及原生代码或某个 flavor 特定路径的改
动，应当在**两个** flavor 上都构建并检查，而不只是你正在迭代的那一个。

## 原生镜头

原生镜头是单一的、无依赖的 C++ 翻译单元，由 NDK 构建为一个小小的 `.so`，通过 `RegisterNatives` 按名称
绑定。它作为普通 Gradle 构建的一部分被编译；没有单独的步骤。它被刻意保持得极小，因为它是那个必须难以被
骗过的部分。

## 各处都放着什么

```
app/src/main/java/ru/vd171/vdinfos/   Kotlin: model, probes, engine, data, ui
app/src/main/cpp/                     the native lens
app/src/main/assets/data/             the plain-text detection lists (see Contributing)
```

参见 [架构](ARCHITECTURE.md) 了解各部分如何拼合在一起，以及
[贡献](CONTRIBUTING.md) 了解如何在不触碰代码的情况下添加一个检测目标。

<!-- doc-footer -->

---

> _本文档由 Claude (Anthropic) 生成，并由 VD171 维护。欢迎修正和改进；请参阅 [CONTRIBUTING.md](CONTRIBUTING.md)。_
