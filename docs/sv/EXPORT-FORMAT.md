> **Languages:** [English](../EXPORT-FORMAT.md) · [العربية](../ar/EXPORT-FORMAT.md) · [Čeština](../cs/EXPORT-FORMAT.md) · [Deutsch](../de/EXPORT-FORMAT.md) · [Español](../es/EXPORT-FORMAT.md) · [فارسی](../fa/EXPORT-FORMAT.md) · [Français](../fr/EXPORT-FORMAT.md) · [हिन्दी](../hi/EXPORT-FORMAT.md) · [Bahasa Indonesia](../id/EXPORT-FORMAT.md) · [Italiano](../it/EXPORT-FORMAT.md) · [日本語](../ja/EXPORT-FORMAT.md) · [한국어](../ko/EXPORT-FORMAT.md) · [Nederlands](../nl/EXPORT-FORMAT.md) · [Polski](../pl/EXPORT-FORMAT.md) · [Português](../pt/EXPORT-FORMAT.md) · [Русский](../ru/EXPORT-FORMAT.md) · Svenska · [ไทย](../th/EXPORT-FORMAT.md) · [Türkçe](../tr/EXPORT-FORMAT.md) · [Українська](../uk/EXPORT-FORMAT.md) · [Tiếng Việt](../vi/EXPORT-FORMAT.md) · [中文](../zh/EXPORT-FORMAT.md)

---

# Exportformat

Rapporten VD Infos exporterar - och `last_snapshot.json` den skriver efter varje skanning - är ett
JSON-objekt med en stabil, dokumenterad form. Den här sidan är schemat. Den beskriver strukturen så
att ett verktyg kan tolka den; den listar inte de enskilda sonderna och tolkar inte någon avläsning
åt dig.

## Översta nivån: ögonblicksbilden

```json
{
  "takenAt": 1730000000000,
  "appVersion": "2.23",
  "results": [ /* ProbeResult objects */ ]
}
```

| Fält | Typ | Betydelse |
|---|---|---|
| `takenAt` | number | När skanningen togs, Unix-epok i millisekunder. |
| `appVersion` | string | Appversionen som producerade rapporten. |
| `results` | array | En `ProbeResult` per sond, i den ordning appen håller dem. |

## Ett resultat

Varje post i `results` är en sond med sitt domslut och varje linsavläsning.

```json
{
  "spec": { /* ProbeSpec */ },
  "values": [ /* LensValue objects */ ],
  "verdict": "MATCH",
  "detail": null
}
```

| Fält | Typ | Betydelse |
|---|---|---|
| `spec` | object | Vad som sondades (se nedan). |
| `values` | array | En `LensValue` per metod som läste den. |
| `verdict` | string | Hur avläsningarna förhåller sig (se [Domslut](#domslut)). |
| `detail` | string eller null | En valfri anteckning om resultatet. |

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

| Fält | Typ | Betydelse |
|---|---|---|
| `id` | string | Stabil identifierare för sonden. |
| `title` | string | Människoläsbart namn. |
| `category` | string | En av [kategorierna](#kategori). |
| `lenses` | array | Linserna som denna sond använder. |
| `note` | string eller null | Valfri kontext. |
| `sensitive` | boolean | `true` när värdet är identitetsbärande; sådana värden är **maskerade i den exporterade rapporten** (se [Maskering](#maskering)). |
| `solution` | string eller null | Ett valfritt tipsfält; kan saknas eller vara null. |

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

| Fält | Typ | Betydelse |
|---|---|---|
| `lens` | string | Vilken metod som läste den (se [Lins](#lins)). |
| `source` | string | Det exakta anropet eller sökvägen bakom denna avläsning. |
| `value` | string eller null | Vad den metoden returnerade; `null` om den inte läste något. |
| `error` | string eller null | Närvarande när metoden misslyckades. |
| `elapsedMicros` | number | Hur lång tid avläsningen tog, i mikrosekunder. |
| `compare` | boolean | `true` om denna avläsning deltar i domslutet; `false` om den visas endast som information. |
| `detail` | string eller null | En valfri anteckning om denna avläsning. |

## Uppräkningar

### Domslut

| Värde | Betydelse |
|---|---|
| `MATCH` | Varje jämförbar lins var överens. |
| `MISMATCH` | De jämförbara linserna var inte överens. |
| `SINGLE` | Endast en lins kunde läsa den. |
| `INFO` | Läst för referens, inte röstad om. |
| `EMPTY` | Ingen lins läste ett värde. |
| `ERROR` | Inget värde, och minst en lins gav fel. |

### Lins

| Värde | Etikett |
|---|---|
| `JAVA` | Java / SDK |
| `NATIVE` | Native / JNI |
| `SHELL` | Shell via JVM |
| `SHELL_NATIVE` | Shell via JNI |
| `ATTEST` | TEE / Attestation |

### Kategori

`IDENTITY`, `FINGERPRINT`, `TELEPHONY`, `NETWORK`, `LOCALE`, `BOOT`, `BUILD`,
`HARDWARE`, `DISPLAY`, `SENSORS`, `MEDIA`, `STORAGE`, `PROCESS`, `PACKAGES`,
`ACCOUNTS`, `WEBVIEW`, `SECURITY`, `INTEGRITY`, `EMULATOR`, `SYSTEM`.

## Maskering

När `spec.sensitive` är `true` bär sondens avläsningar ett identitetsbärande värde (ett
serienummer, IMEI, MAC-adress, annons-ID och liknande). I den **delade eller exporterade** rapporten
ersätts de värdena, och deras detaljer, med en redigeringsmarkör så att en rapport som klistras in
på en offentlig plats inte läcker personliga identifierare; sonden, dess domslut och strukturen
bevaras. Kopian på enheten, `last_snapshot.json`, läsbar endast av appen eller av root, behåller de
verkliga värdena.

## Att läsa den

En minimal tolkning: iterera `results`, läs varje `verdict`, och för en fullständig bild ställ upp
`values` efter deras `lens`. Formen ovan är stabil över releaser; tolka mot dessa fältnamn snarare
än skärmpositioner.

Se [AI-agenter](AI-AGENTS.md) för hur man erhåller filen på enheten.

<!-- doc-footer -->

---

> _Denna dokumentation genererades med Claude (Anthropic) och underhålls av VD171. Rättelser och förbättringar välkomnas; se [CONTRIBUTING.md](CONTRIBUTING.md)._
