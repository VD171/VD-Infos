> **Languages:** [English](../BUILDING.md) · [العربية](../ar/BUILDING.md) · [Čeština](../cs/BUILDING.md) · [Deutsch](../de/BUILDING.md) · [Español](../es/BUILDING.md) · [فارسی](../fa/BUILDING.md) · [Français](../fr/BUILDING.md) · [हिन्दी](../hi/BUILDING.md) · [Bahasa Indonesia](../id/BUILDING.md) · [Italiano](../it/BUILDING.md) · 日本語 · [한국어](../ko/BUILDING.md) · [Nederlands](../nl/BUILDING.md) · [Polski](../pl/BUILDING.md) · [Português](../pt/BUILDING.md) · [Русский](../ru/BUILDING.md) · [Svenska](../sv/BUILDING.md) · [ไทย](../th/BUILDING.md) · [Türkçe](../tr/BUILDING.md) · [Українська](../uk/BUILDING.md) · [Tiếng Việt](../vi/BUILDING.md) · [中文](../zh/BUILDING.md)

---

# VD Infos のビルド

VD Infos は標準的な Android Gradle プロジェクトです。Kotlin と Jetpack Compose に、NDK を通じて
ビルドされる C++ の小さなネイティブレンズを加えたものです。CI でビルドでき、ローカルでも再現
できます。

## 要件

- JDK (17 から 21) と Android SDK。
- ネイティブレンズのための Android NDK と CMake (SDK の `cmake` コンポーネントがあれば、Gradle
  の Android プラグインが対応する CMake を取得します)。
- `minSdk` 26。そのためビルドは Android 8.0 以降で動作します。

## クイックスタート

```sh
# build both flavors, release
./gradlew assembleRelease
```

これはフレーバーごとに 1 つ、**2 つの APK** を生成します。

- **modern** (`SDK_35`) - 厳格な現代のサンドボックス、使うべきビルド。
- **compat** (`SDK_27`) - より緩い SELinux ドメインを持つ古いターゲット、並べて比較する
  用途のみ。

出力は次の場所に生成されます。

```
app/build/outputs/apk/modern/release/VDInfos-v<ver>-SDK_35-release.apk
app/build/outputs/apk/compat/release/VDInfos-v<ver>-SDK_27-release.apk
```

反復作業中に 1 つのフレーバーだけをビルドするには:

```sh
./gradlew assembleModernRelease   # SDK_35 only
./gradlew assembleCompatRelease   # SDK_27 only
```

## 署名

release ビルドは、**ローカルにあり、コミットされない** キーで署名されます (キーストアとその
プロパティは git によって無視されます)。それがなくても release タスクはビルドされますが、APK は
未署名で出てきます。自分のビルドに署名するには、自分のキーストアを用意してください。

## 2 つのフレーバー

アプリは意図的に 2 つのフレーバーを出荷します。異なる API レベルを対象とするため、異なる
Android サンドボックスドメインに入り、それ自体が比較に役立ちます。どちらも同じソースから
ビルドされます。異なるのはターゲットと、いくつかのガードされたコードパスだけです。ネイティブ
コードやフレーバー固有のパスに触れる変更は、反復している一方だけでなく **両方の** フレーバーで
ビルドして確認するべきです。

## ネイティブレンズ

ネイティブレンズは、NDK によって 1 つの小さな `.so` としてビルドされる、単一の依存関係のない
C++ 翻訳単位で、`RegisterNatives` を通じて名前でバインドされます。通常の Gradle ビルドの一部
としてコンパイルされます。別のステップはありません。欺きにくくなければならない部分なので、
意図的にごく小さく保たれています。

## どこに何があるか

```
app/src/main/java/ru/vd171/vdinfos/   Kotlin: model, probes, engine, data, ui
app/src/main/cpp/                     the native lens
app/src/main/assets/data/             the plain-text detection lists (see Contributing)
```

各部分がどう組み合わさるかは [アーキテクチャ](ARCHITECTURE.md) を、コードに触れずに検出対象を
追加する方法は [コントリビューション](CONTRIBUTING.md) を参照してください。

<!-- doc-footer -->

---

> _このドキュメントは Claude (Anthropic) で生成され、VD171 によって保守されています。修正や改善を歓迎します。[CONTRIBUTING.md](CONTRIBUTING.md) を参照してください。_
