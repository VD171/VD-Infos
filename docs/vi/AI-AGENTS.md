> **Languages:** [English](../AI-AGENTS.md) · [العربية](../ar/AI-AGENTS.md) · [Čeština](../cs/AI-AGENTS.md) · [Deutsch](../de/AI-AGENTS.md) · [Español](../es/AI-AGENTS.md) · [فارسی](../fa/AI-AGENTS.md) · [Français](../fr/AI-AGENTS.md) · [हिन्दी](../hi/AI-AGENTS.md) · [Bahasa Indonesia](../id/AI-AGENTS.md) · [Italiano](../it/AI-AGENTS.md) · [日本語](../ja/AI-AGENTS.md) · [한국어](../ko/AI-AGENTS.md) · [Nederlands](../nl/AI-AGENTS.md) · [Polski](../pl/AI-AGENTS.md) · [Português](../pt/AI-AGENTS.md) · [Русский](../ru/AI-AGENTS.md) · [Svenska](../sv/AI-AGENTS.md) · [ไทย](../th/AI-AGENTS.md) · [Türkçe](../tr/AI-AGENTS.md) · [Українська](../uk/AI-AGENTS.md) · Tiếng Việt · [中文](../zh/AI-AGENTS.md)

---

# VD Infos cho tác nhân AI

VD Infos được **xây dựng để máy đọc, không chỉ con người**. Một trình gỡ lỗi phương thức của thiết
bị tạo ra rất nhiều bằng chứng có cấu trúc - mọi giá trị, đọc qua mọi phương thức, xếp cạnh nhau để
so sánh - và bằng chứng đó hữu ích cho một tác nhân như dữ liệu hơn nhiều so với như một ảnh chụp màn
hình. Vì vậy toàn bộ kết quả có sẵn dưới dạng một tài liệu máy đọc được duy nhất, và ứng dụng được
thiết kế xoay quanh điều đó ngay từ nền móng.

Nếu bạn là một trợ lý lập trình AI, một công cụ LLM, một script phân tích hoặc bất kỳ tự động hóa nào
tiếp cận thiết bị qua một shell, trang này dành cho bạn.

## Vì sao nó thân thiện với tác nhân

- **Một tài liệu, không phải một màn hình.** Toàn bộ lần quét - mọi đầu dò, mọi thấu kính, mọi phán
  quyết - là một đối tượng JSON duy nhất. Không OCR, không cào màn hình, không chạm mở qua các thẻ đã
  thu gọn.
- **Cùng dữ liệu mà giao diện có.** Tệp không phải là một bản tóm tắt. Nó đúng là những gì ứng dụng
  giữ bên trong: màn hình hiển thị lần đọc đầu tiên của một mục đã thu gọn, tệp có tất cả chúng.
- **Hình dạng tất định.** Cấu trúc ổn định và được ghi thành tài liệu (xem
  [EXPORT-FORMAT](EXPORT-FORMAT.md)), nên một trình phân tích viết một lần cứ thế tiếp tục hoạt động.
- **Không mạng, không tài khoản, không trạng thái phải quản lý.** Ứng dụng đọc trên thiết bị và ghi
  một tệp. Tác nhân của bạn đọc tệp. Đó là toàn bộ hợp đồng.
- **Ngoại tuyến theo thiết kế.** Nó không cần internet, nên nó chạy y hệt bên trong một môi trường bị
  khóa chặt hoặc cách ly mạng mà một tác nhân có thể đang vận hành.

## Lần đọc một lệnh

Mỗi lần quét ghi báo cáo đầy đủ của nó vào bộ nhớ riêng của ứng dụng. Sau khi một lần quét đã chạy,
một tác nhân đọc kết quả đầy đủ bằng một lệnh duy nhất:

```sh
su -c cat /data/data/ru.vd171.vdinfos/files/last_snapshot.json
```

- **Nó chứa gì:** mọi đầu dò với phán quyết của nó và giá trị từ mọi thấu kính, trong cùng JSON như
  nút xuất.
- **Khi nào nó cập nhật:** vào cuối mỗi lần quét. Hãy chạy một lần quét trước; tệp luôn giữ lần quét
  mới nhất.
- **Yêu cầu:** root, vì tệp nằm trong bộ nhớ riêng của ứng dụng. Không có root, hãy dùng hành động
  trong ứng dụng **chia sẻ / lưu dưới dạng JSON** thay vào đó và đọc tệp được trao cho tác nhân.

## Một luồng tác nhân điển hình

1. Đảm bảo ứng dụng đã được cài (APK **SDK_35** từ
   [Releases](https://github.com/VD171/VD-Infos/releases)).
2. Kích hoạt một lần quét - một người chạm quét, hoặc một tự động hóa khởi chạy màn hình quét của ứng
   dụng - và chờ nó hoàn tất.
3. Đọc `files/last_snapshot.json` bằng lệnh ở trên.
4. Phân tích nó với [định dạng xuất](EXPORT-FORMAT.md) làm lược đồ của bạn: lặp qua các đầu dò, đọc
   `verdict` của từng cái, và xếp thẳng hàng các `values` theo từng thấu kính.
5. Hành động trên kết quả có cấu trúc trong quy trình làm việc của riêng bạn.

Tác nhân quyết định các lần đọc có ý nghĩa gì cho công việc trước mắt; VD Infos cung cấp bằng chứng,
sạch sẽ và trọn vẹn.

## Tác nhân nhận được gì, và không nhận được gì

- Báo cáo mang **mọi lần đọc và mọi phán quyết**, nên một tác nhân có bức tranh đầy đủ mà ứng dụng đã
  dựng.
- Các giá trị mang danh tính (số serial, IMEI, địa chỉ MAC, ID quảng cáo và tương tự) được **che
  trong báo cáo được chia sẻ/xuất ra**, nên một báo cáo được chuyền tay vẫn sạch danh định cá nhân
  trong khi giữ phán quyết và cấu trúc của nó. `last_snapshot.json` **trên thiết bị**, chỉ ứng dụng
  hoặc root đọc được, giữ các giá trị thật cho một tác nhân cục bộ cần đến chúng.

## Tài liệu, được viết để được phân tích

Wiki này được cấu trúc cho việc đọc bằng máy không kém gì cho con người: các mục ngắn, tiêu đề ổn
định, tên trường tường minh, và một trang [định dạng xuất](EXPORT-FORMAT.md) chuyên biệt mà một tác
nhân có thể coi như một lược đồ. Nếu bạn đang dựng một công cụ trên nền VD Infos, hãy bắt đầu từ đó.

## Các trang liên quan

- [Định dạng xuất](EXPORT-FORMAT.md) - lược đồ JSON, từng trường một.
- [Hướng dẫn sử dụng](USER-GUIDE.md) - cài đặt, quét và chia sẻ.
- [Kiến trúc](ARCHITECTURE.md) - ứng dụng được ghép lại ra sao.

<!-- doc-footer -->

---

> _Tài liệu này được tạo bằng Claude (Anthropic) và được duy trì bởi VD171. Chúng tôi hoan nghênh các chỉnh sửa và cải tiến; xem [CONTRIBUTING.md](CONTRIBUTING.md)._
