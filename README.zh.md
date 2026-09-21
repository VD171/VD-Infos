[English](README.md) | [Português](README.pt.md) | [Español](README.es.md) | [Italiano](README.it.md) | [Deutsch](README.de.md) | [Français](README.fr.md) | [Русский](README.ru.md) | [Bahasa Indonesia](README.id.md) | [Türkçe](README.tr.md) | [Polski](README.pl.md) | [Nederlands](README.nl.md) | [Svenska](README.sv.md) | [Čeština](README.cs.md) | [Tiếng Việt](README.vi.md) | **中文** | [日本語](README.ja.md) | [한국어](README.ko.md) | [فارسی](README.fa.md) | [हिन्दी](README.hi.md) | [العربية](README.ar.md) | [ไทย](README.th.md)

# VD Infos

*方法调试器*

[![License: AGPL-3.0-or-later](https://img.shields.io/badge/license-AGPL--3.0--or--later-blue.svg)](LICENSE)
[![Release](https://img.shields.io/github/v/release/VD171/VD-Infos)](https://github.com/VD171/VD-Infos/releases)
[![Downloads](https://img.shields.io/github/downloads/VD171/VD-Infos/total)](https://github.com/VD171/VD-Infos/releases)
[![Android 8.0+](https://img.shields.io/badge/Android-8.0%2B-3DDC84.svg)](https://github.com/VD171/VD-Infos/releases)
[![Languages](https://img.shields.io/badge/languages-21-orange.svg)](README.md)

<img src="images/vdinfos-01.png" height="420"/> <img src="images/vdinfos-02.png" height="420"/>

Android 是一个极其强大且灵活的操作系统；没有人告诉你的是，你所有的个人细节和机密信息对你安装的每一个应用都是可获取的，而保护自己免受这类隐私侵犯是一种义务。VD Infos 向你展示可以从你的设备上捕获什么，并以*方法调试器*的方式进行：对每一项信息，它通过**所有能读取它的方法**读取该值 - `Build.*`、`SystemProperties`、`getprop`、原生的 `__system_property_get`、系统 manager、content provider、文件、系统调用，以及硬件密钥认证（TEE）- 并把它们并排列出以便你比较。当一个方法与其他方法不一致时，中间就有东西在改写那个表面：hook 框架、spoofer、resolver shim。**不存储、不发送、不向任何文件或服务器传输任何信息** - 一切都在设备上运行，携带身份的值在你揭示之前都被遮蔽，报告只有在你明确分享或保存时才会离开设备；如果你愿意，用防火墙拦截网络访问，或直接关掉网络。

## 它检查什么

每一项都通过所有能读取它的方法读取（Java SDK / 原生 / shell），并排比较：

* **约 488 个系统属性**以五种方式读取：`SystemProperties.get`、bionic 的两个入口（`__system_property_read_callback` 和 92 字节的 `__system_property_get`）、由 JVM 启动的 `getprop`，以及从原生代码经 `popen` 启动的 `getprop` - 由 `ProcessBuilder` 运行的 shell 命令与不经 JVM 运行的同一命令并非同一视角，因为前者是隐藏 root 的框架会改写的表面，后者不是。
* **设备身份**：型号、制造商、品牌、device、product、board、hardware、fingerprint、bootloader、build id/tags/type - 每个 `Build.*` 字段对照它所有的 `ro.product.*`（system/vendor/odm）变体、原生与 shell。
* **标识符**：serial（多个 getter）、Android ID（settings 与 provider）、GSF ID、advertising ID、IMEI/primary IMEI/MEID/IMSI/ICCID、user serial。
* **电话**：TelephonyManager 表面（运营商、SIM、网络、漫游……）、SubscriptionManager（多 SIM）、cell info。
* **网络**：每个接口的 **MAC**（`NetworkInterface` vs `/sys/class/net` vs shell）、Wi-Fi（SSID/BSSID/IP/MAC）、蓝牙、DNS。
* **内核 / 进程 / 系统**：`uname`、boot id、uptime、UID/PID、SELinux、时区、locale、环境、运行中的服务/进程。
* **硬件 / 媒体**：CPU、内存、传感器、显示、摄像头、system features、GPU、Widevine DRM ID。
* **软件包 / 账户 / WebView**：已安装包列表与数量、各应用签名摘要、本应用签名、对自身包的自省、intent 解析枚举、账户、通话记录数量、user agent。
* **沙箱合规**：沙箱本应**关闭**的表面（eMMC CID/serial、UFS 与 SoC 身份、`/proc/cmdline`、`/proc/1/*`、`/dev/kmsg`、`/system/build.prop`）以及 22 个 `dumpsys` 服务。这里拒绝就是答案：`EACCES`/`DENIED` 才是合规读数，而把内容交出来的设备则不合规。
* **广告标识符**：来自 Play Services binder 的 GAID，加上整个密钥家族（AAID、OAID/VAID 以及华为的 `pps_*`），在三个 settings store 中扫描。
* **Root / hook / 模拟器检测**：`su`/Magisk/KernelSU/APatch/Xposed/LSPosed/Riru 的痕迹、`/proc/self/maps` 中注入的库、已知包、危险属性与模拟器属性、verified boot state。

## 下载与支持

每个发行版发布两个 APK：**SDK_35** 面向严格的现代沙箱，是推荐使用的版本；**SDK_27** 面向较旧的 API 级别，SELinux 域更宽松，便于对比。两者都可安装在 Android 8.0 及以上。

* Release (APK): https://github.com/VD171/VD-Infos/releases
* https://github.com/VD171/VD-Infos
* https://xdaforums.com/t/VD-Infos.4097379/
* https://t.me/RootDetected
* https://t.me/BlankAssistance

## 语言

21 种界面语言：英语、葡萄牙语、西班牙语、意大利语、德语、法语、俄语、印度尼西亚语、土耳其语、波兰语、荷兰语、瑞典语、捷克语、越南语、中文、日语、韩语、波斯语、印地语、阿拉伯语和泰语。应用在首次启动时提供一次性的语言选择（含"系统默认"选项），否则跟随系统语言。

## 架构

```
core/model      不可变领域: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          NativeBridge (JNI) - SystemProps (反射) - PropCatalog (数据)
                SemanticProbes - IntegrityProbes - ProbeRegistry
engine/         ProbeEngine - 协程 fan-out，以 Flow 流式返回结果
data/           SnapshotStore (持久化 + diff) - Exporter (JSON/文本 分享)
ui/             Jetpack Compose, Material 3, 动态取色、实时进度
cpp/            native_probes.cpp - 原生透镜，无依赖
```

* **并行**：907 个探针在默认 dispatcher 上以有限的许可数散开；结果一到达便流入 UI。
* **后台无任何动作**：没有服务，也没有计划扫描；应用仅在打开时运行，闲置后自行关闭。
* **原生层**：一个小 `.so`，通过 `RegisterNatives` 按名绑定，刻意保持极小，因为这是必须难以被欺骗的部分。

## 参与贡献

大多数贡献不需要 Kotlin：这些列表以纯文本形式放在 `VDInfos/app/src/main/assets/data/` 下。增删一行并提交 pull request 即可。

* `*_apps.txt` - 每行一个包名
* `props.txt` - 系统属性目录，`类别<tab>键`
* `spoof_keys.txt` - settings 的 spoof 矩阵，`键:类型`
* 其他 `.txt` 文件 - 同样每行一个条目（内核模块名片段、`/data/local/tmp` 中的文件名）

`#` 开始注释；空行会被忽略。也欢迎代码贡献。提交贡献即表示你同意其以本项目的 AGPL-3.0-or-later 发布。

## 目录

Root 隐藏与检测的参考（指南、模块、框架、检测器）收录于专门的目录：[CATALOG.md](CATALOG.md)。

## 致谢

特别感谢 **[HMA-OSS](https://github.com/frknkrc44/HMA-OSS)** 的开发者 **[frknkrc44](https://github.com/frknkrc44)** 所倾注的用心。HMA-OSS 正是本应用建议的若干修复的落地之处 - 隐藏目标应用、设置 spoof 预设 - 应用内每一次提及它都会链接回其源。

它的链接、频道以及支持方式：[HMA-OSS.md](HMA-OSS.md)。

## 联系方式

* https://vd171.ru
* https://vd.priv8.ru
* **Telegram:** @VD_Priv8 https://t.me/VD_Priv8
* **Discord:** @VD.Priv8 https://discord.com/users/1296831918989639721
* **电子邮件:** vd.priv8@pm.me
* **XDA-Developers:** @VD171 https://xdaforums.com/m/vd171.4699873/
* **GitHub:** @VD171 https://github.com/VD171

## 许可证

**GNU AGPL-3.0-or-later** - 见 [LICENSE](LICENSE)。Copyleft，含网络条款：任何运行修改版（即使作为服务）的人都必须提供其源代码。为一款研究/反检测工具刻意选择，以保持 fork 开放。

---

`android` `privacy` `security` `root-detection` `hook-detection` `spoof-detection` `anti-detection` `tamper-detection` `device-fingerprint` `magisk` `kernelsu` `lsposed` `play-integrity` `attestation` `tee` `keystore` `scanner` `diagnostics` `reverse-engineering` `kotlin`
