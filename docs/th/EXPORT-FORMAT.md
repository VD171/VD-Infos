> **Languages:** [English](../EXPORT-FORMAT.md) · [العربية](../ar/EXPORT-FORMAT.md) · [Čeština](../cs/EXPORT-FORMAT.md) · [Deutsch](../de/EXPORT-FORMAT.md) · [Español](../es/EXPORT-FORMAT.md) · [فارسی](../fa/EXPORT-FORMAT.md) · [Français](../fr/EXPORT-FORMAT.md) · [हिन्दी](../hi/EXPORT-FORMAT.md) · [Bahasa Indonesia](../id/EXPORT-FORMAT.md) · [Italiano](../it/EXPORT-FORMAT.md) · [日本語](../ja/EXPORT-FORMAT.md) · [한국어](../ko/EXPORT-FORMAT.md) · [Nederlands](../nl/EXPORT-FORMAT.md) · [Polski](../pl/EXPORT-FORMAT.md) · [Português](../pt/EXPORT-FORMAT.md) · [Русский](../ru/EXPORT-FORMAT.md) · [Svenska](../sv/EXPORT-FORMAT.md) · ไทย · [Türkçe](../tr/EXPORT-FORMAT.md) · [Українська](../uk/EXPORT-FORMAT.md) · [Tiếng Việt](../vi/EXPORT-FORMAT.md) · [中文](../zh/EXPORT-FORMAT.md)

---

# รูปแบบการส่งออก

รายงานที่ VD Infos ส่งออก - และ `last_snapshot.json` ที่มันเขียนหลังการสแกนทุกครั้ง - เป็นออบเจกต์ JSON เดียวที่มีรูปทรงเสถียร
และมีเอกสารกำกับ หน้านี้คือสคีมา มันอธิบายโครงสร้างเพื่อให้เครื่องมือแยกวิเคราะห์ได้ มันไม่ได้ระบุรายการโพรบแต่ละตัว
หรือตีความการอ่านค่าใด ๆ ให้คุณ

## ระดับบนสุด: สแนปช็อต

```json
{
  "takenAt": 1730000000000,
  "appVersion": "2.23",
  "results": [ /* ProbeResult objects */ ]
}
```

| ฟิลด์ | ชนิด | ความหมาย |
|---|---|---|
| `takenAt` | number | เวลาที่ทำการสแกน Unix epoch เป็นมิลลิวินาที |
| `appVersion` | string | เวอร์ชันแอปที่ผลิตรายงาน |
| `results` | array | หนึ่ง `ProbeResult` ต่อโพรบ ตามลำดับที่แอปเก็บไว้ |

## ผลลัพธ์หนึ่งรายการ

แต่ละรายการใน `results` คือโพรบพร้อมคำตัดสินของมันและการอ่านค่าของทุกเลนส์

```json
{
  "spec": { /* ProbeSpec */ },
  "values": [ /* LensValue objects */ ],
  "verdict": "MATCH",
  "detail": null
}
```

| ฟิลด์ | ชนิด | ความหมาย |
|---|---|---|
| `spec` | object | สิ่งที่ถูกโพรบ (ดูด้านล่าง) |
| `values` | array | หนึ่ง `LensValue` ต่อเมธอดที่อ่านมัน |
| `verdict` | string | การอ่านค่าสัมพันธ์กันอย่างไร (ดู [คำตัดสิน](#คำตัดสิน)) |
| `detail` | string หรือ null | หมายเหตุเสริมเกี่ยวกับผลลัพธ์ |

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

| ฟิลด์ | ชนิด | ความหมาย |
|---|---|---|
| `id` | string | ตัวระบุที่เสถียรของโพรบ |
| `title` | string | ชื่อที่มนุษย์อ่านได้ |
| `category` | string | หนึ่งใน[หมวดหมู่](#หมวดหมู่) |
| `lenses` | array | เลนส์ที่โพรบนี้ใช้ |
| `note` | string หรือ null | บริบทเสริม |
| `sensitive` | boolean | `true` เมื่อค่านั้นบ่งบอกตัวตน ค่าเช่นนั้นจะถูก **ปิดบังในรายงานที่ส่งออก** (ดู [การปิดบัง](#การปิดบัง)) |
| `solution` | string หรือ null | ฟิลด์คำใบ้เสริม อาจไม่มีหรือเป็น null |

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

| ฟิลด์ | ชนิด | ความหมาย |
|---|---|---|
| `lens` | string | เมธอดใดที่อ่านมัน (ดู [เลนส์](#เลนส์)) |
| `source` | string | การเรียกหรือพาธที่แน่นอนเบื้องหลังการอ่านค่านี้ |
| `value` | string หรือ null | สิ่งที่เมธอดนั้นส่งคืน `null` หากไม่ได้อ่านอะไรเลย |
| `error` | string หรือ null | มีอยู่เมื่อเมธอดล้มเหลว |
| `elapsedMicros` | number | การอ่านค่าใช้เวลานานเท่าใด เป็นไมโครวินาที |
| `compare` | boolean | `true` หากการอ่านค่านี้มีส่วนในคำตัดสิน `false` หากแสดงไว้เพื่อข้อมูลเท่านั้น |
| `detail` | string หรือ null | หมายเหตุเสริมเกี่ยวกับการอ่านค่านี้ |

## การแจงนับ

### คำตัดสิน

| ค่า | ความหมาย |
|---|---|
| `MATCH` | ทุกเลนส์ที่เปรียบเทียบได้เห็นตรงกัน |
| `MISMATCH` | เลนส์ที่เปรียบเทียบได้ไม่เห็นตรงกัน |
| `SINGLE` | มีเพียงเลนส์เดียวที่อ่านได้ |
| `INFO` | อ่านเพื่ออ้างอิง ไม่ได้นำมาลงคะแนน |
| `EMPTY` | ไม่มีเลนส์ใดอ่านค่าได้ |
| `ERROR` | ไม่มีค่า และอย่างน้อยหนึ่งเลนส์เกิดข้อผิดพลาด |

### เลนส์

| ค่า | ป้ายกำกับ |
|---|---|
| `JAVA` | Java / SDK |
| `NATIVE` | Native / JNI |
| `SHELL` | Shell via JVM |
| `SHELL_NATIVE` | Shell via JNI |
| `ATTEST` | TEE / Attestation |

### หมวดหมู่

`IDENTITY`, `FINGERPRINT`, `TELEPHONY`, `NETWORK`, `LOCALE`, `BOOT`, `BUILD`,
`HARDWARE`, `DISPLAY`, `SENSORS`, `MEDIA`, `STORAGE`, `PROCESS`, `PACKAGES`,
`ACCOUNTS`, `WEBVIEW`, `SECURITY`, `INTEGRITY`, `EMULATOR`, `SYSTEM`.

## การปิดบัง

เมื่อ `spec.sensitive` เป็น `true` การอ่านค่าของโพรบจะพกค่าที่บ่งบอกตัวตน (หมายเลขซีเรียล, IMEI, ที่อยู่ MAC, รหัสโฆษณา และอื่น ๆ ทำนองนั้น)
ในรายงานที่ **แชร์หรือส่งออก** ค่าเหล่านั้นและรายละเอียดของมันจะถูกแทนที่ด้วยเครื่องหมายปิดบัง เพื่อให้รายงานที่วางลงในที่สาธารณะ
ไม่รั่วไหลตัวระบุส่วนบุคคล โพรบ คำตัดสินของมัน และโครงสร้างจะถูกเก็บรักษาไว้ สำเนาบนอุปกรณ์ `last_snapshot.json`
ที่อ่านได้โดยแอปหรือ root เท่านั้น จะเก็บค่าจริงไว้

## การอ่านมัน

การแยกวิเคราะห์ขั้นต่ำ: วนซ้ำ `results` อ่าน `verdict` ของแต่ละตัว และสำหรับภาพรวมที่สมบูรณ์ ให้จัดเรียง `values`
ตาม `lens` ของมัน รูปทรงด้านบนมีความเสถียรข้ามรีลีส แยกวิเคราะห์โดยใช้ชื่อฟิลด์เหล่านี้แทนตำแหน่งบนหน้าจอ

ดู [เอเจนต์ AI](AI-AGENTS.md) สำหรับวิธีการรับไฟล์บนอุปกรณ์

<!-- doc-footer -->

---

> _เอกสารนี้สร้างขึ้นด้วย Claude (Anthropic) และดูแลโดย VD171 ยินดีรับการแก้ไขและการปรับปรุง ดู [CONTRIBUTING.md](CONTRIBUTING.md)_
