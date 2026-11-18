> **Languages:** [English](../EXPORT-FORMAT.md) · [العربية](../ar/EXPORT-FORMAT.md) · [Čeština](../cs/EXPORT-FORMAT.md) · [Deutsch](../de/EXPORT-FORMAT.md) · [Español](../es/EXPORT-FORMAT.md) · [فارسی](../fa/EXPORT-FORMAT.md) · [Français](../fr/EXPORT-FORMAT.md) · [हिन्दी](../hi/EXPORT-FORMAT.md) · [Bahasa Indonesia](../id/EXPORT-FORMAT.md) · [Italiano](../it/EXPORT-FORMAT.md) · [日本語](../ja/EXPORT-FORMAT.md) · [한국어](../ko/EXPORT-FORMAT.md) · [Nederlands](../nl/EXPORT-FORMAT.md) · [Polski](../pl/EXPORT-FORMAT.md) · [Português](../pt/EXPORT-FORMAT.md) · [Русский](../ru/EXPORT-FORMAT.md) · [Svenska](../sv/EXPORT-FORMAT.md) · [ไทย](../th/EXPORT-FORMAT.md) · [Türkçe](../tr/EXPORT-FORMAT.md) · [Українська](../uk/EXPORT-FORMAT.md) · [Tiếng Việt](../vi/EXPORT-FORMAT.md) · 中文

---

# 导出格式

VD Infos 导出的报告 - 以及它在每次扫描后写入的 `last_snapshot.json` - 是一个具有稳定、有文档记录
形状的 JSON 对象。本页就是该模式。它描述结构，以便工具能够解析它；它不会列出各个探针，也不会替你解读
任何读数。

## 顶层：快照

```json
{
  "takenAt": 1730000000000,
  "appVersion": "2.23",
  "results": [ /* ProbeResult objects */ ]
}
```

| 字段 | 类型 | 含义 |
|---|---|---|
| `takenAt` | number | 扫描进行的时间，Unix 纪元，以毫秒计。 |
| `appVersion` | string | 生成该报告的应用版本。 |
| `results` | array | 每个探针一个 `ProbeResult`，按应用持有它们的顺序。 |

## 一个结果

`results` 中的每一项都是一个探针，带有它的裁决和每一面镜头的读数。

```json
{
  "spec": { /* ProbeSpec */ },
  "values": [ /* LensValue objects */ ],
  "verdict": "MATCH",
  "detail": null
}
```

| 字段 | 类型 | 含义 |
|---|---|---|
| `spec` | object | 探测了什么（见下）。 |
| `values` | array | 每个读取它的方法一个 `LensValue`。 |
| `verdict` | string | 各读数如何关联（参见 [Verdict](#verdict)）。 |
| `detail` | string or null | 关于该结果的可选备注。 |

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

| 字段 | 类型 | 含义 |
|---|---|---|
| `id` | string | 探针的稳定标识符。 |
| `title` | string | 人类可读的名称。 |
| `category` | string | [分类](#category)之一。 |
| `lenses` | array | 该探针使用的镜头。 |
| `note` | string or null | 可选的上下文。 |
| `sensitive` | boolean | 当值承载身份时为 `true`；此类值在**导出的报告中被遮蔽**（参见 [遮蔽](#遮蔽)）。 |
| `solution` | string or null | 一个可选的提示字段；可能缺失或为 null。 |

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

| 字段 | 类型 | 含义 |
|---|---|---|
| `lens` | string | 哪个方法读取了它（参见 [Lens](#lens)）。 |
| `source` | string | 这次读数背后确切的调用或路径。 |
| `value` | string or null | 该方法返回了什么；若它什么也没读到则为 `null`。 |
| `error` | string or null | 当方法失败时出现。 |
| `elapsedMicros` | number | 这次读数耗时多久，以微秒计。 |
| `compare` | boolean | 若这次读数参与裁决则为 `true`；若它只是为参考而显示则为 `false`。 |
| `detail` | string or null | 关于这次读数的可选备注。 |

## 枚举

### Verdict

| 值 | 含义 |
|---|---|
| `MATCH` | 每一面可比较的镜头都一致。 |
| `MISMATCH` | 这些可比较的镜头不一致。 |
| `SINGLE` | 只有一面镜头能读取它。 |
| `INFO` | 为参考而读取，不参与投票。 |
| `EMPTY` | 没有任何镜头读到一个值。 |
| `ERROR` | 没有值，且至少一面镜头报错。 |

### Lens

| 值 | 标签 |
|---|---|
| `JAVA` | Java / SDK |
| `NATIVE` | Native / JNI |
| `SHELL` | Shell via JVM |
| `SHELL_NATIVE` | Shell via JNI |
| `ATTEST` | TEE / Attestation |

### Category

`IDENTITY`, `FINGERPRINT`, `TELEPHONY`, `NETWORK`, `LOCALE`, `BOOT`, `BUILD`,
`HARDWARE`, `DISPLAY`, `SENSORS`, `MEDIA`, `STORAGE`, `PROCESS`, `PACKAGES`,
`ACCOUNTS`, `WEBVIEW`, `SECURITY`, `INTEGRITY`, `EMULATOR`, `SYSTEM`.

## 遮蔽

当 `spec.sensitive` 为 `true` 时，该探针的读数承载一个承载身份的值（序列号、IMEI、MAC 地址、广告 ID
之类）。在**被分享或导出的**报告中，这些值及其细节会被替换为一个删节标记，这样粘贴到公共场所的报告就
不会泄露个人标识符；探针、它的裁决和结构被保留。**设备上的** `last_snapshot.json` 只有应用或 root 能
读取，保留真实值。

## 阅读它

一次最简解析：遍历 `results`，读取每个 `verdict`，并且为了完整的图景，按各读数的 `lens` 把 `values`
并排排列。上面的形状在各发行版之间是稳定的；请按这些字段名而不是屏幕位置来解析。

参见 [AI 代理](AI-AGENTS.md) 了解如何在设备上获取该文件。

<!-- doc-footer -->

---

> _本文档由 Claude (Anthropic) 生成，并由 VD171 维护。欢迎修正和改进；请参阅 [CONTRIBUTING.md](CONTRIBUTING.md)。_
