> **Languages:** [English](../PRIVACY.md) · [العربية](../ar/PRIVACY.md) · [Čeština](../cs/PRIVACY.md) · [Deutsch](../de/PRIVACY.md) · [Español](../es/PRIVACY.md) · [فارسی](../fa/PRIVACY.md) · Français · [हिन्दी](../hi/PRIVACY.md) · [Bahasa Indonesia](../id/PRIVACY.md) · [Italiano](../it/PRIVACY.md) · [日本語](../ja/PRIVACY.md) · [한국어](../ko/PRIVACY.md) · [Nederlands](../nl/PRIVACY.md) · [Polski](../pl/PRIVACY.md) · [Português](../pt/PRIVACY.md) · [Русский](../ru/PRIVACY.md) · [Svenska](../sv/PRIVACY.md) · [ไทย](../th/PRIVACY.md) · [Türkçe](../tr/PRIVACY.md) · [Українська](../uk/PRIVACY.md) · [Tiếng Việt](../vi/PRIVACY.md) · [中文](../zh/PRIVACY.md)

---

# Confidentialité

La confidentialité n'est pas une fonctionnalité de VD Infos. C'est tout l'intérêt. Cette page énonce exactement
ce que l'application fait et ne fait pas avec vos données, en termes clairs que vous pouvez vérifier.

## La promesse

- **Tout s'exécute sur l'appareil.** Aucune lecture n'est envoyée, téléversée ni transmise à un
  serveur. L'application fonctionne pleinement avec internet bloqué.
- **Pas de compte, pas de connexion, pas de travail en arrière-plan.** L'application lit seulement pendant qu'elle est ouverte,
  ne laisse rien tourner en arrière-plan, et se ferme d'elle-même lorsqu'on la laisse inactive.
- **Rien ne change sur votre système.** VD Infos ne fait que lire et comparer ; il n'écrit jamais
  dans l'état de votre appareil.

## Le rapport reste avec vous

Un rapport ne quitte l'appareil **que lorsque vous le partagez ou l'enregistrez explicitement**. Jusque-là il
existe en deux endroits, tous deux sur l'appareil :

- **À l'écran**, pendant que l'application est ouverte.
- Dans le **stockage privé** de l'application (`files/last_snapshot.json`), que seule l'application
  elle-même ou root peuvent lire. L'analyse suivante l'écrase, et il ne quitte jamais l'appareil.
  Il existe pour que les outils et les agents IA puissent lire le résultat complet ; voir le
  [guide pour agents IA](AI-AGENTS.md).

## Les exports sont masqués

Quand vous partagez ou enregistrez un rapport, les valeurs porteuses d'identité - numéros de série, IMEI, adresses
MAC, identifiants publicitaires et autres - sont **masquées** avant que le rapport soit écrit,
pour qu'un rapport que vous collez dans un fil public ne divulgue pas vos propres identifiants. Les
verdicts et la structure sont préservés ; seules les valeurs sensibles sont caviardées. La
copie privée sur l'appareil conserve les valeurs réelles pour votre propre usage.

## Permissions

L'application démarre sans requérir aucune permission. Certaines lectures ne deviennent disponibles que si vous
accordez la permission correspondante (par exemple l'accès à l'utilisation), et l'accorder est entièrement
votre choix ; là où une lecture est indisponible, l'application le dit simplement.

## Vérifiez-le vous-même

Vous n'avez à prendre rien de tout cela pour argent comptant :

- La source est publique : <https://github.com/VD171/VD-Infos>.
- Le manifeste est dans la source ; confirmez quelles permissions il déclare.
- Bloquez l'accès réseau de l'application avec un pare-feu, ou coupez internet, et elle continue
  de fonctionner de la même façon.

Voir le [guide d'utilisation](USER-GUIDE.md) pour savoir comment lire un rapport et ce que contient
l'export.

<!-- doc-footer -->

---

> _Cette documentation a été générée avec Claude (Anthropic) et est maintenue par VD171. Les corrections et améliorations sont les bienvenues ; voir [CONTRIBUTING.md](CONTRIBUTING.md)._
