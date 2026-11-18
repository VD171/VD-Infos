> **Languages:** [English](../AI-AGENTS.md) · [العربية](../ar/AI-AGENTS.md) · [Čeština](../cs/AI-AGENTS.md) · [Deutsch](../de/AI-AGENTS.md) · [Español](../es/AI-AGENTS.md) · [فارسی](../fa/AI-AGENTS.md) · [Français](../fr/AI-AGENTS.md) · [हिन्दी](../hi/AI-AGENTS.md) · [Bahasa Indonesia](../id/AI-AGENTS.md) · [Italiano](../it/AI-AGENTS.md) · [日本語](../ja/AI-AGENTS.md) · [한국어](../ko/AI-AGENTS.md) · [Nederlands](../nl/AI-AGENTS.md) · [Polski](../pl/AI-AGENTS.md) · [Português](../pt/AI-AGENTS.md) · [Русский](../ru/AI-AGENTS.md) · Svenska · [ไทย](../th/AI-AGENTS.md) · [Türkçe](../tr/AI-AGENTS.md) · [Українська](../uk/AI-AGENTS.md) · [Tiếng Việt](../vi/AI-AGENTS.md) · [中文](../zh/AI-AGENTS.md)

---

# VD Infos för AI-agenter

VD Infos är **byggd för att läsas av maskiner, inte bara av människor**. En metoddebugger för
enheter producerar en mängd strukturerade bevis - varje värde, läst genom varje metod, uppställt
för jämförelse - och de bevisen är långt mer användbara för en agent som data än som en skärmbild.
Så hela resultatet är tillgängligt som ett maskinläsbart dokument, och appen är utformad kring det
från grunden.

Om du är en AI-kodassistent, ett LLM-verktyg, ett analysskript eller någon automation som når
enheten genom ett skal, är den här sidan för dig.

## Varför den är agentvänlig

- **Ett dokument, inte en skärm.** Hela skanningen - varje sond, varje lins, varje domslut - är
  ett enda JSON-objekt. Ingen OCR, ingen skärmskrapning, inget tryckande genom hopfällda kort.
- **Samma data som gränssnittet har.** Filen är inte en sammanfattning. Den är exakt vad appen
  håller internt: skärmen visar den första avläsningen av ett hopfällt objekt, filen har dem alla.
- **Deterministisk form.** Strukturen är stabil och dokumenterad (se
  [EXPORT-FORMAT](EXPORT-FORMAT.md)), så en parser som skrivs en gång fortsätter fungera.
- **Inget nätverk, inget konto, inget tillstånd att hantera.** Appen läser på enheten och skriver
  en fil. Din agent läser filen. Det är hela kontraktet.
- **Offline av design.** Den behöver inget internet, så den körs likadant inuti en nedlåst eller
  luftgapad miljö som en agent kan arbeta i.

## Läsningen med ett kommando

Varje skanning skriver sin fullständiga rapport till appens privata lagring. Efter att en skanning
har körts läser en agent det fullständiga resultatet med ett enda kommando:

```sh
su -c cat /data/data/ru.vd171.vdinfos/files/last_snapshot.json
```

- **Vad den innehåller:** varje sond med sitt domslut och värdet från varje lins, i samma JSON som
  exportknappen.
- **När den uppdateras:** i slutet av varje skanning. Kör en skanning först; filen håller alltid
  den senaste.
- **Krav:** root, eftersom filen bor i appens privata lagring. Utan root, använd åtgärden **dela /
  spara som JSON** i appen istället och läs filen som agenten lämnas.

## Ett typiskt agentflöde

1. Säkerställ att appen är installerad (**SDK_35**-APK:n från
   [Releases](https://github.com/VD171/VD-Infos/releases)).
2. Utlös en skanning - en person trycker på skanna, eller en automation startar appens
   skanningsskärm - och vänta på att den slutförs.
3. Läs `files/last_snapshot.json` med kommandot ovan.
4. Tolka den med [exportformatet](EXPORT-FORMAT.md) som ditt schema: iterera sonderna, läs varje
   ens `verdict`, och ställ upp per-lins-`values`.
5. Agera på det strukturerade resultatet i ditt eget arbetsflöde.

Agenten avgör vad avläsningarna betyder för uppgiften i fråga; VD Infos tillhandahåller bevisen,
rent och fullständigt.

## Vad agenten får, och vad den inte får

- Rapporten bär **varje avläsning och varje domslut**, så en agent har hela bilden som appen
  byggde.
- Identitetsbärande värden (serienummer, IMEI, MAC-adresser, annons-ID:n och liknande) är
  **maskerade i den delade/exporterade rapporten**, så att en rapport som skickas runt förblir fri
  från personliga identifierare samtidigt som den behåller sina domslut och sin struktur. **Kopian
  på enheten**, `last_snapshot.json`, läsbar endast av appen eller root, behåller de verkliga
  värdena för en lokal agent som behöver dem.

## Dokumentation, skriven för att tolkas

Den här wikin är strukturerad för maskinläsning lika mycket som för människor: korta avsnitt,
stabila rubriker, explicita fältnamn, och en dedikerad [exportformat](EXPORT-FORMAT.md)-sida som en
agent kan behandla som ett schema. Om du bygger ett verktyg ovanpå VD Infos, börja där.

## Relaterade sidor

- [Exportformat](EXPORT-FORMAT.md) - JSON-schemat, fält för fält.
- [Användarguide](USER-GUIDE.md) - installera, skanna och dela.
- [Arkitektur](ARCHITECTURE.md) - hur appen är uppbyggd.

<!-- doc-footer -->

---

> _Denna dokumentation genererades med Claude (Anthropic) och underhålls av VD171. Rättelser och förbättringar välkomnas; se [CONTRIBUTING.md](CONTRIBUTING.md)._
