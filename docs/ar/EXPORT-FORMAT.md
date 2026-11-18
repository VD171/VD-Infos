> **Languages:** [English](../EXPORT-FORMAT.md) · العربية · [Čeština](../cs/EXPORT-FORMAT.md) · [Deutsch](../de/EXPORT-FORMAT.md) · [Español](../es/EXPORT-FORMAT.md) · [فارسی](../fa/EXPORT-FORMAT.md) · [Français](../fr/EXPORT-FORMAT.md) · [हिन्दी](../hi/EXPORT-FORMAT.md) · [Bahasa Indonesia](../id/EXPORT-FORMAT.md) · [Italiano](../it/EXPORT-FORMAT.md) · [日本語](../ja/EXPORT-FORMAT.md) · [한국어](../ko/EXPORT-FORMAT.md) · [Nederlands](../nl/EXPORT-FORMAT.md) · [Polski](../pl/EXPORT-FORMAT.md) · [Português](../pt/EXPORT-FORMAT.md) · [Русский](../ru/EXPORT-FORMAT.md) · [Svenska](../sv/EXPORT-FORMAT.md) · [ไทย](../th/EXPORT-FORMAT.md) · [Türkçe](../tr/EXPORT-FORMAT.md) · [Українська](../uk/EXPORT-FORMAT.md) · [Tiếng Việt](../vi/EXPORT-FORMAT.md) · [中文](../zh/EXPORT-FORMAT.md)

---

# صيغة التصدير

التقرير الذي يصدّره VD Infos - وملف `last_snapshot.json` الذي يكتبه بعد كل فحص - هو كائن
JSON واحد بشكل مستقر وموثّق. هذه الصفحة هي المخطَّط. تصف البنية حتى تتمكن أداة من تحليلها؛
لا تسرد المسابير المفردة ولا تفسّر لك أي قراءة.

## المستوى الأعلى: اللقطة

```json
{
  "takenAt": 1730000000000,
  "appVersion": "2.23",
  "results": [ /* ProbeResult objects */ ]
}
```

| الحقل | النوع | المعنى |
|---|---|---|
| `takenAt` | number | متى أُخذ الفحص، حقبة Unix بالميلي ثانية. |
| `appVersion` | string | إصدار التطبيق الذي أنتج التقرير. |
| `results` | array | `ProbeResult` واحد لكل مسبار، بالترتيب الذي يحتفظ به التطبيق. |

## نتيجة

كل إدخال في `results` هو مسبار بحكمه وكل قراءة عدسة.

```json
{
  "spec": { /* ProbeSpec */ },
  "values": [ /* LensValue objects */ ],
  "verdict": "MATCH",
  "detail": null
}
```

| الحقل | النوع | المعنى |
|---|---|---|
| `spec` | object | ما جرى سبره (انظر أدناه). |
| `values` | array | `LensValue` واحد لكل طريقة قرأته. |
| `verdict` | string | كيف ترتبط القراءات ببعضها (انظر [الحكم](#الحكم)). |
| `detail` | string or null | ملاحظة اختيارية عن النتيجة. |

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

| الحقل | النوع | المعنى |
|---|---|---|
| `id` | string | المعرّف المستقر للمسبار. |
| `title` | string | اسم قابل للقراءة البشرية. |
| `category` | string | واحدة من [الفئات](#الفئة). |
| `lenses` | array | العدسات التي يستخدمها هذا المسبار. |
| `note` | string or null | سياق اختياري. |
| `sensitive` | boolean | `true` عندما تكون القيمة حاملة للهوية؛ وتكون هذه القيم **مُقنَّعة في التقرير المُصدَّر** (انظر [التقنيع](#التقنيع)). |
| `solution` | string or null | حقل تلميح اختياري؛ قد يكون غائبًا أو null. |

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

| الحقل | النوع | المعنى |
|---|---|---|
| `lens` | string | أي طريقة قرأته (انظر [العدسة](#العدسة)). |
| `source` | string | الاستدعاء أو المسار الدقيق وراء هذه القراءة. |
| `value` | string or null | ما أعادته تلك الطريقة؛ `null` إذا لم تقرأ شيئًا. |
| `error` | string or null | موجود عندما تفشل الطريقة. |
| `elapsedMicros` | number | كم استغرقت القراءة، بالميكرو ثانية. |
| `compare` | boolean | `true` إذا كانت هذه القراءة تشارك في الحكم؛ `false` إذا عُرضت للمعلومة فقط. |
| `detail` | string or null | ملاحظة اختيارية عن هذه القراءة. |

## التعدادات

### الحكم

| القيمة | المعنى |
|---|---|
| `MATCH` | اتفقت كل عدسة قابلة للمقارنة. |
| `MISMATCH` | لم تتفق العدسات القابلة للمقارنة. |
| `SINGLE` | عدسة واحدة فقط استطاعت قراءته. |
| `INFO` | مقروء للمرجع، لا يُصوَّت عليه. |
| `EMPTY` | لم تقرأ أي عدسة قيمة. |
| `ERROR` | لا قيمة، وأخطأت عدسة واحدة على الأقل. |

### العدسة

| القيمة | التسمية |
|---|---|
| `JAVA` | Java / SDK |
| `NATIVE` | أصلي / JNI |
| `SHELL` | Shell عبر JVM |
| `SHELL_NATIVE` | Shell عبر JNI |
| `ATTEST` | TEE / إشهاد |

### الفئة

`IDENTITY`, `FINGERPRINT`, `TELEPHONY`, `NETWORK`, `LOCALE`, `BOOT`, `BUILD`,
`HARDWARE`, `DISPLAY`, `SENSORS`, `MEDIA`, `STORAGE`, `PROCESS`, `PACKAGES`,
`ACCOUNTS`, `WEBVIEW`, `SECURITY`, `INTEGRITY`, `EMULATOR`, `SYSTEM`.

## التقنيع

عندما يكون `spec.sensitive` بقيمة `true`، تحمل قراءات المسبار قيمة حاملة للهوية (رقم
تسلسلي أو IMEI أو عنوان MAC أو معرّف إعلان وما شابه). في التقرير **المشارك أو المُصدَّر**
تُستبدَل تلك القيم وتفاصيلها بعلامة حجب حتى لا يُسرِّب تقرير مُلصَق في مكان عام معرّفات
شخصية؛ ويُحفظ المسبار وحكمه والبنية. ملف `last_snapshot.json` **الموجود على الجهاز**،
القابل للقراءة فقط بواسطة التطبيق أو الـ root، يحتفظ بالقيم الحقيقية.

## قراءته

تحليل بالحد الأدنى: كرّر على `results`، واقرأ كل `verdict`، وللحصول على صورة كاملة صفّ
`values` حسب `lens` الخاص بها. الشكل أعلاه مستقر عبر الإصدارات؛ حلّل مقابل أسماء الحقول هذه
بدلًا من مواضع الشاشة.

انظر [وكلاء الذكاء الاصطناعي](AI-AGENTS.md) لمعرفة كيفية الحصول على الملف على الجهاز.

<!-- doc-footer -->

---

> _تم إنشاء هذا التوثيق باستخدام Claude (Anthropic) ويتولى صيانته VD171. التصحيحات والتحسينات مرحّب بها؛ راجع [CONTRIBUTING.md](CONTRIBUTING.md)._
