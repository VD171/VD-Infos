[English](README.md) | [Português](README.pt.md) | [Español](README.es.md) | [Italiano](README.it.md) | [Deutsch](README.de.md) | [Français](README.fr.md) | [Русский](README.ru.md) | [Bahasa Indonesia](README.id.md) | [Türkçe](README.tr.md) | [Polski](README.pl.md) | [Nederlands](README.nl.md) | [Svenska](README.sv.md) | [Čeština](README.cs.md) | [Tiếng Việt](README.vi.md) | [中文](README.zh.md) | [日本語](README.ja.md) | [한국어](README.ko.md) | [فارسی](README.fa.md) | [हिन्दी](README.hi.md) | **العربية** | [ไทย](README.th.md)

# VD Infos

*مُنقّح الطرق*

[![License: AGPL-3.0-or-later](https://img.shields.io/badge/license-AGPL--3.0--or--later-blue.svg)](LICENSE)
[![Release](https://img.shields.io/github/v/release/VD171/VD-Infos)](https://github.com/VD171/VD-Infos/releases)
[![Downloads](https://img.shields.io/github/downloads/VD171/VD-Infos/total)](https://github.com/VD171/VD-Infos/releases)
[![Android 8.0+](https://img.shields.io/badge/Android-8.0%2B-3DDC84.svg)](https://github.com/VD171/VD-Infos/releases)
[![Languages](https://img.shields.io/badge/languages-21-orange.svg)](README.md)

<img src="images/vdinfos-01.png" height="420"/> <img src="images/vdinfos-02.png" height="420"/>

أندرويد نظام تشغيل بالغ القوة ومتعدد الاستخدامات؛ ما لا يخبرك به أحد هو أن كل تفاصيلك الشخصية ومعلوماتك السرية متاحة لكل تطبيق تثبّته، وحماية نفسك من هذه الانتهاكات للخصوصية واجب. يعرض لك VD Infos مثالاً لما يمكن التقاطه من جهازك، ويفعل ذلك بوصفه *مُنقّح طرق*: لكل معلومة يقرأ القيمة عبر **كل طريقة قادرة على قراءتها** - `Build.*`، `SystemProperties`، `getprop`، الأصلي `__system_property_get`، مدراء النظام، content provider، الملفات، syscalls، وattestation المفتاح العتادي (TEE) - ويصفّها جنبًا إلى جنب لتقارن. عندما تختلف طريقة عن البقية، فإن شيئًا ما في المنتصف يعيد كتابة ذلك السطح: إطار hook، أو spoofer، أو shim للـ resolver. **لا يتم تخزين أي معلومات أو إرسالها أو نقلها إلى أي ملف أو خادم** - كل شيء يعمل على الجهاز، والقيم الحاملة للهوية مُقنّعة حتى تكشفها، ولا يغادر التقرير الجهاز إلا عندما تشاركه أو تحفظه صراحةً؛ إن أردت، احجب الوصول إلى الإنترنت بجدار حماية أو أطفئه ببساطة.

## ماذا يفحص

يُقرأ كل عنصر عبر كل طريقة قادرة على قراءته (Java SDK / أصلي / shell)، وتُقارَن جنبًا إلى جنب:

* **نحو 488 خاصية نظام** تُقرأ بخمس طرق: `SystemProperties.get`، ونقطتا دخول bionic (`__system_property_read_callback` و`__system_property_get` بحجم 92 بايت)، و`getprop` المُشغَّل من JVM، و`getprop` المُشغَّل من كود أصلي عبر `popen` - أمر shell يشغّله `ProcessBuilder` والأمر نفسه مُشغَّلًا دون JVM ليسا وجهة النظر ذاتها، لأن الأول سطح يعيد إطار إخفاء الروت كتابته والثاني لا.
* **هوية الجهاز**: الطراز، الشركة المصنّعة، العلامة التجارية، device، product، board، hardware، fingerprint، bootloader، build id/tags/type - كل حقل `Build.*` مقابل جميع متغيّرات `ro.product.*` (system/vendor/odm) الخاصة به، أصلي وshell.
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

## اللغات

21 لغة للواجهة: الإنجليزية، البرتغالية، الإسبانية، الإيطالية، الألمانية، الفرنسية، الروسية، الإندونيسية، التركية، البولندية، الهولندية، السويدية، التشيكية، الفيتنامية، الصينية، اليابانية، الكورية، الفارسية، الهندية، العربية والتايلاندية. يعرض التطبيق اختيارًا للغة لمرة واحدة عند أول تشغيل (مع خيار "افتراضي النظام") ويتبع لغة النظام فيما عدا ذلك.

## البنية

```
core/model      نطاق غير قابل للتغيير: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          NativeBridge (JNI) - SystemProps (انعكاس) - PropCatalog (بيانات)
                SemanticProbes - IntegrityProbes - ProbeRegistry
engine/         ProbeEngine - fan-out بالكوروتين، يبثّ النتائج كـ Flow
data/           SnapshotStore (حفظ دائم + diff) - Exporter (مشاركة JSON/نص)
ui/             Jetpack Compose, Material 3, لون ديناميكي، تقدّم مباشر
cpp/            native_probes.cpp - العدسة الأصلية، دون تبعيات
```

* **التوازي**: تتوزع 885 مسبارًا على الـ dispatcher الافتراضي بعدد أذونات محدود؛ وتتدفق النتائج إلى الواجهة فور وصولها.
* **لا شيء في الخلفية**: لا خدمات ولا عمليات مسح مجدولة؛ يعمل التطبيق فقط أثناء فتحه ويغلق نفسه عند تركه خاملاً.
* **الطبقة الأصلية**: ملف `.so` صغير، مربوط بالاسم عبر `RegisterNatives`، أُبقي صغيرًا عمدًا لأنه الجزء الذي يجب أن يصعب خداعه.

## المساهمة

معظم المساهمات لا تحتاج إلى Kotlin: القوائم نصوص عادية ضمن `VDInfos/app/src/main/assets/data/`. أضف سطرًا أو احذفه ثم افتح pull request.

* `*_apps.txt` - اسم حزمة واحد في كل سطر
* `props.txt` - فهرس خصائص النظام، `الفئة<tab>المفتاح`
* `spoof_keys.txt` - مصفوفة spoof الإعدادات، `المفتاح:النوع`
* ملفات `.txt` الأخرى - أيضًا إدخال واحد لكل سطر (أجزاء أسماء وحدات النواة، الأسماء في `/data/local/tmp`)

العلامة `#` تبدأ تعليقًا؛ والأسطر الفارغة تُتجاهل. مساهمات الكود مرحّب بها أيضًا. بمساهمتك توافق على نشر عملك بموجب AGPL-3.0-or-later الخاص بهذا المشروع.

## الفهرس

مراجع إخفاء الروت وكشفه (أدلة، وحدات، أطر عمل، كواشف) موجودة في فهرس مخصص: [CATALOG.md](CATALOG.md).

## شكر وتقدير

شكر خاص إلى **[frknkrc44](https://github.com/frknkrc44)**، مطوّر **[HMA-OSS](https://github.com/frknkrc44/HMA-OSS)**، على ما بذله من عناية. HMA-OSS هو المكان الذي تُطبَّق فيه عدة إصلاحات يقترحها هذا التطبيق - إخفاء التطبيقات المستهدفة وضبط إعدادات spoof المسبقة - وكل ذكر له داخل التطبيق يربط بمصدره.

روابطه وقنواته وكيفية دعمه: [HMA-OSS.md](HMA-OSS.md).

## جهات الاتصال

* https://vd171.ru
* https://vd.priv8.ru
* **Telegram:** @VD_Priv8 https://t.me/VD_Priv8
* **Discord:** @VD.Priv8 https://discord.com/users/1296831918989639721
* **البريد الإلكتروني:** vd.priv8@pm.me
* **XDA-Developers:** @VD171 https://xdaforums.com/m/vd171.4699873/
* **GitHub:** @VD171 https://github.com/VD171

## الترخيص

**GNU AGPL-3.0-or-later** - انظر [LICENSE](LICENSE). حقوق متروكة (Copyleft)، بما في ذلك بند الشبكة: أي شخص يشغّل نسخة معدّلة (حتى كخدمة) يجب أن يوفّر مصدرها. اختير عمدًا لأداة بحثية/مضادة للكشف، للإبقاء على الـ forks مفتوحة.

---

`android` `privacy` `security` `root-detection` `hook-detection` `spoof-detection` `anti-detection` `tamper-detection` `device-fingerprint` `magisk` `kernelsu` `lsposed` `play-integrity` `attestation` `tee` `keystore` `scanner` `diagnostics` `reverse-engineering` `kotlin`
