> **Languages:** [English](../EXPORT-FORMAT.md) · [العربية](../ar/EXPORT-FORMAT.md) · [Čeština](../cs/EXPORT-FORMAT.md) · [Deutsch](../de/EXPORT-FORMAT.md) · [Español](../es/EXPORT-FORMAT.md) · [فارسی](../fa/EXPORT-FORMAT.md) · [Français](../fr/EXPORT-FORMAT.md) · हिन्दी · [Bahasa Indonesia](../id/EXPORT-FORMAT.md) · [Italiano](../it/EXPORT-FORMAT.md) · [日本語](../ja/EXPORT-FORMAT.md) · [한국어](../ko/EXPORT-FORMAT.md) · [Nederlands](../nl/EXPORT-FORMAT.md) · [Polski](../pl/EXPORT-FORMAT.md) · [Português](../pt/EXPORT-FORMAT.md) · [Русский](../ru/EXPORT-FORMAT.md) · [Svenska](../sv/EXPORT-FORMAT.md) · [ไทย](../th/EXPORT-FORMAT.md) · [Türkçe](../tr/EXPORT-FORMAT.md) · [Українська](../uk/EXPORT-FORMAT.md) · [Tiếng Việt](../vi/EXPORT-FORMAT.md) · [中文](../zh/EXPORT-FORMAT.md)

---

# एक्सपोर्ट प्रारूप

VD Infos जो रिपोर्ट एक्सपोर्ट करता है - और `last_snapshot.json` जो वह हर स्कैन के बाद लिखता है -
वह एक स्थिर, प्रलेखित आकार वाला एक JSON ऑब्जेक्ट है। यह पृष्ठ स्कीमा है। यह संरचना का
वर्णन करता है ताकि कोई टूल इसे पार्स कर सके; यह न तो अलग-अलग जाँचों को सूचीबद्ध करता है और न
आपके लिए किसी पठन की व्याख्या करता है।

## शीर्ष स्तर: स्नैपशॉट

```json
{
  "takenAt": 1730000000000,
  "appVersion": "2.23",
  "results": [ /* ProbeResult objects */ ]
}
```

| फ़ील्ड | प्रकार | अर्थ |
|---|---|---|
| `takenAt` | number | स्कैन कब लिया गया, मिलीसेकंड में Unix epoch। |
| `appVersion` | string | वह ऐप संस्करण जिसने रिपोर्ट उत्पन्न की। |
| `results` | array | प्रति जाँच एक `ProbeResult`, उसी क्रम में जिसमें ऐप उन्हें रखता है। |

## एक परिणाम

`results` में हर प्रविष्टि अपने निर्णय और हर लेंस पठन के साथ एक जाँच है।

```json
{
  "spec": { /* ProbeSpec */ },
  "values": [ /* LensValue objects */ ],
  "verdict": "MATCH",
  "detail": null
}
```

| फ़ील्ड | प्रकार | अर्थ |
|---|---|---|
| `spec` | object | क्या जाँचा गया (नीचे देखें)। |
| `values` | array | इसे पढ़ने वाले प्रति मेथड एक `LensValue`। |
| `verdict` | string | पठन कैसे संबंधित हैं (देखें [निर्णय](#निर्णय))। |
| `detail` | string or null | परिणाम के बारे में एक वैकल्पिक टिप्पणी। |

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

| फ़ील्ड | प्रकार | अर्थ |
|---|---|---|
| `id` | string | जाँच का स्थिर पहचानकर्ता। |
| `title` | string | मानव-पठनीय नाम। |
| `category` | string | [श्रेणियों](#श्रेणी) में से एक। |
| `lenses` | array | वे लेंस जो यह जाँच उपयोग करती है। |
| `note` | string or null | वैकल्पिक संदर्भ। |
| `sensitive` | boolean | `true` जब मान पहचान-वाहक हो; ऐसे मान **एक्सपोर्ट की गई रिपोर्ट में मास्क किए जाते हैं** (देखें [मास्किंग](#मास्किंग))। |
| `solution` | string or null | एक वैकल्पिक संकेत फ़ील्ड; अनुपस्थित या null हो सकता है। |

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

| फ़ील्ड | प्रकार | अर्थ |
|---|---|---|
| `lens` | string | किस मेथड ने इसे पढ़ा (देखें [लेंस](#लेंस))। |
| `source` | string | इस पठन के पीछे की सटीक कॉल या पथ। |
| `value` | string or null | उस मेथड ने क्या लौटाया; `null` यदि उसने कुछ नहीं पढ़ा। |
| `error` | string or null | मौजूद जब मेथड विफल हुआ। |
| `elapsedMicros` | number | पठन में कितना समय लगा, माइक्रोसेकंड में। |
| `compare` | boolean | `true` यदि यह पठन निर्णय में भाग लेता है; `false` यदि यह केवल सूचना के लिए दिखाया गया है। |
| `detail` | string or null | इस पठन के बारे में एक वैकल्पिक टिप्पणी। |

## एन्यूमरेशन

### निर्णय

| मान | अर्थ |
|---|---|
| `MATCH` | हर तुलनीय लेंस सहमत हुआ। |
| `MISMATCH` | तुलनीय लेंस सहमत नहीं हुए। |
| `SINGLE` | केवल एक लेंस इसे पढ़ सका। |
| `INFO` | संदर्भ के लिए पढ़ा गया, मतदान नहीं किया गया। |
| `EMPTY` | किसी लेंस ने कोई मान नहीं पढ़ा। |
| `ERROR` | कोई मान नहीं, और कम से कम एक लेंस में त्रुटि हुई। |

### लेंस

| मान | लेबल |
|---|---|
| `JAVA` | Java / SDK |
| `NATIVE` | Native / JNI |
| `SHELL` | Shell via JVM |
| `SHELL_NATIVE` | Shell via JNI |
| `ATTEST` | TEE / Attestation |

### श्रेणी

`IDENTITY`, `FINGERPRINT`, `TELEPHONY`, `NETWORK`, `LOCALE`, `BOOT`, `BUILD`,
`HARDWARE`, `DISPLAY`, `SENSORS`, `MEDIA`, `STORAGE`, `PROCESS`, `PACKAGES`,
`ACCOUNTS`, `WEBVIEW`, `SECURITY`, `INTEGRITY`, `EMULATOR`, `SYSTEM`।

## मास्किंग

जब `spec.sensitive` `true` होता है, तो जाँच के पठन एक पहचान-वाहक मान ले जाते हैं
(एक सीरियल, IMEI, MAC पता, विज्ञापन आईडी और इसी तरह)। **साझा या एक्सपोर्ट की गई** रिपोर्ट में
वे मान, और उनके विवरण, एक रिडैक्शन मार्कर से बदल दिए जाते हैं ताकि किसी सार्वजनिक स्थान पर पेस्ट
की गई रिपोर्ट व्यक्तिगत पहचानकर्ताओं को लीक न करे; जाँच, उसका निर्णय और संरचना संरक्षित रहते हैं।
**डिवाइस पर** `last_snapshot.json`, जिसे केवल ऐप या रूट पढ़ सकता है, असली मान रखता है।

## इसे पढ़ना

एक न्यूनतम पार्स: `results` को इटरेट करें, हर `verdict` पढ़ें, और पूरे चित्र के लिए `values` को
उनके `lens` के अनुसार पंक्तिबद्ध करें। ऊपर दिया गया आकार रिलीज़ों में स्थिर है; स्क्रीन की स्थितियों
के बजाय इन फ़ील्ड नामों के विरुद्ध पार्स करें।

डिवाइस पर फ़ाइल कैसे प्राप्त करें, इसके लिए [AI एजेंट्स](AI-AGENTS.md) देखें।

<!-- doc-footer -->

---

> _यह दस्तावेज़ Claude (Anthropic) से तैयार किया गया है और इसे VD171 द्वारा अनुरक्षित किया जाता है। सुधार और संशोधन का स्वागत है; देखें [CONTRIBUTING.md](CONTRIBUTING.md)।_
