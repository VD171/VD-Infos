> **Languages:** [English](../SECURITY.md) · [العربية](../ar/SECURITY.md) · [Čeština](../cs/SECURITY.md) · [Deutsch](../de/SECURITY.md) · [Español](../es/SECURITY.md) · [فارسی](../fa/SECURITY.md) · [Français](../fr/SECURITY.md) · [हिन्दी](../hi/SECURITY.md) · [Bahasa Indonesia](../id/SECURITY.md) · [Italiano](../it/SECURITY.md) · [日本語](../ja/SECURITY.md) · [한국어](../ko/SECURITY.md) · [Nederlands](../nl/SECURITY.md) · [Polski](../pl/SECURITY.md) · [Português](../pt/SECURITY.md) · [Русский](../ru/SECURITY.md) · Svenska · [ไทย](../th/SECURITY.md) · [Türkçe](../tr/SECURITY.md) · [Українська](../uk/SECURITY.md) · [Tiếng Việt](../vi/SECURITY.md) · [中文](../zh/SECURITY.md)

---

# Säkerhetspolicy

VD Infos är ett integritetsverktyg som läser känsligt enhetstillstånd, så säkerhetsrapporter tas på
allvar.

## Rapportera en sårbarhet

Rapportera säkerhetsproblem **privat**, inte i ett offentligt ärende:

- E-posta **vd.priv8@pm.me**.
- Beskriv problemet, den berörda versionen, och hur man återskapar det.
- Om det gäller dataexponering, beskriv problemklassen; du behöver inte bifoga en fungerande exploit.

Du får ett svar så snart som möjligt. Ge ett rimligt tidsfönster att åtgärda problemet innan någon
offentlig avslöjning.

## Vad som räknas som ett säkerhetsproblem

På grund av vad VD Infos lovar är dessa särskilt viktiga:

- Allt som skulle få appen att nå nätverket, eller skicka någon avläsning av enheten.
- Allt som skulle läcka ett identitetsbärande värde till en delad eller sparad **export** (ett
  serienummer, IMEI, MAC-adress, annons-ID och liknande) trots maskeringen.
- Allt som låter en annan app läsa den privata rapportfilen som är tänkt att vara läsbar endast av
  appen själv eller root.

## Vad som inte är ett säkerhetsproblem

- En avläsning som ser "fel" ut men är det ärliga värdet en metod returnerade. VD Infos rapporterar
  vad varje metod sa; att läsa jämförelsen är ditt att göra. Se [användarguiden](USER-GUIDE.md).
- En avläsning markerad otillgänglig. Det betyder att en metod inte kunde läsa det värdet på din
  enhet, vilket i sig är en del av jämförelsen.

## Stödda versioner

Endast den senaste släppta versionen stöds. Återskapa på det senaste bygget innan du rapporterar.

<!-- doc-footer -->

---

> _Denna dokumentation genererades med Claude (Anthropic) och underhålls av VD171. Rättelser och förbättringar välkomnas; se [CONTRIBUTING.md](CONTRIBUTING.md)._
