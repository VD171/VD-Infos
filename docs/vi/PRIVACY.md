> **Languages:** [English](../PRIVACY.md) · [العربية](../ar/PRIVACY.md) · [Čeština](../cs/PRIVACY.md) · [Deutsch](../de/PRIVACY.md) · [Español](../es/PRIVACY.md) · [فارسی](../fa/PRIVACY.md) · [Français](../fr/PRIVACY.md) · [हिन्दी](../hi/PRIVACY.md) · [Bahasa Indonesia](../id/PRIVACY.md) · [Italiano](../it/PRIVACY.md) · [日本語](../ja/PRIVACY.md) · [한국어](../ko/PRIVACY.md) · [Nederlands](../nl/PRIVACY.md) · [Polski](../pl/PRIVACY.md) · [Português](../pt/PRIVACY.md) · [Русский](../ru/PRIVACY.md) · [Svenska](../sv/PRIVACY.md) · [ไทย](../th/PRIVACY.md) · [Türkçe](../tr/PRIVACY.md) · [Українська](../uk/PRIVACY.md) · Tiếng Việt · [中文](../zh/PRIVACY.md)

---

# Quyền riêng tư

Quyền riêng tư không phải là một tính năng của VD Infos. Nó là toàn bộ điểm cốt lõi. Trang này nêu
chính xác những gì ứng dụng làm và không làm với dữ liệu của bạn, bằng những lời rõ ràng mà bạn có
thể kiểm chứng.

## Lời hứa

- **Mọi thứ chạy trên thiết bị.** Không lần đọc nào được gửi, tải lên hay truyền tới bất kỳ máy chủ
  nào. Ứng dụng hoạt động đầy đủ khi internet bị chặn.
- **Không tài khoản, không đăng nhập, không công việc ở nền.** Ứng dụng chỉ đọc khi nó đang mở, không
  giữ gì chạy ở nền, và tự đóng khi bị bỏ nhàn rỗi.
- **Không có gì thay đổi trên hệ thống của bạn.** VD Infos chỉ đọc và so sánh; nó không bao giờ ghi
  vào trạng thái của thiết bị bạn.

## Báo cáo ở lại với bạn

Một báo cáo rời khỏi thiết bị **chỉ khi bạn chủ động chia sẻ hoặc lưu nó**. Cho tới lúc đó nó tồn tại
ở hai nơi, cả hai đều trên thiết bị:

- **Trên màn hình**, khi ứng dụng đang mở.
- Trong **bộ nhớ riêng** của ứng dụng (`files/last_snapshot.json`), mà chỉ chính ứng dụng hoặc root
  có thể đọc. Lần quét kế tiếp ghi đè lên nó, và nó không bao giờ rời khỏi thiết bị. Nó tồn tại để
  các công cụ và tác nhân AI có thể đọc kết quả đầy đủ; xem
  [hướng dẫn cho tác nhân AI](AI-AGENTS.md).

## Các bản xuất được che

Khi bạn chia sẻ hoặc lưu một báo cáo, các giá trị mang danh tính - số serial, IMEI, địa chỉ MAC, ID
quảng cáo và tương tự - được **che** trước khi báo cáo được ghi, nên một báo cáo bạn dán vào một
luồng công khai không rò rỉ định danh của chính bạn. Các phán quyết và cấu trúc được giữ nguyên; chỉ
các giá trị nhạy cảm bị lược bỏ. Bản sao riêng trên thiết bị giữ các giá trị thật cho việc sử dụng
của riêng bạn.

## Quyền

Ứng dụng khởi đầu mà không cần quyền nào. Một số lần đọc chỉ khả dụng nếu bạn cấp quyền tương ứng
(ví dụ truy cập mức sử dụng), và việc cấp nó hoàn toàn là lựa chọn của bạn; nơi một lần đọc không khả
dụng, ứng dụng chỉ đơn giản nói vậy.

## Tự kiểm chứng

Bạn không phải tin bất cứ điều nào trong số này một cách mù quáng:

- Mã nguồn công khai: <https://github.com/VD171/VD-Infos>.
- Manifest nằm trong mã nguồn; hãy xác nhận nó khai báo những quyền nào.
- Chặn truy cập mạng của ứng dụng bằng một tường lửa, hoặc tắt internet, và nó vẫn cứ hoạt động y hệt.

Xem [hướng dẫn sử dụng](USER-GUIDE.md) để biết cách đọc một báo cáo và bản xuất chứa những gì.

<!-- doc-footer -->

---

> _Tài liệu này được tạo bằng Claude (Anthropic) và được duy trì bởi VD171. Chúng tôi hoan nghênh các chỉnh sửa và cải tiến; xem [CONTRIBUTING.md](CONTRIBUTING.md)._
