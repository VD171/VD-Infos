[English](README.md) | [Português](README.pt.md) | [Español](README.es.md) | [Italiano](README.it.md) | [Deutsch](README.de.md) | [Français](README.fr.md) | [Русский](README.ru.md) | [Bahasa Indonesia](README.id.md) | [Türkçe](README.tr.md) | [Polski](README.pl.md) | [Nederlands](README.nl.md) | [Svenska](README.sv.md) | [Čeština](README.cs.md) | [Tiếng Việt](README.vi.md) | [中文](README.zh.md) | [日本語](README.ja.md) | [한국어](README.ko.md) | **فارسی** | [हिन्दी](README.hi.md) | [العربية](README.ar.md) | [ไทย](README.th.md)

# VD Infos

*اشکال‌زدای روش‌ها*

[![License: AGPL-3.0-or-later](https://img.shields.io/badge/license-AGPL--3.0--or--later-blue.svg)](LICENSE)
[![Release](https://img.shields.io/github/v/release/VD171/VD-Infos)](https://github.com/VD171/VD-Infos/releases)
[![Downloads](https://img.shields.io/github/downloads/VD171/VD-Infos/total)](https://github.com/VD171/VD-Infos/releases)
[![Android 8.0+](https://img.shields.io/badge/Android-8.0%2B-3DDC84.svg)](https://github.com/VD171/VD-Infos/releases)
[![Languages](https://img.shields.io/badge/languages-21-orange.svg)](README.md)

<img src="images/vdinfos-01.png" height="420"/> <img src="images/vdinfos-02.png" height="420"/>

اندروید سیستم‌عاملی بی‌نهایت قدرتمند و همه‌کاره است؛ چیزی که هیچ‌کس به شما نمی‌گوید این است که همه جزئیات شخصی و اطلاعات محرمانه شما در دسترس هر برنامه‌ای است که نصب می‌کنید، و محافظت از خود در برابر این تجاوزها به حریم خصوصی یک وظیفه است. VD Infos نمونه‌ای از آنچه می‌توان از دستگاه شما استخراج کرد را نشان می‌دهد و این کار را همچون یک *اشکال‌زدای روش‌ها* انجام می‌دهد: برای هر اطلاعات، مقدار را با **هر روشی که قادر به خواندن آن است** می‌خواند - `Build.*`، `SystemProperties`، `getprop`، `__system_property_get` بومی، مدیران سیستم، content provider‌ها، فایل‌ها، syscall‌ها و attestation کلید سخت‌افزاری (TEE) - و آن‌ها را کنار هم می‌چیند تا مقایسه کنید. وقتی یک روش با بقیه اختلاف دارد، چیزی در میانه در حال بازنویسی آن سطح است: یک چارچوب hook، یک spoofer، یک shim از resolver. **هیچ اطلاعاتی ذخیره، ارسال یا به هیچ فایل یا سروری منتقل نمی‌شود** - همه چیز روی دستگاه اجرا می‌شود، مقادیرِ حاملِ هویت تا زمانی که آشکارشان کنید پوشانده می‌مانند، و گزارش تنها زمانی دستگاه را ترک می‌کند که خودتان صریحاً آن را به اشتراک بگذارید یا ذخیره کنید؛ اگر خواستید، دسترسی اینترنت را با فایروال مسدود کنید یا صرفاً آن را خاموش کنید.

## چه چیزی را بازرسی می‌کند

هر مورد با هر روشی که قادر به خواندن آن است خوانده می‌شود (Java SDK / بومی / shell) و کنار هم مقایسه می‌شود:

* **حدود ۴۸۸ ویژگی سیستم** به پنج روش خوانده می‌شوند: `SystemProperties.get`، هر دو نقطه ورود bionic (`__system_property_read_callback` و `__system_property_get` ۹۲ بایتی)، `getprop` اجراشده توسط JVM، و `getprop` اجراشده از کد بومی از طریق `popen` - یک فرمان shell که `ProcessBuilder` اجرا می‌کند و همان فرمان که بدون JVM اجرا شود یک زاویه دید نیستند، چون اولی سطحی است که چارچوب پنهان‌سازی root آن را بازنویسی می‌کند و دومی نه.
* **هویت دستگاه**: مدل، سازنده، برند، device، product، board، hardware، fingerprint، bootloader، build id/tags/type - هر فیلد `Build.*` در برابر همه گونه‌های `ro.product.*` آن (system/vendor/odm)، بومی و shell.
* **شناسه‌ها**: serial (گترهای متعدد)، Android ID (settings و provider)، GSF ID، advertising ID، IMEI/primary IMEI/MEID/IMSI/ICCID، user serial.
* **تلفن**: سطح TelephonyManager (اپراتور، SIM، شبکه، رومینگ…)، SubscriptionManager (چند SIM)، cell info.
* **شبکه**: **MAC** به‌ازای هر رابط (`NetworkInterface` در برابر `/sys/class/net` در برابر shell)، Wi-Fi (SSID/BSSID/IP/MAC)، بلوتوث، DNS.
* **هسته / فرایند / سیستم**: `uname`، boot id، uptime، UID/PID، SELinux، منطقه زمانی، locale، محیط، سرویس‌ها/فرایندهای در حال اجرا.
* **سخت‌افزار / رسانه**: CPU، حافظه، حسگرها، نمایشگر، دوربین‌ها، system features، GPU، شناسه DRM Widevine.
* **بسته‌ها / حساب‌ها / WebView**: فهرست و تعداد بسته‌های نصب‌شده، چکیده امضا به‌ازای هر برنامه، امضای این برنامه، خودبازبینی بسته خود، برشماری‌های حل intent، حساب‌ها، تعداد گزارش تماس، user agent.
* **انطباق sandbox**: سطوحی که sandbox باید **ببندد** (CID/serial از eMMC، هویت UFS و SoC، `/proc/cmdline`، `/proc/1/*`، `/dev/kmsg`، `/system/build.prop`) و ۲۲ سرویس `dumpsys`. اینجا امتناع همان پاسخ است: `EACCES`/`DENIED` خواندنِ منطبق است، و دستگاهی که به‌جایش محتوا را تحویل دهد منطبق نیست.
* **شناسه‌های تبلیغاتی**: GAID از binder سرویس‌های Play، به‌علاوه کل خانواده کلید (AAID، OAID/VAID و `pps_*` هواوی) که در سه settings store جاروب می‌شود.
* **آشکارسازهای root / hook / شبیه‌ساز**: نشانه‌های `su`/Magisk/KernelSU/APatch/Xposed/LSPosed/Riru، کتابخانه‌های تزریق‌شده در `/proc/self/maps`، بسته‌های شناخته‌شده، ویژگی‌های خطرناک و شبیه‌ساز، verified boot state.

## دانلود و پشتیبانی

هر انتشار دو APK منتشر می‌کند: **SDK_35** سندباکس مدرن و سخت‌گیرانه را هدف می‌گیرد و همین را استفاده کنید؛ **SDK_27** سطح API قدیمی‌تری را با دامنه SELinux آزادتر هدف می‌گیرد و برای مقایسه مفید است. هر دو روی اندروید ۸.۰ به بالا نصب می‌شوند.

* Release (APK): https://github.com/VD171/VD-Infos/releases
* https://github.com/VD171/VD-Infos
* https://xdaforums.com/t/VD-Infos.4097379/
* https://t.me/RootDetected
* https://t.me/BlankAssistance

## زبان‌ها

۲۱ زبان رابط کاربری: انگلیسی، پرتغالی، اسپانیایی، ایتالیایی، آلمانی، فرانسوی، روسی، اندونزیایی، ترکی، لهستانی، هلندی، سوئدی، چکی، ویتنامی، چینی، ژاپنی، کره‌ای، فارسی، هندی، عربی و تایلندی. برنامه در نخستین اجرا یک انتخاب زبان یک‌باره ارائه می‌دهد (با گزینه "پیش‌فرض سیستم") و در غیر این صورت از زبان سیستم پیروی می‌کند.

## معماری

```
core/model      دامنه تغییرناپذیر: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          NativeBridge (JNI) - SystemProps (بازتاب) - PropCatalog (داده)
                SemanticProbes - IntegrityProbes - ProbeRegistry
engine/         ProbeEngine - fan-out کوروتین، پخش نتایج به‌صورت Flow
data/           SnapshotStore (ماندگاری + diff) - Exporter (اشتراک JSON/متن)
ui/             Jetpack Compose, Material 3, رنگ پویا، پیشرفت زنده
cpp/            native_probes.cpp - لنز بومی، بدون وابستگی
```

* **موازی‌سازی**: ۹۲۲ کاوشگر روی dispatcher پیش‌فرض با شمار مجوز محدود پخش می‌شوند؛ نتایج به‌محض رسیدن به UI سرازیر می‌شوند.
* **هیچ چیز در پس‌زمینه**: نه سرویسی و نه پویش زمان‌بندی‌شده‌ای؛ برنامه فقط تا وقتی باز است اجرا می‌شود و وقتی بی‌کار بماند خودش بسته می‌شود.
* **لایه بومی**: یک `.so` کوچک، که با نام از طریق `RegisterNatives` پیوند می‌خورد، عمداً بسیار کوچک نگه داشته شده چون بخشی است که باید فریب دادنش سخت باشد.

## مشارکت

بیشتر مشارکت‌ها به Kotlin نیاز ندارند: فهرست‌ها به‌صورت متن ساده در `VDInfos/app/src/main/assets/data/` قرار دارند. یک خط اضافه یا حذف کنید و یک pull request باز کنید.

* `*_apps.txt` - در هر خط یک نام بسته
* `props.txt` - فهرست ویژگی‌های سیستم، `دسته<tab>کلید`
* `spoof_keys.txt` - ماتریس spoof تنظیمات، `کلید:نوع`
* دیگر فایل‌های `.txt` - آن‌ها هم هر خط یک ورودی (تکه‌های نام ماژول کرنل، نام‌های داخل `/data/local/tmp`)

یک `#` آغاز توضیح است؛ خطوط خالی نادیده گرفته می‌شوند. مشارکت در کد نیز پذیرفته می‌شود. با مشارکت می‌پذیرید که کارتان تحت AGPL-3.0-or-later این پروژه منتشر شود.

## فهرست

منابع پنهان‌سازی و تشخیص root (راهنماها، ماژول‌ها، چارچوب‌ها، آشکارسازها) در فهرستی جداگانه قرار دارند: [CATALOG.md](CATALOG.md).

## قدردانی

سپاس ویژه از **[frknkrc44](https://github.com/frknkrc44)**، توسعه‌دهنده **[HMA-OSS](https://github.com/frknkrc44/HMA-OSS)**، برای دقتی که در آن گذاشته است. HMA-OSS جایی است که چند مورد از رفع‌هایی که این برنامه پیشنهاد می‌دهد اعمال می‌شوند - پنهان کردن برنامه‌های هدف و تنظیم پیش‌تنظیم‌های spoof - و هر اشاره به آن در برنامه به منبعش پیوند می‌خورد.

پیوندها، کانال‌ها و راه حمایت از آن: [HMA-OSS.md](HMA-OSS.md).

## تماس

* https://vd171.ru
* https://vd.priv8.ru
* **Telegram:** @VD_Priv8 https://t.me/VD_Priv8
* **Discord:** @VD.Priv8 https://discord.com/users/1296831918989639721
* **ایمیل:** vd.priv8@pm.me
* **XDA-Developers:** @VD171 https://xdaforums.com/m/vd171.4699873/
* **GitHub:** @VD171 https://github.com/VD171

## مجوز

**GNU AGPL-3.0-or-later** - نگاه کنید به [LICENSE](LICENSE). کپی‌لفت، شامل بند شبکه: هرکس نسخه‌ای اصلاح‌شده را اجرا کند (حتی به‌عنوان سرویس) باید منبع آن را ارائه دهد. عمداً برای ابزاری پژوهشی/ضدتشخیص انتخاب شده تا fork‌ها باز بمانند.

---

`android` `privacy` `security` `root-detection` `hook-detection` `spoof-detection` `anti-detection` `tamper-detection` `device-fingerprint` `magisk` `kernelsu` `lsposed` `play-integrity` `attestation` `tee` `keystore` `scanner` `diagnostics` `reverse-engineering` `kotlin`
