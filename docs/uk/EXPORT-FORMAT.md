> **Languages:** [English](../EXPORT-FORMAT.md) · [العربية](../ar/EXPORT-FORMAT.md) · [Čeština](../cs/EXPORT-FORMAT.md) · [Deutsch](../de/EXPORT-FORMAT.md) · [Español](../es/EXPORT-FORMAT.md) · [فارسی](../fa/EXPORT-FORMAT.md) · [Français](../fr/EXPORT-FORMAT.md) · [हिन्दी](../hi/EXPORT-FORMAT.md) · [Bahasa Indonesia](../id/EXPORT-FORMAT.md) · [Italiano](../it/EXPORT-FORMAT.md) · [日本語](../ja/EXPORT-FORMAT.md) · [한국어](../ko/EXPORT-FORMAT.md) · [Nederlands](../nl/EXPORT-FORMAT.md) · [Polski](../pl/EXPORT-FORMAT.md) · [Português](../pt/EXPORT-FORMAT.md) · [Русский](../ru/EXPORT-FORMAT.md) · [Svenska](../sv/EXPORT-FORMAT.md) · [ไทย](../th/EXPORT-FORMAT.md) · [Türkçe](../tr/EXPORT-FORMAT.md) · Українська · [Tiếng Việt](../vi/EXPORT-FORMAT.md) · [中文](../zh/EXPORT-FORMAT.md)

---

# Формат експорту

Звіт, який експортує VD Infos - і `last_snapshot.json`, який він записує після кожного сканування -
це один об'єкт JSON зі стабільною, задокументованою формою. Ця сторінка є схемою. Вона описує
структуру, щоб інструмент міг її розібрати; вона не перелічує окремі проби й не тлумачить для вас
жодного зчитування.

## Верхній рівень: знімок

```json
{
  "takenAt": 1730000000000,
  "appVersion": "2.23",
  "results": [ /* ProbeResult objects */ ]
}
```

| Поле | Тип | Значення |
|---|---|---|
| `takenAt` | number | Коли було зроблено сканування, Unix epoch у мілісекундах. |
| `appVersion` | string | Версія застосунку, що створила звіт. |
| `results` | array | Один `ProbeResult` на пробу, у порядку, в якому їх тримає застосунок. |

## Результат

Кожен запис у `results` - це проба зі своїм присудом і зчитуванням кожної лінзи.

```json
{
  "spec": { /* ProbeSpec */ },
  "values": [ /* LensValue objects */ ],
  "verdict": "MATCH",
  "detail": null
}
```

| Поле | Тип | Значення |
|---|---|---|
| `spec` | object | Що було проаналізовано (див. нижче). |
| `values` | array | Один `LensValue` на кожен метод, що його зчитав. |
| `verdict` | string | Як співвідносяться зчитування (див. [Verdict](#verdict)). |
| `detail` | string or null | Необов'язкова примітка про результат. |

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

| Поле | Тип | Значення |
|---|---|---|
| `id` | string | Стабільний ідентифікатор проби. |
| `title` | string | Зрозуміла людині назва. |
| `category` | string | Одна з [категорій](#category). |
| `lenses` | array | Лінзи, які використовує ця проба. |
| `note` | string or null | Необов'язковий контекст. |
| `sensitive` | boolean | `true`, коли значення несе особу; такі значення **замасковані в експортованому звіті** (див. [Маскування](#маскування)). |
| `solution` | string or null | Необов'язкове поле підказки; може бути відсутнім або null. |

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

| Поле | Тип | Значення |
|---|---|---|
| `lens` | string | Який метод його зчитав (див. [Lens](#lens)). |
| `source` | string | Точний виклик чи шлях за цим зчитуванням. |
| `value` | string or null | Що повернув цей метод; `null`, якщо він нічого не зчитав. |
| `error` | string or null | Присутнє, коли метод зазнав невдачі. |
| `elapsedMicros` | number | Скільки тривало зчитування, у мікросекундах. |
| `compare` | boolean | `true`, якщо це зчитування бере участь у присуді; `false`, якщо воно показане лише для інформації. |
| `detail` | string or null | Необов'язкова примітка про це зчитування. |

## Перелічення

### Verdict

| Значення | Значення |
|---|---|
| `MATCH` | Кожна порівнянна лінза погодилася. |
| `MISMATCH` | Порівнянні лінзи не погодилися. |
| `SINGLE` | Лише одна лінза змогла його зчитати. |
| `INFO` | Зчитано для довідки, не голосується. |
| `EMPTY` | Жодна лінза не зчитала значення. |
| `ERROR` | Немає значення, і принаймні одна лінза дала помилку. |

### Lens

| Значення | Мітка |
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

## Маскування

Коли `spec.sensitive` дорівнює `true`, зчитування проби несуть значення, що несе особу
(серійний номер, IMEI, MAC-адресу, рекламний ідентифікатор тощо). У **поширеному чи
експортованому** звіті ці значення та їхні деталі замінюються маркером редагування, щоб звіт,
вставлений у публічне місце, не витікав особистих ідентифікаторів; проба, її присуд і структура
збережені. `last_snapshot.json` **на пристрої**, який може прочитати лише застосунок або root,
зберігає справжні значення.

## Як його читати

Мінімальний розбір: ітеруйте `results`, читайте кожен `verdict`, а для повної картини вишикуйте
`values` за їхньою `lens`. Форма вище стабільна між випусками; розбирайте за цими іменами полів, а
не за позиціями на екрані.

Див. [ШІ-агенти](AI-AGENTS.md) про те, як отримати файл на пристрої.

<!-- doc-footer -->

---

> _Ця документація створена за допомогою Claude (Anthropic) і підтримується VD171. Виправлення та покращення вітаються; див. [CONTRIBUTING.md](CONTRIBUTING.md)._
