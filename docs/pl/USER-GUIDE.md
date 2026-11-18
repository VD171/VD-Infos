> **Languages:** [English](../USER-GUIDE.md) · [العربية](../ar/USER-GUIDE.md) · [Čeština](../cs/USER-GUIDE.md) · [Deutsch](../de/USER-GUIDE.md) · [Español](../es/USER-GUIDE.md) · [فارسی](../fa/USER-GUIDE.md) · [Français](../fr/USER-GUIDE.md) · [हिन्दी](../hi/USER-GUIDE.md) · [Bahasa Indonesia](../id/USER-GUIDE.md) · [Italiano](../it/USER-GUIDE.md) · [日本語](../ja/USER-GUIDE.md) · [한국어](../ko/USER-GUIDE.md) · [Nederlands](../nl/USER-GUIDE.md) · Polski · [Português](../pt/USER-GUIDE.md) · [Русский](../ru/USER-GUIDE.md) · [Svenska](../sv/USER-GUIDE.md) · [ไทย](../th/USER-GUIDE.md) · [Türkçe](../tr/USER-GUIDE.md) · [Українська](../uk/USER-GUIDE.md) · [Tiếng Việt](../vi/USER-GUIDE.md) · [中文](../zh/USER-GUIDE.md)

---

# Przewodnik użytkownika VD Infos

VD Infos to **debugger metod urządzenia**. Odczytuje każdą wartość urządzenia przez każdą
metodę, która potrafi ją odczytać, i ustawia odczyty obok siebie, abyś mógł je
porównać. Ten przewodnik obejmuje instalację aplikacji, uruchomienie skanowania, odczytanie wyniku
i udostępnienie raportu. Nie interpretuje za ciebie żadnego konkretnego wyniku: porównanie
należy odczytać samodzielnie, na własnym urządzeniu.

## Spis treści

1. [Czym jest VD Infos](#1-czym-jest-vd-infos)
2. [Czym nie jest](#2-czym-nie-jest)
3. [Wymagania](#3-wymagania)
4. [Który plik APK zainstalować](#4-który-plik-apk-zainstalować)
5. [Instalacja i pierwsze uruchomienie](#5-instalacja-i-pierwsze-uruchomienie)
6. [Uruchamianie skanowania](#6-uruchamianie-skanowania)
7. [Odczytywanie wyniku](#7-odczytywanie-wyniku)
8. [Eksport raportu](#8-eksport-raportu)
9. [Dla agentów AI](#9-dla-agentów-ai)
10. [Język i motyw](#10-język-i-motyw)
11. [Prywatność w skrócie](#11-prywatność-w-skrócie)
12. [Najczęściej zadawane pytania](#12-najczęściej-zadawane-pytania)

## 1. Czym jest VD Infos

Android przekazuje mnóstwo informacji o twoim urządzeniu każdej instalowanej aplikacji. VD Infos
pokazuje ci przykład tego, co może odczytać zwykła aplikacja, i robi to jako *debugger
metod*: dla każdej informacji odczytuje wartość przez **każdą metodę,
która potrafi ją odczytać** - `Build.*`, `SystemProperties`, `getprop`, natywne
`__system_property_get`, menedżery systemowe, dostawców treści, pliki, wywołania systemowe oraz
atestację kluczy sprzętowych (TEE) - i ustawia je obok siebie, abyś mógł je porównać. To soczewka,
a nie maszyna wydająca werdykty: wydobywa odczyty na wierzch, a ich odczytanie zostawia tobie.

## 2. Czym nie jest

- To **nie** jest narzędzie czyszczące, przyspieszające ani ukrywające. Nie zmienia niczego w twoim
  systemie; tylko odczytuje i porównuje.
- To **nie** jest ocena typu zaliczony/niezaliczony. Pokazuje surowe odczyty; co oznaczają
  dla twojej konfiguracji, masz ocenić sam, na swoim urządzeniu.
- **Nie** potrzebuje internetu i działa w pełni przy zablokowanym internecie.

## 3. Wymagania

- Android 8.0 (API 26) lub nowszy.
- Do uruchomienia nie jest wymagane żadne specjalne uprawnienie. Niektóre odczyty stają się dostępne tylko wtedy, gdy
  przyznasz odpowiednie uprawnienie (na przykład dostęp do użycia), a niektóre istnieją tylko
  na urządzeniu z rootem; tam, gdzie odczyt jest niedostępny, aplikacja po prostu pokazuje, że był
  niedostępny.

## 4. Który plik APK zainstalować

Każde wydanie publikuje **dwa pliki APK**:

- **SDK_35** - celuje w ścisłą nowoczesną piaskownicę. To właśnie ten należy użyć.
- **SDK_27** - celuje w starszy poziom API, z luźniejszą domeną SELinux. Jest przydatny
  tylko do porównania obok siebie; większość osób go nie potrzebuje.

Oba instalują się na Androidzie 8.0 i nowszym. Pobierz je ze strony
[Releases](https://github.com/VD171/VD-Infos/releases).

## 5. Instalacja i pierwsze uruchomienie

1. Pobierz plik APK **SDK_35** z Releases i zainstaluj go (może być konieczne zezwolenie na
   instalacje z przeglądarki lub menedżera plików).
2. Otwórz aplikację. Otwiera się od razu na ekranie skanowania; nie ma konta, nie ma
   logowania ani konfiguracji.
3. Opcjonalnie przyznaj uprawnienie, o które aplikacja prosi (takie jak dostęp do użycia), aby odblokować
   odczyty, które od niego zależą. Jest to całkowicie opcjonalne.

## 6. Uruchamianie skanowania

Dotknij akcji skanowania. Aplikacja rozsyła swoje sondy równolegle i strumieniuje wyniki do
listy w miarę ich napływania, z aktywnym wskaźnikiem postępu. Skanowanie kończy się samo; 
odczytuje tylko wtedy, gdy aplikacja jest otwarta, nie utrzymuje niczego działającego w tle i
zamyka się samo, gdy pozostawione bezczynnie.

Aby uruchomić je ponownie, skanuj ponownie: każde skanowanie zastępuje poprzedni wynik.

## 7. Odczytywanie wyniku

Każdy element to karta, która ustawia obok siebie odczyty z każdej soczewki (metoda Java SDK,
metoda natywna, powłoka i tak dalej). Kolor karty mówi ci, od razu, 
jak te odczyty mają się do siebie:

- **Zgodność** - każda soczewka się zgadzała. Metody zwróciły tę samą wartość.
- **Rozbieżność** - soczewki się nie zgadzały. Jedna metoda zwróciła coś innego
  niż pozostałe.
- **Informacja** - odczyt pokazany dla odniesienia, nad którym nie głosowano.
- **Pojedyncza / niedostępna** - tylko jedna soczewka mogła to odczytać, albo żadna nie mogła.

Karta zawsze pokazuje pełną macierz odczytów, abyś mógł zobaczyć dokładnie, która soczewka
powiedziała co. Zwinięta karta pokazuje swój pierwszy odczyt; rozwiń ją, aby zobaczyć wszystkie.

Na tyle aplikacja mówi za ciebie. **Co dana rozbieżność oznacza na twoim
urządzeniu i co z nią zrobić, masz zbadać sam** - na tym polega cały sens
debuggera: kładzie dowody przed tobą i pozwala ci je odczytać. Użyj pola
wyszukiwania, aby filtrować według właściwości lub wartości, i chipów filtrów, aby zawęzić listę.

## 8. Eksport raportu

Akcje udostępniania i zapisu przekazują ci **cały wynik**, a nie tylko to, co jest na ekranie:

- **Udostępnij / zapisz jako JSON** - kompletny raport w formie czytelnej dla maszyny, te same
  dane, które aplikacja przechowuje wewnętrznie. To format do przechowania lub do przekazania narzędziu.
- **Udostępnij jako tekst** - zwięzłe, czytelne dla człowieka podsumowanie rozbieżności.

Wartości noszące tożsamość (numery seryjne, IMEI, adresy MAC, identyfikatory reklamowe i tym podobne)
są **maskowane w wyeksportowanym raporcie**, tak aby raport, który wkleisz do publicznego wątku,
nie ujawnił twoich własnych identyfikatorów; werdykty i struktura są zachowane.
Kopia na urządzeniu, którą możesz odczytać tylko ty (lub root), zachowuje prawdziwe wartości.

## 9. Dla agentów AI

VD Infos jest zbudowany tak, aby był odczytywany przez narzędzia, nie tylko przez ludzi. Każde skanowanie zapisuje swój pełny
raport w prywatnej pamięci aplikacji, a eksport to ten sam czytelny dla maszyny JSON.
Asystent kodowania AI lub skrypt analityczny działający na urządzeniu może odczytać cały
wynik jednym poleceniem zamiast parsować zrzuty ekranu. Zobacz dedykowany
[przewodnik dla agentów AI](AI-AGENTS.md) oraz dokumentację [formatu eksportu](EXPORT-FORMAT.md).

## 10. Język i motyw

Aplikacja podąża za językiem twojego systemu tam, gdzie istnieje tłumaczenie, a możesz wybrać go
ręcznie. Używa Material 3 z dynamicznym kolorem i podąża za twoim ustawieniem jasny/ciemny.

## 11. Prywatność w skrócie

Wszystko działa na urządzeniu. Żaden odczyt nie jest nigdzie wysyłany, a raport opuszcza urządzenie
tylko wtedy, gdy wyraźnie go udostępnisz lub zapiszesz. Każde skanowanie przechowuje swój pełny raport w prywatnej
pamięci aplikacji (`files/last_snapshot.json`), aby narzędzia mogły go odczytać; tylko sama aplikacja
lub root może odczytać ten plik, następne skanowanie go nadpisuje i nigdy nie opuszcza on urządzenia.
Zobacz [Prywatność](PRIVACY.md), aby poznać pełne oświadczenie.

## 12. Najczęściej zadawane pytania

**Czy zmienia cokolwiek na moim urządzeniu?** Nie. Tylko odczytuje i porównuje.

**Czy potrzebuje roota?** Nie. Aplikacja działa bez roota. Garstka odczytów istnieje tylko
na urządzeniu z rootem, a prywatny plik raportu może być odczytany z powrotem tylko przez root lub samą
aplikację; wszystko inne działa bez roota.

**Czy potrzebuje internetu?** Nie. Działa w pełni offline; zablokuj jej dostęp do sieci, jeśli
chcesz.

**Którego pliku APK użyć?** SDK_35. Kompilacja SDK_27 istnieje tylko do porównania.

**Dlaczego niektóre elementy mówią niedostępne?** Ponieważ ta metoda nie mogła odczytać tej wartości na
twoim urządzeniu - brakujące uprawnienie, reguła piaskownicy albo sprzęt, którego nie ma.
To samo w sobie jest częścią porównania.

**Gdzie zgłosić problem lub zaproponować cel wykrywania?** Zobacz
[Współtworzenie](CONTRIBUTING.md); większość list wykrywania to zwykły tekst i nie wymaga kodu.

<!-- doc-footer -->

---

> _Ta dokumentacja została wygenerowana za pomocą Claude (Anthropic) i jest utrzymywana przez VD171. Poprawki i ulepszenia są mile widziane; zobacz [CONTRIBUTING.md](CONTRIBUTING.md)._
