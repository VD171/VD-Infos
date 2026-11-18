> **Languages:** [English](../EXPORT-FORMAT.md) · [العربية](../ar/EXPORT-FORMAT.md) · [Čeština](../cs/EXPORT-FORMAT.md) · [Deutsch](../de/EXPORT-FORMAT.md) · [Español](../es/EXPORT-FORMAT.md) · [فارسی](../fa/EXPORT-FORMAT.md) · [Français](../fr/EXPORT-FORMAT.md) · [हिन्दी](../hi/EXPORT-FORMAT.md) · Bahasa Indonesia · [Italiano](../it/EXPORT-FORMAT.md) · [日本語](../ja/EXPORT-FORMAT.md) · [한국어](../ko/EXPORT-FORMAT.md) · [Nederlands](../nl/EXPORT-FORMAT.md) · [Polski](../pl/EXPORT-FORMAT.md) · [Português](../pt/EXPORT-FORMAT.md) · [Русский](../ru/EXPORT-FORMAT.md) · [Svenska](../sv/EXPORT-FORMAT.md) · [ไทย](../th/EXPORT-FORMAT.md) · [Türkçe](../tr/EXPORT-FORMAT.md) · [Українська](../uk/EXPORT-FORMAT.md) · [Tiếng Việt](../vi/EXPORT-FORMAT.md) · [中文](../zh/EXPORT-FORMAT.md)

---

# Format ekspor

Laporan yang diekspor VD Infos - dan `last_snapshot.json` yang ia tulis setelah setiap pemindaian -
adalah satu objek JSON dengan bentuk yang stabil dan terdokumentasi. Halaman ini adalah skemanya. Ia
mendeskripsikan struktur sehingga sebuah alat dapat menguraikannya; ia tidak mendaftar masing-masing sonda
maupun menafsirkan pembacaan apa pun untuk Anda.

## Level teratas: snapshot

```json
{
  "takenAt": 1730000000000,
  "appVersion": "2.23",
  "results": [ /* ProbeResult objects */ ]
}
```

| Field | Tipe | Arti |
|---|---|---|
| `takenAt` | number | Kapan pemindaian diambil, Unix epoch dalam milidetik. |
| `appVersion` | string | Versi aplikasi yang menghasilkan laporan. |
| `results` | array | Satu `ProbeResult` per sonda, dalam urutan yang disimpan aplikasi. |

## Sebuah hasil

Setiap entri dalam `results` adalah sonda dengan vonisnya dan setiap pembacaan lensa.

```json
{
  "spec": { /* ProbeSpec */ },
  "values": [ /* LensValue objects */ ],
  "verdict": "MATCH",
  "detail": null
}
```

| Field | Tipe | Arti |
|---|---|---|
| `spec` | object | Apa yang disonda (lihat di bawah). |
| `values` | array | Satu `LensValue` per metode yang membacanya. |
| `verdict` | string | Bagaimana pembacaan berhubungan (lihat [Vonis](#vonis)). |
| `detail` | string or null | Catatan opsional tentang hasil. |

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

| Field | Tipe | Arti |
|---|---|---|
| `id` | string | Pengidentifikasi stabil dari sonda. |
| `title` | string | Nama yang dapat dibaca manusia. |
| `category` | string | Salah satu dari [kategori](#kategori). |
| `lenses` | array | Lensa yang digunakan sonda ini. |
| `note` | string or null | Konteks opsional. |
| `sensitive` | boolean | `true` ketika nilai membawa identitas; nilai seperti itu **disamarkan dalam laporan yang diekspor** (lihat [Penyamaran](#penyamaran)). |
| `solution` | string or null | Sebuah field petunjuk opsional; bisa jadi tidak ada atau null. |

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

| Field | Tipe | Arti |
|---|---|---|
| `lens` | string | Metode mana yang membacanya (lihat [Lensa](#lensa)). |
| `source` | string | Panggilan atau jalur persis di balik pembacaan ini. |
| `value` | string or null | Apa yang dikembalikan metode itu; `null` jika ia tidak membaca apa pun. |
| `error` | string or null | Hadir ketika metode gagal. |
| `elapsedMicros` | number | Berapa lama pembacaan berlangsung, dalam mikrodetik. |
| `compare` | boolean | `true` jika pembacaan ini ikut dalam vonis; `false` jika ia ditampilkan hanya untuk informasi. |
| `detail` | string or null | Catatan opsional tentang pembacaan ini. |

## Enumerasi

### Vonis

| Nilai | Arti |
|---|---|
| `MATCH` | Setiap lensa yang dapat dibandingkan setuju. |
| `MISMATCH` | Lensa yang dapat dibandingkan tidak setuju. |
| `SINGLE` | Hanya satu lensa yang dapat membacanya. |
| `INFO` | Dibaca untuk referensi, tidak divoting. |
| `EMPTY` | Tidak ada lensa yang membaca sebuah nilai. |
| `ERROR` | Tidak ada nilai, dan setidaknya satu lensa mengalami error. |

### Lensa

| Nilai | Label |
|---|---|
| `JAVA` | Java / SDK |
| `NATIVE` | Native / JNI |
| `SHELL` | Shell via JVM |
| `SHELL_NATIVE` | Shell via JNI |
| `ATTEST` | TEE / Attestation |

### Kategori

`IDENTITY`, `FINGERPRINT`, `TELEPHONY`, `NETWORK`, `LOCALE`, `BOOT`, `BUILD`,
`HARDWARE`, `DISPLAY`, `SENSORS`, `MEDIA`, `STORAGE`, `PROCESS`, `PACKAGES`,
`ACCOUNTS`, `WEBVIEW`, `SECURITY`, `INTEGRITY`, `EMULATOR`, `SYSTEM`.

## Penyamaran

Ketika `spec.sensitive` bernilai `true`, pembacaan sonda membawa sebuah nilai yang membawa identitas
(nomor seri, IMEI, alamat MAC, ID iklan, dan sejenisnya). Dalam laporan yang **dibagikan atau diekspor**
nilai-nilai itu, dan detailnya, diganti dengan penanda redaksi sehingga sebuah
laporan yang ditempel di tempat publik tidak membocorkan pengidentifikasi pribadi; sonda, vonisnya,
dan strukturnya dipertahankan. `last_snapshot.json` **di perangkat**,
yang hanya dapat dibaca oleh aplikasi atau oleh root, menyimpan nilai asli.

## Membacanya

Penguraian minimal: iterasi `results`, baca setiap `verdict`, dan untuk gambaran lengkap sejajarkan
`values` berdasarkan `lens` mereka. Bentuk di atas stabil lintas rilis; urai berdasarkan
nama field ini alih-alih posisi layar.

Lihat [Agen AI](AI-AGENTS.md) untuk cara memperoleh berkas di perangkat.

<!-- doc-footer -->

---

> _Dokumentasi ini dibuat dengan Claude (Anthropic) dan dikelola oleh VD171. Koreksi dan perbaikan dipersilakan; lihat [CONTRIBUTING.md](CONTRIBUTING.md)._
