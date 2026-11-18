> **Languages:** [English](../BUILDING.md) · [العربية](../ar/BUILDING.md) · [Čeština](../cs/BUILDING.md) · [Deutsch](../de/BUILDING.md) · [Español](../es/BUILDING.md) · [فارسی](../fa/BUILDING.md) · [Français](../fr/BUILDING.md) · [हिन्दी](../hi/BUILDING.md) · Bahasa Indonesia · [Italiano](../it/BUILDING.md) · [日本語](../ja/BUILDING.md) · [한국어](../ko/BUILDING.md) · [Nederlands](../nl/BUILDING.md) · [Polski](../pl/BUILDING.md) · [Português](../pt/BUILDING.md) · [Русский](../ru/BUILDING.md) · [Svenska](../sv/BUILDING.md) · [ไทย](../th/BUILDING.md) · [Türkçe](../tr/BUILDING.md) · [Українська](../uk/BUILDING.md) · [Tiếng Việt](../vi/BUILDING.md) · [中文](../zh/BUILDING.md)

---

# Membangun VD Infos

VD Infos adalah proyek Android Gradle standar: Kotlin dan Jetpack Compose, dengan lensa nativa
kecil dalam C++ yang dibangun melalui NDK. CI dapat membangunnya, dan Anda dapat mereproduksinya
secara lokal.

## Persyaratan

- Sebuah JDK (17 hingga 21) dan Android SDK.
- Android NDK dan CMake, untuk lensa nativa (plugin Android Gradle mengambil
  CMake yang cocok jika Anda memiliki komponen `cmake` dari SDK).
- `minSdk` 26, sehingga build berjalan pada Android 8.0 dan lebih baru.

## Mulai cepat

```sh
# build both flavors, release
./gradlew assembleRelease
```

Ini menghasilkan **dua APK**, satu per flavor:

- **modern** (`SDK_35`) - sandbox modern yang ketat, build yang harus digunakan.
- **compat** (`SDK_27`) - target lebih lama dengan domain SELinux yang lebih longgar, hanya untuk
  perbandingan berdampingan.

Keluaran mendarat di:

```
app/build/outputs/apk/modern/release/VDInfos-v<ver>-SDK_35-release.apk
app/build/outputs/apk/compat/release/VDInfos-v<ver>-SDK_27-release.apk
```

Untuk membangun hanya satu flavor selagi beriterasi:

```sh
./gradlew assembleModernRelease   # SDK_35 only
./gradlew assembleCompatRelease   # SDK_27 only
```

## Penandatanganan

Build release ditandatangani dengan kunci yang **lokal dan tidak di-commit** (keystore
dan propertinya diabaikan oleh git). Tanpa itu, task release tetap membangun tetapi
APK keluar tanpa tanda tangan. Sediakan keystore Anda sendiri untuk menandatangani build Anda sendiri.

## Kedua flavor

Aplikasi mengapalkan dua flavor dengan sengaja. Mereka menargetkan level API yang berbeda dan oleh karena itu
jatuh ke domain sandbox Android yang berbeda, yang itu sendiri berguna untuk perbandingan. Keduanya
dibangun dari sumber yang sama; hanya target dan beberapa jalur kode yang dijaga yang berbeda. Sebuah
perubahan yang menyentuh kode nativa atau jalur spesifik flavor harus dibangun dan diperiksa di
**kedua** flavor, bukan hanya yang sedang Anda iterasi.

## Lensa nativa

Lensa nativa adalah satu unit terjemahan C++ tunggal tanpa dependensi yang dibangun oleh NDK sebagai
satu `.so` kecil, diikat berdasarkan nama melalui `RegisterNatives`. Ia dikompilasi sebagai bagian dari
build Gradle normal; tidak ada langkah terpisah. Ia dijaga dengan sengaja mungil karena
ia adalah bagian yang harus sulit dikelabui.

## Di mana hal-hal berada

```
app/src/main/java/ru/vd171/vdinfos/   Kotlin: model, probes, engine, data, ui
app/src/main/cpp/                     the native lens
app/src/main/assets/data/             the plain-text detection lists (see Contributing)
```

Lihat [Arsitektur](ARCHITECTURE.md) untuk bagaimana bagian-bagian saling cocok, dan
[Berkontribusi](CONTRIBUTING.md) untuk cara menambahkan target deteksi tanpa menyentuh kode.

<!-- doc-footer -->

---

> _Dokumentasi ini dibuat dengan Claude (Anthropic) dan dikelola oleh VD171. Koreksi dan perbaikan dipersilakan; lihat [CONTRIBUTING.md](CONTRIBUTING.md)._
