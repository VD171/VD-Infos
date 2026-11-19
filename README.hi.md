<!--
  VD INFOS: device method debugger: read every device info by every method, then compare.
  Copyright (C) 2026  VD171. SPDX-License-Identifier: AGPL-3.0-or-later
-->

# VD Infos

<p align="center">
  <img src="images/logo-wordmark.png" alt="VD Infos" width="320">
</p>

<p align="center">
  <a href="https://github.com/VD171/VD-Infos/releases/latest"><img src="https://img.shields.io/github/v/release/VD171/VD-Infos?label=release&color=0D9488" alt="Release"></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/license-AGPL--3.0--or--later-1a7f37" alt="License"></a>
  <a href="https://github.com/VD171/VD-Infos/releases"><img src="https://img.shields.io/github/downloads/VD171/VD-Infos/total?label=downloads&color=8250df" alt="Downloads"></a>
  <a href="https://github.com/VD171/VD-Infos/stargazers"><img src="https://img.shields.io/github/stars/VD171/VD-Infos?color=d29922" alt="Stars"></a>
</p>

<p align="center">
  <b>डिवाइस मेथड डिबगर।</b><br>
  हर डिवाइस मान को हर उस विधि से पढ़ें जो उसे पढ़ सकती है, और उन्हें साथ-साथ रखें ताकि आप तुलना कर सकें।
</p>

<p align="center">
  <a href="https://github.com/VD171/VD-Infos/releases">Releases</a> ·
  <a href="https://github.com/VD171/VD-Infos/wiki">Wiki</a> ·
  <a href="https://github.com/VD171/VD-Infos/wiki/USER-GUIDE">उपयोगकर्ता मार्गदर्शिका</a> ·
  <a href="https://github.com/VD171/VD-Infos/wiki/AI-AGENTS">AI एजेंट</a> ·
  <a href="https://github.com/VD171/VD-Infos/wiki/EXPORT-FORMAT">निर्यात प्रारूप</a> ·
  <a href="https://github.com/VD171/VD-Infos/wiki/ARCHITECTURE">आर्किटेक्चर</a> ·
  <a href="CATALOG.md">कैटलॉग</a> ·
  <a href="CHANGELOG.md">Changelog</a> ·
  <a href="https://xdaforums.com/t/VD-Infos.4097379/">XDA thread</a>
</p>

> **Languages:** [English](README.md) · [العربية](README.ar.md) · [Čeština](README.cs.md) · [Deutsch](README.de.md) · [Español](README.es.md) · [فارسی](README.fa.md) · [Français](README.fr.md) · [हिन्दी](README.hi.md) · [Bahasa Indonesia](README.id.md) · [Italiano](README.it.md) · [日本語](README.ja.md) · [한국어](README.ko.md) · [Nederlands](README.nl.md) · [Polski](README.pl.md) · [Português](README.pt.md) · [Русский](README.ru.md) · [Svenska](README.sv.md) · [ไทย](README.th.md) · [Türkçe](README.tr.md) · [Українська](README.uk.md) · [Tiếng Việt](README.vi.md) · [中文](README.zh.md)

<p align="center">
  <img src="images/vdinfos-01.png" height="420"/>
  <img src="images/vdinfos-02.png" height="420"/>
  <img src="images/vdinfos-03.png" height="420"/>
</p>

---

Android एक बेहद शक्तिशाली और बहुमुखी ऑपरेटिंग सिस्टम है; जो कोई आपको नहीं बताता वह यह है कि आपके सभी व्यक्तिगत विवरण और गोपनीय जानकारी हर उस ऐप के लिए उपलब्ध हैं जिसे आप इंस्टॉल करते हैं, और गोपनीयता के इन हमलों से स्वयं की रक्षा करना एक दायित्व है। VD Infos आपको एक उदाहरण दिखाता है कि आपके डिवाइस से क्या कैप्चर किया जा सकता है, और यह इसे एक *मेथड डिबगर* के रूप में करता है: प्रत्येक जानकारी के लिए यह मान को **हर उस विधि से पढ़ता है जो उसे पढ़ सकती है**: framework API, नेटिव इंटरफ़ेस, shell कमांड, सिस्टम मैनेजर, content provider, फ़ाइलें, syscall और हार्डवेयर कुंजी attestation (TEE), और तुलना के लिए उन्हें साथ-साथ रखता है। जब कोई एक पठन दूसरों से असहमत होता है, तो बीच में कुछ उस सतह को फिर से लिख रहा है: एक hook फ्रेमवर्क, एक spoofer, एक resolver shim। **कोई भी जानकारी संग्रहीत, भेजी या किसी फ़ाइल¹ या सर्वर पर प्रेषित नहीं की जाती**: सब कुछ डिवाइस पर चलता है, पहचान वाले मान तब तक छिपे रहते हैं जब तक आप उन्हें प्रकट न करें, और रिपोर्ट डिवाइस को केवल तभी छोड़ती है जब आप स्पष्ट रूप से उसे साझा या सहेजते हैं; चाहें तो फ़ायरवॉल से इंटरनेट पहुँच रोकें या बस इंटरनेट बंद कर दें।

> **¹ अपवाद:** हर स्कैन अपनी पूरी रिपोर्ट ऐप के निजी स्टोरेज में रखता है (`files/last_snapshot.json`)। इसे केवल ऐप ख़ुद या root पढ़ सकता है, अगला स्कैन इसे अधिलेखित कर देता है और यह कभी डिवाइस से बाहर नहीं जाती। यह इसलिए है कि AI एजेंट पूरा परिणाम पढ़ सकें; देखें **AI एजेंटों के लिए: पूरी रिपोर्ट JSON में**.

## यह क्या जाँचता है

प्रत्येक आइटम को हर उस विधि से पढ़ा जाता है जो उसे पढ़ सकती है (Java SDK / नेटिव / shell), और साथ-साथ तुलना की जाती है:

* **~488 सिस्टम प्रॉपर्टीज़** कई स्वतंत्र रास्तों से पढ़ी जाती हैं: Java API, नेटिव प्रॉपर्टी इंटरफ़ेस, और अलग-अलग तरीकों से पहुँचाया गया `getprop` shell कमांड, फिर इन्हें साथ-साथ रखकर तुलना की जाती है। चूँकि ये सभी रास्ते एक ही परत से होकर नहीं गुज़रते, इसलिए जो मान एक रास्ते पर फिर से लिखा गया हो पर दूसरे पर नहीं, वह असहमति के रूप में सामने आता है।
* **डिवाइस पहचान**: मॉडल, निर्माता, ब्रांड, device, product, board, hardware, fingerprint, bootloader, build id/tags/type, प्रत्येक `Build.*` फ़ील्ड उसके सभी `ro.product.*` (system/vendor/odm) रूपों, नेटिव और shell के विरुद्ध।
* **पहचानकर्ता**: serial (कई getter), Android ID (settings और provider), GSF ID, advertising ID, IMEI/primary IMEI/MEID/IMSI/ICCID, user serial।
* **टेलीफ़ोनी**: TelephonyManager सतह (ऑपरेटर, SIM, नेटवर्क, रोमिंग…), SubscriptionManager (मल्टी-SIM), cell info।
* **नेटवर्क**: प्रति-इंटरफ़ेस **MAC** (`NetworkInterface` बनाम `/sys/class/net` बनाम shell), Wi-Fi (SSID/BSSID/IP/MAC), ब्लूटूथ, DNS।
* **कर्नेल / प्रोसेस / सिस्टम**: `uname`, boot id, uptime, UID/PID, SELinux, समय क्षेत्र, locale, परिवेश, चल रही सेवाएँ/प्रोसेस।
* **हार्डवेयर / मीडिया**: CPU, मेमोरी, सेंसर, डिस्प्ले, कैमरे, system features, GPU, Widevine DRM ID।
* **पैकेज / खाते / WebView**: इंस्टॉल किए पैकेजों की सूची और संख्या, प्रति-ऐप हस्ताक्षर डाइजेस्ट, इस ऐप का हस्ताक्षर, स्वयं के पैकेज का आत्म-निरीक्षण, intent-समाधान गणनाएँ, खाते, कॉल-लॉग गिनती, user agent।
* **सैंडबॉक्स अनुरूपता**: निम्न-स्तरीय पहचान और कर्नेल सतहें, साथ ही कुछ `dumpsys` सेवाएँ, जिन तक ठीक से सैंडबॉक्स किए गए ऐप को नहीं पहुँचना चाहिए। इनके लिए ऐप जाँचता है कि पहुँच सही तरीके से अस्वीकृत होती है या नहीं; जो सतह तब पहुँच योग्य हो जब उसे नहीं होना चाहिए, उसे समीक्षा के लिए चिह्नित किया जाता है।
* **विज्ञापन पहचानकर्ता**: Play Services binder से GAID, साथ ही पूरी कुंजी-परिवार (AAID, OAID/VAID और Huawei के `pps_*`) तीनों settings store में खंगाली गई।
* **Root / hook / एमुलेटर डिटेक्टर**: आम root मैनेजरों, hooking फ्रेमवर्कों और एमुलेटरों (Magisk, KernelSU, APatch, Xposed/LSPosed, Riru आदि) द्वारा छोड़े गए अवशेष और संकेत, साथ ही verified-boot state।

## डाउनलोड और सहायता

हर रिलीज़ दो APK प्रकाशित करती है: **SDK_35** सख़्त आधुनिक सैंडबॉक्स को लक्षित करता है और यही उपयोग करें; **SDK_27** पुराने API स्तर को लक्षित करता है, तुलना के लिए उपयोगी। दोनों Android 8.0 और उससे नए पर इंस्टॉल होते हैं।

* Release (APK): https://github.com/VD171/VD-Infos/releases
* https://github.com/VD171/VD-Infos
* https://xdaforums.com/t/VD-Infos.4097379/
* https://t.me/RootDetected
* https://t.me/BlankAssistance

## AI एजेंटों के लिए: पूरी रिपोर्ट JSON में

shell के ज़रिए डिवाइस पर काम करने वाले AI एजेंटों और LLM टूल (कोडिंग सहायक, विश्लेषण स्क्रिप्ट) के लिए बनाया गया। स्क्रीनशॉट पढ़ने के बजाय एजेंट एक कमांड से पूरा परिणाम पढ़ता है:

```sh
su -c cat /data/data/ru.vd171.vdinfos/files/last_snapshot.json
```

* **इसमें क्या है:** हर सोंड अपने निर्णय और सभी लेंसों के मान के साथ, उसी JSON में जो एक्सपोर्ट बटन देता है। स्क्रीन समेटी गई प्रविष्टि का केवल पहला पठन दिखाती है; फ़ाइल में सभी हैं।
* **कब अपडेट होती है:** हर स्कैन के अंत में, इसलिए पहले एक स्कैन चलाएँ; फ़ाइल में हमेशा सबसे नया परिणाम रहता है।
* **ज़रूरत:** root, क्योंकि फ़ाइल ऐप के निजी स्टोरेज में है।

## आर्किटेक्चर

```
core/model      अपरिवर्तनीय डोमेन: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          NativeBridge (JNI), SystemProps (रिफ्लेक्शन), PropCatalog (डेटा)
                SemanticProbes, IntegrityProbes, ProbeRegistry
engine/         ProbeEngine: कोरूटिन fan-out, परिणामों को Flow के रूप में स्ट्रीम करता है
data/           SnapshotStore (स्थायित्व + diff), Exporter (JSON/टेक्स्ट साझा)
ui/             Jetpack Compose, Material 3, डायनैमिक रंग, लाइव प्रगति
cpp/            native_probes.cpp: नेटिव लेंस, बिना निर्भरता
```

* **समांतरता**: सोंडे सीमित परमिट संख्या के साथ डिफ़ॉल्ट dispatcher पर फैलती हैं; परिणाम आते ही UI में प्रवाहित होते हैं।
* **पृष्ठभूमि में कुछ नहीं**: कोई सेवा नहीं और कोई निर्धारित स्कैन नहीं; ऐप केवल खुले रहने तक चलता है और निष्क्रिय छोड़े जाने पर स्वयं बंद हो जाता है।
* **नेटिव परत**: एक छोटी, निर्भरता-मुक्त `.so`, `RegisterNatives` के ज़रिए नाम से बंधी, जानबूझकर बहुत छोटी और सरल रखी गई।

## योगदान

अधिकांश योगदानों के लिए Kotlin की ज़रूरत नहीं: सूचियाँ `VDInfos/app/src/main/assets/data/` के अंतर्गत सादे पाठ में हैं। एक पंक्ति जोड़ें या हटाएँ और pull request खोलें।

* `*_apps.txt`: प्रति पंक्ति एक पैकेज नाम
* `props.txt`: सिस्टम प्रॉपर्टी कैटलॉग, `श्रेणी<tab>कुंजी`
* `spoof_keys.txt`: settings स्पूफ़ मैट्रिक्स, `कुंजी:प्रकार`
* `known_certs.txt`: साइनिंग प्रमाणपत्रों की सूची, `SHA-256<tab>मालिक` (पूरा हैश, असली APK से `apksigner verify --print-certs` द्वारा)
* अन्य `.txt` फ़ाइलें: इनमें भी प्रति पंक्ति एक प्रविष्टि (कर्नेल-मॉड्यूल नाम के अंश, `/data/local/tmp` के नाम)

`#` टिप्पणी शुरू करता है; खाली पंक्तियाँ अनदेखी की जाती हैं। कुछ फ़ाइलों में प्रति पंक्ति स्पेस या टैब से अलग किए गए कुछ फ़ील्ड, या वैकल्पिक चिह्न (`props.txt` में `S` और `@field`) होते हैं; फ़ाइल में पहले से मौजूद पंक्तियों का स्वरूप बनाए रखें। कोड योगदान का भी स्वागत है। योगदान देकर आप सहमत होते हैं कि आपका कार्य इस परियोजना के AGPL-3.0-or-later के अंतर्गत जारी होगा।

## कैटलॉग

root छिपाने और पहचानने के संदर्भ (गाइड, मॉड्यूल, फ्रेमवर्क, डिटेक्टर) एक समर्पित कैटलॉग में हैं: [CATALOG.md](CATALOG.md)।

## आभार

**[HMA-OSS](https://github.com/frknkrc44/HMA-OSS)** के डेवलपर **[frknkrc44](https://github.com/frknkrc44)** को उसमें लगाई गई देखभाल के लिए विशेष धन्यवाद। HMA-OSS वह जगह है जहाँ इस ऐप द्वारा सुझाए गए कई सुधार लागू होते हैं (लक्ष्य ऐप्स को छिपाना और spoof प्रीसेट सेट करना), और ऐप में इसका हर उल्लेख इसके स्रोत से जुड़ता है।

इसके लिंक, चैनल और समर्थन करने का तरीका: [HMA-OSS.md](HMA-OSS.md)।

## दस्तावेज़ीकरण

- **[Wiki](https://github.com/VD171/VD-Infos/wiki)**: सारा दस्तावेज़ीकरण, 22 भाषाओं में ऑनलाइन ब्राउज़ करने योग्य।
- **[उपयोगकर्ता मार्गदर्शिका](https://github.com/VD171/VD-Infos/wiki/USER-GUIDE)**: इंस्टॉल करना, स्कैन चलाना, परिणाम पढ़ना और रिपोर्ट साझा करना।
- **[AI एजेंट के लिए](https://github.com/VD171/VD-Infos/wiki/AI-AGENTS)** और **[निर्यात प्रारूप](https://github.com/VD171/VD-Infos/wiki/EXPORT-FORMAT)**: मशीन-पठनीय रिपोर्ट और उसका स्कीमा।
- **[आर्किटेक्चर](https://github.com/VD171/VD-Infos/wiki/ARCHITECTURE)** और **[बिल्डिंग](https://github.com/VD171/VD-Infos/wiki/BUILDING)**: यह कैसे काम करता है और इसे कैसे बिल्ड करें।
- **[गोपनीयता](https://github.com/VD171/VD-Infos/wiki/PRIVACY)**, **[सुरक्षा](https://github.com/VD171/VD-Infos/wiki/SECURITY)** और **[आचार संहिता](https://github.com/VD171/VD-Infos/wiki/CODE_OF_CONDUCT)**: गोपनीयता कथन और गवर्नेंस दस्तावेज़।
- **[कैटलॉग](CATALOG.md)**: रूट छिपाने और पहचान से जुड़े संदर्भ (गाइड, मॉड्यूल, फ्रेमवर्क और डिटेक्टर)।
- **[चेंजलॉग](CHANGELOG.md)**: हर रिलीज़ में क्या बदला।
- **[HMA-OSS](https://github.com/VD171/VD-Infos/wiki/HMA-OSS)**: ऐप-छिपाने वाला मॉड्यूल जिस पर इस ऐप के फिक्स आधारित हैं (इसके लिंक, चैनल और इसे समर्थन देने का तरीका)।
- **[योगदान](https://github.com/VD171/VD-Infos/wiki/CONTRIBUTING)**: पहचान लक्ष्य कैसे जोड़ें (ज़्यादातर सादा-पाठ सूचियाँ, Kotlin की ज़रूरत नहीं)।

<!-- /vd-docs -->

## यह भी देखें

उसी लेखक की सहोदर परियोजनाएँ:

- **[VDGoogle](https://github.com/VD171/VD-Google)**: आपकी Google सेवाएँ (RCS, Wallet, Play Integrity attestations), बिना इंटरनेट के आपके डिवाइस पर पढ़ी गईं।
- **[VDBackup](https://github.com/VD171/VD-Backup)**: root के रूप में पूरा ऐप और डेटा बैकअप और पुनर्स्थापन, पूरी तरह WebUI से संचालित।
- **[COPG-VD](https://github.com/VD171/COPG-VD)**: एक डिवाइस स्पूफर जो एक नए fingerprint को एक सुसंगत, समरूप प्रोफ़ाइल में बदल देता है।
- **[VD-dropbear](https://github.com/VD171/VD-dropbear)**: एक स्वतंत्र रूटेड SSH डेमॉन (Dropbear)।

Pixel `build.prop`: **[@VictorGabriel7Dev/build.prop](https://github.com/VictorGabriel7Dev/build.prop)** (fork of @T3SL4).

## Contacts

- https://vd171.ru
- https://vd.priv8.ru
- **Telegram:** @VD_Priv8 https://t.me/VD_Priv8
- **Channel:** @Priv8_VD https://t.me/Priv8_VD
- **Discord:** @VD.Priv8 https://discord.com/users/1296831918989639721
- **E-mail:** vd.priv8@pm.me
- **XDA-Developers:** @VD171 https://xdaforums.com/m/vd171.4699873/
- **XDA thread:** https://xdaforums.com/t/VD-Infos.4097379/
- **GitHub:** @VD171 https://github.com/VD171

## लाइसेंस

**GNU AGPL-3.0-or-later**: देखें [LICENSE](LICENSE)। कॉपीलेफ़्ट, नेटवर्क क्लॉज़ सहित: जो कोई संशोधित संस्करण चलाता है (सेवा के रूप में भी) उसे उसका स्रोत देना होगा। forks को खुला रखने के लिए एक शोध/एंटी-डिटेक्शन उपकरण हेतु जानबूझकर चुना गया।

## ट्रेडमार्क

VD Infos एक स्वतंत्र, अनौपचारिक शोध और डायग्नोस्टिक्स उपकरण है। यह उन किसी भी विक्रेता से संबद्ध, समर्थित या प्रायोजित नहीं है जिनके सॉफ़्टवेयर, सेवाओं या चिह्नों को यह पढ़ता, पहचानता या नाम देता है (Google LLC और Android Open Source Project सहित)। "Android", "Google", "Google Play", "Play Integrity", "Widevine" और अन्य सभी उत्पाद नाम, लोगो और ट्रेडमार्क अपने-अपने स्वामियों की संपत्ति हैं। VD Infos केवल वही पढ़ता है जो पहले से आपके डिवाइस पर मौजूद है और कहीं कुछ नहीं भेजता।

## Tags

`android` `privacy` `security` `root-detection` `hook-detection` `spoof-detection` `anti-detection` `tamper-detection` `device-fingerprint` `magisk` `kernelsu` `lsposed` `play-integrity` `attestation` `tee` `keystore` `scanner` `diagnostics` `reverse-engineering` `kotlin`
