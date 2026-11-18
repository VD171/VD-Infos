> **Languages:** [English](../EXPORT-FORMAT.md) · [العربية](../ar/EXPORT-FORMAT.md) · [Čeština](../cs/EXPORT-FORMAT.md) · [Deutsch](../de/EXPORT-FORMAT.md) · [Español](../es/EXPORT-FORMAT.md) · [فارسی](../fa/EXPORT-FORMAT.md) · [Français](../fr/EXPORT-FORMAT.md) · [हिन्दी](../hi/EXPORT-FORMAT.md) · [Bahasa Indonesia](../id/EXPORT-FORMAT.md) · [Italiano](../it/EXPORT-FORMAT.md) · [日本語](../ja/EXPORT-FORMAT.md) · [한국어](../ko/EXPORT-FORMAT.md) · [Nederlands](../nl/EXPORT-FORMAT.md) · [Polski](../pl/EXPORT-FORMAT.md) · Português · [Русский](../ru/EXPORT-FORMAT.md) · [Svenska](../sv/EXPORT-FORMAT.md) · [ไทย](../th/EXPORT-FORMAT.md) · [Türkçe](../tr/EXPORT-FORMAT.md) · [Українська](../uk/EXPORT-FORMAT.md) · [Tiếng Việt](../vi/EXPORT-FORMAT.md) · [中文](../zh/EXPORT-FORMAT.md)

---

# Formato de exportação

O relatório que o VD Infos exporta - e o `last_snapshot.json` que ele escreve após cada varredura -
é um objeto JSON com um formato estável e documentado. Esta página é o schema. Ela
descreve a estrutura para que uma ferramenta possa analisá-la; ela não lista as sondas
individuais nem interpreta nenhuma leitura para você.

## Nível superior: o snapshot

```json
{
  "takenAt": 1730000000000,
  "appVersion": "2.23",
  "results": [ /* ProbeResult objects */ ]
}
```

| Campo | Tipo | Significado |
|---|---|---|
| `takenAt` | number | Quando a varredura foi feita, época Unix em milissegundos. |
| `appVersion` | string | A versão do app que produziu o relatório. |
| `results` | array | Um `ProbeResult` por sonda, na ordem em que o app os mantém. |

## Um resultado

Cada entrada em `results` é uma sonda com seu veredito e cada leitura de lente.

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
| `spec` | object | O que foi sondado (veja abaixo). |
| `values` | array | Um `LensValue` por método que o leu. |
| `verdict` | string | Como as leituras se relacionam (veja [Veredito](#veredito)). |
| `detail` | string or null | Uma nota opcional sobre o resultado. |

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
| `id` | string | Identificador estável da sonda. |
| `title` | string | Nome legível por humanos. |
| `category` | string | Uma das [categorias](#categoria). |
| `lenses` | array | As lentes que esta sonda usa. |
| `note` | string or null | Contexto opcional. |
| `sensitive` | boolean | `true` quando o valor carrega identidade; tais valores são **mascarados no relatório exportado** (veja [Mascaramento](#mascaramento)). |
| `solution` | string or null | Um campo de dica opcional; pode estar ausente ou null. |

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
| `lens` | string | Qual método o leu (veja [Lente](#lente)). |
| `source` | string | A chamada ou o caminho exato por trás desta leitura. |
| `value` | string or null | O que aquele método retornou; `null` se não leu nada. |
| `error` | string or null | Presente quando o método falhou. |
| `elapsedMicros` | number | Quanto tempo a leitura levou, em microssegundos. |
| `compare` | boolean | `true` se esta leitura participa do veredito; `false` se é mostrada apenas para informação. |
| `detail` | string or null | Uma nota opcional sobre esta leitura. |

## Enumerações

### Veredito

| Valor | Significado |
|---|---|
| `MATCH` | Toda lente comparável concordou. |
| `MISMATCH` | As lentes comparáveis não concordaram. |
| `SINGLE` | Apenas uma lente conseguiu lê-lo. |
| `INFO` | Lido para referência, não votado. |
| `EMPTY` | Nenhuma lente leu um valor. |
| `ERROR` | Sem valor, e ao menos uma lente deu erro. |

### Lente

| Valor | Rótulo |
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

## Mascaramento

Quando `spec.sensitive` é `true`, as leituras da sonda carregam um valor que carrega identidade
(um número de série, IMEI, endereço MAC, ID de publicidade e afins). No relatório **compartilhado ou exportado**
esses valores, e seus detalhes, são substituídos por um marcador de redação para que um
relatório colado em um lugar público não vaze identificadores pessoais; a sonda, seu
veredito e a estrutura são preservados. O `last_snapshot.json` **no dispositivo**,
legível apenas pelo app ou pelo root, mantém os valores reais.

## Lendo o arquivo

Uma análise mínima: itere por `results`, leia cada `verdict` e, para um quadro completo, alinhe
os `values` pela sua `lens`. O formato acima é estável entre lançamentos; analise com base nesses
nomes de campos em vez de posições na tela.

Veja [Agentes de IA](AI-AGENTS.md) para como obter o arquivo no dispositivo.

<!-- doc-footer -->

---

> _Esta documentação foi gerada com o Claude (Anthropic) e é mantida por VD171. Correções e melhorias são bem-vindas; veja [CONTRIBUTING.md](CONTRIBUTING.md)._
