> **Languages:** [English](../AI-AGENTS.md) · [العربية](../ar/AI-AGENTS.md) · [Čeština](../cs/AI-AGENTS.md) · [Deutsch](../de/AI-AGENTS.md) · [Español](../es/AI-AGENTS.md) · [فارسی](../fa/AI-AGENTS.md) · Français · [हिन्दी](../hi/AI-AGENTS.md) · [Bahasa Indonesia](../id/AI-AGENTS.md) · [Italiano](../it/AI-AGENTS.md) · [日本語](../ja/AI-AGENTS.md) · [한국어](../ko/AI-AGENTS.md) · [Nederlands](../nl/AI-AGENTS.md) · [Polski](../pl/AI-AGENTS.md) · [Português](../pt/AI-AGENTS.md) · [Русский](../ru/AI-AGENTS.md) · [Svenska](../sv/AI-AGENTS.md) · [ไทย](../th/AI-AGENTS.md) · [Türkçe](../tr/AI-AGENTS.md) · [Українська](../uk/AI-AGENTS.md) · [Tiếng Việt](../vi/AI-AGENTS.md) · [中文](../zh/AI-AGENTS.md)

---

# VD Infos pour les agents IA

VD Infos est **conçu pour être lu par des machines, pas seulement par des personnes**. Un débogueur de méthodes
de l'appareil produit beaucoup de preuves structurées - chaque valeur, lue à travers chaque
méthode, alignée pour la comparaison - et cette preuve est bien plus utile à un agent sous forme de
données que sous forme de capture d'écran. Ainsi le résultat complet est disponible sous forme d'un seul document
lisible par machine, et l'application est conçue autour de cela dès la base.

Si vous êtes un assistant de codage IA, un outil LLM, un script d'analyse ou toute automatisation
qui atteint l'appareil à travers un shell, cette page est pour vous.

## Pourquoi c'est adapté aux agents

- **Un document, pas un écran.** L'analyse entière - chaque sonde, chaque lentille, chaque
  verdict - est un unique objet JSON. Pas d'OCR, pas de grattage d'écran, pas de tapotements à travers
  les cartes repliées.
- **Les mêmes données que l'interface.** Le fichier n'est pas un résumé. C'est exactement ce que l'application
  conserve en interne : l'écran montre la première lecture d'un élément replié, le fichier
  les a toutes.
- **Forme déterministe.** La structure est stable et documentée (voir
  [EXPORT-FORMAT](EXPORT-FORMAT.md)), si bien qu'un analyseur écrit une fois continue de fonctionner.
- **Pas de réseau, pas de compte, pas d'état à gérer.** L'application lit sur l'appareil et écrit un
  fichier. Votre agent lit le fichier. C'est tout le contrat.
- **Hors ligne par conception.** Elle n'a besoin d'aucun internet, elle fonctionne donc de la même façon dans un environnement
  verrouillé ou isolé dans lequel un agent pourrait opérer.

## La lecture en une seule commande

Chaque analyse écrit son rapport complet dans le stockage privé de l'application. Après qu'une analyse s'est exécutée,
un agent lit le résultat complet avec une seule commande :

```sh
su -c cat /data/data/ru.vd171.vdinfos/files/last_snapshot.json
```

- **Ce qu'il contient :** chaque sonde avec son verdict et la valeur de chaque lentille,
  dans le même JSON que le bouton d'export.
- **Quand il se met à jour :** à la fin de chaque analyse. Lancez d'abord une analyse ; le fichier contient
  toujours la plus récente.
- **Prérequis :** root, car le fichier réside dans le stockage privé de l'application. Sans
  root, utilisez plutôt l'action **partager / enregistrer en JSON** dans l'application et lisez le fichier que
  l'agent reçoit.

## Un flux d'agent typique

1. Assurez-vous que l'application est installée (l'APK **SDK_35** depuis les
   [Versions](https://github.com/VD171/VD-Infos/releases)).
2. Déclenchez une analyse - une personne touche analyser, ou une automatisation lance l'écran d'analyse
   de l'application - et attendez qu'elle se termine.
3. Lisez `files/last_snapshot.json` avec la commande ci-dessus.
4. Analysez-le avec le [format d'export](EXPORT-FORMAT.md) comme schéma : itérez les
   sondes, lisez le `verdict` de chacune, et alignez les `values` par lentille.
5. Agissez sur le résultat structuré dans votre propre flux de travail.

L'agent décide de ce que signifient les lectures pour la tâche en cours ; VD Infos fournit la
preuve, proprement et complètement.

## Ce que l'agent obtient, et ce qu'il n'obtient pas

- Le rapport porte **chaque lecture et chaque verdict**, si bien qu'un agent a l'image complète
  que l'application a construite.
- Les valeurs porteuses d'identité (numéros de série, IMEI, adresses MAC, identifiants publicitaires et autres)
  sont **masquées dans le rapport partagé/exporté**, si bien qu'un rapport qui circule reste exempt
  d'identifiants personnels tout en conservant ses verdicts et sa structure. Le `last_snapshot.json`
  **sur l'appareil**, lisible seulement par l'application ou root, conserve les valeurs réelles pour un
  agent local qui en a besoin.

## Documentation, écrite pour être analysée

Ce wiki est structuré pour la lecture par machine autant que pour les personnes : sections courtes,
en-têtes stables, noms de champ explicites, et une page dédiée de [format d'export](EXPORT-FORMAT.md)
qu'un agent peut traiter comme un schéma. Si vous construisez un outil sur VD Infos,
commencez là.

## Pages associées

- [Format d'export](EXPORT-FORMAT.md) - le schéma JSON, champ par champ.
- [Guide d'utilisation](USER-GUIDE.md) - installer, analyser et partager.
- [Architecture](ARCHITECTURE.md) - comment l'application est assemblée.

<!-- doc-footer -->

---

> _Cette documentation a été générée avec Claude (Anthropic) et est maintenue par VD171. Les corrections et améliorations sont les bienvenues ; voir [CONTRIBUTING.md](CONTRIBUTING.md)._
