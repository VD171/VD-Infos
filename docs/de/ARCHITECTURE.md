> **Languages:** [English](../ARCHITECTURE.md) · [العربية](../ar/ARCHITECTURE.md) · [Čeština](../cs/ARCHITECTURE.md) · Deutsch · [Español](../es/ARCHITECTURE.md) · [فارسی](../fa/ARCHITECTURE.md) · [Français](../fr/ARCHITECTURE.md) · [हिन्दी](../hi/ARCHITECTURE.md) · [Bahasa Indonesia](../id/ARCHITECTURE.md) · [Italiano](../it/ARCHITECTURE.md) · [日本語](../ja/ARCHITECTURE.md) · [한국어](../ko/ARCHITECTURE.md) · [Nederlands](../nl/ARCHITECTURE.md) · [Polski](../pl/ARCHITECTURE.md) · [Português](../pt/ARCHITECTURE.md) · [Русский](../ru/ARCHITECTURE.md) · [Svenska](../sv/ARCHITECTURE.md) · [ไทย](../th/ARCHITECTURE.md) · [Türkçe](../tr/ARCHITECTURE.md) · [Українська](../uk/ARCHITECTURE.md) · [Tiếng Việt](../vi/ARCHITECTURE.md) · [中文](../zh/ARCHITECTURE.md)

---

# Architektur

Dies ist eine Übersichtskarte, wie VD Infos zusammengesetzt ist. Sie beschreibt die Form
der App, nicht den Katalog dessen, was sie liest: der Sinn eines Methoden-Debuggers ist
es, jeden Messwert vor Sie zu legen, und das Lesen ist Ihre Sache, auf Ihrem eigenen
Gerät.

## Entwurfsziele

- **Ein Wert, jede Methode.** Lesen Sie jede Information über jede Methode, die sie lesen
  kann, und reihen Sie die Messwerte auf. Ein Debugger bringt Beweise zum Vorschein; er
  fällt kein Urteil.
- **Auf dem Gerät und leise.** Kein Netzwerk, kein Konto, keine Hintergrundarbeit. Die
  App läuft nur, während sie geöffnet ist.
- **Von Maschinen lesbar.** Das gesamte Ergebnis ist ein maschinenlesbares Dokument,
  sodass ein Werkzeug dasselbe Bild erhält wie ein Mensch. Siehe
  [KI-Agenten](AI-AGENTS.md).
- **Klein, wo es zählt.** Die native Komponente wird bewusst winzig und einfach
  gehalten.

## Die Linsen

Eine *Linse* ist eine Art, einen Wert zu lesen. Derselbe Wert wird über mehrere gelesen,
damit sie verglichen werden können:

| Linse | Was es ist |
|---|---|
| **Java / SDK** | Die Plattform-API, die eine gewöhnliche App aufruft. |
| **Nativ / JNI** | Derselbe Wert, gelesen aus nativem Code. |
| **Shell über JVM** | Ein von der JVM erzeugter Shell-Befehl. |
| **Shell über JNI** | Derselbe Befehl, erzeugt aus nativem Code. |
| **TEE / Attestierung** | Hardwaregestützte Schlüsselattestierung, wo sie zutrifft. |

Die Linsen aufzureihen ist die ganze Idee: Die App zeigt, wo die Lesungen übereinstimmen
und wo sie sich unterscheiden. Was ein Unterschied für einen bestimmten Wert, auf einem
bestimmten Gerät, bedeutet, ist Ihre Sache zu lesen.

## Wie ein Scan abläuft

```
core/model      immutable domain: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          the probes and the bridges they use (JNI, reflection, data lists)
engine/         ProbeEngine: parallel fan-out, streams results as they land
data/           SnapshotStore (persist the report), Exporter (share as JSON / text)
ui/             Jetpack Compose, Material 3, dynamic colour, live progress
cpp/            native_probes.cpp: the native lens, dependency-free
```

- **Parallelität.** Die Sonden fächern über einen Dispatcher mit einer begrenzten
  Anzahl an Berechtigungen auf; die Ergebnisse streamen in die Benutzeroberfläche,
  sobald sie fertig sind, mit einer Live-Fortschrittsanzeige.
- **Nichts im Hintergrund.** Keine Dienste und keine geplanten Scans. Die App liest nur,
  während sie geöffnet ist, und schließt sich selbst, wenn sie im Leerlauf bleibt.
- **Die native Schicht.** Eine kleine, abhängigkeitsfreie `.so`, per Name über
  `RegisterNatives` gebunden, bewusst winzig und einfach gehalten.

## Der Bericht

Jeder Scan erzeugt einen `Snapshot`: die Zeit, zu der er erstellt wurde, die App-Version
und eine Liste von Ergebnissen, eines pro Sonde, jedes mit den Messwerten jeder Linse und
einem Urteil, wie sie zueinander stehen. Die App speichert dies nach jedem Scan in den
privaten Speicher und kann dieselben Daten exportieren. Die Struktur ist als
[Exportformat](EXPORT-FORMAT.md) dokumentiert.

## Es erweitern

Das meiste, wonach die App sucht, liegt als **reine Textlisten** unter
`assets/data/`, nicht als Code. Ein Ziel hinzuzufügen oder zu entfernen bedeutet, eine
Zeile zu bearbeiten. Siehe [Mitwirken](CONTRIBUTING.md).

## Was VD Infos niemals tut

- Es ändert niemals etwas an Ihrem System. Es liest und vergleicht nur.
- Es spricht niemals mit dem Netzwerk. Es funktioniert vollständig offline.
- Es läuft niemals im Hintergrund, plant keinen Scan und hält keinen Dienst am Leben.
- Es sendet niemals einen Messwert irgendwohin. Ein Bericht verlässt das Gerät nur, wenn
  Sie ihn teilen oder speichern, mit maskierten identitätstragenden Werten.

<!-- doc-footer -->

---

> _Diese Dokumentation wurde mit Claude (Anthropic) erstellt und wird von VD171 gepflegt. Korrekturen und Verbesserungen sind willkommen; siehe [CONTRIBUTING.md](CONTRIBUTING.md)._
