> **Languages:** [English](../CONTRIBUTING.md) · [العربية](../ar/CONTRIBUTING.md) · [Čeština](../cs/CONTRIBUTING.md) · Deutsch · [Español](../es/CONTRIBUTING.md) · [فارسی](../fa/CONTRIBUTING.md) · [Français](../fr/CONTRIBUTING.md) · [हिन्दी](../hi/CONTRIBUTING.md) · [Bahasa Indonesia](../id/CONTRIBUTING.md) · [Italiano](../it/CONTRIBUTING.md) · [日本語](../ja/CONTRIBUTING.md) · [한국어](../ko/CONTRIBUTING.md) · [Nederlands](../nl/CONTRIBUTING.md) · [Polski](../pl/CONTRIBUTING.md) · [Português](../pt/CONTRIBUTING.md) · [Русский](../ru/CONTRIBUTING.md) · [Svenska](../sv/CONTRIBUTING.md) · [ไทย](../th/CONTRIBUTING.md) · [Türkçe](../tr/CONTRIBUTING.md) · [Українська](../uk/CONTRIBUTING.md) · [Tiếng Việt](../vi/CONTRIBUTING.md) · [中文](../zh/CONTRIBUTING.md)

---

# Mitwirken

Danke für Ihr Interesse an VD Infos. Beiträge sind willkommen: Erkennungslisten,
Übersetzungen, Fehlerberichte, Dokumentation und Code. Die meisten Beiträge brauchen kein
Kotlin.

## Der einfachste Beitrag: die Listen

Das meiste, wonach die App sucht, liegt als **reine Textlisten** unter
`VDInfos/app/src/main/assets/data/`, nicht als Code. Ein Ziel hinzuzufügen oder zu
entfernen bedeutet, eine Zeile zu bearbeiten und einen Pull Request zu öffnen:

- `*_apps.txt` - ein Paketname pro Zeile.
- `props.txt` - der System-Eigenschaftskatalog, `CATEGORY<tab>key`.
- `spoof_keys.txt` - die Einstellungsmatrix, `key:TYPE`.
- `known_certs.txt` - der Signaturzertifikat-Katalog, `SHA-256<tab>owner` (der
  vollständige Hash, entnommen aus einem echten APK mit `apksigner verify --print-certs`).
- andere `.txt`-Dateien - ein Eintrag pro Zeile (Kernelmodul-Namensfragmente, Dateinamen
  und so weiter).

Ein `#` beginnt einen Kommentar und Leerzeilen werden ignoriert. Einige Dateien packen
ein paar durch Leerzeichen oder Tabs getrennte Felder pro Zeile oder optionale Markierer;
behalten Sie die Form der Zeilen bei, die bereits in der Datei stehen.

## Grundregeln

- **VD Infos liest die Wahrheit und fälscht niemals etwas.** Es meldet genau das, was
  jede Methode zurückgab. Mildern Sie keinen Messwert, um ihn schöner aussehen zu lassen,
  und fügen Sie nichts hinzu, das einen Wert ändern statt ihn lesen würde.
- **Datenschutz ist absolut.** Alles läuft auf dem Gerät. Fügen Sie niemals einen
  Netzwerkaufruf hinzu oder irgendetwas, das einen Messwert vom Gerät sendet.
- **Exporte bleiben maskiert.** Identitätstragende Werte werden in einem geteilten
  Bericht maskiert. Behalten Sie das bei.

## Übersetzungen

Übersetzungen sind ein wertvoller Beitrag. Die Readme wird pro Sprache übersetzt
(`README.<lang>.md`), die App-Strings pro Sprache und diese Dokumentation unter
`docs/<lang>/`. Halten Sie die Terminologie innerhalb einer Sprache konsistent, passen
Sie sich dem bestehenden Ton an und übersetzen Sie niemals maschinell einen Fachbegriff,
bei dem Sie unsicher sind.

## Einen Fehler melden

Öffnen Sie ein Issue mit Ihrem Gerätemodell, der Android-Version und, wenn es um einen
Messwert geht, einem geteilten **Export** (dessen identitätstragende Werte maskiert
sind). Der Export ist genau dafür gebaut.

## Code

- Build release, niemals debug. Testen Sie auf einem echten Gerät. Siehe
  [Bauen](BUILDING.md).
- Halten Sie Kommentare aus dem Code heraus; Namen erklären die Absicht. Der einzige
  Header ist das Lizenzbanner am Anfang jeder Quelldatei.
- Passen Sie sich dem bestehenden Stil an und halten Sie Änderungen fokussiert.

## Lizenz

Durch Ihren Beitrag stimmen Sie zu, dass Ihr Beitrag unter der
**GNU AGPL-3.0-or-later** des Projekts lizenziert wird.

<!-- doc-footer -->

---

> _Diese Dokumentation wurde mit Claude (Anthropic) erstellt und wird von VD171 gepflegt. Korrekturen und Verbesserungen sind willkommen; siehe [CONTRIBUTING.md](CONTRIBUTING.md)._
