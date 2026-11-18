> **Languages:** [English](../ARCHITECTURE.md) · [العربية](../ar/ARCHITECTURE.md) · [Čeština](../cs/ARCHITECTURE.md) · [Deutsch](../de/ARCHITECTURE.md) · [Español](../es/ARCHITECTURE.md) · [فارسی](../fa/ARCHITECTURE.md) · [Français](../fr/ARCHITECTURE.md) · [हिन्दी](../hi/ARCHITECTURE.md) · [Bahasa Indonesia](../id/ARCHITECTURE.md) · Italiano · [日本語](../ja/ARCHITECTURE.md) · [한국어](../ko/ARCHITECTURE.md) · [Nederlands](../nl/ARCHITECTURE.md) · [Polski](../pl/ARCHITECTURE.md) · [Português](../pt/ARCHITECTURE.md) · [Русский](../ru/ARCHITECTURE.md) · [Svenska](../sv/ARCHITECTURE.md) · [ไทย](../th/ARCHITECTURE.md) · [Türkçe](../tr/ARCHITECTURE.md) · [Українська](../uk/ARCHITECTURE.md) · [Tiếng Việt](../vi/ARCHITECTURE.md) · [中文](../zh/ARCHITECTURE.md)

---

# Architettura

Questa è una mappa ad alto livello di come è messo insieme VD Infos. Descrive la forma dell'app,
non il catalogo di ciò che legge: il punto di un debugger dei metodi è mettere ogni
lettura davanti a te, e leggerle spetta a te farlo sul tuo dispositivo.

## Obiettivi di progettazione

- **Un valore, ogni metodo.** Per ogni singola informazione, leggila attraverso ogni
  metodo che può leggerla e allinea le letture. Un debugger fa emergere le prove; non
  emette giudizi.
- **Sul dispositivo e silenzioso.** Nessuna rete, nessun account, nessun lavoro in background. L'app funziona
  solo mentre è aperta.
- **Leggibile dalle macchine.** L'intero risultato è un unico documento leggibile dalle macchine, così uno
  strumento ottiene lo stesso quadro di una persona. Vedi [Agenti AI](AI-AGENTS.md).
- **Piccolo dove conta.** La parte che deve essere difficile da ingannare è mantenuta deliberatamente
  minuscola.

## Le lenti

Una *lente* è un modo di leggere un valore. Lo stesso valore è letto attraverso diverse così che
possano essere confrontate:

| Lente | Cos'è |
|---|---|
| **Java / SDK** | L'API della piattaforma che un'app ordinaria chiama. |
| **Native / JNI** | Lo stesso valore letto dal codice nativo. |
| **Shell via JVM** | Un comando shell generato dalla JVM. |
| **Shell via JNI** | Lo stesso comando generato dal codice nativo, un punto di osservazione diverso. |
| **TEE / Attestation** | Attestazione delle chiavi supportata dall'hardware, dove si applica. |

Allineare le lenti è tutta l'idea: quando sono in disaccordo, qualcosa tra l'app
e il valore risponde in modo diverso a chiamanti diversi. Cosa questo significhi per un dato valore,
su un dato dispositivo, spetta a te leggerlo.

## Come viene eseguita una scansione

```
core/model      immutable domain: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          the probes and the bridges they use (JNI, reflection, data lists)
engine/         ProbeEngine: parallel fan-out, streams results as they land
data/           SnapshotStore (persist the report), Exporter (share as JSON / text)
ui/             Jetpack Compose, Material 3, dynamic colour, live progress
cpp/            native_probes.cpp: the native lens, dependency-free
```

- **Parallelismo.** Le sonde si dispiegano su un dispatcher con un numero limitato di permessi;
  i risultati affluiscono nella UI man mano che si completano, con un indicatore di avanzamento in tempo reale.
- **Nulla in background.** Nessun servizio e nessuna scansione pianificata. L'app legge solo
  mentre è aperta e si chiude da sola quando viene lasciata inattiva.
- **Il livello nativo.** Un piccolo `.so`, legato per nome tramite `RegisterNatives`, mantenuto
  deliberatamente minuscolo perché è la parte che deve essere difficile da ingannare.

## Il report

Ogni scansione produce uno `Snapshot`: l'ora in cui è stata eseguita, la versione dell'app, e una lista di
risultati, uno per sonda, ciascuno con le letture di ogni lente e un verdetto su come
si relazionano. L'app persiste questo nell'archivio privato dopo ogni scansione e può esportare
gli stessi dati. La struttura è documentata come il [formato di esportazione](EXPORT-FORMAT.md).

## Estenderlo

Gran parte di ciò che l'app cerca risiede come **liste di testo semplice** sotto
`assets/data/`, non come codice. Aggiungere o rimuovere un target è modificare una riga. Vedi
[Contribuire](CONTRIBUTING.md).

## Cosa VD Infos non fa mai

- Non cambia mai nulla sul tuo sistema. Si limita a leggere e confrontare.
- Non parla mai alla rete. Funziona pienamente offline.
- Non viene mai eseguito in background, non pianifica una scansione, né mantiene vivo un servizio.
- Non invia mai una lettura da nessuna parte. Un report lascia il dispositivo solo quando lo condividi o lo
  salvi, con i valori che portano un'identità mascherati.

<!-- doc-footer -->

---

> _Questa documentazione è stata generata con Claude (Anthropic) ed è gestita da VD171. Correzioni e miglioramenti sono benvenuti; vedi [CONTRIBUTING.md](CONTRIBUTING.md)._
