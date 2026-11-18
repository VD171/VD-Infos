> **Languages:** [English](../EXPORT-FORMAT.md) · [العربية](../ar/EXPORT-FORMAT.md) · [Čeština](../cs/EXPORT-FORMAT.md) · [Deutsch](../de/EXPORT-FORMAT.md) · [Español](../es/EXPORT-FORMAT.md) · [فارسی](../fa/EXPORT-FORMAT.md) · [Français](../fr/EXPORT-FORMAT.md) · [हिन्दी](../hi/EXPORT-FORMAT.md) · [Bahasa Indonesia](../id/EXPORT-FORMAT.md) · [Italiano](../it/EXPORT-FORMAT.md) · [日本語](../ja/EXPORT-FORMAT.md) · [한국어](../ko/EXPORT-FORMAT.md) · [Nederlands](../nl/EXPORT-FORMAT.md) · [Polski](../pl/EXPORT-FORMAT.md) · [Português](../pt/EXPORT-FORMAT.md) · [Русский](../ru/EXPORT-FORMAT.md) · [Svenska](../sv/EXPORT-FORMAT.md) · [ไทย](../th/EXPORT-FORMAT.md) · [Türkçe](../tr/EXPORT-FORMAT.md) · [Українська](../uk/EXPORT-FORMAT.md) · Tiếng Việt · [中文](../zh/EXPORT-FORMAT.md)

---

# Định dạng xuất

Báo cáo mà VD Infos xuất ra - và `last_snapshot.json` mà nó ghi sau mỗi lần quét - là một đối tượng
JSON với hình dạng ổn định, được ghi thành tài liệu. Trang này là lược đồ. Nó mô tả cấu trúc để một
công cụ có thể phân tích; nó không liệt kê từng đầu dò cũng không diễn giải giúp bạn bất kỳ lần đọc
nào.

## Cấp cao nhất: ảnh chụp

```json
{
  "takenAt": 1730000000000,
  "appVersion": "2.23",
  "results": [ /* ProbeResult objects */ ]
}
```

| Trường | Kiểu | Ý nghĩa |
|---|---|---|
| `takenAt` | number | Thời điểm lần quét được thực hiện, Unix epoch tính bằng mili giây. |
| `appVersion` | string | Phiên bản ứng dụng đã tạo ra báo cáo. |
| `results` | array | Mỗi đầu dò một `ProbeResult`, theo thứ tự mà ứng dụng giữ chúng. |

## Một kết quả

Mỗi mục trong `results` là một đầu dò với phán quyết của nó và mọi lần đọc của từng thấu kính.

```json
{
  "spec": { /* ProbeSpec */ },
  "values": [ /* LensValue objects */ ],
  "verdict": "MATCH",
  "detail": null
}
```

| Trường | Kiểu | Ý nghĩa |
|---|---|---|
| `spec` | object | Đã dò gì (xem bên dưới). |
| `values` | array | Mỗi phương thức đọc nó một `LensValue`. |
| `verdict` | string | Các lần đọc liên hệ ra sao (xem [Verdict](#verdict)). |
| `detail` | string or null | Một ghi chú tùy chọn về kết quả. |

### spec (ProbeSpec)

```json
{
  "id": "identity:model",
  "title": "Model",
  "category": "IDENTITY",
  "lenses": ["JAVA", "NATIVE", "SHELL"],
  "note": null,
  "sensitive": false,
  "solution": null
}
```

| Trường | Kiểu | Ý nghĩa |
|---|---|---|
| `id` | string | Định danh ổn định của đầu dò. |
| `title` | string | Tên con người đọc được. |
| `category` | string | Một trong các [danh mục](#category). |
| `lenses` | array | Các thấu kính mà đầu dò này dùng. |
| `note` | string or null | Ngữ cảnh tùy chọn. |
| `sensitive` | boolean | `true` khi giá trị mang danh tính; những giá trị như vậy được **che trong báo cáo xuất ra** (xem [Che giấu](#che-giấu)). |
| `solution` | string or null | Một trường gợi ý tùy chọn; có thể vắng mặt hoặc null. |

### values (LensValue)

```json
{
  "lens": "JAVA",
  "source": "Build.MODEL",
  "value": "Pixel 9",
  "error": null,
  "elapsedMicros": 42,
  "compare": true,
  "detail": null
}
```

| Trường | Kiểu | Ý nghĩa |
|---|---|---|
| `lens` | string | Phương thức nào đã đọc nó (xem [Lens](#lens)). |
| `source` | string | Lời gọi hoặc đường dẫn chính xác đằng sau lần đọc này. |
| `value` | string or null | Phương thức đó trả về gì; `null` nếu nó không đọc được gì. |
| `error` | string or null | Có mặt khi phương thức thất bại. |
| `elapsedMicros` | number | Lần đọc mất bao lâu, tính bằng micro giây. |
| `compare` | boolean | `true` nếu lần đọc này tham gia vào phán quyết; `false` nếu nó chỉ được hiển thị để tham khảo. |
| `detail` | string or null | Một ghi chú tùy chọn về lần đọc này. |

## Các kiểu liệt kê

### Verdict

| Giá trị | Ý nghĩa |
|---|---|
| `MATCH` | Mọi thấu kính có thể so sánh đều đồng ý. |
| `MISMATCH` | Các thấu kính có thể so sánh không đồng ý. |
| `SINGLE` | Chỉ một thấu kính có thể đọc nó. |
| `INFO` | Đọc để tham khảo, không được bỏ phiếu. |
| `EMPTY` | Không thấu kính nào đọc được một giá trị. |
| `ERROR` | Không có giá trị, và ít nhất một thấu kính báo lỗi. |

### Lens

| Giá trị | Nhãn |
|---|---|
| `JAVA` | Java / SDK |
| `NATIVE` | Native / JNI |
| `SHELL` | Shell via JVM |
| `SHELL_NATIVE` | Shell via JNI |
| `ATTEST` | TEE / Attestation |

### Category

`IDENTITY`, `FINGERPRINT`, `TELEPHONY`, `NETWORK`, `LOCALE`, `BOOT`, `BUILD`,
`HARDWARE`, `DISPLAY`, `SENSORS`, `MEDIA`, `STORAGE`, `PROCESS`, `PACKAGES`,
`ACCOUNTS`, `WEBVIEW`, `SECURITY`, `INTEGRITY`, `EMULATOR`, `SYSTEM`.

## Che giấu

Khi `spec.sensitive` là `true`, các lần đọc của đầu dò mang một giá trị mang danh tính (một số
serial, IMEI, địa chỉ MAC, ID quảng cáo và tương tự). Trong báo cáo được **chia sẻ hoặc xuất ra**,
những giá trị đó, cùng các chi tiết của chúng, được thay bằng một dấu lược bỏ để một báo cáo dán vào
nơi công khai không rò rỉ định danh cá nhân; đầu dò, phán quyết của nó và cấu trúc được giữ nguyên.
`last_snapshot.json` **trên thiết bị**, chỉ ứng dụng hoặc root đọc được, giữ các giá trị thật.

## Đọc nó

Một phép phân tích tối thiểu: lặp qua `results`, đọc từng `verdict`, và để có bức tranh đầy đủ hãy
xếp thẳng hàng `values` theo `lens` của chúng. Hình dạng ở trên ổn định qua các bản phát hành; hãy
phân tích dựa theo các tên trường này thay vì vị trí trên màn hình.

Xem [Tác nhân AI](AI-AGENTS.md) để biết cách lấy tệp trên thiết bị.

<!-- doc-footer -->

---

> _Tài liệu này được tạo bằng Claude (Anthropic) và được duy trì bởi VD171. Chúng tôi hoan nghênh các chỉnh sửa và cải tiến; xem [CONTRIBUTING.md](CONTRIBUTING.md)._
