> **Languages:** [English](../ARCHITECTURE.md) · [العربية](../ar/ARCHITECTURE.md) · [Čeština](../cs/ARCHITECTURE.md) · [Deutsch](../de/ARCHITECTURE.md) · [Español](../es/ARCHITECTURE.md) · [فارسی](../fa/ARCHITECTURE.md) · [Français](../fr/ARCHITECTURE.md) · [हिन्दी](../hi/ARCHITECTURE.md) · [Bahasa Indonesia](../id/ARCHITECTURE.md) · [Italiano](../it/ARCHITECTURE.md) · 日本語 · [한국어](../ko/ARCHITECTURE.md) · [Nederlands](../nl/ARCHITECTURE.md) · [Polski](../pl/ARCHITECTURE.md) · [Português](../pt/ARCHITECTURE.md) · [Русский](../ru/ARCHITECTURE.md) · [Svenska](../sv/ARCHITECTURE.md) · [ไทย](../th/ARCHITECTURE.md) · [Türkçe](../tr/ARCHITECTURE.md) · [Українська](../uk/ARCHITECTURE.md) · [Tiếng Việt](../vi/ARCHITECTURE.md) · [中文](../zh/ARCHITECTURE.md)

---

# アーキテクチャ

これは VD Infos がどう組み立てられているかの大まかな地図です。アプリが読み取るものの一覧
ではなく、アプリの形を記述します。メソッドデバッガーの要点は、あらゆる読み取りをあなたの
目の前に置くことであり、それらを読むのはあなた自身が自分のデバイス上で行うことです。

## 設計目標

- **1 つの値を、あらゆるメソッドで。** それぞれの情報について、それを読み取れるあらゆる
  メソッドを通して読み取り、読み取り結果を並べます。デバッガーは証拠を提示します。判断を
  下すことはしません。
- **デバイス上で、静かに。** ネットワークなし、アカウントなし、バックグラウンド作業なし。
  アプリは開いている間だけ動作します。
- **機械に読める。** 結果全体が 1 つの機械可読なドキュメントなので、ツールは人と同じ全体像を
  得られます。[AI エージェント](AI-AGENTS.md) を参照してください。
- **重要なところは小さく。** ネイティブコンポーネントは、意図的にごく小さくシンプルに
  保たれています。

## レンズ

*レンズ* とは、値を読み取る 1 つの方法です。同じ値を複数のレンズで読み取り、比較できるように
します。

| レンズ | それが何か |
|---|---|
| **Java / SDK** | ごく普通のアプリが呼び出すプラットフォーム API。 |
| **Native / JNI** | 同じ値を、ネイティブコードから読み取ったもの。 |
| **Shell via JVM** | JVM が生成したシェルコマンド。 |
| **Shell via JNI** | 同じコマンドを、ネイティブコードから生成したもの。 |
| **TEE / Attestation** | 当てはまる場合の、ハードウェアに裏付けられたキー証明。 |

レンズを並べることがすべての狙いです。アプリは、読み取りが一致する箇所と食い違う箇所を
示します。その違いがある値について、あるデバイス上で何を意味するかは、あなたが読むことです。

## スキャンの実行方法

```
core/model      immutable domain: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          the probes and the bridges they use (JNI, reflection, data lists)
engine/         ProbeEngine: parallel fan-out, streams results as they land
data/           SnapshotStore (persist the report), Exporter (share as JSON / text)
ui/             Jetpack Compose, Material 3, dynamic colour, live progress
cpp/            native_probes.cpp: the native lens, dependency-free
```

- **並列性。** プローブは、上限付きのパーミット数を持つディスパッチャーにわたって展開され
  ます。結果は完了するたびに UI へストリーミングされ、進行状況がリアルタイムで表示されます。
- **バックグラウンドでは何もしない。** サービスも、スケジュールされたスキャンもありません。
  アプリは開いている間だけ読み取り、放置されると自身を閉じます。
- **ネイティブ層。** `RegisterNatives` を通じて名前でバインドされる、依存のない 1 つの
  小さな `.so`。意図的にごく小さくシンプルに保たれています。

## レポート

各スキャンは 1 つの `Snapshot` を生成します。取得された時刻、アプリのバージョン、そして
結果のリスト (プローブごとに 1 つ) で、各結果はあらゆるレンズからの読み取りと、それらが
どう関係するかの判定を持ちます。アプリは各スキャンのあとこれをプライベートストレージに保存
し、同じデータをエクスポートできます。構造は [エクスポート形式](EXPORT-FORMAT.md) として
文書化されています。

## 拡張する

アプリが探すもののほとんどは、コードではなく `assets/data/` 配下の **プレーンテキスト
リスト** として存在します。対象の追加や削除は 1 行の編集です。
[コントリビューション](CONTRIBUTING.md) を参照してください。

## VD Infos が決してしないこと

- システム上の何も決して変更しません。読み取って比較するだけです。
- ネットワークと決して通信しません。完全にオフラインで動作します。
- バックグラウンドで決して動作せず、スキャンをスケジュールせず、サービスを生かし続けません。
- 読み取りをどこにも決して送信しません。レポートがデバイスを離れるのは、あなたが共有または
  保存したときだけで、ID を含む値はマスクされます。

<!-- doc-footer -->

---

> _このドキュメントは Claude (Anthropic) で生成され、VD171 によって保守されています。修正や改善を歓迎します。[CONTRIBUTING.md](CONTRIBUTING.md) を参照してください。_
