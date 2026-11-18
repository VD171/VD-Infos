> **Languages:** [English](../PRIVACY.md) · [العربية](../ar/PRIVACY.md) · [Čeština](../cs/PRIVACY.md) · [Deutsch](../de/PRIVACY.md) · [Español](../es/PRIVACY.md) · [فارسی](../fa/PRIVACY.md) · [Français](../fr/PRIVACY.md) · [हिन्दी](../hi/PRIVACY.md) · [Bahasa Indonesia](../id/PRIVACY.md) · Italiano · [日本語](../ja/PRIVACY.md) · [한국어](../ko/PRIVACY.md) · [Nederlands](../nl/PRIVACY.md) · [Polski](../pl/PRIVACY.md) · [Português](../pt/PRIVACY.md) · [Русский](../ru/PRIVACY.md) · [Svenska](../sv/PRIVACY.md) · [ไทย](../th/PRIVACY.md) · [Türkçe](../tr/PRIVACY.md) · [Українська](../uk/PRIVACY.md) · [Tiếng Việt](../vi/PRIVACY.md) · [中文](../zh/PRIVACY.md)

---

# Privacy

La privacy non è una funzionalità di VD Infos. È tutto il suo scopo. Questa pagina dichiara esattamente
cosa l'app fa e non fa con i tuoi dati, in termini chiari che puoi verificare.

## La promessa

- **Tutto funziona sul dispositivo.** Nessuna lettura viene inviata, caricata o trasmessa ad alcun
  server. L'app funziona pienamente con internet bloccato.
- **Nessun account, nessun accesso, nessun lavoro in background.** L'app legge solo mentre è aperta,
  non mantiene nulla in esecuzione in background e si chiude da sola quando viene lasciata inattiva.
- **Nulla cambia sul tuo sistema.** VD Infos si limita a leggere e confrontare; non scrive mai
  nello stato del tuo dispositivo.

## Il report resta con te

Un report lascia il dispositivo **solo quando lo condividi o lo salvi esplicitamente**. Fino ad allora esso
esiste in due posti, entrambi sul dispositivo:

- **Sullo schermo**, mentre l'app è aperta.
- Nell'**archivio privato** dell'app (`files/last_snapshot.json`), che solo l'app
  stessa o root possono leggere. La scansione successiva lo sovrascrive, ed esso non lascia mai il dispositivo.
  Esiste affinché gli strumenti e gli agenti AI possano leggere il risultato completo; vedi la
  [guida per gli agenti AI](AI-AGENTS.md).

## Le esportazioni sono mascherate

Quando condividi o salvi un report, i valori che portano un'identità - numeri di serie, IMEI, indirizzi
MAC, ID pubblicitari e simili - sono **mascherati** prima che il report venga scritto,
affinché un report che incolli in un thread pubblico non faccia trapelare i tuoi identificatori. I
verdetti e la struttura sono preservati; solo i valori sensibili sono oscurati. La
copia privata sul dispositivo conserva i valori reali per il tuo uso personale.

## Autorizzazioni

L'app si avvia senza alcuna autorizzazione richiesta. Alcune letture diventano disponibili solo se
concedi l'autorizzazione corrispondente (per esempio l'accesso all'utilizzo), e concederla è interamente
una tua scelta; quando una lettura non è disponibile, l'app lo dice semplicemente.

## Verificalo tu stesso

Non devi credere a nulla di tutto questo sulla fiducia:

- Il sorgente è pubblico: <https://github.com/VD171/VD-Infos>.
- Il manifest è nel sorgente; verifica quali autorizzazioni dichiara.
- Blocca l'accesso alla rete dell'app con un firewall, o spegni internet, ed essa continua a
  funzionare allo stesso modo.

Vedi la [guida utente](USER-GUIDE.md) per come leggere un report e cosa contiene l'esportazione.

<!-- doc-footer -->

---

> _Questa documentazione è stata generata con Claude (Anthropic) ed è gestita da VD171. Correzioni e miglioramenti sono benvenuti; vedi [CONTRIBUTING.md](CONTRIBUTING.md)._
