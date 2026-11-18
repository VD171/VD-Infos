> **Languages:** [English](../CONTRIBUTING.md) · [العربية](../ar/CONTRIBUTING.md) · [Čeština](../cs/CONTRIBUTING.md) · [Deutsch](../de/CONTRIBUTING.md) · [Español](../es/CONTRIBUTING.md) · [فارسی](../fa/CONTRIBUTING.md) · [Français](../fr/CONTRIBUTING.md) · [हिन्दी](../hi/CONTRIBUTING.md) · [Bahasa Indonesia](../id/CONTRIBUTING.md) · [Italiano](../it/CONTRIBUTING.md) · [日本語](../ja/CONTRIBUTING.md) · [한국어](../ko/CONTRIBUTING.md) · Nederlands · [Polski](../pl/CONTRIBUTING.md) · [Português](../pt/CONTRIBUTING.md) · [Русский](../ru/CONTRIBUTING.md) · [Svenska](../sv/CONTRIBUTING.md) · [ไทย](../th/CONTRIBUTING.md) · [Türkçe](../tr/CONTRIBUTING.md) · [Українська](../uk/CONTRIBUTING.md) · [Tiếng Việt](../vi/CONTRIBUTING.md) · [中文](../zh/CONTRIBUTING.md)

---

# Bijdragen

Bedankt voor je interesse in VD Infos. Bijdragen zijn welkom: detectielijsten,
vertalingen, bugmeldingen, documentatie en code. De meeste bijdragen hebben geen Kotlin
nodig.

## De makkelijkste bijdrage: de lijsten

Het meeste van waar de app naar zoekt leeft als **platte-tekstlijsten** onder
`VDInfos/app/src/main/assets/data/`, niet als code. Een doel toevoegen of verwijderen is
een regel bewerken en een pull request openen:

- `*_apps.txt` - een pakketnaam per regel.
- `props.txt` - de systeemeigenschappen-catalogus, `CATEGORY<tab>key`.
- `spoof_keys.txt` - de instellingenmatrix, `key:TYPE`.
- `known_certs.txt` - de catalogus van ondertekeningscertificaten, `SHA-256<tab>owner`
  (de volledige hash, genomen uit een echte APK met `apksigner verify --print-certs`).
- andere `.txt`-bestanden - een item per regel (fragmenten van kernelmodulenamen,
  bestandsnamen, enzovoort).

Een `#` begint een commentaar en lege regels worden genegeerd. Sommige bestanden
verpakken een paar met spaties of tabs gescheiden velden per regel, of optionele
markeringen; behoud de vorm van de regels die al in het bestand staan.

## Grondregels

- **VD Infos leest de waarheid en vervalst nooit iets.** Het rapporteert precies wat elke
  methode teruggaf. Verzacht geen meting om het mooier te laten lijken, en voeg niets toe
  dat een waarde zou veranderen in plaats van lezen.
- **Privacy is absoluut.** Alles draait op het apparaat. Voeg nooit een netwerkaanroep
  toe, of iets dat een meting van het apparaat af stuurt.
- **Exports blijven gemaskeerd.** Identiteitsdragende waarden worden gemaskeerd in een
  gedeeld rapport. Houd het zo.

## Vertalingen

Vertalingen zijn een waardevolle bijdrage. De readme wordt per taal vertaald
(`README.<lang>.md`), de app-strings per taal, en deze documentatie onder `docs/<lang>/`.
Houd de terminologie consistent binnen een taal, sluit aan bij de bestaande toon, en
machinevertaal nooit een technische term waar je niet zeker van bent.

## Een bug melden

Open een issue met je apparaatmodel, Android-versie, en, als het over een meting gaat, een
gedeelde **export** (de identiteitsdragende waarden ervan zijn gemaskeerd). De export is
precies hiervoor gebouwd.

## Code

- Build release, nooit debug. Test op een echt apparaat. Zie [Bouwen](BUILDING.md).
- Houd commentaar uit de code; namen verklaren de intentie. De enige header is de
  licentiebanner bovenaan elk bronbestand.
- Sluit aan bij de bestaande stijl en houd wijzigingen gericht.

## Licentie

Door bij te dragen ga je ermee akkoord dat je bijdrage wordt gelicentieerd onder de
**GNU AGPL-3.0-or-later** van het project.

<!-- doc-footer -->

---

> _Deze documentatie is gegenereerd met Claude (Anthropic) en wordt onderhouden door VD171. Correcties en verbeteringen zijn welkom; zie [CONTRIBUTING.md](CONTRIBUTING.md)._
