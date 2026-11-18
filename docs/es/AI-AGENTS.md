> **Languages:** [English](../AI-AGENTS.md) · [العربية](../ar/AI-AGENTS.md) · [Čeština](../cs/AI-AGENTS.md) · [Deutsch](../de/AI-AGENTS.md) · Español · [فارسی](../fa/AI-AGENTS.md) · [Français](../fr/AI-AGENTS.md) · [हिन्दी](../hi/AI-AGENTS.md) · [Bahasa Indonesia](../id/AI-AGENTS.md) · [Italiano](../it/AI-AGENTS.md) · [日本語](../ja/AI-AGENTS.md) · [한국어](../ko/AI-AGENTS.md) · [Nederlands](../nl/AI-AGENTS.md) · [Polski](../pl/AI-AGENTS.md) · [Português](../pt/AI-AGENTS.md) · [Русский](../ru/AI-AGENTS.md) · [Svenska](../sv/AI-AGENTS.md) · [ไทย](../th/AI-AGENTS.md) · [Türkçe](../tr/AI-AGENTS.md) · [Українська](../uk/AI-AGENTS.md) · [Tiếng Việt](../vi/AI-AGENTS.md) · [中文](../zh/AI-AGENTS.md)

---

# VD Infos para agentes de IA

VD Infos está **construido para ser leído por máquinas, no solo por personas**. Un depurador de métodos
del dispositivo produce mucha evidencia estructurada - cada valor, leído a través de cada
método, alineado para la comparación - y esa evidencia es mucho más útil para un agente como
datos que como una captura de pantalla. Por eso el resultado completo está disponible como un documento
legible por máquina, y la aplicación está diseñada en torno a eso desde su base.

Si eres un asistente de codificación con IA, una herramienta LLM, un script de análisis o cualquier automatización
que llega al dispositivo a través de un shell, esta página es para ti.

## Por qué es amigable para agentes

- **Un documento, no una pantalla.** El escaneo entero - cada sonda, cada lente, cada
  veredicto - es un único objeto JSON. Sin OCR, sin raspado de pantalla, sin tocar a través de
  tarjetas plegadas.
- **Los mismos datos que tiene la interfaz.** El archivo no es un resumen. Es exactamente lo que la aplicación
  guarda internamente: la pantalla muestra la primera lectura de un elemento plegado, el archivo
  las tiene todas.
- **Forma determinista.** La estructura es estable y está documentada (consulta
  [EXPORT-FORMAT](EXPORT-FORMAT.md)), así que un analizador escrito una vez sigue funcionando.
- **Sin red, sin cuenta, sin estado que gestionar.** La aplicación lee en el dispositivo y escribe un
  archivo. Tu agente lee el archivo. Ese es todo el contrato.
- **Sin conexión por diseño.** No necesita internet, así que funciona igual dentro de un entorno
  cerrado o aislado en el que un agente pudiera estar operando.

## La lectura de un solo comando

Cada escaneo escribe su informe completo en el almacenamiento privado de la aplicación. Después de que un escaneo se haya ejecutado,
un agente lee el resultado completo con un solo comando:

```sh
su -c cat /data/data/ru.vd171.vdinfos/files/last_snapshot.json
```

- **Qué contiene:** cada sonda con su veredicto y el valor de cada lente,
  en el mismo JSON que el botón de exportación.
- **Cuándo se actualiza:** al final de cada escaneo. Ejecuta primero un escaneo; el archivo siempre
  contiene el último.
- **Requisito:** root, porque el archivo vive en el almacenamiento privado de la aplicación. Sin
  root, usa en su lugar la acción **compartir / guardar como JSON** dentro de la aplicación y lee el archivo que el
  agente recibe.

## Un flujo típico de agente

1. Asegúrate de que la aplicación está instalada (el APK **SDK_35** desde
   [Lanzamientos](https://github.com/VD171/VD-Infos/releases)).
2. Dispara un escaneo - una persona toca escanear, o una automatización lanza la pantalla de escaneo
   de la aplicación - y espera a que termine.
3. Lee `files/last_snapshot.json` con el comando de arriba.
4. Analízalo con el [formato de exportación](EXPORT-FORMAT.md) como tu esquema: itera las
   sondas, lee el `verdict` de cada una, y alinea los `values` por lente.
5. Actúa sobre el resultado estructurado en tu propio flujo de trabajo.

El agente decide qué significan las lecturas para la tarea en cuestión; VD Infos suministra la
evidencia, de forma limpia y completa.

## Qué obtiene el agente, y qué no

- El informe lleva **cada lectura y cada veredicto**, así que un agente tiene la imagen completa
  que construyó la aplicación.
- Los valores que portan identidad (números de serie, IMEI, direcciones MAC, identificadores de publicidad y similares)
  están **enmascarados en el informe compartido/exportado**, así que un informe que circule queda libre de
  identificadores personales mientras conserva sus veredictos y estructura. El `last_snapshot.json`
  **en el dispositivo**, legible solo por la aplicación o root, conserva los valores reales para un
  agente local que los necesite.

## Documentación, escrita para ser analizada

Esta wiki está estructurada para la lectura por máquina tanto como para las personas: secciones cortas,
encabezados estables, nombres de campo explícitos, y una página dedicada de [formato de exportación](EXPORT-FORMAT.md)
que un agente puede tratar como un esquema. Si estás construyendo una herramienta sobre VD Infos,
empieza ahí.

## Páginas relacionadas

- [Formato de exportación](EXPORT-FORMAT.md) - el esquema JSON, campo por campo.
- [Guía de usuario](USER-GUIDE.md) - instalar, escanear y compartir.
- [Arquitectura](ARCHITECTURE.md) - cómo está construida la aplicación.

<!-- doc-footer -->

---

> _Esta documentación se generó con Claude (Anthropic) y la mantiene VD171. Las correcciones y mejoras son bienvenidas; consulta [CONTRIBUTING.md](CONTRIBUTING.md)._
