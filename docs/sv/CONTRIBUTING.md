> **Languages:** [English](../CONTRIBUTING.md) · [العربية](../ar/CONTRIBUTING.md) · [Čeština](../cs/CONTRIBUTING.md) · [Deutsch](../de/CONTRIBUTING.md) · [Español](../es/CONTRIBUTING.md) · [فارسی](../fa/CONTRIBUTING.md) · [Français](../fr/CONTRIBUTING.md) · [हिन्दी](../hi/CONTRIBUTING.md) · [Bahasa Indonesia](../id/CONTRIBUTING.md) · [Italiano](../it/CONTRIBUTING.md) · [日本語](../ja/CONTRIBUTING.md) · [한국어](../ko/CONTRIBUTING.md) · [Nederlands](../nl/CONTRIBUTING.md) · [Polski](../pl/CONTRIBUTING.md) · [Português](../pt/CONTRIBUTING.md) · [Русский](../ru/CONTRIBUTING.md) · Svenska · [ไทย](../th/CONTRIBUTING.md) · [Türkçe](../tr/CONTRIBUTING.md) · [Українська](../uk/CONTRIBUTING.md) · [Tiếng Việt](../vi/CONTRIBUTING.md) · [中文](../zh/CONTRIBUTING.md)

---

# Bidra

Tack för ditt intresse för VD Infos. Bidrag är välkomna: detekteringslistor, översättningar,
buggrapporter, dokumentation och kod. De flesta bidrag behöver ingen Kotlin.

## Det enklaste bidraget: listorna

Det mesta av vad appen letar efter bor som **ren-text-listor** under
`VDInfos/app/src/main/assets/data/`, inte som kod. Att lägga till eller ta bort ett mål är att
redigera en rad och öppna en pull request:

- `*_apps.txt` - ett paketnamn per rad.
- `props.txt` - katalogen över systemegenskaper, `CATEGORY<tab>key`.
- `spoof_keys.txt` - inställningsmatrisen, `key:TYPE`.
- `known_certs.txt` - katalogen över signeringscertifikat, `SHA-256<tab>owner` (den fullständiga
  hashen, tagen från en riktig APK med `apksigner verify --print-certs`).
- andra `.txt`-filer - en post per rad (fragment av kärnmodulnamn, filnamn, och så vidare).

Ett `#` startar en kommentar och tomma rader ignoreras. Vissa filer packar några mellanslags- eller
tabbseparerade fält per rad, eller valfria markörer; behåll formen på raderna som redan finns i filen.

## Grundregler

- **VD Infos läser sanningen och förfalskar aldrig något.** Den rapporterar vad varje metod
  returnerade, exakt. Mjuka inte upp en avläsning för att få den att se snyggare ut, och lägg inte
  till något som skulle ändra ett värde snarare än att läsa det.
- **Integritet är absolut.** Allt körs på enheten. Lägg aldrig till ett nätverksanrop, eller något
  som skickar en avläsning av enheten.
- **Exporter förblir maskerade.** Identitetsbärande värden är maskerade i en delad rapport. Håll det
  så.

## Översättningar

Översättningar är ett värdefullt bidrag. Readme:n översätts per språk (`README.<lang>.md`),
appsträngarna per språk, och denna dokumentation under `docs/<lang>/`. Håll terminologin konsekvent
inom ett språk, matcha den befintliga tonen, och maskinöversätt aldrig en teknisk term du är osäker
på.

## Rapportera en bugg

Öppna ett ärende med din enhetsmodell, Android-version, och, om det handlar om en avläsning, en delad
**export** (dess identitetsbärande värden är maskerade). Exporten är byggd för exakt detta.

## Kod

- Bygg release, aldrig debug. Testa på en riktig enhet. Se [Bygga](BUILDING.md).
- Håll kommentarer ute ur koden; namn förklarar avsikt. Det enda huvudet är licensbannern högst upp i
  varje källfil.
- Matcha den befintliga stilen och håll ändringar fokuserade.

## Licens

Genom att bidra godkänner du att ditt bidrag licensieras under projektets
**GNU AGPL-3.0-or-later**.

<!-- doc-footer -->

---

> _Denna dokumentation genererades med Claude (Anthropic) och underhålls av VD171. Rättelser och förbättringar välkomnas; se [CONTRIBUTING.md](CONTRIBUTING.md)._
