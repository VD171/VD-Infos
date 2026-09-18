[English](README.md) | [Português](README.pt.md) | [Español](README.es.md) | [Italiano](README.it.md) | [Deutsch](README.de.md) | [Français](README.fr.md) | [Русский](README.ru.md) | [Bahasa Indonesia](README.id.md) | [Türkçe](README.tr.md) | [Polski](README.pl.md) | [Nederlands](README.nl.md) | [Svenska](README.sv.md) | [Čeština](README.cs.md) | [Tiếng Việt](README.vi.md) | [中文](README.zh.md) | [日本語](README.ja.md) | [한국어](README.ko.md) | [فارسی](README.fa.md) | [हिन्दी](README.hi.md) | [العربية](README.ar.md) | **ไทย**

# VD Infos

*ตัวดีบักเมธอด*

[![License: AGPL-3.0-or-later](https://img.shields.io/badge/license-AGPL--3.0--or--later-blue.svg)](LICENSE)
[![Release](https://img.shields.io/github/v/release/VD171/VD-Infos)](https://github.com/VD171/VD-Infos/releases)
[![Downloads](https://img.shields.io/github/downloads/VD171/VD-Infos/total)](https://github.com/VD171/VD-Infos/releases)
[![Android 8.0+](https://img.shields.io/badge/Android-8.0%2B-3DDC84.svg)](https://github.com/VD171/VD-Infos/releases)
[![Languages](https://img.shields.io/badge/languages-21-orange.svg)](README.md)

<img src="images/vdinfos-01.png" height="420"/> <img src="images/vdinfos-02.png" height="420"/>

Android เป็นระบบปฏิบัติการที่ทรงพลังและยืดหยุ่นอย่างยิ่ง สิ่งที่ไม่มีใครบอกคุณคือรายละเอียดส่วนตัวและข้อมูลลับทั้งหมดของคุณเปิดให้ทุกแอปที่คุณติดตั้งเข้าถึงได้ และการปกป้องตัวเองจากการรุกล้ำความเป็นส่วนตัวเช่นนี้เป็นหน้าที่ VD Infos แสดงตัวอย่างสิ่งที่สามารถดึงจากอุปกรณ์ของคุณ และทำในฐานะ *ตัวดีบักเมธอด*: สำหรับข้อมูลแต่ละอย่าง มันอ่านค่าผ่าน **ทุกวิธีที่สามารถอ่านได้** - `Build.*`, `SystemProperties`, `getprop`, `__system_property_get` แบบเนทีฟ, ตัวจัดการระบบ, content provider, ไฟล์, syscall และ attestation ของกุญแจฮาร์ดแวร์ (TEE) - แล้วเรียงไว้ข้างกันให้คุณเปรียบเทียบ เมื่อวิธีหนึ่งไม่ตรงกับวิธีอื่น แสดงว่ามีบางอย่างตรงกลางกำลังเขียนพื้นผิวนั้นใหม่: เฟรมเวิร์ก hook, spoofer, resolver shim **ไม่มีข้อมูลใดถูกจัดเก็บ ส่ง หรือส่งต่อไปยังไฟล์หรือเซิร์ฟเวอร์ใด ๆ** - ทุกอย่างทำงานบนอุปกรณ์ ค่าที่บ่งบอกตัวตนจะถูกปิดบังจนกว่าคุณจะเปิดเผย และรายงานจะออกจากอุปกรณ์เฉพาะเมื่อคุณแชร์หรือบันทึกเองอย่างชัดแจ้ง หากต้องการ ให้บล็อกการเข้าถึงอินเทอร์เน็ตด้วยไฟร์วอลล์หรือเพียงปิดอินเทอร์เน็ต

## ตรวจสอบอะไรบ้าง

แต่ละรายการถูกอ่านผ่านทุกวิธีที่สามารถอ่านได้ (Java SDK / เนทีฟ / shell) และเปรียบเทียบเคียงข้างกัน:

* **ประมาณ 493 พร็อพเพอร์ตี้ของระบบ** อ่านห้าวิธี: `SystemProperties.get`, จุดเข้าทั้งสองของ bionic (`__system_property_read_callback` และ `__system_property_get` ขนาด 92 ไบต์), `getprop` ที่ JVM เรียก, และ `getprop` ที่เรียกจากโค้ดเนทีฟผ่าน `popen` - คำสั่ง shell ที่รันโดย `ProcessBuilder` กับคำสั่งเดียวกันที่รันโดยไม่มี JVM ไม่ใช่มุมมองเดียวกัน เพราะอย่างแรกเป็นพื้นผิวที่เฟรมเวิร์กซ่อน root เขียนใหม่ ส่วนอย่างหลังไม่ใช่
* **อัตลักษณ์อุปกรณ์**: รุ่น, ผู้ผลิต, แบรนด์, device, product, board, hardware, fingerprint, bootloader, build id/tags/type - แต่ละฟิลด์ `Build.*` เทียบกับทุกตัวแปร `ro.product.*` (system/vendor/odm), เนทีฟ และ shell
* **ตัวระบุ**: serial (getter หลายตัว), Android ID (settings และ provider), GSF ID, advertising ID, IMEI/primary IMEI/MEID/IMSI/ICCID, user serial
* **โทรศัพท์**: พื้นผิว TelephonyManager (ผู้ให้บริการ, SIM, เครือข่าย, โรมมิ่ง…), SubscriptionManager (หลาย SIM), cell info
* **เครือข่าย**: **MAC** ต่ออินเทอร์เฟซ (`NetworkInterface` เทียบ `/sys/class/net` เทียบ shell), Wi-Fi (SSID/BSSID/IP/MAC), บลูทูธ, DNS
* **เคอร์เนล / โปรเซส / ระบบ**: `uname`, boot id, uptime, UID/PID, SELinux, เขตเวลา, locale, สภาพแวดล้อม, บริการ/โปรเซสที่กำลังทำงาน
* **ฮาร์ดแวร์ / มีเดีย**: CPU, หน่วยความจำ, เซ็นเซอร์, จอแสดงผล, กล้อง, system features, GPU, Widevine DRM ID
* **แพ็กเกจ / บัญชี / WebView**: รายการและจำนวนแพ็กเกจที่ติดตั้ง, ไดเจสต์ลายเซ็นต่อแอป, ลายเซ็นของแอปนี้, การตรวจสอบแพ็กเกจของตนเอง, การแจงนับการแก้ intent, บัญชี, จำนวนบันทึกการโทร, user agent
* **ความสอดคล้องของ sandbox**: พื้นผิวที่ sandbox ควร **ปิด** (CID/serial ของ eMMC, อัตลักษณ์ UFS และ SoC, `/proc/cmdline`, `/proc/1/*`, `/dev/kmsg`, `/system/build.prop`) และ 22 บริการ `dumpsys` ที่นี่การปฏิเสธคือคำตอบ: `EACCES`/`DENIED` คือการอ่านที่สอดคล้อง และอุปกรณ์ที่กลับส่งมอบเนื้อหาแทนถือว่าไม่สอดคล้อง
* **ตัวระบุโฆษณา**: GAID จาก binder ของ Play Services บวกกับตระกูลกุญแจทั้งหมด (AAID, OAID/VAID และ `pps_*` ของ Huawei) ที่กวาดผ่าน settings store ทั้งสาม
* **ตัวตรวจจับ root / hook / อีมูเลเตอร์**: ร่องรอยของ `su`/Magisk/KernelSU/APatch/Xposed/LSPosed/Riru, ไลบรารีที่ถูกฉีดใน `/proc/self/maps`, แพ็กเกจที่รู้จัก, พร็อพเพอร์ตี้อันตรายและของอีมูเลเตอร์, verified boot state

## ดาวน์โหลดและการสนับสนุน

แต่ละรุ่นเผยแพร่ APK สองไฟล์: **SDK_35** มุ่งไปที่แซนด์บ็อกซ์สมัยใหม่ที่เข้มงวด และเป็นตัวที่ควรใช้ ส่วน **SDK_27** มุ่งไปที่ API ระดับเก่ากว่า ซึ่งมีโดเมน SELinux ที่ผ่อนคลายกว่า มีประโยชน์สำหรับการเปรียบเทียบ ทั้งสองติดตั้งได้บน Android 8.0 ขึ้นไป

* Release (APK): https://github.com/VD171/VD-Infos/releases
* https://github.com/VD171/VD-Infos
* https://xdaforums.com/t/VD-Infos.4097379/
* https://t.me/RootDetected
* https://t.me/BlankAssistance

## ภาษา

21 ภาษาของอินเทอร์เฟซ: อังกฤษ, โปรตุเกส, สเปน, อิตาลี, เยอรมัน, ฝรั่งเศส, รัสเซีย, อินโดนีเซีย, ตุรกี, โปแลนด์, ดัตช์, สวีเดน, เช็ก, เวียดนาม, จีน, ญี่ปุ่น, เกาหลี, เปอร์เซีย, ฮินดี, อาหรับ และไทย แอปเสนอการเลือกภาษาเพียงครั้งเดียวเมื่อเปิดครั้งแรก (พร้อมตัวเลือก "ค่าเริ่มต้นของระบบ") และนอกเหนือจากนั้นจะใช้ตามภาษาของระบบ

## สถาปัตยกรรม

```
core/model      โดเมนที่เปลี่ยนแปลงไม่ได้: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          NativeBridge (JNI) - SystemProps (reflection) - PropCatalog (ข้อมูล)
                SemanticProbes - IntegrityProbes - ProbeRegistry
engine/         ProbeEngine - fan-out ด้วยคอรูทีน สตรีมผลลัพธ์เป็น Flow
data/           SnapshotStore (คงอยู่ + diff) - Exporter (แชร์ JSON/ข้อความ)
ui/             Jetpack Compose, Material 3, สีไดนามิก ความคืบหน้าสด
cpp/            native_probes.cpp - เลนส์เนทีฟ ไม่มี dependency
```

* **การขนาน**: โพรบ 885 ตัวกระจายบน dispatcher เริ่มต้นด้วยจำนวนใบอนุญาตที่จำกัด ผลลัพธ์ไหลเข้าสู่ UI ทันทีที่มาถึง
* **ไม่มีอะไรทำงานเบื้องหลัง**: ไม่มีบริการและไม่มีการสแกนตามกำหนด แอปทำงานเฉพาะตอนเปิดอยู่และปิดตัวเองเมื่อถูกทิ้งไว้เฉยๆ
* **ชั้นเนทีฟ**: `.so` ขนาดเล็กหนึ่งไฟล์ ผูกตามชื่อผ่าน `RegisterNatives` ตั้งใจให้เล็กจิ๋วเพราะเป็นส่วนที่ต้องหลอกได้ยาก

## การมีส่วนร่วม

การมีส่วนร่วมส่วนใหญ่ไม่ต้องใช้ Kotlin: รายการต่าง ๆ เป็นข้อความธรรมดาใน `VDInfos/app/src/main/assets/data/` เพิ่มหรือลบหนึ่งบรรทัดแล้วเปิด pull request

* `*_apps.txt` - หนึ่งชื่อแพ็กเกจต่อบรรทัด
* `props.txt` - แคตตาล็อกพร็อพเพอร์ตี้ระบบ `หมวดหมู่<tab>คีย์`
* `spoof_keys.txt` - เมทริกซ์ spoof ของ settings `คีย์:ชนิด`
* ไฟล์ `.txt` อื่น ๆ - หนึ่งรายการต่อบรรทัดเช่นกัน (ชิ้นส่วนชื่อโมดูลเคอร์เนล, ชื่อไฟล์ใน `/data/local/tmp`)

เครื่องหมาย `#` เริ่มคอมเมนต์ และบรรทัดว่างจะถูกข้าม ยินดีรับการมีส่วนร่วมด้านโค้ดเช่นกัน การมีส่วนร่วมถือว่าคุณยอมรับให้ผลงานเผยแพร่ภายใต้ AGPL-3.0-or-later ของโครงการนี้

## แคตตาล็อก

แหล่งอ้างอิงการซ่อนและตรวจจับ root (คู่มือ, โมดูล, เฟรมเวิร์ก, ตัวตรวจจับ) อยู่ในแคตตาล็อกเฉพาะ: [CATALOG.md](CATALOG.md)

## กิตติกรรมประกาศ

ขอบคุณเป็นพิเศษแก่ **[frknkrc44](https://github.com/frknkrc44)** ผู้พัฒนา **[HMA-OSS](https://github.com/frknkrc44/HMA-OSS)** สำหรับความใส่ใจที่ทุ่มเทลงไป HMA-OSS คือที่ที่การแก้ไขหลายอย่างที่แอปนี้แนะนำถูกนำไปใช้ - ซ่อนแอปเป้าหมายและตั้งค่าพรีเซ็ต spoof - และทุกการกล่าวถึงมันภายในแอปจะลิงก์กลับไปยังแหล่งที่มา

ลิงก์ ช่อง และวิธีสนับสนุน: [HMA-OSS.md](HMA-OSS.md)

## ติดต่อ

* https://vd171.ru
* https://vd.priv8.ru
* **Telegram:** @VD_Priv8 https://t.me/VD_Priv8
* **Discord:** @VD.Priv8 https://discord.com/users/1296831918989639721
* **อีเมล:** vd.priv8@pm.me
* **XDA-Developers:** @VD171 https://xdaforums.com/m/vd171.4699873/
* **GitHub:** @VD171 https://github.com/VD171

## สัญญาอนุญาต

**GNU AGPL-3.0-or-later** - ดู [LICENSE](LICENSE) เป็น copyleft รวมถึงข้อกำหนดเครือข่าย: ผู้ใดรันเวอร์ชันที่แก้ไข (แม้ในรูปแบบบริการ) ต้องเสนอซอร์สของมัน เลือกโดยเจตนาสำหรับเครื่องมือวิจัย/ต่อต้านการตรวจจับ เพื่อให้ fork เปิดอยู่เสมอ

---

`android` `privacy` `security` `root-detection` `hook-detection` `spoof-detection` `anti-detection` `tamper-detection` `device-fingerprint` `magisk` `kernelsu` `lsposed` `play-integrity` `attestation` `tee` `keystore` `scanner` `diagnostics` `reverse-engineering` `kotlin`
