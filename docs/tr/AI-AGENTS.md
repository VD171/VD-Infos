> **Languages:** [English](../AI-AGENTS.md) · [العربية](../ar/AI-AGENTS.md) · [Čeština](../cs/AI-AGENTS.md) · [Deutsch](../de/AI-AGENTS.md) · [Español](../es/AI-AGENTS.md) · [فارسی](../fa/AI-AGENTS.md) · [Français](../fr/AI-AGENTS.md) · [हिन्दी](../hi/AI-AGENTS.md) · [Bahasa Indonesia](../id/AI-AGENTS.md) · [Italiano](../it/AI-AGENTS.md) · [日本語](../ja/AI-AGENTS.md) · [한국어](../ko/AI-AGENTS.md) · [Nederlands](../nl/AI-AGENTS.md) · [Polski](../pl/AI-AGENTS.md) · [Português](../pt/AI-AGENTS.md) · [Русский](../ru/AI-AGENTS.md) · [Svenska](../sv/AI-AGENTS.md) · [ไทย](../th/AI-AGENTS.md) · Türkçe · [Українська](../uk/AI-AGENTS.md) · [Tiếng Việt](../vi/AI-AGENTS.md) · [中文](../zh/AI-AGENTS.md)

---

# Yapay zeka ajanları için VD Infos

VD Infos **yalnızca insanlar tarafından değil, makineler tarafından okunmak üzere oluşturulmuştur**.
Bir cihaz metot hata ayıklayıcısı çok sayıda yapılandırılmış kanıt üretir - her değer, her metot
aracılığıyla okunmuş, karşılaştırma için dizilmiş - ve bu kanıt bir ajan için bir ekran görüntüsü
olarak değil, veri olarak çok daha yararlıdır. Bu yüzden tüm sonuç, makine tarafından okunabilir tek
bir belge olarak mevcuttur ve uygulama baştan sona bunun etrafında tasarlanmıştır.

Bir yapay zeka kodlama asistanı, bir LLM aracı, bir analiz betiği veya cihaza bir kabuk aracılığıyla
ulaşan herhangi bir otomasyon iseniz, bu sayfa sizin içindir.

## Neden ajan dostudur

- **Bir ekran değil, bir belge.** Tüm tarama - her prob, her mercek, her hüküm - tek bir JSON
  nesnesidir. OCR yok, ekran kazıma yok, daraltılmış kartlar arasında dokunma yok.
- **Arayüzün sahip olduğu verinin aynısı.** Dosya bir özet değildir. Uygulamanın dahili olarak
  tuttuğunun tam olarak aynısıdır: ekran daraltılmış bir öğenin ilk okumasını gösterir, dosyada
  hepsi vardır.
- **Belirlenimci biçim.** Yapı kararlı ve belgelenmiştir (bkz. [EXPORT-FORMAT](EXPORT-FORMAT.md)),
  böylece bir kez yazılan bir ayrıştırıcı çalışmaya devam eder.
- **Ağ yok, hesap yok, yönetilecek durum yok.** Uygulama cihazda okur ve bir dosya yazar. Ajanınız
  dosyayı okur. Tüm sözleşme budur.
- **Tasarım gereği çevrimdışı.** İnternete ihtiyaç duymaz, bu yüzden bir ajanın çalışıyor olabileceği
  kilitli veya ağ bağlantısı kesilmiş bir ortamın içinde de aynı şekilde çalışır.

## Tek komutluk okuma

Her tarama tam raporunu uygulamanın özel depolamasına yazar. Bir tarama çalıştıktan sonra, bir ajan
tam sonucu tek bir komutla okur:

```sh
su -c cat /data/data/ru.vd171.vdinfos/files/last_snapshot.json
```

- **Neyi içerir:** her prob, hükmü ve her mercekten gelen değeriyle birlikte, dışa aktarma düğmesiyle
  aynı JSON'da.
- **Ne zaman güncellenir:** her taramanın sonunda. Önce bir tarama çalıştırın; dosya her zaman en
  sonuncuyu tutar.
- **Gereksinim:** root, çünkü dosya uygulamanın özel depolamasında bulunur. Root olmadan, bunun yerine
  uygulama içindeki **JSON olarak paylaş / kaydet** eylemini kullanın ve ajanın eline verilen dosyayı
  okuyun.

## Tipik bir ajan akışı

1. Uygulamanın kurulu olduğundan emin olun ([Releases](https://github.com/VD171/VD-Infos/releases)
   sayfasından **SDK_35** APK'si).
2. Bir tarama tetikleyin - bir kişi tarama'ya dokunur veya bir otomasyon uygulamanın tarama ekranını
   başlatır - ve bitmesini bekleyin.
3. Yukarıdaki komutla `files/last_snapshot.json` dosyasını okuyun.
4. [Dışa aktarma biçimini](EXPORT-FORMAT.md) şemanız olarak kullanarak ayrıştırın: probları yineleyin,
   her birinin `verdict` değerini okuyun ve mercek başına `values` değerlerini dizin.
5. Yapılandırılmış sonuç üzerinde kendi iş akışınızda işlem yapın.

Okumaların eldeki görev için ne anlama geldiğine ajan karar verir; VD Infos kanıtı, temiz ve eksiksiz
bir şekilde sağlar.

## Ajanın elde ettiği ve etmediği

- Rapor **her okumayı ve her hükmü** taşır, böylece bir ajan uygulamanın oluşturduğu tam resme sahip
  olur.
- Kimlik taşıyan değerler (seri numaraları, IMEI, MAC adresleri, reklam kimlikleri ve benzeri)
  **paylaşılan/dışa aktarılan raporda maskelenir**, böylece elden ele dolaşan bir rapor hükümlerini ve
  yapısını korurken kişisel tanımlayıcılardan arınmış kalır. Yalnızca uygulama veya root tarafından
  okunabilen **cihazdaki** `last_snapshot.json`, bunlara ihtiyaç duyan yerel bir ajan için gerçek
  değerleri saklar.

## Ayrıştırılmak üzere yazılmış belgeler

Bu wiki, insanlar için olduğu kadar makine okuması için de yapılandırılmıştır: kısa bölümler, kararlı
başlıklar, açık alan adları ve bir ajanın şema olarak ele alabileceği özel bir
[dışa aktarma biçimi](EXPORT-FORMAT.md) sayfası. VD Infos üzerine bir araç inşa ediyorsanız, oradan
başlayın.

## İlgili sayfalar

- [Dışa aktarma biçimi](EXPORT-FORMAT.md) - JSON şeması, alan alan.
- [Kullanıcı kılavuzu](USER-GUIDE.md) - kurulum, tarama ve paylaşım.
- [Mimari](ARCHITECTURE.md) - uygulamanın nasıl bir araya getirildiği.

<!-- doc-footer -->

---

> _Bu belgeler Claude (Anthropic) ile oluşturulmuş olup VD171 tarafından yürütülmektedir. Düzeltmeler ve iyileştirmeler memnuniyetle karşılanır; bkz. [CONTRIBUTING.md](CONTRIBUTING.md)._
