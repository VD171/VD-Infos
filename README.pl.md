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
  <b>Debuger metod urządzenia.</b><br>
  Odczytaj każdą wartość urządzenia każdą metodą zdolną ją odczytać i zestaw je obok siebie, abyś mógł porównać.
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

Android to niezwykle potężny i wszechstronny system operacyjny; nikt ci nie mówi, że wszystkie twoje dane osobowe i poufne informacje są dostępne dla każdej instalowanej aplikacji, a ochrona przed takim naruszaniem prywatności to obowiązek. VD Infos pokazuje przykład tego, co można przechwycić z twojego urządzenia, i robi to jako *debuger metod*: dla każdej informacji odczytuje wartość przez **każdą metodę zdolną ją odczytać**: `Build.*`, `SystemProperties`, `getprop`, natywne `__system_property_get`, menedżery systemowe, content providery, pliki, syscalle i sprzętowy attestation klucza (TEE), i zestawia je, abyś mógł porównać. Gdy jedna metoda różni się od pozostałych, coś pomiędzy przepisuje tę powierzchnię: framework hakujący, spoofer, shim resolvera. **ŻADNE INFORMACJE NIE SĄ PRZECHOWYWANE, WYSYŁANE ANI PRZESYŁANE DO ŻADNEGO PLIKU¹ CZY SERWERA**: wszystko działa na urządzeniu, wartości niosące tożsamość są maskowane, dopóki ich nie odsłonisz, a raport opuszcza urządzenie tylko wtedy, gdy sam go udostępnisz lub zapiszesz; jeśli chcesz, zablokuj dostęp do internetu firewallem albo po prostu go wyłącz.

> **¹ Wyjątek:** każde skanowanie zapisuje pełny raport w prywatnej pamięci aplikacji (`files/last_snapshot.json`). Odczytać go może tylko sama aplikacja lub root, następne skanowanie go nadpisuje i nigdy nie opuszcza urządzenia. Istnieje po to, by agenci AI mogli odczytać pełny wynik; zob. **Dla agentów AI: pełny raport w JSON**.

## Co sprawdza

Każdy element jest odczytywany przez każdą metodę zdolną go odczytać (Java SDK / natywnie / shell) i porównywany obok siebie:

* **~488 właściwości systemowych** odczytywanych PIĘCIOMA sposobami: `SystemProperties.get`, oba punkty wejścia bionic (`__system_property_read_callback` i 92-bajtowe `__system_property_get`), `getprop` uruchomione przez JVM oraz `getprop` uruchomione z kodu natywnego przez `popen`; polecenie shell uruchomione przez `ProcessBuilder` i to samo polecenie uruchomione bez JVM to nie ten sam punkt widzenia, bo pierwsze jest powierzchnią, którą framework ukrywający root przepisuje, a drugie nie.
* **Tożsamość urządzenia**: model, producent, marka, device, product, board, hardware, fingerprint, bootloader, build id/tags/type, każde pole `Build.*` względem wszystkich jego wariantów `ro.product.*` (system/vendor/odm), natywnie i przez shell.
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

## Dla agentów AI: pełny raport w JSON

Stworzone dla agentów AI i narzędzi LLM, które pracują na urządzeniu przez powłokę (asystenci programowania, skrypty analityczne). Zamiast czytać zrzuty ekranu, agent odczytuje cały wynik jednym poleceniem:

```sh
su -c cat /data/data/ru.vd171.vdinfos/files/last_snapshot.json
```

* **Co zawiera:** każdą sondę z werdyktem i wartością wszystkich soczewek, w tym samym JSON co przycisk eksportu. Ekran pokazuje tylko pierwszy odczyt zwiniętego elementu; plik ma wszystkie.
* **Kiedy się aktualizuje:** na końcu każdego skanowania, więc najpierw uruchom skanowanie; plik zawsze zawiera najnowsze.
* **Wymaganie:** root, bo plik leży w prywatnej pamięci aplikacji.

## Architektura

```
core/model      niezmienna domena: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          NativeBridge (JNI), SystemProps (refleksja), PropCatalog (dane)
                SemanticProbes, IntegrityProbes, ProbeRegistry
engine/         ProbeEngine: fan-out na korutynach, strumieniuje wyniki jako Flow
data/           SnapshotStore (trwałość + diff), Exporter (udostępnianie JSON/tekst)
ui/             Jetpack Compose, Material 3, dynamiczny kolor, postęp na żywo
cpp/            native_probes.cpp: natywna soczewka, bez zależności
```

* **Równoległość**: 996 sond rozkłada się na domyślnym dispatcherze z ograniczoną liczbą pozwoleń; wyniki napływają do UI w miarę ich pojawiania się.
* **Nic w tle**: brak usług i zaplanowanych skanów; aplikacja działa tylko gdy jest otwarta i sama się zamyka, gdy pozostaje bezczynna.
* **Warstwa natywna**: mała `.so`, wiązana po nazwie przez `RegisterNatives`, celowo malutka, bo to część, którą musi być trudno oszukać.

## Współtworzenie

Większość wkładów nie wymaga Kotlina: listy to zwykły tekst w `VDInfos/app/src/main/assets/data/`. Dodaj lub usuń jedną linię i otwórz pull request.

* `*_apps.txt`: jedna nazwa pakietu na linię
* `props.txt`: katalog właściwości systemowych, `KATEGORIA<tab>klucz`
* `spoof_keys.txt`: macierz spoof ustawień, `klucz:TYP`
* `known_certs.txt`: katalog certyfikatów podpisu, `SHA-256<tab>właściciel` (pełny hash, z prawdziwego APK przez `apksigner verify --print-certs`)
* pozostałe pliki `.txt`: również jeden wpis na wiersz (fragmenty nazw modułów jądra, nazwy w `/data/local/tmp`)

`#` rozpoczyna komentarz; puste linie są ignorowane. Niektóre pliki zawierają kilka pól oddzielonych spacją lub tabulatorem w wierszu albo opcjonalne znaczniki (`S` i `@field` w `props.txt`); zachowaj kształt wierszy już obecnych w pliku. Wkłady w kod również są mile widziane. Współtworząc, zgadzasz się, że Twoja praca jest udostępniana na AGPL-3.0-or-later tego projektu.

## Katalog

Odniesienia do ukrywania i wykrywania roota (przewodniki, moduły, frameworki, detektory) znajdują się w osobnym katalogu: [CATALOG.md](CATALOG.md).

## Podziękowania

Szczególne podziękowania dla **[frknkrc44](https://github.com/frknkrc44)**, twórcy **[HMA-OSS](https://github.com/frknkrc44/HMA-OSS)**, za włożoną staranność. HMA-OSS to miejsce, w którym stosuje się kilka poprawek sugerowanych przez tę aplikację (ukrywanie aplikacji docelowych i ustawianie presetów spoof), a każda wzmianka o nim w aplikacji odsyła do jego źródła.

Jego linki, kanały i jak go wesprzeć: [HMA-OSS.md](HMA-OSS.md).

## Dokumentacja

- **[Katalog](CATALOG.md)**: odniesienia dotyczące ukrywania roota i wykrywania (przewodniki, moduły, frameworki i detektory).
- **[Lista zmian](CHANGELOG.md)**: co zmieniło się w każdym wydaniu.
- **[HMA-OSS](HMA-OSS.md)**: moduł ukrywania aplikacji, na którym opierają się poprawki tej aplikacji (jego linki, kanały i jak go wesprzeć).
- **[Współtworzenie](README.md#contributing)**: jak dodawać cele wykrywania (najczęściej zwykłe listy tekstowe, bez potrzeby Kotlina).

<!-- /vd-docs -->

## Zobacz także

Projekty siostrzane tego samego autora:

- **[VDGoogle](https://github.com/VD171/VD-Google)**: twoje usługi Google (RCS, Wallet, atestacje Play Integrity), odczytane na urządzeniu bez internetu.
- **[VDBackup](https://github.com/VD171/VD-Backup)**: pełna kopia zapasowa i przywracanie aplikacji oraz danych jako root, sterowane w całości z WebUI.
- **[COPG-VD](https://github.com/VD171/COPG-VD)**: spoofer urządzenia, który zamienia świeży fingerprint w spójny, konsekwentny profil.
- **[VD-dropbear](https://github.com/VD171/VD-dropbear)**: samodzielny, rootowany demon SSH (Dropbear).

Pixel `build.prop`: **[@VictorGabriel7Dev/build.prop](https://github.com/VictorGabriel7Dev/build.prop)** (fork of @T3SL4).

## Kontakt

- https://vd171.ru
- https://vd.priv8.ru
- *Telegram:* @VD_Priv8 https://t.me/VD_Priv8
- *Channel:* @Priv8_VD https://t.me/Priv8_VD
- *Discord:* @VD.Priv8 https://discord.com/users/1296831918989639721
- *E-mail:* vd.priv8@pm.me
- *XDA-Developers:* @VD171 https://xdaforums.com/m/vd171.4699873/
- *XDA thread:* https://xdaforums.com/t/VD-Infos.4097379/
- *GitHub:* @VD171 https://github.com/VD171

## Licencja

**GNU AGPL-3.0-or-later**: zobacz [LICENSE](LICENSE). Copyleft, w tym klauzula sieciowa: każdy, kto uruchamia zmodyfikowaną wersję (nawet jako usługę), musi udostępnić jej źródło. Wybrana celowo dla narzędzia badawczego/anti-detekcyjnego, aby forki pozostały otwarte.

## Znaki towarowe

VD Infos to niezależne, nieoficjalne narzędzie badawcze i diagnostyczne. Nie jest powiązane z żadnym z dostawców, których oprogramowanie, usługi lub znaki odczytuje, wykrywa lub wymienia, ani przez nich wspierane czy sponsorowane (w tym Google LLC i Android Open Source Project). "Android", "Google", "Google Play", "Play Integrity", "Widevine" oraz wszystkie inne nazwy produktów, loga i znaki towarowe są własnością ich odpowiednich właścicieli. VD Infos odczytuje tylko to, co już jest na twoim urządzeniu, i niczego nigdzie nie wysyła.

## Tags

`android` `privacy` `security` `root-detection` `hook-detection` `spoof-detection` `anti-detection` `tamper-detection` `device-fingerprint` `magisk` `kernelsu` `lsposed` `play-integrity` `attestation` `tee` `keystore` `scanner` `diagnostics` `reverse-engineering` `kotlin`

<!-- doc-footer -->

---

> _Ta dokumentacja została wygenerowana za pomocą Claude (Anthropic) i jest utrzymywana przez VD171. Poprawki i ulepszenia są mile widziane; zobacz [Contributing](README.md#contributing)._
