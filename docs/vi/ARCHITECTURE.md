> **Languages:** [English](../ARCHITECTURE.md) · [العربية](../ar/ARCHITECTURE.md) · [Čeština](../cs/ARCHITECTURE.md) · [Deutsch](../de/ARCHITECTURE.md) · [Español](../es/ARCHITECTURE.md) · [فارسی](../fa/ARCHITECTURE.md) · [Français](../fr/ARCHITECTURE.md) · [हिन्दी](../hi/ARCHITECTURE.md) · [Bahasa Indonesia](../id/ARCHITECTURE.md) · [Italiano](../it/ARCHITECTURE.md) · [日本語](../ja/ARCHITECTURE.md) · [한국어](../ko/ARCHITECTURE.md) · [Nederlands](../nl/ARCHITECTURE.md) · [Polski](../pl/ARCHITECTURE.md) · [Português](../pt/ARCHITECTURE.md) · [Русский](../ru/ARCHITECTURE.md) · [Svenska](../sv/ARCHITECTURE.md) · [ไทย](../th/ARCHITECTURE.md) · [Türkçe](../tr/ARCHITECTURE.md) · [Українська](../uk/ARCHITECTURE.md) · Tiếng Việt · [中文](../zh/ARCHITECTURE.md)

---

# Kiến trúc

Đây là một bản đồ ở mức cao về cách VD Infos được ghép lại. Nó mô tả hình dạng của ứng dụng, không
phải danh mục những gì nó đọc: điểm của một trình gỡ lỗi phương thức là đặt mọi lần đọc trước mặt
bạn, và việc đọc chúng là của bạn để làm trên chính thiết bị của mình.

## Mục tiêu thiết kế

- **Một giá trị, mọi phương thức.** Với mỗi mẩu thông tin, đọc nó qua mọi phương thức có thể đọc nó
  và xếp các lần đọc cạnh nhau. Một trình gỡ lỗi đưa bằng chứng lên bề mặt; nó không phán xét.
- **Trên thiết bị và yên lặng.** Không mạng, không tài khoản, không công việc ở nền. Ứng dụng chỉ
  chạy khi nó đang mở.
- **Máy đọc được.** Toàn bộ kết quả là một tài liệu máy đọc được duy nhất, nên một công cụ nhận được
  cùng bức tranh như một người. Xem [Tác nhân AI](AI-AGENTS.md).
- **Nhỏ ở nơi quan trọng.** Phần phải khó đánh lừa được giữ cố ý bé nhỏ.

## Các thấu kính

Một *thấu kính* là một cách đọc một giá trị. Cùng một giá trị được đọc qua vài thấu kính để chúng có
thể được so sánh:

| Thấu kính | Nó là gì |
|---|---|
| **Java / SDK** | API nền tảng mà một ứng dụng thông thường gọi. |
| **Native / JNI** | Cùng giá trị đọc từ mã native. |
| **Shell via JVM** | Một lệnh shell được JVM sinh ra. |
| **Shell via JNI** | Cùng lệnh đó được sinh ra từ mã native, một điểm nhìn khác. |
| **TEE / Attestation** | Chứng thực khóa có phần cứng hậu thuẫn, nơi nó áp dụng được. |

Xếp các thấu kính cạnh nhau chính là toàn bộ ý tưởng: khi chúng bất đồng, có thứ gì đó giữa ứng dụng
và giá trị đang trả lời khác nhau cho những người gọi khác nhau. Điều đó có ý nghĩa gì với một giá
trị nhất định, trên một thiết bị nhất định, là của bạn để đọc.

## Một lần quét chạy ra sao

```
core/model      immutable domain: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          the probes and the bridges they use (JNI, reflection, data lists)
engine/         ProbeEngine: parallel fan-out, streams results as they land
data/           SnapshotStore (persist the report), Exporter (share as JSON / text)
ui/             Jetpack Compose, Material 3, dynamic colour, live progress
cpp/            native_probes.cpp: the native lens, dependency-free
```

- **Song song.** Các đầu dò trải ra trên một dispatcher với số lượng giấy phép bị giới hạn; kết quả
  truyền vào giao diện khi chúng hoàn thành, với một chỉ báo tiến độ trực tiếp.
- **Không có gì ở nền.** Không dịch vụ và không lần quét theo lịch. Ứng dụng chỉ đọc khi đang mở và
  tự đóng khi bị bỏ nhàn rỗi.
- **Tầng native.** Một `.so` nhỏ, được buộc theo tên qua `RegisterNatives`, được giữ cố ý bé nhỏ vì
  nó là phần phải khó đánh lừa.

## Báo cáo

Mỗi lần quét tạo ra một `Snapshot`: thời điểm nó được thực hiện, phiên bản ứng dụng, và một danh sách
kết quả, mỗi đầu dò một kết quả, mỗi cái với các lần đọc từ mọi thấu kính và một phán quyết về cách
chúng liên hệ. Ứng dụng lưu bền cái này vào bộ nhớ riêng sau mỗi lần quét và có thể xuất cùng dữ
liệu đó. Cấu trúc được ghi thành tài liệu dưới dạng [định dạng xuất](EXPORT-FORMAT.md).

## Mở rộng nó

Hầu hết những gì ứng dụng tìm kiếm tồn tại dưới dạng **danh sách văn bản thuần** dưới
`assets/data/`, không phải dưới dạng mã. Thêm hoặc bớt một mục tiêu là sửa một dòng. Xem
[Đóng góp](CONTRIBUTING.md).

## Những gì VD Infos không bao giờ làm

- Nó không bao giờ thay đổi bất cứ thứ gì trên hệ thống của bạn. Nó chỉ đọc và so sánh.
- Nó không bao giờ nói chuyện với mạng. Nó hoạt động đầy đủ ngoại tuyến.
- Nó không bao giờ chạy ở nền, lên lịch một lần quét, hay giữ một dịch vụ sống.
- Nó không bao giờ gửi một lần đọc đi đâu. Một báo cáo rời khỏi thiết bị chỉ khi bạn chia sẻ hoặc lưu
  nó, với các giá trị mang danh tính được che.

<!-- doc-footer -->

---

> _Tài liệu này được tạo bằng Claude (Anthropic) và được duy trì bởi VD171. Chúng tôi hoan nghênh các chỉnh sửa và cải tiến; xem [CONTRIBUTING.md](CONTRIBUTING.md)._
