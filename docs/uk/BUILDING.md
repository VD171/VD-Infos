> **Languages:** [English](../BUILDING.md) · [العربية](../ar/BUILDING.md) · [Čeština](../cs/BUILDING.md) · [Deutsch](../de/BUILDING.md) · [Español](../es/BUILDING.md) · [فارسی](../fa/BUILDING.md) · [Français](../fr/BUILDING.md) · [हिन्दी](../hi/BUILDING.md) · [Bahasa Indonesia](../id/BUILDING.md) · [Italiano](../it/BUILDING.md) · [日本語](../ja/BUILDING.md) · [한국어](../ko/BUILDING.md) · [Nederlands](../nl/BUILDING.md) · [Polski](../pl/BUILDING.md) · [Português](../pt/BUILDING.md) · [Русский](../ru/BUILDING.md) · [Svenska](../sv/BUILDING.md) · [ไทย](../th/BUILDING.md) · [Türkçe](../tr/BUILDING.md) · Українська · [Tiếng Việt](../vi/BUILDING.md) · [中文](../zh/BUILDING.md)

---

# Збірка VD Infos

VD Infos - це стандартний проєкт Android Gradle: Kotlin і Jetpack Compose, з невеликою нативною
лінзою на C++, зібраною через NDK. CI може зібрати його, і ви можете відтворити його локально.

## Вимоги

- JDK (17 до 21) та Android SDK.
- Android NDK і CMake, для нативної лінзи (плагін Gradle Android дістає відповідний CMake, якщо у
  вас є компонент `cmake` із SDK).
- `minSdk` 26, тож збірки працюють на Android 8.0 і новіших.

## Швидкий старт

```sh
# build both flavors, release
./gradlew assembleRelease
```

Це створює **два APK**, по одному на флейвор:

- **modern** (`SDK_35`) - строга сучасна пісочниця, збірка для використання.
- **compat** (`SDK_27`) - старіша ціль з більш вільним доменом SELinux, лише для порівняння
  пліч-о-пліч.

Вихідні файли опиняються в:

```
app/build/outputs/apk/modern/release/VDInfos-v<ver>-SDK_35-release.apk
app/build/outputs/apk/compat/release/VDInfos-v<ver>-SDK_27-release.apk
```

Щоб зібрати лише один флейвор під час ітерування:

```sh
./gradlew assembleModernRelease   # SDK_35 only
./gradlew assembleCompatRelease   # SDK_27 only
```

## Підписування

Release-збірки підписуються ключем, який є **локальним і не закомічений** (keystore та його
властивості ігноруються git). Без нього завдання release усе одно збирається, але APK виходить
непідписаним. Надайте власний keystore, щоб підписати власні збірки.

## Два флейвори

Застосунок постачається у двох флейворах навмисно. Вони націлені на різні рівні API й тому
потрапляють до різних доменів пісочниці Android, що саме по собі корисно для порівняння. Обидва
збираються з того самого джерельного коду; відрізняються лише ціль і кілька захищених шляхів коду.
Зміна, що торкається нативного коду чи специфічного для флейвора шляху, має бути зібрана й
перевірена в **обох** флейворах, а не лише в тому, у якому ви ітеруєте.

## Нативна лінза

Нативна лінза - це єдина, вільна від залежностей одиниця трансляції C++, зібрана NDK як один маленький `.so`,
прив'язаний за іменем через `RegisterNatives`. Вона компілюється як частина звичайної збірки
Gradle; окремого кроку немає. Її навмисно тримають крихітною, бо це частина, яку має бути важко
обдурити.

## Де що живе

```
app/src/main/java/ru/vd171/vdinfos/   Kotlin: model, probes, engine, data, ui
app/src/main/cpp/                     the native lens
app/src/main/assets/data/             the plain-text detection lists (see Contributing)
```

Див. [Архітектуру](ARCHITECTURE.md) про те, як частини поєднуються докупи, та
[Внесок](CONTRIBUTING.md) про те, як додати ціль виявлення, не торкаючись коду.

<!-- doc-footer -->

---

> _Ця документація створена за допомогою Claude (Anthropic) і підтримується VD171. Виправлення та покращення вітаються; див. [CONTRIBUTING.md](CONTRIBUTING.md)._
