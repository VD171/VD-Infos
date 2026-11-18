> **Languages:** [English](../PRIVACY.md) · [العربية](../ar/PRIVACY.md) · [Čeština](../cs/PRIVACY.md) · [Deutsch](../de/PRIVACY.md) · [Español](../es/PRIVACY.md) · [فارسی](../fa/PRIVACY.md) · [Français](../fr/PRIVACY.md) · [हिन्दी](../hi/PRIVACY.md) · Bahasa Indonesia · [Italiano](../it/PRIVACY.md) · [日本語](../ja/PRIVACY.md) · [한국어](../ko/PRIVACY.md) · [Nederlands](../nl/PRIVACY.md) · [Polski](../pl/PRIVACY.md) · [Português](../pt/PRIVACY.md) · [Русский](../ru/PRIVACY.md) · [Svenska](../sv/PRIVACY.md) · [ไทย](../th/PRIVACY.md) · [Türkçe](../tr/PRIVACY.md) · [Українська](../uk/PRIVACY.md) · [Tiếng Việt](../vi/PRIVACY.md) · [中文](../zh/PRIVACY.md)

---

# Privasi

Privasi bukanlah sebuah fitur VD Infos. Ia adalah seluruh intinya. Halaman ini menyatakan persis
apa yang dilakukan dan tidak dilakukan aplikasi dengan data Anda, dalam istilah jelas yang dapat Anda verifikasi.

## Janji

- **Semuanya berjalan di perangkat.** Tidak ada pembacaan yang dikirim, diunggah, atau ditransmisikan ke
  server mana pun. Aplikasi bekerja sepenuhnya dengan internet diblokir.
- **Tanpa akun, tanpa masuk, tanpa pekerjaan latar belakang.** Aplikasi membaca hanya selagi ia terbuka,
  tidak menjaga apa pun berjalan di latar belakang, dan menutup dirinya sendiri ketika dibiarkan menganggur.
- **Tidak ada yang berubah pada sistem Anda.** VD Infos hanya membaca dan membandingkan; ia tidak pernah menulis
  ke keadaan perangkat Anda.

## Laporan tetap bersama Anda

Sebuah laporan meninggalkan perangkat **hanya ketika Anda secara eksplisit membagikan atau menyimpannya**. Hingga saat itu ia
ada di dua tempat, keduanya di perangkat:

- **Di layar**, selagi aplikasi terbuka.
- Di **penyimpanan privat** aplikasi (`files/last_snapshot.json`), yang hanya aplikasi
  itu sendiri atau root yang dapat membaca. Pemindaian berikutnya menimpanya, dan ia tidak pernah meninggalkan perangkat.
  Ia ada agar alat dan agen AI dapat membaca hasil lengkap; lihat
  [panduan agen AI](AI-AGENTS.md).

## Ekspor disamarkan

Ketika Anda membagikan atau menyimpan laporan, nilai yang membawa identitas - nomor seri, IMEI, alamat
MAC, ID iklan, dan sejenisnya - **disamarkan** sebelum laporan ditulis,
sehingga laporan yang Anda tempel ke dalam utas publik tidak membocorkan pengidentifikasi Anda sendiri.
Vonis dan strukturnya dipertahankan; hanya nilai sensitif yang diredaksi. Salinan
privat di perangkat menyimpan nilai asli untuk penggunaan Anda sendiri.

## Izin

Aplikasi dimulai tanpa izin yang diperlukan. Beberapa pembacaan menjadi tersedia hanya jika Anda
memberikan izin yang sesuai (misalnya akses penggunaan), dan memberikannya sepenuhnya
pilihan Anda; ketika sebuah pembacaan tidak tersedia, aplikasi cukup mengatakannya.

## Verifikasi sendiri

Anda tidak harus menerima semua ini dengan percaya begitu saja:

- Sumbernya publik: <https://github.com/VD171/VD-Infos>.
- Manifest ada di dalam sumber; konfirmasi izin mana yang ia deklarasikan.
- Blokir akses jaringan aplikasi dengan firewall, atau matikan internet, dan ia terus
  bekerja sama saja.

Lihat [panduan pengguna](USER-GUIDE.md) untuk cara membaca laporan dan apa yang dikandung ekspor.

<!-- doc-footer -->

---

> _Dokumentasi ini dibuat dengan Claude (Anthropic) dan dikelola oleh VD171. Koreksi dan perbaikan dipersilakan; lihat [CONTRIBUTING.md](CONTRIBUTING.md)._
