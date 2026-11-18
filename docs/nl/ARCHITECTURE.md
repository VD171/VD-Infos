> **Languages:** [English](../ARCHITECTURE.md) · [العربية](../ar/ARCHITECTURE.md) · [Čeština](../cs/ARCHITECTURE.md) · [Deutsch](../de/ARCHITECTURE.md) · [Español](../es/ARCHITECTURE.md) · [فارسی](../fa/ARCHITECTURE.md) · [Français](../fr/ARCHITECTURE.md) · [हिन्दी](../hi/ARCHITECTURE.md) · [Bahasa Indonesia](../id/ARCHITECTURE.md) · [Italiano](../it/ARCHITECTURE.md) · [日本語](../ja/ARCHITECTURE.md) · [한국어](../ko/ARCHITECTURE.md) · Nederlands · [Polski](../pl/ARCHITECTURE.md) · [Português](../pt/ARCHITECTURE.md) · [Русский](../ru/ARCHITECTURE.md) · [Svenska](../sv/ARCHITECTURE.md) · [ไทย](../th/ARCHITECTURE.md) · [Türkçe](../tr/ARCHITECTURE.md) · [Українська](../uk/ARCHITECTURE.md) · [Tiếng Việt](../vi/ARCHITECTURE.md) · [中文](../zh/ARCHITECTURE.md)

---

# Architectuur

Dit is een overzichtskaart van hoe VD Infos in elkaar zit. Het beschrijft de vorm van de
app, niet de catalogus van wat het leest: het punt van een methode-debugger is om elke
meting voor je neer te leggen, en het lezen ervan is aan jou om te doen op je eigen
apparaat.

## Ontwerpdoelen

- **Een waarde, elke methode.** Lees voor elk stukje informatie het via elke methode die
  het kan lezen en zet de metingen naast elkaar. Een debugger brengt bewijs naar boven;
  het velt geen oordeel.
- **Op het apparaat en stil.** Geen netwerk, geen account, geen achtergrondwerk. De app
  draait alleen terwijl hij open is.
- **Leesbaar door machines.** Het hele resultaat is een machineleesbaar document, zodat
  een tool hetzelfde beeld krijgt als een persoon. Zie [AI-agents](AI-AGENTS.md).
- **Klein waar het ertoe doet.** Het deel dat moeilijk te misleiden moet zijn, wordt
  bewust piepklein gehouden.

## De lenzen

Een *lens* is een manier om een waarde te lezen. Dezelfde waarde wordt via meerdere
lenzen gelezen zodat ze vergeleken kunnen worden:

| Lens | Wat het is |
|---|---|
| **Java / SDK** | De platform-API die een gewone app aanroept. |
| **Native / JNI** | Dezelfde waarde gelezen vanuit native code. |
| **Shell via JVM** | Een shell-commando dat door de JVM wordt gestart. |
| **Shell via JNI** | Hetzelfde commando gestart vanuit native code, een ander gezichtspunt. |
| **TEE / Attestation** | Hardware-ondersteunde key attestation, waar het van toepassing is. |

De lenzen naast elkaar zetten is het hele idee: wanneer ze het oneens zijn, antwoordt
iets tussen de app en de waarde verschillend aan verschillende aanroepers. Wat dat voor
een bepaalde waarde, op een bepaald apparaat, betekent, is aan jou om te lezen.

## Hoe een scan verloopt

```
core/model      immutable domain: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          the probes and the bridges they use (JNI, reflection, data lists)
engine/         ProbeEngine: parallel fan-out, streams results as they land
data/           SnapshotStore (persist the report), Exporter (share as JSON / text)
ui/             Jetpack Compose, Material 3, dynamic colour, live progress
cpp/            native_probes.cpp: the native lens, dependency-free
```

- **Parallellisme.** De probes waaieren uit over een dispatcher met een begrensd aantal
  permits; resultaten stromen de UI in zodra ze voltooid zijn, met een live
  voortgangsindicator.
- **Niets op de achtergrond.** Geen services en geen geplande scans. De app leest alleen
  terwijl hij open is en sluit zichzelf wanneer het inactief wordt gelaten.
- **De native laag.** Een kleine `.so`, per naam gebonden via `RegisterNatives`, bewust
  piepklein gehouden omdat het het deel is dat moeilijk te misleiden moet zijn.

## Het rapport

Elke scan produceert een `Snapshot`: het tijdstip waarop hij is genomen, de appversie, en
een lijst van resultaten, een per probe, elk met de metingen van elke lens en een oordeel
over hoe ze zich verhouden. De app bewaart dit na elke scan in de priveopslag en kan
dezelfde data exporteren. De structuur is gedocumenteerd als het
[exportformaat](EXPORT-FORMAT.md).

## Het uitbreiden

Het meeste van waar de app naar zoekt leeft als **platte-tekstlijsten** onder
`assets/data/`, niet als code. Een doel toevoegen of verwijderen is een regel bewerken.
Zie [Bijdragen](CONTRIBUTING.md).

## Wat VD Infos nooit doet

- Het verandert nooit iets aan je systeem. Het leest en vergelijkt alleen.
- Het praat nooit met het netwerk. Het werkt volledig offline.
- Het draait nooit op de achtergrond, plant nooit een scan, en houdt nooit een service in
  leven.
- Het stuurt nooit een meting ergens naartoe. Een rapport verlaat het apparaat alleen
  wanneer je het deelt of opslaat, met identiteitsdragende waarden gemaskeerd.

<!-- doc-footer -->

---

> _Deze documentatie is gegenereerd met Claude (Anthropic) en wordt onderhouden door VD171. Correcties en verbeteringen zijn welkom; zie [CONTRIBUTING.md](CONTRIBUTING.md)._
