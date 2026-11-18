> **Languages:** [English](../CONTRIBUTING.md) · [العربية](../ar/CONTRIBUTING.md) · [Čeština](../cs/CONTRIBUTING.md) · [Deutsch](../de/CONTRIBUTING.md) · [Español](../es/CONTRIBUTING.md) · [فارسی](../fa/CONTRIBUTING.md) · [Français](../fr/CONTRIBUTING.md) · [हिन्दी](../hi/CONTRIBUTING.md) · [Bahasa Indonesia](../id/CONTRIBUTING.md) · [Italiano](../it/CONTRIBUTING.md) · [日本語](../ja/CONTRIBUTING.md) · [한국어](../ko/CONTRIBUTING.md) · [Nederlands](../nl/CONTRIBUTING.md) · [Polski](../pl/CONTRIBUTING.md) · [Português](../pt/CONTRIBUTING.md) · [Русский](../ru/CONTRIBUTING.md) · [Svenska](../sv/CONTRIBUTING.md) · [ไทย](../th/CONTRIBUTING.md) · Türkçe · [Українська](../uk/CONTRIBUTING.md) · [Tiếng Việt](../vi/CONTRIBUTING.md) · [中文](../zh/CONTRIBUTING.md)

---

# Katkıda bulunma

VD Infos'a ilginiz için teşekkürler. Katkılar hoş karşılanır: algılama listeleri, çeviriler, hata
raporları, belgeler ve kod. Çoğu katkı Kotlin gerektirmez.

## En kolay katkı: listeler

Uygulamanın aradığı şeylerin çoğu kod olarak değil, `VDInfos/app/src/main/assets/data/` altında **düz
metin listeleri** olarak bulunur. Bir hedef eklemek veya kaldırmak bir satırı düzenlemek ve bir pull
request açmaktır:

- `*_apps.txt` - satır başına bir paket adı.
- `props.txt` - sistem özelliği kataloğu, `CATEGORY<tab>key`.
- `spoof_keys.txt` - ayarlar matrisi, `key:TYPE`.
- `known_certs.txt` - imzalama sertifikası kataloğu, `SHA-256<tab>owner` (tam hash, gerçek bir APK'den
  `apksigner verify --print-certs` ile alınmış).
- diğer `.txt` dosyaları - satır başına bir girdi (çekirdek modül adı parçaları, dosya adları vb.).

Bir `#` bir yorum başlatır ve boş satırlar yoksayılır. Bazı dosyalar satır başına birkaç boşluk veya
sekme ile ayrılmış alan ya da isteğe bağlı işaretçi paketler; dosyada zaten bulunan satırların biçimini
koruyun.

## Temel kurallar

- **VD Infos gerçeği okur ve asla hiçbir şeyi sahtelemez.** Her metodun döndürdüğünü tam olarak
  bildirir. Bir okumayı daha hoş görünsün diye yumuşatmayın ve bir değeri okumak yerine değiştirecek
  hiçbir şey eklemeyin.
- **Gizlilik mutlaktır.** Her şey cihazda çalışır. Asla bir ağ çağrısı veya bir okumayı cihazdan
  gönderen herhangi bir şey eklemeyin.
- **Dışa aktarımlar maskeli kalır.** Kimlik taşıyan değerler paylaşılan bir raporda maskelenir. Öyle
  tutun.

## Çeviriler

Çeviriler değerli bir katkıdır. Readme dil başına (`README.<lang>.md`), uygulama dizeleri dil başına ve
bu belgeler `docs/<lang>/` altında çevrilir. Terminolojiyi bir dil içinde tutarlı tutun, mevcut tona
uyun ve emin olmadığınız bir teknik terimi asla makineyle çevirmeyin.

## Bir hatayı bildirme

Cihaz modelinizle, Android sürümünüzle ve bir okumayla ilgiliyse paylaşılan bir **dışa aktarımla** (onun
kimlik taşıyan değerleri maskelenmiştir) bir sorun açın. Dışa aktarım tam olarak bunun için yapılmıştır.

## Kod

- Debug değil, release derleyin. Gerçek bir cihazda test edin. Bkz. [Derleme](BUILDING.md).
- Yorumları koddan uzak tutun; adlar niyeti açıklar. Tek başlık, her kaynak dosyasının en üstündeki
  lisans afişidir.
- Mevcut stile uyun ve değişiklikleri odaklı tutun.

## Lisans

Katkıda bulunarak, katkınızın projenin **GNU AGPL-3.0-or-later** lisansı altında lisanslandığını kabul
edersiniz.

<!-- doc-footer -->

---

> _Bu belgeler Claude (Anthropic) ile oluşturulmuş olup VD171 tarafından yürütülmektedir. Düzeltmeler ve iyileştirmeler memnuniyetle karşılanır; bkz. [CONTRIBUTING.md](CONTRIBUTING.md)._
