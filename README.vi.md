[English](README.md) | [Português](README.pt.md) | [Español](README.es.md) | [Italiano](README.it.md) | [Deutsch](README.de.md) | [Français](README.fr.md) | [Русский](README.ru.md) | [Bahasa Indonesia](README.id.md) | [Türkçe](README.tr.md) | [Polski](README.pl.md) | [Nederlands](README.nl.md) | [Svenska](README.sv.md) | [Čeština](README.cs.md) | **Tiếng Việt** | [中文](README.zh.md) | [日本語](README.ja.md) | [한국어](README.ko.md) | [فارسی](README.fa.md) | [हिन्दी](README.hi.md) | [العربية](README.ar.md) | [ไทย](README.th.md)

# VD Infos

*Trình gỡ lỗi phương thức*

[![License: AGPL-3.0-or-later](https://img.shields.io/badge/license-AGPL--3.0--or--later-blue.svg)](LICENSE)
[![Release](https://img.shields.io/github/v/release/VD171/VD-Infos)](https://github.com/VD171/VD-Infos/releases)
[![Downloads](https://img.shields.io/github/downloads/VD171/VD-Infos/total)](https://github.com/VD171/VD-Infos/releases)
[![Android 8.0+](https://img.shields.io/badge/Android-8.0%2B-3DDC84.svg)](https://github.com/VD171/VD-Infos/releases)
[![Languages](https://img.shields.io/badge/languages-21-orange.svg)](README.md)

<img src="images/vdinfos-01.png" height="420"/> <img src="images/vdinfos-02.png" height="420"/>

Android là một hệ điều hành cực kỳ mạnh mẽ và linh hoạt; điều không ai nói cho bạn biết là mọi chi tiết cá nhân và thông tin bí mật của bạn đều sẵn có cho mọi ứng dụng bạn cài đặt, và tự bảo vệ trước những sự xâm phạm quyền riêng tư này là một nghĩa vụ. VD Infos cho bạn thấy một ví dụ về những gì có thể bị thu thập từ thiết bị của bạn, và làm điều đó như một *trình gỡ lỗi phương thức*: với mỗi thông tin, nó đọc giá trị qua **mọi phương thức có thể đọc được nó** - `Build.*`, `SystemProperties`, `getprop`, `__system_property_get` native, các manager hệ thống, content provider, tệp, syscall và attestation khóa phần cứng (TEE) - rồi xếp cạnh nhau để bạn so sánh. Khi một phương thức khác với các phương thức còn lại, thứ gì đó ở giữa đang viết lại bề mặt đó: một framework hook, một spoofer, một shim resolver. **KHÔNG THÔNG TIN NÀO ĐƯỢC LƯU, GỬI HAY TRUYỀN ĐẾN BẤT KỲ TỆP HAY MÁY CHỦ NÀO** - mọi thứ chạy trên thiết bị, các giá trị mang danh tính bị che cho đến khi bạn hiển thị chúng, và báo cáo chỉ rời khỏi thiết bị khi bạn chia sẻ hoặc lưu một cách rõ ràng; nếu muốn, hãy chặn truy cập internet bằng tường lửa hoặc chỉ cần tắt nó đi.

## Nó kiểm tra gì

Mỗi mục được đọc qua mọi phương thức có thể đọc được nó (Java SDK / native / shell), so sánh cạnh nhau:

* **~493 thuộc tính hệ thống** được đọc theo NĂM cách: `SystemProperties.get`, cả hai điểm vào của bionic (`__system_property_read_callback` và `__system_property_get` 92 byte), `getprop` do JVM khởi chạy, và `getprop` khởi chạy từ mã native qua `popen` - một lệnh shell chạy bởi `ProcessBuilder` và cùng lệnh đó chạy không có JVM không phải cùng một góc nhìn, vì cái đầu là một bề mặt mà framework ẩn root viết lại, còn cái sau thì không.
* **Danh tính thiết bị**: model, nhà sản xuất, thương hiệu, device, product, board, hardware, fingerprint, bootloader, build id/tags/type - mỗi trường `Build.*` đối chiếu với tất cả biến thể `ro.product.*` (system/vendor/odm), native và shell.
* **Định danh**: serial (nhiều getter), Android ID (settings và provider), GSF ID, advertising ID, IMEI/primary IMEI/MEID/IMSI/ICCID, user serial.
* **Điện thoại**: bề mặt TelephonyManager (nhà mạng, SIM, mạng, roaming...), SubscriptionManager (đa SIM), cell info.
* **Mạng**: **MAC** theo giao diện (`NetworkInterface` vs `/sys/class/net` vs shell), Wi-Fi (SSID/BSSID/IP/MAC), Bluetooth, DNS.
* **Kernel / tiến trình / hệ thống**: `uname`, boot id, uptime, UID/PID, SELinux, múi giờ, locale, môi trường, dịch vụ/tiến trình đang chạy.
* **Phần cứng / đa phương tiện**: CPU, bộ nhớ, cảm biến, màn hình, camera, system features, GPU, ID DRM Widevine.
* **Gói / tài khoản / WebView**: danh sách và số lượng gói đã cài, digest chữ ký theo ứng dụng, chữ ký của ứng dụng này, tự soi gói của chính mình, liệt kê phân giải intent, tài khoản, số mục nhật ký cuộc gọi, user agent.
* **Tuân thủ sandbox**: các bề mặt mà sandbox phải ĐÓNG (CID/serial eMMC, danh tính UFS và SoC, `/proc/cmdline`, `/proc/1/*`, `/dev/kmsg`, `/system/build.prop`) và 22 dịch vụ `dumpsys`. Ở đây, sự từ chối chính là câu trả lời: `EACCES`/`DENIED` là kết quả đọc tuân thủ, và một thiết bị thay vào đó lại trao nội dung là không tuân thủ.
* **Định danh quảng cáo**: GAID từ binder của Play Services, cộng với cả họ khóa (AAID, OAID/VAID và `pps_*` của Huawei) được quét qua ba settings store.
* **Trình phát hiện root / hook / giả lập**: dấu vết của `su`/Magisk/KernelSU/APatch/Xposed/LSPosed/Riru, thư viện được tiêm trong `/proc/self/maps`, gói đã biết, thuộc tính nguy hiểm và giả lập, verified boot state.

## Tải xuống và hỗ trợ

Mỗi bản phát hành công bố hai APK: **SDK_35** nhắm tới sandbox hiện đại nghiêm ngặt và là bản nên dùng; **SDK_27** nhắm tới mức API cũ hơn, với miền SELinux lỏng hơn, hữu ích để so sánh. Cả hai cài được trên Android 8.0 trở lên.

* Release (APK): https://github.com/VD171/VD-Infos/releases
* https://github.com/VD171/VD-Infos
* https://xdaforums.com/t/VD-Infos.4097379/
* https://t.me/RootDetected
* https://t.me/BlankAssistance

## Ngôn ngữ

21 ngôn ngữ giao diện: Anh, Bồ Đào Nha, Tây Ban Nha, Ý, Đức, Pháp, Nga, Indonesia, Thổ Nhĩ Kỳ, Ba Lan, Hà Lan, Thụy Điển, Séc, Việt, Trung, Nhật, Hàn, Ba Tư, Hindi, Ả Rập và Thái. Ứng dụng đưa ra lựa chọn ngôn ngữ một lần khi khởi chạy lần đầu (với tùy chọn "Mặc định hệ thống") và ngoài ra tuân theo ngôn ngữ hệ thống.

## Kiến trúc

```
core/model      miền bất biến: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          NativeBridge (JNI) - SystemProps (phản chiếu) - PropCatalog (dữ liệu)
                SemanticProbes - IntegrityProbes - ProbeRegistry
engine/         ProbeEngine - fan-out coroutine, truyền kết quả dưới dạng Flow
data/           SnapshotStore (lưu bền + diff) - Exporter (chia sẻ JSON/văn bản)
ui/             Jetpack Compose, Material 3, màu động, tiến trình trực tiếp
cpp/            native_probes.cpp - ống kính native, không phụ thuộc
```

* **Song song**: 885 thăm dò tỏa ra trên dispatcher mặc định với số giấy phép giới hạn; kết quả chảy vào UI ngay khi đến.
* **Không có gì ở nền**: không dịch vụ và không quét theo lịch; ứng dụng chỉ chạy khi đang mở và tự đóng khi bị bỏ không.
* **Lớp native**: một `.so` nhỏ, liên kết theo tên qua `RegisterNatives`, được giữ cố ý tí hon vì đây là phần phải khó bị đánh lừa.

## Đóng góp

Hầu hết đóng góp không cần Kotlin: các danh sách là văn bản thuần trong `VDInfos/app/src/main/assets/data/`. Thêm hoặc bớt một dòng rồi mở pull request.

* `*_apps.txt` - mỗi dòng một tên gói
* `props.txt` - danh mục thuộc tính hệ thống, `DANH_MỤC<tab>khóa`
* `spoof_keys.txt` - ma trận spoof settings, `khóa:KIỂU`
* các tệp `.txt` khác - cũng một mục mỗi dòng (mảnh tên mô-đun nhân, tên trong `/data/local/tmp`)

Dấu `#` bắt đầu một chú thích; dòng trống bị bỏ qua. Đóng góp mã nguồn cũng được hoan nghênh. Khi đóng góp, bạn đồng ý rằng công việc của mình được phát hành theo AGPL-3.0-or-later của dự án này.

## Danh mục

Các tham chiếu về ẩn và phát hiện root (hướng dẫn, mô-đun, framework, trình phát hiện) nằm trong một danh mục riêng: [CATALOG.md](CATALOG.md).

## Lời cảm ơn

Cảm ơn đặc biệt tới **[frknkrc44](https://github.com/frknkrc44)**, nhà phát triển **[HMA-OSS](https://github.com/frknkrc44/HMA-OSS)**, vì sự tận tâm dành cho nó. HMA-OSS là nơi áp dụng nhiều bản sửa mà ứng dụng này gợi ý - ẩn ứng dụng mục tiêu và đặt preset spoof - và mọi lần nhắc đến nó trong ứng dụng đều liên kết ngược về nguồn.

Các liên kết, kênh và cách ủng hộ: [HMA-OSS.md](HMA-OSS.md).

## Liên hệ

* https://vd171.ru
* https://vd.priv8.ru
* **Telegram:** @VD_Priv8 https://t.me/VD_Priv8
* **Discord:** @VD.Priv8 https://discord.com/users/1296831918989639721
* **E-mail:** vd.priv8@pm.me
* **XDA-Developers:** @VD171 https://xdaforums.com/m/vd171.4699873/
* **GitHub:** @VD171 https://github.com/VD171

## Giấy phép

**GNU AGPL-3.0-or-later** - xem [LICENSE](LICENSE). Copyleft, bao gồm điều khoản mạng: bất kỳ ai chạy một phiên bản đã sửa đổi (kể cả dưới dạng dịch vụ) phải cung cấp mã nguồn của nó. Được chọn có chủ đích cho một công cụ nghiên cứu/chống phát hiện, để giữ cho các fork luôn mở.

---

`android` `privacy` `security` `root-detection` `hook-detection` `spoof-detection` `anti-detection` `tamper-detection` `device-fingerprint` `magisk` `kernelsu` `lsposed` `play-integrity` `attestation` `tee` `keystore` `scanner` `diagnostics` `reverse-engineering` `kotlin`
