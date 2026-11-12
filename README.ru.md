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
  <b>Отладчик методов устройства.</b><br>
  Читайте каждое значение устройства всеми методами, способными его прочитать, и выстраивайте их рядом для сравнения.
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

Android - очень мощная и гибкая операционная система; о чём никто не говорит - все ваши персональные данные и конфиденциальная информация доступны каждому приложению, которое вы устанавливаете, и защищаться от такого вторжения в приватность - обязанность. VD Infos показывает пример того, что можно считать с вашего устройства, и делает это как *отладчик методов*: для каждого факта он читает значение **всеми методами, способными его прочитать** - `Build.*`, `SystemProperties`, `getprop`, нативный `__system_property_get`, системные менеджеры, content provider'ы, файлы, syscall'ы и аппаратный attestation ключа (TEE) - и выстраивает их рядом для сравнения. Когда один метод расходится с остальными, что-то посередине переписывает эту поверхность: фреймворк-хук, спуфер, resolver-shim. **НИКАКАЯ ИНФОРМАЦИЯ НЕ СОХРАНЯЕТСЯ, НЕ ОТПРАВЛЯЕТСЯ И НЕ ПЕРЕДАЁТСЯ НИ В КАКОЙ ФАЙЛ¹ ИЛИ НА СЕРВЕР** - всё работает на устройстве, значения, несущие идентичность, замаскированы, пока вы их не раскроете, а отчёт покидает устройство только когда вы явно делитесь им или сохраняете; при желании заблокируйте доступ в интернет фаерволом или просто отключите его.

> **¹ Исключение:** каждое сканирование сохраняет полный отчёт в приватном хранилище приложения (`files/last_snapshot.json`). Прочитать его может только само приложение или root, следующее сканирование перезаписывает его, и он никогда не покидает устройство. Он нужен, чтобы ИИ-агенты могли прочитать полный результат; см. **Для ИИ-агентов: полный отчёт в JSON**.

## Что проверяет

Каждый пункт читается всеми методами, способными его прочитать (Java SDK / нативный / shell), и сравнивается бок о бок:

* **~488 системных свойств** читаются ПЯТЬЮ способами: `SystemProperties.get`, обе точки входа bionic (`__system_property_read_callback` и 92-байтная `__system_property_get`), `getprop`, запущенный из JVM, и `getprop`, запущенный из нативного кода через `popen` - shell-команда, запущенная `ProcessBuilder`, и та же команда без JVM - это не одна и та же точка обзора, потому что первая - поверхность, которую фреймворк сокрытия root переписывает, а вторая - нет.
* **Идентичность устройства**: модель, производитель, бренд, device, product, board, hardware, fingerprint, bootloader, build id/tags/type - каждое поле `Build.*` против всех его вариантов `ro.product.*` (system/vendor/odm), нативно и через shell.
* **Идентификаторы**: serial (много геттеров), Android ID (settings и provider), GSF ID, advertising ID, IMEI/primary IMEI/MEID/IMSI/ICCID, user serial.
* **Телефония**: поверхность TelephonyManager (оператор, SIM, сеть, роуминг...), SubscriptionManager (multi-SIM), cell info.
* **Сеть**: **MAC** по интерфейсам (`NetworkInterface` vs `/sys/class/net` vs shell), Wi-Fi (SSID/BSSID/IP/MAC), Bluetooth, DNS.
* **Ядро / процесс / система**: `uname`, boot id, uptime, UID/PID, SELinux, часовой пояс, locale, окружение, запущенные службы/процессы.
* **Железо / медиа**: CPU, память, датчики, дисплей, камеры, system features, GPU, Widevine DRM ID.
* **Пакеты / аккаунты / WebView**: список и число установленных пакетов, дайджесты подписи по приложениям, подпись этого приложения, самоинспекция собственного пакета, перечисления разрешения интентов, аккаунты, число записей журнала звонков, user agent.
* **Соответствие sandbox**: поверхности, которые sandbox должен ЗАКРЫТЬ (CID/serial eMMC, идентичность UFS и SoC, `/proc/cmdline`, `/proc/1/*`, `/dev/kmsg`, `/system/build.prop`) и 22 сервиса `dumpsys`. Здесь отказ - это ответ: `EACCES`/`DENIED` - соответствующее чтение, а устройство, которое вместо этого отдаёт содержимое, не соответствует.
* **Рекламные идентификаторы**: GAID через binder Play Services, плюс всё семейство ключей (AAID, OAID/VAID и `pps_*` от Huawei), прочёсанное по трём settings store.
* **Детекторы root / hook / эмулятора**: артефакты `su`/Magisk/KernelSU/APatch/Xposed/LSPosed/Riru, внедрённые библиотеки в `/proc/self/maps`, известные пакеты, опасные и эмуляторные свойства, verified boot state.

## Загрузка и поддержка

В каждом релизе публикуются два APK: **SDK_35** нацелен на строгую современную песочницу и его следует использовать; **SDK_27** нацелен на более старый уровень API с более мягким доменом SELinux, полезен для сравнения. Оба ставятся на Android 8.0 и новее.

* Release (APK): https://github.com/VD171/VD-Infos/releases
* https://github.com/VD171/VD-Infos
* https://xdaforums.com/t/VD-Infos.4097379/
* https://t.me/RootDetected
* https://t.me/BlankAssistance

## Для ИИ-агентов: полный отчёт в JSON

Сделано для ИИ-агентов и LLM-инструментов, которые работают с устройством через shell (ассистенты для программирования, скрипты анализа). Вместо чтения скриншотов агент читает весь результат одной командой:

```sh
su -c cat /data/data/ru.vd171.vdinfos/files/last_snapshot.json
```

* **Что внутри:** каждый зонд с вердиктом и значением всех линз, в том же JSON, что и кнопка экспорта. Экран показывает только первое чтение свёрнутого элемента; в файле есть все.
* **Когда обновляется:** в конце каждого сканирования, поэтому сначала запустите сканирование; в файле всегда самое свежее.
* **Требование:** root, потому что файл лежит в приватном хранилище приложения.

## Архитектура

```
core/model      неизменяемый домен: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          NativeBridge (JNI) - SystemProps (рефлексия) - PropCatalog (данные)
                SemanticProbes - IntegrityProbes - ProbeRegistry
engine/         ProbeEngine - fan-out на корутинах, отдаёт результаты как Flow
data/           SnapshotStore (хранение + diff) - Exporter (экспорт JSON/текст)
ui/             Jetpack Compose, Material 3, динамический цвет, живой прогресс
cpp/            native_probes.cpp - нативная линза, без зависимостей
```

* **Параллелизм**: 996 зондов разворачиваются на дефолтном диспетчере с ограниченным числом разрешений; результаты поступают в UI по мере готовности.
* **Ничего в фоне**: нет служб и нет запланированных сканов; приложение работает только пока открыто и само закрывается, когда простаивает.
* **Нативный слой**: маленькая `.so`, связанная по имени через `RegisterNatives`, намеренно крошечная, потому что это та часть, которую должно быть трудно обмануть.

## Участие в разработке

Большинству вкладов не нужен Kotlin: списки лежат обычным текстом в `VDInfos/app/src/main/assets/data/`. Добавьте или удалите строку и откройте pull request.

* `*_apps.txt` - по одному имени пакета в строке
* `props.txt` - каталог системных свойств, `КАТЕГОРИЯ<tab>ключ`
* `spoof_keys.txt` - матрица спуфа настроек, `ключ:ТИП`
* `known_certs.txt` - каталог сертификатов подписи, `SHA-256<tab>владелец` (полный хеш, из настоящего APK через `apksigner verify --print-certs`)
* другие файлы `.txt` - тоже по одной записи в строке (фрагменты имён модулей ядра, имена в `/data/local/tmp`)

`#` начинает комментарий; пустые строки игнорируются. Некоторые файлы содержат несколько полей, разделённых пробелом или табуляцией, в строке, либо необязательные маркеры (`S` и `@field` в `props.txt`); сохраняйте форму строк, уже имеющихся в файле. Вклады кодом тоже приветствуются. Внося вклад, вы соглашаетесь, что он распространяется под AGPL-3.0-or-later этого проекта.

## Каталог

Справочники по сокрытию и обнаружению root (руководства, модули, фреймворки, детекторы) собраны в отдельном каталоге: [CATALOG.md](CATALOG.md).

## Благодарности

Особая благодарность **[frknkrc44](https://github.com/frknkrc44)**, разработчику **[HMA-OSS](https://github.com/frknkrc44/HMA-OSS)**, за вложенную заботу. HMA-OSS - это то место, где применяются несколько исправлений, предлагаемых этим приложением: скрытие целевых приложений и настройка пресетов спуфа - и каждое упоминание его в приложении ведёт обратно к исходнику.

Его ссылки, каналы и как его поддержать: [HMA-OSS.md](HMA-OSS.md).

## Смотрите также

Родственные проекты того же автора:

- **[VDGoogle](https://github.com/VD171/VD-Google)**: ваши сервисы Google (RCS, Wallet, аттестации Play Integrity), прочитанные на устройстве без интернета.
- **[VDBackup](https://github.com/VD171/VD-Backup)**: полное резервное копирование и восстановление приложений и данных с правами root, полностью управляемое из WebUI.
- **[COPG-VD](https://github.com/VD171/COPG-VD)**: спуфер устройства, который превращает свежий fingerprint в связный, целостный профиль.

Pixel `build.prop`: **[@VictorGabriel7Dev/build.prop](https://github.com/VictorGabriel7Dev/build.prop)** (fork of @T3SL4).

## Контакты

* https://vd171.ru
* https://vd.priv8.ru
* **Telegram:** @VD_Priv8 https://t.me/VD_Priv8
* **Channel:** @Priv8_VD https://t.me/Priv8_VD
* **Discord:** @VD.Priv8 https://discord.com/users/1296831918989639721
* **E-mail:** vd.priv8@pm.me
* **XDA-Developers:** @VD171 https://xdaforums.com/m/vd171.4699873/
* **XDA thread:** https://xdaforums.com/t/VD-Infos.4097379/
* **GitHub:** @VD171 https://github.com/VD171

## Лицензия

**GNU AGPL-3.0-or-later** - см. [LICENSE](LICENSE). Копилефт, включая сетевую оговорку: любой, кто запускает изменённую версию (даже как сервис), обязан предоставить её исходный код. Выбрана намеренно для исследовательского/анти-детекционного инструмента, чтобы форки оставались открытыми.

## Товарные знаки

VD Infos - независимый, неофициальный инструмент для исследований и диагностики. Он не связан, не одобрен и не спонсируется ни одним из поставщиков, чьё программное обеспечение, сервисы или знаки он читает, обнаруживает или называет (включая Google LLC и Android Open Source Project). "Android", "Google", "Google Play", "Play Integrity", "Widevine" и все прочие названия продуктов, логотипы и товарные знаки являются собственностью их владельцев. VD Infos лишь читает то, что уже есть на вашем устройстве, и ничего никуда не отправляет.

## Tags

`android` `privacy` `security` `root-detection` `hook-detection` `spoof-detection` `anti-detection` `tamper-detection` `device-fingerprint` `magisk` `kernelsu` `lsposed` `play-integrity` `attestation` `tee` `keystore` `scanner` `diagnostics` `reverse-engineering` `kotlin`
