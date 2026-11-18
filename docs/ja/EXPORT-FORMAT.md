> **Languages:** [English](../EXPORT-FORMAT.md) · [العربية](../ar/EXPORT-FORMAT.md) · [Čeština](../cs/EXPORT-FORMAT.md) · [Deutsch](../de/EXPORT-FORMAT.md) · [Español](../es/EXPORT-FORMAT.md) · [فارسی](../fa/EXPORT-FORMAT.md) · [Français](../fr/EXPORT-FORMAT.md) · [हिन्दी](../hi/EXPORT-FORMAT.md) · [Bahasa Indonesia](../id/EXPORT-FORMAT.md) · [Italiano](../it/EXPORT-FORMAT.md) · 日本語 · [한국어](../ko/EXPORT-FORMAT.md) · [Nederlands](../nl/EXPORT-FORMAT.md) · [Polski](../pl/EXPORT-FORMAT.md) · [Português](../pt/EXPORT-FORMAT.md) · [Русский](../ru/EXPORT-FORMAT.md) · [Svenska](../sv/EXPORT-FORMAT.md) · [ไทย](../th/EXPORT-FORMAT.md) · [Türkçe](../tr/EXPORT-FORMAT.md) · [Українська](../uk/EXPORT-FORMAT.md) · [Tiếng Việt](../vi/EXPORT-FORMAT.md) · [中文](../zh/EXPORT-FORMAT.md)

---

# エクスポート形式

VD Infos がエクスポートするレポート - そして各スキャンのあとに書き込む `last_snapshot.json` -
は、安定した、文書化された形を持つ 1 つの JSON オブジェクトです。このページはそのスキーマです。
ツールが解析できるよう構造を記述します。個々のプローブを列挙したり、何らかの読み取りを解釈
したりはしません。

## トップレベル: スナップショット

```json
{
  "takenAt": 1730000000000,
  "appVersion": "2.23",
  "results": [ /* ProbeResult objects */ ]
}
```

| フィールド | 型 | 意味 |
|---|---|---|
| `takenAt` | 数値 | スキャンが取得された時刻。Unix エポックのミリ秒。 |
| `appVersion` | 文字列 | レポートを生成したアプリのバージョン。 |
| `results` | 配列 | プローブごとに 1 つの `ProbeResult`。アプリが保持している順。 |

## 1 つの結果

`results` の各エントリは、判定とあらゆるレンズの読み取りを持つ 1 つのプローブです。

```json
{
  "spec": { /* ProbeSpec */ },
  "values": [ /* LensValue objects */ ],
  "verdict": "MATCH",
  "detail": null
}
```

| フィールド | 型 | 意味 |
|---|---|---|
| `spec` | オブジェクト | 何がプローブされたか (下記参照)。 |
| `values` | 配列 | それを読み取った各メソッドにつき 1 つの `LensValue`。 |
| `verdict` | 文字列 | 読み取り結果がどう関係するか ([判定](#判定) を参照)。 |
| `detail` | 文字列または null | 結果に関する任意の注記。 |

### spec (ProbeSpec)

```json
{
  "id": "identity:model",
  "title": "Model",
  "category": "IDENTITY",
  "lenses": ["JAVA", "NATIVE", "SHELL"],
  "note": null,
  "sensitive": false,
  "solution": null
}
```

| フィールド | 型 | 意味 |
|---|---|---|
| `id` | 文字列 | プローブの安定した識別子。 |
| `title` | 文字列 | 人が読める名前。 |
| `category` | 文字列 | [カテゴリ](#カテゴリ) のいずれか 1 つ。 |
| `lenses` | 配列 | このプローブが使うレンズ。 |
| `note` | 文字列または null | 任意のコンテキスト。 |
| `sensitive` | 真偽値 | 値が ID を含む場合に `true`。そのような値は **エクスポートされたレポートではマスクされます** ([マスキング](#マスキング) を参照)。 |
| `solution` | 文字列または null | 任意のヒントフィールド。存在しないか null の場合があります。 |

### values (LensValue)

```json
{
  "lens": "JAVA",
  "source": "Build.MODEL",
  "value": "Pixel 9",
  "error": null,
  "elapsedMicros": 42,
  "compare": true,
  "detail": null
}
```

| フィールド | 型 | 意味 |
|---|---|---|
| `lens` | 文字列 | どのメソッドが読み取ったか ([レンズ](#レンズ) を参照)。 |
| `source` | 文字列 | この読み取りの背後にある正確な呼び出しまたはパス。 |
| `value` | 文字列または null | そのメソッドが返したもの。何も読めなかった場合は `null`。 |
| `error` | 文字列または null | メソッドが失敗したときに存在します。 |
| `elapsedMicros` | 数値 | 読み取りにかかった時間。マイクロ秒単位。 |
| `compare` | 真偽値 | この読み取りが判定に加わる場合は `true`。情報としてのみ表示される場合は `false`。 |
| `detail` | 文字列または null | この読み取りに関する任意の注記。 |

## 列挙型

### 判定

| 値 | 意味 |
|---|---|
| `MATCH` | 比較可能なすべてのレンズが一致した。 |
| `MISMATCH` | 比較可能なレンズが一致しなかった。 |
| `SINGLE` | 1 つのレンズだけが読み取れた。 |
| `INFO` | 参考として読み取られ、判定の対象外。 |
| `EMPTY` | どのレンズも値を読まなかった。 |
| `ERROR` | 値がなく、少なくとも 1 つのレンズがエラーになった。 |

### レンズ

| 値 | ラベル |
|---|---|
| `JAVA` | Java / SDK |
| `NATIVE` | Native / JNI |
| `SHELL` | Shell via JVM |
| `SHELL_NATIVE` | Shell via JNI |
| `ATTEST` | TEE / Attestation |

### カテゴリ

`IDENTITY`、`FINGERPRINT`、`TELEPHONY`、`NETWORK`、`LOCALE`、`BOOT`、`BUILD`、
`HARDWARE`、`DISPLAY`、`SENSORS`、`MEDIA`、`STORAGE`、`PROCESS`、`PACKAGES`、
`ACCOUNTS`、`WEBVIEW`、`SECURITY`、`INTEGRITY`、`EMULATOR`、`SYSTEM`。

## マスキング

`spec.sensitive` が `true` のとき、そのプローブの読み取りは ID を含む値 (シリアル、IMEI、
MAC アドレス、広告 ID など) を帯びています。**共有またはエクスポートされる** レポートでは、
それらの値とその詳細は、公開の場に貼り付けられたレポートが個人の識別子を漏らさないように、
墨消しマーカーに置き換えられます。プローブ、その判定、構造は保持されます。アプリまたは root
だけが読める **デバイス上の** `last_snapshot.json` は、実際の値を保持します。

## 読み取り方

最小限の解析: `results` を反復処理し、各 `verdict` を読み、全体像を得るには `values` を
その `lens` ごとに並べます。上記の形はリリースをまたいで安定しています。画面上の位置では
なく、これらのフィールド名に対して解析してください。

デバイス上でこのファイルを取得する方法は [AI エージェント](AI-AGENTS.md) を参照してください。

<!-- doc-footer -->

---

> _このドキュメントは Claude (Anthropic) で生成され、VD171 によって保守されています。修正や改善を歓迎します。[CONTRIBUTING.md](CONTRIBUTING.md) を参照してください。_
