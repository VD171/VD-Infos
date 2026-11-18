> **Languages:** [English](../EXPORT-FORMAT.md) · [العربية](../ar/EXPORT-FORMAT.md) · [Čeština](../cs/EXPORT-FORMAT.md) · Deutsch · [Español](../es/EXPORT-FORMAT.md) · [فارسی](../fa/EXPORT-FORMAT.md) · [Français](../fr/EXPORT-FORMAT.md) · [हिन्दी](../hi/EXPORT-FORMAT.md) · [Bahasa Indonesia](../id/EXPORT-FORMAT.md) · [Italiano](../it/EXPORT-FORMAT.md) · [日本語](../ja/EXPORT-FORMAT.md) · [한국어](../ko/EXPORT-FORMAT.md) · [Nederlands](../nl/EXPORT-FORMAT.md) · [Polski](../pl/EXPORT-FORMAT.md) · [Português](../pt/EXPORT-FORMAT.md) · [Русский](../ru/EXPORT-FORMAT.md) · [Svenska](../sv/EXPORT-FORMAT.md) · [ไทย](../th/EXPORT-FORMAT.md) · [Türkçe](../tr/EXPORT-FORMAT.md) · [Українська](../uk/EXPORT-FORMAT.md) · [Tiếng Việt](../vi/EXPORT-FORMAT.md) · [中文](../zh/EXPORT-FORMAT.md)

---

# Exportformat

Der Bericht, den VD Infos exportiert - und die `last_snapshot.json`, die es nach jedem
Scan schreibt - ist ein JSON-Objekt mit einer stabilen, dokumentierten Form. Diese Seite
ist das Schema. Sie beschreibt die Struktur, damit ein Werkzeug sie parsen kann; sie
listet nicht die einzelnen Sonden auf und deutet keinen Messwert für Sie.

## Oberste Ebene: der Snapshot

```json
{
  "takenAt": 1730000000000,
  "appVersion": "2.23",
  "results": [ /* ProbeResult objects */ ]
}
```

| Feld | Typ | Bedeutung |
|---|---|---|
| `takenAt` | number | Wann der Scan erstellt wurde, Unix-Epoche in Millisekunden. |
| `appVersion` | string | Die App-Version, die den Bericht erzeugt hat. |
| `results` | array | Ein `ProbeResult` pro Sonde, in der Reihenfolge, in der die App sie vorhält. |

## Ein Ergebnis

Jeder Eintrag in `results` ist eine Sonde mit ihrem Urteil und jedem Linsen-Messwert.

```json
{
  "spec": { /* ProbeSpec */ },
  "values": [ /* LensValue objects */ ],
  "verdict": "MATCH",
  "detail": null
}
```

| Feld | Typ | Bedeutung |
|---|---|---|
| `spec` | object | Was abgefragt wurde (siehe unten). |
| `values` | array | Ein `LensValue` pro Methode, die ihn gelesen hat. |
| `verdict` | string | Wie die Messwerte zueinander stehen (siehe [Urteil](#urteil)). |
| `detail` | string or null | Eine optionale Anmerkung zum Ergebnis. |

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

| Feld | Typ | Bedeutung |
|---|---|---|
| `id` | string | Stabiler Bezeichner der Sonde. |
| `title` | string | Menschenlesbarer Name. |
| `category` | string | Eine der [Kategorien](#kategorie). |
| `lenses` | array | Die Linsen, die diese Sonde verwendet. |
| `note` | string or null | Optionaler Kontext. |
| `sensitive` | boolean | `true`, wenn der Wert identitätstragend ist; solche Werte werden **im exportierten Bericht maskiert** (siehe [Maskierung](#maskierung)). |
| `solution` | string or null | Ein optionales Hinweisfeld; kann fehlen oder null sein. |

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

| Feld | Typ | Bedeutung |
|---|---|---|
| `lens` | string | Welche Methode ihn gelesen hat (siehe [Linse](#linse)). |
| `source` | string | Der genaue Aufruf oder Pfad hinter diesem Messwert. |
| `value` | string or null | Was diese Methode zurückgab; `null`, wenn sie nichts gelesen hat. |
| `error` | string or null | Vorhanden, wenn die Methode fehlgeschlagen ist. |
| `elapsedMicros` | number | Wie lange der Messwert dauerte, in Mikrosekunden. |
| `compare` | boolean | `true`, wenn dieser Messwert am Urteil teilnimmt; `false`, wenn er nur zur Information angezeigt wird. |
| `detail` | string or null | Eine optionale Anmerkung zu diesem Messwert. |

## Aufzählungen

### Urteil

| Wert | Bedeutung |
|---|---|
| `MATCH` | Jede vergleichbare Linse war sich einig. |
| `MISMATCH` | Die vergleichbaren Linsen waren sich nicht einig. |
| `SINGLE` | Nur eine Linse konnte ihn lesen. |
| `INFO` | Zur Referenz gelesen, nicht abgestimmt. |
| `EMPTY` | Keine Linse hat einen Wert gelesen. |
| `ERROR` | Kein Wert, und mindestens eine Linse hatte einen Fehler. |

### Linse

| Wert | Bezeichnung |
|---|---|
| `JAVA` | Java / SDK |
| `NATIVE` | Nativ / JNI |
| `SHELL` | Shell über JVM |
| `SHELL_NATIVE` | Shell über JNI |
| `ATTEST` | TEE / Attestierung |

### Kategorie

`IDENTITY`, `FINGERPRINT`, `TELEPHONY`, `NETWORK`, `LOCALE`, `BOOT`, `BUILD`,
`HARDWARE`, `DISPLAY`, `SENSORS`, `MEDIA`, `STORAGE`, `PROCESS`, `PACKAGES`,
`ACCOUNTS`, `WEBVIEW`, `SECURITY`, `INTEGRITY`, `EMULATOR`, `SYSTEM`.

## Maskierung

Wenn `spec.sensitive` `true` ist, tragen die Messwerte der Sonde einen
identitätstragenden Wert (eine Seriennummer, IMEI, MAC-Adresse, Werbe-ID und
dergleichen). Im **geteilten oder exportierten** Bericht werden diese Werte und ihre
Details durch einen Schwärzungsmarker ersetzt, damit ein an einen öffentlichen Ort
eingefügter Bericht keine persönlichen Kennungen preisgibt; die Sonde, ihr Urteil und
die Struktur bleiben erhalten. Die `last_snapshot.json` **auf dem Gerät**, lesbar nur von
der App oder von root, behält die echten Werte.

## Es lesen

Ein minimaler Parse: iterieren Sie über `results`, lesen Sie jedes `verdict`, und reihen
Sie für ein vollständiges Bild die `values` nach ihrer `lens` auf. Die obige Form ist
über Veröffentlichungen hinweg stabil; parsen Sie gegen diese Feldnamen statt gegen
Bildschirmpositionen.

Siehe [KI-Agenten](AI-AGENTS.md) für den Erhalt der Datei auf dem Gerät.

<!-- doc-footer -->

---

> _Diese Dokumentation wurde mit Claude (Anthropic) erstellt und wird von VD171 gepflegt. Korrekturen und Verbesserungen sind willkommen; siehe [CONTRIBUTING.md](CONTRIBUTING.md)._
