> **Languages:** [English](../USER-GUIDE.md) · [العربية](../ar/USER-GUIDE.md) · [Čeština](../cs/USER-GUIDE.md) · [Deutsch](../de/USER-GUIDE.md) · [Español](../es/USER-GUIDE.md) · [فارسی](../fa/USER-GUIDE.md) · [Français](../fr/USER-GUIDE.md) · [हिन्दी](../hi/USER-GUIDE.md) · [Bahasa Indonesia](../id/USER-GUIDE.md) · [Italiano](../it/USER-GUIDE.md) · [日本語](../ja/USER-GUIDE.md) · [한국어](../ko/USER-GUIDE.md) · Nederlands · [Polski](../pl/USER-GUIDE.md) · [Português](../pt/USER-GUIDE.md) · [Русский](../ru/USER-GUIDE.md) · [Svenska](../sv/USER-GUIDE.md) · [ไทย](../th/USER-GUIDE.md) · [Türkçe](../tr/USER-GUIDE.md) · [Українська](../uk/USER-GUIDE.md) · [Tiếng Việt](../vi/USER-GUIDE.md) · [中文](../zh/USER-GUIDE.md)

---

# VD Infos gebruikershandleiding

VD Infos is een **apparaatmethode-debugger**. Het leest elke apparaatwaarde via elke
methode die haar kan lezen, en zet de metingen naast elkaar zodat je ze kunt
vergelijken. Deze handleiding behandelt het installeren van de app, het uitvoeren van
een scan, het lezen van het resultaat en het delen van een rapport. Het interpreteert
geen enkele specifieke bevinding voor je: de vergelijking is aan jou om te lezen, op je
eigen apparaat.

## Inhoudsopgave

1. [Wat VD Infos is](#1-wat-vd-infos-is)
2. [Wat het niet is](#2-wat-het-niet-is)
3. [Vereisten](#3-vereisten)
4. [Welke APK te installeren](#4-welke-apk-te-installeren)
5. [Installeren en eerste start](#5-installeren-en-eerste-start)
6. [Een scan uitvoeren](#6-een-scan-uitvoeren)
7. [Het resultaat lezen](#7-het-resultaat-lezen)
8. [Je rapport exporteren](#8-je-rapport-exporteren)
9. [Voor AI-agents](#9-voor-ai-agents)
10. [Taal en thema](#10-taal-en-thema)
11. [Privacy in het kort](#11-privacy-in-het-kort)
12. [Veelgestelde vragen](#12-veelgestelde-vragen)

## 1. Wat VD Infos is

Android geeft veel van de informatie van je apparaat door aan elke app die je
installeert. VD Infos laat je een voorbeeld zien van wat een gewone app kan lezen, en
het doet dat als een *methode-debugger*: voor elk stukje informatie leest het de waarde
via **elke methode die haar kan lezen** - platform-API's, systeemeigenschappen, native
calls, systeemmanagers, content providers, bestanden, syscalls, en hardware key
attestation (TEE) - en zet ze naast elkaar zodat je kunt
vergelijken. Het is een lens, geen oordeelmachine: het brengt de metingen naar boven en
laat het lezen ervan aan jou over.

## 2. Wat het niet is

- Het is **geen** cleaner, booster of verbergtool. Het verandert niets aan je systeem;
  het leest en vergelijkt alleen.
- Het is **geen** geslaagd/mislukt-score. Het laat je de ruwe metingen zien; wat ze
  voor jouw situatie betekenen, is aan jou om te beoordelen, op je apparaat.
- Het heeft **geen** internet nodig, en het werkt volledig met het internet
  geblokkeerd.

## 3. Vereisten

- Android 8.0 (API 26) of nieuwer.
- Er is geen speciale toestemming nodig om te starten. Sommige metingen komen pas
  beschikbaar als je de bijbehorende toestemming verleent (bijvoorbeeld toegang tot
  gebruiksgegevens), en sommige bestaan alleen op een geroot apparaat; waar een meting
  niet beschikbaar is, laat de app eenvoudigweg zien dat ze niet beschikbaar was.

## 4. Welke APK te installeren

Elke release publiceert **twee APK's**:

- **SDK_35** - richt zich op de strikte moderne sandbox. Dit is degene om te gebruiken.
- **SDK_27** - richt zich op een ouder API-niveau. Het is alleen nuttig voor
  vergelijking naast elkaar; de meeste mensen hebben het niet nodig.

Beide installeren op Android 8.0 en nieuwer. Download ze van de
[Releases](https://github.com/VD171/VD-Infos/releases)-pagina.

## 5. Installeren en eerste start

1. Download de **SDK_35**-APK van Releases en installeer hem (je moet mogelijk
   installaties vanuit je browser of bestandsbeheerder toestaan).
2. Open de app. Hij opent meteen in het scanscherm; er is geen account, geen aanmelding
   en geen instelling.
3. Verleen optioneel een toestemming waar de app om vraagt (zoals toegang tot
   gebruiksgegevens) om de metingen die ervan afhangen te ontgrendelen. Dit is
   volledig optioneel.

## 6. Een scan uitvoeren

Tik op de scanactie. De app waaiert zijn probes parallel uit en streamt resultaten in
de lijst zodra ze binnenkomen, met een live voortgangsindicator. Een scan voltooit
vanzelf; het leest alleen terwijl de app open is, houdt niets op de achtergrond draaien,
en sluit zichzelf wanneer het inactief wordt gelaten.

Om hem opnieuw uit te voeren, scan je opnieuw: elke scan vervangt het vorige resultaat.

## 7. Het resultaat lezen

Elk item is een kaart die de metingen van elke lens (de Java SDK-methode, de native
methode, de shell, enzovoort) naast elkaar zet. De kleur van de kaart vertelt je in een
oogopslag hoe die metingen zich tot elkaar verhouden:

- **Overeenkomst** - elke lens was het eens. De methoden gaven dezelfde waarde terug.
- **Afwijking** - de lenzen waren het niet eens. Een methode gaf iets anders terug dan
  de rest.
- **Informatie** - een meting die ter referentie wordt getoond, waarover niet wordt
  gestemd.
- **Enkel / niet beschikbaar** - slechts een lens kon haar lezen, of geen enkele.

De kaart toont altijd de volledige matrix van metingen, zodat je precies kunt zien
welke lens wat zei. Een ingeklapte kaart toont zijn eerste meting; klap hem uit om ze
allemaal te zien.

Verder spreekt de app niet voor je. **Wat een bepaalde afwijking op jouw apparaat
betekent, en wat eraan te doen, is aan jou om te onderzoeken** - dat is het hele punt
van een debugger: hij legt het bewijs voor je neer en laat jou het lezen. Gebruik het
zoekvak om op eigenschap of waarde te filteren, en de filterchips om de lijst te
versmallen.

## 8. Je rapport exporteren

De deel- en opslagacties geven je het **hele resultaat**, niet alleen wat op het scherm
staat:

- **Delen / opslaan als JSON** - het volledige rapport in machineleesbare vorm, dezelfde
  gegevens die de app intern bewaart. Dit is het formaat om te bewaren of aan een tool
  te voeren.
- **Delen als tekst** - een compacte, voor mensen leesbare samenvatting van de
  afwijkingen.

Identiteitsdragende waarden (serienummers, IMEI, MAC-adressen, advertentie-ID's en
dergelijke) worden **gemaskeerd in het geexporteerde rapport** zodat een rapport dat je
in een openbare thread plakt niet je eigen identificatoren lekt; de oordelen en de
structuur blijven behouden. De kopie op het apparaat die alleen jij (of root) kunt
lezen, behoudt de echte waarden.

## 9. Voor AI-agents

VD Infos is gebouwd om door tools gelezen te worden, niet alleen door mensen. Elke scan
schrijft zijn volledige rapport naar de priveopslag van de app, en de export is
dezelfde machineleesbare JSON. Een AI-codeerassistent of analyseschript dat op het
apparaat draait kan het hele resultaat met een enkel commando lezen in plaats van
screenshots te parseren. Zie de speciale [gids voor AI-agents](AI-AGENTS.md) en de
[exportformaat](EXPORT-FORMAT.md)-referentie.

## 10. Taal en thema

De app volgt je systeemtaal waar een vertaling bestaat, en je kunt er handmatig een
kiezen. Het gebruikt Material 3 met dynamische kleur en volgt je licht/donker-instelling.

## 11. Privacy in het kort

Alles draait op het apparaat. Geen enkele meting wordt ergens naartoe gestuurd, en een
rapport verlaat het apparaat alleen wanneer je het expliciet deelt of opslaat. Elke scan
bewaart zijn volledige rapport in de priveopslag van de app (`files/last_snapshot.json`)
zodat tools het kunnen lezen; alleen de app zelf of root kan dat bestand lezen, de
volgende scan overschrijft het, en het verlaat het apparaat nooit. Zie
[Privacy](PRIVACY.md) voor de volledige verklaring.

## 12. Veelgestelde vragen

**Verandert het iets aan mijn apparaat?** Nee. Het leest en vergelijkt alleen.

**Heeft het root nodig?** Nee. De app draait zonder root. Een handvol metingen bestaat
alleen op een geroot apparaat, en het priverapportbestand kan alleen door root of de app
zelf teruggelezen worden; al het andere werkt zonder root.

**Heeft het internet nodig?** Nee. Het werkt volledig offline; blokkeer zijn
netwerktoegang als je wilt.

**Welke APK moet ik gebruiken?** SDK_35. De SDK_27-build bestaat alleen voor
vergelijking.

**Waarom zeggen sommige items niet beschikbaar?** Omdat die methode die waarde niet kon
lezen op jouw apparaat - een ontbrekende toestemming, een sandboxregel, of hardware die
niet aanwezig is. Dat is zelf deel van de vergelijking.

**Waar meld ik een probleem of stel ik een detectiedoel voor?** Zie
[Bijdragen](CONTRIBUTING.md); de meeste detectielijsten zijn platte tekst en hebben geen
code nodig.

<!-- doc-footer -->

---

> _Deze documentatie is gegenereerd met Claude (Anthropic) en wordt onderhouden door VD171. Correcties en verbeteringen zijn welkom; zie [CONTRIBUTING.md](CONTRIBUTING.md)._
