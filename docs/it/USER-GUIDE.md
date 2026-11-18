> **Languages:** [English](../USER-GUIDE.md) · [العربية](../ar/USER-GUIDE.md) · [Čeština](../cs/USER-GUIDE.md) · [Deutsch](../de/USER-GUIDE.md) · [Español](../es/USER-GUIDE.md) · [فارسی](../fa/USER-GUIDE.md) · [Français](../fr/USER-GUIDE.md) · [हिन्दी](../hi/USER-GUIDE.md) · [Bahasa Indonesia](../id/USER-GUIDE.md) · Italiano · [日本語](../ja/USER-GUIDE.md) · [한국어](../ko/USER-GUIDE.md) · [Nederlands](../nl/USER-GUIDE.md) · [Polski](../pl/USER-GUIDE.md) · [Português](../pt/USER-GUIDE.md) · [Русский](../ru/USER-GUIDE.md) · [Svenska](../sv/USER-GUIDE.md) · [ไทย](../th/USER-GUIDE.md) · [Türkçe](../tr/USER-GUIDE.md) · [Українська](../uk/USER-GUIDE.md) · [Tiếng Việt](../vi/USER-GUIDE.md) · [中文](../zh/USER-GUIDE.md)

---

# Guida utente di VD Infos

VD Infos è un **debugger dei metodi del dispositivo**. Legge ogni valore del dispositivo attraverso ogni
metodo che può leggerlo, e allinea le letture fianco a fianco in modo che tu possa confrontarle.
Questa guida illustra come installare l'app, eseguire una scansione, leggere il risultato e
condividere un report. Non interpreta per te alcun risultato specifico: il confronto
spetta a te leggerlo, sul tuo dispositivo.

## Indice

1. [Cos'è VD Infos](#1-cosè-vd-infos)
2. [Cosa non è](#2-cosa-non-è)
3. [Requisiti](#3-requisiti)
4. [Quale APK installare](#4-quale-apk-installare)
5. [Installazione e primo avvio](#5-installazione-e-primo-avvio)
6. [Eseguire una scansione](#6-eseguire-una-scansione)
7. [Leggere il risultato](#7-leggere-il-risultato)
8. [Esporta il tuo report](#8-esporta-il-tuo-report)
9. [Per gli agenti AI](#9-per-gli-agenti-ai)
10. [Lingua e tema](#10-lingua-e-tema)
11. [Privacy in breve](#11-privacy-in-breve)
12. [Domande frequenti](#12-domande-frequenti)

## 1. Cos'è VD Infos

Android consegna molte informazioni del tuo dispositivo a ogni app che installi. VD Infos
ti mostra un esempio di ciò che un'app ordinaria può leggere, e lo fa come un *debugger
dei metodi*: per ogni singola informazione legge il valore attraverso **ogni metodo
che può leggerlo** - API della piattaforma, proprietà di sistema, chiamate native, i system
manager, i content provider, i file, le syscall e l'attestazione delle chiavi hardware (TEE) - e li allinea in modo che tu possa confrontarli. È una lente,
non una macchina di verdetti: fa emergere le letture e lascia a te il compito di leggerle.

## 2. Cosa non è

- **Non** è un pulitore, un booster o uno strumento per nascondere. Non cambia nulla sul tuo
  sistema; si limita a leggere e confrontare.
- **Non** è un punteggio di superato/non superato. Ti mostra le letture grezze; ciò che significano per
  la tua configurazione spetta a te giudicarlo, sul tuo dispositivo.
- **Non** ha bisogno di internet, e funziona pienamente con internet bloccato.

## 3. Requisiti

- Android 8.0 (API 26) o più recente.
- Non è richiesta alcuna autorizzazione speciale per iniziare. Alcune letture diventano disponibili solo se
  concedi l'autorizzazione corrispondente (per esempio l'accesso all'utilizzo), e alcune esistono solo
  su un dispositivo con root; quando una lettura non è disponibile, l'app mostra semplicemente che non era
  disponibile.

## 4. Quale APK installare

Ogni release pubblica **due APK**:

- **SDK_35** - mira alla rigida sandbox moderna. È quello da usare.
- **SDK_27** - mira a un livello di API più vecchio. È utile
  solo per il confronto fianco a fianco; la maggior parte delle persone non ne ha bisogno.

Entrambi si installano su Android 8.0 e più recente. Scaricali dalla pagina
[Releases](https://github.com/VD171/VD-Infos/releases).

## 5. Installazione e primo avvio

1. Scarica l'APK **SDK_35** da Releases e installalo (potresti dover consentire
   le installazioni dal browser o dal file manager).
2. Apri l'app. Si apre direttamente nella schermata di scansione; non c'è alcun account, nessun
   accesso e nessuna configurazione.
3. Facoltativamente concedi un'autorizzazione che l'app richiede (come l'accesso all'utilizzo) per sbloccare
   le letture che ne dipendono. Questo è del tutto facoltativo.

## 6. Eseguire una scansione

Tocca l'azione di scansione. L'app dispiega le sue sonde in parallelo e fa affluire i risultati nella
lista man mano che arrivano, con un indicatore di avanzamento in tempo reale. Una scansione si completa da sola; essa
legge solo mentre l'app è aperta, non mantiene nulla in esecuzione in background e si chiude
da sola quando viene lasciata inattiva.

Per eseguirla di nuovo, scansiona di nuovo: ogni scansione sostituisce il risultato precedente.

## 7. Leggere il risultato

Ogni elemento è una scheda che allinea le letture di ogni lente (il metodo Java SDK,
il metodo nativo, la shell, e così via). Il colore della scheda ti dice, a colpo d'occhio,
come queste letture si relazionano tra loro:

- **Corrispondenza** - ogni lente è d'accordo. I metodi hanno restituito lo stesso valore.
- **Divergenza** - le lenti non sono d'accordo. Un metodo ha restituito qualcosa di diverso
  dagli altri.
- **Informazione** - una lettura mostrata come riferimento, non sottoposta a votazione.
- **Singola / non disponibile** - solo una lente ha potuto leggerla, o nessuna ci è riuscita.

La scheda mostra sempre la matrice completa delle letture, così puoi vedere esattamente quale lente
ha detto cosa. Una scheda compressa mostra la sua prima lettura; espandila per vederle tutte.

Questo è il limite di ciò che l'app dice al posto tuo. **Cosa significhi una data divergenza sul tuo
dispositivo, e cosa farci, spetta a te indagarlo** - è questo tutto il senso
di un debugger: mette le prove davanti a te e ti lascia leggerle. Usa la
casella di ricerca per filtrare per proprietà o valore, e i chip di filtro per restringere la lista.

## 8. Esporta il tuo report

Le azioni di condivisione e salvataggio ti consegnano l'**intero risultato**, non solo ciò che è sullo schermo:

- **Condividi / salva come JSON** - il report completo in forma leggibile dalle macchine, gli stessi
  dati che l'app conserva internamente. È questo il formato da conservare o da fornire a uno strumento.
- **Condividi come testo** - un riepilogo compatto e leggibile dalle persone delle divergenze.

I valori che portano un'identità (numeri di serie, IMEI, indirizzi MAC, ID pubblicitari e simili)
sono **mascherati nel report esportato** affinché un report che incolli in un thread pubblico
non faccia trapelare i tuoi identificatori; i verdetti e la struttura sono preservati. La
copia sul dispositivo che solo tu (o root) puoi leggere conserva i valori reali.

## 9. Per gli agenti AI

VD Infos è costruito per essere letto dagli strumenti, non solo dalle persone. Ogni scansione scrive il suo report
completo nell'archivio privato dell'app, e l'esportazione è lo stesso JSON leggibile dalle macchine.
Un assistente di programmazione AI o uno script di analisi in esecuzione sul dispositivo può leggere l'intero
risultato con un solo comando invece di analizzare screenshot. Vedi la
[guida per gli agenti AI](AI-AGENTS.md) dedicata e il riferimento sul [formato di esportazione](EXPORT-FORMAT.md).

## 10. Lingua e tema

L'app segue la lingua del tuo sistema dove esiste una traduzione, e puoi sceglierne una
manualmente. Usa Material 3 con il colore dinamico e segue la tua impostazione chiaro/scuro.

## 11. Privacy in breve

Tutto funziona sul dispositivo. Nessuna lettura viene inviata da nessuna parte, e un report lascia il dispositivo
solo quando lo condividi o lo salvi esplicitamente. Ogni scansione conserva il suo report completo nell'archivio
privato dell'app (`files/last_snapshot.json`) così gli strumenti possono leggerlo; solo l'app stessa
o root possono leggere quel file, la scansione successiva lo sovrascrive, ed esso non lascia mai il dispositivo.
Vedi [Privacy](PRIVACY.md) per la dichiarazione completa.

## 12. Domande frequenti

**Cambia qualcosa sul mio dispositivo?** No. Si limita a leggere e confrontare.

**Ha bisogno del root?** No. L'app funziona senza root. Una manciata di letture esiste solo
su un dispositivo con root, e il file di report privato può essere riletto solo da root o dall'app
stessa; tutto il resto funziona senza root.

**Ha bisogno di internet?** No. Funziona pienamente offline; blocca il suo accesso alla rete se
vuoi.

**Quale APK dovrei usare?** SDK_35. La build SDK_27 esiste solo per il confronto.

**Perché alcuni elementi dicono non disponibile?** Perché quel metodo non ha potuto leggere quel valore sul
tuo dispositivo - un'autorizzazione mancante, una regola della sandbox, o hardware che non è presente.
Questo è di per sé parte del confronto.

**Dove segnalo un problema o suggerisco un target di rilevamento?** Vedi
[Contribuire](CONTRIBUTING.md); la maggior parte delle liste di rilevamento sono testo semplice e non richiedono codice.

<!-- doc-footer -->

---

> _Questa documentazione è stata generata con Claude (Anthropic) ed è gestita da VD171. Correzioni e miglioramenti sono benvenuti; vedi [CONTRIBUTING.md](CONTRIBUTING.md)._
