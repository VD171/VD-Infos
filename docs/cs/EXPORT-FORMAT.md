> **Languages:** [English](../EXPORT-FORMAT.md) · [العربية](../ar/EXPORT-FORMAT.md) · Čeština · [Deutsch](../de/EXPORT-FORMAT.md) · [Español](../es/EXPORT-FORMAT.md) · [فارسی](../fa/EXPORT-FORMAT.md) · [Français](../fr/EXPORT-FORMAT.md) · [हिन्दी](../hi/EXPORT-FORMAT.md) · [Bahasa Indonesia](../id/EXPORT-FORMAT.md) · [Italiano](../it/EXPORT-FORMAT.md) · [日本語](../ja/EXPORT-FORMAT.md) · [한국어](../ko/EXPORT-FORMAT.md) · [Nederlands](../nl/EXPORT-FORMAT.md) · [Polski](../pl/EXPORT-FORMAT.md) · [Português](../pt/EXPORT-FORMAT.md) · [Русский](../ru/EXPORT-FORMAT.md) · [Svenska](../sv/EXPORT-FORMAT.md) · [ไทย](../th/EXPORT-FORMAT.md) · [Türkçe](../tr/EXPORT-FORMAT.md) · [Українська](../uk/EXPORT-FORMAT.md) · [Tiếng Việt](../vi/EXPORT-FORMAT.md) · [中文](../zh/EXPORT-FORMAT.md)

---

# Formát exportu

Zpráva, kterou VD Infos exportuje - a `last_snapshot.json`, kterou zapisuje po každém
skenu - je jeden objekt JSON se stabilním, zdokumentovaným tvarem. Tato stránka je
schéma. Popisuje strukturu, aby ji nástroj mohl parsovat; nevypisuje jednotlivé sondy ani
za vás neinterpretuje žádný naměřený údaj.

## Nejvyšší úroveň: snapshot

```json
{
  "takenAt": 1730000000000,
  "appVersion": "2.23",
  "results": [ /* ProbeResult objects */ ]
}
```

| Pole | Typ | Význam |
|---|---|---|
| `takenAt` | number | Kdy byl sken pořízen, unixová epocha v milisekundách. |
| `appVersion` | string | Verze aplikace, která zprávu vytvořila. |
| `results` | array | Jeden `ProbeResult` na sondu, v pořadí, v jakém je aplikace uchovává. |

## Výsledek

Každá položka v `results` je sonda se svým verdiktem a každým naměřeným údajem čočky.

```json
{
  "spec": { /* ProbeSpec */ },
  "values": [ /* LensValue objects */ ],
  "verdict": "MATCH",
  "detail": null
}
```

| Pole | Typ | Význam |
|---|---|---|
| `spec` | object | Co bylo sondováno (viz níže). |
| `values` | array | Jeden `LensValue` na metodu, která to přečetla. |
| `verdict` | string | Jak naměřené údaje spolu souvisejí (viz [Verdikt](#verdikt)). |
| `detail` | string or null | Volitelná poznámka k výsledku. |

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

| Pole | Typ | Význam |
|---|---|---|
| `id` | string | Stabilní identifikátor sondy. |
| `title` | string | Pro člověka čitelný název. |
| `category` | string | Jedna z [kategorií](#kategorie). |
| `lenses` | array | Čočky, které tato sonda používá. |
| `note` | string or null | Volitelný kontext. |
| `sensitive` | boolean | `true`, když hodnota nese identitu; takové hodnoty jsou **v exportované zprávě maskované** (viz [Maskování](#maskování)). |
| `solution` | string or null | Volitelné pole s nápovědou; může chybět nebo být null. |

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

| Pole | Typ | Význam |
|---|---|---|
| `lens` | string | Která metoda to přečetla (viz [Čočka](#čočka)). |
| `source` | string | Přesné volání nebo cesta za tímto naměřeným údajem. |
| `value` | string or null | Co ta metoda vrátila; `null`, pokud nic nepřečetla. |
| `error` | string or null | Přítomné, když metoda selhala. |
| `elapsedMicros` | number | Jak dlouho čtení trvalo, v mikrosekundách. |
| `compare` | boolean | `true`, pokud se tento naměřený údaj účastní verdiktu; `false`, pokud je zobrazen jen pro informaci. |
| `detail` | string or null | Volitelná poznámka k tomuto naměřenému údaji. |

## Výčty

### Verdikt

| Hodnota | Význam |
|---|---|
| `MATCH` | Každá porovnatelná čočka se shodla. |
| `MISMATCH` | Porovnatelné čočky se neshodly. |
| `SINGLE` | Jen jedna čočka to dokázala přečíst. |
| `INFO` | Přečteno pro referenci, nehlasuje se. |
| `EMPTY` | Žádná čočka nepřečetla hodnotu. |
| `ERROR` | Žádná hodnota a alespoň jedna čočka chybovala. |

### Čočka

| Hodnota | Označení |
|---|---|
| `JAVA` | Java / SDK |
| `NATIVE` | Nativní / JNI |
| `SHELL` | Shell přes JVM |
| `SHELL_NATIVE` | Shell přes JNI |
| `ATTEST` | TEE / Atestace |

### Kategorie

`IDENTITY`, `FINGERPRINT`, `TELEPHONY`, `NETWORK`, `LOCALE`, `BOOT`, `BUILD`,
`HARDWARE`, `DISPLAY`, `SENSORS`, `MEDIA`, `STORAGE`, `PROCESS`, `PACKAGES`,
`ACCOUNTS`, `WEBVIEW`, `SECURITY`, `INTEGRITY`, `EMULATOR`, `SYSTEM`.

## Maskování

Když je `spec.sensitive` `true`, naměřené údaje sondy nesou hodnotu nesoucí identitu
(sériové číslo, IMEI, MAC adresu, reklamní ID a podobně). Ve **sdílené nebo exportované**
zprávě jsou tyto hodnoty a jejich detaily nahrazeny značkou začernění, aby zpráva vložená
na veřejné místo neunikla osobní identifikátory; sonda, její verdikt a struktura jsou
zachovány. Soubor `last_snapshot.json` **na zařízení**, čitelný jen aplikací nebo rootem,
uchovává skutečné hodnoty.

## Jak ho číst

Minimální parsování: iterujte přes `results`, přečtěte každý `verdict`, a pro úplný obraz
seřaďte `values` podle jejich `lens`. Výše uvedený tvar je stabilní napříč vydáními;
parsujte proti těmto názvům polí, ne proti pozicím na obrazovce.

Viz [AI agenti](AI-AGENTS.md), jak soubor na zařízení získat.

<!-- doc-footer -->

---

> _Tato dokumentace byla vytvořena pomocí Claude (Anthropic) a spravuje ji VD171. Opravy a vylepšení jsou vítány; viz [CONTRIBUTING.md](CONTRIBUTING.md)._
