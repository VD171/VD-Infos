> **Languages:** [English](../EXPORT-FORMAT.md) · [العربية](../ar/EXPORT-FORMAT.md) · [Čeština](../cs/EXPORT-FORMAT.md) · [Deutsch](../de/EXPORT-FORMAT.md) · [Español](../es/EXPORT-FORMAT.md) · [فارسی](../fa/EXPORT-FORMAT.md) · [Français](../fr/EXPORT-FORMAT.md) · [हिन्दी](../hi/EXPORT-FORMAT.md) · [Bahasa Indonesia](../id/EXPORT-FORMAT.md) · [Italiano](../it/EXPORT-FORMAT.md) · [日本語](../ja/EXPORT-FORMAT.md) · [한국어](../ko/EXPORT-FORMAT.md) · [Nederlands](../nl/EXPORT-FORMAT.md) · [Polski](../pl/EXPORT-FORMAT.md) · [Português](../pt/EXPORT-FORMAT.md) · [Русский](../ru/EXPORT-FORMAT.md) · [Svenska](../sv/EXPORT-FORMAT.md) · [ไทย](../th/EXPORT-FORMAT.md) · Türkçe · [Українська](../uk/EXPORT-FORMAT.md) · [Tiếng Việt](../vi/EXPORT-FORMAT.md) · [中文](../zh/EXPORT-FORMAT.md)

---

# Dışa aktarma biçimi

VD Infos'un dışa aktardığı rapor - ve her taramadan sonra yazdığı `last_snapshot.json` - kararlı,
belgelenmiş bir biçime sahip tek bir JSON nesnesidir. Bu sayfa şemadır. Bir aracın ayrıştırabilmesi
için yapıyı tanımlar; tek tek probları listelemez veya sizin için herhangi bir okumayı yorumlamaz.

## Üst düzey: anlık görüntü

```json
{
  "takenAt": 1730000000000,
  "appVersion": "2.23",
  "results": [ /* ProbeResult objects */ ]
}
```

| Alan | Tür | Anlam |
|---|---|---|
| `takenAt` | number | Taramanın ne zaman alındığı, milisaniye cinsinden Unix epoch. |
| `appVersion` | string | Raporu üreten uygulama sürümü. |
| `results` | array | Prob başına bir `ProbeResult`, uygulamanın onları tuttuğu sırayla. |

## Bir sonuç

`results` içindeki her girdi, hükmü ve her mercek okumasıyla birlikte bir probdur.

```json
{
  "spec": { /* ProbeSpec */ },
  "values": [ /* LensValue objects */ ],
  "verdict": "MATCH",
  "detail": null
}
```

| Alan | Tür | Anlam |
|---|---|---|
| `spec` | object | Neyin problandığı (aşağıya bakın). |
| `values` | array | Onu okuyan metot başına bir `LensValue`. |
| `verdict` | string | Okumaların nasıl ilişkili olduğu (bkz. [Hüküm](#hüküm)). |
| `detail` | string veya null | Sonuç hakkında isteğe bağlı bir not. |

### spec (ProbeSpec)

```json
{
  "id": "identity:model",
  "title": "Model",
  "category": "IDENTITY",
  "lenses": ["JAVA", "NATIVE", "SHELL"],
  "note": null,
  "sensitive": false,
  "solution": null
}
```

| Alan | Tür | Anlam |
|---|---|---|
| `id` | string | Probun kararlı tanımlayıcısı. |
| `title` | string | İnsan tarafından okunabilir ad. |
| `category` | string | [Kategorilerden](#kategori) biri. |
| `lenses` | array | Bu probun kullandığı mercekler. |
| `note` | string veya null | İsteğe bağlı bağlam. |
| `sensitive` | boolean | Değer kimlik taşıdığında `true`; bu tür değerler **dışa aktarılan raporda maskelenir** (bkz. [Maskeleme](#maskeleme)). |
| `solution` | string veya null | İsteğe bağlı bir ipucu alanı; bulunmayabilir veya null olabilir. |

### values (LensValue)

```json
{
  "lens": "JAVA",
  "source": "Build.MODEL",
  "value": "Pixel 9",
  "error": null,
  "elapsedMicros": 42,
  "compare": true,
  "detail": null
}
```

| Alan | Tür | Anlam |
|---|---|---|
| `lens` | string | Onu hangi metodun okuduğu (bkz. [Mercek](#mercek)). |
| `source` | string | Bu okumanın arkasındaki tam çağrı veya yol. |
| `value` | string veya null | O metodun döndürdüğü; hiçbir şey okumadıysa `null`. |
| `error` | string veya null | Metot başarısız olduğunda bulunur. |
| `elapsedMicros` | number | Okumanın ne kadar sürdüğü, mikrosaniye cinsinden. |
| `compare` | boolean | Bu okuma hükme katılıyorsa `true`; yalnızca bilgi için gösteriliyorsa `false`. |
| `detail` | string veya null | Bu okuma hakkında isteğe bağlı bir not. |

## Numaralandırmalar

### Hüküm

| Değer | Anlam |
|---|---|
| `MATCH` | Karşılaştırılabilir her mercek hemfikirdi. |
| `MISMATCH` | Karşılaştırılabilir mercekler hemfikir değildi. |
| `SINGLE` | Yalnızca bir mercek okuyabildi. |
| `INFO` | Referans için okundu, oylanmadı. |
| `EMPTY` | Hiçbir mercek bir değer okumadı. |
| `ERROR` | Değer yok ve en az bir mercek hata verdi. |

### Mercek

| Değer | Etiket |
|---|---|
| `JAVA` | Java / SDK |
| `NATIVE` | Native / JNI |
| `SHELL` | Shell via JVM |
| `SHELL_NATIVE` | Shell via JNI |
| `ATTEST` | TEE / Attestation |

### Kategori

`IDENTITY`, `FINGERPRINT`, `TELEPHONY`, `NETWORK`, `LOCALE`, `BOOT`, `BUILD`,
`HARDWARE`, `DISPLAY`, `SENSORS`, `MEDIA`, `STORAGE`, `PROCESS`, `PACKAGES`,
`ACCOUNTS`, `WEBVIEW`, `SECURITY`, `INTEGRITY`, `EMULATOR`, `SYSTEM`.

## Maskeleme

`spec.sensitive` değeri `true` olduğunda, probun okumaları kimlik taşıyan bir değer taşır (bir seri
numarası, IMEI, MAC adresi, reklam kimliği ve benzeri). **Paylaşılan veya dışa aktarılan** raporda o
değerler ve ayrıntıları, herkese açık bir yere yapıştırılan bir raporun kişisel tanımlayıcıları
sızdırmaması için bir redaksiyon işaretçisiyle değiştirilir; prob, hükmü ve yapı korunur. Yalnızca
uygulama veya root tarafından okunabilen cihazdaki `last_snapshot.json`, gerçek değerleri saklar.

## Onu okuma

Minimal bir ayrıştırma: `results` öğesini yineleyin, her `verdict` değerini okuyun ve tam bir resim
için `values` öğelerini `lens` değerlerine göre dizin. Yukarıdaki biçim sürümler arasında kararlıdır;
ekran konumlarına göre değil, bu alan adlarına göre ayrıştırın.

Dosyayı cihazda nasıl elde edeceğiniz için [Yapay zeka ajanları](AI-AGENTS.md) sayfasına bakın.

<!-- doc-footer -->

---

> _Bu belgeler Claude (Anthropic) ile oluşturulmuş olup VD171 tarafından yürütülmektedir. Düzeltmeler ve iyileştirmeler memnuniyetle karşılanır; bkz. [CONTRIBUTING.md](CONTRIBUTING.md)._
