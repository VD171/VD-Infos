> **Languages:** [English](../PRIVACY.md) · [العربية](../ar/PRIVACY.md) · [Čeština](../cs/PRIVACY.md) · [Deutsch](../de/PRIVACY.md) · [Español](../es/PRIVACY.md) · [فارسی](../fa/PRIVACY.md) · [Français](../fr/PRIVACY.md) · [हिन्दी](../hi/PRIVACY.md) · [Bahasa Indonesia](../id/PRIVACY.md) · [Italiano](../it/PRIVACY.md) · [日本語](../ja/PRIVACY.md) · [한국어](../ko/PRIVACY.md) · Nederlands · [Polski](../pl/PRIVACY.md) · [Português](../pt/PRIVACY.md) · [Русский](../ru/PRIVACY.md) · [Svenska](../sv/PRIVACY.md) · [ไทย](../th/PRIVACY.md) · [Türkçe](../tr/PRIVACY.md) · [Українська](../uk/PRIVACY.md) · [Tiếng Việt](../vi/PRIVACY.md) · [中文](../zh/PRIVACY.md)

---

# Privacy

Privacy is geen functie van VD Infos. Het is het hele punt. Deze pagina stelt precies wat
de app wel en niet met je gegevens doet, in duidelijke bewoordingen die je kunt
verifieren.

## De belofte

- **Alles draait op het apparaat.** Geen enkele meting wordt verstuurd, geupload of
  overgedragen naar een server. De app werkt volledig met het internet geblokkeerd.
- **Geen account, geen aanmelding, geen achtergrondwerk.** De app leest alleen terwijl
  hij open is, houdt niets op de achtergrond draaien, en sluit zichzelf wanneer het
  inactief wordt gelaten.
- **Er verandert niets aan je systeem.** VD Infos leest en vergelijkt alleen; het schrijft
  nooit naar de staat van je apparaat.

## Het rapport blijft bij jou

Een rapport verlaat het apparaat **alleen wanneer je het expliciet deelt of opslaat**.
Tot dan bestaat het op twee plaatsen, beide op het apparaat:

- **Op het scherm**, terwijl de app open is.
- In de **priveopslag** van de app (`files/last_snapshot.json`), die alleen de app zelf
  of root kan lezen. De volgende scan overschrijft het, en het verlaat het apparaat
  nooit. Het bestaat zodat tools en AI-agents het volledige resultaat kunnen lezen; zie de
  [gids voor AI-agents](AI-AGENTS.md).

## Exports zijn gemaskeerd

Wanneer je een rapport deelt of opslaat, worden identiteitsdragende waarden -
serienummers, IMEI, MAC-adressen, advertentie-ID's en dergelijke - **gemaskeerd** voordat
het rapport wordt geschreven, zodat een rapport dat je in een openbare thread plakt niet
je eigen identificatoren lekt. De oordelen en de structuur blijven behouden; alleen de
gevoelige waarden worden geredigeerd. De prive-kopie op het apparaat behoudt de echte
waarden voor je eigen gebruik.

## Toestemmingen

De app start zonder dat er toestemming nodig is. Sommige metingen komen pas beschikbaar
als je de bijbehorende toestemming verleent (bijvoorbeeld toegang tot gebruiksgegevens),
en het verlenen ervan is volledig jouw keuze; waar een meting niet beschikbaar is, zegt
de app dat eenvoudigweg.

## Verifieer het zelf

Je hoeft niets hiervan op vertrouwen aan te nemen:

- De broncode is openbaar: <https://github.com/VD171/VD-Infos>.
- Het manifest zit in de broncode; bevestig welke toestemmingen het declareert.
- Blokkeer de netwerktoegang van de app met een firewall, of zet het internet uit, en het
  blijft hetzelfde werken.

Zie de [gebruikershandleiding](USER-GUIDE.md) voor hoe je een rapport leest en wat de
export bevat.

<!-- doc-footer -->

---

> _Deze documentatie is gegenereerd met Claude (Anthropic) en wordt onderhouden door VD171. Correcties en verbeteringen zijn welkom; zie [CONTRIBUTING.md](CONTRIBUTING.md)._
