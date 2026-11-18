> **Languages:** [English](../USER-GUIDE.md) · [العربية](../ar/USER-GUIDE.md) · [Čeština](../cs/USER-GUIDE.md) · [Deutsch](../de/USER-GUIDE.md) · Español · [فارسی](../fa/USER-GUIDE.md) · [Français](../fr/USER-GUIDE.md) · [हिन्दी](../hi/USER-GUIDE.md) · [Bahasa Indonesia](../id/USER-GUIDE.md) · [Italiano](../it/USER-GUIDE.md) · [日本語](../ja/USER-GUIDE.md) · [한국어](../ko/USER-GUIDE.md) · [Nederlands](../nl/USER-GUIDE.md) · [Polski](../pl/USER-GUIDE.md) · [Português](../pt/USER-GUIDE.md) · [Русский](../ru/USER-GUIDE.md) · [Svenska](../sv/USER-GUIDE.md) · [ไทย](../th/USER-GUIDE.md) · [Türkçe](../tr/USER-GUIDE.md) · [Українська](../uk/USER-GUIDE.md) · [Tiếng Việt](../vi/USER-GUIDE.md) · [中文](../zh/USER-GUIDE.md)

---

# Guía de usuario de VD Infos

VD Infos es un **depurador de métodos del dispositivo**. Lee cada valor del dispositivo a través de cada
método que puede leerlo, y alinea las lecturas una al lado de la otra para que puedas compararlas.
Esta guía cubre cómo instalar la aplicación, ejecutar un escaneo, leer el resultado y
compartir un informe. No interpreta ningún hallazgo concreto por ti: la comparación
es tuya para leerla, en tu propio dispositivo.

## Tabla de contenidos

1. [Qué es VD Infos](#1-qué-es-vd-infos)
2. [Qué no es](#2-qué-no-es)
3. [Requisitos](#3-requisitos)
4. [Qué APK instalar](#4-qué-apk-instalar)
5. [Instalación y primer uso](#5-instalación-y-primer-uso)
6. [Ejecutar un escaneo](#6-ejecutar-un-escaneo)
7. [Leer el resultado](#7-leer-el-resultado)
8. [Exportar tu informe](#8-exportar-tu-informe)
9. [Para agentes de IA](#9-para-agentes-de-ia)
10. [Idioma y tema](#10-idioma-y-tema)
11. [Privacidad de un vistazo](#11-privacidad-de-un-vistazo)
12. [Preguntas frecuentes](#12-preguntas-frecuentes)

## 1. Qué es VD Infos

Android entrega mucha información de tu dispositivo a cada aplicación que instalas. VD Infos
te muestra un ejemplo de lo que puede leer una aplicación corriente, y lo hace como un *depurador
de métodos*: para cada pieza de información lee el valor a través de **cada método
que puede leerlo** - las APIs de plataforma, las propiedades del sistema, las llamadas nativas,
los gestores del sistema, los proveedores de contenido, los archivos, las llamadas al sistema, y
la atestación de claves por hardware (TEE) - y los alinea para que puedas comparar. Es una lente,
no una máquina de veredictos: saca a la luz las lecturas y deja la lectura de ellas en tus manos.

## 2. Qué no es

- **No** es un limpiador, un acelerador ni una herramienta de ocultación. No cambia nada en tu
  sistema; solo lee y compara.
- **No** es una puntuación de aprobado/suspenso. Te muestra las lecturas en bruto; lo que significan para
  tu configuración es tuyo para juzgarlo, en tu dispositivo.
- **No** necesita internet, y funciona por completo con internet bloqueado.

## 3. Requisitos

- Android 8.0 (API 26) o más reciente.
- No se requiere ningún permiso especial para empezar. Algunas lecturas quedan disponibles solo si
  concedes el permiso correspondiente (por ejemplo acceso de uso), y algunas solo existen
  en un dispositivo con root; donde una lectura no está disponible, la aplicación simplemente muestra que no
  estaba disponible.

## 4. Qué APK instalar

Cada versión publica **dos APK**:

- **SDK_35** - apunta a la sandbox moderna y estricta. Es la que hay que usar.
- **SDK_27** - apunta a un nivel de API más antiguo. Es útil solo para la comparación lado a lado;
  la mayoría de la gente no la necesita.

Ambas se instalan en Android 8.0 y más reciente. Descárgalas desde la página de
[Lanzamientos](https://github.com/VD171/VD-Infos/releases).

## 5. Instalación y primer uso

1. Descarga el APK **SDK_35** desde Lanzamientos e instálalo (puede que necesites permitir
   instalaciones desde tu navegador o gestor de archivos).
2. Abre la aplicación. Se abre directamente en la pantalla de escaneo; no hay cuenta, no hay
   inicio de sesión ni configuración.
3. Opcionalmente concede un permiso que la aplicación te pida (como el acceso de uso) para desbloquear
   las lecturas que dependen de él. Esto es del todo opcional.

## 6. Ejecutar un escaneo

Toca la acción de escaneo. La aplicación despliega sus sondas en paralelo y transmite los resultados a
la lista a medida que llegan, con un indicador de progreso en vivo. Un escaneo se completa por sí solo;
lee solo mientras la aplicación está abierta, no deja nada ejecutándose en segundo plano, y se cierra
sola cuando se deja inactiva.

Para ejecutarlo de nuevo, escanea otra vez: cada escaneo reemplaza el resultado anterior.

## 7. Leer el resultado

Cada elemento es una tarjeta que alinea las lecturas de cada lente (el método Java SDK,
el método nativo, el shell, y así sucesivamente). El color de la tarjeta te dice, de un vistazo,
cómo se relacionan esas lecturas entre sí:

- **Coincidencia** - todas las lentes coincidieron. Los métodos devolvieron el mismo valor.
- **Divergencia** - las lentes no coincidieron. Un método devolvió algo distinto
  de los demás.
- **Información** - una lectura mostrada como referencia, no sometida a votación.
- **Único / no disponible** - solo una lente pudo leerlo, o ninguna pudo.

La tarjeta siempre muestra la matriz completa de lecturas, para que puedas ver exactamente qué lente
dijo qué. Una tarjeta plegada muestra su primera lectura; despliégala para verlas todas.

Hasta ahí llega lo que la aplicación dice por ti. **Lo que una divergencia dada significa en tu
dispositivo, y qué hacer al respecto, es tuyo para investigarlo** - ese es el sentido mismo
de un depurador: pone la evidencia delante de ti y te deja leerla. Usa la
caja de búsqueda para filtrar por propiedad o valor, y los chips de filtro para acotar la lista.

## 8. Exportar tu informe

Las acciones de compartir y guardar te entregan el **resultado completo**, no solo lo que está en pantalla:

- **Compartir / guardar como JSON** - el informe completo en forma legible por máquina, los mismos
  datos que la aplicación guarda internamente. Este es el formato para conservar o para alimentar a una herramienta.
- **Compartir como texto** - un resumen compacto y legible por humanos de las divergencias.

Los valores que portan identidad (números de serie, IMEI, direcciones MAC, identificadores de publicidad y similares)
están **enmascarados en el informe exportado** para que un informe que pegues en un hilo público
no filtre tus propios identificadores; los veredictos y la estructura se conservan. La
copia en el dispositivo que solo tú (o root) puedes leer conserva los valores reales.

## 9. Para agentes de IA

VD Infos está construido para ser leído por herramientas, no solo por personas. Cada escaneo escribe su
informe completo en el almacenamiento privado de la aplicación, y la exportación es el mismo JSON legible por máquina.
Un asistente de codificación con IA o un script de análisis ejecutándose en el dispositivo puede leer el resultado
completo con un solo comando en lugar de analizar capturas de pantalla. Consulta la
[guía para agentes de IA](AI-AGENTS.md) dedicada y la referencia del [formato de exportación](EXPORT-FORMAT.md).

## 10. Idioma y tema

La aplicación sigue el idioma de tu sistema cuando existe una traducción, y puedes elegir uno
manualmente. Usa Material 3 con color dinámico y sigue tu ajuste claro/oscuro.

## 11. Privacidad de un vistazo

Todo se ejecuta en el dispositivo. Ninguna lectura se envía a ninguna parte, y un informe sale del dispositivo
solo cuando lo compartes o lo guardas explícitamente. Cada escaneo conserva su informe completo en el
almacenamiento privado de la aplicación (`files/last_snapshot.json`) para que las herramientas puedan leerlo; solo la propia aplicación
o root pueden leer ese archivo, el siguiente escaneo lo sobrescribe, y nunca sale del dispositivo.
Consulta [Privacidad](PRIVACY.md) para la declaración completa.

## 12. Preguntas frecuentes

**¿Cambia algo en mi dispositivo?** No. Solo lee y compara.

**¿Necesita root?** No. La aplicación funciona sin root. Un puñado de lecturas solo existen
en un dispositivo con root, y el archivo de informe privado solo puede volver a ser leído por root o la
aplicación misma; todo lo demás funciona sin root.

**¿Necesita internet?** No. Funciona por completo sin conexión; bloquea su acceso a la red si
quieres.

**¿Qué APK debo usar?** SDK_35. La compilación SDK_27 existe solo para comparación.

**¿Por qué algunos elementos dicen no disponible?** Porque ese método no pudo leer ese valor en
tu dispositivo - un permiso que falta, una regla de la sandbox, o hardware que no está presente.
Eso es en sí mismo parte de la comparación.

**¿Dónde reporto un problema o sugiero un objetivo de detección?** Consulta
[Contribuir](CONTRIBUTING.md); la mayoría de las listas de detección son texto plano y no necesitan código.

<!-- doc-footer -->

---

> _Esta documentación se generó con Claude (Anthropic) y la mantiene VD171. Las correcciones y mejoras son bienvenidas; consulta [CONTRIBUTING.md](CONTRIBUTING.md)._
