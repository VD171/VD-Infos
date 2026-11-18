> **Languages:** [English](../PRIVACY.md) · [العربية](../ar/PRIVACY.md) · [Čeština](../cs/PRIVACY.md) · [Deutsch](../de/PRIVACY.md) · Español · [فارسی](../fa/PRIVACY.md) · [Français](../fr/PRIVACY.md) · [हिन्दी](../hi/PRIVACY.md) · [Bahasa Indonesia](../id/PRIVACY.md) · [Italiano](../it/PRIVACY.md) · [日本語](../ja/PRIVACY.md) · [한국어](../ko/PRIVACY.md) · [Nederlands](../nl/PRIVACY.md) · [Polski](../pl/PRIVACY.md) · [Português](../pt/PRIVACY.md) · [Русский](../ru/PRIVACY.md) · [Svenska](../sv/PRIVACY.md) · [ไทย](../th/PRIVACY.md) · [Türkçe](../tr/PRIVACY.md) · [Українська](../uk/PRIVACY.md) · [Tiếng Việt](../vi/PRIVACY.md) · [中文](../zh/PRIVACY.md)

---

# Privacidad

La privacidad no es una funcionalidad de VD Infos. Es el sentido entero. Esta página expresa exactamente
qué hace y qué no hace la aplicación con tus datos, en términos claros que puedes verificar.

## La promesa

- **Todo se ejecuta en el dispositivo.** Ninguna lectura se envía, sube ni transmite a ningún
  servidor. La aplicación funciona por completo con internet bloqueado.
- **Sin cuenta, sin inicio de sesión, sin trabajo en segundo plano.** La aplicación lee solo mientras está abierta,
  no deja nada ejecutándose en segundo plano, y se cierra sola cuando se deja inactiva.
- **Nada cambia en tu sistema.** VD Infos solo lee y compara; nunca escribe
  en el estado de tu dispositivo.

## El informe se queda contigo

Un informe sale del dispositivo **solo cuando lo compartes o lo guardas explícitamente**. Hasta entonces
existe en dos lugares, ambos en el dispositivo:

- **En pantalla**, mientras la aplicación está abierta.
- En el **almacenamiento privado** de la aplicación (`files/last_snapshot.json`), que solo la aplicación
  misma o root pueden leer. El siguiente escaneo lo sobrescribe, y nunca sale del dispositivo.
  Existe para que las herramientas y los agentes de IA puedan leer el resultado completo; consulta la
  [guía para agentes de IA](AI-AGENTS.md).

## Las exportaciones están enmascaradas

Cuando compartes o guardas un informe, los valores que portan identidad - números de serie, IMEI, direcciones
MAC, identificadores de publicidad y similares - están **enmascarados** antes de que el informe se escriba,
para que un informe que pegues en un hilo público no filtre tus propios identificadores. Los
veredictos y la estructura se conservan; solo los valores sensibles se redactan. La
copia privada en el dispositivo conserva los valores reales para tu propio uso.

## Permisos

La aplicación empieza sin requerir ningún permiso. Algunas lecturas quedan disponibles solo si
concedes el permiso correspondiente (por ejemplo acceso de uso), y concederlo es del todo
tu elección; donde una lectura no está disponible, la aplicación simplemente lo dice.

## Verifícalo tú mismo

No tienes que tomarte nada de esto por fe:

- La fuente es pública: <https://github.com/VD171/VD-Infos>.
- El manifiesto está en la fuente; confirma qué permisos declara.
- Bloquea el acceso a la red de la aplicación con un cortafuegos, o apaga internet, y sigue
  funcionando igual.

Consulta la [guía de usuario](USER-GUIDE.md) para saber cómo leer un informe y qué contiene la
exportación.

<!-- doc-footer -->

---

> _Esta documentación se generó con Claude (Anthropic) y la mantiene VD171. Las correcciones y mejoras son bienvenidas; consulta [CONTRIBUTING.md](CONTRIBUTING.md)._
