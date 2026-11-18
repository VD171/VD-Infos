> **Languages:** [English](../SECURITY.md) · [العربية](../ar/SECURITY.md) · [Čeština](../cs/SECURITY.md) · [Deutsch](../de/SECURITY.md) · [Español](../es/SECURITY.md) · [فارسی](../fa/SECURITY.md) · [Français](../fr/SECURITY.md) · [हिन्दी](../hi/SECURITY.md) · [Bahasa Indonesia](../id/SECURITY.md) · [Italiano](../it/SECURITY.md) · [日本語](../ja/SECURITY.md) · [한국어](../ko/SECURITY.md) · [Nederlands](../nl/SECURITY.md) · [Polski](../pl/SECURITY.md) · [Português](../pt/SECURITY.md) · [Русский](../ru/SECURITY.md) · [Svenska](../sv/SECURITY.md) · [ไทย](../th/SECURITY.md) · Türkçe · [Українська](../uk/SECURITY.md) · [Tiếng Việt](../vi/SECURITY.md) · [中文](../zh/SECURITY.md)

---

# Güvenlik politikası

VD Infos, hassas cihaz durumunu okuyan bir gizlilik aracıdır, bu yüzden güvenlik raporları ciddiye
alınır.

## Bir güvenlik açığını bildirme

Lütfen güvenlik sorunlarını herkese açık bir sorun olarak değil, **özel olarak** bildirin:

- **vd.priv8@pm.me** adresine e-posta gönderin.
- Sorunu, etkilenen sürümü ve nasıl yeniden üretileceğini açıklayın.
- Veri ifşasıyla ilgiliyse, sorunun sınıfını açıklayın; çalışan bir istismar eklemeniz gerekmez.

Mümkün olan en kısa sürede bir yanıt alacaksınız. Lütfen herhangi bir kamuya açıklamadan önce sorunu
düzeltmek için makul bir süre tanıyın.

## Neyin güvenlik sorunu sayıldığı

VD Infos'un vaat ettiği şeyler nedeniyle, bunlar özellikle önemlidir:

- Uygulamanın ağa ulaşmasını veya herhangi bir okumayı cihazdan göndermesini sağlayacak herhangi bir
  şey.
- Maskelemeye rağmen, kimlik taşıyan bir değeri paylaşılan veya kaydedilmiş bir **dışa aktarıma** (bir
  seri numarası, IMEI, MAC adresi, reklam kimliği ve benzeri) sızdıracak herhangi bir şey.
- Yalnızca uygulamanın kendisi veya root tarafından okunabilir olması gereken özel rapor dosyasını başka
  bir uygulamanın okumasına izin veren herhangi bir şey.

## Neyin güvenlik sorunu olmadığı

- "Yanlış" görünen ama bir metodun döndürdüğü dürüst değer olan bir okuma. VD Infos her metodun
  söylediğini bildirir; karşılaştırmayı okumak sizin yapmanız içindir. Bkz. [kullanıcı kılavuzu](USER-GUIDE.md).
- Kullanılamaz olarak işaretlenmiş bir okuma. Bu, bir metodun o değeri cihazınızda okuyamadığı anlamına
  gelir, ki bu da karşılaştırmanın bir parçasıdır.

## Desteklenen sürümler

Yalnızca en son yayınlanan sürüm desteklenir. Lütfen bildirmeden önce en son derlemede yeniden üretin.

<!-- doc-footer -->

---

> _Bu belgeler Claude (Anthropic) ile oluşturulmuş olup VD171 tarafından yürütülmektedir. Düzeltmeler ve iyileştirmeler memnuniyetle karşılanır; bkz. [CONTRIBUTING.md](CONTRIBUTING.md)._
