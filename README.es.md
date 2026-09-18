[English](README.md) | [Português](README.pt.md) | **Español** | [Italiano](README.it.md) | [Deutsch](README.de.md) | [Français](README.fr.md) | [Русский](README.ru.md) | [Bahasa Indonesia](README.id.md) | [Türkçe](README.tr.md) | [Polski](README.pl.md) | [Nederlands](README.nl.md) | [Svenska](README.sv.md) | [Čeština](README.cs.md) | [Tiếng Việt](README.vi.md) | [中文](README.zh.md) | [日本語](README.ja.md) | [한국어](README.ko.md) | [فارسی](README.fa.md) | [हिन्दी](README.hi.md) | [العربية](README.ar.md) | [ไทย](README.th.md)

# VD Infos

*Depurador de métodos*

[![License: AGPL-3.0-or-later](https://img.shields.io/badge/license-AGPL--3.0--or--later-blue.svg)](LICENSE)
[![Release](https://img.shields.io/github/v/release/VD171/VD-Infos)](https://github.com/VD171/VD-Infos/releases)
[![Downloads](https://img.shields.io/github/downloads/VD171/VD-Infos/total)](https://github.com/VD171/VD-Infos/releases)
[![Android 8.0+](https://img.shields.io/badge/Android-8.0%2B-3DDC84.svg)](https://github.com/VD171/VD-Infos/releases)
[![Languages](https://img.shields.io/badge/languages-21-orange.svg)](README.md)

<img src="images/vdinfos-01.png" height="420"/> <img src="images/vdinfos-02.png" height="420"/>

Android es un sistema operativo súper potente y versátil; lo que nadie te cuenta es que todos tus datos personales e información confidencial quedan al alcance de cada aplicación que instalas, y protegerte contra estas invasiones de privacidad es una obligación. VD Infos te muestra un ejemplo de lo que se puede capturar de tu dispositivo, y lo hace como un *depurador de métodos*: para cada dato lee el valor por **todos los métodos capaces de leerlo** - `Build.*`, `SystemProperties`, `getprop`, el nativo `__system_property_get`, managers del sistema, content providers, archivos, syscalls y attestation de clave por hardware (TEE) - y los alinea para que compares. Cuando un método discrepa de los demás, algo en medio está reescribiendo esa superficie: un framework de hooking, un spoofer, un shim de resolución. **NINGUNA INFORMACIÓN SE ALMACENA, ENVÍA NI TRANSMITE A NINGÚN ARCHIVO O SERVIDOR** - todo se ejecuta en el dispositivo, los valores de identidad quedan enmascarados hasta que los reveles, y un informe solo sale del dispositivo cuando lo compartes o guardas explícitamente; si quieres, bloquea el acceso a internet con un firewall o simplemente apágalo.

## Qué inspecciona

Cada elemento se lee por todos los métodos capaces de leerlo (SDK Java / nativo / shell), comparados lado a lado:

* **~493 propiedades del sistema** leídas de CINCO formas: `SystemProperties.get`, las dos entradas de bionic (`__system_property_read_callback` y la de 92 bytes `__system_property_get`), `getprop` lanzado por la JVM, y `getprop` lanzado desde código nativo con `popen` - un comando de shell ejecutado por `ProcessBuilder` y el mismo comando ejecutado sin la JVM no son el mismo punto de vista, porque el primero es una superficie que un framework de ocultación reescribe y el segundo no.
* **Identidad del dispositivo**: modelo, fabricante, marca, device, product, board, hardware, fingerprint, bootloader, build id/tags/type - cada campo `Build.*` contra todas sus variantes `ro.product.*` (system/vendor/odm), nativo y shell.
* **Identificadores**: serial (varios getters), Android ID (settings y provider), GSF ID, advertising ID, IMEI/primary IMEI/MEID/IMSI/ICCID, user serial.
* **Telefonía**: la superficie de TelephonyManager (operadora, SIM, red, roaming...), SubscriptionManager (multi-SIM), cell info.
* **Red**: **MAC** por interfaz (`NetworkInterface` vs `/sys/class/net` vs shell), Wi-Fi (SSID/BSSID/IP/MAC), Bluetooth, DNS.
* **Kernel / proceso / sistema**: `uname`, boot id, uptime, UID/PID, SELinux, zona horaria, locale, entorno, servicios/procesos en ejecución.
* **Hardware / medios**: CPU, memoria, sensores, pantalla, cámaras, system features, GPU, ID DRM Widevine.
* **Paquetes / cuentas / WebView**: lista y recuento de paquetes instalados, digests de firma por app, firma de esta app, auto-inspección del propio paquete, enumeraciones de resolución de intents, cuentas, recuento del registro de llamadas, user agent.
* **Conformidad del sandbox**: superficies que el sandbox debe CERRAR (CID/serial de eMMC, identidad de UFS y SoC, `/proc/cmdline`, `/proc/1/*`, `/dev/kmsg`, `/system/build.prop`) y 22 servicios de `dumpsys`. Aquí el rechazo es la respuesta: `EACCES`/`DENIED` es la lectura conforme, y un dispositivo que en su lugar entrega el contenido está fuera de conformidad.
* **Identificadores de anuncios**: el GAID desde el binder de Play Services, más toda la familia de claves (AAID, OAID/VAID y los `pps_*` de Huawei) barrida por los tres settings stores.
* **Detectores de root / hook / emulador**: artefactos de `su`/Magisk/KernelSU/APatch/Xposed/LSPosed/Riru, libs inyectadas en `/proc/self/maps`, paquetes conocidos, propiedades peligrosas y de emulador, verified boot state.

## Descarga y soporte

Cada release publica dos APKs: **SDK_35** apunta al sandbox moderno y estricto, y es el recomendado; **SDK_27** apunta a un nivel de API más antiguo, con un dominio SELinux más laxo, útil para comparar. Ambos se instalan en Android 8.0 o superior.

* Release (APK): https://github.com/VD171/VD-Infos/releases
* https://github.com/VD171/VD-Infos
* https://xdaforums.com/t/VD-Infos.4097379/
* https://t.me/RootDetected
* https://t.me/BlankAssistance

## Idiomas

21 idiomas de interfaz: Inglés, Portugués, Español, Italiano, Alemán, Francés, Ruso, Indonesio, Turco, Polaco, Neerlandés, Sueco, Checo, Vietnamita, Chino, Japonés, Coreano, Persa, Hindi, Árabe y Tailandés. La app ofrece una elección de idioma única en el primer inicio (con opción "Predeterminado del sistema") y, en caso contrario, sigue el idioma del sistema.

## Arquitectura

```
core/model      dominio inmutable: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          NativeBridge (JNI) - SystemProps (reflexión) - PropCatalog (datos)
                SemanticProbes - IntegrityProbes - ProbeRegistry
engine/         ProbeEngine - fan-out con coroutines, transmite resultados como un Flow
data/           SnapshotStore (persistencia + diff) - Exporter (compartir JSON/texto)
ui/             Jetpack Compose, Material 3, color dinámico, progreso en vivo
cpp/            native_probes.cpp - la lente nativa, sin dependencias
```

* **Paralelismo**: 885 sondas se despliegan en el dispatcher por defecto con un número de permisos acotado; los resultados fluyen a la UI a medida que llegan.
* **Nada en segundo plano**: sin servicios ni escaneos programados; la app se ejecuta solo mientras está abierta y se cierra sola cuando queda inactiva.
* **Capa nativa**: un `.so` pequeño, enlazado por nombre vía `RegisterNatives`, deliberadamente diminuto porque es la parte que debe ser difícil de engañar.

## Contribuir

La mayoría de las contribuciones no requieren Kotlin: las listas son texto plano en `VDInfos/app/src/main/assets/data/`. Añade o quita una línea y abre un pull request.

* `*_apps.txt` - un nombre de paquete por línea
* `props.txt` - el catálogo de propiedades del sistema, `CATEGORÍA<tab>clave`
* `spoof_keys.txt` - la matriz de spoof de settings, `clave:TIPO`
* otros archivos `.txt` - también una entrada por línea (fragmentos de nombre de módulos del kernel, nombres en `/data/local/tmp`)

Un `#` inicia un comentario; las líneas vacías se ignoran. Las contribuciones de código también son bienvenidas. Al contribuir aceptas que tu trabajo se distribuya bajo la AGPL-3.0-or-later de este proyecto.

## Catálogo

Las referencias de ocultación y detección de root (guías, módulos, frameworks, detectores) están en un catálogo dedicado: [CATALOG.md](CATALOG.md).

## Agradecimientos

Agradecimiento especial a **[frknkrc44](https://github.com/frknkrc44)**, desarrollador de **[HMA-OSS](https://github.com/frknkrc44/HMA-OSS)**, por el cariño dedicado. HMA-OSS es donde se aplican varias de las correcciones que esta app sugiere - ocultar apps objetivo y definir presets de spoof - y cada mención a él dentro de la app enlaza de vuelta a su fuente.

Sus enlaces, canales y cómo apoyarlo: [HMA-OSS.md](HMA-OSS.md).

## Contactos

* https://vd171.ru
* https://vd.priv8.ru
* **Telegram:** @VD_Priv8 https://t.me/VD_Priv8
* **Discord:** @VD.Priv8 https://discord.com/users/1296831918989639721
* **Correo:** vd.priv8@pm.me
* **XDA-Developers:** @VD171 https://xdaforums.com/m/vd171.4699873/
* **GitHub:** @VD171 https://github.com/VD171

## Licencia

**GNU AGPL-3.0-or-later** - ver [LICENSE](LICENSE). Copyleft, incluida la cláusula de red: cualquiera que ejecute una versión modificada (incluso como servicio) debe ofrecer su código fuente. Elegida deliberadamente para una herramienta de investigación/anti-detección, para mantener los forks abiertos.

---

`android` `privacy` `security` `root-detection` `hook-detection` `spoof-detection` `anti-detection` `tamper-detection` `device-fingerprint` `magisk` `kernelsu` `lsposed` `play-integrity` `attestation` `tee` `keystore` `scanner` `diagnostics` `reverse-engineering` `kotlin`
