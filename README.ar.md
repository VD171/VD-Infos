<!--
  VD INFOS: device method debugger: read every device info by every method, then compare.
  Copyright (C) 2026  VD171. SPDX-License-Identifier: AGPL-3.0-or-later
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
  <b>مُنقّح الطرق.</b><br>
  اقرأ كل قيمة في الجهاز عبر كل طريقة قادرة على قراءتها، وصفّها جنبًا إلى جنب لتقارن.
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

أندرويد نظام تشغيل بالغ القوة ومتعدد الاستخدامات؛ ما لا يخبرك به أحد هو أن كل تفاصيلك الشخصية ومعلوماتك السرية متاحة لكل تطبيق تثبّته، وحماية نفسك من هذه الانتهاكات للخصوصية واجب. يعرض لك VD Infos مثالاً لما يمكن التقاطه من جهازك، ويفعل ذلك بوصفه *مُنقّح طرق*: لكل معلومة يقرأ القيمة عبر **كل طريقة قادرة على قراءتها**: `Build.*`، `SystemProperties`، `getprop`، الأصلي `__system_property_get`، مدراء النظام، content provider، الملفات، syscalls، وattestation المفتاح العتادي (TEE)، ويصفّها جنبًا إلى جنب لتقارن. عندما تختلف طريقة عن البقية، فإن شيئًا ما في المنتصف يعيد كتابة ذلك السطح: إطار hook، أو spoofer، أو shim للـ resolver. **لا يتم تخزين أي معلومات أو إرسالها أو نقلها إلى أي ملف¹ أو خادم**: كل شيء يعمل على الجهاز، والقيم الحاملة للهوية مُقنّعة حتى تكشفها، ولا يغادر التقرير الجهاز إلا عندما تشاركه أو تحفظه صراحةً؛ إن أردت، احجب الوصول إلى الإنترنت بجدار حماية أو أطفئه ببساطة.

> **¹ استثناء:** يحفظ كل فحص تقريره الكامل في التخزين الخاص بالتطبيق (`files/last_snapshot.json`). لا يقرؤه إلا التطبيق نفسه أو root، ويكتب الفحص التالي فوقه، ولا يغادر الجهاز أبدًا. وُجد ليتمكّن وكلاء الذكاء الاصطناعي من قراءة النتيجة كاملة؛ انظر **لوكلاء الذكاء الاصطناعي: التقرير الكامل بصيغة JSON**.

## ماذا يفحص

يُقرأ كل عنصر عبر كل طريقة قادرة على قراءته (Java SDK / أصلي / shell)، وتُقارَن جنبًا إلى جنب:

* **نحو 488 خاصية نظام** تُقرأ بخمس طرق: `SystemProperties.get`، ونقطتا دخول bionic (`__system_property_read_callback` و`__system_property_get` بحجم 92 بايت)، و`getprop` المُشغَّل من JVM، و`getprop` المُشغَّل من كود أصلي عبر `popen`؛ أمر shell يشغّله `ProcessBuilder` والأمر نفسه مُشغَّلًا دون JVM ليسا وجهة النظر ذاتها، لأن الأول سطح يعيد إطار إخفاء الروت كتابته والثاني لا.
* **هوية الجهاز**: الطراز، الشركة المصنّعة، العلامة التجارية، device، product، board، hardware، fingerprint، bootloader، build id/tags/type، كل حقل `Build.*` مقابل جميع متغيّرات `ro.product.*` (system/vendor/odm) الخاصة به، أصلي وshell.
* **المعرّفات**: serial (عدة getters)، Android ID (settings وprovider)، GSF ID، advertising ID، IMEI/primary IMEI/MEID/IMSI/ICCID، user serial.
* **الهاتف**: سطح TelephonyManager (المشغّل، SIM، الشبكة، التجوال…)، SubscriptionManager (متعدد SIM)، cell info.
* **الشبكة**: **MAC** لكل واجهة (`NetworkInterface` مقابل `/sys/class/net` مقابل shell)، Wi-Fi (SSID/BSSID/IP/MAC)، البلوتوث، DNS.
* **النواة / العملية / النظام**: `uname`، boot id، uptime، UID/PID، SELinux، المنطقة الزمنية، locale، البيئة، الخدمات/العمليات الجارية.
* **العتاد / الوسائط**: CPU، الذاكرة، المستشعرات، الشاشة، الكاميرات، system features، GPU، معرّف DRM من Widevine.
* **الحزم / الحسابات / WebView**: قائمة الحزم المثبّتة وعددها، بصمات التوقيع لكل تطبيق، توقيع هذا التطبيق، الاستبطان الذاتي لحزمته، تعدادات حل الـ intent، الحسابات، عدد سجل المكالمات، user agent.
* **مطابقة الـ sandbox**: الأسطح التي يُفترض أن **يغلقها** الـ sandbox (CID/serial لـ eMMC، هوية UFS وSoC، `/proc/cmdline`، `/proc/1/*`، `/dev/kmsg`، `/system/build.prop`) و22 خدمة `dumpsys`. هنا الرفض هو الجواب: `EACCES`/`DENIED` هي القراءة المطابِقة، والجهاز الذي يسلّم المحتوى بدلًا من ذلك غير مطابق.
* **معرّفات الإعلانات**: الـ GAID من binder خدمات Play، إضافةً إلى عائلة المفاتيح كاملةً (AAID، OAID/VAID و`pps_*` من هواوي) مُمسوحةً عبر settings store الثلاثة.
* **كواشف الروت / hook / المحاكي**: آثار `su`/Magisk/KernelSU/APatch/Xposed/LSPosed/Riru، المكتبات المحقونة في `/proc/self/maps`، الحزم المعروفة، الخصائص الخطرة وخصائص المحاكي، verified boot state.

## التنزيل والدعم

ينشر كل إصدار ملفَّي APK: **SDK_35** يستهدف الحاوية الحديثة الصارمة وهو الذي يُستخدم؛ **SDK_27** يستهدف مستوى واجهة أقدم، بنطاق SELinux أكثر تساهلًا، ومفيد للمقارنة. كلاهما يُثبَّت على أندرويد 8.0 وأحدث.

* Release (APK): https://github.com/VD171/VD-Infos/releases
* https://github.com/VD171/VD-Infos
* https://xdaforums.com/t/VD-Infos.4097379/
* https://t.me/RootDetected
* https://t.me/BlankAssistance

## لوكلاء الذكاء الاصطناعي: التقرير الكامل بصيغة JSON

مصمَّم لوكلاء الذكاء الاصطناعي وأدوات LLM التي تعمل على الجهاز عبر shell (مساعدو البرمجة، سكربتات التحليل). بدلًا من قراءة لقطات الشاشة، يقرأ الوكيل النتيجة كاملة بأمر واحد:

```sh
su -c cat /data/data/ru.vd171.vdinfos/files/last_snapshot.json
```

* **المحتوى:** كل مسبار مع حكمه وقيمة جميع العدسات، بصيغة JSON نفسها التي يُخرجها زر التصدير. تعرض الشاشة القراءة الأولى فقط للعنصر المطوي؛ أما الملف ففيه كل القراءات.
* **متى يتحدّث:** في نهاية كل فحص، لذا شغّل فحصًا أولًا؛ يحتوي الملف دائمًا على الأحدث.
* **المتطلب:** root، لأن الملف موجود في التخزين الخاص بالتطبيق.

## البنية

```
core/model      نطاق غير قابل للتغيير: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          NativeBridge (JNI), SystemProps (انعكاس), PropCatalog (بيانات)
                SemanticProbes, IntegrityProbes, ProbeRegistry
engine/         ProbeEngine: fan-out بالكوروتين، يبثّ النتائج كـ Flow
data/           SnapshotStore (حفظ دائم + diff), Exporter (مشاركة JSON/نص)
ui/             Jetpack Compose, Material 3, لون ديناميكي، تقدّم مباشر
cpp/            native_probes.cpp: العدسة الأصلية، دون تبعيات
```

* **التوازي**: تتوزع 991 مسبارًا على الـ dispatcher الافتراضي بعدد أذونات محدود؛ وتتدفق النتائج إلى الواجهة فور وصولها.
* **لا شيء في الخلفية**: لا خدمات ولا عمليات مسح مجدولة؛ يعمل التطبيق فقط أثناء فتحه ويغلق نفسه عند تركه خاملاً.
* **الطبقة الأصلية**: ملف `.so` صغير، مربوط بالاسم عبر `RegisterNatives`، أُبقي صغيرًا عمدًا لأنه الجزء الذي يجب أن يصعب خداعه.

## المساهمة

معظم المساهمات لا تحتاج إلى Kotlin: القوائم نصوص عادية ضمن `VDInfos/app/src/main/assets/data/`. أضف سطرًا أو احذفه ثم افتح pull request.

* `*_apps.txt`: اسم حزمة واحد في كل سطر
* `props.txt`: فهرس خصائص النظام، `الفئة<tab>المفتاح`
* `spoof_keys.txt`: مصفوفة spoof الإعدادات، `المفتاح:النوع`
* `known_certs.txt`: فهرس شهادات التوقيع، `SHA-256<tab>المالك` (الهاش كاملًا، مأخوذ من APK حقيقي عبر `apksigner verify --print-certs`)
* ملفات `.txt` الأخرى: أيضًا إدخال واحد لكل سطر (أجزاء أسماء وحدات النواة، الأسماء في `/data/local/tmp`)

العلامة `#` تبدأ تعليقًا؛ والأسطر الفارغة تُتجاهل. بعض الملفات تحتوي على عدة حقول مفصولة بمسافة أو Tab في كل سطر، أو علامات اختيارية (`S` و`@field` في `props.txt`)؛ حافظ على شكل الأسطر الموجودة بالفعل في الملف. مساهمات الكود مرحّب بها أيضًا. بمساهمتك توافق على نشر عملك بموجب AGPL-3.0-or-later الخاص بهذا المشروع.

## الفهرس

مراجع إخفاء الروت وكشفه (أدلة، وحدات، أطر عمل، كواشف) موجودة في فهرس مخصص: [CATALOG.md](CATALOG.md).

## شكر وتقدير

شكر خاص إلى **[frknkrc44](https://github.com/frknkrc44)**، مطوّر **[HMA-OSS](https://github.com/frknkrc44/HMA-OSS)**، على ما بذله من عناية. HMA-OSS هو المكان الذي تُطبَّق فيه عدة إصلاحات يقترحها هذا التطبيق (إخفاء التطبيقات المستهدفة وضبط إعدادات spoof المسبقة)، وكل ذكر له داخل التطبيق يربط بمصدره.

روابطه وقنواته وكيفية دعمه: [HMA-OSS.md](HMA-OSS.md).

## التوثيق

- **[الفهرس](CATALOG.md)**: مراجع إخفاء الروت والكشف (أدلة ووحدات وأطر وكاشفات).
- **[سجل التغييرات](CHANGELOG.md)**: ما الذي تغيّر في كل إصدار.
- **[HMA-OSS](HMA-OSS.md)**: وحدة إخفاء التطبيقات التي تبني عليها إصلاحات التطبيق (روابطها وقنواتها وكيفية دعمها).
- **[المساهمة](README.md#contributing)**: كيفية إضافة أهداف الكشف (معظمها قوائم نصية بسيطة، دون حاجة إلى Kotlin).

<!-- /vd-docs -->

## انظر أيضًا

مشاريع شقيقة للمؤلف نفسه:

- **[VDGoogle](https://github.com/VD171/VD-Google)**: خدمات Google الخاصة بك (RCS، Wallet، attestations الخاصة بـ Play Integrity)، تُقرأ على جهازك دون إنترنت.
- **[VDBackup](https://github.com/VD171/VD-Backup)**: نسخ احتياطي واستعادة كاملان للتطبيقات والبيانات بصلاحيات root، مُدارة بالكامل من WebUI.
- **[COPG-VD](https://github.com/VD171/COPG-VD)**: spoofer للجهاز يحوّل fingerprint جديدًا إلى ملف تعريف متماسك ومتسق.
- **[VD-dropbear](https://github.com/VD171/VD-dropbear)**: خادم SSH مستقل يعمل بصلاحيات الروت (Dropbear).

Pixel `build.prop`: **[@VictorGabriel7Dev/build.prop](https://github.com/VictorGabriel7Dev/build.prop)** (fork من @T3SL4).

## جهات الاتصال

- https://vd171.ru
- https://vd.priv8.ru
- *Telegram:* @VD_Priv8 https://t.me/VD_Priv8
- *Channel:* @Priv8_VD https://t.me/Priv8_VD
- *Discord:* @VD.Priv8 https://discord.com/users/1296831918989639721
- *E-mail:* vd.priv8@pm.me
- *XDA-Developers:* @VD171 https://xdaforums.com/m/vd171.4699873/
- *XDA thread:* https://xdaforums.com/t/VD-Infos.4097379/
- *GitHub:* @VD171 https://github.com/VD171

## الترخيص

**GNU AGPL-3.0-or-later**: انظر [LICENSE](LICENSE). حقوق متروكة (Copyleft)، بما في ذلك بند الشبكة: أي شخص يشغّل نسخة معدّلة (حتى كخدمة) يجب أن يوفّر مصدرها. اختير عمدًا لأداة بحثية/مضادة للكشف، للإبقاء على الـ forks مفتوحة.

## العلامات التجارية

إن VD Infos أداة بحث وتشخيص مستقلة وغير رسمية. لا ترتبط بأي من الموردين الذين تقرأ برامجهم أو خدماتهم أو علاماتهم أو تكشفها أو تسمّيها، ولا تحظى بتأييدهم أو رعايتهم (بما في ذلك Google LLC ومشروع Android Open Source). إن "Android" و"Google" و"Google Play" و"Play Integrity" و"Widevine" وجميع أسماء المنتجات والشعارات والعلامات التجارية الأخرى ملك لأصحابها المعنيين. يقرأ VD Infos فقط ما هو موجود بالفعل على جهازك ولا يرسل شيئًا إلى أي مكان.

## Tags

`android` `privacy` `security` `root-detection` `hook-detection` `spoof-detection` `anti-detection` `tamper-detection` `device-fingerprint` `magisk` `kernelsu` `lsposed` `play-integrity` `attestation` `tee` `keystore` `scanner` `diagnostics` `reverse-engineering` `kotlin`

<!-- doc-footer -->

---

> _تم إنشاء هذا التوثيق باستخدام Claude (Anthropic) ويتولى صيانته VD171. التصحيحات والتحسينات مرحّب بها؛ راجع [Contributing](README.md#contributing)._
