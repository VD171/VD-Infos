> **Languages:** [English](../BUILDING.md) · [العربية](../ar/BUILDING.md) · [Čeština](../cs/BUILDING.md) · [Deutsch](../de/BUILDING.md) · [Español](../es/BUILDING.md) · فارسی · [Français](../fr/BUILDING.md) · [हिन्दी](../hi/BUILDING.md) · [Bahasa Indonesia](../id/BUILDING.md) · [Italiano](../it/BUILDING.md) · [日本語](../ja/BUILDING.md) · [한국어](../ko/BUILDING.md) · [Nederlands](../nl/BUILDING.md) · [Polski](../pl/BUILDING.md) · [Português](../pt/BUILDING.md) · [Русский](../ru/BUILDING.md) · [Svenska](../sv/BUILDING.md) · [ไทย](../th/BUILDING.md) · [Türkçe](../tr/BUILDING.md) · [Українська](../uk/BUILDING.md) · [Tiếng Việt](../vi/BUILDING.md) · [中文](../zh/BUILDING.md)

---

# ساختن VD Infos

VD Infos یک پروژهٔ استاندارد Android Gradle است: Kotlin و Jetpack Compose، با یک عدسی
بومی کوچک در C++ که از طریق NDK ساخته می‌شود. CI می‌تواند آن را بسازد، و می‌توانید آن را
به‌صورت محلی بازتولید کنید.

## پیش‌نیازها

- یک JDK (17 تا 21) و Android SDK.
- Android NDK و CMake، برای عدسی بومی (پلاگین Android Gradle یک
  CMake همخوان را واکشی می‌کند اگر مؤلفهٔ `cmake` از SDK را داشته باشید).
- `minSdk` 26، پس ساخت‌ها روی اندروید 8.0 و جدیدتر اجرا می‌شوند.

## شروع سریع

```sh
# build both flavors, release
./gradlew assembleRelease
```

این **دو APK** تولید می‌کند، یکی به‌ازای هر flavor:

- **modern** (`SDK_35`) - جعبهٔ شنیِ مدرن و سخت‌گیر، ساختی که باید استفاده کنید.
- **compat** (`SDK_27`) - یک هدف API قدیمی‌تر، تنها برای مقایسهٔ پهلوبه‌پهلو.

خروجی‌ها در اینجا فرود می‌آیند:

```
app/build/outputs/apk/modern/release/VDInfos-v<ver>-SDK_35-release.apk
app/build/outputs/apk/compat/release/VDInfos-v<ver>-SDK_27-release.apk
```

برای ساختن تنها یک flavor هنگام تکرار:

```sh
./gradlew assembleModernRelease   # SDK_35 only
./gradlew assembleCompatRelease   # SDK_27 only
```

## امضا

ساخت‌های release با کلیدی امضا می‌شوند که **محلی و نسخه‌نشده** است (keystore
و ویژگی‌هایش به‌دست git نادیده گرفته می‌شوند). بدون آن، وظیفهٔ release همچنان می‌سازد اما
APK بدون امضا بیرون می‌آید. keystore خودتان را فراهم کنید تا ساخت‌های خودتان را امضا کنید.

## دو flavor

برنامه از روی قصد دو flavor را منتشر می‌کند. آن‌ها سطوح API متفاوتی را هدف می‌گیرند و
از همان منبع ساخته می‌شوند؛ تنها هدف و چند مسیر کد محافظت‌شده تفاوت دارند. یک
تغییر که کد بومی یا مسیری خاص یک flavor را لمس کند باید در
**هر دو** flavor ساخته و بررسی شود، نه فقط آنکه روی آن تکرار می‌کنید.

## عدسی بومی

عدسی بومی یک واحد ترجمهٔ C++ یکتا و بدون‌وابستگی است که به‌دست NDK به‌صورت
یک `.so` کوچک ساخته می‌شود، که با نام از طریق `RegisterNatives` بسته می‌شود. به‌عنوان بخشی از
ساخت عادی Gradle کامپایل می‌شود؛ گام جداگانه‌ای نیست. عمداً بسیار کوچک و ساده نگه داشته شده است.

## چیزها کجا زندگی می‌کنند

```
app/src/main/java/ru/vd171/vdinfos/   Kotlin: model, probes, engine, data, ui
app/src/main/cpp/                     the native lens
app/src/main/assets/data/             the plain-text detection lists (see Contributing)
```

برای اینکه قطعه‌ها چگونه کنار هم جا می‌افتند [معماری](ARCHITECTURE.md) را ببینید، و
برای اینکه چگونه یک هدف تشخیص را بدون لمس کد بیفزایید [مشارکت](CONTRIBUTING.md) را ببینید.

<!-- doc-footer -->

---

> _این مستندات با Claude (Anthropic) تولید شده و توسط VD171 نگهداری می‌شود. اصلاحات و بهبودها خوش‌آمد است؛ به [CONTRIBUTING.md](CONTRIBUTING.md) مراجعه کنید._
