> **Languages:** [English](../EXPORT-FORMAT.md) · [العربية](../ar/EXPORT-FORMAT.md) · [Čeština](../cs/EXPORT-FORMAT.md) · [Deutsch](../de/EXPORT-FORMAT.md) · Español · [فارسی](../fa/EXPORT-FORMAT.md) · [Français](../fr/EXPORT-FORMAT.md) · [हिन्दी](../hi/EXPORT-FORMAT.md) · [Bahasa Indonesia](../id/EXPORT-FORMAT.md) · [Italiano](../it/EXPORT-FORMAT.md) · [日本語](../ja/EXPORT-FORMAT.md) · [한국어](../ko/EXPORT-FORMAT.md) · [Nederlands](../nl/EXPORT-FORMAT.md) · [Polski](../pl/EXPORT-FORMAT.md) · [Português](../pt/EXPORT-FORMAT.md) · [Русский](../ru/EXPORT-FORMAT.md) · [Svenska](../sv/EXPORT-FORMAT.md) · [ไทย](../th/EXPORT-FORMAT.md) · [Türkçe](../tr/EXPORT-FORMAT.md) · [Українська](../uk/EXPORT-FORMAT.md) · [Tiếng Việt](../vi/EXPORT-FORMAT.md) · [中文](../zh/EXPORT-FORMAT.md)

---

# Formato de exportación

El informe que VD Infos exporta - y el `last_snapshot.json` que escribe tras cada escaneo -
es un objeto JSON con una forma estable y documentada. Esta página es el esquema. Describe
la estructura para que una herramienta pueda analizarla; no enumera las sondas individuales
ni interpreta ninguna lectura por ti.

## Nivel superior: la instantánea

```json
{
  "takenAt": 1730000000000,
  "appVersion": "2.23",
  "results": [ /* ProbeResult objects */ ]
}
```

| Campo | Tipo | Significado |
|---|---|---|
| `takenAt` | number | Cuándo se tomó el escaneo, epoch Unix en milisegundos. |
| `appVersion` | string | La versión de la aplicación que produjo el informe. |
| `results` | array | Un `ProbeResult` por sonda, en el orden en que la aplicación los guarda. |

## Un resultado

Cada entrada en `results` es una sonda con su veredicto y la lectura de cada lente.

```json
{
  "spec": { /* ProbeSpec */ },
  "values": [ /* LensValue objects */ ],
  "verdict": "MATCH",
  "detail": null
}
```

| Campo | Tipo | Significado |
|---|---|---|
| `spec` | object | Qué se sondeó (ver abajo). |
| `values` | array | Un `LensValue` por cada método que lo leyó. |
| `verdict` | string | Cómo se relacionan las lecturas (ver [Veredicto](#veredicto)). |
| `detail` | string or null | Una nota opcional sobre el resultado. |

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

| Campo | Tipo | Significado |
|---|---|---|
| `id` | string | Identificador estable de la sonda. |
| `title` | string | Nombre legible por humanos. |
| `category` | string | Una de las [categorías](#categoría). |
| `lenses` | array | Las lentes que usa esta sonda. |
| `note` | string or null | Contexto opcional. |
| `sensitive` | boolean | `true` cuando el valor porta identidad; tales valores están **enmascarados en el informe exportado** (ver [Enmascaramiento](#enmascaramiento)). |
| `solution` | string or null | Un campo opcional de sugerencia; puede estar ausente o ser null. |

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

| Campo | Tipo | Significado |
|---|---|---|
| `lens` | string | Qué método lo leyó (ver [Lente](#lente)). |
| `source` | string | La llamada o ruta exacta detrás de esta lectura. |
| `value` | string or null | Lo que devolvió ese método; `null` si no leyó nada. |
| `error` | string or null | Presente cuando el método falló. |
| `elapsedMicros` | number | Cuánto tardó la lectura, en microsegundos. |
| `compare` | boolean | `true` si esta lectura participa en el veredicto; `false` si se muestra solo como información. |
| `detail` | string or null | Una nota opcional sobre esta lectura. |

## Enumeraciones

### Veredicto

| Valor | Significado |
|---|---|
| `MATCH` | Todas las lentes comparables coincidieron. |
| `MISMATCH` | Las lentes comparables no coincidieron. |
| `SINGLE` | Solo una lente pudo leerlo. |
| `INFO` | Leído como referencia, no sometido a votación. |
| `EMPTY` | Ninguna lente leyó un valor. |
| `ERROR` | Sin valor, y al menos una lente dio error. |

### Lente

| Valor | Etiqueta |
|---|---|
| `JAVA` | Java / SDK |
| `NATIVE` | Nativo / JNI |
| `SHELL` | Shell vía JVM |
| `SHELL_NATIVE` | Shell vía JNI |
| `ATTEST` | TEE / Atestación |

### Categoría

`IDENTITY`, `FINGERPRINT`, `TELEPHONY`, `NETWORK`, `LOCALE`, `BOOT`, `BUILD`,
`HARDWARE`, `DISPLAY`, `SENSORS`, `MEDIA`, `STORAGE`, `PROCESS`, `PACKAGES`,
`ACCOUNTS`, `WEBVIEW`, `SECURITY`, `INTEGRITY`, `EMULATOR`, `SYSTEM`.

## Enmascaramiento

Cuando `spec.sensitive` es `true`, las lecturas de la sonda llevan un valor que porta identidad
(un número de serie, IMEI, dirección MAC, identificador de publicidad y similares). En el informe **compartido o exportado**
esos valores, y sus detalles, se reemplazan con un marcador de redacción para que un
informe pegado en un lugar público no filtre identificadores personales; la sonda, su
veredicto y la estructura se conservan. El `last_snapshot.json` **en el dispositivo**,
legible solo por la aplicación o por root, conserva los valores reales.

## Leer esto

Un análisis mínimo: itera `results`, lee cada `verdict`, y para una imagen completa alinea
los `values` por su `lens`. La forma de arriba es estable a lo largo de las versiones; analiza contra
estos nombres de campo en lugar de posiciones en pantalla.

Consulta [Agentes de IA](AI-AGENTS.md) para saber cómo obtener el archivo en el dispositivo.

<!-- doc-footer -->

---

> _Esta documentación se generó con Claude (Anthropic) y la mantiene VD171. Las correcciones y mejoras son bienvenidas; consulta [CONTRIBUTING.md](CONTRIBUTING.md)._
