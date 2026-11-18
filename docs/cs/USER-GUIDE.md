> **Languages:** [English](../USER-GUIDE.md) · [العربية](../ar/USER-GUIDE.md) · Čeština · [Deutsch](../de/USER-GUIDE.md) · [Español](../es/USER-GUIDE.md) · [فارسی](../fa/USER-GUIDE.md) · [Français](../fr/USER-GUIDE.md) · [हिन्दी](../hi/USER-GUIDE.md) · [Bahasa Indonesia](../id/USER-GUIDE.md) · [Italiano](../it/USER-GUIDE.md) · [日本語](../ja/USER-GUIDE.md) · [한국어](../ko/USER-GUIDE.md) · [Nederlands](../nl/USER-GUIDE.md) · [Polski](../pl/USER-GUIDE.md) · [Português](../pt/USER-GUIDE.md) · [Русский](../ru/USER-GUIDE.md) · [Svenska](../sv/USER-GUIDE.md) · [ไทย](../th/USER-GUIDE.md) · [Türkçe](../tr/USER-GUIDE.md) · [Українська](../uk/USER-GUIDE.md) · [Tiếng Việt](../vi/USER-GUIDE.md) · [中文](../zh/USER-GUIDE.md)

---

# Uživatelská příručka VD Infos

VD Infos je **ladicí nástroj metod zařízení**. Čte každou hodnotu zařízení přes každou
metodu, která ji umí přečíst, a seřazuje naměřené údaje vedle sebe, abyste je mohli
porovnat. Tato příručka pokrývá instalaci aplikace, spuštění skenu, čtení výsledku a
sdílení zprávy. Neinterpretuje za vás žádný konkrétní nález: porovnání je vaše, abyste si
ho přečetli na svém vlastním zařízení.

## Obsah

1. [Co je VD Infos](#1-co-je-vd-infos)
2. [Co to není](#2-co-to-není)
3. [Požadavky](#3-požadavky)
4. [Které APK nainstalovat](#4-které-apk-nainstalovat)
5. [Instalace a první spuštění](#5-instalace-a-první-spuštění)
6. [Spuštění skenu](#6-spuštění-skenu)
7. [Čtení výsledku](#7-čtení-výsledku)
8. [Export vaší zprávy](#8-export-vaší-zprávy)
9. [Pro AI agenty](#9-pro-ai-agenty)
10. [Jazyk a vzhled](#10-jazyk-a-vzhled)
11. [Soukromí v kostce](#11-soukromí-v-kostce)
12. [Často kladené dotazy](#12-často-kladené-dotazy)

## 1. Co je VD Infos

Android předává spoustu informací o vašem zařízení každé aplikaci, kterou nainstalujete.
VD Infos vám ukazuje příklad toho, co dokáže přečíst běžná aplikace, a dělá to jako
*ladicí nástroj metod*: pro každou informaci čte hodnotu přes **každou metodu, která ji
umí přečíst** - `Build.*`, `SystemProperties`, `getprop`, nativní
`__system_property_get`, systémové manažery, poskytovatele obsahu, soubory, syscally a
hardwarovou atestaci klíčů (TEE) - a seřazuje je, abyste mohli porovnat. Je to čočka, ne
stroj na verdikty: vynáší naměřené údaje na povrch a jejich čtení ponechává na vás.

## 2. Co to není

- **Není** to čistič, booster ani nástroj na skrývání. Nemění nic na vašem systému; pouze
  čte a porovnává.
- **Není** to skóre prospěl/neprospěl. Ukazuje vám syrové naměřené údaje; co znamenají
  pro vaši konfiguraci, je na vašem posouzení, na vašem zařízení.
- **Nepotřebuje** internet a funguje plně se zablokovaným internetem.

## 3. Požadavky

- Android 8.0 (API 26) nebo novější.
- Ke spuštění není potřeba žádné zvláštní oprávnění. Některé naměřené údaje se stanou
  dostupnými, až když udělíte odpovídající oprávnění (například přístup k využití), a
  některé existují pouze na zařízení s rootem; kde je naměřený údaj nedostupný, aplikace
  jednoduše ukáže, že nebyl dostupný.

## 4. Které APK nainstalovat

Každé vydání publikuje **dvě APK**:

- **SDK_35** - cílí na striktní moderní sandbox. Toto je ten, který se má použít.
- **SDK_27** - cílí na starší úroveň API, s volnější SELinux doménou. Je užitečný pouze
  pro srovnání bok po boku; většina lidí ho nepotřebuje.

Obě se instalují na Androidu 8.0 a novějším. Stáhněte si je ze stránky
[Releases](https://github.com/VD171/VD-Infos/releases).

## 5. Instalace a první spuštění

1. Stáhněte si APK **SDK_35** z Releases a nainstalujte ho (možná budete muset povolit
   instalace z prohlížeče nebo správce souborů).
2. Otevřete aplikaci. Otevře se rovnou na obrazovce skenu; není zde žádný účet, žádné
   přihlášení a žádné nastavování.
3. Volitelně udělte oprávnění, na které se aplikace ptá (například přístup k využití),
   abyste odemkli naměřené údaje, které na něm závisí. Toto je zcela volitelné.

## 6. Spuštění skenu

Klepněte na akci skenu. Aplikace rozvětví své sondy paralelně a streamuje výsledky do
seznamu, jak přicházejí, s živým ukazatelem postupu. Sken se dokončí sám; čte pouze
tehdy, když je aplikace otevřená, nenechává nic běžet na pozadí a sám se zavře, když je
ponechán nečinný.

Chcete-li ho spustit znovu, skenujte znovu: každý sken nahradí předchozí výsledek.

## 7. Čtení výsledku

Každá položka je karta, která seřazuje naměřené údaje z každé čočky (metoda Java SDK,
nativní metoda, shell a tak dále). Barva karty vám na první pohled říká, jak tyto
naměřené údaje spolu souvisejí:

- **Shoda** - každá čočka se shodla. Metody vrátily stejnou hodnotu.
- **Rozdíl** - čočky se neshodly. Jedna metoda vrátila něco jiného než ostatní.
- **Informace** - naměřený údaj zobrazený pro referenci, nehlasuje se o něm.
- **Jednotlivý / nedostupný** - jen jedna čočka ho dokázala přečíst, nebo žádná nemohla.

Karta vždy ukazuje úplnou matici naměřených údajů, takže vidíte přesně, která čočka řekla
co. Sbalená karta ukazuje svůj první naměřený údaj; rozbalte ji, abyste viděli všechny.

Potud za vás aplikace mluví. **Co daný rozdíl znamená na vašem zařízení a co s tím dělat,
je na vás, abyste prozkoumali** - to je celý smysl ladicího nástroje: položí důkazy před
vás a nechá vás je přečíst. Použijte vyhledávací pole k filtrování podle vlastnosti nebo
hodnoty a filtrovací štítky k zúžení seznamu.

## 8. Export vaší zprávy

Akce sdílení a uložení vám předají **celý výsledek**, nejen to, co je na obrazovce:

- **Sdílet / uložit jako JSON** - úplná zpráva ve strojově čitelné podobě, stejná data,
  jaká aplikace uchovává interně. Toto je formát pro uchování nebo pro předání nástroji.
- **Sdílet jako text** - kompaktní, pro člověka čitelný souhrn rozdílů.

Hodnoty nesoucí identitu (sériová čísla, IMEI, MAC adresy, reklamní ID a podobně) jsou
**v exportované zprávě maskované**, aby zpráva, kterou vložíte do veřejného vlákna,
neunikla vaše vlastní identifikátory; verdikty a struktura jsou zachovány. Kopie na
zařízení, kterou můžete číst jen vy (nebo root), uchovává skutečné hodnoty.

## 9. Pro AI agenty

VD Infos je postaven tak, aby ho četly nástroje, nejen lidé. Každý sken zapíše svou
úplnou zprávu do soukromého úložiště aplikace a export je stejný strojově čitelný JSON. AI
programátorský asistent nebo analytický skript běžící na zařízení může přečíst celý
výsledek jedním příkazem místo parsování snímků obrazovky. Viz vyhrazený
[průvodce AI agenty](AI-AGENTS.md) a referenci [formátu exportu](EXPORT-FORMAT.md).

## 10. Jazyk a vzhled

Aplikace se řídí jazykem vašeho systému, kde překlad existuje, a jeden si můžete vybrat
ručně. Používá Material 3 s dynamickou barvou a řídí se vaším nastavením světlého/tmavého
režimu.

## 11. Soukromí v kostce

Vše běží na zařízení. Žádný naměřený údaj se nikam neodesílá a zpráva opustí zařízení jen
tehdy, když ji výslovně sdílíte nebo uložíte. Každý sken uchovává svou úplnou zprávu v
soukromém úložišti aplikace (`files/last_snapshot.json`), aby ji nástroje mohly číst;
tento soubor může číst jen aplikace sama nebo root, následující sken ho přepíše a nikdy
neopustí zařízení. Viz [Soukromí](PRIVACY.md) pro úplné prohlášení.

## 12. Často kladené dotazy

**Mění něco na mém zařízení?** Ne. Pouze čte a porovnává.

**Potřebuje root?** Ne. Aplikace běží bez rootu. Hrstka naměřených údajů existuje jen na
zařízení s rootem a soukromý soubor zprávy může zpětně přečíst jen root nebo aplikace
sama; vše ostatní funguje bez rootu.

**Potřebuje internet?** Ne. Funguje plně offline; zablokujte jí přístup k síti, jestli
chcete.

**Které APK bych měl použít?** SDK_35. Build SDK_27 existuje jen pro srovnání.

**Proč některé položky říkají, že jsou nedostupné?** Protože ta metoda nedokázala přečíst
tu hodnotu na vašem zařízení - chybějící oprávnění, pravidlo sandboxu nebo hardware, který
není přítomen. To je samo o sobě součástí porovnání.

**Kde nahlásím problém nebo navrhnu cíl detekce?** Viz [Přispívání](CONTRIBUTING.md);
většina seznamů detekce je prostý text a nepotřebuje žádný kód.

<!-- doc-footer -->

---

> _Tato dokumentace byla vytvořena pomocí Claude (Anthropic) a spravuje ji VD171. Opravy a vylepšení jsou vítány; viz [CONTRIBUTING.md](CONTRIBUTING.md)._
