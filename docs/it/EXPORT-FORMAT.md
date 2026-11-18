> **Languages:** [English](../EXPORT-FORMAT.md) · [العربية](../ar/EXPORT-FORMAT.md) · [Čeština](../cs/EXPORT-FORMAT.md) · [Deutsch](../de/EXPORT-FORMAT.md) · [Español](../es/EXPORT-FORMAT.md) · [فارسی](../fa/EXPORT-FORMAT.md) · [Français](../fr/EXPORT-FORMAT.md) · [हिन्दी](../hi/EXPORT-FORMAT.md) · [Bahasa Indonesia](../id/EXPORT-FORMAT.md) · Italiano · [日本語](../ja/EXPORT-FORMAT.md) · [한국어](../ko/EXPORT-FORMAT.md) · [Nederlands](../nl/EXPORT-FORMAT.md) · [Polski](../pl/EXPORT-FORMAT.md) · [Português](../pt/EXPORT-FORMAT.md) · [Русский](../ru/EXPORT-FORMAT.md) · [Svenska](../sv/EXPORT-FORMAT.md) · [ไทย](../th/EXPORT-FORMAT.md) · [Türkçe](../tr/EXPORT-FORMAT.md) · [Українська](../uk/EXPORT-FORMAT.md) · [Tiếng Việt](../vi/EXPORT-FORMAT.md) · [中文](../zh/EXPORT-FORMAT.md)

---

# Formato di esportazione

Il report che VD Infos esporta - e il `last_snapshot.json` che scrive dopo ogni scansione -
è un unico oggetto JSON con una forma stabile e documentata. Questa pagina è lo schema. Essa
descrive la struttura affinché uno strumento possa analizzarla; non elenca le singole sonde
né interpreta per te alcuna lettura.

## Livello superiore: lo snapshot

```json
{
  "takenAt": 1730000000000,
  "appVersion": "2.23",
  "results": [ /* ProbeResult objects */ ]
}
```

| Campo | Tipo | Significato |
|---|---|---|
| `takenAt` | number | Quando è stata eseguita la scansione, Unix epoch in millisecondi. |
| `appVersion` | string | La versione dell'app che ha prodotto il report. |
| `results` | array | Un `ProbeResult` per sonda, nell'ordine in cui l'app li conserva. |

## Un risultato

Ogni voce in `results` è una sonda con il suo verdetto e ogni lettura delle lenti.

```json
{
  "spec": { /* ProbeSpec */ },
  "values": [ /* LensValue objects */ ],
  "verdict": "MATCH",
  "detail": null
}
```

| Campo | Tipo | Significato |
|---|---|---|
| `spec` | object | Cosa è stato sondato (vedi sotto). |
| `values` | array | Un `LensValue` per ogni metodo che lo ha letto. |
| `verdict` | string | Come si relazionano le letture (vedi [Verdetto](#verdetto)). |
| `detail` | string or null | Una nota facoltativa sul risultato. |

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

| Campo | Tipo | Significato |
|---|---|---|
| `id` | string | Identificatore stabile della sonda. |
| `title` | string | Nome leggibile dalle persone. |
| `category` | string | Una delle [categorie](#categoria). |
| `lenses` | array | Le lenti che questa sonda utilizza. |
| `note` | string or null | Contesto facoltativo. |
| `sensitive` | boolean | `true` quando il valore porta un'identità; tali valori sono **mascherati nel report esportato** (vedi [Mascheramento](#mascheramento)). |
| `solution` | string or null | Un campo suggerimento facoltativo; può essere assente o null. |

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

| Campo | Tipo | Significato |
|---|---|---|
| `lens` | string | Quale metodo lo ha letto (vedi [Lente](#lente)). |
| `source` | string | L'esatta chiamata o percorso dietro questa lettura. |
| `value` | string or null | Cosa ha restituito quel metodo; `null` se non ha letto nulla. |
| `error` | string or null | Presente quando il metodo è fallito. |
| `elapsedMicros` | number | Quanto è durata la lettura, in microsecondi. |
| `compare` | boolean | `true` se questa lettura prende parte al verdetto; `false` se è mostrata solo a titolo informativo. |
| `detail` | string or null | Una nota facoltativa su questa lettura. |

## Enumerazioni

### Verdetto

| Valore | Significato |
|---|---|
| `MATCH` | Ogni lente comparabile è d'accordo. |
| `MISMATCH` | Le lenti comparabili non sono d'accordo. |
| `SINGLE` | Solo una lente ha potuto leggerlo. |
| `INFO` | Letto come riferimento, non sottoposto a votazione. |
| `EMPTY` | Nessuna lente ha letto un valore. |
| `ERROR` | Nessun valore, e almeno una lente ha generato un errore. |

### Lente

| Valore | Etichetta |
|---|---|
| `JAVA` | Java / SDK |
| `NATIVE` | Native / JNI |
| `SHELL` | Shell via JVM |
| `SHELL_NATIVE` | Shell via JNI |
| `ATTEST` | TEE / Attestation |

### Categoria

`IDENTITY`, `FINGERPRINT`, `TELEPHONY`, `NETWORK`, `LOCALE`, `BOOT`, `BUILD`,
`HARDWARE`, `DISPLAY`, `SENSORS`, `MEDIA`, `STORAGE`, `PROCESS`, `PACKAGES`,
`ACCOUNTS`, `WEBVIEW`, `SECURITY`, `INTEGRITY`, `EMULATOR`, `SYSTEM`.

## Mascheramento

Quando `spec.sensitive` è `true`, le letture della sonda portano un valore che porta un'identità
(un numero di serie, IMEI, indirizzo MAC, ID pubblicitario e simili). Nel report **condiviso o esportato**
quei valori, e i loro dettagli, sono sostituiti con un marcatore di redazione affinché un
report incollato in un luogo pubblico non faccia trapelare identificatori personali; la sonda, il suo
verdetto e la struttura sono preservati. Il `last_snapshot.json` **sul dispositivo**,
leggibile solo dall'app o da root, conserva i valori reali.

## Leggerlo

Un'analisi minima: itera `results`, leggi ogni `verdict`, e per un quadro completo allinea
i `values` in base alla loro `lens`. La forma sopra è stabile tra le release; analizza in base a
questi nomi di campo piuttosto che alle posizioni sullo schermo.

Vedi [Agenti AI](AI-AGENTS.md) per come ottenere il file sul dispositivo.

<!-- doc-footer -->

---

> _Questa documentazione è stata generata con Claude (Anthropic) ed è gestita da VD171. Correzioni e miglioramenti sono benvenuti; vedi [CONTRIBUTING.md](CONTRIBUTING.md)._
