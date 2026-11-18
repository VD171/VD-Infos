> **Languages:** [English](../SECURITY.md) · [العربية](../ar/SECURITY.md) · [Čeština](../cs/SECURITY.md) · [Deutsch](../de/SECURITY.md) · [Español](../es/SECURITY.md) · [فارسی](../fa/SECURITY.md) · [Français](../fr/SECURITY.md) · [हिन्दी](../hi/SECURITY.md) · [Bahasa Indonesia](../id/SECURITY.md) · [Italiano](../it/SECURITY.md) · 日本語 · [한국어](../ko/SECURITY.md) · [Nederlands](../nl/SECURITY.md) · [Polski](../pl/SECURITY.md) · [Português](../pt/SECURITY.md) · [Русский](../ru/SECURITY.md) · [Svenska](../sv/SECURITY.md) · [ไทย](../th/SECURITY.md) · [Türkçe](../tr/SECURITY.md) · [Українська](../uk/SECURITY.md) · [Tiếng Việt](../vi/SECURITY.md) · [中文](../zh/SECURITY.md)

---

# セキュリティポリシー

VD Infos は機微なデバイスの状態を読み取るプライバシーツールなので、セキュリティの報告は真剣に
受け止められます。

## 脆弱性の報告

セキュリティの問題は、公開の issue ではなく **非公開で** 報告してください。

- **vd.priv8@pm.me** にメールしてください。
- 問題、影響を受けるバージョン、そして再現方法を説明してください。
- データの露出に関わる場合は、問題の種類を説明してください。動作する exploit を添付する必要は
  ありません。

できるだけ早く返信します。公開する前に、問題を修正するための妥当な期間を設けてください。

## 何がセキュリティ問題にあたるか

VD Infos が約束していることゆえに、以下は特に重要です。

- アプリがネットワークに到達したり、いずれかの読み取りをデバイス外へ送信したりするようにする
  もの。
- マスキングにもかかわらず、ID を含む値 (シリアル、IMEI、MAC アドレス、広告 ID など) を、
  共有または保存された **エクスポート** へ漏らすもの。
- アプリ自身または root だけが読めるはずのプライベートなレポートファイルを、別のアプリに読ませる
  もの。

## 何がセキュリティ問題にあたらないか

- 「間違って」見えるが、あるメソッドが返した正直な値である読み取り。VD Infos は各メソッドが
  言ったことを報告します。比較を読むのはあなたが行うことです。[ユーザーガイド](USER-GUIDE.md)
  を参照してください。
- 利用不可とマークされた読み取り。それは、あるメソッドがあなたのデバイスでその値を読めなかった
  ことを意味し、それ自体が比較の一部です。

## サポートされるバージョン

最新のリリース版のみがサポートされます。報告する前に最新のビルドで再現してください。

<!-- doc-footer -->

---

> _このドキュメントは Claude (Anthropic) で生成され、VD171 によって保守されています。修正や改善を歓迎します。[CONTRIBUTING.md](CONTRIBUTING.md) を参照してください。_
