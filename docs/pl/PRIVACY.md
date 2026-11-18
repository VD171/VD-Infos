> **Languages:** [English](../PRIVACY.md) · [العربية](../ar/PRIVACY.md) · [Čeština](../cs/PRIVACY.md) · [Deutsch](../de/PRIVACY.md) · [Español](../es/PRIVACY.md) · [فارسی](../fa/PRIVACY.md) · [Français](../fr/PRIVACY.md) · [हिन्दी](../hi/PRIVACY.md) · [Bahasa Indonesia](../id/PRIVACY.md) · [Italiano](../it/PRIVACY.md) · [日本語](../ja/PRIVACY.md) · [한국어](../ko/PRIVACY.md) · [Nederlands](../nl/PRIVACY.md) · Polski · [Português](../pt/PRIVACY.md) · [Русский](../ru/PRIVACY.md) · [Svenska](../sv/PRIVACY.md) · [ไทย](../th/PRIVACY.md) · [Türkçe](../tr/PRIVACY.md) · [Українська](../uk/PRIVACY.md) · [Tiếng Việt](../vi/PRIVACY.md) · [中文](../zh/PRIVACY.md)

---

# Prywatność

Prywatność nie jest funkcją VD Infos. Jest całym sensem. Ta strona stwierdza dokładnie,
co aplikacja robi, a czego nie robi z twoimi danymi, w prostych słowach, które możesz zweryfikować.

## Obietnica

- **Wszystko działa na urządzeniu.** Żaden odczyt nie jest wysyłany, przesyłany ani przekazywany na żaden
  serwer. Aplikacja działa w pełni przy zablokowanym internecie.
- **Bez konta, bez logowania, bez pracy w tle.** Aplikacja odczytuje tylko wtedy, gdy jest otwarta,
  nie utrzymuje niczego działającego w tle i zamyka się sama, gdy pozostawiona bezczynnie.
- **Nic nie zmienia się w twoim systemie.** VD Infos tylko odczytuje i porównuje; nigdy nie zapisuje
  do stanu twojego urządzenia.

## Raport zostaje przy tobie

Raport opuszcza urządzenie **tylko wtedy, gdy wyraźnie go udostępnisz lub zapiszesz**. Do tego czasu
istnieje w dwóch miejscach, oba na urządzeniu:

- **Na ekranie**, gdy aplikacja jest otwarta.
- W **prywatnej pamięci** aplikacji (`files/last_snapshot.json`), którą tylko sama aplikacja
  lub root może odczytać. Następne skanowanie go nadpisuje i nigdy nie opuszcza on urządzenia.
  Istnieje po to, aby narzędzia i agenci AI mogli odczytać pełny wynik; zobacz
  [przewodnik dla agentów AI](AI-AGENTS.md).

## Eksporty są maskowane

Gdy udostępniasz lub zapisujesz raport, wartości noszące tożsamość - numery seryjne, IMEI, adresy
MAC, identyfikatory reklamowe i tym podobne - są **maskowane** przed zapisaniem raportu,
tak aby raport, który wkleisz do publicznego wątku, nie ujawnił twoich własnych identyfikatorów.
Werdykty i struktura są zachowane; redagowane są tylko wrażliwe wartości.
Prywatna kopia na urządzeniu zachowuje prawdziwe wartości do twojego własnego użytku.

## Uprawnienia

Aplikacja startuje bez żadnego wymaganego uprawnienia. Niektóre odczyty stają się dostępne tylko wtedy, gdy
przyznasz odpowiednie uprawnienie (na przykład dostęp do użycia), a przyznanie go jest całkowicie
twoim wyborem; tam, gdzie odczyt jest niedostępny, aplikacja po prostu to mówi.

## Zweryfikuj to sam

Nie musisz brać niczego z tego na wiarę:

- Źródło jest publiczne: <https://github.com/VD171/VD-Infos>.
- Manifest jest w źródle; potwierdź, które uprawnienia deklaruje.
- Zablokuj dostęp aplikacji do sieci zaporą sieciową lub wyłącz internet, a będzie działać
  tak samo.

Zobacz [przewodnik użytkownika](USER-GUIDE.md), aby dowiedzieć się, jak odczytać raport i co zawiera
eksport.

<!-- doc-footer -->

---

> _Ta dokumentacja została wygenerowana za pomocą Claude (Anthropic) i jest utrzymywana przez VD171. Poprawki i ulepszenia są mile widziane; zobacz [CONTRIBUTING.md](CONTRIBUTING.md)._
