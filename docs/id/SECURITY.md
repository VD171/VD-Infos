> **Languages:** [English](../SECURITY.md) · [العربية](../ar/SECURITY.md) · [Čeština](../cs/SECURITY.md) · [Deutsch](../de/SECURITY.md) · [Español](../es/SECURITY.md) · [فارسی](../fa/SECURITY.md) · [Français](../fr/SECURITY.md) · [हिन्दी](../hi/SECURITY.md) · Bahasa Indonesia · [Italiano](../it/SECURITY.md) · [日本語](../ja/SECURITY.md) · [한국어](../ko/SECURITY.md) · [Nederlands](../nl/SECURITY.md) · [Polski](../pl/SECURITY.md) · [Português](../pt/SECURITY.md) · [Русский](../ru/SECURITY.md) · [Svenska](../sv/SECURITY.md) · [ไทย](../th/SECURITY.md) · [Türkçe](../tr/SECURITY.md) · [Українська](../uk/SECURITY.md) · [Tiếng Việt](../vi/SECURITY.md) · [中文](../zh/SECURITY.md)

---

# Kebijakan keamanan

VD Infos adalah alat privasi yang membaca keadaan perangkat yang sensitif, jadi laporan
keamanan ditanggapi dengan serius.

## Melaporkan kerentanan

Mohon laporkan masalah keamanan **secara privat**, bukan di isu publik:

- Kirim e-mail ke **vd.priv8@pm.me**.
- Deskripsikan masalahnya, versi yang terpengaruh, dan cara mereproduksinya.
- Jika menyangkut paparan data, deskripsikan kelas masalahnya; Anda tidak perlu melampirkan
  exploit yang berfungsi.

Anda akan mendapat tanggapan secepat mungkin. Mohon berikan jendela yang wajar untuk memperbaiki
masalah sebelum pengungkapan publik apa pun.

## Apa yang dihitung sebagai masalah keamanan

Karena apa yang dijanjikan VD Infos, hal-hal ini sangat penting:

- Apa pun yang akan membuat aplikasi menjangkau jaringan, atau mengirim pembacaan apa pun keluar dari
  perangkat.
- Apa pun yang akan membocorkan nilai yang membawa identitas ke dalam **ekspor** yang dibagikan atau disimpan
  (nomor seri, IMEI, alamat MAC, ID iklan, dan sejenisnya) meskipun ada penyamaran.
- Apa pun yang memungkinkan aplikasi lain membaca berkas laporan privat yang dimaksudkan untuk dapat
  dibaca hanya oleh aplikasi itu sendiri atau root.

## Apa yang bukan masalah keamanan

- Sebuah pembacaan yang tampak "salah" tetapi merupakan nilai jujur yang dikembalikan sebuah metode. VD Infos
  melaporkan apa yang dikatakan setiap metode; membaca perbandingan adalah milik Anda untuk dilakukan. Lihat
  [panduan pengguna](USER-GUIDE.md).
- Sebuah pembacaan yang ditandai tidak tersedia. Itu berarti sebuah metode tidak dapat membaca nilai itu pada
  perangkat Anda, yang itu sendiri adalah bagian dari perbandingan.

## Versi yang didukung

Hanya versi terbaru yang dirilis yang didukung. Mohon reproduksi pada build terbaru
sebelum melaporkan.

<!-- doc-footer -->

---

> _Dokumentasi ini dibuat dengan Claude (Anthropic) dan dikelola oleh VD171. Koreksi dan perbaikan dipersilakan; lihat [CONTRIBUTING.md](CONTRIBUTING.md)._
