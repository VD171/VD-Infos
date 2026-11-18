> **Languages:** [English](../BUILDING.md) · [العربية](../ar/BUILDING.md) · [Čeština](../cs/BUILDING.md) · [Deutsch](../de/BUILDING.md) · [Español](../es/BUILDING.md) · [فارسی](../fa/BUILDING.md) · [Français](../fr/BUILDING.md) · [हिन्दी](../hi/BUILDING.md) · [Bahasa Indonesia](../id/BUILDING.md) · [Italiano](../it/BUILDING.md) · [日本語](../ja/BUILDING.md) · [한국어](../ko/BUILDING.md) · [Nederlands](../nl/BUILDING.md) · [Polski](../pl/BUILDING.md) · [Português](../pt/BUILDING.md) · [Русский](../ru/BUILDING.md) · [Svenska](../sv/BUILDING.md) · [ไทย](../th/BUILDING.md) · [Türkçe](../tr/BUILDING.md) · [Українська](../uk/BUILDING.md) · Tiếng Việt · [中文](../zh/BUILDING.md)

---

# Dựng VD Infos

VD Infos là một dự án Android Gradle tiêu chuẩn: Kotlin và Jetpack Compose, với một thấu kính native
nhỏ bằng C++ được dựng qua NDK. CI có thể dựng nó, và bạn có thể tái hiện nó cục bộ.

## Yêu cầu

- Một JDK (17 tới 21) và Android SDK.
- Android NDK và CMake, cho thấu kính native (plugin Gradle Android lấy một CMake tương ứng nếu bạn
  có thành phần `cmake` của SDK).
- `minSdk` 26, nên các bản dựng chạy trên Android 8.0 và mới hơn.

## Khởi động nhanh

```sh
# build both flavors, release
./gradlew assembleRelease
```

Điều này tạo ra **hai APK**, mỗi flavor một cái:

- **modern** (`SDK_35`) - sandbox hiện đại nghiêm ngặt, bản dựng nên dùng.
- **compat** (`SDK_27`) - một mục tiêu API cũ hơn, chỉ để so sánh song song.

Các đầu ra nằm ở:

```
app/build/outputs/apk/modern/release/VDInfos-v<ver>-SDK_35-release.apk
app/build/outputs/apk/compat/release/VDInfos-v<ver>-SDK_27-release.apk
```

Để chỉ dựng một flavor trong khi lặp:

```sh
./gradlew assembleModernRelease   # SDK_35 only
./gradlew assembleCompatRelease   # SDK_27 only
```

## Ký

Các bản dựng release được ký bằng một khóa **cục bộ và không được commit** (keystore và các thuộc
tính của nó bị git bỏ qua). Không có nó, tác vụ release vẫn dựng nhưng APK ra đời không được ký. Hãy
cung cấp keystore của riêng bạn để ký các bản dựng của riêng bạn.

## Hai flavor

Ứng dụng xuất xưởng với hai flavor một cách có chủ đích. Chúng nhắm tới các mức API khác nhau và
dựng từ cùng một mã nguồn; chỉ mục tiêu và vài đường mã được bảo vệ là khác. Một thay đổi chạm tới mã
native hoặc một đường đặc thù theo flavor nên được dựng và kiểm tra trên **cả hai** flavor, không chỉ
cái bạn đang lặp.

## Thấu kính native

Thấu kính native là một đơn vị dịch C++ duy nhất, không có phụ thuộc, được NDK dựng thành một `.so`
nhỏ, được buộc theo tên qua `RegisterNatives`. Nó được biên dịch như một phần của bản dựng Gradle
thông thường; không có bước riêng. Nó được giữ cố ý bé nhỏ và đơn giản.

## Các thứ nằm ở đâu

```
app/src/main/java/ru/vd171/vdinfos/   Kotlin: model, probes, engine, data, ui
app/src/main/cpp/                     the native lens
app/src/main/assets/data/             the plain-text detection lists (see Contributing)
```

Xem [Kiến trúc](ARCHITECTURE.md) để biết các mảnh ghép khớp với nhau ra sao, và
[Đóng góp](CONTRIBUTING.md) để biết cách thêm một mục tiêu phát hiện mà không chạm tới mã.

<!-- doc-footer -->

---

> _Tài liệu này được tạo bằng Claude (Anthropic) và được duy trì bởi VD171. Chúng tôi hoan nghênh các chỉnh sửa và cải tiến; xem [CONTRIBUTING.md](CONTRIBUTING.md)._
