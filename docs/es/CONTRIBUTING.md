> **Languages:** [English](../CONTRIBUTING.md) · [العربية](../ar/CONTRIBUTING.md) · [Čeština](../cs/CONTRIBUTING.md) · [Deutsch](../de/CONTRIBUTING.md) · Español · [فارسی](../fa/CONTRIBUTING.md) · [Français](../fr/CONTRIBUTING.md) · [हिन्दी](../hi/CONTRIBUTING.md) · [Bahasa Indonesia](../id/CONTRIBUTING.md) · [Italiano](../it/CONTRIBUTING.md) · [日本語](../ja/CONTRIBUTING.md) · [한국어](../ko/CONTRIBUTING.md) · [Nederlands](../nl/CONTRIBUTING.md) · [Polski](../pl/CONTRIBUTING.md) · [Português](../pt/CONTRIBUTING.md) · [Русский](../ru/CONTRIBUTING.md) · [Svenska](../sv/CONTRIBUTING.md) · [ไทย](../th/CONTRIBUTING.md) · [Türkçe](../tr/CONTRIBUTING.md) · [Українська](../uk/CONTRIBUTING.md) · [Tiếng Việt](../vi/CONTRIBUTING.md) · [中文](../zh/CONTRIBUTING.md)

---

# Contribuir

Gracias por tu interés en VD Infos. Las contribuciones son bienvenidas: listas de detección,
traducciones, reportes de errores, documentación y código. La mayoría de las contribuciones no necesitan Kotlin.

## La contribución más fácil: las listas

La mayor parte de lo que la aplicación busca vive como **listas de texto plano** bajo
`VDInfos/app/src/main/assets/data/`, no como código. Añadir o quitar un objetivo es editar
una línea y abrir un pull request:

- `*_apps.txt` - un nombre de paquete por línea.
- `props.txt` - el catálogo de propiedades del sistema, `CATEGORY<tab>key`.
- `spoof_keys.txt` - la matriz de ajustes, `key:TYPE`.
- `known_certs.txt` - el catálogo de certificados de firma, `SHA-256<tab>owner` (el hash
  completo, tomado de un APK real con `apksigner verify --print-certs`).
- otros archivos `.txt` - una entrada por línea (fragmentos de nombres de módulos del kernel, nombres de archivo,
  y así sucesivamente).

Un `#` inicia un comentario y las líneas en blanco se ignoran. Algunos archivos empaquetan unos pocos campos separados por espacios o
tabulaciones por línea, o marcadores opcionales; conserva la forma de las líneas que ya están
en el archivo.

## Reglas de base

- **VD Infos lee la verdad y nunca falsifica nada.** Reporta lo que cada método
  devolvió, exactamente. No suavices una lectura para hacerla parecer más bonita, y no añadas
  nada que cambie un valor en lugar de leerlo.
- **La privacidad es absoluta.** Todo se ejecuta en el dispositivo. Nunca añadas una llamada de red, ni
  nada que envíe una lectura fuera del dispositivo.
- **Las exportaciones permanecen enmascaradas.** Los valores que portan identidad están enmascarados en un informe compartido. Mantenlo
  así.

## Traducciones

Las traducciones son una contribución valiosa. El readme se traduce por idioma
(`README.<lang>.md`), las cadenas de la aplicación por idioma, y esta documentación bajo
`docs/<lang>/`. Mantén la terminología consistente dentro de un idioma, ajústate al tono existente,
y nunca traduzcas a máquina un término técnico del que no estés seguro.

## Reportar un error

Abre una incidencia con el modelo de tu dispositivo, la versión de Android, y, si es sobre una lectura, una
**exportación** compartida (sus valores que portan identidad están enmascarados). La exportación está construida para
exactamente esto.

## Código

- Compila release, nunca debug. Prueba en un dispositivo real. Consulta [Compilar](BUILDING.md).
- Mantén los comentarios fuera del código; los nombres explican la intención. El único encabezado es el banner
  de licencia en lo alto de cada archivo fuente.
- Ajústate al estilo existente y mantén los cambios enfocados.

## Licencia

Al contribuir aceptas que tu contribución se licencia bajo la
**GNU AGPL-3.0-or-later** del proyecto.

<!-- doc-footer -->

---

> _Esta documentación se generó con Claude (Anthropic) y la mantiene VD171. Las correcciones y mejoras son bienvenidas; consulta [CONTRIBUTING.md](CONTRIBUTING.md)._
