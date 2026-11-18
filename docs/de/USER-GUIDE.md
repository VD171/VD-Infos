> **Languages:** [English](../USER-GUIDE.md) · [العربية](../ar/USER-GUIDE.md) · [Čeština](../cs/USER-GUIDE.md) · Deutsch · [Español](../es/USER-GUIDE.md) · [فارسی](../fa/USER-GUIDE.md) · [Français](../fr/USER-GUIDE.md) · [हिन्दी](../hi/USER-GUIDE.md) · [Bahasa Indonesia](../id/USER-GUIDE.md) · [Italiano](../it/USER-GUIDE.md) · [日本語](../ja/USER-GUIDE.md) · [한국어](../ko/USER-GUIDE.md) · [Nederlands](../nl/USER-GUIDE.md) · [Polski](../pl/USER-GUIDE.md) · [Português](../pt/USER-GUIDE.md) · [Русский](../ru/USER-GUIDE.md) · [Svenska](../sv/USER-GUIDE.md) · [ไทย](../th/USER-GUIDE.md) · [Türkçe](../tr/USER-GUIDE.md) · [Українська](../uk/USER-GUIDE.md) · [Tiếng Việt](../vi/USER-GUIDE.md) · [中文](../zh/USER-GUIDE.md)

---

# VD Infos Benutzerhandbuch

VD Infos ist ein **Geräte-Methoden-Debugger**. Es liest jeden Gerätewert über jede
Methode, die ihn lesen kann, und stellt die Messwerte nebeneinander, damit Sie sie
vergleichen können. Dieses Handbuch behandelt die Installation der App, das Ausführen
eines Scans, das Lesen des Ergebnisses und das Teilen eines Berichts. Es deutet keinen
bestimmten Befund für Sie: der Vergleich ist Ihrer, den Sie auf Ihrem eigenen Gerät
lesen.

## Inhaltsverzeichnis

1. [Was VD Infos ist](#1-was-vd-infos-ist)
2. [Was es nicht ist](#2-was-es-nicht-ist)
3. [Voraussetzungen](#3-voraussetzungen)
4. [Welches APK installiert werden soll](#4-welches-apk-installiert-werden-soll)
5. [Installation und erster Start](#5-installation-und-erster-start)
6. [Einen Scan ausführen](#6-einen-scan-ausführen)
7. [Das Ergebnis lesen](#7-das-ergebnis-lesen)
8. [Ihren Bericht exportieren](#8-ihren-bericht-exportieren)
9. [Für KI-Agenten](#9-für-ki-agenten)
10. [Sprache und Design](#10-sprache-und-design)
11. [Datenschutz auf einen Blick](#11-datenschutz-auf-einen-blick)
12. [Häufig gestellte Fragen](#12-häufig-gestellte-fragen)

## 1. Was VD Infos ist

Android übergibt jeder App, die Sie installieren, eine Menge an Informationen über Ihr
Gerät. VD Infos zeigt Ihnen ein Beispiel dafür, was eine gewöhnliche App lesen kann, und
tut dies als *Methoden-Debugger*: für jede Information liest es den Wert über **jede
Methode, die ihn lesen kann** - Plattform-APIs, Systemeigenschaften, native Aufrufe,
System-Manager, Content-Provider, Dateien, Syscalls und
hardwaregestützte Schlüsselattestierung (TEE) - und stellt sie nebeneinander, damit Sie
vergleichen können. Es ist eine Linse, keine Urteilsmaschine: es bringt die Messwerte
zum Vorschein und überlässt das Lesen Ihnen.

## 2. Was es nicht ist

- Es ist **kein** Cleaner, kein Booster und kein Verbergungswerkzeug. Es ändert nichts
  an Ihrem System; es liest und vergleicht nur.
- Es ist **keine** Bestanden/Nicht-bestanden-Bewertung. Es zeigt Ihnen die rohen
  Messwerte; was sie für Ihre Konfiguration bedeuten, ist Ihre Beurteilung, auf Ihrem
  Gerät.
- Es **benötigt** kein Internet und funktioniert vollständig bei blockiertem Internet.

## 3. Voraussetzungen

- Android 8.0 (API 26) oder neuer.
- Zum Start ist keine besondere Berechtigung erforderlich. Einige Messwerte werden erst
  verfügbar, wenn Sie die passende Berechtigung erteilen (zum Beispiel Nutzungszugriff),
  und einige existieren nur auf einem gerooteten Gerät; wo ein Messwert nicht verfügbar
  ist, zeigt die App einfach an, dass er nicht verfügbar war.

## 4. Welches APK installiert werden soll

Jede Veröffentlichung veröffentlicht **zwei APKs**:

- **SDK_35** - zielt auf die strikte moderne Sandbox. Dieses ist zu verwenden.
- **SDK_27** - zielt auf eine ältere API-Ebene. Es
  ist nur für den direkten Vergleich nützlich; die meisten Leute brauchen es nicht.

Beide installieren sich auf Android 8.0 und neuer. Laden Sie sie von der
[Releases](https://github.com/VD171/VD-Infos/releases)-Seite herunter.

## 5. Installation und erster Start

1. Laden Sie das **SDK_35**-APK von Releases herunter und installieren Sie es (Sie
   müssen möglicherweise Installationen aus Ihrem Browser oder Dateimanager erlauben).
2. Öffnen Sie die App. Sie öffnet sich direkt im Scan-Bildschirm; es gibt kein Konto,
   keine Anmeldung und keine Einrichtung.
3. Erteilen Sie optional eine Berechtigung, nach der die App fragt (etwa
   Nutzungszugriff), um die davon abhängigen Messwerte freizuschalten. Dies ist völlig
   optional.

## 6. Einen Scan ausführen

Tippen Sie auf die Scan-Aktion. Die App fächert ihre Sonden parallel auf und streamt die
Ergebnisse in die Liste, sobald sie eintreffen, mit einer Live-Fortschrittsanzeige. Ein
Scan schließt sich von selbst ab; er liest nur, während die App geöffnet ist, lässt
nichts im Hintergrund laufen und schließt sich selbst, wenn er im Leerlauf bleibt.

Um ihn erneut auszuführen, scannen Sie erneut: jeder Scan ersetzt das vorherige
Ergebnis.

## 7. Das Ergebnis lesen

Jeder Eintrag ist eine Karte, die die Messwerte jeder Linse (die Java-SDK-Methode, die
native Methode, die Shell und so weiter) aufreiht. Die Farbe der Karte sagt Ihnen auf
einen Blick, wie diese Messwerte zueinander stehen:

- **Übereinstimmung** - jede Linse war sich einig. Die Methoden gaben denselben Wert
  zurück.
- **Abweichung** - die Linsen waren sich nicht einig. Eine Methode gab etwas anderes
  zurück als die übrigen.
- **Information** - ein Messwert, der zur Referenz angezeigt wird, nicht abgestimmt.
- **Einzeln / nicht verfügbar** - nur eine Linse konnte ihn lesen, oder keine konnte es.

Die Karte zeigt immer die vollständige Matrix der Messwerte, damit Sie genau sehen
können, welche Linse was gesagt hat. Eine eingeklappte Karte zeigt ihren ersten
Messwert; klappen Sie sie auf, um alle zu sehen.

So weit spricht die App für Sie. **Was eine bestimmte Abweichung auf Ihrem Gerät
bedeutet und was dagegen zu tun ist, ist Ihre Sache zu untersuchen** - das ist der ganze
Sinn eines Debuggers: er legt die Beweise vor Sie und lässt Sie sie lesen. Verwenden Sie
das Suchfeld, um nach Eigenschaft oder Wert zu filtern, und die Filter-Chips, um die
Liste einzugrenzen.

## 8. Ihren Bericht exportieren

Die Aktionen zum Teilen und Speichern übergeben Ihnen das **gesamte Ergebnis**, nicht
nur das, was auf dem Bildschirm ist:

- **Teilen / Speichern als JSON** - der vollständige Bericht in maschinenlesbarer Form,
  dieselben Daten, die die App intern vorhält. Dies ist das Format zum Aufbewahren oder
  zum Einspeisen in ein Werkzeug.
- **Als Text teilen** - eine kompakte, menschenlesbare Zusammenfassung der Abweichungen.

Identitätstragende Werte (Seriennummern, IMEI, MAC-Adressen, Werbe-IDs und dergleichen)
werden **im exportierten Bericht maskiert**, damit ein Bericht, den Sie in einen
öffentlichen Thread einfügen, nicht Ihre eigenen Kennungen preisgibt; die Urteile und
die Struktur bleiben erhalten. Die Kopie auf dem Gerät, die nur Sie (oder root) lesen
können, behält die echten Werte.

## 9. Für KI-Agenten

VD Infos ist dafür gebaut, von Werkzeugen gelesen zu werden, nicht nur von Menschen.
Jeder Scan schreibt seinen vollständigen Bericht in den privaten Speicher der App, und
der Export ist dasselbe maschinenlesbare JSON. Ein KI-Programmierassistent oder ein
Analyseskript, das auf dem Gerät läuft, kann das gesamte Ergebnis mit einem einzigen
Befehl lesen, statt Screenshots zu parsen. Siehe den eigenen
[KI-Agenten-Leitfaden](AI-AGENTS.md) und die [Exportformat](EXPORT-FORMAT.md)-Referenz.

## 10. Sprache und Design

Die App folgt Ihrer Systemsprache, wo eine Übersetzung existiert, und Sie können eine
manuell auswählen. Sie verwendet Material 3 mit dynamischer Farbe und folgt Ihrer
Hell/Dunkel-Einstellung.

## 11. Datenschutz auf einen Blick

Alles läuft auf dem Gerät. Kein Messwert wird irgendwohin gesendet, und ein Bericht
verlässt das Gerät nur, wenn Sie ihn ausdrücklich teilen oder speichern. Jeder Scan
behält seinen vollständigen Bericht im privaten Speicher der App
(`files/last_snapshot.json`), damit Werkzeuge ihn lesen können; nur die App selbst oder
root können diese Datei lesen, der nächste Scan überschreibt sie, und sie verlässt
niemals das Gerät. Siehe [Datenschutz](PRIVACY.md) für die vollständige Erklärung.

## 12. Häufig gestellte Fragen

**Ändert es irgendetwas an meinem Gerät?** Nein. Es liest und vergleicht nur.

**Benötigt es root?** Nein. Die App läuft ohne root. Eine Handvoll Messwerte existiert
nur auf einem gerooteten Gerät, und die private Berichtsdatei kann nur von root oder der
App selbst zurückgelesen werden; alles andere funktioniert ohne root.

**Benötigt es das Internet?** Nein. Es funktioniert vollständig offline; blockieren Sie
seinen Netzwerkzugriff, wenn Sie möchten.

**Welches APK sollte ich verwenden?** SDK_35. Der SDK_27-Build existiert nur zum
Vergleich.

**Warum sagen einige Einträge, dass sie nicht verfügbar sind?** Weil diese Methode
diesen Wert auf Ihrem Gerät nicht lesen konnte - eine fehlende Berechtigung, eine
Sandbox-Regel oder nicht vorhandene Hardware. Das ist selbst Teil des Vergleichs.

**Wo melde ich ein Problem oder schlage ein Erkennungsziel vor?** Siehe
[Mitwirken](CONTRIBUTING.md); die meisten Erkennungslisten sind reiner Text und
benötigen keinen Code.

<!-- doc-footer -->

---

> _Diese Dokumentation wurde mit Claude (Anthropic) erstellt und wird von VD171 gepflegt. Korrekturen und Verbesserungen sind willkommen; siehe [CONTRIBUTING.md](CONTRIBUTING.md)._
