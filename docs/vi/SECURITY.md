> **Languages:** [English](../SECURITY.md) · [العربية](../ar/SECURITY.md) · [Čeština](../cs/SECURITY.md) · [Deutsch](../de/SECURITY.md) · [Español](../es/SECURITY.md) · [فارسی](../fa/SECURITY.md) · [Français](../fr/SECURITY.md) · [हिन्दी](../hi/SECURITY.md) · [Bahasa Indonesia](../id/SECURITY.md) · [Italiano](../it/SECURITY.md) · [日本語](../ja/SECURITY.md) · [한국어](../ko/SECURITY.md) · [Nederlands](../nl/SECURITY.md) · [Polski](../pl/SECURITY.md) · [Português](../pt/SECURITY.md) · [Русский](../ru/SECURITY.md) · [Svenska](../sv/SECURITY.md) · [ไทย](../th/SECURITY.md) · [Türkçe](../tr/SECURITY.md) · [Українська](../uk/SECURITY.md) · Tiếng Việt · [中文](../zh/SECURITY.md)

---

# Chính sách bảo mật

VD Infos là một công cụ về quyền riêng tư đọc trạng thái nhạy cảm của thiết bị, nên các báo cáo bảo
mật được xem xét nghiêm túc.

## Báo cáo một lỗ hổng

Vui lòng báo cáo các vấn đề bảo mật một cách **riêng tư**, không phải trong một issue công khai:

- Email **vd.priv8@pm.me**.
- Mô tả vấn đề, phiên bản bị ảnh hưởng, và cách tái hiện nó.
- Nếu nó liên quan tới việc lộ dữ liệu, hãy mô tả lớp vấn đề; bạn không cần đính kèm một exploit hoạt
  động được.

Bạn sẽ nhận được phản hồi sớm nhất có thể. Vui lòng cho một khoảng thời gian hợp lý để khắc phục vấn
đề trước bất kỳ công bố công khai nào.

## Điều gì được tính là một vấn đề bảo mật

Vì những gì VD Infos hứa hẹn, những điều này đặc biệt quan trọng:

- Bất cứ điều gì khiến ứng dụng tiếp cận mạng, hoặc gửi bất kỳ lần đọc nào ra khỏi thiết bị.
- Bất cứ điều gì làm rò rỉ một giá trị mang danh tính vào một **bản xuất** được chia sẻ hoặc lưu (một
  số serial, IMEI, địa chỉ MAC, ID quảng cáo và tương tự) bất chấp việc che.
- Bất cứ điều gì cho phép một ứng dụng khác đọc tệp báo cáo riêng vốn chỉ được định là đọc được bởi
  chính ứng dụng hoặc root.

## Điều gì không phải là một vấn đề bảo mật

- Một lần đọc trông có vẻ "sai" nhưng là giá trị trung thực mà một phương thức trả về. VD Infos báo
  cáo những gì mỗi phương thức nói; việc đọc phép so sánh là của bạn. Xem
  [hướng dẫn sử dụng](USER-GUIDE.md).
- Một lần đọc được đánh dấu là không khả dụng. Điều đó nghĩa là một phương thức không thể đọc giá trị
  đó trên thiết bị của bạn, mà tự nó là một phần của phép so sánh.

## Các phiên bản được hỗ trợ

Chỉ phiên bản phát hành mới nhất được hỗ trợ. Vui lòng tái hiện trên bản dựng mới nhất trước khi báo
cáo.

<!-- doc-footer -->

---

> _Tài liệu này được tạo bằng Claude (Anthropic) và được duy trì bởi VD171. Chúng tôi hoan nghênh các chỉnh sửa và cải tiến; xem [CONTRIBUTING.md](CONTRIBUTING.md)._
