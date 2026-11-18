> **Languages:** [English](../SECURITY.md) · [العربية](../ar/SECURITY.md) · [Čeština](../cs/SECURITY.md) · [Deutsch](../de/SECURITY.md) · Español · [فارسی](../fa/SECURITY.md) · [Français](../fr/SECURITY.md) · [हिन्दी](../hi/SECURITY.md) · [Bahasa Indonesia](../id/SECURITY.md) · [Italiano](../it/SECURITY.md) · [日本語](../ja/SECURITY.md) · [한국어](../ko/SECURITY.md) · [Nederlands](../nl/SECURITY.md) · [Polski](../pl/SECURITY.md) · [Português](../pt/SECURITY.md) · [Русский](../ru/SECURITY.md) · [Svenska](../sv/SECURITY.md) · [ไทย](../th/SECURITY.md) · [Türkçe](../tr/SECURITY.md) · [Українська](../uk/SECURITY.md) · [Tiếng Việt](../vi/SECURITY.md) · [中文](../zh/SECURITY.md)

---

# Política de seguridad

VD Infos es una herramienta de privacidad que lee estado sensible del dispositivo, así que los reportes de seguridad se
toman en serio.

## Reportar una vulnerabilidad

Por favor reporta los problemas de seguridad **de forma privada**, no en una incidencia pública:

- Correo electrónico **vd.priv8@pm.me**.
- Describe el problema, la versión afectada, y cómo reproducirlo.
- Si concierne a una exposición de datos, describe la clase de problema; no necesitas adjuntar
  un exploit funcional.

Obtendrás una respuesta tan pronto como sea posible. Por favor concede una ventana razonable para arreglar el
problema antes de cualquier divulgación pública.

## Qué cuenta como un problema de seguridad

Por lo que VD Infos promete, estos son especialmente importantes:

- Cualquier cosa que haga que la aplicación alcance la red, o envíe cualquier lectura fuera del
  dispositivo.
- Cualquier cosa que filtre un valor que porta identidad en una **exportación** compartida o guardada
  (un número de serie, IMEI, dirección MAC, identificador de publicidad y similares) a pesar del enmascaramiento.
- Cualquier cosa que permita a otra aplicación leer el archivo de informe privado que está pensado para ser
  legible solo por la aplicación misma o root.

## Qué no es un problema de seguridad

- Una lectura que parece "incorrecta" pero es el valor honesto que devolvió un método. VD Infos
  reporta lo que dijo cada método; leer la comparación es tuyo para hacerlo. Consulta la
  [guía de usuario](USER-GUIDE.md).
- Una lectura marcada como no disponible. Eso significa que un método no pudo leer ese valor en tu
  dispositivo, lo cual es en sí mismo parte de la comparación.

## Versiones compatibles

Solo la última versión lanzada tiene soporte. Por favor reproduce en la última compilación
antes de reportar.

<!-- doc-footer -->

---

> _Esta documentación se generó con Claude (Anthropic) y la mantiene VD171. Las correcciones y mejoras son bienvenidas; consulta [CONTRIBUTING.md](CONTRIBUTING.md)._
