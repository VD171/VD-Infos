> **Languages:** [English](../ARCHITECTURE.md) · [العربية](../ar/ARCHITECTURE.md) · Čeština · [Deutsch](../de/ARCHITECTURE.md) · [Español](../es/ARCHITECTURE.md) · [فارسی](../fa/ARCHITECTURE.md) · [Français](../fr/ARCHITECTURE.md) · [हिन्दी](../hi/ARCHITECTURE.md) · [Bahasa Indonesia](../id/ARCHITECTURE.md) · [Italiano](../it/ARCHITECTURE.md) · [日本語](../ja/ARCHITECTURE.md) · [한국어](../ko/ARCHITECTURE.md) · [Nederlands](../nl/ARCHITECTURE.md) · [Polski](../pl/ARCHITECTURE.md) · [Português](../pt/ARCHITECTURE.md) · [Русский](../ru/ARCHITECTURE.md) · [Svenska](../sv/ARCHITECTURE.md) · [ไทย](../th/ARCHITECTURE.md) · [Türkçe](../tr/ARCHITECTURE.md) · [Українська](../uk/ARCHITECTURE.md) · [Tiếng Việt](../vi/ARCHITECTURE.md) · [中文](../zh/ARCHITECTURE.md)

---

# Architektura

Toto je mapa vysoké úrovně toho, jak je VD Infos poskládán. Popisuje tvar aplikace, ne
katalog toho, co čte: smyslem ladicího nástroje metod je položit každý naměřený údaj před
vás, a jejich čtení je na vás, na vašem vlastním zařízení.

## Cíle návrhu

- **Jedna hodnota, každá metoda.** Pro každou informaci ji přečtěte přes každou metodu,
  která ji umí přečíst, a seřaďte naměřené údaje. Ladicí nástroj vynáší důkazy na povrch;
  nevynáší soud.
- **Na zařízení a tiše.** Žádná síť, žádný účet, žádná práce na pozadí. Aplikace běží jen,
  když je otevřená.
- **Čitelné stroji.** Celý výsledek je jeden strojově čitelný dokument, takže nástroj
  dostane stejný obraz jako člověk. Viz [AI agenti](AI-AGENTS.md).
- **Malé tam, kde na tom záleží.** Část, kterou musí být těžké oklamat, je držena záměrně
  drobná.

## Čočky

*Čočka* je jeden způsob čtení hodnoty. Stejná hodnota je čtena přes několik, aby bylo
možné je porovnat:

| Čočka | Co to je |
|---|---|
| **Java / SDK** | Platformové API, které volá běžná aplikace. |
| **Nativní / JNI** | Stejná hodnota čtená z nativního kódu. |
| **Shell přes JVM** | Příkaz shellu spuštěný JVM. |
| **Shell přes JNI** | Stejný příkaz spuštěný z nativního kódu, jiné výhodné místo. |
| **TEE / Atestace** | Hardwarově podložená atestace klíčů, kde se uplatňuje. |

Seřazení čoček je celá myšlenka: když se neshodnou, něco mezi aplikací a hodnotou
odpovídá různým volajícím různě. Co to znamená pro danou hodnotu, na daném zařízení, je na
vás, abyste si přečetli.

## Jak sken probíhá

```
core/model      immutable domain: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          the probes and the bridges they use (JNI, reflection, data lists)
engine/         ProbeEngine: parallel fan-out, streams results as they land
data/           SnapshotStore (persist the report), Exporter (share as JSON / text)
ui/             Jetpack Compose, Material 3, dynamic colour, live progress
cpp/            native_probes.cpp: the native lens, dependency-free
```

- **Paralelismus.** Sondy se rozvětví napříč dispatcherem s omezeným počtem povolení;
  výsledky streamují do uživatelského rozhraní, jak se dokončují, s živým ukazatelem
  postupu.
- **Nic na pozadí.** Žádné služby a žádné naplánované skeny. Aplikace čte jen, když je
  otevřená, a sama se zavře, když je ponechána nečinná.
- **Nativní vrstva.** Jedna malá `.so`, vázaná podle názvu přes `RegisterNatives`, držená
  záměrně drobná, protože je to část, kterou musí být těžké oklamat.

## Zpráva

Každý sken produkuje `Snapshot`: čas, kdy byl pořízen, verzi aplikace a seznam výsledků,
jeden na sondu, každý s naměřenými údaji z každé čočky a verdiktem o tom, jak spolu
souvisejí. Aplikace to po každém skenu uchová do soukromého úložiště a může exportovat
stejná data. Struktura je zdokumentována jako [formát exportu](EXPORT-FORMAT.md).

## Rozšiřování

Většina toho, co aplikace hledá, žije jako **seznamy prostého textu** pod
`assets/data/`, ne jako kód. Přidání nebo odebrání cíle je úprava jednoho řádku. Viz
[Přispívání](CONTRIBUTING.md).

## Co VD Infos nikdy nedělá

- Nikdy nemění nic na vašem systému. Pouze čte a porovnává.
- Nikdy nemluví se sítí. Funguje plně offline.
- Nikdy neběží na pozadí, neplánuje sken ani neudržuje službu naživu.
- Nikdy neodesílá naměřený údaj nikam. Zpráva opustí zařízení jen, když ji sdílíte nebo
  uložíte, s maskovanými hodnotami nesoucími identitu.

<!-- doc-footer -->

---

> _Tato dokumentace byla vytvořena pomocí Claude (Anthropic) a spravuje ji VD171. Opravy a vylepšení jsou vítány; viz [CONTRIBUTING.md](CONTRIBUTING.md)._
