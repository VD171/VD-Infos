> **Languages:** [English](../PRIVACY.md) · [العربية](../ar/PRIVACY.md) · [Čeština](../cs/PRIVACY.md) · [Deutsch](../de/PRIVACY.md) · [Español](../es/PRIVACY.md) · [فارسی](../fa/PRIVACY.md) · [Français](../fr/PRIVACY.md) · [हिन्दी](../hi/PRIVACY.md) · [Bahasa Indonesia](../id/PRIVACY.md) · [Italiano](../it/PRIVACY.md) · [日本語](../ja/PRIVACY.md) · [한국어](../ko/PRIVACY.md) · [Nederlands](../nl/PRIVACY.md) · [Polski](../pl/PRIVACY.md) · [Português](../pt/PRIVACY.md) · [Русский](../ru/PRIVACY.md) · [Svenska](../sv/PRIVACY.md) · [ไทย](../th/PRIVACY.md) · [Türkçe](../tr/PRIVACY.md) · [Українська](../uk/PRIVACY.md) · [Tiếng Việt](../vi/PRIVACY.md) · 中文

---

# 隐私

隐私不是 VD Infos 的一个功能。它是全部要点。本页用你可以验证的直白措辞，确切地陈述应用对你的数据做什
么和不做什么。

## 承诺

- **一切都在设备上运行。** 没有任何读数被发送、上传或传输到任何服务器。应用在互联网被屏蔽时也完全可用。
- **无账户、无登录、无后台工作。** 应用只在它打开时读取，不在后台保留任何运行中的东西，并在被闲置时自
  行关闭。
- **你系统上的任何东西都不被改变。** VD Infos 只读取与比较；它从不写入你设备的状态。

## 报告留在你身边

报告**只有在你明确分享或保存它时**才会离开设备。在那之前，它存在于两处，都在设备上：

- **在屏幕上**，当应用打开时。
- 在应用的**私有存储**（`files/last_snapshot.json`）中，只有应用本身或 root 能读取它。下一次扫描会覆盖
  它，并且它永远不会离开设备。它的存在是为了让工具和 AI 代理能读取完整结果；参见
  [AI 代理指南](AI-AGENTS.md)。

## 导出会被遮蔽

当你分享或保存一份报告时，承载身份的值 - 序列号、IMEI、MAC 地址、广告 ID 之类 - 在报告被写入之前**被遮
蔽**，这样你粘贴到公共帖子里的报告就不会泄露你自己的标识符。裁决与结构被保留；只有敏感值被删节。设备上
的私有副本为你自己使用而保留真实值。

## 权限

应用启动时不需要任何权限。某些读数只有在你授予对应权限（例如使用情况访问）时才变得可用，而是否授予它
完全是你的选择；当某个读数不可用时，应用只会如实说明。

## 自己来验证

你不必凭信任接受上述任何一点：

- 源码是公开的：<https://github.com/VD171/VD-Infos>。
- 清单文件就在源码中；请确认它声明了哪些权限。
- 用防火墙屏蔽应用的网络访问，或关掉互联网，它仍照常工作。

参见[用户指南](USER-GUIDE.md)了解如何阅读一份报告以及导出包含什么。

<!-- doc-footer -->

---

> _本文档由 Claude (Anthropic) 生成，并由 VD171 维护。欢迎修正和改进；请参阅 [CONTRIBUTING.md](CONTRIBUTING.md)。_
