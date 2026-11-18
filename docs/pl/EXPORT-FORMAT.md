> **Languages:** [English](../EXPORT-FORMAT.md) · [العربية](../ar/EXPORT-FORMAT.md) · [Čeština](../cs/EXPORT-FORMAT.md) · [Deutsch](../de/EXPORT-FORMAT.md) · [Español](../es/EXPORT-FORMAT.md) · [فارسی](../fa/EXPORT-FORMAT.md) · [Français](../fr/EXPORT-FORMAT.md) · [हिन्दी](../hi/EXPORT-FORMAT.md) · [Bahasa Indonesia](../id/EXPORT-FORMAT.md) · [Italiano](../it/EXPORT-FORMAT.md) · [日本語](../ja/EXPORT-FORMAT.md) · [한국어](../ko/EXPORT-FORMAT.md) · [Nederlands](../nl/EXPORT-FORMAT.md) · Polski · [Português](../pt/EXPORT-FORMAT.md) · [Русский](../ru/EXPORT-FORMAT.md) · [Svenska](../sv/EXPORT-FORMAT.md) · [ไทย](../th/EXPORT-FORMAT.md) · [Türkçe](../tr/EXPORT-FORMAT.md) · [Українська](../uk/EXPORT-FORMAT.md) · [Tiếng Việt](../vi/EXPORT-FORMAT.md) · [中文](../zh/EXPORT-FORMAT.md)

---

# Format eksportu

Raport, który VD Infos eksportuje - oraz `last_snapshot.json`, który zapisuje po każdym skanowaniu -
jest jednym obiektem JSON o stabilnym, udokumentowanym kształcie. Ta strona jest schematem. Opisuje
strukturę, aby narzędzie mogło ją sparsować; nie wymienia poszczególnych sond
ani nie interpretuje za ciebie żadnego odczytu.

## Najwyższy poziom: migawka

```json
{
  "takenAt": 1730000000000,
  "appVersion": "2.23",
  "results": [ /* ProbeResult objects */ ]
}
```

| Pole | Typ | Znaczenie |
|---|---|---|
| `takenAt` | number | Kiedy skanowanie zostało wykonane, czas uniksowy w milisekundach. |
| `appVersion` | string | Wersja aplikacji, która wytworzyła raport. |
| `results` | array | Jeden `ProbeResult` na sondę, w kolejności, w jakiej aplikacja je przechowuje. |

## Wynik

Każdy wpis w `results` to sonda z jej werdyktem i każdym odczytem soczewki.

```json
{
  "spec": { /* ProbeSpec */ },
  "values": [ /* LensValue objects */ ],
  "verdict": "MATCH",
  "detail": null
}
```

| Pole | Typ | Znaczenie |
|---|---|---|
| `spec` | object | Co zostało zbadane (zobacz niżej). |
| `values` | array | Jeden `LensValue` na metodę, która to odczytała. |
| `verdict` | string | Jak odczyty mają się do siebie (zobacz [Werdykt](#werdykt)). |
| `detail` | string or null | Opcjonalna notatka o wyniku. |

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

| Pole | Typ | Znaczenie |
|---|---|---|
| `id` | string | Stabilny identyfikator sondy. |
| `title` | string | Nazwa czytelna dla człowieka. |
| `category` | string | Jedna z [kategorii](#kategoria). |
| `lenses` | array | Soczewki, których używa ta sonda. |
| `note` | string or null | Opcjonalny kontekst. |
| `sensitive` | boolean | `true`, gdy wartość nosi tożsamość; takie wartości są **maskowane w wyeksportowanym raporcie** (zobacz [Maskowanie](#maskowanie)). |
| `solution` | string or null | Opcjonalne pole podpowiedzi; może być nieobecne lub null. |

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

| Pole | Typ | Znaczenie |
|---|---|---|
| `lens` | string | Która metoda to odczytała (zobacz [Soczewka](#soczewka)). |
| `source` | string | Dokładne wywołanie lub ścieżka stojąca za tym odczytem. |
| `value` | string or null | Co ta metoda zwróciła; `null`, jeśli nic nie odczytała. |
| `error` | string or null | Obecne, gdy metoda zawiodła. |
| `elapsedMicros` | number | Jak długo trwał odczyt, w mikrosekundach. |
| `compare` | boolean | `true`, jeśli ten odczyt bierze udział w werdykcie; `false`, jeśli jest pokazany tylko dla informacji. |
| `detail` | string or null | Opcjonalna notatka o tym odczycie. |

## Wyliczenia

### Werdykt

| Wartość | Znaczenie |
|---|---|
| `MATCH` | Każda porównywalna soczewka się zgadzała. |
| `MISMATCH` | Porównywalne soczewki się nie zgadzały. |
| `SINGLE` | Tylko jedna soczewka mogła to odczytać. |
| `INFO` | Odczytane dla odniesienia, nad czym nie głosowano. |
| `EMPTY` | Żadna soczewka nie odczytała wartości. |
| `ERROR` | Brak wartości i przynajmniej jedna soczewka zgłosiła błąd. |

### Soczewka

| Wartość | Etykieta |
|---|---|
| `JAVA` | Java / SDK |
| `NATIVE` | Native / JNI |
| `SHELL` | Shell via JVM |
| `SHELL_NATIVE` | Shell via JNI |
| `ATTEST` | TEE / Attestation |

### Kategoria

`IDENTITY`, `FINGERPRINT`, `TELEPHONY`, `NETWORK`, `LOCALE`, `BOOT`, `BUILD`,
`HARDWARE`, `DISPLAY`, `SENSORS`, `MEDIA`, `STORAGE`, `PROCESS`, `PACKAGES`,
`ACCOUNTS`, `WEBVIEW`, `SECURITY`, `INTEGRITY`, `EMULATOR`, `SYSTEM`.

## Maskowanie

Gdy `spec.sensitive` ma wartość `true`, odczyty sondy niosą wartość noszącą tożsamość
(numer seryjny, IMEI, adres MAC, identyfikator reklamowy i tym podobne). W **udostępnionym lub wyeksportowanym**
raporcie te wartości, oraz ich szczegóły, są zastępowane znacznikiem redakcji, tak aby
raport wklejony w publiczne miejsce nie ujawnił osobistych identyfikatorów; sonda, jej
werdykt i struktura są zachowane. **Na urządzeniu** `last_snapshot.json`,
odczytywalny tylko przez aplikację lub przez root, zachowuje prawdziwe wartości.

## Odczytywanie go

Minimalny parsing: iteruj po `results`, odczytaj każdy `verdict`, a dla pełnego obrazu ustaw obok siebie
wartości `values` według ich `lens`. Powyższy kształt jest stabilny między wydaniami; parsuj według
tych nazw pól, a nie pozycji na ekranie.

Zobacz [Agenci AI](AI-AGENTS.md), aby dowiedzieć się, jak zdobyć plik na urządzeniu.

<!-- doc-footer -->

---

> _Ta dokumentacja została wygenerowana za pomocą Claude (Anthropic) i jest utrzymywana przez VD171. Poprawki i ulepszenia są mile widziane; zobacz [CONTRIBUTING.md](CONTRIBUTING.md)._
