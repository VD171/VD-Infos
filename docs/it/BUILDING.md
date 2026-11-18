> **Languages:** [English](../BUILDING.md) · [العربية](../ar/BUILDING.md) · [Čeština](../cs/BUILDING.md) · [Deutsch](../de/BUILDING.md) · [Español](../es/BUILDING.md) · [فارسی](../fa/BUILDING.md) · [Français](../fr/BUILDING.md) · [हिन्दी](../hi/BUILDING.md) · [Bahasa Indonesia](../id/BUILDING.md) · Italiano · [日本語](../ja/BUILDING.md) · [한국어](../ko/BUILDING.md) · [Nederlands](../nl/BUILDING.md) · [Polski](../pl/BUILDING.md) · [Português](../pt/BUILDING.md) · [Русский](../ru/BUILDING.md) · [Svenska](../sv/BUILDING.md) · [ไทย](../th/BUILDING.md) · [Türkçe](../tr/BUILDING.md) · [Українська](../uk/BUILDING.md) · [Tiếng Việt](../vi/BUILDING.md) · [中文](../zh/BUILDING.md)

---

# Compilare VD Infos

VD Infos è un progetto Android Gradle standard: Kotlin e Jetpack Compose, con una piccola
lente nativa in C++ compilata tramite l'NDK. La CI può compilarlo, e puoi riprodurlo
localmente.

## Requisiti

- Un JDK (da 17 a 21) e l'Android SDK.
- L'Android NDK e CMake, per la lente nativa (il plugin Android di Gradle recupera un
  CMake compatibile se hai il componente `cmake` dell'SDK).
- `minSdk` 26, così le build funzionano su Android 8.0 e più recente.

## Avvio rapido

```sh
# build both flavors, release
./gradlew assembleRelease
```

Questo produce **due APK**, uno per flavor:

- **modern** (`SDK_35`) - la rigida sandbox moderna, la build da usare.
- **compat** (`SDK_27`) - un target più vecchio con un dominio SELinux più permissivo, solo per il
  confronto fianco a fianco.

Gli output finiscono in:

```
app/build/outputs/apk/modern/release/VDInfos-v<ver>-SDK_35-release.apk
app/build/outputs/apk/compat/release/VDInfos-v<ver>-SDK_27-release.apk
```

Per compilare solo un flavor mentre lavori:

```sh
./gradlew assembleModernRelease   # SDK_35 only
./gradlew assembleCompatRelease   # SDK_27 only
```

## Firma

Le build di release sono firmate con una chiave che è **locale e non committata** (il keystore
e le sue proprietà sono ignorati da git). Senza di essa, il task di release compila comunque ma
l'APK esce non firmato. Fornisci il tuo keystore per firmare le tue build.

## I due flavor

L'app distribuisce due flavor di proposito. Mirano a livelli di API diversi e perciò
ricadono in domini della sandbox di Android diversi, il che è di per sé utile per il confronto. Entrambi
compilano dallo stesso sorgente; solo il target e alcuni percorsi di codice protetti differiscono. Una
modifica che tocca il codice nativo o un percorso specifico di un flavor dovrebbe essere compilata e verificata in
**entrambi** i flavor, non solo in quello su cui stai lavorando.

## La lente nativa

La lente nativa è una singola unità di traduzione C++ priva di dipendenze compilata dall'NDK come
un piccolo `.so`, legato per nome tramite `RegisterNatives`. È compilata come parte della
normale build di Gradle; non c'è un passaggio separato. È mantenuta deliberatamente minuscola perché
è la parte che deve essere difficile da ingannare.

## Dove risiedono le cose

```
app/src/main/java/ru/vd171/vdinfos/   Kotlin: model, probes, engine, data, ui
app/src/main/cpp/                     the native lens
app/src/main/assets/data/             the plain-text detection lists (see Contributing)
```

Vedi [Architettura](ARCHITECTURE.md) per come si incastrano i pezzi, e
[Contribuire](CONTRIBUTING.md) per come aggiungere un target di rilevamento senza toccare il codice.

<!-- doc-footer -->

---

> _Questa documentazione è stata generata con Claude (Anthropic) ed è gestita da VD171. Correzioni e miglioramenti sono benvenuti; vedi [CONTRIBUTING.md](CONTRIBUTING.md)._
