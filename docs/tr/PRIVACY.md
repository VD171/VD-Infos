> **Languages:** [English](../PRIVACY.md) · [العربية](../ar/PRIVACY.md) · [Čeština](../cs/PRIVACY.md) · [Deutsch](../de/PRIVACY.md) · [Español](../es/PRIVACY.md) · [فارسی](../fa/PRIVACY.md) · [Français](../fr/PRIVACY.md) · [हिन्दी](../hi/PRIVACY.md) · [Bahasa Indonesia](../id/PRIVACY.md) · [Italiano](../it/PRIVACY.md) · [日本語](../ja/PRIVACY.md) · [한국어](../ko/PRIVACY.md) · [Nederlands](../nl/PRIVACY.md) · [Polski](../pl/PRIVACY.md) · [Português](../pt/PRIVACY.md) · [Русский](../ru/PRIVACY.md) · [Svenska](../sv/PRIVACY.md) · [ไทย](../th/PRIVACY.md) · Türkçe · [Українська](../uk/PRIVACY.md) · [Tiếng Việt](../vi/PRIVACY.md) · [中文](../zh/PRIVACY.md)

---

# Gizlilik

Gizlilik VD Infos'un bir özelliği değildir. Bütün amaç odur. Bu sayfa, uygulamanın verilerinizle neyi
yapıp neyi yapmadığını, doğrulayabileceğiniz açık terimlerle tam olarak belirtir.

## Söz

- **Her şey cihazda çalışır.** Hiçbir okuma herhangi bir sunucuya gönderilmez, yüklenmez veya
  iletilmez. Uygulama internet engellenmişken tamamen çalışır.
- **Hesap yok, oturum açma yok, arka plan işi yok.** Uygulama yalnızca açıkken okur, arka planda hiçbir
  şeyi çalışır durumda tutmaz ve boş bırakıldığında kendini kapatır.
- **Sisteminizde hiçbir şey değişmez.** VD Infos yalnızca okur ve karşılaştırır; cihazınızın durumuna
  asla yazmaz.

## Rapor sizde kalır

Bir rapor cihazdan **yalnızca onu açıkça paylaştığınızda veya kaydettiğinizde** ayrılır. O zamana
kadar, ikisi de cihazda olmak üzere, iki yerde bulunur:

- **Ekranda**, uygulama açıkken.
- Uygulamanın **özel depolamasında** (`files/last_snapshot.json`), ki bunu yalnızca uygulamanın
  kendisi veya root okuyabilir. Bir sonraki tarama onun üzerine yazar ve o asla cihazdan ayrılmaz.
  Araçların ve yapay zeka ajanlarının tam sonucu okuyabilmesi için vardır; bkz.
  [Yapay zeka ajanları kılavuzu](AI-AGENTS.md).

## Dışa aktarımlar maskelenir

Bir raporu paylaştığınızda veya kaydettiğinizde, kimlik taşıyan değerler - seri numaraları, IMEI, MAC
adresleri, reklam kimlikleri ve benzeri - rapor yazılmadan önce **maskelenir**, böylece herkese açık
bir konuya yapıştırdığınız bir rapor kendi tanımlayıcılarınızı sızdırmaz. Hükümler ve yapı korunur;
yalnızca hassas değerler redakte edilir. Cihazdaki özel kopya, kendi kullanımınız için gerçek değerleri
saklar.

## İzinler

Uygulama hiçbir izin gerekmeden başlar. Bazı okumalar yalnızca eşleşen izni verdiğinizde kullanılabilir
hale gelir (örneğin kullanım erişimi) ve onu vermek tamamen sizin tercihinizdir; bir okumanın
kullanılamadığı yerde, uygulama yalnızca öyle olduğunu söyler.

## Kendiniz doğrulayın

Bunların hiçbirini güvene dayanarak kabul etmek zorunda değilsiniz:

- Kaynak herkese açıktır: <https://github.com/VD171/VD-Infos>.
- Manifest kaynaktadır; hangi izinleri beyan ettiğini onaylayın.
- Uygulamanın ağ erişimini bir güvenlik duvarıyla engelleyin veya interneti kapatın, o yine de aynı
  şekilde çalışmaya devam eder.

Bir raporu nasıl okuyacağınız ve dışa aktarımın neyi içerdiği için [kullanıcı kılavuzu](USER-GUIDE.md)
sayfasına bakın.

<!-- doc-footer -->

---

> _Bu belgeler Claude (Anthropic) ile oluşturulmuş olup VD171 tarafından yürütülmektedir. Düzeltmeler ve iyileştirmeler memnuniyetle karşılanır; bkz. [CONTRIBUTING.md](CONTRIBUTING.md)._
