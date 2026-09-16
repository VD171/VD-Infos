[English](README.md) | [Português](README.pt.md) | [Español](README.es.md) | [Italiano](README.it.md) | [Deutsch](README.de.md) | [Français](README.fr.md) | [Русский](README.ru.md) | [Bahasa Indonesia](README.id.md) | **Türkçe** | [Polski](README.pl.md) | [Nederlands](README.nl.md) | [Svenska](README.sv.md) | [Čeština](README.cs.md) | [Tiếng Việt](README.vi.md) | [中文](README.zh.md) | [日本語](README.ja.md) | [한국어](README.ko.md) | [فارسی](README.fa.md) | [हिन्दी](README.hi.md) | [العربية](README.ar.md) | [ไทย](README.th.md)

# VD Infos

*Yöntem hata ayıklayıcı*

[![License: AGPL-3.0-or-later](https://img.shields.io/badge/license-AGPL--3.0--or--later-blue.svg)](LICENSE)
[![Release](https://img.shields.io/github/v/release/VD171/VD-Infos)](https://github.com/VD171/VD-Infos/releases)
[![Downloads](https://img.shields.io/github/downloads/VD171/VD-Infos/total)](https://github.com/VD171/VD-Infos/releases)
[![Android 8.0+](https://img.shields.io/badge/Android-8.0%2B-3DDC84.svg)](https://github.com/VD171/VD-Infos/releases)
[![Languages](https://img.shields.io/badge/languages-21-orange.svg)](README.md)

<img src="images/vdinfos-01.png" height="420"/> <img src="images/vdinfos-02.png" height="420"/>

Android son derece güçlü ve çok yönlü bir işletim sistemidir; kimsenin söylemediği şey ise tüm kişisel ayrıntılarınızın ve gizli bilgilerinizin kurduğunuz her uygulamaya açık olduğu ve bu tür gizlilik ihlallerine karşı kendinizi korumanın bir zorunluluk olduğudur. VD Infos, cihazınızdan nelerin yakalanabileceğine dair bir örnek gösterir ve bunu bir *yöntem hata ayıklayıcısı* olarak yapar: her bilgi için değeri **onu okuyabilen her yöntemle** okur - `Build.*`, `SystemProperties`, `getprop`, native `__system_property_get`, sistem yöneticileri, content provider'lar, dosyalar, syscall'lar ve donanım anahtarı attestation (TEE) - ve karşılaştırabilmeniz için yan yana dizer. Bir yöntem diğerleriyle uyuşmadığında, aradaki bir şey o yüzeyi yeniden yazıyordur: bir hook çerçevesi, bir spoofer, bir resolver shim'i. **HİÇBİR BİLGİ SAKLANMAZ, GÖNDERİLMEZ VEYA HERHANGİ BİR DOSYAYA YA DA SUNUCUYA İLETİLMEZ** - her şey cihazda çalışır, kimlik taşıyan değerler siz açığa çıkarana kadar maskelenir ve bir rapor cihazdan yalnızca siz açıkça paylaştığınızda veya kaydettiğinizde çıkar; isterseniz internet erişimini bir güvenlik duvarıyla engelleyin ya da doğrudan kapatın.

## Neyi inceler

Her öğe onu okuyabilen her yöntemle okunur (Java SDK / native / shell) ve yan yana karşılaştırılır:

* **~493 sistem özelliği** BEŞ şekilde okunur: `SystemProperties.get`, bionic'in iki giriş noktası (`__system_property_read_callback` ve 92 baytlık `__system_property_get`), JVM tarafından başlatılan `getprop` ve native koddan `popen` ile başlatılan `getprop` - `ProcessBuilder` tarafından çalıştırılan bir shell komutu ile aynı komutun JVM olmadan çalıştırılması aynı bakış açısı değildir, çünkü ilki bir root gizleme çerçevesinin yeniden yazdığı bir yüzeydir, ikincisi değildir.
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

## Diller

21 arayüz dili: İngilizce, Portekizce, İspanyolca, İtalyanca, Almanca, Fransızca, Rusça, Endonezce, Türkçe, Lehçe, Felemenkçe, İsveççe, Çekçe, Vietnamca, Çince, Japonca, Korece, Farsça, Hintçe, Arapça ve Tayca. Uygulama, ilk açılışta tek seferlik bir dil seçimi sunar ("Sistem varsayılanı" seçeneğiyle) ve aksi halde sistem dilini izler.

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

* **Paralellik**: 885 sonda, sınırlı izin sayısıyla varsayılan dispatcher üzerinde dağılır; sonuçlar geldikçe UI'ye akar.
* **Arka planda hiçbir şey yok**: hizmet yok, zamanlanmış tarama yok; uygulama yalnızca açıkken çalışır ve boşta bırakılınca kendini kapatır.
* **Native katman**: `RegisterNatives` ile ada göre bağlanan küçük bir `.so`, kandırılması zor olması gereken parça olduğu için bilerek minik tutulur.

## Katkıda bulunma

Katkıların çoğu Kotlin gerektirmez: listeler `VDInfos/app/src/main/assets/data/` altında düz metindir. Bir satır ekleyin veya çıkarın ve bir pull request açın.

* `*_apps.txt` - satır başına bir paket adı
* `props.txt` - sistem özellikleri kataloğu, `KATEGORİ<tab>anahtar`
* `spoof_keys.txt` - settings spoof matrisi, `anahtar:TİP`
* diğer `.txt` dosyaları - yine satır başına bir girdi (çekirdek modülü ad parçaları, `/data/local/tmp` adları)

Bir `#` yorum başlatır; boş satırlar yok sayılır. Kod katkıları da memnuniyetle karşılanır. Katkıda bulunarak çalışmanızın bu projenin AGPL-3.0-or-later lisansıyla dağıtılmasını kabul edersiniz.

## Katalog

Root gizleme ve tespit referansları (kılavuzlar, modüller, çerçeveler, dedektörler) ayrı bir katalogda: [CATALOG.md](CATALOG.md).

## Teşekkürler

**[HMA-OSS](https://github.com/frknkrc44/HMA-OSS)** geliştiricisi **[frknkrc44](https://github.com/frknkrc44)**'e gösterdiği özen için özel teşekkürler. HMA-OSS, bu uygulamanın önerdiği düzeltmelerin birçoğunun uygulandığı yerdir - hedef uygulamaları gizlemek ve spoof ön ayarlarını belirlemek - ve uygulama içindeki her anışı kaynağına geri bağlanır.

Bağlantıları, kanalları ve nasıl destek olabileceğiniz: [HMA-OSS.md](HMA-OSS.md).

## İletişim

* https://vd171.ru
* https://vd.priv8.ru
* **Telegram:** @VD_Priv8 https://t.me/VD_Priv8
* **Discord:** @VD.Priv8 https://discord.com/users/1296831918989639721
* **E-posta:** vd.priv8@pm.me
* **XDA-Developers:** @VD171 https://xdaforums.com/m/vd171.4699873/
* **GitHub:** @VD171 https://github.com/VD171

## Lisans

**GNU AGPL-3.0-or-later** - bkz. [LICENSE](LICENSE). Copyleft, ağ maddesi dahil: değiştirilmiş bir sürümü çalıştıran herkes (hizmet olarak bile) kaynağını sunmak zorundadır. Fork'ları açık tutmak için bir araştırma/anti-tespit aracına bilerek seçilmiştir.

---

`android` `privacy` `security` `root-detection` `hook-detection` `spoof-detection` `anti-detection` `tamper-detection` `device-fingerprint` `magisk` `kernelsu` `xposed` `play-integrity` `attestation` `tee` `keystore` `scanner` `diagnostics` `reverse-engineering` `kotlin`
