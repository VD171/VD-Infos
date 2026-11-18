> **Languages:** [English](../BUILDING.md) · [العربية](../ar/BUILDING.md) · [Čeština](../cs/BUILDING.md) · [Deutsch](../de/BUILDING.md) · [Español](../es/BUILDING.md) · [فارسی](../fa/BUILDING.md) · [Français](../fr/BUILDING.md) · हिन्दी · [Bahasa Indonesia](../id/BUILDING.md) · [Italiano](../it/BUILDING.md) · [日本語](../ja/BUILDING.md) · [한국어](../ko/BUILDING.md) · [Nederlands](../nl/BUILDING.md) · [Polski](../pl/BUILDING.md) · [Português](../pt/BUILDING.md) · [Русский](../ru/BUILDING.md) · [Svenska](../sv/BUILDING.md) · [ไทย](../th/BUILDING.md) · [Türkçe](../tr/BUILDING.md) · [Українська](../uk/BUILDING.md) · [Tiếng Việt](../vi/BUILDING.md) · [中文](../zh/BUILDING.md)

---

# VD Infos बिल्ड करना

VD Infos एक मानक Android Gradle परियोजना है: Kotlin और Jetpack Compose, NDK के ज़रिए बनाए गए
C++ में एक छोटे नेटिव लेंस के साथ। CI इसे बिल्ड कर सकती है, और आप इसे स्थानीय रूप से पुनरुत्पादित
कर सकते हैं।

## आवश्यकताएँ

- एक JDK (17 से 21) और Android SDK।
- Android NDK और CMake, नेटिव लेंस के लिए (यदि आपके पास SDK का `cmake` घटक है तो Gradle Android
  प्लगइन एक मेल खाता CMake प्राप्त कर लेता है)।
- `minSdk` 26, इसलिए बिल्ड Android 8.0 और नए पर चलते हैं।

## त्वरित शुरुआत

```sh
# build both flavors, release
./gradlew assembleRelease
```

यह **दो APK** उत्पन्न करता है, प्रति फ़्लेवर एक:

- **modern** (`SDK_35`) - सख़्त आधुनिक सैंडबॉक्स, उपयोग करने वाला बिल्ड।
- **compat** (`SDK_27`) - एक पुराना API लक्ष्य, केवल अगल-बगल की तुलना के लिए।

आउटपुट यहाँ आते हैं:

```
app/build/outputs/apk/modern/release/VDInfos-v<ver>-SDK_35-release.apk
app/build/outputs/apk/compat/release/VDInfos-v<ver>-SDK_27-release.apk
```

इटरेट करते समय केवल एक फ़्लेवर बिल्ड करने के लिए:

```sh
./gradlew assembleModernRelease   # SDK_35 only
./gradlew assembleCompatRelease   # SDK_27 only
```

## साइनिंग

रिलीज़ बिल्ड एक ऐसी की से साइन किए जाते हैं जो **स्थानीय है और कमिट नहीं की गई** (कीस्टोर और उसकी
प्रॉपर्टीज़ git द्वारा अनदेखी की जाती हैं)। इसके बिना, रिलीज़ टास्क फिर भी बिल्ड करता है लेकिन APK
बिना साइन किए निकलता है। अपने स्वयं के बिल्ड साइन करने के लिए अपना स्वयं का कीस्टोर प्रदान करें।

## दो फ़्लेवर

ऐप जानबूझकर दो फ़्लेवर शिप करता है। वे अलग-अलग API स्तरों को लक्षित करते हैं और एक ही स्रोत से बिल्ड
होते हैं; केवल लक्ष्य और कुछ संरक्षित कोड पथ भिन्न होते हैं। एक ऐसा परिवर्तन जो नेटिव कोड या किसी
फ़्लेवर-विशिष्ट पथ को छूता है, उसे केवल उस एक फ़्लेवर में नहीं जिस पर आप इटरेट कर रहे हैं, बल्कि
**दोनों** फ़्लेवर में बिल्ड और जाँचा जाना चाहिए।

## नेटिव लेंस

नेटिव लेंस एक एकल, निर्भरता-मुक्त C++ अनुवाद इकाई है जिसे NDK एक छोटे `.so` के रूप में बिल्ड करता है,
`RegisterNatives` के ज़रिए नाम से बँधा। यह सामान्य Gradle बिल्ड के भाग के रूप में संकलित होता है;
कोई अलग चरण नहीं है। इसे जानबूझकर बहुत छोटा और सरल रखा जाता है।

## चीज़ें कहाँ रहती हैं

```
app/src/main/java/ru/vd171/vdinfos/   Kotlin: model, probes, engine, data, ui
app/src/main/cpp/                     the native lens
app/src/main/assets/data/             the plain-text detection lists (see Contributing)
```

टुकड़े आपस में कैसे फ़िट होते हैं इसके लिए [आर्किटेक्चर](ARCHITECTURE.md) देखें, और कोड को छुए बिना
एक पहचान लक्ष्य कैसे जोड़ें इसके लिए [योगदान](CONTRIBUTING.md)।

<!-- doc-footer -->

---

> _यह दस्तावेज़ Claude (Anthropic) से तैयार किया गया है और इसे VD171 द्वारा अनुरक्षित किया जाता है। सुधार और संशोधन का स्वागत है; देखें [CONTRIBUTING.md](CONTRIBUTING.md)।_
