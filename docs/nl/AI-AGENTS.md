> **Languages:** [English](../AI-AGENTS.md) · [العربية](../ar/AI-AGENTS.md) · [Čeština](../cs/AI-AGENTS.md) · [Deutsch](../de/AI-AGENTS.md) · [Español](../es/AI-AGENTS.md) · [فارسی](../fa/AI-AGENTS.md) · [Français](../fr/AI-AGENTS.md) · [हिन्दी](../hi/AI-AGENTS.md) · [Bahasa Indonesia](../id/AI-AGENTS.md) · [Italiano](../it/AI-AGENTS.md) · [日本語](../ja/AI-AGENTS.md) · [한국어](../ko/AI-AGENTS.md) · Nederlands · [Polski](../pl/AI-AGENTS.md) · [Português](../pt/AI-AGENTS.md) · [Русский](../ru/AI-AGENTS.md) · [Svenska](../sv/AI-AGENTS.md) · [ไทย](../th/AI-AGENTS.md) · [Türkçe](../tr/AI-AGENTS.md) · [Українська](../uk/AI-AGENTS.md) · [Tiếng Việt](../vi/AI-AGENTS.md) · [中文](../zh/AI-AGENTS.md)

---

# VD Infos voor AI-agents

VD Infos is **gebouwd om door machines gelezen te worden, niet alleen door mensen**. Een
apparaatmethode-debugger produceert veel gestructureerd bewijs - elke waarde, gelezen via
elke methode, naast elkaar gezet ter vergelijking - en dat bewijs is voor een agent veel
nuttiger als data dan als een screenshot. Dus is het hele resultaat beschikbaar als een
machineleesbaar document, en de app is daar vanaf de grond op ontworpen.

Als je een AI-codeerassistent, een LLM-tool, een analyseschript of welke automatisering
dan ook bent die het apparaat via een shell bereikt, dan is deze pagina voor jou.

## Waarom het agentvriendelijk is

- **Een document, geen scherm.** De hele scan - elke probe, elke lens, elk oordeel - is
  een enkel JSON-object. Geen OCR, geen scherm scrapen, geen door ingeklapte kaarten
  tikken.
- **Dezelfde data die de UI heeft.** Het bestand is geen samenvatting. Het is precies wat
  de app intern bewaart: het scherm toont de eerste meting van een ingeklapt item, het
  bestand heeft ze allemaal.
- **Deterministische vorm.** De structuur is stabiel en gedocumenteerd (zie
  [EXPORT-FORMAT](EXPORT-FORMAT.md)), zodat een parser die een keer geschreven is
  blijft werken.
- **Geen netwerk, geen account, geen staat om te beheren.** De app leest op het apparaat
  en schrijft een bestand. Jouw agent leest het bestand. Dat is het hele contract.
- **Offline van ontwerp.** Het heeft geen internet nodig, dus het draait hetzelfde
  binnen een afgeschermde of air-gapped omgeving waarin een agent zou kunnen opereren.

## Het lezen met een commando

Elke scan schrijft zijn volledige rapport naar de priveopslag van de app. Nadat een scan
is uitgevoerd, leest een agent het volledige resultaat met een enkel commando:

```sh
su -c cat /data/data/ru.vd171.vdinfos/files/last_snapshot.json
```

- **Wat het bevat:** elke probe met zijn oordeel en de waarde van elke lens, in dezelfde
  JSON als de exportknop.
- **Wanneer het bijwerkt:** aan het einde van elke scan. Voer eerst een scan uit; het
  bestand bevat altijd de meest recente.
- **Vereiste:** root, omdat het bestand in de priveopslag van de app leeft. Zonder root,
  gebruik de in-app actie **delen / opslaan als JSON** in plaats daarvan en lees het
  bestand dat aan de agent wordt gegeven.

## Een typische agentstroom

1. Zorg dat de app geinstalleerd is (de **SDK_35**-APK van
   [Releases](https://github.com/VD171/VD-Infos/releases)).
2. Start een scan - een persoon tikt op scannen, of een automatisering lanceert het
   scanscherm van de app - en wacht tot hij klaar is.
3. Lees `files/last_snapshot.json` met het commando hierboven.
4. Parseer het met het [exportformaat](EXPORT-FORMAT.md) als je schema: doorloop de
   probes, lees het `verdict` van elke, en zet de `values` per lens naast elkaar.
5. Handel op het gestructureerde resultaat in je eigen workflow.

De agent beslist wat de metingen betekenen voor de taak die voorligt; VD Infos levert het
bewijs, netjes en volledig.

## Wat de agent krijgt, en wat niet

- Het rapport bevat **elke meting en elk oordeel**, zodat een agent het volledige beeld
  heeft dat de app bouwde.
- Identiteitsdragende waarden (serienummers, IMEI, MAC-adressen, advertentie-ID's en
  dergelijke) worden **gemaskeerd in het gedeelde/geexporteerde rapport**, zodat een
  rondgereikt rapport vrij van persoonlijke identificatoren blijft terwijl het zijn
  oordelen en structuur behoudt. De **op het apparaat** staande `last_snapshot.json`,
  alleen leesbaar door de app of root, behoudt de echte waarden voor een lokale agent die
  ze nodig heeft.

## Documentatie, geschreven om geparseerd te worden

Deze wiki is net zozeer gestructureerd voor machinaal lezen als voor mensen: korte
secties, stabiele koppen, expliciete veldnamen, en een speciale
[exportformaat](EXPORT-FORMAT.md)-pagina die een agent als schema kan behandelen. Als je
een tool bovenop VD Infos bouwt, begin daar.

## Gerelateerde pagina's

- [Exportformaat](EXPORT-FORMAT.md) - het JSON-schema, veld voor veld.
- [Gebruikershandleiding](USER-GUIDE.md) - installeren, scannen en delen.
- [Architectuur](ARCHITECTURE.md) - hoe de app in elkaar zit.

<!-- doc-footer -->

---

> _Deze documentatie is gegenereerd met Claude (Anthropic) en wordt onderhouden door VD171. Correcties en verbeteringen zijn welkom; zie [CONTRIBUTING.md](CONTRIBUTING.md)._
