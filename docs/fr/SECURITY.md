> **Languages:** [English](../SECURITY.md) · [العربية](../ar/SECURITY.md) · [Čeština](../cs/SECURITY.md) · [Deutsch](../de/SECURITY.md) · [Español](../es/SECURITY.md) · [فارسی](../fa/SECURITY.md) · Français · [हिन्दी](../hi/SECURITY.md) · [Bahasa Indonesia](../id/SECURITY.md) · [Italiano](../it/SECURITY.md) · [日本語](../ja/SECURITY.md) · [한국어](../ko/SECURITY.md) · [Nederlands](../nl/SECURITY.md) · [Polski](../pl/SECURITY.md) · [Português](../pt/SECURITY.md) · [Русский](../ru/SECURITY.md) · [Svenska](../sv/SECURITY.md) · [ไทย](../th/SECURITY.md) · [Türkçe](../tr/SECURITY.md) · [Українська](../uk/SECURITY.md) · [Tiếng Việt](../vi/SECURITY.md) · [中文](../zh/SECURITY.md)

---

# Politique de sécurité

VD Infos est un outil de confidentialité qui lit de l'état sensible de l'appareil, si bien que les signalements de sécurité sont
pris au sérieux.

## Signaler une vulnérabilité

Veuillez signaler les problèmes de sécurité **en privé**, pas dans une issue publique :

- Courriel **vd.priv8@pm.me**.
- Décrivez le problème, la version affectée, et comment le reproduire.
- S'il concerne une exposition de données, décrivez la classe de problème ; vous n'avez pas besoin de joindre
  un exploit fonctionnel.

Vous obtiendrez une réponse dès que possible. Veuillez accorder une fenêtre raisonnable pour corriger le
problème avant toute divulgation publique.

## Ce qui compte comme un problème de sécurité

En raison de ce que VD Infos promet, ceux-ci sont particulièrement importants :

- Tout ce qui ferait que l'application atteigne le réseau, ou envoie une quelconque lecture hors de
  l'appareil.
- Tout ce qui divulguerait une valeur porteuse d'identité dans un **export** partagé ou enregistré
  (un numéro de série, IMEI, adresse MAC, identifiant publicitaire et autres) malgré le masquage.
- Tout ce qui permet à une autre application de lire le fichier de rapport privé qui est censé être
  lisible seulement par l'application elle-même ou root.

## Ce qui n'est pas un problème de sécurité

- Une lecture qui a l'air « fausse » mais qui est la valeur honnête qu'une méthode a renvoyée. VD Infos
  rapporte ce que chaque méthode a dit ; lire la comparaison est à vous de le faire. Voir le
  [guide d'utilisation](USER-GUIDE.md).
- Une lecture marquée indisponible. Cela signifie qu'une méthode n'a pas pu lire cette valeur sur votre
  appareil, ce qui fait en soi partie de la comparaison.

## Versions prises en charge

Seule la dernière version publiée est prise en charge. Veuillez reproduire sur la dernière build
avant de signaler.

<!-- doc-footer -->

---

> _Cette documentation a été générée avec Claude (Anthropic) et est maintenue par VD171. Les corrections et améliorations sont les bienvenues ; voir [CONTRIBUTING.md](CONTRIBUTING.md)._
