> **Languages:** [English](../SECURITY.md) · [العربية](../ar/SECURITY.md) · [Čeština](../cs/SECURITY.md) · [Deutsch](../de/SECURITY.md) · [Español](../es/SECURITY.md) · [فارسی](../fa/SECURITY.md) · [Français](../fr/SECURITY.md) · [हिन्दी](../hi/SECURITY.md) · [Bahasa Indonesia](../id/SECURITY.md) · [Italiano](../it/SECURITY.md) · [日本語](../ja/SECURITY.md) · [한국어](../ko/SECURITY.md) · [Nederlands](../nl/SECURITY.md) · Polski · [Português](../pt/SECURITY.md) · [Русский](../ru/SECURITY.md) · [Svenska](../sv/SECURITY.md) · [ไทย](../th/SECURITY.md) · [Türkçe](../tr/SECURITY.md) · [Українська](../uk/SECURITY.md) · [Tiếng Việt](../vi/SECURITY.md) · [中文](../zh/SECURITY.md)

---

# Polityka bezpieczeństwa

VD Infos to narzędzie prywatności, które odczytuje wrażliwy stan urządzenia, więc zgłoszenia bezpieczeństwa są
traktowane poważnie.

## Zgłaszanie podatności

Prosimy zgłaszać problemy bezpieczeństwa **prywatnie**, a nie w publicznym zgłoszeniu:

- E-mail **vd.priv8@pm.me**.
- Opisz problem, dotkniętą wersję i jak go odtworzyć.
- Jeśli dotyczy ujawnienia danych, opisz klasę problemu; nie musisz dołączać
  działającego exploita.

Otrzymasz odpowiedź tak szybko, jak to możliwe. Prosimy o rozsądny okres na naprawę
problemu przed jakimkolwiek publicznym ujawnieniem.

## Co liczy się jako problem bezpieczeństwa

Ze względu na to, co VD Infos obiecuje, te są szczególnie ważne:

- Cokolwiek, co sprawiłoby, że aplikacja sięgnęłaby do sieci lub wysłałaby jakikolwiek odczyt poza
  urządzenie.
- Cokolwiek, co ujawniłoby wartość noszącą tożsamość do udostępnionego lub zapisanego **eksportu**
  (numer seryjny, IMEI, adres MAC, identyfikator reklamowy i tym podobne) mimo maskowania.
- Cokolwiek, co pozwala innej aplikacji odczytać prywatny plik raportu, który ma być
  odczytywalny tylko przez samą aplikację lub root.

## Co nie jest problemem bezpieczeństwa

- Odczyt, który wygląda "źle", ale jest uczciwą wartością, którą zwróciła metoda. VD Infos
  raportuje to, co powiedziała każda metoda; odczytanie porównania masz wykonać sam. Zobacz
  [przewodnik użytkownika](USER-GUIDE.md).
- Odczyt oznaczony jako niedostępny. To oznacza, że metoda nie mogła odczytać tej wartości na twoim
  urządzeniu, co samo w sobie jest częścią porównania.

## Wspierane wersje

Wspierana jest tylko najnowsza wydana wersja. Prosimy odtworzyć na najnowszej kompilacji
przed zgłoszeniem.

<!-- doc-footer -->

---

> _Ta dokumentacja została wygenerowana za pomocą Claude (Anthropic) i jest utrzymywana przez VD171. Poprawki i ulepszenia są mile widziane; zobacz [CONTRIBUTING.md](CONTRIBUTING.md)._
