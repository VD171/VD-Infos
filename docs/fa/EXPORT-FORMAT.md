> **Languages:** [English](../EXPORT-FORMAT.md) · [العربية](../ar/EXPORT-FORMAT.md) · [Čeština](../cs/EXPORT-FORMAT.md) · [Deutsch](../de/EXPORT-FORMAT.md) · [Español](../es/EXPORT-FORMAT.md) · فارسی · [Français](../fr/EXPORT-FORMAT.md) · [हिन्दी](../hi/EXPORT-FORMAT.md) · [Bahasa Indonesia](../id/EXPORT-FORMAT.md) · [Italiano](../it/EXPORT-FORMAT.md) · [日本語](../ja/EXPORT-FORMAT.md) · [한국어](../ko/EXPORT-FORMAT.md) · [Nederlands](../nl/EXPORT-FORMAT.md) · [Polski](../pl/EXPORT-FORMAT.md) · [Português](../pt/EXPORT-FORMAT.md) · [Русский](../ru/EXPORT-FORMAT.md) · [Svenska](../sv/EXPORT-FORMAT.md) · [ไทย](../th/EXPORT-FORMAT.md) · [Türkçe](../tr/EXPORT-FORMAT.md) · [Українська](../uk/EXPORT-FORMAT.md) · [Tiếng Việt](../vi/EXPORT-FORMAT.md) · [中文](../zh/EXPORT-FORMAT.md)

---

# قالب خروجی

گزارشی که VD Infos خروجی می‌گیرد - و `last_snapshot.json` که پس از هر اسکن می‌نویسد -
یک شیء JSON با شکلی پایدار و مستند است. این صفحه طرحواره است. ساختار را
توصیف می‌کند تا یک ابزار بتواند آن را تجزیه کند؛ کاوشگرهای منفرد را فهرست نمی‌کند
و هیچ خوانشی را برای شما تفسیر نمی‌کند.

## سطح بالا: عکس فوری

```json
{
  "takenAt": 1730000000000,
  "appVersion": "2.23",
  "results": [ /* ProbeResult objects */ ]
}
```

| فیلد | نوع | معنا |
|---|---|---|
| `takenAt` | number | زمانی که اسکن گرفته شد، epoch یونیکس به میلی‌ثانیه. |
| `appVersion` | string | نسخهٔ برنامه‌ای که گزارش را تولید کرد. |
| `results` | array | یک `ProbeResult` به‌ازای هر کاوشگر، به ترتیبی که برنامه آن‌ها را نگه می‌دارد. |

## یک نتیجه

هر ورودی در `results` یک کاوشگر با حکم خود و خوانش هر عدسی است.

```json
{
  "spec": { /* ProbeSpec */ },
  "values": [ /* LensValue objects */ ],
  "verdict": "MATCH",
  "detail": null
}
```

| فیلد | نوع | معنا |
|---|---|---|
| `spec` | object | چه چیزی کاوش شد (پایین‌تر را ببینید). |
| `values` | array | یک `LensValue` به‌ازای هر متدی که آن را خواند. |
| `verdict` | string | خوانش‌ها چگونه به هم مربوط‌اند ([حکم](#حکم) را ببینید). |
| `detail` | string or null | یادداشتی اختیاری دربارهٔ نتیجه. |

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

| فیلد | نوع | معنا |
|---|---|---|
| `id` | string | شناسهٔ پایدار کاوشگر. |
| `title` | string | نام انسان‌خوان. |
| `category` | string | یکی از [دسته‌ها](#دسته). |
| `lenses` | array | عدسی‌هایی که این کاوشگر استفاده می‌کند. |
| `note` | string or null | زمینهٔ اختیاری. |
| `sensitive` | boolean | `true` هنگامی که مقدار هویت‌دار است؛ چنین مقادیری **در گزارش خروجی‌گرفته‌شده پوشانده می‌شوند** ([پوشاندن](#پوشاندن) را ببینید). |
| `solution` | string or null | یک فیلد اشارهٔ اختیاری؛ ممکن است غایب یا null باشد. |

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

| فیلد | نوع | معنا |
|---|---|---|
| `lens` | string | کدام متد آن را خواند ([عدسی](#عدسی) را ببینید). |
| `source` | string | فراخوانی یا مسیر دقیق پشت این خوانش. |
| `value` | string or null | آنچه آن متد برگرداند؛ اگر چیزی نخواند `null`. |
| `error` | string or null | هنگامی که متد شکست خورد حاضر است. |
| `elapsedMicros` | number | خوانش چقدر طول کشید، به میکروثانیه. |
| `compare` | boolean | `true` اگر این خوانش در حکم شرکت کند؛ `false` اگر تنها برای اطلاعات نشان داده شود. |
| `detail` | string or null | یادداشتی اختیاری دربارهٔ این خوانش. |

## شمارش‌ها

### حکم

| مقدار | معنا |
|---|---|
| `MATCH` | همهٔ عدسی‌های قابل‌مقایسه موافق بودند. |
| `MISMATCH` | عدسی‌های قابل‌مقایسه موافق نبودند. |
| `SINGLE` | تنها یک عدسی توانست آن را بخواند. |
| `INFO` | برای مرجع خوانده شد، نه برای رأی‌گیری. |
| `EMPTY` | هیچ عدسی‌ای مقداری نخواند. |
| `ERROR` | بدون مقدار، و دست‌کم یک عدسی خطا داد. |

### عدسی

| مقدار | برچسب |
|---|---|
| `JAVA` | Java / SDK |
| `NATIVE` | بومی / JNI |
| `SHELL` | شِل از طریق JVM |
| `SHELL_NATIVE` | شِل از طریق JNI |
| `ATTEST` | TEE / گواهی |

### دسته

`IDENTITY`، `FINGERPRINT`، `TELEPHONY`، `NETWORK`، `LOCALE`، `BOOT`، `BUILD`،
`HARDWARE`، `DISPLAY`، `SENSORS`، `MEDIA`، `STORAGE`، `PROCESS`، `PACKAGES`،
`ACCOUNTS`، `WEBVIEW`، `SECURITY`، `INTEGRITY`، `EMULATOR`، `SYSTEM`.

## پوشاندن

هنگامی که `spec.sensitive` برابر `true` است، خوانش‌های کاوشگر یک مقدار هویت‌دار حمل می‌کنند
(یک شماره سریال، IMEI، نشانی MAC، شناسهٔ تبلیغاتی و مانند آن). در گزارش **به‌اشتراک‌گذاشته یا خروجی‌گرفته‌شده**
آن مقادیر، و جزئیاتشان، با یک نشانگر حذف جایگزین می‌شوند تا یک
گزارش که در جایی عمومی چسبانده می‌شود شناسه‌های شخصی را لو ندهد؛ کاوشگر، حکم
آن و ساختار حفظ می‌شوند. فایل `last_snapshot.json` **روی‌دستگاه**،
که تنها به‌دست برنامه یا به‌دست روت خواندنی است، مقادیر واقعی را نگه می‌دارد.

## خواندن آن

یک تجزیهٔ کمینه: روی `results` تکرار کنید، هر `verdict` را بخوانید، و برای تصویری کامل
`values` را بر پایهٔ `lens` آن‌ها کنار هم بچینید. شکل بالا در طول انتشارها پایدار است؛ در برابر
این نام‌های فیلد تجزیه کنید نه موقعیت‌های روی صفحه.

برای اینکه چگونه فایل را روی دستگاه به‌دست آورید [عامل‌های هوش مصنوعی](AI-AGENTS.md) را ببینید.

<!-- doc-footer -->

---

> _این مستندات با Claude (Anthropic) تولید شده و توسط VD171 نگهداری می‌شود. اصلاحات و بهبودها خوش‌آمد است؛ به [CONTRIBUTING.md](CONTRIBUTING.md) مراجعه کنید._
