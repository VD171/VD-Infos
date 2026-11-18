> **Languages:** [English](../ARCHITECTURE.md) · [العربية](../ar/ARCHITECTURE.md) · [Čeština](../cs/ARCHITECTURE.md) · [Deutsch](../de/ARCHITECTURE.md) · [Español](../es/ARCHITECTURE.md) · [فارسی](../fa/ARCHITECTURE.md) · [Français](../fr/ARCHITECTURE.md) · [हिन्दी](../hi/ARCHITECTURE.md) · [Bahasa Indonesia](../id/ARCHITECTURE.md) · [Italiano](../it/ARCHITECTURE.md) · [日本語](../ja/ARCHITECTURE.md) · [한국어](../ko/ARCHITECTURE.md) · [Nederlands](../nl/ARCHITECTURE.md) · [Polski](../pl/ARCHITECTURE.md) · [Português](../pt/ARCHITECTURE.md) · [Русский](../ru/ARCHITECTURE.md) · Svenska · [ไทย](../th/ARCHITECTURE.md) · [Türkçe](../tr/ARCHITECTURE.md) · [Українська](../uk/ARCHITECTURE.md) · [Tiếng Việt](../vi/ARCHITECTURE.md) · [中文](../zh/ARCHITECTURE.md)

---

# Arkitektur

Detta är en översiktlig karta över hur VD Infos är uppbyggd. Den beskriver appens form, inte
katalogen över vad den läser: poängen med en metoddebugger är att lägga varje avläsning framför dig,
och att läsa dem är ditt att göra på din egen enhet.

## Designmål

- **Ett värde, varje metod.** För varje informationsdel, läs den genom varje metod som kan läsa den
  och ställ upp avläsningarna. En debugger lyfter fram bevis; den fäller inte en dom.
- **På enheten och tyst.** Inget nätverk, inget konto, inget bakgrundsarbete. Appen körs endast
  medan den är öppen.
- **Läsbar av maskiner.** Hela resultatet är ett maskinläsbart dokument, så att ett verktyg får
  samma bild som en person gör. Se [AI-agenter](AI-AGENTS.md).
- **Liten där det betyder något.** Den del som måste vara svår att lura hålls medvetet pytteliten.

## Linserna

En *lins* är ett sätt att läsa ett värde. Samma värde läses genom flera så att de kan jämföras:

| Lins | Vad det är |
|---|---|
| **Java / SDK** | Plattforms-API:n som en vanlig app anropar. |
| **Native / JNI** | Samma värde läst från nativ kod. |
| **Shell via JVM** | Ett skalkommando som startas av JVM. |
| **Shell via JNI** | Samma kommando startat från nativ kod, en annan utsiktspunkt. |
| **TEE / Attestation** | Hårdvarubaserad nyckelattestering, där den gäller. |

Att ställa upp linserna är hela idén: när de inte är överens svarar något mellan appen och värdet
olika till olika anropare. Vad det betyder för ett givet värde, på en given enhet, är ditt att läsa.

## Hur en skanning körs

```
core/model      immutable domain: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          the probes and the bridges they use (JNI, reflection, data lists)
engine/         ProbeEngine: parallel fan-out, streams results as they land
data/           SnapshotStore (persist the report), Exporter (share as JSON / text)
ui/             Jetpack Compose, Material 3, dynamic colour, live progress
cpp/            native_probes.cpp: the native lens, dependency-free
```

- **Parallellism.** Sonderna fläktar ut över en dispatcher med ett begränsat antal tillstånd;
  resultat strömmar in i gränssnittet allteftersom de slutförs, med en live-förloppsindikator.
- **Ingenting i bakgrunden.** Inga tjänster och inga schemalagda skanningar. Appen läser endast
  medan den är öppen och stänger sig själv när den lämnas inaktiv.
- **Det nativa lagret.** En liten `.so`, bunden med namn genom `RegisterNatives`, hållen medvetet
  pytteliten eftersom det är den del som måste vara svår att lura.

## Rapporten

Varje skanning producerar en `Snapshot`: tiden den togs, appversionen, och en lista med resultat, ett
per sond, vart och ett med avläsningarna från varje lins och ett domslut för hur de förhåller sig.
Appen bevarar detta till privat lagring efter varje skanning och kan exportera samma data. Strukturen
är dokumenterad som [exportformatet](EXPORT-FORMAT.md).

## Att utöka den

Det mesta av vad appen letar efter bor som **ren-text-listor** under `assets/data/`, inte som kod. Att
lägga till eller ta bort ett mål är att redigera en rad. Se [Bidra](CONTRIBUTING.md).

## Vad VD Infos aldrig gör

- Den ändrar aldrig något på ditt system. Den bara läser och jämför.
- Den talar aldrig med nätverket. Den fungerar fullt ut offline.
- Den körs aldrig i bakgrunden, schemalägger aldrig en skanning, och håller aldrig en tjänst vid liv.
- Den skickar aldrig en avläsning någonstans. En rapport lämnar enheten endast när du delar eller
  sparar den, med identitetsbärande värden maskerade.

<!-- doc-footer -->

---

> _Denna dokumentation genererades med Claude (Anthropic) och underhålls av VD171. Rättelser och förbättringar välkomnas; se [CONTRIBUTING.md](CONTRIBUTING.md)._
