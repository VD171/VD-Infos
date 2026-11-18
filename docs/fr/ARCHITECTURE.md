> **Languages:** [English](../ARCHITECTURE.md) · [العربية](../ar/ARCHITECTURE.md) · [Čeština](../cs/ARCHITECTURE.md) · [Deutsch](../de/ARCHITECTURE.md) · [Español](../es/ARCHITECTURE.md) · [فارسی](../fa/ARCHITECTURE.md) · Français · [हिन्दी](../hi/ARCHITECTURE.md) · [Bahasa Indonesia](../id/ARCHITECTURE.md) · [Italiano](../it/ARCHITECTURE.md) · [日本語](../ja/ARCHITECTURE.md) · [한국어](../ko/ARCHITECTURE.md) · [Nederlands](../nl/ARCHITECTURE.md) · [Polski](../pl/ARCHITECTURE.md) · [Português](../pt/ARCHITECTURE.md) · [Русский](../ru/ARCHITECTURE.md) · [Svenska](../sv/ARCHITECTURE.md) · [ไทย](../th/ARCHITECTURE.md) · [Türkçe](../tr/ARCHITECTURE.md) · [Українська](../uk/ARCHITECTURE.md) · [Tiếng Việt](../vi/ARCHITECTURE.md) · [中文](../zh/ARCHITECTURE.md)

---

# Architecture

Ceci est une carte de haut niveau de la façon dont VD Infos est assemblé. Elle décrit la forme de
l'application, pas le catalogue de ce qu'elle lit : l'intérêt d'un débogueur de méthodes est de mettre chaque
lecture devant vous, et les lire est à vous de le faire sur votre propre appareil.

## Objectifs de conception

- **Une valeur, chaque méthode.** Pour chaque élément d'information, lisez-le à travers chaque
  méthode capable de le lire et alignez les lectures. Un débogueur fait remonter la preuve ; il
  ne porte pas de jugement.
- **Sur l'appareil et discret.** Pas de réseau, pas de compte, pas de travail en arrière-plan. L'application s'exécute
  seulement pendant qu'elle est ouverte.
- **Lisible par les machines.** Le résultat complet est un document lisible par machine, si bien qu'un
  outil obtient la même image qu'une personne. Voir [Agents IA](AI-AGENTS.md).
- **Petit là où ça compte.** La partie qui doit être difficile à tromper est gardée délibérément
  minuscule.

## Les lentilles

Une *lentille* est une façon de lire une valeur. La même valeur est lue à travers plusieurs pour qu'elles
puissent être comparées :

| Lentille | Ce que c'est |
|---|---|
| **Java / SDK** | L'API de plateforme qu'appelle une application ordinaire. |
| **Natif / JNI** | La même valeur lue depuis du code natif. |
| **Shell via JVM** | Une commande shell engendrée par la JVM. |
| **Shell via JNI** | La même commande engendrée depuis du code natif, un point de vue différent. |
| **TEE / Attestation** | Attestation de clés adossée au matériel, là où elle s'applique. |

Aligner les lentilles est toute l'idée : quand elles ne concordent pas, quelque chose entre l'application
et la valeur répond différemment à différents appelants. Ce que cela signifie pour une
valeur donnée, sur un appareil donné, est à vous de le lire.

## Comment se déroule une analyse

```
core/model      immutable domain: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          the probes and the bridges they use (JNI, reflection, data lists)
engine/         ProbeEngine: parallel fan-out, streams results as they land
data/           SnapshotStore (persist the report), Exporter (share as JSON / text)
ui/             Jetpack Compose, Material 3, dynamic colour, live progress
cpp/            native_probes.cpp: the native lens, dependency-free
```

- **Parallélisme.** Les sondes se déploient à travers un dispatcher avec un nombre borné de permis ;
  les résultats sont diffusés vers l'interface au fur et à mesure qu'ils se terminent, avec un indicateur de progression en direct.
- **Rien en arrière-plan.** Pas de services et pas d'analyses programmées. L'application lit seulement
  pendant qu'elle est ouverte et se ferme d'elle-même lorsqu'on la laisse inactive.
- **La couche native.** Un petit `.so`, lié par nom à travers `RegisterNatives`, gardé
  délibérément minuscule parce que c'est la partie qui doit être difficile à tromper.

## Le rapport

Chaque analyse produit un `Snapshot` : l'heure à laquelle elle a été prise, la version de l'application, et une liste de
résultats, un par sonde, chacun avec les lectures de chaque lentille et un verdict sur la façon dont elles se
rapportent. L'application persiste cela dans le stockage privé après chaque analyse et peut exporter
les mêmes données. La structure est documentée comme le [format d'export](EXPORT-FORMAT.md).

## L'étendre

L'essentiel de ce que l'application recherche vit sous forme de **listes de texte brut** sous
`assets/data/`, pas sous forme de code. Ajouter ou retirer une cible, c'est éditer une ligne. Voir
[Contribuer](CONTRIBUTING.md).

## Ce que VD Infos ne fait jamais

- Il ne change jamais rien sur votre système. Il ne fait que lire et comparer.
- Il ne parle jamais au réseau. Il fonctionne pleinement hors ligne.
- Il ne s'exécute jamais en arrière-plan, ne programme pas d'analyse, et ne maintient pas de service en vie.
- Il n'envoie jamais une lecture où que ce soit. Un rapport ne quitte l'appareil que lorsque vous le partagez ou
  l'enregistrez, avec les valeurs porteuses d'identité masquées.

<!-- doc-footer -->

---

> _Cette documentation a été générée avec Claude (Anthropic) et est maintenue par VD171. Les corrections et améliorations sont les bienvenues ; voir [CONTRIBUTING.md](CONTRIBUTING.md)._
