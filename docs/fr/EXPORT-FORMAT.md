> **Languages:** [English](../EXPORT-FORMAT.md) · [العربية](../ar/EXPORT-FORMAT.md) · [Čeština](../cs/EXPORT-FORMAT.md) · [Deutsch](../de/EXPORT-FORMAT.md) · [Español](../es/EXPORT-FORMAT.md) · [فارسی](../fa/EXPORT-FORMAT.md) · Français · [हिन्दी](../hi/EXPORT-FORMAT.md) · [Bahasa Indonesia](../id/EXPORT-FORMAT.md) · [Italiano](../it/EXPORT-FORMAT.md) · [日本語](../ja/EXPORT-FORMAT.md) · [한국어](../ko/EXPORT-FORMAT.md) · [Nederlands](../nl/EXPORT-FORMAT.md) · [Polski](../pl/EXPORT-FORMAT.md) · [Português](../pt/EXPORT-FORMAT.md) · [Русский](../ru/EXPORT-FORMAT.md) · [Svenska](../sv/EXPORT-FORMAT.md) · [ไทย](../th/EXPORT-FORMAT.md) · [Türkçe](../tr/EXPORT-FORMAT.md) · [Українська](../uk/EXPORT-FORMAT.md) · [Tiếng Việt](../vi/EXPORT-FORMAT.md) · [中文](../zh/EXPORT-FORMAT.md)

---

# Format d'export

Le rapport que VD Infos exporte - et le `last_snapshot.json` qu'il écrit après chaque analyse -
est un objet JSON à la forme stable et documentée. Cette page est le schéma. Elle
décrit la structure pour qu'un outil puisse l'analyser ; elle n'énumère pas les sondes individuelles
ni n'interprète aucune lecture à votre place.

## Niveau supérieur : l'instantané

```json
{
  "takenAt": 1730000000000,
  "appVersion": "2.23",
  "results": [ /* ProbeResult objects */ ]
}
```

| Champ | Type | Signification |
|---|---|---|
| `takenAt` | number | Quand l'analyse a été prise, epoch Unix en millisecondes. |
| `appVersion` | string | La version de l'application qui a produit le rapport. |
| `results` | array | Un `ProbeResult` par sonde, dans l'ordre où l'application les conserve. |

## Un résultat

Chaque entrée dans `results` est une sonde avec son verdict et la lecture de chaque lentille.

```json
{
  "spec": { /* ProbeSpec */ },
  "values": [ /* LensValue objects */ ],
  "verdict": "MATCH",
  "detail": null
}
```

| Champ | Type | Signification |
|---|---|---|
| `spec` | object | Ce qui a été sondé (voir ci-dessous). |
| `values` | array | Un `LensValue` par méthode qui l'a lu. |
| `verdict` | string | Comment les lectures se rapportent (voir [Verdict](#verdict)). |
| `detail` | string or null | Une note facultative sur le résultat. |

### spec (ProbeSpec)

```json
{
  "id": "identity:model",
  "title": "Model",
  "category": "IDENTITY",
  "lenses": ["JAVA", "NATIVE", "SHELL"],
  "note": null,
  "sensitive": false,
  "solution": null
}
```

| Champ | Type | Signification |
|---|---|---|
| `id` | string | Identifiant stable de la sonde. |
| `title` | string | Nom lisible par un humain. |
| `category` | string | Une des [catégories](#catégorie). |
| `lenses` | array | Les lentilles qu'utilise cette sonde. |
| `note` | string or null | Contexte facultatif. |
| `sensitive` | boolean | `true` quand la valeur porte une identité ; de telles valeurs sont **masquées dans le rapport exporté** (voir [Masquage](#masquage)). |
| `solution` | string or null | Un champ d'indice facultatif ; peut être absent ou null. |

### values (LensValue)

```json
{
  "lens": "JAVA",
  "source": "Build.MODEL",
  "value": "Pixel 9",
  "error": null,
  "elapsedMicros": 42,
  "compare": true,
  "detail": null
}
```

| Champ | Type | Signification |
|---|---|---|
| `lens` | string | Quelle méthode l'a lu (voir [Lentille](#lentille)). |
| `source` | string | L'appel ou le chemin exact derrière cette lecture. |
| `value` | string or null | Ce que cette méthode a renvoyé ; `null` si elle n'a rien lu. |
| `error` | string or null | Présent quand la méthode a échoué. |
| `elapsedMicros` | number | Combien de temps a pris la lecture, en microsecondes. |
| `compare` | boolean | `true` si cette lecture participe au verdict ; `false` si elle est affichée seulement pour information. |
| `detail` | string or null | Une note facultative sur cette lecture. |

## Énumérations

### Verdict

| Valeur | Signification |
|---|---|
| `MATCH` | Toutes les lentilles comparables sont d'accord. |
| `MISMATCH` | Les lentilles comparables ne sont pas d'accord. |
| `SINGLE` | Une seule lentille a pu la lire. |
| `INFO` | Lu pour référence, non soumis au vote. |
| `EMPTY` | Aucune lentille n'a lu de valeur. |
| `ERROR` | Aucune valeur, et au moins une lentille a renvoyé une erreur. |

### Lentille

| Valeur | Libellé |
|---|---|
| `JAVA` | Java / SDK |
| `NATIVE` | Natif / JNI |
| `SHELL` | Shell via JVM |
| `SHELL_NATIVE` | Shell via JNI |
| `ATTEST` | TEE / Attestation |

### Catégorie

`IDENTITY`, `FINGERPRINT`, `TELEPHONY`, `NETWORK`, `LOCALE`, `BOOT`, `BUILD`,
`HARDWARE`, `DISPLAY`, `SENSORS`, `MEDIA`, `STORAGE`, `PROCESS`, `PACKAGES`,
`ACCOUNTS`, `WEBVIEW`, `SECURITY`, `INTEGRITY`, `EMULATOR`, `SYSTEM`.

## Masquage

Quand `spec.sensitive` est `true`, les lectures de la sonde portent une valeur porteuse d'identité
(un numéro de série, IMEI, adresse MAC, identifiant publicitaire et autres). Dans le rapport **partagé ou exporté**
ces valeurs, et leurs détails, sont remplacés par un marqueur de caviardage pour qu'un
rapport collé dans un lieu public ne divulgue pas d'identifiants personnels ; la sonde, son
verdict et la structure sont préservés. Le `last_snapshot.json` **sur l'appareil**,
lisible seulement par l'application ou par root, conserve les valeurs réelles.

## Lire ceci

Une analyse minimale : itérez `results`, lisez chaque `verdict`, et pour une image complète alignez
les `values` par leur `lens`. La forme ci-dessus est stable d'une version à l'autre ; analysez contre
ces noms de champ plutôt que des positions à l'écran.

Voir [Agents IA](AI-AGENTS.md) pour savoir comment obtenir le fichier sur l'appareil.

<!-- doc-footer -->

---

> _Cette documentation a été générée avec Claude (Anthropic) et est maintenue par VD171. Les corrections et améliorations sont les bienvenues ; voir [CONTRIBUTING.md](CONTRIBUTING.md)._
