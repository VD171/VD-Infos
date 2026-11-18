> **Languages:** [English](../SECURITY.md) · [العربية](../ar/SECURITY.md) · [Čeština](../cs/SECURITY.md) · [Deutsch](../de/SECURITY.md) · [Español](../es/SECURITY.md) · [فارسی](../fa/SECURITY.md) · [Français](../fr/SECURITY.md) · [हिन्दी](../hi/SECURITY.md) · [Bahasa Indonesia](../id/SECURITY.md) · [Italiano](../it/SECURITY.md) · [日本語](../ja/SECURITY.md) · [한국어](../ko/SECURITY.md) · Nederlands · [Polski](../pl/SECURITY.md) · [Português](../pt/SECURITY.md) · [Русский](../ru/SECURITY.md) · [Svenska](../sv/SECURITY.md) · [ไทย](../th/SECURITY.md) · [Türkçe](../tr/SECURITY.md) · [Українська](../uk/SECURITY.md) · [Tiếng Việt](../vi/SECURITY.md) · [中文](../zh/SECURITY.md)

---

# Beveiligingsbeleid

VD Infos is een privacytool die gevoelige apparaatstaat leest, dus
beveiligingsmeldingen worden serieus genomen.

## Een kwetsbaarheid melden

Meld beveiligingsproblemen alsjeblieft **prive**, niet in een openbare issue:

- E-mail **vd.priv8@pm.me**.
- Beschrijf het probleem, de getroffen versie, en hoe het te reproduceren.
- Als het gaat over gegevensblootstelling, beschrijf de klasse van het probleem; je hoeft
  geen werkende exploit mee te sturen.

Je krijgt zo snel mogelijk een reactie. Geef alsjeblieft een redelijke termijn om het
probleem op te lossen voordat er openbaar wordt gemaakt.

## Wat telt als een beveiligingsprobleem

Vanwege wat VD Infos belooft, zijn deze bijzonder belangrijk:

- Alles waardoor de app het netwerk zou bereiken, of een meting van het apparaat af zou
  sturen.
- Alles waardoor een identiteitsdragende waarde in een gedeelde of opgeslagen **export**
  zou lekken (een serienummer, IMEI, MAC-adres, advertentie-ID en dergelijke) ondanks de
  maskering.
- Alles waardoor een andere app het priverapportbestand kan lezen dat alleen door de app
  zelf of root leesbaar hoort te zijn.

## Wat geen beveiligingsprobleem is

- Een meting die er "fout" uitziet maar de eerlijke waarde is die een methode teruggaf.
  VD Infos rapporteert wat elke methode zei; het lezen van de vergelijking is aan jou. Zie
  de [gebruikershandleiding](USER-GUIDE.md).
- Een meting die als niet beschikbaar is gemarkeerd. Dat betekent dat een methode die
  waarde niet kon lezen op jouw apparaat, wat zelf deel is van de vergelijking.

## Ondersteunde versies

Alleen de laatst uitgebrachte versie wordt ondersteund. Reproduceer alsjeblieft op de
laatste build voordat je meldt.

<!-- doc-footer -->

---

> _Deze documentatie is gegenereerd met Claude (Anthropic) en wordt onderhouden door VD171. Correcties en verbeteringen zijn welkom; zie [CONTRIBUTING.md](CONTRIBUTING.md)._
