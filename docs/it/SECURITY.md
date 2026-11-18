> **Languages:** [English](../SECURITY.md) · [العربية](../ar/SECURITY.md) · [Čeština](../cs/SECURITY.md) · [Deutsch](../de/SECURITY.md) · [Español](../es/SECURITY.md) · [فارسی](../fa/SECURITY.md) · [Français](../fr/SECURITY.md) · [हिन्दी](../hi/SECURITY.md) · [Bahasa Indonesia](../id/SECURITY.md) · Italiano · [日本語](../ja/SECURITY.md) · [한국어](../ko/SECURITY.md) · [Nederlands](../nl/SECURITY.md) · [Polski](../pl/SECURITY.md) · [Português](../pt/SECURITY.md) · [Русский](../ru/SECURITY.md) · [Svenska](../sv/SECURITY.md) · [ไทย](../th/SECURITY.md) · [Türkçe](../tr/SECURITY.md) · [Українська](../uk/SECURITY.md) · [Tiếng Việt](../vi/SECURITY.md) · [中文](../zh/SECURITY.md)

---

# Politica di sicurezza

VD Infos è uno strumento per la privacy che legge lo stato sensibile del dispositivo, perciò le segnalazioni di
sicurezza sono prese sul serio.

## Segnalare una vulnerabilità

Si prega di segnalare i problemi di sicurezza **in privato**, non in un problema pubblico:

- Invia un'e-mail a **vd.priv8@pm.me**.
- Descrivi il problema, la versione interessata e come riprodurlo.
- Se riguarda l'esposizione di dati, descrivi la classe del problema; non è necessario allegare
  un exploit funzionante.

Riceverai una risposta il prima possibile. Si prega di concedere una finestra ragionevole per correggere il
problema prima di qualsiasi divulgazione pubblica.

## Cosa conta come un problema di sicurezza

Per via di ciò che VD Infos promette, questi sono particolarmente importanti:

- Qualsiasi cosa che farebbe raggiungere all'app la rete, o inviare qualsiasi lettura fuori dal
  dispositivo.
- Qualsiasi cosa che farebbe trapelare un valore che porta un'identità in un'**esportazione** condivisa o salvata
  (un numero di serie, IMEI, indirizzo MAC, ID pubblicitario e simili) nonostante il mascheramento.
- Qualsiasi cosa che permetta a un'altra app di leggere il file di report privato che è destinato a essere
  leggibile solo dall'app stessa o da root.

## Cosa non è un problema di sicurezza

- Una lettura che sembra "sbagliata" ma è il valore onesto che un metodo ha restituito. VD Infos
  segnala ciò che ogni metodo ha detto; leggere il confronto spetta a te. Vedi la
  [guida utente](USER-GUIDE.md).
- Una lettura contrassegnata come non disponibile. Significa che un metodo non ha potuto leggere quel valore sul tuo
  dispositivo, il che è di per sé parte del confronto.

## Versioni supportate

Solo l'ultima versione rilasciata è supportata. Si prega di riprodurre sull'ultima build
prima di segnalare.

<!-- doc-footer -->

---

> _Questa documentazione è stata generata con Claude (Anthropic) ed è gestita da VD171. Correzioni e miglioramenti sono benvenuti; vedi [CONTRIBUTING.md](CONTRIBUTING.md)._
