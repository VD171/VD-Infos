> **Languages:** [English](../SECURITY.md) · [العربية](../ar/SECURITY.md) · [Čeština](../cs/SECURITY.md) · Deutsch · [Español](../es/SECURITY.md) · [فارسی](../fa/SECURITY.md) · [Français](../fr/SECURITY.md) · [हिन्दी](../hi/SECURITY.md) · [Bahasa Indonesia](../id/SECURITY.md) · [Italiano](../it/SECURITY.md) · [日本語](../ja/SECURITY.md) · [한국어](../ko/SECURITY.md) · [Nederlands](../nl/SECURITY.md) · [Polski](../pl/SECURITY.md) · [Português](../pt/SECURITY.md) · [Русский](../ru/SECURITY.md) · [Svenska](../sv/SECURITY.md) · [ไทย](../th/SECURITY.md) · [Türkçe](../tr/SECURITY.md) · [Українська](../uk/SECURITY.md) · [Tiếng Việt](../vi/SECURITY.md) · [中文](../zh/SECURITY.md)

---

# Sicherheitsrichtlinie

VD Infos ist ein Datenschutzwerkzeug, das sensiblen Gerätezustand liest, daher werden
Sicherheitsmeldungen ernst genommen.

## Eine Schwachstelle melden

Bitte melden Sie Sicherheitsprobleme **privat**, nicht in einem öffentlichen Issue:

- E-Mail **vd.priv8@pm.me**.
- Beschreiben Sie das Problem, die betroffene Version und wie man es reproduziert.
- Wenn es Datenoffenlegung betrifft, beschreiben Sie die Art des Problems; Sie müssen
  keinen funktionierenden Exploit beifügen.

Sie erhalten so bald wie möglich eine Antwort. Bitte geben Sie ein angemessenes
Zeitfenster, um das Problem zu beheben, bevor es öffentlich bekannt gemacht wird.

## Was als Sicherheitsproblem zählt

Wegen dessen, was VD Infos verspricht, sind diese besonders wichtig:

- Alles, was die App dazu bringen würde, das Netzwerk zu erreichen oder irgendeinen
  Messwert vom Gerät zu senden.
- Alles, was einen identitätstragenden Wert trotz der Maskierung in einen geteilten oder
  gespeicherten **Export** gelangen ließe (eine Seriennummer, IMEI, MAC-Adresse, Werbe-ID
  und dergleichen).
- Alles, was einer anderen App erlaubt, die private Berichtsdatei zu lesen, die nur von
  der App selbst oder root lesbar sein soll.

## Was kein Sicherheitsproblem ist

- Ein Messwert, der "falsch" aussieht, aber der ehrliche Wert ist, den eine Methode
  zurückgab. VD Infos meldet, was jede Methode sagte; das Lesen des Vergleichs ist Ihre
  Sache. Siehe das [Benutzerhandbuch](USER-GUIDE.md).
- Ein Messwert, der als nicht verfügbar markiert ist. Das bedeutet, dass eine Methode
  diesen Wert auf Ihrem Gerät nicht lesen konnte, was selbst Teil des Vergleichs ist.

## Unterstützte Versionen

Nur die zuletzt veröffentlichte Version wird unterstützt. Bitte reproduzieren Sie auf dem
neuesten Build, bevor Sie melden.

<!-- doc-footer -->

---

> _Diese Dokumentation wurde mit Claude (Anthropic) erstellt und wird von VD171 gepflegt. Korrekturen und Verbesserungen sind willkommen; siehe [CONTRIBUTING.md](CONTRIBUTING.md)._
