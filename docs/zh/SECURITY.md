> **Languages:** [English](../SECURITY.md) · [العربية](../ar/SECURITY.md) · [Čeština](../cs/SECURITY.md) · [Deutsch](../de/SECURITY.md) · [Español](../es/SECURITY.md) · [فارسی](../fa/SECURITY.md) · [Français](../fr/SECURITY.md) · [हिन्दी](../hi/SECURITY.md) · [Bahasa Indonesia](../id/SECURITY.md) · [Italiano](../it/SECURITY.md) · [日本語](../ja/SECURITY.md) · [한국어](../ko/SECURITY.md) · [Nederlands](../nl/SECURITY.md) · [Polski](../pl/SECURITY.md) · [Português](../pt/SECURITY.md) · [Русский](../ru/SECURITY.md) · [Svenska](../sv/SECURITY.md) · [ไทย](../th/SECURITY.md) · [Türkçe](../tr/SECURITY.md) · [Українська](../uk/SECURITY.md) · [Tiếng Việt](../vi/SECURITY.md) · 中文

---

# 安全策略

VD Infos 是一个读取敏感设备状态的隐私工具，因此安全报告会被认真对待。

## 报告一个漏洞

请**私下**报告安全问题，而不是在一个公开的 issue 中：

- 发邮件至 **vd.priv8@pm.me**。
- 描述该问题、受影响的版本，以及如何复现它。
- 如果它涉及数据暴露，请描述问题的类别；你不需要附上一个可用的利用程序。

你会尽快得到回复。请在任何公开披露之前给出一个合理的时间窗来修复该问题。

## 什么算作一个安全问题

由于 VD Infos 所承诺的内容，以下这些尤其重要：

- 任何会让应用触及网络、或把任何读数发送出设备的东西。
- 任何会把一个承载身份的值泄露进被分享或保存的**导出**中的东西（序列号、IMEI、MAC 地址、广告 ID 之类），
  尽管有遮蔽。
- 任何让另一个应用得以读取那个本应只能由应用本身或 root 读取的私有报告文件的东西。

## 什么不是一个安全问题

- 一个看起来"不对"但却是某个方法返回的诚实值的读数。VD Infos 报告每个方法所说的内容；阅读这个比较是你
  要做的。参见[用户指南](USER-GUIDE.md)。
- 一个被标记为不可用的读数。那意味着某个方法在你的设备上无法读取那个值，而这本身就是比较的一部分。

## 受支持的版本

只有最新发布的版本受支持。请在报告之前在最新构建上复现。

<!-- doc-footer -->

---

> _本文档由 Claude (Anthropic) 生成，并由 VD171 维护。欢迎修正和改进；请参阅 [CONTRIBUTING.md](CONTRIBUTING.md)。_
