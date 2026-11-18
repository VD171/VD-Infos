> **Languages:** [English](../AI-AGENTS.md) · [العربية](../ar/AI-AGENTS.md) · [Čeština](../cs/AI-AGENTS.md) · [Deutsch](../de/AI-AGENTS.md) · [Español](../es/AI-AGENTS.md) · [فارسی](../fa/AI-AGENTS.md) · [Français](../fr/AI-AGENTS.md) · [हिन्दी](../hi/AI-AGENTS.md) · [Bahasa Indonesia](../id/AI-AGENTS.md) · [Italiano](../it/AI-AGENTS.md) · [日本語](../ja/AI-AGENTS.md) · [한국어](../ko/AI-AGENTS.md) · [Nederlands](../nl/AI-AGENTS.md) · [Polski](../pl/AI-AGENTS.md) · [Português](../pt/AI-AGENTS.md) · [Русский](../ru/AI-AGENTS.md) · [Svenska](../sv/AI-AGENTS.md) · [ไทย](../th/AI-AGENTS.md) · [Türkçe](../tr/AI-AGENTS.md) · [Українська](../uk/AI-AGENTS.md) · [Tiếng Việt](../vi/AI-AGENTS.md) · 中文

---

# 面向 AI 代理的 VD Infos

VD Infos **的构建是为了被机器读取，而不仅仅是被人读取**。一个设备方法调试器会产生大量结构化证据 -
每一个值，通过每一种方法读取，并排排列以供比较 - 而对一个代理来说，这些证据作为数据远比作为截图有用。
因此整个结果都以单一的机器可读文档形式提供，并且应用从根基处就是围绕这一点设计的。

如果你是一个 AI 编码助手、一个 LLM 工具、一个分析脚本，或是任何通过 shell 触及设备的自动化，本页就是
为你而写。

## 为什么它对代理友好

- **一份文档，而不是一个屏幕。** 整次扫描 - 每个探针、每面镜头、每个裁决 - 是单一的 JSON 对象。无需
  OCR、无需屏幕抓取、无需逐个点开折叠的卡片。
- **与界面所拥有的是同一份数据。** 该文件不是摘要。它恰是应用内部所持有的：屏幕显示一个折叠项的第一个
  读数，文件则拥有它们全部。
- **确定性的形状。** 结构稳定且有文档记录（参见
  [EXPORT-FORMAT](EXPORT-FORMAT.md)），因此写一次的解析器能持续工作。
- **无网络、无账户、无需管理的状态。** 应用在设备上读取并写入一个文件。你的代理读取该文件。这就是全部
  契约。
- **按设计离线。** 它不需要互联网，因此它在一个代理可能正在其中运行的封闭或物理隔离网络的环境里也照常
  运行。

## 一条命令的读取

每次扫描都把它的完整报告写入应用的私有存储。一次扫描运行过后，代理用单一一条命令读取完整结果：

```sh
su -c cat /data/data/ru.vd171.vdinfos/files/last_snapshot.json
```

- **它包含什么：** 每个探针，带有它的裁决以及来自每面镜头的值，采用与导出按钮相同的 JSON。
- **它何时更新：** 在每次扫描结束时。请先运行一次扫描；该文件始终持有最新的那次。
- **要求：** root，因为该文件位于应用的私有存储中。没有 root 时，请改用应用内的**分享 / 另存为 JSON**
  操作，并读取交给代理的那个文件。

## 一个典型的代理流程

1. 确保应用已安装（来自 [Releases](https://github.com/VD171/VD-Infos/releases) 的 **SDK_35** APK）。
2. 触发一次扫描 - 由某人点击扫描，或由某个自动化启动应用的扫描界面 - 并等待它完成。
3. 用上面的命令读取 `files/last_snapshot.json`。
4. 以[导出格式](EXPORT-FORMAT.md)作为你的模式来解析它：遍历各探针，读取每个的 `verdict`，并把各镜头
   的 `values` 并排排列。
5. 在你自己的工作流中基于这个结构化结果采取行动。

代理决定这些读数对手头任务意味着什么；VD Infos 干净而完整地提供证据。

## 代理得到什么，不得到什么

- 报告承载**每一个读数和每一个裁决**，因此代理拥有应用所构建的完整图景。
- 承载身份的值（序列号、IMEI、MAC 地址、广告 ID 之类）在**被分享/导出的报告中被遮蔽**，因此一份被传来
  传去的报告在保留其裁决和结构的同时，不含个人标识符。**设备上的** `last_snapshot.json` 只能由应用或
  root 读取，为需要真实值的本地代理保留它们。

## 为被解析而编写的文档

这个 wiki 为机器阅读所做的结构化不亚于为人阅读：短小的小节、稳定的标题、明确的字段名，以及一个代理可
以当作模式对待的专门的[导出格式](EXPORT-FORMAT.md)页面。如果你正在 VD Infos 之上构建一个工具，就从那
里开始。

## 相关页面

- [导出格式](EXPORT-FORMAT.md) - JSON 模式，逐字段。
- [用户指南](USER-GUIDE.md) - 安装、扫描与分享。
- [架构](ARCHITECTURE.md) - 应用是如何组装起来的。

<!-- doc-footer -->

---

> _本文档由 Claude (Anthropic) 生成，并由 VD171 维护。欢迎修正和改进；请参阅 [CONTRIBUTING.md](CONTRIBUTING.md)。_
