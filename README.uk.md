<!--
  VD INFOS - device method debugger: read every device info by every method, then compare.
  Copyright (C) 2026  VD171 - SPDX-License-Identifier: AGPL-3.0-or-later
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
  <b>Зневаджувач методів пристрою.</b><br>
  Читає кожне значення пристрою через кожен метод, що здатен його прочитати, і вишиковує їх поряд, щоб ви могли порівняти.
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

Android - надзвичайно потужна та універсальна операційна система; чого вам ніхто не каже - це те, що всі ваші особисті дані та конфіденційна інформація доступні кожному застосунку, який ви встановлюєте, і захищати себе від цих вторгнень у приватність - це обов'язок. VD Infos показує вам приклад того, що можна зчитати з вашого пристрою, і робить це як *зневаджувач методів*: для кожної одиниці інформації він читає значення через **кожен метод, що здатен його прочитати** - `Build.*`, `SystemProperties`, `getprop`, нативний `__system_property_get`, системні менеджери, content provider'и, файли, системні виклики та апаратну атестацію ключів (TEE) - і вишиковує їх поряд, щоб ви могли порівняти. Коли один метод розходиться з іншими, щось посередині переписує цю поверхню: фреймворк гукінгу, спуфер, resolver-шим. **ЖОДНА ІНФОРМАЦІЯ НЕ ЗБЕРІГАЄТЬСЯ, НЕ НАДСИЛАЄТЬСЯ І НЕ ПЕРЕДАЄТЬСЯ ДО ЖОДНОГО ФАЙЛУ¹ ЧИ СЕРВЕРА** - усе працює на пристрої, значення, що несуть ідентичність, замасковані, доки ви їх не розкриєте, а звіт залишає пристрій лише тоді, коли ви явно поділитеся ним або збережете його; за бажанням заблокуйте доступ до інтернету фаєрволом або просто вимкніть його.

> **¹ Виняток:** кожне сканування зберігає свій повний звіт у приватному сховищі застосунку (`files/last_snapshot.json`). Прочитати його може лише сам застосунок або root, наступне сканування перезаписує його, і він ніколи не залишає пристрій. Він існує, щоб AI-агенти могли прочитати повний результат; див. **Для AI-агентів: повний звіт у форматі JSON**.

## Що він перевіряє

Кожну одиницю зчитано через кожен метод, що здатен її прочитати (Java SDK / native / shell), і порівняно поряд:

* **~488 системних властивостей** зчитано П'ЯТЬМА способами: `SystemProperties.get`, обидві точки входу bionic (`__system_property_read_callback` і 92-байтовий `__system_property_get`), `getprop`, запущений JVM, і `getprop`, запущений з нативного коду через `popen` - shell-команда, запущена через `ProcessBuilder`, і та сама команда, запущена без JVM, - це не той самий ракурс, бо перше є поверхнею, яку переписує фреймворк приховування root, а друге - ні.
* **Ідентичність пристрою**: model, manufacturer, brand, device, product, board, hardware, fingerprint, bootloader, build id/tags/type - кожне поле `Build.*` проти всіх його варіантів `ro.product.*` (system/vendor/odm), нативного та shell.
* **Ідентифікатори**: serial (багато getter'ів), Android ID (settings і provider), GSF ID, advertising ID, IMEI/primary IMEI/MEID/IMSI/ICCID, user serial.
* **Телефонія**: поверхня TelephonyManager (оператор, SIM, мережа, роумінг...), SubscriptionManager (кілька SIM), cell info.
* **Мережа**: **MAC** за інтерфейсами (`NetworkInterface` проти `/sys/class/net` проти shell), Wi-Fi (SSID/BSSID/IP/MAC), Bluetooth, DNS.
* **Ядро / процес / система**: `uname`, boot id, uptime, UID/PID, SELinux, часовий пояс, locale, середовище, запущені служби/процеси.
* **Апаратне забезпечення / медіа**: CPU, пам'ять, сенсори, дисплей, камери, system features, GPU, Widevine DRM ID.
* **Пакети / облікові записи / WebView**: список і кількість встановлених пакетів, дайджести підпису для кожного застосунку, підпис цього застосунку, інтроспекція власного пакета, перелічення розв'язання intent'ів, облікові записи, кількість записів журналу викликів, user agent.
* **Відповідність пісочниці**: поверхні, які пісочниця має ЗАКРИВАТИ (eMMC CID/serial, ідентичність UFS і SoC, `/proc/cmdline`, `/proc/1/*`, `/dev/kmsg`, `/system/build.prop`) та 22 служби `dumpsys`. Тут відмова і є відповіддю: `EACCES`/`DENIED` - це відповідне зчитування, а пристрій, який натомість віддає вміст, є невідповідним.
* **Рекламні ідентифікатори**: GAID з binder'а Play Services, плюс ціла родина ключів (AAID, OAID/VAID і `pps_*` від Huawei), пройдена по трьох сховищах settings.
* **Детектори root / hook / емулятора**: артефакти `su`/Magisk/KernelSU/APatch/Xposed/LSPosed/Riru, впроваджені бібліотеки в `/proc/self/maps`, відомі пакети, небезпечні властивості та властивості емулятора, verified boot state.

## Завантаження та підтримка

Кожен випуск публікує два APK: **SDK_35** націлений на сувору сучасну пісочницю і є тим, який варто використовувати; **SDK_27** націлений на старіший рівень API, з вільнішим доменом SELinux, корисний для порівняння. Обидва встановлюються на Android 8.0 і новіші.

* Release (APK): https://github.com/VD171/VD-Infos/releases
* https://github.com/VD171/VD-Infos
* https://xdaforums.com/t/VD-Infos.4097379/
* https://t.me/RootDetected
* https://t.me/BlankAssistance

## Для AI-агентів: повний звіт у форматі JSON

Створено для AI-агентів та LLM-інструментів, що працюють на пристрої через shell (асистенти з програмування, скрипти аналізу). Замість читання скриншотів агент читає весь результат однією командою:

```sh
su -c cat /data/data/ru.vd171.vdinfos/files/last_snapshot.json
```

* **Що він містить:** кожен зонд з його вердиктом і значенням з кожної лінзи, у тому ж JSON, що й кнопка експорту. Екран показує лише перше зчитування згорнутого елемента; у файлі є всі.
* **Коли він оновлюється:** наприкінці кожного сканування, тож спершу запустіть сканування; файл завжди містить найновіше.
* **Вимога:** root, бо файл зберігається у приватному сховищі застосунку.

## Архітектура

```
core/model      незмінний домен: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          NativeBridge (JNI) - SystemProps (рефлексія) - PropCatalog (дані)
                SemanticProbes - IntegrityProbes - ProbeRegistry
engine/         ProbeEngine - fan-out корутин, передає результати як Flow
data/           SnapshotStore (збереження + diff) - Exporter (спільний доступ JSON/текст)
ui/             Jetpack Compose, Material 3, динамічний колір, живий прогрес
cpp/            native_probes.cpp - нативна лінза, без залежностей
```

* **Паралелізм**: 996 зондів розходяться по типовому диспетчері з обмеженою кількістю дозволів; результати потрапляють до UI тоді, коли надходять.
* **Нічого у фоні**: жодних служб і жодних запланованих сканувань; застосунок працює лише поки відкритий і сам закривається, коли простоює.
* **Нативний шар**: один маленький `.so`, прив'язаний за іменем через `RegisterNatives`, навмисно крихітний, бо це та частина, яку має бути важко обдурити.

## Внесок

Більшість внесків не потребують Kotlin: списки зберігаються як звичайний текст у `VDInfos/app/src/main/assets/data/`. Додайте або видаліть один рядок і відкрийте pull request.

* `*_apps.txt` - одна назва пакета на рядок
* `props.txt` - каталог системних властивостей, `КАТЕГОРІЯ<tab>ключ`
* `spoof_keys.txt` - матриця спуфінгу settings, `ключ:ТИП`
* `known_certs.txt` - каталог сертифікатів підпису, `SHA-256<tab>власник` (повний хеш, узятий зі справжнього APK за допомогою `apksigner verify --print-certs`)
* інші файли `.txt` - теж один запис на рядок (фрагменти назв модулів ядра, назви файлів у `/data/local/tmp`)

`#` починає коментар; порожні рядки ігноруються. Деякі файли вміщують кілька полів на рядок, розділених пробілами чи табуляціями, або необов'язкові маркери (`S` і `@field` у `props.txt`); зберігайте форму рядків, які вже є у файлі. Внески коду теж вітаються. Роблячи внесок, ви погоджуєтеся, що ваша робота постачається за ліцензією цього проєкту AGPL-3.0-or-later.

## Каталог

Довідники з приховування та виявлення root (посібники, модулі, фреймворки, детектори) зібрані в окремому каталозі: [CATALOG.md](CATALOG.md).

## Подяки

Особлива подяка **[frknkrc44](https://github.com/frknkrc44)**, розробнику **[HMA-OSS](https://github.com/frknkrc44/HMA-OSS)**, за турботу, вкладену в нього. HMA-OSS - це місце, де застосовано кілька виправлень, які пропонує цей застосунок - приховування цільових застосунків і налаштування пресетів спуфінгу - і кожна згадка про нього в застосунку веде назад до його джерела.

Його посилання, канали та як його підтримати: [HMA-OSS.md](HMA-OSS.md).

## Документація

- **[Каталог](CATALOG.md)**: довідники з приховування root і виявлення - посібники, модулі, фреймворки та детектори.
- **[Список змін](CHANGELOG.md)**: що змінилося в кожному випуску.
- **[HMA-OSS](HMA-OSS.md)**: модуль приховування застосунків, на якому ґрунтуються виправлення застосунку - його посилання, канали та як його підтримати.
- **[Внесок](README.md#contributing)**: як додавати цілі виявлення (здебільшого прості текстові списки, Kotlin не потрібен).

<!-- /vd-docs -->

## Дивіться також

Споріднені проєкти того самого автора:

- **[VDGoogle](https://github.com/VD171/VD-Google)**: ваші сервіси Google (RCS, Wallet, атестації Play Integrity), прочитані на вашому пристрої без інтернету.
- **[VDBackup](https://github.com/VD171/VD-Backup)**: повне резервне копіювання й відновлення застосунків і даних з правами root, кероване повністю з WebUI.
- **[COPG-VD](https://github.com/VD171/COPG-VD)**: спуфер пристрою, що перетворює свіжий fingerprint на узгоджений, послідовний профіль.

Pixel `build.prop`: **[@VictorGabriel7Dev/build.prop](https://github.com/VictorGabriel7Dev/build.prop)** (fork of @T3SL4).

## Контакти

* https://vd171.ru
* https://vd.priv8.ru
* **Telegram:** @VD_Priv8 https://t.me/VD_Priv8
* **Channel:** @Priv8_VD https://t.me/Priv8_VD
* **Discord:** @VD.Priv8 https://discord.com/users/1296831918989639721
* **E-mail:** vd.priv8@pm.me
* **XDA-Developers:** @VD171 https://xdaforums.com/m/vd171.4699873/
* **XDA thread:** https://xdaforums.com/t/VD-Infos.4097379/
* **GitHub:** @VD171 https://github.com/VD171

## Ліцензія

**GNU AGPL-3.0-or-later** - див. [LICENSE](LICENSE). Copyleft, включно з мережевим пунктом: будь-хто, хто запускає змінену версію (навіть як сервіс), повинен надати її вихідний код. Обрано навмисно для дослідницького/антидетекційного інструменту, щоб тримати форки відкритими.

## Торгові марки

VD Infos - це незалежний, неофіційний інструмент для досліджень і діагностики. Він не пов'язаний, не схвалений і не спонсорований жодним із постачальників, чиє програмне забезпечення, сервіси або марки він читає, виявляє чи називає (включно з Google LLC та Android Open Source Project). "Android", "Google", "Google Play", "Play Integrity", "Widevine" та всі інші назви продуктів, логотипи й торгові марки є власністю їхніх відповідних власників. VD Infos лише читає те, що вже є на вашому пристрої, і нікуди нічого не надсилає.

## Tags

`android` `privacy` `security` `root-detection` `hook-detection` `spoof-detection` `anti-detection` `tamper-detection` `device-fingerprint` `magisk` `kernelsu` `lsposed` `play-integrity` `attestation` `tee` `keystore` `scanner` `diagnostics` `reverse-engineering` `kotlin`

<!-- doc-footer -->

---

> _Ця документація створена за допомогою Claude (Anthropic) і підтримується VD171. Виправлення та покращення вітаються; див. [Contributing](README.md#contributing)._
