> **Languages:** [English](../AI-AGENTS.md) · [العربية](../ar/AI-AGENTS.md) · [Čeština](../cs/AI-AGENTS.md) · [Deutsch](../de/AI-AGENTS.md) · [Español](../es/AI-AGENTS.md) · [فارسی](../fa/AI-AGENTS.md) · [Français](../fr/AI-AGENTS.md) · [हिन्दी](../hi/AI-AGENTS.md) · [Bahasa Indonesia](../id/AI-AGENTS.md) · Italiano · [日本語](../ja/AI-AGENTS.md) · [한국어](../ko/AI-AGENTS.md) · [Nederlands](../nl/AI-AGENTS.md) · [Polski](../pl/AI-AGENTS.md) · [Português](../pt/AI-AGENTS.md) · [Русский](../ru/AI-AGENTS.md) · [Svenska](../sv/AI-AGENTS.md) · [ไทย](../th/AI-AGENTS.md) · [Türkçe](../tr/AI-AGENTS.md) · [Українська](../uk/AI-AGENTS.md) · [Tiếng Việt](../vi/AI-AGENTS.md) · [中文](../zh/AI-AGENTS.md)

---

# VD Infos per gli agenti AI

VD Infos è **costruito per essere letto dalle macchine, non solo dalle persone**. Un debugger dei metodi
del dispositivo produce una grande quantità di prove strutturate - ogni valore, letto attraverso ogni
metodo, allineato per il confronto - e quelle prove sono molto più utili a un agente come
dati che come screenshot. Perciò l'intero risultato è disponibile come un unico documento leggibile
dalle macchine, e l'app è progettata attorno a questo fin dalle fondamenta.

Se sei un assistente di programmazione AI, uno strumento LLM, uno script di analisi o una qualsiasi automazione
che raggiunge il dispositivo attraverso una shell, questa pagina è per te.

## Perché è adatto agli agenti

- **Un documento, non uno schermo.** L'intera scansione - ogni sonda, ogni lente, ogni
  verdetto - è un singolo oggetto JSON. Nessun OCR, nessuno scraping dello schermo, nessun tocco tra
  schede compresse.
- **Gli stessi dati che ha la UI.** Il file non è un riepilogo. È esattamente ciò che l'app
  conserva internamente: lo schermo mostra la prima lettura di un elemento compresso, il file
  le ha tutte.
- **Forma deterministica.** La struttura è stabile e documentata (vedi
  [EXPORT-FORMAT](EXPORT-FORMAT.md)), così un parser scritto una volta continua a funzionare.
- **Nessuna rete, nessun account, nessuno stato da gestire.** L'app legge sul dispositivo e scrive un
  file. Il tuo agente legge il file. Questo è tutto il contratto.
- **Offline per progettazione.** Non ha bisogno di internet, quindi funziona allo stesso modo all'interno di un ambiente
  isolato o air-gapped in cui un agente potrebbe operare.

## La lettura con un solo comando

Ogni scansione scrive il suo report completo nell'archivio privato dell'app. Dopo che una scansione è stata eseguita,
un agente legge il risultato completo con un singolo comando:

```sh
su -c cat /data/data/ru.vd171.vdinfos/files/last_snapshot.json
```

- **Cosa contiene:** ogni sonda con il suo verdetto e il valore di ogni lente,
  nello stesso JSON del pulsante di esportazione.
- **Quando si aggiorna:** alla fine di ogni scansione. Esegui prima una scansione; il file contiene sempre
  l'ultima.
- **Requisito:** root, perché il file risiede nell'archivio privato dell'app. Senza
  root, usa invece l'azione **condividi / salva come JSON** all'interno dell'app e leggi il file che l'agente
  riceve.

## Un tipico flusso di un agente

1. Assicurati che l'app sia installata (l'APK **SDK_35** da
   [Releases](https://github.com/VD171/VD-Infos/releases)).
2. Avvia una scansione - una persona tocca scansiona, o un'automazione lancia la schermata di scansione
   dell'app - e attendi che finisca.
3. Leggi `files/last_snapshot.json` con il comando sopra.
4. Analizzalo con il [formato di esportazione](EXPORT-FORMAT.md) come tuo schema: itera le
   sonde, leggi il `verdict` di ciascuna, e allinea i `values` per lente.
5. Agisci sul risultato strutturato nel tuo flusso di lavoro.

L'agente decide cosa significhino le letture per il compito in questione; VD Infos fornisce le
prove, in modo pulito e completo.

## Cosa ottiene l'agente, e cosa no

- Il report contiene **ogni lettura e ogni verdetto**, così un agente ha il quadro
  completo costruito dall'app.
- I valori che portano un'identità (numeri di serie, IMEI, indirizzi MAC, ID pubblicitari e simili)
  sono **mascherati nel report condiviso/esportato**, così un report che circola resta privo di
  identificatori personali pur mantenendo i suoi verdetti e la sua struttura. Il
  `last_snapshot.json` **sul dispositivo**, leggibile solo dall'app o da root, conserva i valori reali per un
  agente locale che ne ha bisogno.

## Documentazione, scritta per essere analizzata

Questo wiki è strutturato per la lettura automatica tanto quanto per le persone: sezioni brevi,
intestazioni stabili, nomi di campo espliciti, e una pagina dedicata al [formato di esportazione](EXPORT-FORMAT.md)
che un agente può trattare come uno schema. Se stai costruendo uno strumento sopra VD Infos,
parti da lì.

## Pagine correlate

- [Formato di esportazione](EXPORT-FORMAT.md) - lo schema JSON, campo per campo.
- [Guida utente](USER-GUIDE.md) - installazione, scansione e condivisione.
- [Architettura](ARCHITECTURE.md) - come è messa insieme l'app.

<!-- doc-footer -->

---

> _Questa documentazione è stata generata con Claude (Anthropic) ed è gestita da VD171. Correzioni e miglioramenti sono benvenuti; vedi [CONTRIBUTING.md](CONTRIBUTING.md)._
