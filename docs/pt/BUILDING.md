> **Languages:** [English](../BUILDING.md) · [العربية](../ar/BUILDING.md) · [Čeština](../cs/BUILDING.md) · [Deutsch](../de/BUILDING.md) · [Español](../es/BUILDING.md) · [فارسی](../fa/BUILDING.md) · [Français](../fr/BUILDING.md) · [हिन्दी](../hi/BUILDING.md) · [Bahasa Indonesia](../id/BUILDING.md) · [Italiano](../it/BUILDING.md) · [日本語](../ja/BUILDING.md) · [한국어](../ko/BUILDING.md) · [Nederlands](../nl/BUILDING.md) · [Polski](../pl/BUILDING.md) · Português · [Русский](../ru/BUILDING.md) · [Svenska](../sv/BUILDING.md) · [ไทย](../th/BUILDING.md) · [Türkçe](../tr/BUILDING.md) · [Українська](../uk/BUILDING.md) · [Tiếng Việt](../vi/BUILDING.md) · [中文](../zh/BUILDING.md)

---

# Construindo o VD Infos

O VD Infos é um projeto Android Gradle padrão: Kotlin e Jetpack Compose, com uma pequena
lente nativa em C++ construída pelo NDK. A CI consegue construí-lo, e você pode reproduzi-lo
localmente.

## Requisitos

- Um JDK (17 a 21) e o Android SDK.
- O Android NDK e o CMake, para a lente nativa (o plugin Android do Gradle busca um
  CMake compatível se você tiver o componente `cmake` do SDK).
- `minSdk` 26, então as builds rodam no Android 8.0 e mais recente.

## Início rápido

```sh
# build both flavors, release
./gradlew assembleRelease
```

Isso produz **dois APKs**, um por flavor:

- **modern** (`SDK_35`) - o sandbox moderno estrito, a build a usar.
- **compat** (`SDK_27`) - um alvo de API mais antigo, apenas para comparação lado a lado.

As saídas aterrissam em:

```
app/build/outputs/apk/modern/release/VDInfos-v<ver>-SDK_35-release.apk
app/build/outputs/apk/compat/release/VDInfos-v<ver>-SDK_27-release.apk
```

Para construir apenas um flavor enquanto itera:

```sh
./gradlew assembleModernRelease   # SDK_35 only
./gradlew assembleCompatRelease   # SDK_27 only
```

## Assinatura

Builds release são assinadas com uma chave que é **local e não commitada** (o keystore
e suas propriedades são ignorados pelo git). Sem ela, a tarefa release ainda constrói, mas
o APK sai sem assinatura. Forneça seu próprio keystore para assinar suas próprias builds.

## Os dois flavors

O app entrega dois flavors de propósito. Eles visam níveis de API diferentes e constroem
a partir do mesmo código-fonte; apenas o alvo e alguns caminhos de código protegidos diferem. Uma mudança
que toca código nativo ou um caminho específico de flavor deve ser construída e verificada em
**ambos** os flavors, não apenas naquele sobre o qual você está iterando.

## A lente nativa

A lente nativa é uma única unidade de tradução C++ sem dependências, construída pelo NDK como
um pequeno `.so`, vinculada por nome via `RegisterNatives`. Ela é compilada como parte da
build Gradle normal; não há etapa separada. Ela é mantida deliberadamente minúscula e simples.

## Onde as coisas ficam

```
app/src/main/java/ru/vd171/vdinfos/   Kotlin: model, probes, engine, data, ui
app/src/main/cpp/                     the native lens
app/src/main/assets/data/             the plain-text detection lists (see Contributing)
```

Veja [Arquitetura](ARCHITECTURE.md) para saber como as peças se encaixam, e
[Contribuindo](CONTRIBUTING.md) para saber como adicionar um alvo de detecção sem tocar no código.

<!-- doc-footer -->

---

> _Esta documentação foi gerada com o Claude (Anthropic) e é mantida por VD171. Correções e melhorias são bem-vindas; veja [CONTRIBUTING.md](CONTRIBUTING.md)._
