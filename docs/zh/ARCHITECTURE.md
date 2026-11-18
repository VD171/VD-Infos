> **Languages:** [English](../ARCHITECTURE.md) · [العربية](../ar/ARCHITECTURE.md) · [Čeština](../cs/ARCHITECTURE.md) · [Deutsch](../de/ARCHITECTURE.md) · [Español](../es/ARCHITECTURE.md) · [فارسی](../fa/ARCHITECTURE.md) · [Français](../fr/ARCHITECTURE.md) · [हिन्दी](../hi/ARCHITECTURE.md) · [Bahasa Indonesia](../id/ARCHITECTURE.md) · [Italiano](../it/ARCHITECTURE.md) · [日本語](../ja/ARCHITECTURE.md) · [한국어](../ko/ARCHITECTURE.md) · [Nederlands](../nl/ARCHITECTURE.md) · [Polski](../pl/ARCHITECTURE.md) · [Português](../pt/ARCHITECTURE.md) · [Русский](../ru/ARCHITECTURE.md) · [Svenska](../sv/ARCHITECTURE.md) · [ไทย](../th/ARCHITECTURE.md) · [Türkçe](../tr/ARCHITECTURE.md) · [Українська](../uk/ARCHITECTURE.md) · [Tiếng Việt](../vi/ARCHITECTURE.md) · 中文

---

# 架构

这是一份关于 VD Infos 如何组装起来的高层地图。它描述应用的形状，而不是它所读取内容的目录：方法调试器
的要点是把每一个读数摆在你面前，而阅读它们是你在自己设备上要做的事。

## 设计目标

- **一个值，每一种方法。** 对每一条信息，通过每一种能读取它的方法去读取它，并把读数并排排列。调试器把
  证据呈现出来；它不作裁判。
- **在设备上且安静。** 无网络、无账户、无后台工作。应用只在它打开时运行。
- **可被机器读取。** 整个结果是单一的机器可读文档，因此工具得到的图景与人得到的相同。参见
  [AI 代理](AI-AGENTS.md)。
- **在要紧之处保持小巧。** 那个必须难以被骗过的部分被刻意保持得极小。

## 各镜头

*镜头*是读取一个值的一种方式。同一个值通过数种镜头读取，以便它们能被比较：

| 镜头 | 它是什么 |
|---|---|
| **Java / SDK** | 一个普通应用调用的平台 API。 |
| **Native / JNI** | 从原生代码读取的同一个值。 |
| **Shell via JVM** | 由 JVM 派生的一条 shell 命令。 |
| **Shell via JNI** | 从原生代码派生的同一条命令，一个不同的观察点。 |
| **TEE / Attestation** | 由硬件背书的密钥证明，在它适用之处。 |

把各镜头并排排列正是整个思路：当它们不一致时，在应用与值之间有某种东西正对不同的调用者作出不同的回答。
这对某个给定的值、在某个给定的设备上意味着什么，是你要读的。

## 一次扫描如何运行

```
core/model      immutable domain: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          the probes and the bridges they use (JNI, reflection, data lists)
engine/         ProbeEngine: parallel fan-out, streams results as they land
data/           SnapshotStore (persist the report), Exporter (share as JSON / text)
ui/             Jetpack Compose, Material 3, dynamic colour, live progress
cpp/            native_probes.cpp: the native lens, dependency-free
```

- **并行。** 各探针在一个带有限许可计数的调度器上铺开；结果在完成时流入界面，带有一个实时进度指示器。
- **后台没有任何东西。** 没有服务，也没有计划扫描。应用只在打开时读取，并在被闲置时自行关闭。
- **原生层。** 一个小小的 `.so`，通过 `RegisterNatives` 按名称绑定，被刻意保持得极小，因为它是那个必须
  难以被骗过的部分。

## 报告

每次扫描都产生一个 `Snapshot`：它被进行的时间、应用版本，以及一个结果列表，每个探针一个，每个都带有来
自每面镜头的读数以及一个关于它们如何关联的裁决。应用在每次扫描后把它持久化到私有存储，并能导出同一份
数据。该结构以[导出格式](EXPORT-FORMAT.md)记录为文档。

## 扩展它

应用所寻找内容的大部分，是作为 `assets/data/` 下的**纯文本列表**存在的，而不是作为代码。添加或移除一
个目标就是编辑一行。参见 [贡献](CONTRIBUTING.md)。

## VD Infos 从不做的事

- 它从不改变你系统上的任何东西。它只读取与比较。
- 它从不与网络通信。它完全离线工作。
- 它从不在后台运行、计划一次扫描，或让某个服务保持存活。
- 它从不把任何读数发送到任何地方。报告只有在你分享或保存它时才会离开设备，并且承载身份的值被遮蔽。

<!-- doc-footer -->

---

> _本文档由 Claude (Anthropic) 生成，并由 VD171 维护。欢迎修正和改进；请参阅 [CONTRIBUTING.md](CONTRIBUTING.md)。_
