> **Languages:** [English](../CONTRIBUTING.md) · [العربية](../ar/CONTRIBUTING.md) · [Čeština](../cs/CONTRIBUTING.md) · [Deutsch](../de/CONTRIBUTING.md) · [Español](../es/CONTRIBUTING.md) · [فارسی](../fa/CONTRIBUTING.md) · [Français](../fr/CONTRIBUTING.md) · [हिन्दी](../hi/CONTRIBUTING.md) · [Bahasa Indonesia](../id/CONTRIBUTING.md) · [Italiano](../it/CONTRIBUTING.md) · [日本語](../ja/CONTRIBUTING.md) · [한국어](../ko/CONTRIBUTING.md) · [Nederlands](../nl/CONTRIBUTING.md) · [Polski](../pl/CONTRIBUTING.md) · [Português](../pt/CONTRIBUTING.md) · [Русский](../ru/CONTRIBUTING.md) · [Svenska](../sv/CONTRIBUTING.md) · [ไทย](../th/CONTRIBUTING.md) · [Türkçe](../tr/CONTRIBUTING.md) · [Українська](../uk/CONTRIBUTING.md) · [Tiếng Việt](../vi/CONTRIBUTING.md) · 中文

---

# 贡献

感谢你对 VD Infos 的兴趣。欢迎各种贡献：检测列表、翻译、错误报告、文档和代码。大多数贡献不需要 Kotlin。

## 最简单的贡献：那些列表

应用所寻找内容的大部分，是作为 `VDInfos/app/src/main/assets/data/` 下的**纯文本列表**存在的，而不是作
为代码。添加或移除一个目标就是编辑一行并开一个 pull request：

- `*_apps.txt` - 每行一个包名。
- `props.txt` - 系统属性目录，`CATEGORY<tab>key`。
- `spoof_keys.txt` - 设置矩阵，`key:TYPE`。
- `known_certs.txt` - 签名证书目录，`SHA-256<tab>owner`（完整哈希，用 `apksigner verify --print-certs`
  从一个真实的 APK 取得）。
- 其他 `.txt` 文件 - 每行一个条目（内核模块名片段、文件名等等）。

`#` 开始一条注释，空行被忽略。有些文件每行塞进几个以空格或制表符分隔的字段，或可选的标记；请保持文件中
已有各行的形状。

## 基本规则

- **VD Infos 读取真相，从不伪造任何东西。** 它精确地报告每个方法所返回的内容。不要为了让读数看起来更好
  看而软化它，也不要添加任何会改变一个值而不是读取它的东西。
- **隐私是绝对的。** 一切都在设备上运行。切勿添加网络调用，或任何把读数发送出设备的东西。
- **导出保持被遮蔽。** 承载身份的值在被分享的报告中被遮蔽。请保持这样。

## 翻译

翻译是一项宝贵的贡献。readme 按语言翻译（`README.<lang>.md`），应用字符串按语言翻译，而本文档在
`docs/<lang>/` 之下。请在一种语言内保持术语一致，匹配既有的语气，并且切勿机器翻译一个你没把握的技术术
语。

## 报告一个错误

开一个 issue，附上你的设备型号、Android 版本，并且，如果它是关于某个读数的，附上一份被分享的**导出**
（它承载身份的值已被遮蔽）。导出正是为此而构建的。

## 代码

- 构建 release，从不 debug。在真实设备上测试。参见 [构建](BUILDING.md)。
- 让注释远离代码；名称解释意图。唯一的头部是每个源文件顶部的许可证横幅。
- 匹配既有风格，并让改动保持聚焦。

## 许可证

通过贡献，你同意你的贡献按项目的 **GNU AGPL-3.0-or-later** 授权。

<!-- doc-footer -->

---

> _本文档由 Claude (Anthropic) 生成，并由 VD171 维护。欢迎修正和改进；请参阅 [CONTRIBUTING.md](CONTRIBUTING.md)。_
