> **Languages:** [English](../BUILDING.md) · [العربية](../ar/BUILDING.md) · [Čeština](../cs/BUILDING.md) · [Deutsch](../de/BUILDING.md) · [Español](../es/BUILDING.md) · [فارسی](../fa/BUILDING.md) · [Français](../fr/BUILDING.md) · [हिन्दी](../hi/BUILDING.md) · [Bahasa Indonesia](../id/BUILDING.md) · [Italiano](../it/BUILDING.md) · [日本語](../ja/BUILDING.md) · [한국어](../ko/BUILDING.md) · [Nederlands](../nl/BUILDING.md) · [Polski](../pl/BUILDING.md) · [Português](../pt/BUILDING.md) · [Русский](../ru/BUILDING.md) · [Svenska](../sv/BUILDING.md) · [ไทย](../th/BUILDING.md) · Türkçe · [Українська](../uk/BUILDING.md) · [Tiếng Việt](../vi/BUILDING.md) · [中文](../zh/BUILDING.md)

---

# VD Infos'u derleme

VD Infos standart bir Android Gradle projesidir: NDK aracılığıyla derlenen küçük bir yerel mercekle
birlikte Kotlin ve Jetpack Compose. CI onu derleyebilir ve siz onu yerel olarak yeniden üretebilirsiniz.

## Gereksinimler

- Bir JDK (17 ile 21 arası) ve Android SDK.
- Yerel mercek için Android NDK ve CMake (SDK'nin `cmake` bileşenine sahipseniz Gradle Android eklentisi
  eşleşen bir CMake getirir).
- `minSdk` 26, böylece derlemeler Android 8.0 ve daha yenisinde çalışır.

## Hızlı başlangıç

```sh
# build both flavors, release
./gradlew assembleRelease
```

Bu, flavor başına bir tane olmak üzere **iki APK** üretir:

- **modern** (`SDK_35`) - katı modern sanal alan, kullanılacak derleme.
- **compat** (`SDK_27`) - daha eski bir API hedefi, yalnızca yan yana
  karşılaştırma için.

Çıktılar şuraya iner:

```
app/build/outputs/apk/modern/release/VDInfos-v<ver>-SDK_35-release.apk
app/build/outputs/apk/compat/release/VDInfos-v<ver>-SDK_27-release.apk
```

Yineleme yaparken yalnızca bir flavor derlemek için:

```sh
./gradlew assembleModernRelease   # SDK_35 only
./gradlew assembleCompatRelease   # SDK_27 only
```

## İmzalama

Release derlemeleri **yerel ve işlenmemiş** bir anahtarla imzalanır (keystore ve özellikleri git
tarafından yoksayılır). Onsuz, release görevi yine de derler ama APK imzasız çıkar. Kendi
derlemelerinizi imzalamak için kendi keystore'unuzu sağlayın.

## İki flavor

Uygulama kasıtlı olarak iki flavor gönderir. Farklı API düzeylerini hedeflerler ve aynı kaynaktan
derlenir; yalnızca hedef ve birkaç korumalı kod yolu farklıdır. Yerel koda
veya flavor'a özgü bir yola dokunan bir değişiklik, yalnızca üzerinde yineleme yaptığınız flavor'da
değil, **her iki** flavor'da derlenip kontrol edilmelidir.

## Yerel mercek

Yerel mercek, NDK tarafından küçük bir `.so` olarak derlenen, adıyla `RegisterNatives` aracılığıyla
bağlanan, bağımlılıktan arınmış tek bir C++ çeviri birimidir. Normal Gradle derlemesinin bir parçası
olarak derlenir; ayrı bir adım yoktur. Bilinçli olarak minik ve basit tutulur.

## Şeylerin nerede olduğu

```
app/src/main/java/ru/vd171/vdinfos/   Kotlin: model, probes, engine, data, ui
app/src/main/cpp/                     the native lens
app/src/main/assets/data/             the plain-text detection lists (see Contributing)
```

Parçaların nasıl bir araya geldiği için [Mimari](ARCHITECTURE.md) sayfasına ve koda dokunmadan bir
algılama hedefinin nasıl ekleneceği için [Katkıda bulunma](CONTRIBUTING.md) sayfasına bakın.

<!-- doc-footer -->

---

> _Bu belgeler Claude (Anthropic) ile oluşturulmuş olup VD171 tarafından yürütülmektedir. Düzeltmeler ve iyileştirmeler memnuniyetle karşılanır; bkz. [CONTRIBUTING.md](CONTRIBUTING.md)._
