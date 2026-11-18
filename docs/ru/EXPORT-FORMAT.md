> **Languages:** [English](../EXPORT-FORMAT.md) · [العربية](../ar/EXPORT-FORMAT.md) · [Čeština](../cs/EXPORT-FORMAT.md) · [Deutsch](../de/EXPORT-FORMAT.md) · [Español](../es/EXPORT-FORMAT.md) · [فارسی](../fa/EXPORT-FORMAT.md) · [Français](../fr/EXPORT-FORMAT.md) · [हिन्दी](../hi/EXPORT-FORMAT.md) · [Bahasa Indonesia](../id/EXPORT-FORMAT.md) · [Italiano](../it/EXPORT-FORMAT.md) · [日本語](../ja/EXPORT-FORMAT.md) · [한국어](../ko/EXPORT-FORMAT.md) · [Nederlands](../nl/EXPORT-FORMAT.md) · [Polski](../pl/EXPORT-FORMAT.md) · [Português](../pt/EXPORT-FORMAT.md) · Русский · [Svenska](../sv/EXPORT-FORMAT.md) · [ไทย](../th/EXPORT-FORMAT.md) · [Türkçe](../tr/EXPORT-FORMAT.md) · [Українська](../uk/EXPORT-FORMAT.md) · [Tiếng Việt](../vi/EXPORT-FORMAT.md) · [中文](../zh/EXPORT-FORMAT.md)

---

# Формат экспорта

Отчёт, который VD Infos экспортирует - и `last_snapshot.json`, который он пишет после каждого сканирования -
это один JSON-объект со стабильной, задокументированной формой. Эта страница - схема. Она
описывает структуру, чтобы инструмент мог её разобрать; она не перечисляет отдельные зонды
и не истолковывает за вас ни одно показание.

## Верхний уровень: снимок

```json
{
  "takenAt": 1730000000000,
  "appVersion": "2.23",
  "results": [ /* ProbeResult objects */ ]
}
```

| Поле | Тип | Значение |
|---|---|---|
| `takenAt` | number | Когда сканирование было сделано, Unix-эпоха в миллисекундах. |
| `appVersion` | string | Версия приложения, которая произвела отчёт. |
| `results` | array | Один `ProbeResult` на зонд, в порядке, в котором приложение их держит. |

## Результат

Каждая запись в `results` - это зонд с его вердиктом и каждым показанием линзы.

```json
{
  "spec": { /* ProbeSpec */ },
  "values": [ /* LensValue objects */ ],
  "verdict": "MATCH",
  "detail": null
}
```

| Поле | Тип | Значение |
|---|---|---|
| `spec` | object | Что было прозондировано (смотрите ниже). |
| `values` | array | Один `LensValue` на каждый метод, который это прочитал. |
| `verdict` | string | Как показания соотносятся (смотрите [Вердикт](#вердикт)). |
| `detail` | string or null | Необязательная заметка о результате. |

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

| Поле | Тип | Значение |
|---|---|---|
| `id` | string | Стабильный идентификатор зонда. |
| `title` | string | Человекочитаемое имя. |
| `category` | string | Одна из [категорий](#категория). |
| `lenses` | array | Линзы, которые использует этот зонд. |
| `note` | string or null | Необязательный контекст. |
| `sensitive` | boolean | `true`, когда значение несёт идентичность; такие значения **маскируются в экспортируемом отчёте** (смотрите [Маскирование](#маскирование)). |
| `solution` | string or null | Необязательное поле подсказки; может отсутствовать или быть null. |

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

| Поле | Тип | Значение |
|---|---|---|
| `lens` | string | Какой метод это прочитал (смотрите [Линза](#линза)). |
| `source` | string | Точный вызов или путь за этим показанием. |
| `value` | string or null | Что этот метод вернул; `null`, если он ничего не прочитал. |
| `error` | string or null | Присутствует, когда метод дал сбой. |
| `elapsedMicros` | number | Сколько времени заняло чтение, в микросекундах. |
| `compare` | boolean | `true`, если это показание участвует в вердикте; `false`, если оно показано только для информации. |
| `detail` | string or null | Необязательная заметка об этом показании. |

## Перечисления

### Вердикт

| Значение | Описание |
|---|---|
| `MATCH` | Каждая сравнимая линза согласилась. |
| `MISMATCH` | Сравнимые линзы не согласились. |
| `SINGLE` | Только одна линза смогла это прочитать. |
| `INFO` | Прочитано для справки, не голосуется. |
| `EMPTY` | Ни одна линза не прочитала значение. |
| `ERROR` | Нет значения, и по крайней мере одна линза дала ошибку. |

### Линза

| Значение | Метка |
|---|---|
| `JAVA` | Java / SDK |
| `NATIVE` | Native / JNI |
| `SHELL` | Shell via JVM |
| `SHELL_NATIVE` | Shell via JNI |
| `ATTEST` | TEE / Attestation |

### Категория

`IDENTITY`, `FINGERPRINT`, `TELEPHONY`, `NETWORK`, `LOCALE`, `BOOT`, `BUILD`,
`HARDWARE`, `DISPLAY`, `SENSORS`, `MEDIA`, `STORAGE`, `PROCESS`, `PACKAGES`,
`ACCOUNTS`, `WEBVIEW`, `SECURITY`, `INTEGRITY`, `EMULATOR`, `SYSTEM`.

## Маскирование

Когда `spec.sensitive` равно `true`, показания зонда несут значение, несущее идентичность
(серийный номер, IMEI, MAC-адрес, рекламный идентификатор и тому подобное). В **отправленном или экспортированном**
отчёте эти значения, и их детали, заменяются маркером редактирования, чтобы
отчёт, вставленный в публичное место, не раскрыл персональные идентификаторы; зонд, его
вердикт и структура сохраняются. Файл `last_snapshot.json` **на устройстве**,
читаемый только приложением или root, хранит реальные значения.

## Как его читать

Минимальный разбор: итерируйте по `results`, читайте каждый `verdict`, а для полной картины выстраивайте
`values` по их `lens`. Форма выше стабильна между релизами; разбирайте по этим
именам полей, а не по позициям на экране.

Смотрите [ИИ-агенты](AI-AGENTS.md) о том, как получить файл на устройстве.

<!-- doc-footer -->

---

> _Эта документация создана с помощью Claude (Anthropic) и поддерживается VD171. Исправления и улучшения приветствуются; см. [CONTRIBUTING.md](CONTRIBUTING.md)._
