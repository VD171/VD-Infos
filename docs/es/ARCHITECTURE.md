> **Languages:** [English](../ARCHITECTURE.md) · [العربية](../ar/ARCHITECTURE.md) · [Čeština](../cs/ARCHITECTURE.md) · [Deutsch](../de/ARCHITECTURE.md) · Español · [فارسی](../fa/ARCHITECTURE.md) · [Français](../fr/ARCHITECTURE.md) · [हिन्दी](../hi/ARCHITECTURE.md) · [Bahasa Indonesia](../id/ARCHITECTURE.md) · [Italiano](../it/ARCHITECTURE.md) · [日本語](../ja/ARCHITECTURE.md) · [한국어](../ko/ARCHITECTURE.md) · [Nederlands](../nl/ARCHITECTURE.md) · [Polski](../pl/ARCHITECTURE.md) · [Português](../pt/ARCHITECTURE.md) · [Русский](../ru/ARCHITECTURE.md) · [Svenska](../sv/ARCHITECTURE.md) · [ไทย](../th/ARCHITECTURE.md) · [Türkçe](../tr/ARCHITECTURE.md) · [Українська](../uk/ARCHITECTURE.md) · [Tiếng Việt](../vi/ARCHITECTURE.md) · [中文](../zh/ARCHITECTURE.md)

---

# Arquitectura

Este es un mapa de alto nivel de cómo está construida VD Infos. Describe la forma de la
aplicación, no el catálogo de lo que lee: el sentido de un depurador de métodos es poner cada
lectura delante de ti, y leerlas es tuyo para hacerlo en tu propio dispositivo.

## Objetivos de diseño

- **Un valor, cada método.** Para cada pieza de información, léela a través de cada
  método que pueda leerla y alinea las lecturas. Un depurador saca a la luz la evidencia; no
  emite juicios.
- **En el dispositivo y silencioso.** Sin red, sin cuenta, sin trabajo en segundo plano. La aplicación se ejecuta
  solo mientras está abierta.
- **Legible por máquinas.** El resultado completo es un documento legible por máquina, así que una
  herramienta obtiene la misma imagen que una persona. Consulta [Agentes de IA](AI-AGENTS.md).
- **Pequeño donde importa.** La parte que debe ser difícil de engañar se mantiene deliberadamente
  diminuta.

## Las lentes

Una *lente* es una forma de leer un valor. El mismo valor se lee a través de varias para que
puedan compararse:

| Lente | Qué es |
|---|---|
| **Java / SDK** | La API de plataforma que llama una aplicación corriente. |
| **Nativo / JNI** | El mismo valor leído desde código nativo. |
| **Shell vía JVM** | Un comando de shell generado por la JVM. |
| **Shell vía JNI** | El mismo comando generado desde código nativo, un punto de vista distinto. |
| **TEE / Atestación** | Atestación de claves respaldada por hardware, donde aplica. |

Alinear las lentes es toda la idea: cuando no coinciden, algo entre la aplicación
y el valor responde de forma distinta a distintos llamadores. Lo que eso significa para un
valor dado, en un dispositivo dado, es tuyo para leerlo.

## Cómo se ejecuta un escaneo

```
core/model      immutable domain: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          the probes and the bridges they use (JNI, reflection, data lists)
engine/         ProbeEngine: parallel fan-out, streams results as they land
data/           SnapshotStore (persist the report), Exporter (share as JSON / text)
ui/             Jetpack Compose, Material 3, dynamic colour, live progress
cpp/            native_probes.cpp: the native lens, dependency-free
```

- **Paralelismo.** Las sondas se despliegan a través de un dispatcher con un conteo acotado de permisos;
  los resultados se transmiten a la interfaz a medida que se completan, con un indicador de progreso en vivo.
- **Nada en segundo plano.** Sin servicios y sin escaneos programados. La aplicación lee solo
  mientras está abierta y se cierra sola cuando se deja inactiva.
- **La capa nativa.** Un pequeño `.so`, vinculado por nombre a través de `RegisterNatives`, mantenido
  deliberadamente diminuto porque es la parte que debe ser difícil de engañar.

## El informe

Cada escaneo produce un `Snapshot`: la hora en que se tomó, la versión de la aplicación, y una lista de
resultados, uno por sonda, cada uno con las lecturas de cada lente y un veredicto de cómo se
relacionan. La aplicación persiste esto en el almacenamiento privado tras cada escaneo y puede exportar
los mismos datos. La estructura está documentada como el [formato de exportación](EXPORT-FORMAT.md).

## Extenderla

La mayor parte de lo que la aplicación busca vive como **listas de texto plano** bajo
`assets/data/`, no como código. Añadir o quitar un objetivo es editar una línea. Consulta
[Contribuir](CONTRIBUTING.md).

## Lo que VD Infos nunca hace

- Nunca cambia nada en tu sistema. Solo lee y compara.
- Nunca habla con la red. Funciona por completo sin conexión.
- Nunca se ejecuta en segundo plano, programa un escaneo, ni mantiene un servicio vivo.
- Nunca envía una lectura a ninguna parte. Un informe sale del dispositivo solo cuando lo compartes o lo
  guardas, con los valores que portan identidad enmascarados.

<!-- doc-footer -->

---

> _Esta documentación se generó con Claude (Anthropic) y la mantiene VD171. Las correcciones y mejoras son bienvenidas; consulta [CONTRIBUTING.md](CONTRIBUTING.md)._
