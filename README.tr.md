<!--
  VD INFOS - device method debugger: read every device info by every method, then compare.
  Copyright (C) 2026  VD171 - SPDX-License-Identifier: AGPL-3.0-or-later
-->

# VD Infos

<p align="center">
  <img src="images/logo-wordmark.png" alt="VD Infos" width="320">
</p>

<p align="center">
  <a href="https://github.com/VD171/VD-Infos/releases/latest"><img src="https://img.shields.io/github/v/release/VD171/VD-Infos?label=release&color=0D9488" alt="Release"></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/license-AGPL--3.0--or--later-1a7f37" alt="License"></a>
  <a href="https://github.com/VD171/VD-Infos/releases"><img src="https://img.shields.io/github/downloads/VD171/VD-Infos/total?label=downloads&color=8250df" alt="Downloads"></a>
  <a href="https://github.com/VD171/VD-Infos/stargazers"><img src="https://img.shields.io/github/stars/VD171/VD-Infos?color=d29922" alt="Stars"></a>
</p>

<p align="center">
  <b>Cihaz yöntem hata ayıklayıcısı.</b><br>
  Her cihaz değerini onu okuyabilen her yöntemle okuyun ve karşılaştırabilmeniz için yan yana dizin.
</p>

<p align="center">
  <a href="https://github.com/VD171/VD-Infos/releases">Releases</a> ·
  <a href="CATALOG.md">Catalog</a> ·
  <a href="#contributing">Contributing</a> ·
  <a href="#for-ai-agents-the-full-report-as-json">AI agents</a> ·
  <a href="CHANGELOG.md">Changelog</a> ·
  <a href="https://xdaforums.com/t/VD-Infos.4097379/">XDA thread</a>
</p>

> **Languages:** [English](README.md) · [العربية](README.ar.md) · [Čeština](README.cs.md) · [Deutsch](README.de.md) · [Español](README.es.md) · [فارسی](README.fa.md) · [Français](README.fr.md) · [हिन्दी](README.hi.md) · [Bahasa Indonesia](README.id.md) · [Italiano](README.it.md) · [日本語](README.ja.md) · [한국어](README.ko.md) · [Nederlands](README.nl.md) · [Polski](README.pl.md) · [Português](README.pt.md) · [Русский](README.ru.md) · [Svenska](README.sv.md) · [ไทย](README.th.md) · [Türkçe](README.tr.md) · [Українська](README.uk.md) · [Tiếng Việt](README.vi.md) · [中文](README.zh.md)

<p align="center">
  <img src="images/vdinfos-01.png" height="420"/>
  <img src="images/vdinfos-02.png" height="420"/>
</p>

---

Android son derece güçlü ve çok yönlü bir işletim sistemidir; kimsenin söylemediği şey ise tüm kişisel ayrıntılarınızın ve gizli bilgilerinizin kurduğunuz her uygulamaya açık olduğu ve bu tür gizlilik ihlallerine karşı kendinizi korumanın bir zorunluluk olduğudur. VD Infos, cihazınızdan nelerin yakalanabileceğine dair bir örnek gösterir ve bunu bir *yöntem hata ayıklayıcısı* olarak yapar: her bilgi için değeri **onu okuyabilen her yöntemle** okur - `Build.*`, `SystemProperties`, `getprop`, native `__system_property_get`, sistem yöneticileri, content provider'lar, dosyalar, syscall'lar ve donanım anahtarı attestation (TEE) - ve karşılaştırabilmeniz için yan yana dizer. Bir yöntem diğerleriyle uyuşmadığında, aradaki bir şey o yüzeyi yeniden yazıyordur: bir hook çerçevesi, bir spoofer, bir resolver shim'i. **HİÇBİR BİLGİ SAKLANMAZ, GÖNDERİLMEZ VEYA HERHANGİ BİR DOSYAYA¹ YA DA SUNUCUYA İLETİLMEZ** - her şey cihazda çalışır, kimlik taşıyan değerler siz açığa çıkarana kadar maskelenir ve bir rapor cihazdan yalnızca siz açıkça paylaştığınızda veya kaydettiğinizde çıkar; isterseniz internet erişimini bir güvenlik duvarıyla engelleyin ya da doğrudan kapatın.

> **¹ İstisna:** her tarama tam raporunu uygulamanın özel depolamasında saklar (`files/last_snapshot.json`). Onu yalnızca uygulamanın kendisi veya root okuyabilir, bir sonraki tarama üzerine yazar ve cihazdan asla çıkmaz. Yapay zekâ ajanlarının tam sonucu okuyabilmesi için vardır; bkz. **Yapay zekâ ajanları için: JSON olarak tam rapor**.

## Neyi inceler

Her öğe onu okuyabilen her yöntemle okunur (Java SDK / native / shell) ve yan yana karşılaştırılır:

* **~488 sistem özelliği** BEŞ şekilde okunur: `SystemProperties.get`, bionic'in iki giriş noktası (`__system_property_read_callback` ve 92 baytlık `__system_property_get`), JVM tarafından başlatılan `getprop` ve native koddan `popen` ile başlatılan `getprop` - `ProcessBuilder` tarafından çalıştırılan bir shell komutu ile aynı komutun JVM olmadan çalıştırılması aynı bakış açısı değildir, çünkü ilki bir root gizleme çerçevesinin yeniden yazdığı bir yüzeydir, ikincisi değildir.
* **Cihaz kimliği**: model, üretici, marka, device, product, board, hardware, fingerprint, bootloader, build id/tags/type - her `Build.*` alanı tüm `ro.product.*` (system/vendor/odm) varyantlarına, native ve shell'e karşı.
* **Tanımlayıcılar**: serial (birçok getter), Android ID (settings ve provider), GSF ID, advertising ID, IMEI/primary IMEI/MEID/IMSI/ICCID, user serial.
* **Telefon**: TelephonyManager yüzeyi (operatör, SIM, ağ, roaming...), SubscriptionManager (çoklu SIM), cell info.
* **Ağ**: arabirim başına **MAC** (`NetworkInterface` vs `/sys/class/net` vs shell), Wi-Fi (SSID/BSSID/IP/MAC), Bluetooth, DNS.
* **Çekirdek / süreç / sistem**: `uname`, boot id, uptime, UID/PID, SELinux, saat dilimi, locale, ortam, çalışan hizmetler/süreçler.
* **Donanım / medya**: CPU, bellek, sensörler, ekran, kameralar, system features, GPU, Widevine DRM ID.
* **Paketler / hesaplar / WebView**: kurulu paket listesi ve sayısı, uygulama başına imza digest'leri, bu uygulamanın imzası, kendi paketini inceleme, intent çözümleme sayımları, hesaplar, arama günlüğü sayısı, user agent.
* **Sandbox uygunluğu**: sandbox'ın KAPATMASI gereken yüzeyler (eMMC CID/serial, UFS ve SoC kimliği, `/proc/cmdline`, `/proc/1/*`, `/dev/kmsg`, `/system/build.prop`) ve 22 `dumpsys` hizmeti. Burada ret, cevaptır: `EACCES`/`DENIED` uygun okumadır ve içeriği bunun yerine teslim eden bir cihaz uygun değildir.
* **Reklam tanımlayıcıları**: Play Services binder'ından GAID, artı tüm anahtar ailesi (AAID, OAID/VAID ve Huawei'nin `pps_*`'leri) üç settings store boyunca taranır.
* **Root / hook / emülatör dedektörleri**: `su`/Magisk/KernelSU/APatch/Xposed/LSPosed/Riru artefaktları, `/proc/self/maps` içine enjekte edilmiş kütüphaneler, bilinen paketler, tehlikeli ve emülatör özellikleri, verified boot state.

## İndirme ve destek

Her sürüm iki APK yayınlar: **SDK_35** katı modern sandbox'ı hedefler ve kullanılması gereken budur; **SDK_27** daha eski bir API seviyesini, daha gevşek bir SELinux alanıyla hedefler, karşılaştırma için yararlıdır. İkisi de Android 8.0 ve üzerine kurulur.

* Release (APK): https://github.com/VD171/VD-Infos/releases
* https://github.com/VD171/VD-Infos
* https://xdaforums.com/t/VD-Infos.4097379/
* https://t.me/RootDetected
* https://t.me/BlankAssistance

## Yapay zekâ ajanları için: JSON olarak tam rapor

Cihazda bir kabuk üzerinden çalışan yapay zekâ ajanları ve LLM araçları için yapıldı (kodlama asistanları, analiz betikleri). Ajan ekran görüntüsü okumak yerine tüm sonucu tek komutla okur:

```sh
su -c cat /data/data/ru.vd171.vdinfos/files/last_snapshot.json
```

* **İçeriği:** her sonda kendi kararı ve tüm lenslerin değeriyle, dışa aktarma düğmesiyle aynı JSON'da. Ekran, daraltılmış bir öğenin yalnızca ilk okumasını gösterir; dosyada hepsi vardır.
* **Ne zaman güncellenir:** her taramanın sonunda; bu yüzden önce bir tarama çalıştırın, dosya her zaman en yenisini tutar.
* **Gereksinim:** root, çünkü dosya uygulamanın özel depolamasındadır.

## Mimari

```
core/model      değişmez alan: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          NativeBridge (JNI) - SystemProps (yansıma) - PropCatalog (veri)
                SemanticProbes - IntegrityProbes - ProbeRegistry
engine/         ProbeEngine - coroutine fan-out, sonuçları Flow olarak akıtır
data/           SnapshotStore (kalıcılık + diff) - Exporter (JSON/metin paylaşımı)
ui/             Jetpack Compose, Material 3, dinamik renk, canlı ilerleme
cpp/            native_probes.cpp - native lens, bağımlılıksız
```

* **Paralellik**: 996 sonda, sınırlı izin sayısıyla varsayılan dispatcher üzerinde dağılır; sonuçlar geldikçe UI'ye akar.
* **Arka planda hiçbir şey yok**: hizmet yok, zamanlanmış tarama yok; uygulama yalnızca açıkken çalışır ve boşta bırakılınca kendini kapatır.
* **Native katman**: `RegisterNatives` ile ada göre bağlanan küçük bir `.so`, kandırılması zor olması gereken parça olduğu için bilerek minik tutulur.

## Katkıda bulunma

Katkıların çoğu Kotlin gerektirmez: listeler `VDInfos/app/src/main/assets/data/` altında düz metindir. Bir satır ekleyin veya çıkarın ve bir pull request açın.

* `*_apps.txt` - satır başına bir paket adı
* `props.txt` - sistem özellikleri kataloğu, `KATEGORİ<tab>anahtar`
* `spoof_keys.txt` - settings spoof matrisi, `anahtar:TİP`
* `known_certs.txt` - imza sertifikası kataloğu, `SHA-256<tab>sahip` (tam hash, gerçek bir APK'dan `apksigner verify --print-certs` ile alınır)
* diğer `.txt` dosyaları - yine satır başına bir girdi (çekirdek modülü ad parçaları, `/data/local/tmp` adları)

Bir `#` yorum başlatır; boş satırlar yok sayılır. Bazı dosyalar satır başına boşluk veya sekme ile ayrılmış birkaç alan ya da isteğe bağlı işaretler (`props.txt` içinde `S` ve `@field`) içerir; dosyada zaten bulunan satırların biçimini koru. Kod katkıları da memnuniyetle karşılanır. Katkıda bulunarak çalışmanızın bu projenin AGPL-3.0-or-later lisansıyla dağıtılmasını kabul edersiniz.

## Katalog

Root gizleme ve tespit referansları (kılavuzlar, modüller, çerçeveler, dedektörler) ayrı bir katalogda: [CATALOG.md](CATALOG.md).

## Teşekkürler

**[HMA-OSS](https://github.com/frknkrc44/HMA-OSS)** geliştiricisi **[frknkrc44](https://github.com/frknkrc44)**'e gösterdiği özen için özel teşekkürler. HMA-OSS, bu uygulamanın önerdiği düzeltmelerin birçoğunun uygulandığı yerdir - hedef uygulamaları gizlemek ve spoof ön ayarlarını belirlemek - ve uygulama içindeki her anışı kaynağına geri bağlanır.

Bağlantıları, kanalları ve nasıl destek olabileceğiniz: [HMA-OSS.md](HMA-OSS.md).

## Belgeler

- **[Katalog](CATALOG.md)**: root gizleme ve tespit referansları - kılavuzlar, modüller, çerçeveler ve tespit ediciler.
- **[Değişiklik günlüğü](CHANGELOG.md)**: her sürümde nelerin değiştiği.
- **[HMA-OSS](HMA-OSS.md)**: uygulamanın düzeltmelerinin dayandığı uygulama gizleme modülü - bağlantıları, kanalları ve ona nasıl destek olunacağı.
- **[Katkıda bulunma](README.md#contributing)**: tespit hedeflerinin nasıl ekleneceği (çoğunlukla düz metin listeleri, Kotlin gerekmez).

<!-- /vd-docs -->

## Ayrıca bakınız

Aynı yazarın kardeş projeleri:

- **[VDGoogle](https://github.com/VD171/VD-Google)**: Google hizmetleriniz (RCS, Wallet, Play Integrity attestation'ları), cihazınızda internet olmadan okunur.
- **[VDBackup](https://github.com/VD171/VD-Backup)**: root olarak tam uygulama ve veri yedekleme ile geri yükleme, tamamen bir WebUI'den yönetilir.
- **[COPG-VD](https://github.com/VD171/COPG-VD)**: yeni bir fingerprint'i tutarlı, uyumlu bir profile dönüştüren bir cihaz sahteleyici.

Pixel `build.prop`: **[@VictorGabriel7Dev/build.prop](https://github.com/VictorGabriel7Dev/build.prop)** (fork of @T3SL4).

## İletişim

* https://vd171.ru
* https://vd.priv8.ru
* **Telegram:** @VD_Priv8 https://t.me/VD_Priv8
* **Channel:** @Priv8_VD https://t.me/Priv8_VD
* **Discord:** @VD.Priv8 https://discord.com/users/1296831918989639721
* **E-mail:** vd.priv8@pm.me
* **XDA-Developers:** @VD171 https://xdaforums.com/m/vd171.4699873/
* **XDA thread:** https://xdaforums.com/t/VD-Infos.4097379/
* **GitHub:** @VD171 https://github.com/VD171

## Lisans

**GNU AGPL-3.0-or-later** - bkz. [LICENSE](LICENSE). Copyleft, ağ maddesi dahil: değiştirilmiş bir sürümü çalıştıran herkes (hizmet olarak bile) kaynağını sunmak zorundadır. Fork'ları açık tutmak için bir araştırma/anti-tespit aracına bilerek seçilmiştir.

## Ticari markalar

VD Infos bağımsız, resmi olmayan bir araştırma ve teşhis aracıdır. Yazılımını, hizmetlerini veya markalarını okuduğu, tespit ettiği ya da adını andığı satıcıların hiçbiriyle (Google LLC ve Android Open Source Project dahil) bağlantılı değildir, onlar tarafından onaylanmamış veya desteklenmemiştir. "Android", "Google", "Google Play", "Play Integrity", "Widevine" ve diğer tüm ürün adları, logolar ve ticari markalar ilgili sahiplerinin mülküdür. VD Infos yalnızca cihazınızda zaten bulunanı okur ve hiçbir yere hiçbir şey göndermez.

## Tags

`android` `privacy` `security` `root-detection` `hook-detection` `spoof-detection` `anti-detection` `tamper-detection` `device-fingerprint` `magisk` `kernelsu` `lsposed` `play-integrity` `attestation` `tee` `keystore` `scanner` `diagnostics` `reverse-engineering` `kotlin`

<!-- doc-footer -->

---

> _Bu belgeler Claude (Anthropic) ile oluşturulmuş olup VD171 tarafından yürütülmektedir. Düzeltmeler ve iyileştirmeler memnuniyetle karşılanır; bkz. [Contributing](README.md#contributing)._
