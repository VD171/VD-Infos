> **Languages:** [English](../ARCHITECTURE.md) · [العربية](../ar/ARCHITECTURE.md) · [Čeština](../cs/ARCHITECTURE.md) · [Deutsch](../de/ARCHITECTURE.md) · [Español](../es/ARCHITECTURE.md) · [فارسی](../fa/ARCHITECTURE.md) · [Français](../fr/ARCHITECTURE.md) · [हिन्दी](../hi/ARCHITECTURE.md) · [Bahasa Indonesia](../id/ARCHITECTURE.md) · [Italiano](../it/ARCHITECTURE.md) · [日本語](../ja/ARCHITECTURE.md) · [한국어](../ko/ARCHITECTURE.md) · [Nederlands](../nl/ARCHITECTURE.md) · [Polski](../pl/ARCHITECTURE.md) · [Português](../pt/ARCHITECTURE.md) · [Русский](../ru/ARCHITECTURE.md) · [Svenska](../sv/ARCHITECTURE.md) · [ไทย](../th/ARCHITECTURE.md) · Türkçe · [Українська](../uk/ARCHITECTURE.md) · [Tiếng Việt](../vi/ARCHITECTURE.md) · [中文](../zh/ARCHITECTURE.md)

---

# Mimari

Bu, VD Infos'un nasıl bir araya getirildiğine dair üst düzey bir haritadır. Uygulamanın biçimini
tanımlar, neyi okuduğunun kataloğunu değil: bir metot hata ayıklayıcısının amacı her okumayı önünüze
koymaktır ve onları okumak, kendi cihazınızda yapmanız içindir.

## Tasarım hedefleri

- **Bir değer, her metot.** Her bilgi parçası için, onu okuyabilen her metot aracılığıyla okuyun ve
  okumaları dizin. Bir hata ayıklayıcı kanıt yüzeye çıkarır; hüküm vermez.
- **Cihazda ve sessiz.** Ağ yok, hesap yok, arka plan işi yok. Uygulama yalnızca açıkken çalışır.
- **Makineler tarafından okunabilir.** Tüm sonuç makine tarafından okunabilir tek bir belgedir,
  böylece bir araç bir kişinin gördüğü resmin aynısını elde eder. Bkz. [Yapay zeka ajanları](AI-AGENTS.md).
- **Önemli olan yerde küçük.** Yerel bileşen bilinçli olarak minik ve basit tutulur.

## Mercekler

*Mercek*, bir değeri okumanın bir yoludur. Aynı değer, karşılaştırılabilmeleri için birkaç mercekten
okunur:

| Mercek | Ne olduğu |
|---|---|
| **Java / SDK** | Sıradan bir uygulamanın çağırdığı platform API'si. |
| **Native / JNI** | Aynı değerin yerel koddan okunması. |
| **Shell via JVM** | JVM tarafından oluşturulan bir kabuk komutu. |
| **Shell via JNI** | Aynı komutun yerel koddan oluşturulması. |
| **TEE / Attestation** | Uygulandığı yerde, donanım destekli anahtar doğrulaması. |

Mercekleri dizmek bütün fikirdir: uygulama, okumaların nerede hemfikir olduğunu ve nerede ayrıştığını
gösterir. Bir farkın belirli bir değer için, belirli bir cihazda ne anlama geldiği, okumanız içindir.

## Bir tarama nasıl çalışır

```
core/model      immutable domain: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          the probes and the bridges they use (JNI, reflection, data lists)
engine/         ProbeEngine: parallel fan-out, streams results as they land
data/           SnapshotStore (persist the report), Exporter (share as JSON / text)
ui/             Jetpack Compose, Material 3, dynamic colour, live progress
cpp/            native_probes.cpp: the native lens, dependency-free
```

- **Paralellik.** Problar, sınırlı sayıda izne sahip bir dağıtıcı üzerinde yelpaze gibi açılır;
  sonuçlar tamamlandıkça, canlı bir ilerleme göstergesiyle arayüze akar.
- **Arka planda hiçbir şey yok.** Hizmet yok ve zamanlanmış tarama yok. Uygulama yalnızca açıkken okur
  ve boş bırakıldığında kendini kapatır.
- **Yerel katman.** Adıyla `RegisterNatives` aracılığıyla bağlanan, bağımlılığı olmayan küçük bir `.so`,
  bilinçli olarak minik ve basit tutulur.

## Rapor

Her tarama bir `Snapshot` üretir: alındığı zaman, uygulama sürümü ve prob başına bir tane olmak üzere
bir sonuç listesi; her biri her mercekten gelen okumalarla ve bunların nasıl ilişkili olduğuna dair bir
hükümle birlikte. Uygulama bunu her taramadan sonra özel depolamaya kalıcı hale getirir ve aynı veriyi
dışa aktarabilir. Yapı [dışa aktarma biçimi](EXPORT-FORMAT.md) olarak belgelenmiştir.

## Onu genişletme

Uygulamanın aradığı şeylerin çoğu kod olarak değil, `assets/data/` altında **düz metin listeleri**
olarak bulunur. Bir hedef eklemek veya kaldırmak bir satırı düzenlemektir. Bkz. [Katkıda bulunma](CONTRIBUTING.md).

## VD Infos'un asla yapmadıkları

- Sisteminizde asla bir şey değiştirmez. Yalnızca okur ve karşılaştırır.
- Ağ ile asla konuşmaz. Tamamen çevrimdışı çalışır.
- Asla arka planda çalışmaz, bir tarama zamanlamaz veya bir hizmeti canlı tutmaz.
- Bir okumayı asla hiçbir yere göndermez. Bir rapor cihazdan yalnızca onu paylaştığınızda veya
  kaydettiğinizde ayrılır, kimlik taşıyan değerler maskelenmiş olarak.

<!-- doc-footer -->

---

> _Bu belgeler Claude (Anthropic) ile oluşturulmuş olup VD171 tarafından yürütülmektedir. Düzeltmeler ve iyileştirmeler memnuniyetle karşılanır; bkz. [CONTRIBUTING.md](CONTRIBUTING.md)._
