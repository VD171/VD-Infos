> **Languages:** [English](../USER-GUIDE.md) · [العربية](../ar/USER-GUIDE.md) · [Čeština](../cs/USER-GUIDE.md) · [Deutsch](../de/USER-GUIDE.md) · [Español](../es/USER-GUIDE.md) · [فارسی](../fa/USER-GUIDE.md) · [Français](../fr/USER-GUIDE.md) · [हिन्दी](../hi/USER-GUIDE.md) · Bahasa Indonesia · [Italiano](../it/USER-GUIDE.md) · [日本語](../ja/USER-GUIDE.md) · [한국어](../ko/USER-GUIDE.md) · [Nederlands](../nl/USER-GUIDE.md) · [Polski](../pl/USER-GUIDE.md) · [Português](../pt/USER-GUIDE.md) · [Русский](../ru/USER-GUIDE.md) · [Svenska](../sv/USER-GUIDE.md) · [ไทย](../th/USER-GUIDE.md) · [Türkçe](../tr/USER-GUIDE.md) · [Українська](../uk/USER-GUIDE.md) · [Tiếng Việt](../vi/USER-GUIDE.md) · [中文](../zh/USER-GUIDE.md)

---

# Panduan pengguna VD Infos

VD Infos adalah **debugger metode perangkat**. Ia membaca setiap nilai perangkat melalui setiap
metode yang dapat membacanya, dan menyejajarkan pembacaan berdampingan sehingga Anda dapat membandingkannya.
Panduan ini membahas pemasangan aplikasi, menjalankan pemindaian, membaca hasil, dan
berbagi laporan. Ia tidak menafsirkan temuan spesifik apa pun untuk Anda: perbandingan itu
milik Anda untuk dibaca, di perangkat Anda sendiri.

## Daftar isi

1. [Apa itu VD Infos](#1-apa-itu-vd-infos)
2. [Apa yang bukan](#2-apa-yang-bukan)
3. [Persyaratan](#3-persyaratan)
4. [APK mana yang harus dipasang](#4-apk-mana-yang-harus-dipasang)
5. [Pemasangan dan menjalankan pertama kali](#5-pemasangan-dan-menjalankan-pertama-kali)
6. [Menjalankan pemindaian](#6-menjalankan-pemindaian)
7. [Membaca hasil](#7-membaca-hasil)
8. [Ekspor laporan Anda](#8-ekspor-laporan-anda)
9. [Untuk agen AI](#9-untuk-agen-ai)
10. [Bahasa dan tema](#10-bahasa-dan-tema)
11. [Privasi sekilas](#11-privasi-sekilas)
12. [Pertanyaan yang sering diajukan](#12-pertanyaan-yang-sering-diajukan)

## 1. Apa itu VD Infos

Android menyerahkan banyak informasi perangkat Anda ke setiap aplikasi yang Anda pasang. VD Infos
menunjukkan kepada Anda sebuah contoh dari apa yang dapat dibaca oleh aplikasi biasa, dan ia melakukannya sebagai *debugger
metode*: untuk setiap potong informasi ia membaca nilai melalui **setiap metode
yang dapat membacanya** - `Build.*`, `SystemProperties`, `getprop`, `__system_property_get`
nativa, system manager, content provider, berkas, syscall, dan
atestasi kunci perangkat keras (TEE) - dan menyejajarkannya sehingga Anda dapat membandingkan. Ia adalah lensa,
bukan mesin vonis: ia memunculkan pembacaan dan menyerahkan pembacaannya kepada Anda.

## 2. Apa yang bukan

- Ia **bukan** pembersih, booster, atau alat penyembunyi. Ia tidak mengubah apa pun pada
  sistem Anda; ia hanya membaca dan membandingkan.
- Ia **bukan** skor lulus/gagal. Ia menunjukkan kepada Anda pembacaan mentah; apa artinya bagi
  pengaturan Anda adalah milik Anda untuk dinilai, di perangkat Anda.
- Ia **tidak** membutuhkan internet, dan ia bekerja sepenuhnya dengan internet diblokir.

## 3. Persyaratan

- Android 8.0 (API 26) atau lebih baru.
- Tidak ada izin khusus yang diperlukan untuk memulai. Beberapa pembacaan menjadi tersedia hanya jika
  Anda memberikan izin yang sesuai (misalnya akses penggunaan), dan beberapa hanya ada
  pada perangkat yang di-root; ketika sebuah pembacaan tidak tersedia, aplikasi cukup menunjukkan bahwa ia tidak
  tersedia.

## 4. APK mana yang harus dipasang

Setiap rilis menerbitkan **dua APK**:

- **SDK_35** - menargetkan sandbox modern yang ketat. Inilah yang harus digunakan.
- **SDK_27** - menargetkan level API yang lebih lama, dengan domain SELinux yang lebih longgar. Ia berguna
  hanya untuk perbandingan berdampingan; kebanyakan orang tidak membutuhkannya.

Keduanya dipasang pada Android 8.0 dan lebih baru. Unduh keduanya dari halaman
[Releases](https://github.com/VD171/VD-Infos/releases).

## 5. Pemasangan dan menjalankan pertama kali

1. Unduh APK **SDK_35** dari Releases dan pasang (Anda mungkin perlu mengizinkan
   pemasangan dari peramban atau pengelola berkas Anda).
2. Buka aplikasi. Ia langsung membuka ke layar pemindaian; tidak ada akun, tidak ada
   masuk, dan tidak ada penyiapan.
3. Secara opsional berikan izin yang ditanyakan aplikasi (seperti akses penggunaan) untuk membuka
   pembacaan yang bergantung padanya. Ini sepenuhnya opsional.

## 6. Menjalankan pemindaian

Ketuk aksi pemindaian. Aplikasi menyebarkan sondanya secara paralel dan mengalirkan hasil ke dalam
daftar saat mereka tiba, dengan indikator kemajuan langsung. Pemindaian selesai dengan sendirinya; ia
membaca hanya selagi aplikasi terbuka, tidak menjaga apa pun berjalan di latar belakang, dan menutup
dirinya sendiri ketika dibiarkan menganggur.

Untuk menjalankannya lagi, pindai lagi: setiap pemindaian menggantikan hasil sebelumnya.

## 7. Membaca hasil

Setiap item adalah kartu yang menyejajarkan pembacaan dari setiap lensa (metode Java SDK,
metode nativa, shell, dan seterusnya). Warna kartu memberi tahu Anda, sekilas,
bagaimana pembacaan itu berhubungan satu sama lain:

- **Cocok** - setiap lensa setuju. Metode-metode mengembalikan nilai yang sama.
- **Divergensi** - lensa-lensa tidak setuju. Satu metode mengembalikan sesuatu yang berbeda
  dari yang lain.
- **Informasi** - sebuah pembacaan yang ditampilkan untuk referensi, tidak divoting.
- **Tunggal / tidak tersedia** - hanya satu lensa yang dapat membacanya, atau tidak ada.

Kartu selalu menampilkan matriks pembacaan lengkap, jadi Anda dapat melihat persis lensa mana
mengatakan apa. Kartu yang diciutkan menampilkan pembacaan pertamanya; bentangkan untuk melihat semuanya.

Itulah sejauh mana aplikasi berbicara untuk Anda. **Apa arti suatu divergensi tertentu di
perangkat Anda, dan apa yang harus dilakukan tentangnya, adalah milik Anda untuk diselidiki** - itulah seluruh inti
dari sebuah debugger: ia menaruh bukti di depan Anda dan membiarkan Anda membacanya. Gunakan
kotak pencarian untuk menyaring berdasarkan properti atau nilai, dan chip filter untuk mempersempit daftar.

## 8. Ekspor laporan Anda

Aksi berbagi dan simpan menyerahkan kepada Anda **seluruh hasil**, bukan hanya apa yang ada di layar:

- **Bagikan / simpan sebagai JSON** - laporan lengkap dalam bentuk yang dapat dibaca mesin, data yang
  sama yang disimpan aplikasi secara internal. Inilah format untuk disimpan atau diberikan ke sebuah alat.
- **Bagikan sebagai teks** - ringkasan ringkas yang dapat dibaca manusia dari divergensi-divergensi.

Nilai yang membawa identitas (nomor seri, IMEI, alamat MAC, ID iklan, dan sejenisnya)
**disamarkan dalam laporan yang diekspor** sehingga laporan yang Anda tempel ke dalam utas publik
tidak membocorkan pengidentifikasi Anda sendiri; vonis dan strukturnya dipertahankan. Salinan
di perangkat yang hanya Anda (atau root) yang dapat membaca menyimpan nilai asli.

## 9. Untuk agen AI

VD Infos dibangun untuk dibaca oleh alat, bukan hanya oleh orang. Setiap pemindaian menulis laporan
lengkapnya ke penyimpanan privat aplikasi, dan ekspornya adalah JSON yang sama yang dapat dibaca mesin.
Asisten pengkodean AI atau skrip analisis yang berjalan di perangkat dapat membaca seluruh
hasil dengan satu perintah alih-alih mengurai tangkapan layar. Lihat
[panduan agen AI](AI-AGENTS.md) khusus dan referensi [format ekspor](EXPORT-FORMAT.md).

## 10. Bahasa dan tema

Aplikasi mengikuti bahasa sistem Anda di mana terjemahan ada, dan Anda dapat memilih satu
secara manual. Ia menggunakan Material 3 dengan warna dinamis dan mengikuti pengaturan terang/gelap Anda.

## 11. Privasi sekilas

Semuanya berjalan di perangkat. Tidak ada pembacaan yang dikirim ke mana pun, dan laporan meninggalkan perangkat
hanya ketika Anda secara eksplisit membagikan atau menyimpannya. Setiap pemindaian menyimpan laporan lengkapnya di penyimpanan
privat aplikasi (`files/last_snapshot.json`) sehingga alat dapat membacanya; hanya aplikasi itu sendiri
atau root yang dapat membaca berkas itu, pemindaian berikutnya menimpanya, dan ia tidak pernah meninggalkan perangkat.
Lihat [Privasi](PRIVACY.md) untuk pernyataan lengkap.

## 12. Pertanyaan yang sering diajukan

**Apakah ia mengubah sesuatu di perangkat saya?** Tidak. Ia hanya membaca dan membandingkan.

**Apakah ia membutuhkan root?** Tidak. Aplikasi berjalan tanpa root. Segelintir pembacaan hanya ada
pada perangkat yang di-root, dan berkas laporan privat hanya dapat dibaca kembali oleh root atau aplikasi
itu sendiri; segala sesuatu yang lain bekerja tanpa root.

**Apakah ia membutuhkan internet?** Tidak. Ia bekerja sepenuhnya luring; blokir akses jaringannya jika
Anda mau.

**APK mana yang harus saya gunakan?** SDK_35. Build SDK_27 ada hanya untuk perbandingan.

**Mengapa beberapa item mengatakan tidak tersedia?** Karena metode itu tidak dapat membaca nilai itu pada
perangkat Anda - izin yang hilang, aturan sandbox, atau perangkat keras yang tidak hadir.
Itu sendiri adalah bagian dari perbandingan.

**Di mana saya melaporkan masalah atau menyarankan target deteksi?** Lihat
[Berkontribusi](CONTRIBUTING.md); kebanyakan daftar deteksi adalah teks biasa dan tidak membutuhkan kode.

<!-- doc-footer -->

---

> _Dokumentasi ini dibuat dengan Claude (Anthropic) dan dikelola oleh VD171. Koreksi dan perbaikan dipersilakan; lihat [CONTRIBUTING.md](CONTRIBUTING.md)._
