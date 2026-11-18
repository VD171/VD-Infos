> **Languages:** [English](../CONTRIBUTING.md) · [العربية](../ar/CONTRIBUTING.md) · Čeština · [Deutsch](../de/CONTRIBUTING.md) · [Español](../es/CONTRIBUTING.md) · [فارسی](../fa/CONTRIBUTING.md) · [Français](../fr/CONTRIBUTING.md) · [हिन्दी](../hi/CONTRIBUTING.md) · [Bahasa Indonesia](../id/CONTRIBUTING.md) · [Italiano](../it/CONTRIBUTING.md) · [日本語](../ja/CONTRIBUTING.md) · [한국어](../ko/CONTRIBUTING.md) · [Nederlands](../nl/CONTRIBUTING.md) · [Polski](../pl/CONTRIBUTING.md) · [Português](../pt/CONTRIBUTING.md) · [Русский](../ru/CONTRIBUTING.md) · [Svenska](../sv/CONTRIBUTING.md) · [ไทย](../th/CONTRIBUTING.md) · [Türkçe](../tr/CONTRIBUTING.md) · [Українська](../uk/CONTRIBUTING.md) · [Tiếng Việt](../vi/CONTRIBUTING.md) · [中文](../zh/CONTRIBUTING.md)

---

# Přispívání

Děkujeme za váš zájem o VD Infos. Příspěvky jsou vítány: seznamy detekce, překlady,
hlášení chyb, dokumentace a kód. Většina příspěvků nepotřebuje žádný Kotlin.

## Nejsnazší příspěvek: seznamy

Většina toho, co aplikace hledá, žije jako **seznamy prostého textu** pod
`VDInfos/app/src/main/assets/data/`, ne jako kód. Přidání nebo odebrání cíle je úprava
jednoho řádku a otevření pull requestu:

- `*_apps.txt` - jeden název balíčku na řádek.
- `props.txt` - katalog systémových vlastností, `CATEGORY<tab>key`.
- `spoof_keys.txt` - matice nastavení, `key:TYPE`.
- `known_certs.txt` - katalog podpisových certifikátů, `SHA-256<tab>owner` (úplný hash,
  získaný z reálného APK pomocí `apksigner verify --print-certs`).
- ostatní soubory `.txt` - jeden záznam na řádek (fragmenty názvů jaderných modulů, názvy
  souborů a tak dále).

`#` zahajuje komentář a prázdné řádky se ignorují. Některé soubory zabalují na řádek pár
polí oddělených mezerami nebo tabulátory, nebo volitelné značky; zachovejte tvar řádků,
které už v souboru jsou.

## Základní pravidla

- **VD Infos čte pravdu a nikdy nic nefalšuje.** Hlásí přesně to, co každá metoda vrátila.
  Nezmírňujte naměřený údaj, aby vypadal hezčeji, a nepřidávejte nic, co by hodnotu spíše
  změnilo než přečetlo.
- **Soukromí je absolutní.** Vše běží na zařízení. Nikdy nepřidávejte síťové volání ani
  nic, co odesílá naměřený údaj ze zařízení.
- **Exporty zůstávají maskované.** Hodnoty nesoucí identitu jsou ve sdílené zprávě
  maskované. Nechte to tak.

## Překlady

Překlady jsou cenný příspěvek. Readme se překládá podle jazyka (`README.<lang>.md`),
řetězce aplikace podle jazyka a tato dokumentace pod `docs/<lang>/`. Udržujte terminologii
konzistentní v rámci jazyka, odpovídejte stávajícímu tónu a nikdy strojově nepřekládejte
odborný termín, kterým si nejste jistí.

## Nahlášení chyby

Otevřete issue s modelem svého zařízení, verzí Androidu a, pokud jde o naměřený údaj, se
sdíleným **exportem** (jeho hodnoty nesoucí identitu jsou maskované). Export je postaven
přesně pro tohle.

## Kód

- Build release, nikdy debug. Testujte na reálném zařízení. Viz [Sestavení](BUILDING.md).
- Nedávejte komentáře do kódu; názvy vysvětlují záměr. Jediná hlavička je licenční banner
  na začátku každého zdrojového souboru.
- Odpovídejte stávajícímu stylu a držte změny zaměřené.

## Licence

Příspěvkem souhlasíte s tím, že váš příspěvek je licencován pod projektovou
**GNU AGPL-3.0-or-later**.

<!-- doc-footer -->

---

> _Tato dokumentace byla vytvořena pomocí Claude (Anthropic) a spravuje ji VD171. Opravy a vylepšení jsou vítány; viz [CONTRIBUTING.md](CONTRIBUTING.md)._
