> **Languages:** [English](../ARCHITECTURE.md) · [العربية](../ar/ARCHITECTURE.md) · [Čeština](../cs/ARCHITECTURE.md) · [Deutsch](../de/ARCHITECTURE.md) · [Español](../es/ARCHITECTURE.md) · [فارسی](../fa/ARCHITECTURE.md) · [Français](../fr/ARCHITECTURE.md) · [हिन्दी](../hi/ARCHITECTURE.md) · [Bahasa Indonesia](../id/ARCHITECTURE.md) · [Italiano](../it/ARCHITECTURE.md) · [日本語](../ja/ARCHITECTURE.md) · [한국어](../ko/ARCHITECTURE.md) · [Nederlands](../nl/ARCHITECTURE.md) · [Polski](../pl/ARCHITECTURE.md) · Português · [Русский](../ru/ARCHITECTURE.md) · [Svenska](../sv/ARCHITECTURE.md) · [ไทย](../th/ARCHITECTURE.md) · [Türkçe](../tr/ARCHITECTURE.md) · [Українська](../uk/ARCHITECTURE.md) · [Tiếng Việt](../vi/ARCHITECTURE.md) · [中文](../zh/ARCHITECTURE.md)

---

# Arquitetura

Este é um mapa de alto nível de como o VD Infos é montado. Ele descreve o formato do
app, não o catálogo do que ele lê: o ponto de um depurador de métodos é colocar cada
leitura diante de você, e lê-las é tarefa sua, no seu próprio dispositivo.

## Objetivos de design

- **Um valor, cada método.** Para cada informação, leia-a por cada
  método capaz de lê-la e alinhe as leituras. Um depurador traz evidências à tona; ele
  não julga.
- **No dispositivo e silencioso.** Sem rede, sem conta, sem trabalho em segundo plano. O app roda
  apenas enquanto está aberto.
- **Legível por máquinas.** O resultado inteiro é um único documento legível por máquina, então uma
  ferramenta obtém o mesmo quadro que uma pessoa. Veja [Agentes de IA](AI-AGENTS.md).
- **Pequeno onde importa.** A parte que precisa ser difícil de enganar é mantida deliberadamente
  minúscula.

## As lentes

Uma *lente* é um jeito de ler um valor. O mesmo valor é lido por várias para que
possam ser comparadas:

| Lente | O que é |
|---|---|
| **Java / SDK** | A API da plataforma que um app comum chama. |
| **Native / JNI** | O mesmo valor lido a partir de código nativo. |
| **Shell via JVM** | Um comando de shell disparado pela JVM. |
| **Shell via JNI** | O mesmo comando disparado a partir de código nativo, um ponto de observação diferente. |
| **TEE / Attestation** | Atestação de chave respaldada por hardware, onde se aplica. |

Alinhar as lentes é a ideia inteira: quando elas discordam, algo entre o app
e o valor está respondendo de forma diferente a diferentes chamadores. O que isso significa para um
dado valor, em um dado dispositivo, é você quem deve ler.

## Como uma varredura roda

```
core/model      immutable domain: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          the probes and the bridges they use (JNI, reflection, data lists)
engine/         ProbeEngine: parallel fan-out, streams results as they land
data/           SnapshotStore (persist the report), Exporter (share as JSON / text)
ui/             Jetpack Compose, Material 3, dynamic colour, live progress
cpp/            native_probes.cpp: the native lens, dependency-free
```

- **Paralelismo.** As sondas se espalham por um dispatcher com uma contagem limitada de permissões;
  os resultados fluem para a interface conforme são concluídos, com um indicador de progresso ao vivo.
- **Nada em segundo plano.** Sem serviços e sem varreduras agendadas. O app lê apenas
  enquanto aberto e se fecha quando deixado ocioso.
- **A camada nativa.** Um pequeno `.so`, vinculado por nome via `RegisterNatives`, mantido
  deliberadamente minúsculo porque é a parte que precisa ser difícil de enganar.

## O relatório

Cada varredura produz um `Snapshot`: a hora em que foi feita, a versão do app e uma lista de
resultados, um por sonda, cada um com as leituras de cada lente e um veredito de como
elas se relacionam. O app persiste isso no armazenamento privado após cada varredura e pode exportar
os mesmos dados. A estrutura está documentada como o [formato de exportação](EXPORT-FORMAT.md).

## Estendendo

A maior parte do que o app procura vive como **listas de texto puro** sob
`assets/data/`, não como código. Adicionar ou remover um alvo é editar uma linha. Veja
[Contribuindo](CONTRIBUTING.md).

## O que o VD Infos nunca faz

- Ele nunca muda nada no seu sistema. Ele apenas lê e compara.
- Ele nunca fala com a rede. Ele funciona totalmente offline.
- Ele nunca roda em segundo plano, agenda uma varredura ou mantém um serviço vivo.
- Ele nunca envia uma leitura a lugar nenhum. Um relatório sai do dispositivo apenas quando você compartilha ou
  salva, com os valores que carregam identidade mascarados.

<!-- doc-footer -->

---

> _Esta documentação foi gerada com o Claude (Anthropic) e é mantida por VD171. Correções e melhorias são bem-vindas; veja [CONTRIBUTING.md](CONTRIBUTING.md)._
