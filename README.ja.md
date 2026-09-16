[English](README.md) | [Português](README.pt.md) | [Español](README.es.md) | [Italiano](README.it.md) | [Deutsch](README.de.md) | [Français](README.fr.md) | [Русский](README.ru.md) | [Bahasa Indonesia](README.id.md) | [Türkçe](README.tr.md) | [Polski](README.pl.md) | [Nederlands](README.nl.md) | [Svenska](README.sv.md) | [Čeština](README.cs.md) | [Tiếng Việt](README.vi.md) | [中文](README.zh.md) | **日本語** | [한국어](README.ko.md) | [فارسی](README.fa.md) | [हिन्दी](README.hi.md) | [العربية](README.ar.md) | [ไทย](README.th.md)

# VD Infos

*メソッドデバッガー*

[![License: AGPL-3.0-or-later](https://img.shields.io/badge/license-AGPL--3.0--or--later-blue.svg)](LICENSE)
[![Release](https://img.shields.io/github/v/release/VD171/VD-Infos)](https://github.com/VD171/VD-Infos/releases)
[![Downloads](https://img.shields.io/github/downloads/VD171/VD-Infos/total)](https://github.com/VD171/VD-Infos/releases)
[![Android 8.0+](https://img.shields.io/badge/Android-8.0%2B-3DDC84.svg)](https://github.com/VD171/VD-Infos/releases)
[![Languages](https://img.shields.io/badge/languages-21-orange.svg)](README.md)

<img src="images/vdinfos-01.png" height="420"/> <img src="images/vdinfos-02.png" height="420"/>

Android は非常に強力で多機能なオペレーティングシステムですが、誰も教えてくれないのは、あなたの個人的な詳細や機密情報のすべてが、インストールするすべてのアプリから取得可能だということ、そしてこの種のプライバシー侵害から身を守ることは義務だということです。VD Infos は、あなたの端末から何が取得され得るかの一例を示し、それを*メソッドデバッガー*として行います。各情報について、値を**読み取れるあらゆる方法**で読み取ります - `Build.*`、`SystemProperties`、`getprop`、ネイティブの `__system_property_get`、システムマネージャー、content provider、ファイル、syscall、ハードウェア鍵の attestation（TEE）- そして比較できるように並べます。ある方法が他と食い違うとき、その間で何かがその表面を書き換えています。hook フレームワーク、spoofer、resolver shim です。**いかなる情報もファイルやサーバーに保存・送信・転送されません** - すべては端末上で動作し、身元を帯びる値はあなたが明かすまでマスクされ、レポートはあなたが明示的に共有または保存したときにのみ端末を離れます。必要なら、ファイアウォールでインターネットアクセスを遮断するか、単に切ってください。

## 何を検査するか

各項目は、それを読み取れるあらゆる方法（Java SDK / ネイティブ / shell）で読み取られ、並べて比較されます:

* **約 493 のシステムプロパティ**を五通りで読み取り: `SystemProperties.get`、bionic の 2 つのエントリポイント（`__system_property_read_callback` と 92 バイトの `__system_property_get`）、JVM が起動する `getprop`、ネイティブコードから `popen` 経由で起動する `getprop` - `ProcessBuilder` が実行する shell コマンドと、JVM を介さず実行する同じコマンドは同じ視点ではありません。前者は root 隠蔽フレームワークが書き換える表面であり、後者はそうではないからです。
* **端末の識別情報**: モデル、メーカー、ブランド、device、product、board、hardware、fingerprint、bootloader、build id/tags/type - 各 `Build.*` フィールドをそのすべての `ro.product.*`（system/vendor/odm）バリアント、ネイティブ、shell と照合。
* **識別子**: serial（多数のゲッター）、Android ID（settings と provider）、GSF ID、advertising ID、IMEI/primary IMEI/MEID/IMSI/ICCID、user serial。
* **テレフォニー**: TelephonyManager の表面（通信事業者、SIM、ネットワーク、ローミング…）、SubscriptionManager（マルチ SIM）、cell info。
* **ネットワーク**: インターフェースごとの **MAC**（`NetworkInterface` vs `/sys/class/net` vs shell）、Wi-Fi（SSID/BSSID/IP/MAC）、Bluetooth、DNS。
* **カーネル / プロセス / システム**: `uname`、boot id、uptime、UID/PID、SELinux、タイムゾーン、locale、環境、実行中のサービス/プロセス。
* **ハードウェア / メディア**: CPU、メモリ、センサー、ディスプレイ、カメラ、system features、GPU、Widevine DRM ID。
* **パッケージ / アカウント / WebView**: インストール済みパッケージの一覧と数、アプリごとの署名ダイジェスト、本アプリの署名、自身のパッケージの内省、intent 解決の列挙、アカウント、通話履歴件数、user agent。
* **サンドボックス適合性**: サンドボックスが**閉じる**べき表面（eMMC CID/serial、UFS と SoC の識別情報、`/proc/cmdline`、`/proc/1/*`、`/dev/kmsg`、`/system/build.prop`）と 22 個の `dumpsys` サービス。ここでは拒否こそが答えです。`EACCES`/`DENIED` が適合した読み取りであり、代わりに内容を渡す端末は不適合です。
* **広告識別子**: Play Services の binder からの GAID に加え、鍵ファミリー全体（AAID、OAID/VAID、Huawei の `pps_*`）を 3 つの settings store で走査。
* **Root / hook / エミュレーター検出**: `su`/Magisk/KernelSU/APatch/Xposed/LSPosed/Riru の痕跡、`/proc/self/maps` に注入されたライブラリ、既知のパッケージ、危険なプロパティとエミュレーターのプロパティ、verified boot state。

## ダウンロードとサポート

各リリースでは 2 つの APK を公開します。**SDK_35** は厳格な最新サンドボックスを対象とし、こちらを使ってください。**SDK_27** は古い API レベルを対象とし、SELinux ドメインが緩いため比較に役立ちます。どちらも Android 8.0 以降にインストールできます。

* Release (APK): https://github.com/VD171/VD-Infos/releases
* https://github.com/VD171/VD-Infos
* https://xdaforums.com/t/VD-Infos.4097379/
* https://t.me/RootDetected
* https://t.me/BlankAssistance

## 言語

21 の UI 言語: 英語、ポルトガル語、スペイン語、イタリア語、ドイツ語、フランス語、ロシア語、インドネシア語、トルコ語、ポーランド語、オランダ語、スウェーデン語、チェコ語、ベトナム語、中国語、日本語、韓国語、ペルシャ語、ヒンディー語、アラビア語、タイ語。アプリは初回起動時に一度だけ言語の選択を提示し（"システムの既定" オプションあり）、それ以外はシステムの言語に従います。

## アーキテクチャ

```
core/model      不変ドメイン: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          NativeBridge (JNI) - SystemProps (リフレクション) - PropCatalog (データ)
                SemanticProbes - IntegrityProbes - ProbeRegistry
engine/         ProbeEngine - コルーチンのファンアウト、結果を Flow としてストリーム
data/           SnapshotStore (永続化 + diff) - Exporter (JSON/テキスト共有)
ui/             Jetpack Compose, Material 3, ダイナミックカラー、ライブ進捗
cpp/            native_probes.cpp - ネイティブレンズ、依存なし
```

* **並列性**: 885 のプローブが、上限付きのパーミット数でデフォルトの dispatcher に展開され、結果は届くそばから UI に流れ込みます。
* **バックグラウンドでは何もしない**: サービスもスケジュールされたスキャンもなし。アプリは開いている間だけ動作し、放置されると自ら閉じます。
* **ネイティブ層**: `RegisterNatives` で名前によって束縛される小さな `.so`。騙されにくくあるべき部分なので、意図的に極小に保っています。

## コントリビュート

ほとんどの貢献に Kotlin は不要です。リストは `VDInfos/app/src/main/assets/data/` 配下のプレーンテキストです。1 行を追加または削除して pull request を開いてください。

* `*_apps.txt` - 1 行に 1 つのパッケージ名
* `props.txt` - システムプロパティのカタログ、`カテゴリ<tab>キー`
* `spoof_keys.txt` - settings の spoof マトリクス、`キー:型`
* その他の `.txt` ファイル - こちらも 1 行に 1 項目（カーネルモジュール名の断片、`/data/local/tmp` のファイル名）

`#` はコメントの開始で、空行は無視されます。コードの貢献も歓迎します。貢献することで、その成果が本プロジェクトの AGPL-3.0-or-later で配布されることに同意したものとみなされます。

## カタログ

root の隠蔽と検出に関する参照（ガイド、モジュール、フレームワーク、検出器）は専用のカタログにあります: [CATALOG.md](CATALOG.md)。

## 謝辞

**[HMA-OSS](https://github.com/frknkrc44/HMA-OSS)** の開発者 **[frknkrc44](https://github.com/frknkrc44)** に、その丹念な仕事に対して特別な感謝を。HMA-OSS は、本アプリが提案する修正のいくつかが適用される場所です - 対象アプリの隠蔽と spoof プリセットの設定 - そしてアプリ内でのその言及はすべて、そのソースへリンクします。

リンク、チャンネル、支援方法: [HMA-OSS.md](HMA-OSS.md)。

## 連絡先

* https://vd171.ru
* https://vd.priv8.ru
* **Telegram:** @VD_Priv8 https://t.me/VD_Priv8
* **Discord:** @VD.Priv8 https://discord.com/users/1296831918989639721
* **メール:** vd.priv8@pm.me
* **XDA-Developers:** @VD171 https://xdaforums.com/m/vd171.4699873/
* **GitHub:** @VD171 https://github.com/VD171

## ライセンス

**GNU AGPL-3.0-or-later** - [LICENSE](LICENSE) を参照。ネットワーク条項を含むコピーレフト: 改変版を（サービスとしてであっても）実行する者は、そのソースを提供しなければなりません。fork を開いたまま保つため、研究/反検出ツール向けに意図的に選択しました。

---

`android` `privacy` `security` `root-detection` `hook-detection` `spoof-detection` `anti-detection` `tamper-detection` `device-fingerprint` `magisk` `kernelsu` `xposed` `play-integrity` `attestation` `tee` `keystore` `scanner` `diagnostics` `reverse-engineering` `kotlin`
