> **Languages:** [English](../USER-GUIDE.md) · [العربية](../ar/USER-GUIDE.md) · [Čeština](../cs/USER-GUIDE.md) · [Deutsch](../de/USER-GUIDE.md) · [Español](../es/USER-GUIDE.md) · [فارسی](../fa/USER-GUIDE.md) · [Français](../fr/USER-GUIDE.md) · [हिन्दी](../hi/USER-GUIDE.md) · [Bahasa Indonesia](../id/USER-GUIDE.md) · [Italiano](../it/USER-GUIDE.md) · [日本語](../ja/USER-GUIDE.md) · [한국어](../ko/USER-GUIDE.md) · [Nederlands](../nl/USER-GUIDE.md) · [Polski](../pl/USER-GUIDE.md) · [Português](../pt/USER-GUIDE.md) · [Русский](../ru/USER-GUIDE.md) · [Svenska](../sv/USER-GUIDE.md) · [ไทย](../th/USER-GUIDE.md) · Türkçe · [Українська](../uk/USER-GUIDE.md) · [Tiếng Việt](../vi/USER-GUIDE.md) · [中文](../zh/USER-GUIDE.md)

---

# VD Infos kullanıcı kılavuzu

VD Infos bir **cihaz metot hata ayıklayıcısıdır**. Her cihaz değerini onu okuyabilen her metot
aracılığıyla okur ve okumaları karşılaştırabilmeniz için yan yana dizer. Bu kılavuz uygulamanın
kurulumunu, bir tarama çalıştırmayı, sonucu okumayı ve bir rapor paylaşmayı kapsar. Sizin için
herhangi bir belirli bulguyu yorumlamaz: karşılaştırma, kendi cihazınızda, okumanız için sizindir.

## İçindekiler

1. [VD Infos nedir](#1-vd-infos-nedir)
2. [Ne değildir](#2-ne-değildir)
3. [Gereksinimler](#3-gereksinimler)
4. [Hangi APK kurulmalı](#4-hangi-apk-kurulmalı)
5. [Kurulum ve ilk çalıştırma](#5-kurulum-ve-ilk-çalıştırma)
6. [Tarama çalıştırma](#6-tarama-çalıştırma)
7. [Sonucu okuma](#7-sonucu-okuma)
8. [Raporunuzu dışa aktarma](#8-raporunuzu-dışa-aktarma)
9. [Yapay zeka ajanları için](#9-yapay-zeka-ajanları-için)
10. [Dil ve tema](#10-dil-ve-tema)
11. [Bir bakışta gizlilik](#11-bir-bakışta-gizlilik)
12. [Sıkça sorulan sorular](#12-sıkça-sorulan-sorular)

## 1. VD Infos nedir

Android, cihazınızın bilgilerinin büyük bir bölümünü kurduğunuz her uygulamaya verir. VD Infos size
sıradan bir uygulamanın neyi okuyabileceğine dair bir örnek gösterir ve bunu bir *metot hata
ayıklayıcısı* olarak yapar: her bilgi parçası için değeri **onu okuyabilen her metot** aracılığıyla
okur - platform API'leri, sistem özellikleri, yerel çağrılar, sistem
yöneticileri, content providers, dosyalar, syscalls ve donanım anahtar doğrulaması (TEE) - ve
karşılaştırabilmeniz için bunları dizer. O bir mercektir, bir hüküm makinesi değil: okumaları
yüzeye çıkarır ve onların okunmasını size bırakır.

## 2. Ne değildir

- O bir temizleyici, bir hızlandırıcı veya bir gizleme aracı **değildir**. Sisteminizde hiçbir şeyi
  değiştirmez; yalnızca okur ve karşılaştırır.
- O bir geçti/kaldı puanı **değildir**. Size ham okumaları gösterir; bunların kurulumunuz için ne
  anlama geldiği, cihazınızda, sizin yargılamanız içindir.
- İnternete ihtiyaç **duymaz** ve internet engellenmişken tamamen çalışır.

## 3. Gereksinimler

- Android 8.0 (API 26) veya daha yenisi.
- Başlatmak için özel bir izin gerekmez. Bazı okumalar yalnızca eşleşen izni verdiğinizde
  kullanılabilir hale gelir (örneğin kullanım erişimi) ve bazıları yalnızca köklendirilmiş bir
  cihazda bulunur; bir
  okumanın kullanılamadığı yerde, uygulama yalnızca kullanılamadığını gösterir.

## 4. Hangi APK kurulmalı

Her sürüm **iki APK** yayınlar:

- **SDK_35** - katı modern sanal alanı hedefler. Kullanılacak olan budur.
- **SDK_27** - daha eski bir API düzeyini hedefler. Yalnızca yan
  yana karşılaştırma için kullanışlıdır; çoğu kişinin buna ihtiyacı yoktur.

Her ikisi de Android 8.0 ve daha yenisine kurulur. Bunları
[Releases](https://github.com/VD171/VD-Infos/releases) sayfasından indirin.

## 5. Kurulum ve ilk çalıştırma

1. **SDK_35** APK'sini Releases'ten indirin ve kurun (tarayıcınızdan veya dosya yöneticinizden
   kurulumlara izin vermeniz gerekebilir).
2. Uygulamayı açın. Doğrudan tarama ekranına açılır; hesap, oturum açma veya kurulum yoktur.
3. İsteğe bağlı olarak, ona bağlı okumaların kilidini açmak için uygulamanın sorduğu bir izni (örneğin
   kullanım erişimi) verin. Bu tamamen isteğe bağlıdır.

## 6. Tarama çalıştırma

Tarama eylemine dokunun. Uygulama problarını paralel olarak yelpaze gibi açar ve sonuçları
geldikçe, canlı bir ilerleme göstergesiyle listeye akıtır. Bir tarama kendi kendine tamamlanır;
yalnızca uygulama açıkken okur, arka planda hiçbir şeyi çalışır durumda tutmaz ve boş bırakıldığında
kendini kapatır.

Yeniden çalıştırmak için yeniden tarayın: her tarama önceki sonucun yerini alır.

## 7. Sonucu okuma

Her öğe, her mercekten (Java SDK metodu, yerel metot, kabuk vb.) gelen okumaları dizen bir karttır.
Kartın rengi, bu okumaların birbirleriyle nasıl ilişkili olduğunu bir bakışta söyler:

- **Eşleşme** - her mercek hemfikirdi. Metotlar aynı değeri döndürdü.
- **Ayrışma** - mercekler hemfikir değildi. Bir metot diğerlerinden farklı bir şey döndürdü.
- **Bilgi** - referans için gösterilen, oylanmayan bir okuma.
- **Tekil / kullanılamaz** - yalnızca bir mercek okuyabildi veya hiçbiri okuyamadı.

Kart her zaman okumaların tam matrisini gösterir, böylece hangi merceğin ne dediğini tam olarak
görebilirsiniz. Daraltılmış bir kart ilk okumasını gösterir; hepsini görmek için genişletin.

Uygulama sizin adınıza bu kadar konuşur. **Belirli bir ayrışmanın cihazınızda ne anlama geldiği ve
bununla ne yapılacağı, araştırmanız içindir** - bir hata ayıklayıcının bütün amacı budur: kanıtı
önünüze koyar ve onu okumanıza izin verir. Mülke veya değere göre filtrelemek için arama kutusunu ve
listeyi daraltmak için filtre çiplerini kullanın.

## 8. Raporunuzu dışa aktarma

Paylaş ve kaydet eylemleri size yalnızca ekrandakini değil, **tüm sonucu** verir:

- **JSON olarak paylaş / kaydet** - uygulamanın dahili olarak tuttuğu verinin aynısı olan, makine
  tarafından okunabilir biçimdeki tam rapor. Bu, saklanacak veya bir araca beslenecek biçimdir.
- **Metin olarak paylaş** - ayrışmaların kompakt, insan tarafından okunabilir bir özeti.

Kimlik taşıyan değerler (seri numaraları, IMEI, MAC adresleri, reklam kimlikleri ve benzeri)
**dışa aktarılan raporda maskelenir**, böylece herkese açık bir konuya yapıştırdığınız bir rapor kendi
tanımlayıcılarınızı sızdırmaz; hükümler ve yapı korunur. Yalnızca sizin (veya root'un) okuyabildiği
cihazdaki kopya gerçek değerleri saklar.

## 9. Yapay zeka ajanları için

VD Infos yalnızca insanlar tarafından değil, araçlar tarafından okunmak üzere oluşturulmuştur. Her
tarama tam raporunu uygulamanın özel depolamasına yazar ve dışa aktarım aynı makine tarafından
okunabilir JSON'dur. Cihazda çalışan bir yapay zeka kodlama asistanı veya analiz betiği, ekran
görüntülerini ayrıştırmak yerine tüm sonucu tek bir komutla okuyabilir. Özel
[Yapay zeka ajanları kılavuzuna](AI-AGENTS.md) ve [dışa aktarma biçimi](EXPORT-FORMAT.md) referansına
bakın.

## 10. Dil ve tema

Uygulama, bir çevirinin bulunduğu yerde sistem dilinizi izler ve birini manuel olarak seçebilirsiniz.
Dinamik renkli Material 3 kullanır ve açık/koyu ayarınızı izler.

## 11. Bir bakışta gizlilik

Her şey cihazda çalışır. Hiçbir okuma hiçbir yere gönderilmez ve bir rapor cihazdan yalnızca onu
açıkça paylaştığınızda veya kaydettiğinizde ayrılır. Her tarama, araçların okuyabilmesi için tam
raporunu uygulamanın özel depolamasında (`files/last_snapshot.json`) saklar; o dosyayı yalnızca
uygulamanın kendisi veya root okuyabilir, bir sonraki tarama onun üzerine yazar ve o asla cihazdan
ayrılmaz. Tam beyan için [Gizlilik](PRIVACY.md) sayfasına bakın.

## 12. Sıkça sorulan sorular

**Cihazımda bir şey değiştiriyor mu?** Hayır. Yalnızca okur ve karşılaştırır.

**Root'a ihtiyaç duyuyor mu?** Hayır. Uygulama root olmadan çalışır. Bir avuç okuma yalnızca
köklendirilmiş bir cihazda bulunur ve özel rapor dosyası yalnızca root veya uygulamanın kendisi
tarafından geri okunabilir; geri kalan her şey root'suz çalışır.

**İnternete ihtiyaç duyuyor mu?** Hayır. Tamamen çevrimdışı çalışır; isterseniz ağ erişimini
engelleyin.

**Hangi APK'yi kullanmalıyım?** SDK_35. SDK_27 derlemesi yalnızca karşılaştırma için vardır.

**Bazı öğeler neden kullanılamaz diyor?** Çünkü o metot o değeri cihazınızda okuyamadı - eksik bir
izin, bir sanal alan kuralı veya mevcut olmayan donanım. Bu da karşılaştırmanın bir parçasıdır.

**Bir sorunu nereye bildiririm veya bir algılama hedefi nasıl öneririm?** [Katkıda bulunma](CONTRIBUTING.md)
sayfasına bakın; çoğu algılama listesi düz metindir ve koda ihtiyaç duymaz.

<!-- doc-footer -->

---

> _Bu belgeler Claude (Anthropic) ile oluşturulmuş olup VD171 tarafından yürütülmektedir. Düzeltmeler ve iyileştirmeler memnuniyetle karşılanır; bkz. [CONTRIBUTING.md](CONTRIBUTING.md)._
