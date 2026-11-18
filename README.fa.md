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
  <b>اشکال‌زدای روش‌ها.</b><br>
  هر مقدار دستگاه را با هر روشی که قادر به خواندن آن است می‌خواند و آن‌ها را کنار هم می‌چیند تا مقایسه کنید.
</p>

<p align="center">
  <a href="https://github.com/VD171/VD-Infos/releases">Releases</a> ·
  <a href="https://github.com/VD171/VD-Infos/wiki">Wiki</a> ·
  <a href="https://github.com/VD171/VD-Infos/wiki/USER-GUIDE">راهنمای کاربر</a> ·
  <a href="https://github.com/VD171/VD-Infos/wiki/AI-AGENTS">عامل‌های هوش مصنوعی</a> ·
  <a href="https://github.com/VD171/VD-Infos/wiki/EXPORT-FORMAT">قالب خروجی</a> ·
  <a href="https://github.com/VD171/VD-Infos/wiki/ARCHITECTURE">معماری</a> ·
  <a href="CATALOG.md">فهرست</a> ·
  <a href="CHANGELOG.md">Changelog</a> ·
  <a href="https://xdaforums.com/t/VD-Infos.4097379/">XDA thread</a>
</p>

> **Languages:** [English](README.md) · [العربية](README.ar.md) · [Čeština](README.cs.md) · [Deutsch](README.de.md) · [Español](README.es.md) · [فارسی](README.fa.md) · [Français](README.fr.md) · [हिन्दी](README.hi.md) · [Bahasa Indonesia](README.id.md) · [Italiano](README.it.md) · [日本語](README.ja.md) · [한국어](README.ko.md) · [Nederlands](README.nl.md) · [Polski](README.pl.md) · [Português](README.pt.md) · [Русский](README.ru.md) · [Svenska](README.sv.md) · [ไทย](README.th.md) · [Türkçe](README.tr.md) · [Українська](README.uk.md) · [Tiếng Việt](README.vi.md) · [中文](README.zh.md)

<p align="center">
  <img src="images/vdinfos-01.png" height="420"/>
  <img src="images/vdinfos-02.png" height="420"/>
</p>

---

اندروید سیستم‌عاملی بی‌نهایت قدرتمند و همه‌کاره است؛ چیزی که هیچ‌کس به شما نمی‌گوید این است که همه جزئیات شخصی و اطلاعات محرمانه شما در دسترس هر برنامه‌ای است که نصب می‌کنید، و محافظت از خود در برابر این تجاوزها به حریم خصوصی یک وظیفه است. VD Infos نمونه‌ای از آنچه می‌توان از دستگاه شما استخراج کرد را نشان می‌دهد و این کار را همچون یک *اشکال‌زدای روش‌ها* انجام می‌دهد: برای هر اطلاعات، مقدار را با **هر روشی که قادر به خواندن آن است** می‌خواند: `Build.*`، `SystemProperties`، `getprop`، `__system_property_get` بومی، مدیران سیستم، content provider‌ها، فایل‌ها، syscall‌ها و attestation کلید سخت‌افزاری (TEE)، و آن‌ها را کنار هم می‌چیند تا مقایسه کنید. وقتی یک روش با بقیه اختلاف دارد، چیزی در میانه در حال بازنویسی آن سطح است: یک چارچوب hook، یک spoofer، یک shim از resolver. **هیچ اطلاعاتی ذخیره، ارسال یا به هیچ فایل¹ یا سروری منتقل نمی‌شود**: همه چیز روی دستگاه اجرا می‌شود، مقادیرِ حاملِ هویت تا زمانی که آشکارشان کنید پوشانده می‌مانند، و گزارش تنها زمانی دستگاه را ترک می‌کند که خودتان صریحاً آن را به اشتراک بگذارید یا ذخیره کنید؛ اگر خواستید، دسترسی اینترنت را با فایروال مسدود کنید یا صرفاً آن را خاموش کنید.

> **¹ استثنا:** هر اسکن گزارش کامل خود را در حافظهٔ خصوصی برنامه نگه می‌دارد (`files/last_snapshot.json`). فقط خود برنامه یا root می‌تواند آن را بخواند، اسکن بعدی آن را بازنویسی می‌کند و هرگز از دستگاه خارج نمی‌شود. برای این وجود دارد که عامل‌های هوش مصنوعی نتیجهٔ کامل را بخوانند؛ ببینید **برای عامل‌های هوش مصنوعی: گزارش کامل به‌صورت JSON**.

## چه چیزی را بازرسی می‌کند

هر مورد با هر روشی که قادر به خواندن آن است خوانده می‌شود (Java SDK / بومی / shell) و کنار هم مقایسه می‌شود:

* **حدود ۴۸۸ ویژگی سیستم** به پنج روش خوانده می‌شوند: `SystemProperties.get`، هر دو نقطه ورود bionic (`__system_property_read_callback` و `__system_property_get` ۹۲ بایتی)، `getprop` اجراشده توسط JVM، و `getprop` اجراشده از کد بومی از طریق `popen`؛ یک فرمان shell که `ProcessBuilder` اجرا می‌کند و همان فرمان که بدون JVM اجرا شود یک زاویه دید نیستند، چون اولی سطحی است که چارچوب پنهان‌سازی root آن را بازنویسی می‌کند و دومی نه.
* **هویت دستگاه**: مدل، سازنده، برند، device، product، board، hardware، fingerprint، bootloader، build id/tags/type، هر فیلد `Build.*` در برابر همه گونه‌های `ro.product.*` آن (system/vendor/odm)، بومی و shell.
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

## برای عامل‌های هوش مصنوعی: گزارش کامل به‌صورت JSON

برای عامل‌های هوش مصنوعی و ابزارهای LLM ساخته شده که از طریق shell روی دستگاه کار می‌کنند (دستیارهای برنامه‌نویسی، اسکریپت‌های تحلیل). عامل به‌جای خواندن اسکرین‌شات، کل نتیجه را با یک فرمان می‌خواند:

```sh
su -c cat /data/data/ru.vd171.vdinfos/files/last_snapshot.json
```

* **محتوا:** هر کاوشگر با حکم خود و مقدار همهٔ لنزها، با همان JSON دکمهٔ خروجی. صفحه برای مورد جمع‌شده فقط اولین خوانش را نشان می‌دهد؛ فایل همه را دارد.
* **زمان به‌روزرسانی:** پایان هر اسکن؛ پس اول یک اسکن اجرا کنید. فایل همیشه آخرین نتیجه را دارد.
* **پیش‌نیاز:** root، چون فایل در حافظهٔ خصوصی برنامه است.

## معماری

```
core/model      دامنه تغییرناپذیر: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          NativeBridge (JNI), SystemProps (بازتاب), PropCatalog (داده)
                SemanticProbes, IntegrityProbes, ProbeRegistry
engine/         ProbeEngine: fan-out کوروتین، پخش نتایج به‌صورت Flow
data/           SnapshotStore (ماندگاری + diff), Exporter (اشتراک JSON/متن)
ui/             Jetpack Compose, Material 3, رنگ پویا، پیشرفت زنده
cpp/            native_probes.cpp: لنز بومی، بدون وابستگی
```

* **موازی‌سازی**: ۹۹۶ کاوشگر روی dispatcher پیش‌فرض با شمار مجوز محدود پخش می‌شوند؛ نتایج به‌محض رسیدن به UI سرازیر می‌شوند.
* **هیچ چیز در پس‌زمینه**: نه سرویسی و نه پویش زمان‌بندی‌شده‌ای؛ برنامه فقط تا وقتی باز است اجرا می‌شود و وقتی بی‌کار بماند خودش بسته می‌شود.
* **لایه بومی**: یک `.so` کوچک، که با نام از طریق `RegisterNatives` پیوند می‌خورد، عمداً بسیار کوچک نگه داشته شده چون بخشی است که باید فریب دادنش سخت باشد.

## مشارکت

بیشتر مشارکت‌ها به Kotlin نیاز ندارند: فهرست‌ها به‌صورت متن ساده در `VDInfos/app/src/main/assets/data/` قرار دارند. یک خط اضافه یا حذف کنید و یک pull request باز کنید.

* `*_apps.txt`: در هر خط یک نام بسته
* `props.txt`: فهرست ویژگی‌های سیستم، `دسته<tab>کلید`
* `spoof_keys.txt`: ماتریس spoof تنظیمات، `کلید:نوع`
* `known_certs.txt`: فهرست گواهی‌های امضا، `SHA-256<tab>مالک` (هش کامل، گرفته‌شده از یک APK واقعی با `apksigner verify --print-certs`)
* دیگر فایل‌های `.txt`: آن‌ها هم هر خط یک ورودی (تکه‌های نام ماژول کرنل، نام‌های داخل `/data/local/tmp`)

یک `#` آغاز توضیح است؛ خطوط خالی نادیده گرفته می‌شوند. برخی فایل‌ها در هر خط چند فیلد جداشده با فاصله یا تب دارند، یا نشانگرهای اختیاری (`S` و `@field` در `props.txt`)؛ شکل خطوطی را که از قبل در فایل هستند حفظ کنید. مشارکت در کد نیز پذیرفته می‌شود. با مشارکت می‌پذیرید که کارتان تحت AGPL-3.0-or-later این پروژه منتشر شود.

## فهرست

منابع پنهان‌سازی و تشخیص root (راهنماها، ماژول‌ها، چارچوب‌ها، آشکارسازها) در فهرستی جداگانه قرار دارند: [CATALOG.md](CATALOG.md).

## قدردانی

سپاس ویژه از **[frknkrc44](https://github.com/frknkrc44)**، توسعه‌دهنده **[HMA-OSS](https://github.com/frknkrc44/HMA-OSS)**، برای دقتی که در آن گذاشته است. HMA-OSS جایی است که چند مورد از رفع‌هایی که این برنامه پیشنهاد می‌دهد اعمال می‌شوند (پنهان کردن برنامه‌های هدف و تنظیم پیش‌تنظیم‌های spoof)، و هر اشاره به آن در برنامه به منبعش پیوند می‌خورد.

پیوندها، کانال‌ها و راه حمایت از آن: [HMA-OSS.md](HMA-OSS.md).

## مستندات

- **[Wiki](https://github.com/VD171/VD-Infos/wiki)**: همهٔ مستندات، قابل مرور آنلاین به ۲۲ زبان.
- **[راهنمای کاربر](https://github.com/VD171/VD-Infos/wiki/USER-GUIDE)**: نصب، اجرای یک اسکن، خواندن نتیجه و اشتراک‌گذاری یک گزارش.
- **[برای عامل‌های هوش مصنوعی](https://github.com/VD171/VD-Infos/wiki/AI-AGENTS)** و **[قالب خروجی](https://github.com/VD171/VD-Infos/wiki/EXPORT-FORMAT)**: گزارش قابل‌خواندن توسط ماشین و شِمای آن.
- **[معماری](https://github.com/VD171/VD-Infos/wiki/ARCHITECTURE)** و **[ساخت](https://github.com/VD171/VD-Infos/wiki/BUILDING)**: چگونه کار می‌کند و چگونه آن را بسازیم.
- **[حریم خصوصی](https://github.com/VD171/VD-Infos/wiki/PRIVACY)**، **[امنیت](https://github.com/VD171/VD-Infos/wiki/SECURITY)** و **[منشور رفتار](https://github.com/VD171/VD-Infos/wiki/CODE_OF_CONDUCT)**: بیانیهٔ حریم خصوصی و مستندات حاکمیت.
- **[فهرست](CATALOG.md)**: مراجع پنهان‌سازی روت و شناسایی (راهنماها، ماژول‌ها، چارچوب‌ها و شناساگرها).
- **[سیاههٔ تغییرات](CHANGELOG.md)**: آنچه در هر نسخه تغییر کرده است.
- **[HMA-OSS](https://github.com/VD171/VD-Infos/wiki/HMA-OSS)**: ماژول پنهان‌سازی برنامه‌ها که اصلاحات این برنامه بر پایهٔ آن است (پیوندها، کانال‌ها و راه حمایت از آن).
- **[مشارکت](https://github.com/VD171/VD-Infos/wiki/CONTRIBUTING)**: چگونگی افزودن هدف‌های شناسایی (بیشتر فهرست‌های متنی ساده، بدون نیاز به Kotlin).

<!-- /vd-docs -->

## همچنین ببینید

پروژه‌های خواهر از همان نویسنده:

- **[VDGoogle](https://github.com/VD171/VD-Google)**: سرویس‌های Google شما (RCS، Wallet، گواهی‌های Play Integrity)، خوانده‌شده روی دستگاه شما بدون اینترنت.
- **[VDBackup](https://github.com/VD171/VD-Backup)**: پشتیبان‌گیری و بازیابی کامل برنامه‌ها و داده‌ها به‌صورت root، که به‌طور کامل از یک WebUI اداره می‌شود.
- **[COPG-VD](https://github.com/VD171/COPG-VD)**: یک جعل‌کننده دستگاه که یک fingerprint تازه را به یک نمایه منسجم و سازگار تبدیل می‌کند.
- **[VD-dropbear](https://github.com/VD171/VD-dropbear)**: یک دیمن SSH مستقل با دسترسی روت (Dropbear).

Pixel `build.prop`: **[@VictorGabriel7Dev/build.prop](https://github.com/VictorGabriel7Dev/build.prop)** (فورک از @T3SL4).

## تماس

- https://vd171.ru
- https://vd.priv8.ru
- *Telegram:* @VD_Priv8 https://t.me/VD_Priv8
- *Channel:* @Priv8_VD https://t.me/Priv8_VD
- *Discord:* @VD.Priv8 https://discord.com/users/1296831918989639721
- *E-mail:* vd.priv8@pm.me
- *XDA-Developers:* @VD171 https://xdaforums.com/m/vd171.4699873/
- *XDA thread:* https://xdaforums.com/t/VD-Infos.4097379/
- *GitHub:* @VD171 https://github.com/VD171

## مجوز

**GNU AGPL-3.0-or-later**: نگاه کنید به [LICENSE](LICENSE). کپی‌لفت، شامل بند شبکه: هرکس نسخه‌ای اصلاح‌شده را اجرا کند (حتی به‌عنوان سرویس) باید منبع آن را ارائه دهد. عمداً برای ابزاری پژوهشی/ضدتشخیص انتخاب شده تا fork‌ها باز بمانند.

## علائم تجاری

VD Infos یک ابزار مستقل و غیررسمی پژوهش و عیب‌یابی است. با هیچ‌یک از فروشندگانی که نرم‌افزار، سرویس‌ها یا علائم آن‌ها را می‌خواند، تشخیص می‌دهد یا نام می‌برد وابسته نیست و از سوی آن‌ها تأیید یا پشتیبانی مالی نمی‌شود (از جمله Google LLC و Android Open Source Project). "Android"، "Google"، "Google Play"، "Play Integrity"، "Widevine" و همه نام‌های محصول، آرم‌ها و علائم تجاری دیگر متعلق به صاحبان مربوط به خودشان هستند. VD Infos فقط آنچه را که از پیش روی دستگاه شماست می‌خواند و چیزی به هیچ‌جا ارسال نمی‌کند.

## برچسب‌ها

`android` `privacy` `security` `root-detection` `hook-detection` `spoof-detection` `anti-detection` `tamper-detection` `device-fingerprint` `magisk` `kernelsu` `lsposed` `play-integrity` `attestation` `tee` `keystore` `scanner` `diagnostics` `reverse-engineering` `kotlin`
