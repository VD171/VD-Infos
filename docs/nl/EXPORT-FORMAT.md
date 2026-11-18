> **Languages:** [English](../EXPORT-FORMAT.md) · [العربية](../ar/EXPORT-FORMAT.md) · [Čeština](../cs/EXPORT-FORMAT.md) · [Deutsch](../de/EXPORT-FORMAT.md) · [Español](../es/EXPORT-FORMAT.md) · [فارسی](../fa/EXPORT-FORMAT.md) · [Français](../fr/EXPORT-FORMAT.md) · [हिन्दी](../hi/EXPORT-FORMAT.md) · [Bahasa Indonesia](../id/EXPORT-FORMAT.md) · [Italiano](../it/EXPORT-FORMAT.md) · [日本語](../ja/EXPORT-FORMAT.md) · [한국어](../ko/EXPORT-FORMAT.md) · Nederlands · [Polski](../pl/EXPORT-FORMAT.md) · [Português](../pt/EXPORT-FORMAT.md) · [Русский](../ru/EXPORT-FORMAT.md) · [Svenska](../sv/EXPORT-FORMAT.md) · [ไทย](../th/EXPORT-FORMAT.md) · [Türkçe](../tr/EXPORT-FORMAT.md) · [Українська](../uk/EXPORT-FORMAT.md) · [Tiếng Việt](../vi/EXPORT-FORMAT.md) · [中文](../zh/EXPORT-FORMAT.md)

---

# Exportformaat

Het rapport dat VD Infos exporteert - en de `last_snapshot.json` die het na elke scan
schrijft - is een JSON-object met een stabiele, gedocumenteerde vorm. Deze pagina is het
schema. Het beschrijft de structuur zodat een tool het kan parseren; het somt de
afzonderlijke probes niet op en interpreteert geen enkele meting voor je.

## Bovenste niveau: de snapshot

```json
{
  "takenAt": 1730000000000,
  "appVersion": "2.23",
  "results": [ /* ProbeResult objects */ ]
}
```

| Veld | Type | Betekenis |
|---|---|---|
| `takenAt` | getal | Wanneer de scan is genomen, Unix-epoch in milliseconden. |
| `appVersion` | tekenreeks | De appversie die het rapport produceerde. |
| `results` | array | Een `ProbeResult` per probe, in de volgorde waarin de app ze bewaart. |

## Een resultaat

Elk item in `results` is een probe met zijn oordeel en elke lensmeting.

```json
{
  "spec": { /* ProbeSpec */ },
  "values": [ /* LensValue objects */ ],
  "verdict": "MATCH",
  "detail": null
}
```

| Veld | Type | Betekenis |
|---|---|---|
| `spec` | object | Wat er geprobed werd (zie hieronder). |
| `values` | array | Een `LensValue` per methode die het las. |
| `verdict` | tekenreeks | Hoe de metingen zich verhouden (zie [Oordeel](#oordeel)). |
| `detail` | tekenreeks of null | Een optionele notitie over het resultaat. |

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

| Veld | Type | Betekenis |
|---|---|---|
| `id` | tekenreeks | Stabiele identificator van de probe. |
| `title` | tekenreeks | Voor mensen leesbare naam. |
| `category` | tekenreeks | Een van de [categorieen](#categorie). |
| `lenses` | array | De lenzen die deze probe gebruikt. |
| `note` | tekenreeks of null | Optionele context. |
| `sensitive` | boolean | `true` wanneer de waarde identiteitsdragend is; zulke waarden worden **gemaskeerd in het geexporteerde rapport** (zie [Maskeren](#maskeren)). |
| `solution` | tekenreeks of null | Een optioneel hintveld; kan afwezig of null zijn. |

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

| Veld | Type | Betekenis |
|---|---|---|
| `lens` | tekenreeks | Welke methode het las (zie [Lens](#lens)). |
| `source` | tekenreeks | De exacte aanroep of het pad achter deze meting. |
| `value` | tekenreeks of null | Wat die methode teruggaf; `null` als het niets las. |
| `error` | tekenreeks of null | Aanwezig wanneer de methode faalde. |
| `elapsedMicros` | getal | Hoe lang de meting duurde, in microseconden. |
| `compare` | boolean | `true` als deze meting deelneemt aan het oordeel; `false` als ze alleen ter informatie wordt getoond. |
| `detail` | tekenreeks of null | Een optionele notitie over deze meting. |

## Opsommingen

### Oordeel

| Waarde | Betekenis |
|---|---|
| `MATCH` | Elke vergelijkbare lens was het eens. |
| `MISMATCH` | De vergelijkbare lenzen waren het niet eens. |
| `SINGLE` | Slechts een lens kon het lezen. |
| `INFO` | Ter referentie gelezen, niet over gestemd. |
| `EMPTY` | Geen enkele lens las een waarde. |
| `ERROR` | Geen waarde, en ten minste een lens gaf een fout. |

### Lens

| Waarde | Label |
|---|---|
| `JAVA` | Java / SDK |
| `NATIVE` | Native / JNI |
| `SHELL` | Shell via JVM |
| `SHELL_NATIVE` | Shell via JNI |
| `ATTEST` | TEE / Attestation |

### Categorie

`IDENTITY`, `FINGERPRINT`, `TELEPHONY`, `NETWORK`, `LOCALE`, `BOOT`, `BUILD`,
`HARDWARE`, `DISPLAY`, `SENSORS`, `MEDIA`, `STORAGE`, `PROCESS`, `PACKAGES`,
`ACCOUNTS`, `WEBVIEW`, `SECURITY`, `INTEGRITY`, `EMULATOR`, `SYSTEM`.

## Maskeren

Wanneer `spec.sensitive` `true` is, dragen de metingen van de probe een
identiteitsdragende waarde (een serienummer, IMEI, MAC-adres, advertentie-ID en
dergelijke). In het **gedeelde of geexporteerde** rapport worden die waarden, en hun
details, vervangen door een redactiemarkering zodat een rapport dat op een openbare plek
wordt geplakt geen persoonlijke identificatoren lekt; de probe, zijn oordeel en de
structuur blijven behouden. De **op het apparaat** staande `last_snapshot.json`, alleen
leesbaar door de app of door root, behoudt de echte waarden.

## Het lezen

Een minimale parse: doorloop `results`, lees elk `verdict`, en zet voor een volledig
beeld de `values` naast elkaar op hun `lens`. De vorm hierboven is stabiel over releases
heen; parseer tegen deze veldnamen in plaats van schermposities.

Zie [AI-agents](AI-AGENTS.md) voor hoe je het bestand op het apparaat verkrijgt.

<!-- doc-footer -->

---

> _Deze documentatie is gegenereerd met Claude (Anthropic) en wordt onderhouden door VD171. Correcties en verbeteringen zijn welkom; zie [CONTRIBUTING.md](CONTRIBUTING.md)._
