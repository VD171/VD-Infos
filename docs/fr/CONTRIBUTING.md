> **Languages:** [English](../CONTRIBUTING.md) · [العربية](../ar/CONTRIBUTING.md) · [Čeština](../cs/CONTRIBUTING.md) · [Deutsch](../de/CONTRIBUTING.md) · [Español](../es/CONTRIBUTING.md) · [فارسی](../fa/CONTRIBUTING.md) · Français · [हिन्दी](../hi/CONTRIBUTING.md) · [Bahasa Indonesia](../id/CONTRIBUTING.md) · [Italiano](../it/CONTRIBUTING.md) · [日本語](../ja/CONTRIBUTING.md) · [한국어](../ko/CONTRIBUTING.md) · [Nederlands](../nl/CONTRIBUTING.md) · [Polski](../pl/CONTRIBUTING.md) · [Português](../pt/CONTRIBUTING.md) · [Русский](../ru/CONTRIBUTING.md) · [Svenska](../sv/CONTRIBUTING.md) · [ไทย](../th/CONTRIBUTING.md) · [Türkçe](../tr/CONTRIBUTING.md) · [Українська](../uk/CONTRIBUTING.md) · [Tiếng Việt](../vi/CONTRIBUTING.md) · [中文](../zh/CONTRIBUTING.md)

---

# Contribuer

Merci de votre intérêt pour VD Infos. Les contributions sont les bienvenues : listes de détection,
traductions, rapports de bugs, documentation et code. La plupart des contributions ne nécessitent aucun Kotlin.

## La contribution la plus facile : les listes

L'essentiel de ce que l'application recherche vit sous forme de **listes de texte brut** sous
`VDInfos/app/src/main/assets/data/`, pas sous forme de code. Ajouter ou retirer une cible, c'est éditer
une ligne et ouvrir une pull request :

- `*_apps.txt` - un nom de paquet par ligne.
- `props.txt` - le catalogue des propriétés système, `CATEGORY<tab>key`.
- `spoof_keys.txt` - la matrice des réglages, `key:TYPE`.
- `known_certs.txt` - le catalogue des certificats de signature, `SHA-256<tab>owner` (le hash
  complet, pris d'un vrai APK avec `apksigner verify --print-certs`).
- autres fichiers `.txt` - une entrée par ligne (fragments de noms de modules du noyau, noms de fichiers,
  et ainsi de suite).

Un `#` démarre un commentaire et les lignes vides sont ignorées. Certains fichiers condensent quelques champs séparés par des espaces ou
des tabulations par ligne, ou des marqueurs facultatifs ; conservez la forme des lignes déjà présentes
dans le fichier.

## Règles de base

- **VD Infos lit la vérité et ne falsifie jamais rien.** Il rapporte ce que chaque méthode a
  renvoyé, exactement. N'adoucissez pas une lecture pour la rendre plus jolie, et n'ajoutez
  rien qui changerait une valeur plutôt que de la lire.
- **La confidentialité est absolue.** Tout s'exécute sur l'appareil. N'ajoutez jamais d'appel réseau, ni
  rien qui envoie une lecture hors de l'appareil.
- **Les exports restent masqués.** Les valeurs porteuses d'identité sont masquées dans un rapport partagé. Gardez-le
  ainsi.

## Traductions

Les traductions sont une contribution précieuse. Le readme est traduit par langue
(`README.<lang>.md`), les chaînes de l'application par langue, et cette documentation sous
`docs/<lang>/`. Gardez une terminologie cohérente au sein d'une langue, respectez le ton existant,
et ne traduisez jamais à la machine un terme technique dont vous n'êtes pas sûr.

## Signaler un bug

Ouvrez une issue avec le modèle de votre appareil, la version d'Android, et, s'il s'agit d'une lecture, un
**export** partagé (ses valeurs porteuses d'identité sont masquées). L'export est construit pour
exactement cela.

## Code

- Compilez en release, jamais en debug. Testez sur un vrai appareil. Voir [Compiler](BUILDING.md).
- Gardez les commentaires hors du code ; les noms expliquent l'intention. Le seul en-tête est le banner
  de licence en haut de chaque fichier source.
- Respectez le style existant et gardez les changements ciblés.

## Licence

En contribuant, vous acceptez que votre contribution soit sous licence
**GNU AGPL-3.0-or-later** du projet.

<!-- doc-footer -->

---

> _Cette documentation a été générée avec Claude (Anthropic) et est maintenue par VD171. Les corrections et améliorations sont les bienvenues ; voir [CONTRIBUTING.md](CONTRIBUTING.md)._
