> **Languages:** [English](../BUILDING.md) · [العربية](../ar/BUILDING.md) · [Čeština](../cs/BUILDING.md) · [Deutsch](../de/BUILDING.md) · [Español](../es/BUILDING.md) · [فارسی](../fa/BUILDING.md) · [Français](../fr/BUILDING.md) · [हिन्दी](../hi/BUILDING.md) · [Bahasa Indonesia](../id/BUILDING.md) · [Italiano](../it/BUILDING.md) · [日本語](../ja/BUILDING.md) · [한국어](../ko/BUILDING.md) · [Nederlands](../nl/BUILDING.md) · [Polski](../pl/BUILDING.md) · [Português](../pt/BUILDING.md) · Русский · [Svenska](../sv/BUILDING.md) · [ไทย](../th/BUILDING.md) · [Türkçe](../tr/BUILDING.md) · [Українська](../uk/BUILDING.md) · [Tiếng Việt](../vi/BUILDING.md) · [中文](../zh/BUILDING.md)

---

# Сборка VD Infos

VD Infos - это стандартный проект Android Gradle: Kotlin и Jetpack Compose, с небольшой
нативной линзой на C++, собираемой через NDK. CI может собрать его, и вы можете воспроизвести его
локально.

## Требования

- JDK (от 17 до 21) и Android SDK.
- Android NDK и CMake, для нативной линзы (плагин Android для Gradle подтягивает
  совместимый CMake, если у вас есть компонент `cmake` из SDK).
- `minSdk` 26, так что сборки работают на Android 8.0 и новее.

## Быстрый старт

```sh
# build both flavors, release
./gradlew assembleRelease
```

Это производит **два APK**, по одному на flavor:

- **modern** (`SDK_35`) - строгая современная песочница, сборка для использования.
- **compat** (`SDK_27`) - более старая цель с менее строгим доменом SELinux, только для
  сравнения бок о бок.

Выходные файлы оказываются в:

```
app/build/outputs/apk/modern/release/VDInfos-v<ver>-SDK_35-release.apk
app/build/outputs/apk/compat/release/VDInfos-v<ver>-SDK_27-release.apk
```

Чтобы собрать только один flavor во время итераций:

```sh
./gradlew assembleModernRelease   # SDK_35 only
./gradlew assembleCompatRelease   # SDK_27 only
```

## Подписание

Сборки release подписываются ключом, который является **локальным и не коммитится** (keystore
и его свойства игнорируются git). Без него задача release всё равно собирается, но
APK выходит неподписанным. Предоставьте собственный keystore, чтобы подписывать собственные сборки.

## Два flavor

Приложение поставляет два flavor намеренно. Они нацелены на разные уровни API и потому
попадают в разные домены песочницы Android, что само по себе полезно для сравнения. Оба
собираются из одного исходного кода; различаются только цель и несколько защищённых путей кода. Изменение,
которое затрагивает нативный код или путь, специфичный для flavor, должно быть собрано и проверено в
**обоих** flavor, а не только в том, над которым вы итерируете.

## Нативная линза

Нативная линза - это единая, свободная от зависимостей единица трансляции C++, собираемая NDK как
один небольшой `.so`, привязанный по имени через `RegisterNatives`. Она компилируется как часть
обычной сборки Gradle; отдельного шага нет. Она намеренно держится крошечной, потому что
это часть, которую должно быть трудно обмануть.

## Где что находится

```
app/src/main/java/ru/vd171/vdinfos/   Kotlin: model, probes, engine, data, ui
app/src/main/cpp/                     the native lens
app/src/main/assets/data/             the plain-text detection lists (see Contributing)
```

Смотрите [Архитектура](ARCHITECTURE.md) о том, как части складываются вместе, и
[Участие](CONTRIBUTING.md) о том, как добавить цель обнаружения, не трогая код.

<!-- doc-footer -->

---

> _Эта документация создана с помощью Claude (Anthropic) и поддерживается VD171. Исправления и улучшения приветствуются; см. [CONTRIBUTING.md](CONTRIBUTING.md)._
