> **Languages:** [English](../AI-AGENTS.md) · [العربية](../ar/AI-AGENTS.md) · [Čeština](../cs/AI-AGENTS.md) · Deutsch · [Español](../es/AI-AGENTS.md) · [فارسی](../fa/AI-AGENTS.md) · [Français](../fr/AI-AGENTS.md) · [हिन्दी](../hi/AI-AGENTS.md) · [Bahasa Indonesia](../id/AI-AGENTS.md) · [Italiano](../it/AI-AGENTS.md) · [日本語](../ja/AI-AGENTS.md) · [한국어](../ko/AI-AGENTS.md) · [Nederlands](../nl/AI-AGENTS.md) · [Polski](../pl/AI-AGENTS.md) · [Português](../pt/AI-AGENTS.md) · [Русский](../ru/AI-AGENTS.md) · [Svenska](../sv/AI-AGENTS.md) · [ไทย](../th/AI-AGENTS.md) · [Türkçe](../tr/AI-AGENTS.md) · [Українська](../uk/AI-AGENTS.md) · [Tiếng Việt](../vi/AI-AGENTS.md) · [中文](../zh/AI-AGENTS.md)

---

# VD Infos für KI-Agenten

VD Infos ist **dafür gebaut, von Maschinen gelesen zu werden, nicht nur von Menschen**.
Ein Geräte-Methoden-Debugger erzeugt eine Menge strukturierter Beweise - jeden Wert,
gelesen über jede Methode, aufgereiht zum Vergleich - und diese Beweise sind für einen
Agenten als Daten weit nützlicher denn als Screenshot. Deshalb ist das gesamte Ergebnis
als ein einziges maschinenlesbares Dokument verfügbar, und die App ist von Grund auf
darum herum entworfen.

Wenn Sie ein KI-Programmierassistent, ein LLM-Werkzeug, ein Analyseskript oder
irgendeine Automatisierung sind, die das Gerät über eine Shell erreicht, ist diese Seite
für Sie.

## Warum es agentenfreundlich ist

- **Ein Dokument, kein Bildschirm.** Der gesamte Scan - jede Sonde, jede Linse, jedes
  Urteil - ist ein einziges JSON-Objekt. Kein OCR, kein Screen-Scraping, kein
  Durchtippen eingeklappter Karten.
- **Dieselben Daten, die die Benutzeroberfläche hat.** Die Datei ist keine
  Zusammenfassung. Sie ist genau das, was die App intern vorhält: der Bildschirm zeigt
  den ersten Messwert eines eingeklappten Eintrags, die Datei hat alle.
- **Deterministische Form.** Die Struktur ist stabil und dokumentiert (siehe
  [EXPORT-FORMAT](EXPORT-FORMAT.md)), sodass ein einmal geschriebener Parser weiter
  funktioniert.
- **Kein Netzwerk, kein Konto, kein Zustand zu verwalten.** Die App liest auf dem Gerät
  und schreibt eine Datei. Ihr Agent liest die Datei. Das ist der ganze Vertrag.
- **Offline von Grund auf.** Es benötigt kein Internet, sodass es genauso in einer
  abgeschotteten oder luftisolierten Umgebung läuft, in der ein Agent möglicherweise
  arbeitet.

## Das Lesen mit einem Befehl

Jeder Scan schreibt seinen vollständigen Bericht in den privaten Speicher der App.
Nachdem ein Scan gelaufen ist, liest ein Agent das vollständige Ergebnis mit einem
einzigen Befehl:

```sh
su -c cat /data/data/ru.vd171.vdinfos/files/last_snapshot.json
```

- **Was sie enthält:** jede Sonde mit ihrem Urteil und dem Wert jeder Linse, im selben
  JSON wie die Export-Schaltfläche.
- **Wann sie sich aktualisiert:** am Ende jedes Scans. Führen Sie zuerst einen Scan aus;
  die Datei enthält immer den neuesten.
- **Voraussetzung:** root, weil die Datei im privaten Speicher der App liegt. Ohne root
  verwenden Sie stattdessen die In-App-Aktion **Teilen / Speichern als JSON** und lesen
  die Datei, die dem Agenten übergeben wird.

## Ein typischer Agenten-Ablauf

1. Stellen Sie sicher, dass die App installiert ist (das **SDK_35**-APK von
   [Releases](https://github.com/VD171/VD-Infos/releases)).
2. Lösen Sie einen Scan aus - eine Person tippt auf Scan, oder eine Automatisierung
   startet den Scan-Bildschirm der App - und warten Sie, bis er fertig ist.
3. Lesen Sie `files/last_snapshot.json` mit dem obigen Befehl.
4. Parsen Sie es mit dem [Exportformat](EXPORT-FORMAT.md) als Ihr Schema: iterieren Sie
   über die Sonden, lesen Sie das `verdict` jeder einzelnen und reihen Sie die
   `values` pro Linse auf.
5. Handeln Sie nach dem strukturierten Ergebnis in Ihrem eigenen Arbeitsablauf.

Der Agent entscheidet, was die Messwerte für die jeweilige Aufgabe bedeuten; VD Infos
liefert die Beweise, sauber und vollständig.

## Was der Agent bekommt und was nicht

- Der Bericht trägt **jeden Messwert und jedes Urteil**, sodass ein Agent das
  vollständige Bild hat, das die App aufgebaut hat.
- Identitätstragende Werte (Seriennummern, IMEI, MAC-Adressen, Werbe-IDs und
  dergleichen) sind **im geteilten/exportierten Bericht maskiert**, sodass ein
  weitergereichter Bericht frei von persönlichen Kennungen bleibt, während er seine
  Urteile und Struktur behält. Die `last_snapshot.json` **auf dem Gerät**, die nur von
  der App oder root lesbar ist, behält die echten Werte für einen lokalen Agenten, der
  sie benötigt.

## Dokumentation, zum Parsen geschrieben

Dieses Wiki ist ebenso sehr für das maschinelle Lesen strukturiert wie für Menschen:
kurze Abschnitte, stabile Überschriften, explizite Feldnamen und eine eigene
[Exportformat](EXPORT-FORMAT.md)-Seite, die ein Agent als Schema behandeln kann. Wenn Sie
ein Werkzeug auf VD Infos aufbauen, beginnen Sie dort.

## Verwandte Seiten

- [Exportformat](EXPORT-FORMAT.md) - das JSON-Schema, Feld für Feld.
- [Benutzerhandbuch](USER-GUIDE.md) - Installation, Scannen und Teilen.
- [Architektur](ARCHITECTURE.md) - wie die App zusammengesetzt ist.

<!-- doc-footer -->

---

> _Diese Dokumentation wurde mit Claude (Anthropic) erstellt und wird von VD171 gepflegt. Korrekturen und Verbesserungen sind willkommen; siehe [CONTRIBUTING.md](CONTRIBUTING.md)._
