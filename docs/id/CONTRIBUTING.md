> **Languages:** [English](../CONTRIBUTING.md) · [العربية](../ar/CONTRIBUTING.md) · [Čeština](../cs/CONTRIBUTING.md) · [Deutsch](../de/CONTRIBUTING.md) · [Español](../es/CONTRIBUTING.md) · [فارسی](../fa/CONTRIBUTING.md) · [Français](../fr/CONTRIBUTING.md) · [हिन्दी](../hi/CONTRIBUTING.md) · Bahasa Indonesia · [Italiano](../it/CONTRIBUTING.md) · [日本語](../ja/CONTRIBUTING.md) · [한국어](../ko/CONTRIBUTING.md) · [Nederlands](../nl/CONTRIBUTING.md) · [Polski](../pl/CONTRIBUTING.md) · [Português](../pt/CONTRIBUTING.md) · [Русский](../ru/CONTRIBUTING.md) · [Svenska](../sv/CONTRIBUTING.md) · [ไทย](../th/CONTRIBUTING.md) · [Türkçe](../tr/CONTRIBUTING.md) · [Українська](../uk/CONTRIBUTING.md) · [Tiếng Việt](../vi/CONTRIBUTING.md) · [中文](../zh/CONTRIBUTING.md)

---

# Berkontribusi

Terima kasih atas minat Anda pada VD Infos. Kontribusi disambut baik: daftar deteksi,
terjemahan, laporan bug, dokumentasi, dan kode. Sebagian besar kontribusi tidak membutuhkan Kotlin.

## Kontribusi termudah: daftar

Sebagian besar dari apa yang dicari aplikasi berada sebagai **daftar teks biasa** di bawah
`VDInfos/app/src/main/assets/data/`, bukan sebagai kode. Menambah atau menghapus target adalah mengubah
satu baris dan membuka pull request:

- `*_apps.txt` - satu nama paket per baris.
- `props.txt` - katalog properti sistem, `CATEGORY<tab>key`.
- `spoof_keys.txt` - matriks pengaturan, `key:TYPE`.
- `known_certs.txt` - katalog sertifikat penandatanganan, `SHA-256<tab>owner` (hash
  lengkap, diambil dari APK nyata dengan `apksigner verify --print-certs`).
- berkas `.txt` lainnya - satu entri per baris (fragmen nama modul kernel, nama berkas,
  dan seterusnya).

Sebuah `#` memulai komentar dan baris kosong diabaikan. Beberapa berkas memuat beberapa field yang dipisahkan spasi
atau tab per baris, atau penanda opsional; pertahankan bentuk baris yang sudah
ada di berkas.

## Aturan dasar

- **VD Infos membaca kebenaran dan tidak pernah memalsukan apa pun.** Ia melaporkan apa yang dikembalikan setiap
  metode, persis. Jangan melunakkan sebuah pembacaan agar terlihat lebih bagus, dan jangan menambahkan
  apa pun yang akan mengubah sebuah nilai alih-alih membacanya.
- **Privasi bersifat mutlak.** Semuanya berjalan di perangkat. Jangan pernah menambahkan panggilan jaringan, atau
  apa pun yang mengirim pembacaan keluar dari perangkat.
- **Ekspor tetap disamarkan.** Nilai yang membawa identitas disamarkan dalam laporan yang dibagikan. Pertahankan
  seperti itu.

## Terjemahan

Terjemahan adalah kontribusi yang berharga. Readme diterjemahkan per bahasa
(`README.<lang>.md`), string aplikasi per bahasa, dan dokumentasi ini di bawah
`docs/<lang>/`. Jaga terminologi tetap konsisten dalam satu bahasa, sesuaikan dengan nada yang ada,
dan jangan pernah menerjemahkan secara mesin sebuah istilah teknis yang tidak Anda yakini.

## Melaporkan bug

Buka sebuah isu dengan model perangkat Anda, versi Android, dan, jika itu tentang sebuah pembacaan, sebuah
**ekspor** yang dibagikan (nilai-nilai yang membawa identitasnya disamarkan). Ekspor dibangun untuk
persis hal ini.

## Kode

- Build release, jangan pernah debug. Uji pada perangkat nyata. Lihat [Membangun](BUILDING.md).
- Jauhkan komentar dari kode; nama menjelaskan maksud. Satu-satunya header adalah banner
  lisensi di bagian atas setiap berkas sumber.
- Sesuaikan dengan gaya yang ada dan jaga perubahan tetap terfokus.

## Lisensi

Dengan berkontribusi Anda setuju bahwa kontribusi Anda dilisensikan di bawah
**GNU AGPL-3.0-or-later** proyek.

<!-- doc-footer -->

---

> _Dokumentasi ini dibuat dengan Claude (Anthropic) dan dikelola oleh VD171. Koreksi dan perbaikan dipersilakan; lihat [CONTRIBUTING.md](CONTRIBUTING.md)._
