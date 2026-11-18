> **Languages:** [English](../AI-AGENTS.md) · [العربية](../ar/AI-AGENTS.md) · [Čeština](../cs/AI-AGENTS.md) · [Deutsch](../de/AI-AGENTS.md) · [Español](../es/AI-AGENTS.md) · [فارسی](../fa/AI-AGENTS.md) · [Français](../fr/AI-AGENTS.md) · [हिन्दी](../hi/AI-AGENTS.md) · Bahasa Indonesia · [Italiano](../it/AI-AGENTS.md) · [日本語](../ja/AI-AGENTS.md) · [한국어](../ko/AI-AGENTS.md) · [Nederlands](../nl/AI-AGENTS.md) · [Polski](../pl/AI-AGENTS.md) · [Português](../pt/AI-AGENTS.md) · [Русский](../ru/AI-AGENTS.md) · [Svenska](../sv/AI-AGENTS.md) · [ไทย](../th/AI-AGENTS.md) · [Türkçe](../tr/AI-AGENTS.md) · [Українська](../uk/AI-AGENTS.md) · [Tiếng Việt](../vi/AI-AGENTS.md) · [中文](../zh/AI-AGENTS.md)

---

# VD Infos untuk agen AI

VD Infos **dibangun untuk dibaca oleh mesin, bukan hanya oleh orang**. Sebuah debugger metode
perangkat menghasilkan banyak bukti terstruktur - setiap nilai, dibaca melalui setiap
metode, disejajarkan untuk perbandingan - dan bukti itu jauh lebih berguna bagi agen sebagai
data daripada sebagai tangkapan layar. Jadi seluruh hasil tersedia sebagai satu dokumen yang dapat dibaca
mesin, dan aplikasi dirancang di sekitar itu dari dasar.

Jika Anda adalah asisten pengkodean AI, alat LLM, skrip analisis, atau otomatisasi apa pun
yang menjangkau perangkat melalui shell, halaman ini untuk Anda.

## Mengapa ia ramah agen

- **Satu dokumen, bukan satu layar.** Seluruh pemindaian - setiap sonda, setiap lensa, setiap
  vonis - adalah satu objek JSON tunggal. Tanpa OCR, tanpa pengikisan layar, tanpa mengetuk melalui
  kartu yang diciutkan.
- **Data yang sama dengan yang dimiliki UI.** Berkas itu bukan ringkasan. Ia persis apa yang disimpan
  aplikasi secara internal: layar menampilkan pembacaan pertama dari item yang diciutkan, berkas
  memiliki semuanya.
- **Bentuk deterministik.** Strukturnya stabil dan terdokumentasi (lihat
  [EXPORT-FORMAT](EXPORT-FORMAT.md)), jadi pengurai yang ditulis sekali terus bekerja.
- **Tanpa jaringan, tanpa akun, tanpa status untuk dikelola.** Aplikasi membaca di perangkat dan menulis sebuah
  berkas. Agen Anda membaca berkas itu. Itulah seluruh kontraknya.
- **Luring berdasarkan rancangan.** Ia tidak membutuhkan internet, jadi ia berjalan sama di dalam lingkungan
  yang terkunci atau air-gapped tempat agen mungkin beroperasi.

## Pembacaan satu perintah

Setiap pemindaian menulis laporan lengkapnya ke penyimpanan privat aplikasi. Setelah sebuah pemindaian berjalan,
agen membaca hasil lengkap dengan satu perintah:

```sh
su -c cat /data/data/ru.vd171.vdinfos/files/last_snapshot.json
```

- **Apa yang dikandungnya:** setiap sonda dengan vonisnya dan nilai dari setiap lensa,
  dalam JSON yang sama dengan tombol ekspor.
- **Kapan ia diperbarui:** di akhir setiap pemindaian. Jalankan pemindaian dulu; berkas selalu
  menyimpan yang terbaru.
- **Persyaratan:** root, karena berkas berada di penyimpanan privat aplikasi. Tanpa
  root, gunakan aksi **bagikan / simpan sebagai JSON** di dalam aplikasi sebagai gantinya dan baca berkas yang
  diserahkan kepada agen.

## Alur agen yang khas

1. Pastikan aplikasi terpasang (APK **SDK_35** dari
   [Releases](https://github.com/VD171/VD-Infos/releases)).
2. Picu sebuah pemindaian - seseorang mengetuk pindai, atau otomatisasi meluncurkan layar pemindaian
   aplikasi - dan tunggu hingga selesai.
3. Baca `files/last_snapshot.json` dengan perintah di atas.
4. Urai dengan [format ekspor](EXPORT-FORMAT.md) sebagai skema Anda: iterasi
   sonda, baca `verdict` masing-masing, dan sejajarkan `values` per lensa.
5. Bertindaklah atas hasil terstruktur dalam alur kerja Anda sendiri.

Agen memutuskan apa arti pembacaan bagi tugas yang dihadapi; VD Infos menyediakan
bukti, secara bersih dan lengkap.

## Apa yang didapat agen, dan apa yang tidak

- Laporan membawa **setiap pembacaan dan setiap vonis**, jadi agen memiliki gambaran
  lengkap yang dibangun aplikasi.
- Nilai yang membawa identitas (nomor seri, IMEI, alamat MAC, ID iklan, dan sejenisnya)
  **disamarkan dalam laporan yang dibagikan/diekspor**, jadi laporan yang diedarkan tetap bebas dari
  pengidentifikasi pribadi sambil mempertahankan vonis dan strukturnya. `last_snapshot.json`
  **di perangkat**, yang hanya dapat dibaca oleh aplikasi atau root, menyimpan nilai asli untuk agen
  lokal yang membutuhkannya.

## Dokumentasi, ditulis untuk diurai

Wiki ini terstruktur untuk pembacaan mesin sama banyaknya dengan untuk orang: bagian pendek,
judul stabil, nama field eksplisit, dan halaman khusus [format ekspor](EXPORT-FORMAT.md)
yang dapat diperlakukan agen sebagai skema. Jika Anda membangun alat di atas VD Infos,
mulai dari sana.

## Halaman terkait

- [Format ekspor](EXPORT-FORMAT.md) - skema JSON, field demi field.
- [Panduan pengguna](USER-GUIDE.md) - memasang, memindai, dan berbagi.
- [Arsitektur](ARCHITECTURE.md) - bagaimana aplikasi disusun.

<!-- doc-footer -->

---

> _Dokumentasi ini dibuat dengan Claude (Anthropic) dan dikelola oleh VD171. Koreksi dan perbaikan dipersilakan; lihat [CONTRIBUTING.md](CONTRIBUTING.md)._
