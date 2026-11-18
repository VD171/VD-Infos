> **Languages:** [English](../CONTRIBUTING.md) · [العربية](../ar/CONTRIBUTING.md) · [Čeština](../cs/CONTRIBUTING.md) · [Deutsch](../de/CONTRIBUTING.md) · [Español](../es/CONTRIBUTING.md) · [فارسی](../fa/CONTRIBUTING.md) · [Français](../fr/CONTRIBUTING.md) · [हिन्दी](../hi/CONTRIBUTING.md) · [Bahasa Indonesia](../id/CONTRIBUTING.md) · Italiano · [日本語](../ja/CONTRIBUTING.md) · [한국어](../ko/CONTRIBUTING.md) · [Nederlands](../nl/CONTRIBUTING.md) · [Polski](../pl/CONTRIBUTING.md) · [Português](../pt/CONTRIBUTING.md) · [Русский](../ru/CONTRIBUTING.md) · [Svenska](../sv/CONTRIBUTING.md) · [ไทย](../th/CONTRIBUTING.md) · [Türkçe](../tr/CONTRIBUTING.md) · [Українська](../uk/CONTRIBUTING.md) · [Tiếng Việt](../vi/CONTRIBUTING.md) · [中文](../zh/CONTRIBUTING.md)

---

# Contribuire

Grazie per il tuo interesse in VD Infos. I contributi sono benvenuti: liste di rilevamento,
traduzioni, segnalazioni di bug, documentazione e codice. La maggior parte dei contributi non richiede Kotlin.

## Il contributo più semplice: le liste

Gran parte di ciò che l'app cerca risiede come **liste di testo semplice** sotto
`VDInfos/app/src/main/assets/data/`, non come codice. Aggiungere o rimuovere un target è modificare
una riga e aprire una pull request:

- `*_apps.txt` - un nome di pacchetto per riga.
- `props.txt` - il catalogo delle proprietà di sistema, `CATEGORY<tab>key`.
- `spoof_keys.txt` - la matrice delle impostazioni, `key:TYPE`.
- `known_certs.txt` - il catalogo dei certificati di firma, `SHA-256<tab>owner` (l'hash
  completo, preso da un APK reale con `apksigner verify --print-certs`).
- altri file `.txt` - una voce per riga (frammenti di nomi di moduli del kernel, nomi di file,
  e così via).

Un `#` inizia un commento e le righe vuote vengono ignorate. Alcuni file contengono alcuni campi separati da spazi
o tabulazioni per riga, o marcatori facoltativi; mantieni la forma delle righe già
presenti nel file.

## Regole di base

- **VD Infos legge la verità e non falsifica mai nulla.** Segnala ciò che ogni metodo ha
  restituito, esattamente. Non ammorbidire una lettura per farla sembrare più bella, e non aggiungere
  nulla che cambierebbe un valore anziché leggerlo.
- **La privacy è assoluta.** Tutto funziona sul dispositivo. Non aggiungere mai una chiamata di rete, o
  qualsiasi cosa che invii una lettura fuori dal dispositivo.
- **Le esportazioni restano mascherate.** I valori che portano un'identità sono mascherati in un report condiviso. Mantienilo
  così.

## Traduzioni

Le traduzioni sono un contributo prezioso. Il readme è tradotto per lingua
(`README.<lang>.md`), le stringhe dell'app per lingua, e questa documentazione sotto
`docs/<lang>/`. Mantieni la terminologia coerente all'interno di una lingua, corrispondi al tono esistente,
e non tradurre mai automaticamente un termine tecnico di cui non sei sicuro.

## Segnalare un bug

Apri un problema con il modello del tuo dispositivo, la versione di Android, e, se riguarda una lettura, un'
**esportazione** condivisa (i suoi valori che portano un'identità sono mascherati). L'esportazione è costruita per
esattamente questo.

## Codice

- Compila in release, mai in debug. Testa su un dispositivo reale. Vedi [Compilazione](BUILDING.md).
- Tieni i commenti fuori dal codice; i nomi spiegano l'intento. L'unica intestazione è il banner
  della licenza in cima a ciascun file sorgente.
- Corrispondi allo stile esistente e mantieni le modifiche focalizzate.

## Licenza

Contribuendo accetti che il tuo contributo sia concesso in licenza sotto la
**GNU AGPL-3.0-or-later** del progetto.

<!-- doc-footer -->

---

> _Questa documentazione è stata generata con Claude (Anthropic) ed è gestita da VD171. Correzioni e miglioramenti sono benvenuti; vedi [CONTRIBUTING.md](CONTRIBUTING.md)._
