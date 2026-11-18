> **Languages:** [English](../SECURITY.md) · [العربية](../ar/SECURITY.md) · Čeština · [Deutsch](../de/SECURITY.md) · [Español](../es/SECURITY.md) · [فارسی](../fa/SECURITY.md) · [Français](../fr/SECURITY.md) · [हिन्दी](../hi/SECURITY.md) · [Bahasa Indonesia](../id/SECURITY.md) · [Italiano](../it/SECURITY.md) · [日本語](../ja/SECURITY.md) · [한국어](../ko/SECURITY.md) · [Nederlands](../nl/SECURITY.md) · [Polski](../pl/SECURITY.md) · [Português](../pt/SECURITY.md) · [Русский](../ru/SECURITY.md) · [Svenska](../sv/SECURITY.md) · [ไทย](../th/SECURITY.md) · [Türkçe](../tr/SECURITY.md) · [Українська](../uk/SECURITY.md) · [Tiếng Việt](../vi/SECURITY.md) · [中文](../zh/SECURITY.md)

---

# Bezpečnostní zásady

VD Infos je nástroj na soukromí, který čte citlivý stav zařízení, takže bezpečnostní
hlášení jsou brána vážně.

## Nahlášení zranitelnosti

Nahlaste prosím bezpečnostní problémy **soukromě**, ne ve veřejném issue:

- E-mail **vd.priv8@pm.me**.
- Popište problém, dotčenou verzi a jak ho reprodukovat.
- Pokud se týká vystavení dat, popište třídu problému; nemusíte přikládat funkční exploit.

Dostanete odpověď co nejdříve. Dejte prosím rozumné okno na opravu problému před jakýmkoli
veřejným zveřejněním.

## Co se počítá jako bezpečnostní problém

Kvůli tomu, co VD Infos slibuje, jsou tyto obzvláště důležité:

- Cokoli, co by přimělo aplikaci dosáhnout na síť nebo odeslat jakýkoli naměřený údaj ze
  zařízení.
- Cokoli, co by i přes maskování nechalo uniknout hodnotu nesoucí identitu do sdíleného
  nebo uloženého **exportu** (sériové číslo, IMEI, MAC adresu, reklamní ID a podobně).
- Cokoli, co umožní jiné aplikaci číst soukromý soubor zprávy, který má být čitelný jen
  aplikací samotnou nebo rootem.

## Co není bezpečnostní problém

- Naměřený údaj, který vypadá "špatně", ale je to poctivá hodnota, kterou metoda vrátila.
  VD Infos hlásí, co každá metoda řekla; čtení porovnání je na vás. Viz
  [uživatelská příručka](USER-GUIDE.md).
- Naměřený údaj označený jako nedostupný. To znamená, že metoda nedokázala přečíst tu
  hodnotu na vašem zařízení, což je samo o sobě součástí porovnání.

## Podporované verze

Podporována je pouze poslední vydaná verze. Reprodukujte prosím na nejnovějším buildu před
nahlášením.

<!-- doc-footer -->

---

> _Tato dokumentace byla vytvořena pomocí Claude (Anthropic) a spravuje ji VD171. Opravy a vylepšení jsou vítány; viz [CONTRIBUTING.md](CONTRIBUTING.md)._
