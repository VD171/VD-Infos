> **Languages:** [English](../CONTRIBUTING.md) · [العربية](../ar/CONTRIBUTING.md) · [Čeština](../cs/CONTRIBUTING.md) · [Deutsch](../de/CONTRIBUTING.md) · [Español](../es/CONTRIBUTING.md) · [فارسی](../fa/CONTRIBUTING.md) · [Français](../fr/CONTRIBUTING.md) · [हिन्दी](../hi/CONTRIBUTING.md) · [Bahasa Indonesia](../id/CONTRIBUTING.md) · [Italiano](../it/CONTRIBUTING.md) · 日本語 · [한국어](../ko/CONTRIBUTING.md) · [Nederlands](../nl/CONTRIBUTING.md) · [Polski](../pl/CONTRIBUTING.md) · [Português](../pt/CONTRIBUTING.md) · [Русский](../ru/CONTRIBUTING.md) · [Svenska](../sv/CONTRIBUTING.md) · [ไทย](../th/CONTRIBUTING.md) · [Türkçe](../tr/CONTRIBUTING.md) · [Українська](../uk/CONTRIBUTING.md) · [Tiếng Việt](../vi/CONTRIBUTING.md) · [中文](../zh/CONTRIBUTING.md)

---

# コントリビューション

VD Infos に関心を持っていただきありがとうございます。コントリビューションは歓迎します。検出
リスト、翻訳、バグ報告、ドキュメント、コード。ほとんどのコントリビューションに Kotlin は不要です。

## 最も簡単なコントリビューション: リスト

アプリが探すもののほとんどは、コードではなく
`VDInfos/app/src/main/assets/data/` 配下の **プレーンテキストリスト** として存在します。対象の
追加や削除は、1 行を編集してプルリクエストを開くことです。

- `*_apps.txt` - 1 行につき 1 つのパッケージ名。
- `props.txt` - システムプロパティのカタログ、`CATEGORY<tab>key`。
- `spoof_keys.txt` - 設定のマトリクス、`key:TYPE`。
- `known_certs.txt` - 署名証明書のカタログ、`SHA-256<tab>owner` (実際の APK から
  `apksigner verify --print-certs` で取得したフルハッシュ)。
- その他の `.txt` ファイル - 1 行につき 1 エントリ (カーネルモジュール名の断片、ファイル名
  など)。

`#` でコメントが始まり、空行は無視されます。一部のファイルは 1 行につきスペースまたはタブ区切り
のフィールドをいくつか、あるいは任意のマーカーを詰めています。ファイルに既にある行の形を保って
ください。

## 基本ルール

- **VD Infos は真実を読み取り、決して偽装しません。** 各メソッドが返したものを、そのまま正確に
  報告します。見栄えを良くするために読み取りを和らげたり、値を読み取るのではなく変更するような
  ものを追加したりしないでください。
- **プライバシーは絶対です。** すべてがデバイス上で動作します。ネットワーク呼び出しや、読み取り
  をデバイス外へ送信するものを決して追加しないでください。
- **エクスポートはマスクされたままにします。** ID を含む値は共有されるレポートではマスクされ
  ます。その状態を保ってください。

## 翻訳

翻訳は価値あるコントリビューションです。readme は言語ごと (`README.<lang>.md`)、アプリの文字列は
言語ごと、そしてこのドキュメントは `docs/<lang>/` 配下に翻訳されます。1 つの言語の中で用語を
一貫させ、既存のトーンに合わせ、確信が持てない技術用語を決して機械翻訳しないでください。

## バグの報告

デバイスのモデル、Android のバージョン、そして読み取りに関するものであれば共有された
**エクスポート** (その ID を含む値はマスクされます) を添えて issue を開いてください。エクスポート
はまさにこのために作られています。

## コード

- release をビルドし、決して debug をビルドしないでください。実機でテストしてください。
  [ビルド](BUILDING.md) を参照してください。
- コードにコメントを入れないでください。名前が意図を説明します。唯一のヘッダーは各ソース
  ファイルの先頭にあるライセンスバナーです。
- 既存のスタイルに合わせ、変更を集中させてください。

## ライセンス

コントリビューションすることで、あなたのコントリビューションがプロジェクトの
**GNU AGPL-3.0-or-later** の下でライセンスされることに同意するものとします。

<!-- doc-footer -->

---

> _このドキュメントは Claude (Anthropic) で生成され、VD171 によって保守されています。修正や改善を歓迎します。[CONTRIBUTING.md](CONTRIBUTING.md) を参照してください。_
