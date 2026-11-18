> **Languages:** English · [العربية](ar/EXPORT-FORMAT.md) · [Čeština](cs/EXPORT-FORMAT.md) · [Deutsch](de/EXPORT-FORMAT.md) · [Español](es/EXPORT-FORMAT.md) · [فارسی](fa/EXPORT-FORMAT.md) · [Français](fr/EXPORT-FORMAT.md) · [हिन्दी](hi/EXPORT-FORMAT.md) · [Bahasa Indonesia](id/EXPORT-FORMAT.md) · [Italiano](it/EXPORT-FORMAT.md) · [日本語](ja/EXPORT-FORMAT.md) · [한국어](ko/EXPORT-FORMAT.md) · [Nederlands](nl/EXPORT-FORMAT.md) · [Polski](pl/EXPORT-FORMAT.md) · [Português](pt/EXPORT-FORMAT.md) · [Русский](ru/EXPORT-FORMAT.md) · [Svenska](sv/EXPORT-FORMAT.md) · [ไทย](th/EXPORT-FORMAT.md) · [Türkçe](tr/EXPORT-FORMAT.md) · [Українська](uk/EXPORT-FORMAT.md) · [Tiếng Việt](vi/EXPORT-FORMAT.md) · [中文](zh/EXPORT-FORMAT.md)

---

# Export format

The report VD Infos exports - and the `last_snapshot.json` it writes after every scan -
is one JSON object with a stable, documented shape. This page is the schema. It
describes the structure so a tool can parse it; it does not list the individual probes
or interpret any reading for you.

## Top level: the snapshot

```json
{
  "takenAt": 1730000000000,
  "appVersion": "2.23",
  "results": [ /* ProbeResult objects */ ]
}
```

| Field | Type | Meaning |
|---|---|---|
| `takenAt` | number | When the scan was taken, Unix epoch in milliseconds. |
| `appVersion` | string | The app version that produced the report. |
| `results` | array | One `ProbeResult` per probe, in the order the app holds them. |

## A result

Each entry in `results` is a probe with its verdict and every lens reading.

```json
{
  "spec": { /* ProbeSpec */ },
  "values": [ /* LensValue objects */ ],
  "verdict": "MATCH",
  "detail": null
}
```

| Field | Type | Meaning |
|---|---|---|
| `spec` | object | What was probed (see below). |
| `values` | array | One `LensValue` per method that read it. |
| `verdict` | string | How the readings relate (see [Verdict](#verdict)). |
| `detail` | string or null | An optional note about the result. |

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

| Field | Type | Meaning |
|---|---|---|
| `id` | string | Stable identifier of the probe. |
| `title` | string | Human-readable name. |
| `category` | string | One of the [categories](#category). |
| `lenses` | array | The lenses this probe uses. |
| `note` | string or null | Optional context. |
| `sensitive` | boolean | `true` when the value is identity-bearing; such values are **masked in the exported report** (see [Masking](#masking)). |
| `solution` | string or null | An optional hint field; may be absent or null. |

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

| Field | Type | Meaning |
|---|---|---|
| `lens` | string | Which method read it (see [Lens](#lens)). |
| `source` | string | The exact call or path behind this reading. |
| `value` | string or null | What that method returned; `null` if it read nothing. |
| `error` | string or null | Present when the method failed. |
| `elapsedMicros` | number | How long the reading took, in microseconds. |
| `compare` | boolean | `true` if this reading takes part in the verdict; `false` if it is shown for information only. |
| `detail` | string or null | An optional note about this reading. |

## Enumerations

### Verdict

| Value | Meaning |
|---|---|
| `MATCH` | Every comparable lens agreed. |
| `MISMATCH` | The comparable lenses did not agree. |
| `SINGLE` | Only one lens could read it. |
| `INFO` | Read for reference, not voted on. |
| `EMPTY` | No lens read a value. |
| `ERROR` | No value, and at least one lens errored. |

### Lens

| Value | Label |
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

## Masking

When `spec.sensitive` is `true`, the probe's readings carry an identity-bearing value
(a serial, IMEI, MAC address, advertising ID and the like). In the **shared or exported**
report those values, and their details, are replaced with a redaction marker so a
report pasted into a public place does not leak personal identifiers; the probe, its
verdict and the structure are preserved. The **on-device** `last_snapshot.json`,
readable only by the app or by root, keeps the real values.

## Reading it

A minimal parse: iterate `results`, read each `verdict`, and for a full picture line up
the `values` by their `lens`. The shape above is stable across releases; parse against
these field names rather than screen positions.

See [AI agents](AI-AGENTS.md) for how to obtain the file on the device.

<!-- doc-footer -->

---

> _This documentation was generated with Claude (Anthropic) and is maintained by VD171. Corrections and improvements are welcome; see [CONTRIBUTING.md](CONTRIBUTING.md)._
