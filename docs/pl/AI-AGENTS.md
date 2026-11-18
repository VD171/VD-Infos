> **Languages:** [English](../AI-AGENTS.md) · [العربية](../ar/AI-AGENTS.md) · [Čeština](../cs/AI-AGENTS.md) · [Deutsch](../de/AI-AGENTS.md) · [Español](../es/AI-AGENTS.md) · [فارسی](../fa/AI-AGENTS.md) · [Français](../fr/AI-AGENTS.md) · [हिन्दी](../hi/AI-AGENTS.md) · [Bahasa Indonesia](../id/AI-AGENTS.md) · [Italiano](../it/AI-AGENTS.md) · [日本語](../ja/AI-AGENTS.md) · [한국어](../ko/AI-AGENTS.md) · [Nederlands](../nl/AI-AGENTS.md) · Polski · [Português](../pt/AI-AGENTS.md) · [Русский](../ru/AI-AGENTS.md) · [Svenska](../sv/AI-AGENTS.md) · [ไทย](../th/AI-AGENTS.md) · [Türkçe](../tr/AI-AGENTS.md) · [Українська](../uk/AI-AGENTS.md) · [Tiếng Việt](../vi/AI-AGENTS.md) · [中文](../zh/AI-AGENTS.md)

---

# VD Infos dla agentów AI

VD Infos jest **zbudowany tak, aby był odczytywany przez maszyny, nie tylko przez ludzi**. Debugger metod
urządzenia wytwarza mnóstwo uporządkowanych dowodów - każdą wartość, odczytaną przez każdą
metodę, ustawioną do porównania - a te dowody są o wiele bardziej użyteczne dla agenta jako
dane niż jako zrzut ekranu. Dlatego cały wynik jest dostępny jako jeden czytelny dla maszyny
dokument, a aplikacja jest wokół tego zaprojektowana od podstaw.

Jeśli jesteś asystentem kodowania AI, narzędziem LLM, skryptem analitycznym lub jakąkolwiek automatyzacją,
która sięga do urządzenia przez powłokę, ta strona jest dla ciebie.

## Dlaczego jest przyjazny agentom

- **Jeden dokument, a nie ekran.** Całe skanowanie - każda sonda, każda soczewka, każdy
  werdykt - jest jednym obiektem JSON. Bez OCR, bez zeskrobywania ekranu, bez przeklikiwania przez
  zwinięte karty.
- **Te same dane, które ma interfejs.** Plik nie jest podsumowaniem. To dokładnie to, co aplikacja
  przechowuje wewnętrznie: ekran pokazuje pierwszy odczyt zwiniętego elementu, plik
  ma je wszystkie.
- **Deterministyczny kształt.** Struktura jest stabilna i udokumentowana (zobacz
  [EXPORT-FORMAT](EXPORT-FORMAT.md)), więc parser napisany raz działa dalej.
- **Bez sieci, bez konta, bez stanu do zarządzania.** Aplikacja odczytuje na urządzeniu i zapisuje
  plik. Twój agent odczytuje plik. To cały kontrakt.
- **Offline z założenia.** Nie potrzebuje internetu, więc działa tak samo wewnątrz zamkniętego
  lub odizolowanego środowiska, w którym agent może działać.

## Odczyt jednym poleceniem

Każde skanowanie zapisuje swój pełny raport w prywatnej pamięci aplikacji. Po uruchomieniu skanowania
agent odczytuje kompletny wynik jednym poleceniem:

```sh
su -c cat /data/data/ru.vd171.vdinfos/files/last_snapshot.json
```

- **Co zawiera:** każdą sondę z jej werdyktem i wartością z każdej soczewki,
  w tym samym JSON co przycisk eksportu.
- **Kiedy się aktualizuje:** na końcu każdego skanowania. Najpierw uruchom skanowanie; plik zawsze
  przechowuje najnowsze.
- **Wymaganie:** root, ponieważ plik mieści się w prywatnej pamięci aplikacji. Bez
  roota użyj zamiast tego wbudowanej akcji **udostępnij / zapisz jako JSON** i odczytaj plik,
  który agent otrzyma.

## Typowy przepływ agenta

1. Upewnij się, że aplikacja jest zainstalowana (plik APK **SDK_35** z
   [Releases](https://github.com/VD171/VD-Infos/releases)).
2. Wyzwól skanowanie - osoba dotyka skanowania albo automatyzacja uruchamia ekran skanowania
   aplikacji - i poczekaj, aż się zakończy.
3. Odczytaj `files/last_snapshot.json` powyższym poleceniem.
4. Sparsuj go z [formatem eksportu](EXPORT-FORMAT.md) jako schematem: iteruj po
   sondach, odczytaj `verdict` każdej z nich i ustaw obok siebie wartości `values` z poszczególnych soczewek.
5. Działaj na uporządkowanym wyniku we własnym przepływie pracy.

Agent decyduje, co odczyty oznaczają dla danego zadania; VD Infos dostarcza
dowody, czysto i w całości.

## Co agent dostaje, a czego nie

- Raport niesie **każdy odczyt i każdy werdykt**, więc agent ma pełny
  obraz, który aplikacja zbudowała.
- Wartości noszące tożsamość (numery seryjne, IMEI, adresy MAC, identyfikatory reklamowe i tym podobne)
  są **maskowane w udostępnionym/wyeksportowanym raporcie**, tak aby przekazywany dalej raport pozostawał wolny od
  osobistych identyfikatorów, zachowując swoje werdykty i strukturę. **Na urządzeniu**
  `last_snapshot.json`, odczytywalny tylko przez aplikację lub root, zachowuje prawdziwe wartości dla
  lokalnego agenta, który ich potrzebuje.

## Dokumentacja, napisana by była parsowana

To wiki jest zbudowane tak samo pod odczyt maszynowy, jak i pod ludzi: krótkie sekcje,
stabilne nagłówki, jawne nazwy pól i dedykowana strona [formatu eksportu](EXPORT-FORMAT.md),
którą agent może traktować jak schemat. Jeśli budujesz narzędzie oparte na VD Infos,
zacznij tam.

## Powiązane strony

- [Format eksportu](EXPORT-FORMAT.md) - schemat JSON, pole po polu.
- [Przewodnik użytkownika](USER-GUIDE.md) - instalacja, skanowanie i udostępnianie.
- [Architektura](ARCHITECTURE.md) - jak aplikacja jest zbudowana.

<!-- doc-footer -->

---

> _Ta dokumentacja została wygenerowana za pomocą Claude (Anthropic) i jest utrzymywana przez VD171. Poprawki i ulepszenia są mile widziane; zobacz [CONTRIBUTING.md](CONTRIBUTING.md)._
