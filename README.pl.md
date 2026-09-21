[English](README.md) | [Português](README.pt.md) | [Español](README.es.md) | [Italiano](README.it.md) | [Deutsch](README.de.md) | [Français](README.fr.md) | [Русский](README.ru.md) | [Bahasa Indonesia](README.id.md) | [Türkçe](README.tr.md) | **Polski** | [Nederlands](README.nl.md) | [Svenska](README.sv.md) | [Čeština](README.cs.md) | [Tiếng Việt](README.vi.md) | [中文](README.zh.md) | [日本語](README.ja.md) | [한국어](README.ko.md) | [فارسی](README.fa.md) | [हिन्दी](README.hi.md) | [العربية](README.ar.md) | [ไทย](README.th.md)

# VD Infos

*Debuger metod*

[![License: AGPL-3.0-or-later](https://img.shields.io/badge/license-AGPL--3.0--or--later-blue.svg)](LICENSE)
[![Release](https://img.shields.io/github/v/release/VD171/VD-Infos)](https://github.com/VD171/VD-Infos/releases)
[![Downloads](https://img.shields.io/github/downloads/VD171/VD-Infos/total)](https://github.com/VD171/VD-Infos/releases)
[![Android 8.0+](https://img.shields.io/badge/Android-8.0%2B-3DDC84.svg)](https://github.com/VD171/VD-Infos/releases)
[![Languages](https://img.shields.io/badge/languages-21-orange.svg)](README.md)

<img src="images/vdinfos-01.png" height="420"/> <img src="images/vdinfos-02.png" height="420"/>

Android to niezwykle potężny i wszechstronny system operacyjny; nikt ci nie mówi, że wszystkie twoje dane osobowe i poufne informacje są dostępne dla każdej instalowanej aplikacji, a ochrona przed takim naruszaniem prywatności to obowiązek. VD Infos pokazuje przykład tego, co można przechwycić z twojego urządzenia, i robi to jako *debuger metod*: dla każdej informacji odczytuje wartość przez **każdą metodę zdolną ją odczytać** - `Build.*`, `SystemProperties`, `getprop`, natywne `__system_property_get`, menedżery systemowe, content providery, pliki, syscalle i sprzętowy attestation klucza (TEE) - i zestawia je, abyś mógł porównać. Gdy jedna metoda różni się od pozostałych, coś pomiędzy przepisuje tę powierzchnię: framework hakujący, spoofer, shim resolvera. **ŻADNE INFORMACJE NIE SĄ PRZECHOWYWANE, WYSYŁANE ANI PRZESYŁANE DO ŻADNEGO PLIKU CZY SERWERA** - wszystko działa na urządzeniu, wartości niosące tożsamość są maskowane, dopóki ich nie odsłonisz, a raport opuszcza urządzenie tylko wtedy, gdy sam go udostępnisz lub zapiszesz; jeśli chcesz, zablokuj dostęp do internetu firewallem albo po prostu go wyłącz.

## Co sprawdza

Każdy element jest odczytywany przez każdą metodę zdolną go odczytać (Java SDK / natywnie / shell) i porównywany obok siebie:

* **~488 właściwości systemowych** odczytywanych PIĘCIOMA sposobami: `SystemProperties.get`, oba punkty wejścia bionic (`__system_property_read_callback` i 92-bajtowe `__system_property_get`), `getprop` uruchomione przez JVM oraz `getprop` uruchomione z kodu natywnego przez `popen` - polecenie shell uruchomione przez `ProcessBuilder` i to samo polecenie uruchomione bez JVM to nie ten sam punkt widzenia, bo pierwsze jest powierzchnią, którą framework ukrywający root przepisuje, a drugie nie.
* **Tożsamość urządzenia**: model, producent, marka, device, product, board, hardware, fingerprint, bootloader, build id/tags/type - każde pole `Build.*` względem wszystkich jego wariantów `ro.product.*` (system/vendor/odm), natywnie i przez shell.
* **Identyfikatory**: serial (wiele getterów), Android ID (settings i provider), GSF ID, advertising ID, IMEI/primary IMEI/MEID/IMSI/ICCID, user serial.
* **Telefonia**: powierzchnia TelephonyManager (operator, SIM, sieć, roaming...), SubscriptionManager (multi-SIM), cell info.
* **Sieć**: **MAC** na interfejs (`NetworkInterface` vs `/sys/class/net` vs shell), Wi-Fi (SSID/BSSID/IP/MAC), Bluetooth, DNS.
* **Jądro / proces / system**: `uname`, boot id, uptime, UID/PID, SELinux, strefa czasowa, locale, środowisko, uruchomione usługi/procesy.
* **Sprzęt / media**: CPU, pamięć, czujniki, ekran, aparaty, system features, GPU, ID DRM Widevine.
* **Pakiety / konta / WebView**: lista i liczba zainstalowanych pakietów, skróty podpisów per aplikacja, podpis tej aplikacji, introspekcja własnego pakietu, wyliczenia rozwiązywania intentów, konta, liczba wpisów rejestru połączeń, user agent.
* **Zgodność sandboxa**: powierzchnie, które sandbox powinien ZAMKNĄĆ (CID/serial eMMC, tożsamość UFS i SoC, `/proc/cmdline`, `/proc/1/*`, `/dev/kmsg`, `/system/build.prop`) oraz 22 usługi `dumpsys`. Tutaj odmowa jest odpowiedzią: `EACCES`/`DENIED` to zgodny odczyt, a urządzenie, które zamiast tego wydaje treść, jest niezgodne.
* **Identyfikatory reklamowe**: GAID z bindera Play Services plus cała rodzina kluczy (AAID, OAID/VAID i `pps_*` Huawei) przeszukana w trzech settings store.
* **Detektory root / hook / emulatora**: artefakty `su`/Magisk/KernelSU/APatch/Xposed/LSPosed/Riru, wstrzyknięte biblioteki w `/proc/self/maps`, znane pakiety, niebezpieczne i emulatorowe właściwości, verified boot state.

## Pobieranie i wsparcie

Każde wydanie publikuje dwa pliki APK: **SDK_35** celuje w surowy nowoczesny sandbox i to jego należy używać; **SDK_27** celuje w starszy poziom API, z luźniejszą domeną SELinux, przydatny do porównań. Oba instalują się na Androidzie 8.0 i nowszym.

* Release (APK): https://github.com/VD171/VD-Infos/releases
* https://github.com/VD171/VD-Infos
* https://xdaforums.com/t/VD-Infos.4097379/
* https://t.me/RootDetected
* https://t.me/BlankAssistance

## Języki

21 języków interfejsu: angielski, portugalski, hiszpański, włoski, niemiecki, francuski, rosyjski, indonezyjski, turecki, polski, niderlandzki, szwedzki, czeski, wietnamski, chiński, japoński, koreański, perski, hindi, arabski i tajski. Aplikacja oferuje jednorazowy wybór języka przy pierwszym uruchomieniu (z opcją "Domyślny systemu"), a poza tym podąża za językiem systemu.

## Architektura

```
core/model      niezmienna domena: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          NativeBridge (JNI) - SystemProps (refleksja) - PropCatalog (dane)
                SemanticProbes - IntegrityProbes - ProbeRegistry
engine/         ProbeEngine - fan-out na korutynach, strumieniuje wyniki jako Flow
data/           SnapshotStore (trwałość + diff) - Exporter (udostępnianie JSON/tekst)
ui/             Jetpack Compose, Material 3, dynamiczny kolor, postęp na żywo
cpp/            native_probes.cpp - natywna soczewka, bez zależności
```

* **Równoległość**: 907 sond rozkłada się na domyślnym dispatcherze z ograniczoną liczbą pozwoleń; wyniki napływają do UI w miarę ich pojawiania się.
* **Nic w tle**: brak usług i zaplanowanych skanów; aplikacja działa tylko gdy jest otwarta i sama się zamyka, gdy pozostaje bezczynna.
* **Warstwa natywna**: mała `.so`, wiązana po nazwie przez `RegisterNatives`, celowo malutka, bo to część, którą musi być trudno oszukać.

## Współtworzenie

Większość wkładów nie wymaga Kotlina: listy to zwykły tekst w `VDInfos/app/src/main/assets/data/`. Dodaj lub usuń jedną linię i otwórz pull request.

* `*_apps.txt` - jedna nazwa pakietu na linię
* `props.txt` - katalog właściwości systemowych, `KATEGORIA<tab>klucz`
* `spoof_keys.txt` - macierz spoof ustawień, `klucz:TYP`
* pozostałe pliki `.txt` - również jeden wpis na wiersz (fragmenty nazw modułów jądra, nazwy w `/data/local/tmp`)

`#` rozpoczyna komentarz; puste linie są ignorowane. Wkłady w kod również są mile widziane. Współtworząc, zgadzasz się, że Twoja praca jest udostępniana na AGPL-3.0-or-later tego projektu.

## Katalog

Odniesienia do ukrywania i wykrywania roota (przewodniki, moduły, frameworki, detektory) znajdują się w osobnym katalogu: [CATALOG.md](CATALOG.md).

## Podziękowania

Szczególne podziękowania dla **[frknkrc44](https://github.com/frknkrc44)**, twórcy **[HMA-OSS](https://github.com/frknkrc44/HMA-OSS)**, za włożoną staranność. HMA-OSS to miejsce, w którym stosuje się kilka poprawek sugerowanych przez tę aplikację - ukrywanie aplikacji docelowych i ustawianie presetów spoof - a każda wzmianka o nim w aplikacji odsyła do jego źródła.

Jego linki, kanały i jak go wesprzeć: [HMA-OSS.md](HMA-OSS.md).

## Kontakt

* https://vd171.ru
* https://vd.priv8.ru
* **Telegram:** @VD_Priv8 https://t.me/VD_Priv8
* **Discord:** @VD.Priv8 https://discord.com/users/1296831918989639721
* **E-mail:** vd.priv8@pm.me
* **XDA-Developers:** @VD171 https://xdaforums.com/m/vd171.4699873/
* **GitHub:** @VD171 https://github.com/VD171

## Licencja

**GNU AGPL-3.0-or-later** - zobacz [LICENSE](LICENSE). Copyleft, w tym klauzula sieciowa: każdy, kto uruchamia zmodyfikowaną wersję (nawet jako usługę), musi udostępnić jej źródło. Wybrana celowo dla narzędzia badawczego/anti-detekcyjnego, aby forki pozostały otwarte.

---

`android` `privacy` `security` `root-detection` `hook-detection` `spoof-detection` `anti-detection` `tamper-detection` `device-fingerprint` `magisk` `kernelsu` `lsposed` `play-integrity` `attestation` `tee` `keystore` `scanner` `diagnostics` `reverse-engineering` `kotlin`
