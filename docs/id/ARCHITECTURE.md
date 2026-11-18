> **Languages:** [English](../ARCHITECTURE.md) · [العربية](../ar/ARCHITECTURE.md) · [Čeština](../cs/ARCHITECTURE.md) · [Deutsch](../de/ARCHITECTURE.md) · [Español](../es/ARCHITECTURE.md) · [فارسی](../fa/ARCHITECTURE.md) · [Français](../fr/ARCHITECTURE.md) · [हिन्दी](../hi/ARCHITECTURE.md) · Bahasa Indonesia · [Italiano](../it/ARCHITECTURE.md) · [日本語](../ja/ARCHITECTURE.md) · [한국어](../ko/ARCHITECTURE.md) · [Nederlands](../nl/ARCHITECTURE.md) · [Polski](../pl/ARCHITECTURE.md) · [Português](../pt/ARCHITECTURE.md) · [Русский](../ru/ARCHITECTURE.md) · [Svenska](../sv/ARCHITECTURE.md) · [ไทย](../th/ARCHITECTURE.md) · [Türkçe](../tr/ARCHITECTURE.md) · [Українська](../uk/ARCHITECTURE.md) · [Tiếng Việt](../vi/ARCHITECTURE.md) · [中文](../zh/ARCHITECTURE.md)

---

# Arsitektur

Ini adalah peta tingkat tinggi tentang bagaimana VD Infos disusun. Ia mendeskripsikan bentuk
aplikasi, bukan katalog apa yang ia baca: inti dari debugger metode adalah menaruh setiap
pembacaan di depan Anda, dan membacanya adalah milik Anda untuk dilakukan di perangkat Anda sendiri.

## Tujuan rancangan

- **Satu nilai, setiap metode.** Untuk setiap potong informasi, baca ia melalui setiap
  metode yang dapat membacanya dan sejajarkan pembacaannya. Sebuah debugger memunculkan bukti; ia
  tidak menjatuhkan penilaian.
- **Di perangkat dan senyap.** Tanpa jaringan, tanpa akun, tanpa pekerjaan latar belakang. Aplikasi berjalan
  hanya selagi ia terbuka.
- **Dapat dibaca oleh mesin.** Seluruh hasil adalah satu dokumen yang dapat dibaca mesin, jadi sebuah
  alat mendapat gambaran yang sama dengan yang didapat seseorang. Lihat [Agen AI](AI-AGENTS.md).
- **Kecil di tempat yang penting.** Bagian yang harus sulit dikelabui dijaga dengan sengaja
  mungil.

## Lensa-lensa

Sebuah *lensa* adalah satu cara membaca sebuah nilai. Nilai yang sama dibaca melalui beberapa cara sehingga
mereka dapat dibandingkan:

| Lensa | Apa itu |
|---|---|
| **Java / SDK** | API platform yang dipanggil oleh aplikasi biasa. |
| **Native / JNI** | Nilai yang sama dibaca dari kode nativa. |
| **Shell via JVM** | Perintah shell yang dilahirkan oleh JVM. |
| **Shell via JNI** | Perintah yang sama dilahirkan dari kode nativa, sebuah sudut pandang yang berbeda. |
| **TEE / Attestation** | Atestasi kunci yang didukung perangkat keras, di mana ia berlaku. |

Menyejajarkan lensa-lensa adalah seluruh idenya: ketika mereka berbeda pendapat, sesuatu di antara aplikasi
dan nilai menjawab secara berbeda kepada pemanggil yang berbeda. Apa artinya itu bagi suatu nilai,
pada suatu perangkat, adalah milik Anda untuk dibaca.

## Bagaimana pemindaian berjalan

```
core/model      immutable domain: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          the probes and the bridges they use (JNI, reflection, data lists)
engine/         ProbeEngine: parallel fan-out, streams results as they land
data/           SnapshotStore (persist the report), Exporter (share as JSON / text)
ui/             Jetpack Compose, Material 3, dynamic colour, live progress
cpp/            native_probes.cpp: the native lens, dependency-free
```

- **Paralelisme.** Sonda menyebar di sebuah dispatcher dengan jumlah izin terbatas;
  hasil mengalir ke dalam UI saat mereka selesai, dengan indikator kemajuan langsung.
- **Tidak ada apa pun di latar belakang.** Tanpa layanan dan tanpa pemindaian terjadwal. Aplikasi membaca hanya
  selagi terbuka dan menutup dirinya sendiri ketika dibiarkan menganggur.
- **Lapisan nativa.** Satu `.so` kecil, diikat berdasarkan nama melalui `RegisterNatives`, dijaga
  dengan sengaja mungil karena ia adalah bagian yang harus sulit dikelabui.

## Laporan

Setiap pemindaian menghasilkan sebuah `Snapshot`: waktu ia diambil, versi aplikasi, dan daftar
hasil, satu per sonda, masing-masing dengan pembacaan dari setiap lensa dan vonis tentang bagaimana
mereka berhubungan. Aplikasi mempersistenkan ini ke penyimpanan privat setelah setiap pemindaian dan dapat mengekspor
data yang sama. Strukturnya terdokumentasi sebagai [format ekspor](EXPORT-FORMAT.md).

## Memperluasnya

Sebagian besar dari apa yang dicari aplikasi berada sebagai **daftar teks biasa** di bawah
`assets/data/`, bukan sebagai kode. Menambah atau menghapus target adalah mengubah satu baris. Lihat
[Berkontribusi](CONTRIBUTING.md).

## Apa yang tidak pernah dilakukan VD Infos

- Ia tidak pernah mengubah apa pun pada sistem Anda. Ia hanya membaca dan membandingkan.
- Ia tidak pernah berbicara ke jaringan. Ia bekerja sepenuhnya luring.
- Ia tidak pernah berjalan di latar belakang, menjadwalkan pemindaian, atau menjaga sebuah layanan tetap hidup.
- Ia tidak pernah mengirim sebuah pembacaan ke mana pun. Sebuah laporan meninggalkan perangkat hanya ketika Anda membagikan atau
  menyimpannya, dengan nilai yang membawa identitas disamarkan.

<!-- doc-footer -->

---

> _Dokumentasi ini dibuat dengan Claude (Anthropic) dan dikelola oleh VD171. Koreksi dan perbaikan dipersilakan; lihat [CONTRIBUTING.md](CONTRIBUTING.md)._
