> **Languages:** [English](../USER-GUIDE.md) · [العربية](../ar/USER-GUIDE.md) · [Čeština](../cs/USER-GUIDE.md) · [Deutsch](../de/USER-GUIDE.md) · [Español](../es/USER-GUIDE.md) · [فارسی](../fa/USER-GUIDE.md) · [Français](../fr/USER-GUIDE.md) · [हिन्दी](../hi/USER-GUIDE.md) · [Bahasa Indonesia](../id/USER-GUIDE.md) · [Italiano](../it/USER-GUIDE.md) · [日本語](../ja/USER-GUIDE.md) · [한국어](../ko/USER-GUIDE.md) · [Nederlands](../nl/USER-GUIDE.md) · [Polski](../pl/USER-GUIDE.md) · [Português](../pt/USER-GUIDE.md) · [Русский](../ru/USER-GUIDE.md) · Svenska · [ไทย](../th/USER-GUIDE.md) · [Türkçe](../tr/USER-GUIDE.md) · [Українська](../uk/USER-GUIDE.md) · [Tiếng Việt](../vi/USER-GUIDE.md) · [中文](../zh/USER-GUIDE.md)

---

# VD Infos användarguide

VD Infos är en **metoddebugger för enheter**. Den läser varje enhetsvärde genom varje
metod som kan läsa det, och ställer upp avläsningarna sida vid sida så att du kan jämföra
dem. Den här guiden täcker att installera appen, köra en skanning, läsa resultatet och
dela en rapport. Den tolkar inte något specifikt resultat åt dig: jämförelsen är din att
läsa, på din egen enhet.

## Innehållsförteckning

1. [Vad VD Infos är](#1-vad-vd-infos-är)
2. [Vad den inte är](#2-vad-den-inte-är)
3. [Krav](#3-krav)
4. [Vilken APK du ska installera](#4-vilken-apk-du-ska-installera)
5. [Installation och första körningen](#5-installation-och-första-körningen)
6. [Köra en skanning](#6-köra-en-skanning)
7. [Läsa resultatet](#7-läsa-resultatet)
8. [Exportera din rapport](#8-exportera-din-rapport)
9. [För AI-agenter](#9-för-ai-agenter)
10. [Språk och tema](#10-språk-och-tema)
11. [Integritet i korthet](#11-integritet-i-korthet)
12. [Vanliga frågor](#12-vanliga-frågor)

## 1. Vad VD Infos är

Android lämnar en stor del av din enhets information till varje app du installerar. VD Infos
visar dig ett exempel på vad en vanlig app kan läsa, och den gör det som en *metoddebugger*:
för varje informationsdel läser den värdet genom **varje metod som kan läsa det** - `Build.*`,
`SystemProperties`, `getprop`, den nativa `__system_property_get`, systemhanterare, content
providers, filer, syscalls och hårdvarubaserad nyckelattestering (TEE) - och ställer upp dem
så att du kan jämföra. Den är en lins, inte en domsmaskin: den lyfter fram avläsningarna och
lämnar läsningen av dem till dig.

## 2. Vad den inte är

- Den är **inte** en rensare, en booster eller ett döljningsverktyg. Den ändrar ingenting på
  ditt system; den bara läser och jämför.
- Den är **inte** ett godkänt/underkänt-betyg. Den visar dig de råa avläsningarna; vad de
  betyder för din uppsättning är ditt att bedöma, på din enhet.
- Den behöver **inte** internet, och den fungerar fullt ut med internet blockerat.

## 3. Krav

- Android 8.0 (API 26) eller nyare.
- Ingen särskild behörighet krävs för att starta. Vissa avläsningar blir tillgängliga endast om
  du beviljar den matchande behörigheten (till exempel användningsåtkomst), och vissa finns
  endast på en rootad enhet; där en avläsning är otillgänglig visar appen helt enkelt att den
  inte var tillgänglig.

## 4. Vilken APK du ska installera

Varje release publicerar **två APK:er**:

- **SDK_35** - riktar sig mot den strikta moderna sandlådan. Det är den du ska använda.
- **SDK_27** - riktar sig mot en äldre API-nivå, med en lösare SELinux-domän. Den är användbar
  endast för jämförelse sida vid sida; de flesta behöver den inte.

Båda installeras på Android 8.0 och nyare. Ladda ner dem från
[Releases](https://github.com/VD171/VD-Infos/releases)-sidan.

## 5. Installation och första körningen

1. Ladda ner **SDK_35**-APK:n från Releases och installera den (du kan behöva tillåta
   installationer från din webbläsare eller filhanterare).
2. Öppna appen. Den öppnas direkt till skanningsskärmen; det finns inget konto, ingen
   inloggning och ingen installation.
3. Bevilja valfritt en behörighet som appen frågar om (till exempel användningsåtkomst) för att
   låsa upp de avläsningar som beror på den. Detta är helt valfritt.

## 6. Köra en skanning

Tryck på skanningsåtgärden. Appen fläktar ut sina sonder parallellt och strömmar resultat in i
listan allteftersom de landar, med en live-förloppsindikator. En skanning slutförs av sig själv;
den läser endast medan appen är öppen, håller ingenting igång i bakgrunden och stänger sig själv
när den lämnas inaktiv.

För att köra den igen, skanna igen: varje skanning ersätter det föregående resultatet.

## 7. Läsa resultatet

Varje objekt är ett kort som ställer upp avläsningarna från varje lins (Java SDK-metoden, den
nativa metoden, skalet och så vidare). Kortets färg talar om för dig, med en blick, hur de
avläsningarna förhåller sig till varandra:

- **Matchning** - varje lins var överens. Metoderna returnerade samma värde.
- **Divergens** - linserna var inte överens. En metod returnerade något annat än de andra.
- **Information** - en avläsning som visas för referens, inte röstad om.
- **Enstaka / otillgänglig** - endast en lins kunde läsa den, eller ingen kunde.

Kortet visar alltid den fullständiga matrisen av avläsningar, så att du kan se exakt vilken lins
som sa vad. Ett hopfällt kort visar sin första avläsning; fäll ut det för att se dem alla.

Så långt talar appen för dig. **Vad en given divergens betyder på din enhet, och vad man ska göra
åt den, är ditt att undersöka** - det är hela poängen med en debugger: den lägger bevisen framför
dig och låter dig läsa dem. Använd sökrutan för att filtrera på egenskap eller värde, och
filterchipsen för att smalna av listan.

## 8. Exportera din rapport

Dela- och spara-åtgärderna ger dig **hela resultatet**, inte bara det som är på skärmen:

- **Dela / spara som JSON** - den fullständiga rapporten i maskinläsbar form, samma data som
  appen håller internt. Detta är formatet att behålla eller att mata till ett verktyg.
- **Dela som text** - en kompakt människoläsbar sammanfattning av divergenserna.

Identitetsbärande värden (serienummer, IMEI, MAC-adresser, annons-ID:n och liknande) är
**maskerade i den exporterade rapporten** så att en rapport som du klistrar in i en offentlig
tråd inte läcker dina egna identifierare; domsluten och strukturen bevaras. Kopian på enheten som
bara du (eller root) kan läsa behåller de verkliga värdena.

## 9. För AI-agenter

VD Infos är byggd för att läsas av verktyg, inte bara av människor. Varje skanning skriver sin
fullständiga rapport till appens privata lagring, och exporten är samma maskinläsbara JSON. En
AI-kodassistent eller ett analysskript som körs på enheten kan läsa hela resultatet med ett
kommando istället för att tolka skärmbilder. Se den dedikerade
[guiden för AI-agenter](AI-AGENTS.md) och referensen för [exportformatet](EXPORT-FORMAT.md).

## 10. Språk och tema

Appen följer ditt systemspråk där en översättning finns, och du kan välja ett manuellt. Den
använder Material 3 med dynamisk färg och följer din ljus/mörk-inställning.

## 11. Integritet i korthet

Allt körs på enheten. Ingen avläsning skickas någonstans, och en rapport lämnar enheten endast
när du uttryckligen delar eller sparar den. Varje skanning behåller sin fullständiga rapport i
appens privata lagring (`files/last_snapshot.json`) så att verktyg kan läsa den; endast appen
själv eller root kan läsa den filen, nästa skanning skriver över den, och den lämnar aldrig
enheten. Se [Integritet](PRIVACY.md) för det fullständiga uttalandet.

## 12. Vanliga frågor

**Ändrar den något på min enhet?** Nej. Den bara läser och jämför.

**Behöver den root?** Nej. Appen körs utan root. En handfull avläsningar finns endast på en
rootad enhet, och den privata rapportfilen kan endast läsas tillbaka av root eller appen själv;
allt annat fungerar utan root.

**Behöver den internet?** Nej. Den fungerar fullt ut offline; blockera dess nätverksåtkomst om du
vill.

**Vilken APK ska jag använda?** SDK_35. SDK_27-bygget finns endast för jämförelse.

**Varför säger vissa objekt otillgänglig?** För att den metoden inte kunde läsa det värdet på din
enhet - en saknad behörighet, en sandlåderegel, eller hårdvara som inte finns. Det är i sig en del
av jämförelsen.

**Var rapporterar jag ett problem eller föreslår ett detekteringsmål?** Se
[Bidra](CONTRIBUTING.md); de flesta detekteringslistor är ren text och behöver ingen kod.

<!-- doc-footer -->

---

> _Denna dokumentation genererades med Claude (Anthropic) och underhålls av VD171. Rättelser och förbättringar välkomnas; se [CONTRIBUTING.md](CONTRIBUTING.md)._
