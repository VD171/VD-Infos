> **Languages:** [English](../PRIVACY.md) · [العربية](../ar/PRIVACY.md) · [Čeština](../cs/PRIVACY.md) · [Deutsch](../de/PRIVACY.md) · [Español](../es/PRIVACY.md) · [فارسی](../fa/PRIVACY.md) · [Français](../fr/PRIVACY.md) · [हिन्दी](../hi/PRIVACY.md) · [Bahasa Indonesia](../id/PRIVACY.md) · [Italiano](../it/PRIVACY.md) · [日本語](../ja/PRIVACY.md) · [한국어](../ko/PRIVACY.md) · [Nederlands](../nl/PRIVACY.md) · [Polski](../pl/PRIVACY.md) · [Português](../pt/PRIVACY.md) · [Русский](../ru/PRIVACY.md) · Svenska · [ไทย](../th/PRIVACY.md) · [Türkçe](../tr/PRIVACY.md) · [Українська](../uk/PRIVACY.md) · [Tiếng Việt](../vi/PRIVACY.md) · [中文](../zh/PRIVACY.md)

---

# Integritet

Integritet är inte en funktion i VD Infos. Det är hela poängen. Den här sidan anger exakt vad appen
gör och inte gör med dina data, i enkla termer som du kan verifiera.

## Löftet

- **Allt körs på enheten.** Ingen avläsning skickas, laddas upp eller överförs till någon server.
  Appen fungerar fullt ut med internet blockerat.
- **Inget konto, ingen inloggning, inget bakgrundsarbete.** Appen läser endast medan den är öppen,
  håller ingenting igång i bakgrunden, och stänger sig själv när den lämnas inaktiv.
- **Ingenting ändras på ditt system.** VD Infos bara läser och jämför; den skriver aldrig till din
  enhets tillstånd.

## Rapporten stannar hos dig

En rapport lämnar enheten **endast när du uttryckligen delar eller sparar den**. Fram till dess finns
den på två platser, båda på enheten:

- **På skärmen**, medan appen är öppen.
- I appens **privata lagring** (`files/last_snapshot.json`), som endast appen själv eller root kan
  läsa. Nästa skanning skriver över den, och den lämnar aldrig enheten. Den finns så att verktyg och
  AI-agenter kan läsa det fullständiga resultatet; se [guiden för AI-agenter](AI-AGENTS.md).

## Exporter är maskerade

När du delar eller sparar en rapport är identitetsbärande värden - serienummer, IMEI, MAC-adresser,
annons-ID:n och liknande - **maskerade** innan rapporten skrivs, så att en rapport som du klistrar in
i en offentlig tråd inte läcker dina egna identifierare. Domsluten och strukturen bevaras; endast de
känsliga värdena redigeras bort. Den privata kopian på enheten behåller de verkliga värdena för din
egen användning.

## Behörigheter

Appen startar utan att någon behörighet krävs. Vissa avläsningar blir tillgängliga endast om du
beviljar den matchande behörigheten (till exempel användningsåtkomst), och att bevilja den är helt
ditt val; där en avläsning är otillgänglig säger appen helt enkelt det.

## Verifiera det själv

Du behöver inte ta något av detta på tro:

- Källan är offentlig: <https://github.com/VD171/VD-Infos>.
- Manifestet finns i källan; bekräfta vilka behörigheter den deklarerar.
- Blockera appens nätverksåtkomst med en brandvägg, eller stäng av internet, och den fortsätter
  fungera likadant.

Se [användarguiden](USER-GUIDE.md) för hur man läser en rapport och vad exporten innehåller.

<!-- doc-footer -->

---

> _Denna dokumentation genererades med Claude (Anthropic) och underhålls av VD171. Rättelser och förbättringar välkomnas; se [CONTRIBUTING.md](CONTRIBUTING.md)._
