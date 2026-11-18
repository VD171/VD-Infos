> **Languages:** [English](../CONTRIBUTING.md) · [العربية](../ar/CONTRIBUTING.md) · [Čeština](../cs/CONTRIBUTING.md) · [Deutsch](../de/CONTRIBUTING.md) · [Español](../es/CONTRIBUTING.md) · [فارسی](../fa/CONTRIBUTING.md) · [Français](../fr/CONTRIBUTING.md) · [हिन्दी](../hi/CONTRIBUTING.md) · [Bahasa Indonesia](../id/CONTRIBUTING.md) · [Italiano](../it/CONTRIBUTING.md) · [日本語](../ja/CONTRIBUTING.md) · [한국어](../ko/CONTRIBUTING.md) · [Nederlands](../nl/CONTRIBUTING.md) · Polski · [Português](../pt/CONTRIBUTING.md) · [Русский](../ru/CONTRIBUTING.md) · [Svenska](../sv/CONTRIBUTING.md) · [ไทย](../th/CONTRIBUTING.md) · [Türkçe](../tr/CONTRIBUTING.md) · [Українська](../uk/CONTRIBUTING.md) · [Tiếng Việt](../vi/CONTRIBUTING.md) · [中文](../zh/CONTRIBUTING.md)

---

# Współtworzenie

Dziękujemy za zainteresowanie VD Infos. Wkłady są mile widziane: listy wykrywania,
tłumaczenia, zgłoszenia błędów, dokumentacja i kod. Większość wkładów nie wymaga Kotlina.

## Najłatwiejszy wkład: listy

Większość tego, czego aplikacja szuka, mieści się jako **listy zwykłego tekstu** pod
`VDInfos/app/src/main/assets/data/`, a nie jako kod. Dodanie lub usunięcie celu to edycja
jednej linii i otwarcie pull requesta:

- `*_apps.txt` - jedna nazwa pakietu na linię.
- `props.txt` - katalog właściwości systemowych, `CATEGORY<tab>key`.
- `spoof_keys.txt` - macierz ustawień, `key:TYPE`.
- `known_certs.txt` - katalog certyfikatów podpisujących, `SHA-256<tab>owner` (pełny
  skrót, pobrany z prawdziwego APK przez `apksigner verify --print-certs`).
- inne pliki `.txt` - jeden wpis na linię (fragmenty nazw modułów jądra, nazwy plików
  i tak dalej).

`#` rozpoczyna komentarz, a puste linie są ignorowane. Niektóre pliki pakują kilka pól oddzielonych spacją
lub tabulatorem na linię, albo opcjonalne znaczniki; zachowaj kształt linii już
w pliku.

## Zasady podstawowe

- **VD Infos odczytuje prawdę i nigdy niczego nie fałszuje.** Raportuje to, co każda metoda
  zwróciła, dokładnie. Nie łagodź odczytu, aby wyglądał ładniej, i nie dodawaj
  niczego, co zmieniałoby wartość zamiast ją odczytywać.
- **Prywatność jest absolutna.** Wszystko działa na urządzeniu. Nigdy nie dodawaj wywołania sieciowego ani
  niczego, co wysyła odczyt poza urządzenie.
- **Eksporty pozostają maskowane.** Wartości noszące tożsamość są maskowane w udostępnionym raporcie. Zachowaj
  to tak.

## Tłumaczenia

Tłumaczenia są wartościowym wkładem. Readme jest tłumaczone per język
(`README.<lang>.md`), ciągi aplikacji per język, a ta dokumentacja pod
`docs/<lang>/`. Zachowaj spójność terminologii w obrębie języka, dopasuj istniejący ton
i nigdy nie tłumacz maszynowo terminu technicznego, którego nie jesteś pewien.

## Zgłaszanie błędu

Otwórz zgłoszenie z modelem swojego urządzenia, wersją Androida oraz, jeśli dotyczy odczytu,
udostępnionym **eksportem** (jego wartości noszące tożsamość są maskowane). Eksport jest zbudowany dokładnie
do tego.

## Kod

- Buduj release, nigdy debug. Testuj na prawdziwym urządzeniu. Zobacz [Budowanie](BUILDING.md).
- Trzymaj komentarze poza kodem; nazwy wyjaśniają intencję. Jedynym nagłówkiem jest baner
  licencji na górze każdego pliku źródłowego.
- Dopasuj istniejący styl i trzymaj zmiany skupione.

## Licencja

Współtworząc, zgadzasz się, że twój wkład jest licencjonowany na warunkach projektu
**GNU AGPL-3.0-or-later**.

<!-- doc-footer -->

---

> _Ta dokumentacja została wygenerowana za pomocą Claude (Anthropic) i jest utrzymywana przez VD171. Poprawki i ulepszenia są mile widziane; zobacz [CONTRIBUTING.md](CONTRIBUTING.md)._
