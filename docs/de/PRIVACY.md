> **Languages:** [English](../PRIVACY.md) · [العربية](../ar/PRIVACY.md) · [Čeština](../cs/PRIVACY.md) · Deutsch · [Español](../es/PRIVACY.md) · [فارسی](../fa/PRIVACY.md) · [Français](../fr/PRIVACY.md) · [हिन्दी](../hi/PRIVACY.md) · [Bahasa Indonesia](../id/PRIVACY.md) · [Italiano](../it/PRIVACY.md) · [日本語](../ja/PRIVACY.md) · [한국어](../ko/PRIVACY.md) · [Nederlands](../nl/PRIVACY.md) · [Polski](../pl/PRIVACY.md) · [Português](../pt/PRIVACY.md) · [Русский](../ru/PRIVACY.md) · [Svenska](../sv/PRIVACY.md) · [ไทย](../th/PRIVACY.md) · [Türkçe](../tr/PRIVACY.md) · [Українська](../uk/PRIVACY.md) · [Tiếng Việt](../vi/PRIVACY.md) · [中文](../zh/PRIVACY.md)

---

# Datenschutz

Datenschutz ist kein Feature von VD Infos. Er ist der ganze Sinn. Diese Seite erklärt
genau, was die App mit Ihren Daten tut und was nicht, in klaren Worten, die Sie
überprüfen können.

## Das Versprechen

- **Alles läuft auf dem Gerät.** Kein Messwert wird an irgendeinen Server gesendet,
  hochgeladen oder übertragen. Die App funktioniert vollständig bei blockiertem Internet.
- **Kein Konto, keine Anmeldung, keine Hintergrundarbeit.** Die App liest nur, während
  sie geöffnet ist, lässt nichts im Hintergrund laufen und schließt sich selbst, wenn sie
  im Leerlauf bleibt.
- **Nichts ändert sich an Ihrem System.** VD Infos liest und vergleicht nur; es schreibt
  niemals in den Zustand Ihres Geräts.

## Der Bericht bleibt bei Ihnen

Ein Bericht verlässt das Gerät **nur, wenn Sie ihn ausdrücklich teilen oder speichern**.
Bis dahin existiert er an zwei Stellen, beide auf dem Gerät:

- **Auf dem Bildschirm**, während die App geöffnet ist.
- Im **privaten Speicher** der App (`files/last_snapshot.json`), den nur die App selbst
  oder root lesen kann. Der nächste Scan überschreibt ihn, und er verlässt niemals das
  Gerät. Er existiert, damit Werkzeuge und KI-Agenten das vollständige Ergebnis lesen
  können; siehe den [KI-Agenten-Leitfaden](AI-AGENTS.md).

## Exporte sind maskiert

Wenn Sie einen Bericht teilen oder speichern, werden identitätstragende Werte -
Seriennummern, IMEI, MAC-Adressen, Werbe-IDs und dergleichen - **maskiert**, bevor der
Bericht geschrieben wird, damit ein Bericht, den Sie in einen öffentlichen Thread
einfügen, nicht Ihre eigenen Kennungen preisgibt. Die Urteile und die Struktur bleiben
erhalten; nur die sensiblen Werte werden geschwärzt. Die private Kopie auf dem Gerät
behält die echten Werte zu Ihrer eigenen Verwendung.

## Berechtigungen

Die App startet, ohne dass eine Berechtigung erforderlich ist. Einige Messwerte werden
erst verfügbar, wenn Sie die passende Berechtigung erteilen (zum Beispiel
Nutzungszugriff), und sie zu erteilen ist völlig Ihre Wahl; wo ein Messwert nicht
verfügbar ist, sagt die App das einfach.

## Überprüfen Sie es selbst

Sie müssen nichts davon auf Treu und Glauben hinnehmen:

- Der Quellcode ist öffentlich: <https://github.com/VD171/VD-Infos>.
- Das Manifest liegt im Quellcode; bestätigen Sie, welche Berechtigungen es deklariert.
- Blockieren Sie den Netzwerkzugriff der App mit einer Firewall oder schalten Sie das
  Internet aus, und sie funktioniert genauso weiter.

Siehe das [Benutzerhandbuch](USER-GUIDE.md) dafür, wie man einen Bericht liest und was
der Export enthält.

<!-- doc-footer -->

---

> _Diese Dokumentation wurde mit Claude (Anthropic) erstellt und wird von VD171 gepflegt. Korrekturen und Verbesserungen sind willkommen; siehe [CONTRIBUTING.md](CONTRIBUTING.md)._
