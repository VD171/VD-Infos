[English](README.md) | [Português](README.pt.md) | [Español](README.es.md) | [Italiano](README.it.md) | [Deutsch](README.de.md) | [Français](README.fr.md) | [Русский](README.ru.md) | [Bahasa Indonesia](README.id.md) | [Türkçe](README.tr.md) | [Polski](README.pl.md) | [Nederlands](README.nl.md) | [Svenska](README.sv.md) | [Čeština](README.cs.md) | [Tiếng Việt](README.vi.md) | [中文](README.zh.md) | [日本語](README.ja.md) | [한국어](README.ko.md) | [فارسی](README.fa.md) | **हिन्दी** | [العربية](README.ar.md) | [ไทย](README.th.md)

# VD Infos

*मेथड डिबगर*

[![License: AGPL-3.0-or-later](https://img.shields.io/badge/license-AGPL--3.0--or--later-blue.svg)](LICENSE)
[![Release](https://img.shields.io/github/v/release/VD171/VD-Infos)](https://github.com/VD171/VD-Infos/releases)
[![Downloads](https://img.shields.io/github/downloads/VD171/VD-Infos/total)](https://github.com/VD171/VD-Infos/releases)
[![Android 8.0+](https://img.shields.io/badge/Android-8.0%2B-3DDC84.svg)](https://github.com/VD171/VD-Infos/releases)
[![Languages](https://img.shields.io/badge/languages-21-orange.svg)](README.md)

<img src="images/vdinfos-01.png" height="420"/> <img src="images/vdinfos-02.png" height="420"/>

Android एक बेहद शक्तिशाली और बहुमुखी ऑपरेटिंग सिस्टम है; जो कोई आपको नहीं बताता वह यह है कि आपके सभी व्यक्तिगत विवरण और गोपनीय जानकारी हर उस ऐप के लिए उपलब्ध हैं जिसे आप इंस्टॉल करते हैं, और गोपनीयता के इन हमलों से स्वयं की रक्षा करना एक दायित्व है। VD Infos आपको एक उदाहरण दिखाता है कि आपके डिवाइस से क्या कैप्चर किया जा सकता है, और यह इसे एक *मेथड डिबगर* के रूप में करता है: प्रत्येक जानकारी के लिए यह मान को **हर उस विधि से पढ़ता है जो उसे पढ़ सकती है** - `Build.*`, `SystemProperties`, `getprop`, नेटिव `__system_property_get`, सिस्टम मैनेजर, content provider, फ़ाइलें, syscall और हार्डवेयर कुंजी attestation (TEE) - और तुलना के लिए उन्हें साथ-साथ रखता है। जब कोई विधि दूसरों से असहमत होती है, तो बीच में कुछ उस सतह को फिर से लिख रहा है: एक hook फ्रेमवर्क, एक spoofer, एक resolver shim। **कोई भी जानकारी संग्रहीत, भेजी या किसी फ़ाइल या सर्वर पर प्रेषित नहीं की जाती** - सब कुछ डिवाइस पर चलता है, पहचान वाले मान तब तक छिपे रहते हैं जब तक आप उन्हें प्रकट न करें, और रिपोर्ट डिवाइस को केवल तभी छोड़ती है जब आप स्पष्ट रूप से उसे साझा या सहेजते हैं; चाहें तो फ़ायरवॉल से इंटरनेट पहुँच रोकें या बस इंटरनेट बंद कर दें।

## यह क्या जाँचता है

प्रत्येक आइटम को हर उस विधि से पढ़ा जाता है जो उसे पढ़ सकती है (Java SDK / नेटिव / shell), और साथ-साथ तुलना की जाती है:

* **~493 सिस्टम प्रॉपर्टीज़** पाँच तरीकों से पढ़ी जाती हैं: `SystemProperties.get`, bionic के दोनों प्रवेश-बिंदु (`__system_property_read_callback` और 92-बाइट वाला `__system_property_get`), JVM द्वारा चलाया गया `getprop`, और नेटिव कोड से `popen` के ज़रिए चलाया गया `getprop` - `ProcessBuilder` द्वारा चलाया गया shell कमांड और वही कमांड JVM के बिना चलाया गया एक ही दृष्टिकोण नहीं हैं, क्योंकि पहला एक सतह है जिसे root-छिपाने वाला फ्रेमवर्क फिर से लिखता है और दूसरा नहीं।
* **डिवाइस पहचान**: मॉडल, निर्माता, ब्रांड, device, product, board, hardware, fingerprint, bootloader, build id/tags/type - प्रत्येक `Build.*` फ़ील्ड उसके सभी `ro.product.*` (system/vendor/odm) रूपों, नेटिव और shell के विरुद्ध।
* **पहचानकर्ता**: serial (कई getter), Android ID (settings और provider), GSF ID, advertising ID, IMEI/primary IMEI/MEID/IMSI/ICCID, user serial।
* **टेलीफ़ोनी**: TelephonyManager सतह (ऑपरेटर, SIM, नेटवर्क, रोमिंग…), SubscriptionManager (मल्टी-SIM), cell info।
* **नेटवर्क**: प्रति-इंटरफ़ेस **MAC** (`NetworkInterface` बनाम `/sys/class/net` बनाम shell), Wi-Fi (SSID/BSSID/IP/MAC), ब्लूटूथ, DNS।
* **कर्नेल / प्रोसेस / सिस्टम**: `uname`, boot id, uptime, UID/PID, SELinux, समय क्षेत्र, locale, परिवेश, चल रही सेवाएँ/प्रोसेस।
* **हार्डवेयर / मीडिया**: CPU, मेमोरी, सेंसर, डिस्प्ले, कैमरे, system features, GPU, Widevine DRM ID।
* **पैकेज / खाते / WebView**: इंस्टॉल किए पैकेजों की सूची और संख्या, प्रति-ऐप हस्ताक्षर डाइजेस्ट, इस ऐप का हस्ताक्षर, स्वयं के पैकेज का आत्म-निरीक्षण, intent-समाधान गणनाएँ, खाते, कॉल-लॉग गिनती, user agent।
* **सैंडबॉक्स अनुरूपता**: वे सतहें जिन्हें सैंडबॉक्स को **बंद** करना चाहिए (eMMC CID/serial, UFS और SoC पहचान, `/proc/cmdline`, `/proc/1/*`, `/dev/kmsg`, `/system/build.prop`) और 22 `dumpsys` सेवाएँ। यहाँ इनकार ही उत्तर है: `EACCES`/`DENIED` अनुरूप पठन है, और जो डिवाइस इसके बजाय सामग्री सौंप देता है वह अनुरूप नहीं है।
* **विज्ञापन पहचानकर्ता**: Play Services binder से GAID, साथ ही पूरी कुंजी-परिवार (AAID, OAID/VAID और Huawei के `pps_*`) तीनों settings store में खंगाली गई।
* **Root / hook / एमुलेटर डिटेक्टर**: `su`/Magisk/KernelSU/APatch/Xposed/LSPosed/Riru के अवशेष, `/proc/self/maps` में इंजेक्ट की गई libs, ज्ञात पैकेज, ख़तरनाक और एमुलेटर प्रॉपर्टीज़, verified boot state।

## डाउनलोड और सहायता

हर रिलीज़ दो APK प्रकाशित करती है: **SDK_35** सख़्त आधुनिक सैंडबॉक्स को लक्षित करता है और यही उपयोग करें; **SDK_27** पुराने API स्तर को लक्षित करता है, ढीले SELinux डोमेन के साथ, तुलना के लिए उपयोगी। दोनों Android 8.0 और उससे नए पर इंस्टॉल होते हैं।

* Release (APK): https://github.com/VD171/VD-Infos/releases
* https://github.com/VD171/VD-Infos
* https://xdaforums.com/t/VD-Infos.4097379/
* https://t.me/RootDetected
* https://t.me/BlankAssistance

## भाषाएँ

21 UI भाषाएँ: अंग्रेज़ी, पुर्तगाली, स्पेनिश, इतालवी, जर्मन, फ़्रेंच, रूसी, इंडोनेशियाई, तुर्की, पोलिश, डच, स्वीडिश, चेक, वियतनामी, चीनी, जापानी, कोरियाई, फ़ारसी, हिन्दी, अरबी और थाई। ऐप पहली बार शुरू होने पर एक बार भाषा चुनने का विकल्प देता है ("सिस्टम डिफ़ॉल्ट" विकल्प के साथ) और अन्यथा सिस्टम की भाषा का अनुसरण करता है।

## आर्किटेक्चर

```
core/model      अपरिवर्तनीय डोमेन: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          NativeBridge (JNI) - SystemProps (रिफ्लेक्शन) - PropCatalog (डेटा)
                SemanticProbes - IntegrityProbes - ProbeRegistry
engine/         ProbeEngine - कोरूटिन fan-out, परिणामों को Flow के रूप में स्ट्रीम करता है
data/           SnapshotStore (स्थायित्व + diff) - Exporter (JSON/टेक्स्ट साझा)
ui/             Jetpack Compose, Material 3, डायनैमिक रंग, लाइव प्रगति
cpp/            native_probes.cpp - नेटिव लेंस, बिना निर्भरता
```

* **समांतरता**: 885 सोंडे सीमित परमिट संख्या के साथ डिफ़ॉल्ट dispatcher पर फैलती हैं; परिणाम आते ही UI में प्रवाहित होते हैं।
* **पृष्ठभूमि में कुछ नहीं**: कोई सेवा नहीं और कोई निर्धारित स्कैन नहीं; ऐप केवल खुले रहने तक चलता है और निष्क्रिय छोड़े जाने पर स्वयं बंद हो जाता है।
* **नेटिव परत**: एक छोटी `.so`, `RegisterNatives` के ज़रिए नाम से बंधी, जानबूझकर बहुत छोटी रखी गई क्योंकि यही वह हिस्सा है जिसे धोखा देना कठिन होना चाहिए।

## योगदान

अधिकांश योगदानों के लिए Kotlin की ज़रूरत नहीं: सूचियाँ `VDInfos/app/src/main/assets/data/` के अंतर्गत सादे पाठ में हैं। एक पंक्ति जोड़ें या हटाएँ और pull request खोलें।

* `*_apps.txt` - प्रति पंक्ति एक पैकेज नाम
* `props.txt` - सिस्टम प्रॉपर्टी कैटलॉग, `श्रेणी<tab>कुंजी`
* `spoof_keys.txt` - settings स्पूफ़ मैट्रिक्स, `कुंजी:प्रकार`

`#` टिप्पणी शुरू करता है; खाली पंक्तियाँ अनदेखी की जाती हैं। कोड योगदान का भी स्वागत है। योगदान देकर आप सहमत होते हैं कि आपका कार्य इस परियोजना के AGPL-3.0-or-later के अंतर्गत जारी होगा।

## कैटलॉग

root छिपाने और पहचानने के संदर्भ (गाइड, मॉड्यूल, फ्रेमवर्क, डिटेक्टर) एक समर्पित कैटलॉग में हैं: [CATALOG.md](CATALOG.md)।

## आभार

**[HMA-OSS](https://github.com/frknkrc44/HMA-OSS)** के डेवलपर **[frknkrc44](https://github.com/frknkrc44)** को उसमें लगाई गई देखभाल के लिए विशेष धन्यवाद। HMA-OSS वह जगह है जहाँ इस ऐप द्वारा सुझाए गए कई सुधार लागू होते हैं - लक्ष्य ऐप्स को छिपाना और spoof प्रीसेट सेट करना - और ऐप में इसका हर उल्लेख इसके स्रोत से जुड़ता है।

इसके लिंक, चैनल और समर्थन करने का तरीका: [HMA-OSS.md](HMA-OSS.md)।

## संपर्क

* https://vd171.ru
* https://vd.priv8.ru
* **Telegram:** @VD_Priv8 https://t.me/VD_Priv8
* **Discord:** @VD.Priv8 https://discord.com/users/1296831918989639721
* **ईमेल:** vd.priv8@pm.me
* **XDA-Developers:** @VD171 https://xdaforums.com/m/vd171.4699873/
* **GitHub:** @VD171 https://github.com/VD171

## लाइसेंस

**GNU AGPL-3.0-or-later** - देखें [LICENSE](LICENSE)। कॉपीलेफ़्ट, नेटवर्क क्लॉज़ सहित: जो कोई संशोधित संस्करण चलाता है (सेवा के रूप में भी) उसे उसका स्रोत देना होगा। forks को खुला रखने के लिए एक शोध/एंटी-डिटेक्शन उपकरण हेतु जानबूझकर चुना गया।
