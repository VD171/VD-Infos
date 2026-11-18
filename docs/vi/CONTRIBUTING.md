> **Languages:** [English](../CONTRIBUTING.md) · [العربية](../ar/CONTRIBUTING.md) · [Čeština](../cs/CONTRIBUTING.md) · [Deutsch](../de/CONTRIBUTING.md) · [Español](../es/CONTRIBUTING.md) · [فارسی](../fa/CONTRIBUTING.md) · [Français](../fr/CONTRIBUTING.md) · [हिन्दी](../hi/CONTRIBUTING.md) · [Bahasa Indonesia](../id/CONTRIBUTING.md) · [Italiano](../it/CONTRIBUTING.md) · [日本語](../ja/CONTRIBUTING.md) · [한국어](../ko/CONTRIBUTING.md) · [Nederlands](../nl/CONTRIBUTING.md) · [Polski](../pl/CONTRIBUTING.md) · [Português](../pt/CONTRIBUTING.md) · [Русский](../ru/CONTRIBUTING.md) · [Svenska](../sv/CONTRIBUTING.md) · [ไทย](../th/CONTRIBUTING.md) · [Türkçe](../tr/CONTRIBUTING.md) · [Українська](../uk/CONTRIBUTING.md) · Tiếng Việt · [中文](../zh/CONTRIBUTING.md)

---

# Đóng góp

Cảm ơn bạn đã quan tâm tới VD Infos. Các đóng góp được hoan nghênh: danh sách phát hiện, bản dịch,
báo cáo lỗi, tài liệu và mã. Hầu hết các đóng góp không cần Kotlin.

## Đóng góp dễ nhất: các danh sách

Hầu hết những gì ứng dụng tìm kiếm tồn tại dưới dạng **danh sách văn bản thuần** dưới
`VDInfos/app/src/main/assets/data/`, không phải dưới dạng mã. Thêm hoặc bớt một mục tiêu là sửa một
dòng và mở một pull request:

- `*_apps.txt` - một tên gói mỗi dòng.
- `props.txt` - danh mục thuộc tính hệ thống, `CATEGORY<tab>key`.
- `spoof_keys.txt` - ma trận cài đặt, `key:TYPE`.
- `known_certs.txt` - danh mục chứng chỉ ký, `SHA-256<tab>owner` (hash đầy đủ, lấy từ một APK thật
  bằng `apksigner verify --print-certs`).
- các tệp `.txt` khác - một mục mỗi dòng (mảnh tên mô-đun kernel, tên tệp, v.v.).

Dấu `#` bắt đầu một chú thích và các dòng trống được bỏ qua. Một số tệp dồn vài trường ngăn cách bằng
khoảng trắng hoặc tab mỗi dòng, hoặc các dấu tùy chọn; hãy giữ hình dạng của các dòng đã có sẵn trong
tệp.

## Quy tắc nền tảng

- **VD Infos đọc sự thật và không bao giờ giả mạo bất cứ thứ gì.** Nó báo cáo những gì mỗi phương
  thức trả về, chính xác. Đừng làm dịu một lần đọc để nó trông đẹp hơn, và đừng thêm bất cứ thứ gì
  làm thay đổi một giá trị thay vì đọc nó.
- **Quyền riêng tư là tuyệt đối.** Mọi thứ chạy trên thiết bị. Không bao giờ thêm một lời gọi mạng,
  hay bất cứ thứ gì gửi một lần đọc ra khỏi thiết bị.
- **Các bản xuất vẫn được che.** Các giá trị mang danh tính được che trong một báo cáo được chia sẻ.
  Hãy giữ nó như vậy.

## Bản dịch

Bản dịch là một đóng góp giá trị. Readme được dịch theo từng ngôn ngữ (`README.<lang>.md`), các chuỗi
của ứng dụng theo từng ngôn ngữ, và tài liệu này dưới `docs/<lang>/`. Hãy giữ thuật ngữ nhất quán
trong một ngôn ngữ, khớp với giọng điệu hiện có, và không bao giờ dịch máy một thuật ngữ kỹ thuật mà
bạn không chắc chắn.

## Báo cáo một lỗi

Mở một issue với kiểu máy thiết bị của bạn, phiên bản Android, và, nếu nó về một lần đọc, một **bản
xuất** được chia sẻ (các giá trị mang danh tính của nó được che). Bản xuất được dựng cho đúng việc
này.

## Mã

- Dựng release, không bao giờ debug. Kiểm thử trên một thiết bị thật. Xem [Dựng](BUILDING.md).
- Giữ chú thích ra khỏi mã; tên giải thích ý định. Tiêu đề duy nhất là banner giấy phép ở đầu mỗi tệp
  nguồn.
- Khớp với phong cách hiện có và giữ các thay đổi tập trung.

## Giấy phép

Bằng việc đóng góp, bạn đồng ý rằng đóng góp của bạn được cấp phép theo
**GNU AGPL-3.0-or-later** của dự án.

<!-- doc-footer -->

---

> _Tài liệu này được tạo bằng Claude (Anthropic) và được duy trì bởi VD171. Chúng tôi hoan nghênh các chỉnh sửa và cải tiến; xem [CONTRIBUTING.md](CONTRIBUTING.md)._
