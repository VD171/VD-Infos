> **Languages:** [English](../USER-GUIDE.md) · [العربية](../ar/USER-GUIDE.md) · [Čeština](../cs/USER-GUIDE.md) · [Deutsch](../de/USER-GUIDE.md) · [Español](../es/USER-GUIDE.md) · [فارسی](../fa/USER-GUIDE.md) · [Français](../fr/USER-GUIDE.md) · [हिन्दी](../hi/USER-GUIDE.md) · [Bahasa Indonesia](../id/USER-GUIDE.md) · [Italiano](../it/USER-GUIDE.md) · [日本語](../ja/USER-GUIDE.md) · [한국어](../ko/USER-GUIDE.md) · [Nederlands](../nl/USER-GUIDE.md) · [Polski](../pl/USER-GUIDE.md) · [Português](../pt/USER-GUIDE.md) · [Русский](../ru/USER-GUIDE.md) · [Svenska](../sv/USER-GUIDE.md) · [ไทย](../th/USER-GUIDE.md) · [Türkçe](../tr/USER-GUIDE.md) · [Українська](../uk/USER-GUIDE.md) · Tiếng Việt · [中文](../zh/USER-GUIDE.md)

---

# Hướng dẫn sử dụng VD Infos

VD Infos là một **trình gỡ lỗi phương thức của thiết bị**. Nó đọc mọi giá trị của thiết bị qua mọi
phương thức có thể đọc giá trị đó, và xếp các lần đọc cạnh nhau để bạn có thể so sánh. Hướng dẫn này
bao gồm việc cài đặt ứng dụng, chạy một lần quét, đọc kết quả và chia sẻ báo cáo. Nó không diễn giải
giúp bạn bất kỳ phát hiện cụ thể nào: việc đọc phần so sánh là của bạn, trên chính thiết bị của bạn.

## Mục lục

1. [VD Infos là gì](#1-vd-infos-là-gì)
2. [Nó không phải là gì](#2-nó-không-phải-là-gì)
3. [Yêu cầu](#3-yêu-cầu)
4. [Nên cài APK nào](#4-nên-cài-apk-nào)
5. [Cài đặt và chạy lần đầu](#5-cài-đặt-và-chạy-lần-đầu)
6. [Chạy quét](#6-chạy-quét)
7. [Đọc kết quả](#7-đọc-kết-quả)
8. [Xuất báo cáo của bạn](#8-xuất-báo-cáo-của-bạn)
9. [Dành cho tác nhân AI](#9-dành-cho-tác-nhân-ai)
10. [Ngôn ngữ và giao diện](#10-ngôn-ngữ-và-giao-diện)
11. [Quyền riêng tư nhìn nhanh](#11-quyền-riêng-tư-nhìn-nhanh)
12. [Câu hỏi thường gặp](#12-câu-hỏi-thường-gặp)

## 1. VD Infos là gì

Android trao rất nhiều thông tin về thiết bị của bạn cho mọi ứng dụng mà bạn cài đặt. VD Infos cho
bạn thấy một ví dụ về những gì một ứng dụng thông thường có thể đọc, và nó làm điều đó như một
*trình gỡ lỗi phương thức*: với mỗi mẩu thông tin, nó đọc giá trị qua **mọi phương thức có thể đọc
giá trị đó** - `Build.*`, `SystemProperties`, `getprop`, `__system_property_get` ở tầng native, các
trình quản lý hệ thống, content provider, tệp, syscall, và chứng thực khóa phần cứng (TEE) - rồi xếp
chúng cạnh nhau để bạn có thể so sánh. Nó là một thấu kính, không phải một cỗ máy phán xét: nó đưa
các lần đọc lên bề mặt và để việc đọc chúng lại cho bạn.

## 2. Nó không phải là gì

- Nó **không** phải là công cụ dọn dẹp, tăng tốc hay ẩn giấu. Nó không thay đổi gì trên hệ thống
  của bạn; nó chỉ đọc và so sánh.
- Nó **không** phải là điểm đạt/không đạt. Nó cho bạn thấy các lần đọc thô; chúng có ý nghĩa gì với
  thiết lập của bạn là do bạn phán xét, trên thiết bị của bạn.
- Nó **không** cần internet, và nó hoạt động đầy đủ khi internet bị chặn.

## 3. Yêu cầu

- Android 8.0 (API 26) hoặc mới hơn.
- Không cần quyền đặc biệt nào để bắt đầu. Một số lần đọc chỉ khả dụng nếu bạn cấp quyền tương ứng
  (ví dụ truy cập mức sử dụng), và một số chỉ tồn tại trên thiết bị đã root; nơi một lần đọc không
  khả dụng, ứng dụng chỉ đơn giản cho biết rằng nó không khả dụng.

## 4. Nên cài APK nào

Mỗi bản phát hành công bố **hai APK**:

- **SDK_35** - nhắm tới sandbox hiện đại nghiêm ngặt. Đây là bản nên dùng.
- **SDK_27** - nhắm tới một mức API cũ hơn, với miền SELinux lỏng hơn. Nó chỉ hữu ích cho việc so
  sánh song song; hầu hết mọi người không cần nó.

Cả hai đều cài được trên Android 8.0 trở lên. Tải chúng từ trang
[Releases](https://github.com/VD171/VD-Infos/releases).

## 5. Cài đặt và chạy lần đầu

1. Tải APK **SDK_35** từ trang Releases và cài nó (bạn có thể cần cho phép cài đặt từ trình duyệt
   hoặc trình quản lý tệp của mình).
2. Mở ứng dụng. Nó mở thẳng vào màn hình quét; không có tài khoản, không đăng nhập và không thiết lập.
3. Tùy chọn cấp một quyền mà ứng dụng hỏi tới (chẳng hạn truy cập mức sử dụng) để mở khóa các lần đọc
   phụ thuộc vào nó. Điều này hoàn toàn tùy chọn.

## 6. Chạy quét

Chạm vào hành động quét. Ứng dụng trải các đầu dò của nó ra song song và truyền kết quả vào danh sách
khi chúng đổ về, với một chỉ báo tiến độ trực tiếp. Một lần quét tự hoàn tất; nó chỉ đọc khi ứng dụng
đang mở, không giữ gì chạy ở nền, và tự đóng khi bị bỏ nhàn rỗi.

Để chạy lại, hãy quét lại: mỗi lần quét thay thế kết quả trước đó.

## 7. Đọc kết quả

Mỗi mục là một thẻ xếp thẳng hàng các lần đọc từ mọi thấu kính (phương thức Java SDK, phương thức
native, shell, v.v.). Màu của thẻ cho bạn biết, trong một cái nhìn, các lần đọc đó liên hệ với nhau
ra sao:

- **Khớp** - mọi thấu kính đều đồng ý. Các phương thức trả về cùng một giá trị.
- **Phân kỳ** - các thấu kính không đồng ý. Một phương thức trả về thứ khác với các thấu kính còn lại.
- **Thông tin** - một lần đọc được hiển thị để tham khảo, không được bỏ phiếu.
- **Đơn lẻ / không khả dụng** - chỉ một thấu kính có thể đọc nó, hoặc không thấu kính nào đọc được.

Thẻ luôn hiển thị ma trận đầy đủ các lần đọc, nên bạn có thể thấy chính xác thấu kính nào nói gì. Một
thẻ đã thu gọn hiển thị lần đọc đầu tiên của nó; mở rộng nó để thấy tất cả.

Ứng dụng nói thay bạn chỉ đến đó thôi. **Một phân kỳ nhất định có ý nghĩa gì trên thiết bị của bạn,
và phải làm gì với nó, là việc của bạn để điều tra** - đó chính là toàn bộ điểm của một trình gỡ lỗi:
nó đặt bằng chứng trước mặt bạn và để bạn đọc. Dùng ô tìm kiếm để lọc theo thuộc tính hoặc giá trị,
và các chip lọc để thu hẹp danh sách.

## 8. Xuất báo cáo của bạn

Các hành động chia sẻ và lưu trao cho bạn **toàn bộ kết quả**, không chỉ những gì trên màn hình:

- **Chia sẻ / lưu dưới dạng JSON** - báo cáo đầy đủ ở dạng máy đọc được, đúng là dữ liệu mà ứng dụng
  giữ bên trong. Đây là định dạng để giữ hoặc để đưa cho một công cụ.
- **Chia sẻ dưới dạng văn bản** - một bản tóm tắt gọn, con người đọc được, về các phân kỳ.

Các giá trị mang danh tính (số serial, IMEI, địa chỉ MAC, ID quảng cáo và tương tự) được **che trong
báo cáo xuất ra** để một báo cáo bạn dán vào một luồng công khai không rò rỉ định danh của chính bạn;
các phán quyết và cấu trúc được giữ nguyên. Bản sao trên thiết bị mà chỉ bạn (hoặc root) có thể đọc
giữ các giá trị thật.

## 9. Dành cho tác nhân AI

VD Infos được xây dựng để các công cụ đọc, không chỉ con người. Mỗi lần quét ghi báo cáo đầy đủ của
nó vào bộ nhớ riêng của ứng dụng, và bản xuất là cùng một JSON máy đọc được. Một trợ lý lập trình AI
hoặc một script phân tích chạy trên thiết bị có thể đọc toàn bộ kết quả bằng một lệnh thay vì phân
tích ảnh chụp màn hình. Xem [hướng dẫn cho tác nhân AI](AI-AGENTS.md) chuyên biệt và tài liệu tham
khảo [định dạng xuất](EXPORT-FORMAT.md).

## 10. Ngôn ngữ và giao diện

Ứng dụng đi theo ngôn ngữ hệ thống của bạn nơi có bản dịch, và bạn có thể chọn một ngôn ngữ thủ công.
Nó dùng Material 3 với màu động và đi theo thiết lập sáng/tối của bạn.

## 11. Quyền riêng tư nhìn nhanh

Mọi thứ chạy trên thiết bị. Không lần đọc nào được gửi đi đâu, và một báo cáo rời khỏi thiết bị chỉ
khi bạn chủ động chia sẻ hoặc lưu nó. Mỗi lần quét giữ báo cáo đầy đủ của nó trong bộ nhớ riêng của
ứng dụng (`files/last_snapshot.json`) để các công cụ có thể đọc nó; chỉ chính ứng dụng hoặc root có
thể đọc tệp đó, lần quét kế tiếp ghi đè lên nó, và nó không bao giờ rời khỏi thiết bị. Xem
[Quyền riêng tư](PRIVACY.md) để có tuyên bố đầy đủ.

## 12. Câu hỏi thường gặp

**Nó có thay đổi gì trên thiết bị của tôi không?** Không. Nó chỉ đọc và so sánh.

**Nó có cần root không?** Không. Ứng dụng chạy không cần root. Một nhúm lần đọc chỉ tồn tại trên thiết
bị đã root, và tệp báo cáo riêng chỉ có thể được đọc lại bởi root hoặc chính ứng dụng; mọi thứ khác
đều hoạt động khi không root.

**Nó có cần internet không?** Không. Nó hoạt động đầy đủ ngoại tuyến; chặn truy cập mạng của nó nếu
bạn muốn.

**Tôi nên dùng APK nào?** SDK_35. Bản dựng SDK_27 chỉ tồn tại để so sánh.

**Tại sao một số mục ghi là không khả dụng?** Vì phương thức đó không thể đọc giá trị đó trên thiết
bị của bạn - một quyền còn thiếu, một quy tắc sandbox, hoặc phần cứng không hiện diện. Điều đó tự nó
là một phần của phép so sánh.

**Tôi báo cáo sự cố hoặc đề xuất một mục tiêu phát hiện ở đâu?** Xem
[Đóng góp](CONTRIBUTING.md); hầu hết các danh sách phát hiện là văn bản thuần và không cần mã.

<!-- doc-footer -->

---

> _Tài liệu này được tạo bằng Claude (Anthropic) và được duy trì bởi VD171. Chúng tôi hoan nghênh các chỉnh sửa và cải tiến; xem [CONTRIBUTING.md](CONTRIBUTING.md)._
