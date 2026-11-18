> **Languages:** [English](../ARCHITECTURE.md) · [العربية](../ar/ARCHITECTURE.md) · [Čeština](../cs/ARCHITECTURE.md) · [Deutsch](../de/ARCHITECTURE.md) · [Español](../es/ARCHITECTURE.md) · [فارسی](../fa/ARCHITECTURE.md) · [Français](../fr/ARCHITECTURE.md) · [हिन्दी](../hi/ARCHITECTURE.md) · [Bahasa Indonesia](../id/ARCHITECTURE.md) · [Italiano](../it/ARCHITECTURE.md) · [日本語](../ja/ARCHITECTURE.md) · [한국어](../ko/ARCHITECTURE.md) · [Nederlands](../nl/ARCHITECTURE.md) · Polski · [Português](../pt/ARCHITECTURE.md) · [Русский](../ru/ARCHITECTURE.md) · [Svenska](../sv/ARCHITECTURE.md) · [ไทย](../th/ARCHITECTURE.md) · [Türkçe](../tr/ARCHITECTURE.md) · [Українська](../uk/ARCHITECTURE.md) · [Tiếng Việt](../vi/ARCHITECTURE.md) · [中文](../zh/ARCHITECTURE.md)

---

# Architektura

To jest wysokopoziomowa mapa tego, jak VD Infos jest zbudowany. Opisuje kształt
aplikacji, a nie katalog tego, co odczytuje: sednem debuggera metod jest postawienie każdego
odczytu przed tobą, a ich odczytanie masz wykonać sam na własnym urządzeniu.

## Cele projektowe

- **Jedna wartość, każda metoda.** Dla każdej informacji odczytaj ją przez każdą
  metodę, która potrafi ją odczytać, i ustaw odczyty obok siebie. Debugger wydobywa dowody; nie
  wydaje osądu.
- **Na urządzeniu i cicho.** Bez sieci, bez konta, bez pracy w tle. Aplikacja działa
  tylko wtedy, gdy jest otwarta.
- **Czytelne dla maszyn.** Cały wynik jest jednym czytelnym dla maszyny dokumentem, więc
  narzędzie dostaje ten sam obraz co człowiek. Zobacz [Agenci AI](AI-AGENTS.md).
- **Małe tam, gdzie to ważne.** Część, którą musi być trudno oszukać, jest celowo utrzymana
  maleńka.

## Soczewki

*Soczewka* to jeden sposób odczytania wartości. Ta sama wartość jest odczytywana przez kilka, aby
mogły być porównane:

| Soczewka | Czym jest |
|---|---|
| **Java / SDK** | API platformy, które wywołuje zwykła aplikacja. |
| **Native / JNI** | Ta sama wartość odczytana z kodu natywnego. |
| **Shell via JVM** | Polecenie powłoki uruchomione przez JVM. |
| **Shell via JNI** | To samo polecenie uruchomione z kodu natywnego, inny punkt obserwacji. |
| **TEE / Attestation** | Atestacja klucza wsparta sprzętowo, tam gdzie ma zastosowanie. |

Ustawienie soczewek obok siebie to cała idea: gdy się nie zgadzają, coś między aplikacją
a wartością odpowiada różnie różnym wywołującym. Co to oznacza dla danej
wartości, na danym urządzeniu, masz odczytać sam.

## Jak przebiega skanowanie

```
core/model      immutable domain: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          the probes and the bridges they use (JNI, reflection, data lists)
engine/         ProbeEngine: parallel fan-out, streams results as they land
data/           SnapshotStore (persist the report), Exporter (share as JSON / text)
ui/             Jetpack Compose, Material 3, dynamic colour, live progress
cpp/            native_probes.cpp: the native lens, dependency-free
```

- **Równoległość.** Sondy rozsyłają się po dyspozytorze z ograniczoną liczbą pozwoleń;
  wyniki strumieniują się do interfejsu w miarę ich ukończenia, z aktywnym wskaźnikiem postępu.
- **Nic w tle.** Bez usług i bez zaplanowanych skanowań. Aplikacja odczytuje tylko
  gdy jest otwarta i zamyka się sama, gdy pozostawiona bezczynnie.
- **Warstwa natywna.** Jedno małe `.so`, związane po nazwie przez `RegisterNatives`, utrzymane
  celowo maleńkie, ponieważ jest to część, którą musi być trudno oszukać.

## Raport

Każde skanowanie wytwarza `Snapshot`: czas, w którym zostało wykonane, wersję aplikacji oraz listę
wyników, po jednym na sondę, każdy z odczytami z każdej soczewki i werdyktem, jak
się one mają do siebie. Aplikacja utrwala to w prywatnej pamięci po każdym skanowaniu i może eksportować
te same dane. Struktura jest udokumentowana jako [format eksportu](EXPORT-FORMAT.md).

## Rozszerzanie

Większość tego, czego aplikacja szuka, mieści się jako **listy zwykłego tekstu** pod
`assets/data/`, a nie jako kod. Dodanie lub usunięcie celu to edycja jednej linii. Zobacz
[Współtworzenie](CONTRIBUTING.md).

## Czego VD Infos nigdy nie robi

- Nigdy nie zmienia niczego w twoim systemie. Tylko odczytuje i porównuje.
- Nigdy nie rozmawia z siecią. Działa w pełni offline.
- Nigdy nie działa w tle, nie planuje skanowania ani nie utrzymuje usługi przy życiu.
- Nigdy nie wysyła odczytu nigdzie. Raport opuszcza urządzenie tylko wtedy, gdy udostępnisz lub
  zapiszesz, z zamaskowanymi wartościami noszącymi tożsamość.

<!-- doc-footer -->

---

> _Ta dokumentacja została wygenerowana za pomocą Claude (Anthropic) i jest utrzymywana przez VD171. Poprawki i ulepszenia są mile widziane; zobacz [CONTRIBUTING.md](CONTRIBUTING.md)._
