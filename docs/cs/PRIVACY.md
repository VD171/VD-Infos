> **Languages:** [English](../PRIVACY.md) · [العربية](../ar/PRIVACY.md) · Čeština · [Deutsch](../de/PRIVACY.md) · [Español](../es/PRIVACY.md) · [فارسی](../fa/PRIVACY.md) · [Français](../fr/PRIVACY.md) · [हिन्दी](../hi/PRIVACY.md) · [Bahasa Indonesia](../id/PRIVACY.md) · [Italiano](../it/PRIVACY.md) · [日本語](../ja/PRIVACY.md) · [한국어](../ko/PRIVACY.md) · [Nederlands](../nl/PRIVACY.md) · [Polski](../pl/PRIVACY.md) · [Português](../pt/PRIVACY.md) · [Русский](../ru/PRIVACY.md) · [Svenska](../sv/PRIVACY.md) · [ไทย](../th/PRIVACY.md) · [Türkçe](../tr/PRIVACY.md) · [Українська](../uk/PRIVACY.md) · [Tiếng Việt](../vi/PRIVACY.md) · [中文](../zh/PRIVACY.md)

---

# Soukromí

Soukromí není funkce VD Infos. Je to celý smysl. Tato stránka přesně uvádí, co aplikace s
vašimi daty dělá a co ne, v jasných pojmech, které si můžete ověřit.

## Slib

- **Vše běží na zařízení.** Žádný naměřený údaj se neodesílá, nenahrává ani nepřenáší na
  žádný server. Aplikace funguje plně se zablokovaným internetem.
- **Žádný účet, žádné přihlášení, žádná práce na pozadí.** Aplikace čte jen, když je
  otevřená, nenechává nic běžet na pozadí a sama se zavře, když je ponechána nečinná.
- **Nic se nemění na vašem systému.** VD Infos pouze čte a porovnává; nikdy nezapisuje do
  stavu vašeho zařízení.

## Zpráva zůstává u vás

Zpráva opustí zařízení **jen tehdy, když ji výslovně sdílíte nebo uložíte**. Do té doby
existuje na dvou místech, obou na zařízení:

- **Na obrazovce**, zatímco je aplikace otevřená.
- V **soukromém úložišti** aplikace (`files/last_snapshot.json`), které může číst jen
  aplikace sama nebo root. Následující sken ho přepíše a nikdy neopustí zařízení. Existuje
  proto, aby nástroje a AI agenti mohli číst úplný výsledek; viz
  [průvodce AI agenty](AI-AGENTS.md).

## Exporty jsou maskované

Když zprávu sdílíte nebo uložíte, hodnoty nesoucí identitu - sériová čísla, IMEI, MAC
adresy, reklamní ID a podobně - jsou **maskované** před tím, než je zpráva zapsána, aby
zpráva, kterou vložíte do veřejného vlákna, neunikla vaše vlastní identifikátory. Verdikty
a struktura jsou zachovány; začerní se jen citlivé hodnoty. Soukromá kopie na zařízení
uchovává skutečné hodnoty pro vaše vlastní použití.

## Oprávnění

Aplikace se spouští bez jakéhokoli potřebného oprávnění. Některé naměřené údaje se stanou
dostupnými, až když udělíte odpovídající oprávnění (například přístup k využití), a udělit
ho je zcela vaše volba; kde je naměřený údaj nedostupný, aplikace to prostě řekne.

## Ověřte si to sami

Nemusíte nic z toho brát na víru:

- Zdrojový kód je veřejný: <https://github.com/VD171/VD-Infos>.
- Manifest je ve zdrojovém kódu; potvrďte, která oprávnění deklaruje.
- Zablokujte aplikaci přístup k síti firewallem nebo vypněte internet, a funguje dál
  stejně.

Viz [uživatelská příručka](USER-GUIDE.md) pro to, jak číst zprávu a co export obsahuje.

<!-- doc-footer -->

---

> _Tato dokumentace byla vytvořena pomocí Claude (Anthropic) a spravuje ji VD171. Opravy a vylepšení jsou vítány; viz [CONTRIBUTING.md](CONTRIBUTING.md)._
