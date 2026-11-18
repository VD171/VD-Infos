> **Languages:** [English](../BUILDING.md) · [العربية](../ar/BUILDING.md) · [Čeština](../cs/BUILDING.md) · [Deutsch](../de/BUILDING.md) · [Español](../es/BUILDING.md) · [فارسی](../fa/BUILDING.md) · [Français](../fr/BUILDING.md) · [हिन्दी](../hi/BUILDING.md) · [Bahasa Indonesia](../id/BUILDING.md) · [Italiano](../it/BUILDING.md) · [日本語](../ja/BUILDING.md) · [한국어](../ko/BUILDING.md) · [Nederlands](../nl/BUILDING.md) · [Polski](../pl/BUILDING.md) · [Português](../pt/BUILDING.md) · [Русский](../ru/BUILDING.md) · [Svenska](../sv/BUILDING.md) · ไทย · [Türkçe](../tr/BUILDING.md) · [Українська](../uk/BUILDING.md) · [Tiếng Việt](../vi/BUILDING.md) · [中文](../zh/BUILDING.md)

---

# การสร้าง VD Infos

VD Infos เป็นโปรเจกต์ Android Gradle มาตรฐาน: Kotlin และ Jetpack Compose พร้อมเลนส์เนทีฟขนาดเล็กใน C++
ที่สร้างผ่าน NDK CI สามารถสร้างมันได้ และคุณสามารถทำซ้ำมันในเครื่องได้

## ข้อกำหนด

- JDK (17 ถึง 21) และ Android SDK
- Android NDK และ CMake สำหรับเลนส์เนทีฟ (ปลั๊กอิน Gradle Android ดึง CMake ที่ตรงกันหากคุณมีคอมโพเนนต์ `cmake` ของ SDK)
- `minSdk` 26 ดังนั้นบิลด์จึงทำงานบน Android 8.0 และใหม่กว่า

## เริ่มต้นอย่างรวดเร็ว

```sh
# build both flavors, release
./gradlew assembleRelease
```

สิ่งนี้ผลิต **APK สองตัว** หนึ่งตัวต่อ flavor:

- **modern** (`SDK_35`) - แซนด์บ็อกซ์สมัยใหม่ที่เข้มงวด บิลด์ที่ควรใช้
- **compat** (`SDK_27`) - เป้าหมายที่เก่ากว่าโดยมีโดเมน SELinux ที่หลวมกว่า สำหรับการเปรียบเทียบเคียงข้างกันเท่านั้น

ผลลัพธ์ลงที่:

```
app/build/outputs/apk/modern/release/VDInfos-v<ver>-SDK_35-release.apk
app/build/outputs/apk/compat/release/VDInfos-v<ver>-SDK_27-release.apk
```

หากต้องการสร้างเพียง flavor เดียวขณะทำซ้ำ:

```sh
./gradlew assembleModernRelease   # SDK_35 only
./gradlew assembleCompatRelease   # SDK_27 only
```

## การลงนาม

บิลด์ release ลงนามด้วยคีย์ที่ **อยู่ในเครื่องและไม่ถูกคอมมิต** (keystore และคุณสมบัติของมันถูก git ละเว้น)
หากไม่มีมัน งาน release ยังคงสร้างได้แต่ APK จะออกมาโดยไม่มีการลงนาม จัดหา keystore ของคุณเองเพื่อลงนามบิลด์ของคุณเอง

## สอง flavor

แอปจัดส่งสอง flavor โดยเจตนา พวกมันมุ่งเป้าไปที่ API ระดับที่ต่างกันและด้วยเหตุนี้จึงตกอยู่ในโดเมนแซนด์บ็อกซ์ Android ที่ต่างกัน
ซึ่งเองก็มีประโยชน์สำหรับการเปรียบเทียบ ทั้งคู่สร้างจากซอร์สเดียวกัน มีเพียงเป้าหมายและโค้ดพาธที่มีการป้องกันไม่กี่ตัวที่ต่างกัน
การเปลี่ยนแปลงที่แตะโค้ดเนทีฟหรือพาธเฉพาะ flavor ควรถูกสร้างและตรวจสอบใน **ทั้งสอง** flavor ไม่ใช่เฉพาะตัวที่คุณกำลังทำซ้ำอยู่

## เลนส์เนทีฟ

เลนส์เนทีฟเป็นหน่วยการแปล C++ เดียวที่ปราศจากการพึ่งพา สร้างโดย NDK เป็น `.so` ขนาดเล็กหนึ่งตัว ผูกด้วยชื่อผ่าน `RegisterNatives`
มันถูกคอมไพล์เป็นส่วนหนึ่งของบิลด์ Gradle ปกติ ไม่มีขั้นตอนแยกต่างหาก มันถูกเก็บให้เล็กจิ๋วโดยเจตนาเพราะมันเป็นส่วนที่ต้องหลอกได้ยาก

## สิ่งต่าง ๆ อยู่ที่ไหน

```
app/src/main/java/ru/vd171/vdinfos/   Kotlin: model, probes, engine, data, ui
app/src/main/cpp/                     the native lens
app/src/main/assets/data/             the plain-text detection lists (see Contributing)
```

ดู[สถาปัตยกรรม](ARCHITECTURE.md) สำหรับวิธีที่ชิ้นส่วนต่าง ๆ ประกอบเข้าด้วยกัน และ[การมีส่วนร่วม](CONTRIBUTING.md)
สำหรับวิธีการเพิ่มเป้าหมายการตรวจจับโดยไม่ต้องแตะโค้ด

<!-- doc-footer -->

---

> _เอกสารนี้สร้างขึ้นด้วย Claude (Anthropic) และดูแลโดย VD171 ยินดีรับการแก้ไขและการปรับปรุง ดู [CONTRIBUTING.md](CONTRIBUTING.md)_
