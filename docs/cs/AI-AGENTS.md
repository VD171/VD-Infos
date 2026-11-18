> **Languages:** [English](../AI-AGENTS.md) · [العربية](../ar/AI-AGENTS.md) · Čeština · [Deutsch](../de/AI-AGENTS.md) · [Español](../es/AI-AGENTS.md) · [فارسی](../fa/AI-AGENTS.md) · [Français](../fr/AI-AGENTS.md) · [हिन्दी](../hi/AI-AGENTS.md) · [Bahasa Indonesia](../id/AI-AGENTS.md) · [Italiano](../it/AI-AGENTS.md) · [日本語](../ja/AI-AGENTS.md) · [한국어](../ko/AI-AGENTS.md) · [Nederlands](../nl/AI-AGENTS.md) · [Polski](../pl/AI-AGENTS.md) · [Português](../pt/AI-AGENTS.md) · [Русский](../ru/AI-AGENTS.md) · [Svenska](../sv/AI-AGENTS.md) · [ไทย](../th/AI-AGENTS.md) · [Türkçe](../tr/AI-AGENTS.md) · [Українська](../uk/AI-AGENTS.md) · [Tiếng Việt](../vi/AI-AGENTS.md) · [中文](../zh/AI-AGENTS.md)

---

# VD Infos pro AI agenty

VD Infos je **postaven tak, aby ho četly stroje, nejen lidé**. Ladicí nástroj metod
zařízení produkuje spoustu strukturovaných důkazů - každou hodnotu, přečtenou přes každou
metodu, seřazenou pro porovnání - a tyto důkazy jsou pro agenta mnohem užitečnější jako
data než jako snímek obrazovky. Proto je celý výsledek dostupný jako jeden strojově
čitelný dokument a aplikace je kolem toho navržena od základu.

Pokud jste AI programátorský asistent, LLM nástroj, analytický skript nebo jakákoli
automatizace, která dosáhne na zařízení přes shell, tato stránka je pro vás.

## Proč je přívětivý k agentům

- **Jeden dokument, ne obrazovka.** Celý sken - každá sonda, každá čočka, každý verdikt -
  je jediný objekt JSON. Žádné OCR, žádné škrábání obrazovky, žádné proklikávání sbalených
  karet.
- **Stejná data, jaká má uživatelské rozhraní.** Soubor není souhrn. Je přesně tím, co
  aplikace uchovává interně: obrazovka ukazuje první naměřený údaj sbalené položky, soubor
  má všechny.
- **Deterministický tvar.** Struktura je stabilní a zdokumentovaná (viz
  [EXPORT-FORMAT](EXPORT-FORMAT.md)), takže jednou napsaný parser dál funguje.
- **Žádná síť, žádný účet, žádný stav ke správě.** Aplikace čte na zařízení a zapisuje
  soubor. Váš agent čte soubor. To je celá smlouva.
- **Offline od základu.** Nepotřebuje internet, takže běží stejně uvnitř uzavřeného nebo
  vzduchově izolovaného prostředí, ve kterém může agent pracovat.

## Čtení jedním příkazem

Každý sken zapíše svou úplnou zprávu do soukromého úložiště aplikace. Poté, co proběhl
sken, přečte agent úplný výsledek jediným příkazem:

```sh
su -c cat /data/data/ru.vd171.vdinfos/files/last_snapshot.json
```

- **Co obsahuje:** každou sondu s jejím verdiktem a hodnotou z každé čočky, ve stejném
  JSON jako tlačítko exportu.
- **Kdy se aktualizuje:** na konci každého skenu. Spusťte nejprve sken; soubor vždy
  obsahuje ten nejnovější.
- **Požadavek:** root, protože soubor leží v soukromém úložišti aplikace. Bez rootu místo
  toho použijte v aplikaci akci **sdílet / uložit jako JSON** a přečtěte soubor, který je
  agentovi předán.

## Typický tok agenta

1. Ujistěte se, že je aplikace nainstalovaná (APK **SDK_35** z
   [Releases](https://github.com/VD171/VD-Infos/releases)).
2. Spusťte sken - člověk klepne na sken, nebo automatizace spustí obrazovku skenu
   aplikace - a počkejte, až skončí.
3. Přečtěte `files/last_snapshot.json` výše uvedeným příkazem.
4. Parsujte ho s [formátem exportu](EXPORT-FORMAT.md) jako svým schématem: iterujte přes
   sondy, přečtěte `verdict` každé z nich a seřaďte `values` podle čočky.
5. Jednejte podle strukturovaného výsledku ve svém vlastním pracovním postupu.

Agent rozhoduje, co naměřené údaje znamenají pro daný úkol; VD Infos dodává důkazy, čistě
a úplně.

## Co agent dostane a co ne

- Zpráva nese **každý naměřený údaj a každý verdikt**, takže agent má úplný obraz, který
  aplikace sestavila.
- Hodnoty nesoucí identitu (sériová čísla, IMEI, MAC adresy, reklamní ID a podobně) jsou
  **ve sdílené/exportované zprávě maskované**, takže předávaná zpráva zůstává bez osobních
  identifikátorů, přičemž si zachovává své verdikty a strukturu. Soubor
  `last_snapshot.json` **na zařízení**, čitelný jen aplikací nebo rootem, uchovává
  skutečné hodnoty pro místního agenta, který je potřebuje.

## Dokumentace, psaná pro parsování

Tato wiki je strukturována pro strojové čtení stejně jako pro lidi: krátké sekce, stabilní
nadpisy, explicitní názvy polí a vyhrazená stránka [formátu exportu](EXPORT-FORMAT.md),
kterou agent může brát jako schéma. Pokud stavíte nástroj nad VD Infos, začněte tam.

## Související stránky

- [Formát exportu](EXPORT-FORMAT.md) - schéma JSON, pole po poli.
- [Uživatelská příručka](USER-GUIDE.md) - instalace, skenování a sdílení.
- [Architektura](ARCHITECTURE.md) - jak je aplikace poskládaná.

<!-- doc-footer -->

---

> _Tato dokumentace byla vytvořena pomocí Claude (Anthropic) a spravuje ji VD171. Opravy a vylepšení jsou vítány; viz [CONTRIBUTING.md](CONTRIBUTING.md)._
