[English](README.md) | [Português](README.pt.md) | [Español](README.es.md) | [Italiano](README.it.md) | [Deutsch](README.de.md) | [Français](README.fr.md) | [Русский](README.ru.md) | [Bahasa Indonesia](README.id.md) | [Türkçe](README.tr.md) | [Polski](README.pl.md) | [Nederlands](README.nl.md) | [Svenska](README.sv.md) | [Čeština](README.cs.md) | [Tiếng Việt](README.vi.md) | [中文](README.zh.md) | [日本語](README.ja.md) | **한국어** | [فارسی](README.fa.md) | [हिन्दी](README.hi.md) | [العربية](README.ar.md) | [ไทย](README.th.md)

# VD Infos

*메서드 디버거*

[![License: AGPL-3.0-or-later](https://img.shields.io/badge/license-AGPL--3.0--or--later-blue.svg)](LICENSE)
[![Release](https://img.shields.io/github/v/release/VD171/VD-Infos)](https://github.com/VD171/VD-Infos/releases)
[![Downloads](https://img.shields.io/github/downloads/VD171/VD-Infos/total)](https://github.com/VD171/VD-Infos/releases)
[![Android 8.0+](https://img.shields.io/badge/Android-8.0%2B-3DDC84.svg)](https://github.com/VD171/VD-Infos/releases)
[![Languages](https://img.shields.io/badge/languages-21-orange.svg)](README.md)

<img src="images/vdinfos-01.png" height="420"/> <img src="images/vdinfos-02.png" height="420"/>

Android는 매우 강력하고 다재다능한 운영체제이지만, 아무도 말해주지 않는 것은 당신의 모든 개인 정보와 기밀 정보가 설치하는 모든 앱에 노출되어 있다는 점이며, 이런 사생활 침해로부터 스스로를 보호하는 것은 의무입니다. VD Infos는 당신의 기기에서 무엇이 수집될 수 있는지 예시를 보여주며, 이를 *메서드 디버거*로 수행합니다. 각 정보에 대해 **그것을 읽을 수 있는 모든 방법**으로 값을 읽습니다 - `Build.*`, `SystemProperties`, `getprop`, 네이티브 `__system_property_get`, 시스템 매니저, content provider, 파일, syscall, 하드웨어 키 attestation(TEE) - 그리고 비교할 수 있도록 나란히 정렬합니다. 한 방법이 다른 방법과 어긋날 때, 그 사이의 무언가가 그 표면을 다시 쓰고 있는 것입니다. hook 프레임워크, spoofer, resolver shim입니다. **어떤 정보도 저장, 전송되거나 어떤 파일이나 서버로도 전달되지 않습니다** - 모든 것은 기기에서 실행되고, 신원을 담은 값은 당신이 드러내기 전까지 가려지며, 보고서는 당신이 명시적으로 공유하거나 저장할 때만 기기를 떠납니다. 원한다면 방화벽으로 인터넷 접근을 차단하거나 그냥 꺼두세요.

## 무엇을 검사하는가

각 항목은 그것을 읽을 수 있는 모든 방법(Java SDK / 네이티브 / shell)으로 읽혀 나란히 비교됩니다:

* **약 488개의 시스템 속성**을 다섯 가지 방식으로 읽습니다: `SystemProperties.get`, bionic의 두 진입점(`__system_property_read_callback`과 92바이트 `__system_property_get`), JVM이 실행하는 `getprop`, 그리고 네이티브 코드에서 `popen`으로 실행하는 `getprop` - `ProcessBuilder`가 실행한 shell 명령과 JVM 없이 실행한 같은 명령은 동일한 관점이 아닙니다. 전자는 root 은닉 프레임워크가 다시 쓰는 표면이고 후자는 아니기 때문입니다.
* **기기 신원**: 모델, 제조사, 브랜드, device, product, board, hardware, fingerprint, bootloader, build id/tags/type - 각 `Build.*` 필드를 그 모든 `ro.product.*`(system/vendor/odm) 변형, 네이티브, shell과 대조.
* **식별자**: serial(여러 게터), Android ID(settings와 provider), GSF ID, advertising ID, IMEI/primary IMEI/MEID/IMSI/ICCID, user serial.
* **전화**: TelephonyManager 표면(통신사, SIM, 네트워크, 로밍…), SubscriptionManager(멀티 SIM), cell info.
* **네트워크**: 인터페이스별 **MAC**(`NetworkInterface` vs `/sys/class/net` vs shell), Wi-Fi(SSID/BSSID/IP/MAC), 블루투스, DNS.
* **커널 / 프로세스 / 시스템**: `uname`, boot id, uptime, UID/PID, SELinux, 시간대, locale, 환경, 실행 중인 서비스/프로세스.
* **하드웨어 / 미디어**: CPU, 메모리, 센서, 디스플레이, 카메라, system features, GPU, Widevine DRM ID.
* **패키지 / 계정 / WebView**: 설치된 패키지 목록과 수, 앱별 서명 다이제스트, 이 앱의 서명, 자체 패키지 자기 성찰, intent 해석 열거, 계정, 통화 기록 수, user agent.
* **샌드박스 준수**: 샌드박스가 **닫아야** 하는 표면(eMMC CID/serial, UFS 및 SoC 신원, `/proc/cmdline`, `/proc/1/*`, `/dev/kmsg`, `/system/build.prop`)과 22개의 `dumpsys` 서비스. 여기서 거부가 곧 답입니다. `EACCES`/`DENIED`가 준수하는 읽기이며, 대신 내용을 넘겨주는 기기는 준수하지 않습니다.
* **광고 식별자**: Play Services 바인더의 GAID와 함께, 키 계열 전체(AAID, OAID/VAID, 화웨이의 `pps_*`)를 세 개의 settings store에서 훑습니다.
* **Root / hook / 에뮬레이터 탐지기**: `su`/Magisk/KernelSU/APatch/Xposed/LSPosed/Riru의 흔적, `/proc/self/maps`에 주입된 라이브러리, 알려진 패키지, 위험 속성과 에뮬레이터 속성, verified boot state.

## 다운로드 및 지원

릴리스마다 두 개의 APK를 게시합니다. **SDK_35** 는 엄격한 최신 샌드박스를 대상으로 하며 이것을 사용하세요. **SDK_27** 은 더 낮은 API 레벨을 대상으로 하여 SELinux 도메인이 느슨하므로 비교에 유용합니다. 둘 다 Android 8.0 이상에 설치됩니다.

* Release (APK): https://github.com/VD171/VD-Infos/releases
* https://github.com/VD171/VD-Infos
* https://xdaforums.com/t/VD-Infos.4097379/
* https://t.me/RootDetected
* https://t.me/BlankAssistance

## 언어

21개의 UI 언어: 영어, 포르투갈어, 스페인어, 이탈리아어, 독일어, 프랑스어, 러시아어, 인도네시아어, 터키어, 폴란드어, 네덜란드어, 스웨덴어, 체코어, 베트남어, 중국어, 일본어, 한국어, 페르시아어, 힌디어, 아랍어, 태국어. 앱은 첫 실행 시 일회성 언어 선택을 제공하며("시스템 기본값" 옵션 포함), 그 외에는 시스템 언어를 따릅니다.

## 아키텍처

```
core/model      불변 도메인: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          NativeBridge (JNI) - SystemProps (리플렉션) - PropCatalog (데이터)
                SemanticProbes - IntegrityProbes - ProbeRegistry
engine/         ProbeEngine - 코루틴 팬아웃, 결과를 Flow로 스트리밍
data/           SnapshotStore (영속화 + diff) - Exporter (JSON/텍스트 공유)
ui/             Jetpack Compose, Material 3, 동적 색상, 실시간 진행
cpp/            native_probes.cpp - 네이티브 렌즈, 의존성 없음
```

* **병렬성**: 922개의 프로브가 제한된 permit 수로 기본 dispatcher에 펼쳐지며, 결과는 도착하는 대로 UI로 흘러듭니다.
* **백그라운드에서 아무것도 하지 않음**: 서비스도 예약된 스캔도 없습니다. 앱은 열려 있는 동안에만 실행되고 방치되면 스스로 닫힙니다.
* **네이티브 계층**: `RegisterNatives`로 이름에 의해 바인딩되는 작은 `.so`. 속이기 어려워야 하는 부분이기에 의도적으로 아주 작게 유지합니다.

## 기여하기

대부분의 기여에는 Kotlin이 필요 없습니다. 목록은 `VDInfos/app/src/main/assets/data/` 아래에 일반 텍스트로 있습니다. 한 줄을 추가하거나 제거하고 pull request를 열면 됩니다.

* `*_apps.txt` - 한 줄에 패키지 이름 하나
* `props.txt` - 시스템 속성 카탈로그, `카테고리<tab>키`
* `spoof_keys.txt` - settings spoof 행렬, `키:타입`
* 기타 `.txt` 파일 - 마찬가지로 한 줄에 하나의 항목(커널 모듈 이름 조각, `/data/local/tmp` 파일 이름)

`#` 는 주석을 시작하며 빈 줄은 무시됩니다. 코드 기여도 환영합니다. 기여하면 귀하의 작업이 이 프로젝트의 AGPL-3.0-or-later 로 배포되는 데 동의하는 것입니다.

## 카탈로그

root 은닉 및 탐지 참고자료(가이드, 모듈, 프레임워크, 탐지기)는 별도 카탈로그에 있습니다: [CATALOG.md](CATALOG.md).

## 감사의 말

**[HMA-OSS](https://github.com/frknkrc44/HMA-OSS)** 의 개발자 **[frknkrc44](https://github.com/frknkrc44)** 님이 쏟은 정성에 특별히 감사드립니다. HMA-OSS는 이 앱이 제안하는 여러 수정이 적용되는 곳입니다 - 대상 앱 숨기기와 spoof 프리셋 설정 - 그리고 앱 내의 모든 언급은 그 소스로 다시 링크됩니다.

링크, 채널, 후원 방법: [HMA-OSS.md](HMA-OSS.md).

## 연락처

* https://vd171.ru
* https://vd.priv8.ru
* **Telegram:** @VD_Priv8 https://t.me/VD_Priv8
* **Discord:** @VD.Priv8 https://discord.com/users/1296831918989639721
* **이메일:** vd.priv8@pm.me
* **XDA-Developers:** @VD171 https://xdaforums.com/m/vd171.4699873/
* **GitHub:** @VD171 https://github.com/VD171

## 라이선스

**GNU AGPL-3.0-or-later** - [LICENSE](LICENSE) 참조. 네트워크 조항을 포함한 카피레프트: 수정본을 (서비스로라도) 실행하는 누구든 그 소스를 제공해야 합니다. fork를 열린 상태로 유지하기 위해 연구/반탐지 도구용으로 의도적으로 선택했습니다.

---

`android` `privacy` `security` `root-detection` `hook-detection` `spoof-detection` `anti-detection` `tamper-detection` `device-fingerprint` `magisk` `kernelsu` `lsposed` `play-integrity` `attestation` `tee` `keystore` `scanner` `diagnostics` `reverse-engineering` `kotlin`
