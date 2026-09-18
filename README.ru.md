[English](README.md) | [Português](README.pt.md) | [Español](README.es.md) | [Italiano](README.it.md) | [Deutsch](README.de.md) | [Français](README.fr.md) | **Русский** | [Bahasa Indonesia](README.id.md) | [Türkçe](README.tr.md) | [Polski](README.pl.md) | [Nederlands](README.nl.md) | [Svenska](README.sv.md) | [Čeština](README.cs.md) | [Tiếng Việt](README.vi.md) | [中文](README.zh.md) | [日本語](README.ja.md) | [한국어](README.ko.md) | [فارسی](README.fa.md) | [हिन्दी](README.hi.md) | [العربية](README.ar.md) | [ไทย](README.th.md)

# VD Infos

*Отладчик методов*

[![License: AGPL-3.0-or-later](https://img.shields.io/badge/license-AGPL--3.0--or--later-blue.svg)](LICENSE)
[![Release](https://img.shields.io/github/v/release/VD171/VD-Infos)](https://github.com/VD171/VD-Infos/releases)
[![Downloads](https://img.shields.io/github/downloads/VD171/VD-Infos/total)](https://github.com/VD171/VD-Infos/releases)
[![Android 8.0+](https://img.shields.io/badge/Android-8.0%2B-3DDC84.svg)](https://github.com/VD171/VD-Infos/releases)
[![Languages](https://img.shields.io/badge/languages-21-orange.svg)](README.md)

<img src="images/vdinfos-01.png" height="420"/> <img src="images/vdinfos-02.png" height="420"/>

Android - очень мощная и гибкая операционная система; о чём никто не говорит - все ваши персональные данные и конфиденциальная информация доступны каждому приложению, которое вы устанавливаете, и защищаться от такого вторжения в приватность - обязанность. VD Infos показывает пример того, что можно считать с вашего устройства, и делает это как *отладчик методов*: для каждого факта он читает значение **всеми методами, способными его прочитать** - `Build.*`, `SystemProperties`, `getprop`, нативный `__system_property_get`, системные менеджеры, content provider'ы, файлы, syscall'ы и аппаратный attestation ключа (TEE) - и выстраивает их рядом для сравнения. Когда один метод расходится с остальными, что-то посередине переписывает эту поверхность: фреймворк-хук, спуфер, resolver-shim. **НИКАКАЯ ИНФОРМАЦИЯ НЕ СОХРАНЯЕТСЯ, НЕ ОТПРАВЛЯЕТСЯ И НЕ ПЕРЕДАЁТСЯ НИ В КАКОЙ ФАЙЛ ИЛИ НА СЕРВЕР** - всё работает на устройстве, значения, несущие идентичность, замаскированы, пока вы их не раскроете, а отчёт покидает устройство только когда вы явно делитесь им или сохраняете; при желании заблокируйте доступ в интернет фаерволом или просто отключите его.

## Что проверяет

Каждый пункт читается всеми методами, способными его прочитать (Java SDK / нативный / shell), и сравнивается бок о бок:

* **~493 системных свойств** читаются ПЯТЬЮ способами: `SystemProperties.get`, обе точки входа bionic (`__system_property_read_callback` и 92-байтная `__system_property_get`), `getprop`, запущенный из JVM, и `getprop`, запущенный из нативного кода через `popen` - shell-команда, запущенная `ProcessBuilder`, и та же команда без JVM - это не одна и та же точка обзора, потому что первая - поверхность, которую фреймворк сокрытия root переписывает, а вторая - нет.
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

## Языки

21 язык интерфейса: английский, португальский, испанский, итальянский, немецкий, французский, русский, индонезийский, турецкий, польский, нидерландский, шведский, чешский, вьетнамский, китайский, японский, корейский, персидский, хинди, арабский и тайский. Приложение предлагает единоразовый выбор языка при первом запуске (с опцией "Системный по умолчанию"), а в остальном следует языку системы.

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

* **Параллелизм**: 885 зонд разворачивается на дефолтном диспетчере с ограниченным числом разрешений; результаты поступают в UI по мере готовности.
* **Ничего в фоне**: нет служб и нет запланированных сканов; приложение работает только пока открыто и само закрывается, когда простаивает.
* **Нативный слой**: маленькая `.so`, связанная по имени через `RegisterNatives`, намеренно крошечная, потому что это та часть, которую должно быть трудно обмануть.

## Участие в разработке

Большинству вкладов не нужен Kotlin: списки лежат обычным текстом в `VDInfos/app/src/main/assets/data/`. Добавьте или удалите строку и откройте pull request.

* `*_apps.txt` - по одному имени пакета в строке
* `props.txt` - каталог системных свойств, `КАТЕГОРИЯ<tab>ключ`
* `spoof_keys.txt` - матрица спуфа настроек, `ключ:ТИП`
* другие файлы `.txt` - тоже по одной записи в строке (фрагменты имён модулей ядра, имена в `/data/local/tmp`)

`#` начинает комментарий; пустые строки игнорируются. Вклады кодом тоже приветствуются. Внося вклад, вы соглашаетесь, что он распространяется под AGPL-3.0-or-later этого проекта.

## Каталог

Справочники по сокрытию и обнаружению root (руководства, модули, фреймворки, детекторы) собраны в отдельном каталоге: [CATALOG.md](CATALOG.md).

## Благодарности

Особая благодарность **[frknkrc44](https://github.com/frknkrc44)**, разработчику **[HMA-OSS](https://github.com/frknkrc44/HMA-OSS)**, за вложенную заботу. HMA-OSS - это то место, где применяются несколько исправлений, предлагаемых этим приложением: скрытие целевых приложений и настройка пресетов спуфа - и каждое упоминание его в приложении ведёт обратно к исходнику.

Его ссылки, каналы и как его поддержать: [HMA-OSS.md](HMA-OSS.md).

## Контакты

* https://vd171.ru
* https://vd.priv8.ru
* **Telegram:** @VD_Priv8 https://t.me/VD_Priv8
* **Discord:** @VD.Priv8 https://discord.com/users/1296831918989639721
* **E-mail:** vd.priv8@pm.me
* **XDA-Developers:** @VD171 https://xdaforums.com/m/vd171.4699873/
* **GitHub:** @VD171 https://github.com/VD171

## Лицензия

**GNU AGPL-3.0-or-later** - см. [LICENSE](LICENSE). Копилефт, включая сетевую оговорку: любой, кто запускает изменённую версию (даже как сервис), обязан предоставить её исходный код. Выбрана намеренно для исследовательского/анти-детекционного инструмента, чтобы форки оставались открытыми.

---

`android` `privacy` `security` `root-detection` `hook-detection` `spoof-detection` `anti-detection` `tamper-detection` `device-fingerprint` `magisk` `kernelsu` `lsposed` `play-integrity` `attestation` `tee` `keystore` `scanner` `diagnostics` `reverse-engineering` `kotlin`
