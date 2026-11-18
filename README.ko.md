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
  <b>기기 메서드 디버거.</b><br>
  기기의 모든 값을 그것을 읽을 수 있는 모든 방법으로 읽어, 비교할 수 있도록 나란히 정렬합니다.
</p>

<p align="center">
  <a href="https://github.com/VD171/VD-Infos/releases">Releases</a> ·
  <a href="https://github.com/VD171/VD-Infos/wiki">Wiki</a> ·
  <a href="https://github.com/VD171/VD-Infos/wiki/USER-GUIDE">사용자 가이드</a> ·
  <a href="https://github.com/VD171/VD-Infos/wiki/AI-AGENTS">AI 에이전트</a> ·
  <a href="https://github.com/VD171/VD-Infos/wiki/EXPORT-FORMAT">내보내기 형식</a> ·
  <a href="https://github.com/VD171/VD-Infos/wiki/ARCHITECTURE">아키텍처</a> ·
  <a href="CATALOG.md">카탈로그</a> ·
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

Android는 매우 강력하고 다재다능한 운영체제이지만, 아무도 말해주지 않는 것은 당신의 모든 개인 정보와 기밀 정보가 설치하는 모든 앱에 노출되어 있다는 점이며, 이런 사생활 침해로부터 스스로를 보호하는 것은 의무입니다. VD Infos는 당신의 기기에서 무엇이 수집될 수 있는지 예시를 보여주며, 이를 *메서드 디버거*로 수행합니다. 각 정보에 대해 **그것을 읽을 수 있는 모든 방법**으로 값을 읽습니다: 프레임워크 API, 네이티브 인터페이스, 셸 명령, 시스템 매니저, content provider, 파일, syscall, 하드웨어 키 attestation(TEE), 그리고 비교할 수 있도록 나란히 정렬합니다. 한 읽기가 다른 읽기와 어긋날 때, 그 사이의 무언가가 그 표면을 다시 쓰고 있는 것입니다. hook 프레임워크, spoofer, resolver shim입니다. **어떤 정보도 저장, 전송되거나 어떤 파일¹이나 서버로도 전달되지 않습니다**: 모든 것은 기기에서 실행되고, 신원을 담은 값은 당신이 드러내기 전까지 가려지며, 보고서는 당신이 명시적으로 공유하거나 저장할 때만 기기를 떠납니다. 원한다면 방화벽으로 인터넷 접근을 차단하거나 그냥 꺼두세요.

> **¹ 예외:** 각 스캔은 전체 보고서를 앱의 비공개 저장소(`files/last_snapshot.json`)에 보관합니다. 앱 자신이나 root만 읽을 수 있고, 다음 스캔이 덮어쓰며, 기기를 벗어나지 않습니다. AI 에이전트가 전체 결과를 읽을 수 있도록 존재합니다. 참고: **AI 에이전트용: JSON으로 된 전체 보고서**.

## 무엇을 검사하는가

각 항목은 그것을 읽을 수 있는 모든 방법(Java SDK / 네이티브 / shell)으로 읽혀 나란히 비교됩니다:

* **약 488개의 시스템 속성**을 여러 독립적인 경로로 읽습니다: Java API, 네이티브 속성 인터페이스, 그리고 서로 다른 방식으로 도달하는 `getprop` shell 명령. 그런 다음 나란히 정렬하여 비교합니다. 이 경로들이 모두 같은 계층을 거치지는 않으므로, 한 경로에서는 다시 쓰였지만 다른 경로에서는 그렇지 않은 값은 불일치로 드러납니다.
* **기기 신원**: 모델, 제조사, 브랜드, device, product, board, hardware, fingerprint, bootloader, build id/tags/type, 각 `Build.*` 필드를 그 모든 `ro.product.*`(system/vendor/odm) 변형, 네이티브, shell과 대조.
* **식별자**: serial(여러 게터), Android ID(settings와 provider), GSF ID, advertising ID, IMEI/primary IMEI/MEID/IMSI/ICCID, user serial.
* **전화**: TelephonyManager 표면(통신사, SIM, 네트워크, 로밍…), SubscriptionManager(멀티 SIM), cell info.
* **네트워크**: 인터페이스별 **MAC**(`NetworkInterface` vs `/sys/class/net` vs shell), Wi-Fi(SSID/BSSID/IP/MAC), 블루투스, DNS.
* **커널 / 프로세스 / 시스템**: `uname`, boot id, uptime, UID/PID, SELinux, 시간대, locale, 환경, 실행 중인 서비스/프로세스.
* **하드웨어 / 미디어**: CPU, 메모리, 센서, 디스플레이, 카메라, system features, GPU, Widevine DRM ID.
* **패키지 / 계정 / WebView**: 설치된 패키지 목록과 수, 앱별 서명 다이제스트, 이 앱의 서명, 자체 패키지 자기 성찰, intent 해석 열거, 계정, 통화 기록 수, user agent.
* **샌드박스 준수**: 적절히 샌드박스된 앱이 도달할 수 없어야 하는 저수준 신원 및 커널 표면과 일련의 `dumpsys` 서비스. 이들에 대해 앱은 접근이 올바르게 거부되는지 확인합니다. 도달할 수 없어야 하는데 도달 가능한 표면은 검토 대상으로 표시됩니다.
* **광고 식별자**: Play Services 바인더의 GAID와 함께, 키 계열 전체(AAID, OAID/VAID, 화웨이의 `pps_*`)를 세 개의 settings store에서 훑습니다.
* **Root / hook / 에뮬레이터 탐지기**: 일반적인 root 매니저, hook 프레임워크, 에뮬레이터(Magisk, KernelSU, APatch, Xposed/LSPosed, Riru 등)가 남기는 흔적과 신호, 그리고 verified boot state.

## 다운로드 및 지원

릴리스마다 두 개의 APK를 게시합니다. **SDK_35** 는 엄격한 최신 샌드박스를 대상으로 하며 이것을 사용하세요. **SDK_27** 은 더 낮은 API 레벨을 대상으로 하여 비교에 유용합니다. 둘 다 Android 8.0 이상에 설치됩니다.

* Release (APK): https://github.com/VD171/VD-Infos/releases
* https://github.com/VD171/VD-Infos
* https://xdaforums.com/t/VD-Infos.4097379/
* https://t.me/RootDetected
* https://t.me/BlankAssistance

## AI 에이전트용: JSON으로 된 전체 보고서

셸을 통해 기기에서 작업하는 AI 에이전트와 LLM 도구(코딩 어시스턴트, 분석 스크립트)를 위한 기능입니다. 스크린샷을 읽는 대신, 에이전트는 명령 하나로 전체 결과를 읽습니다:

```sh
su -c cat /data/data/ru.vd171.vdinfos/files/last_snapshot.json
```

* **내용:** 각 프로브의 판정과 모든 렌즈의 값이며, 내보내기 버튼과 같은 JSON입니다. 화면은 접힌 항목의 첫 번째 읽기만 보여 주지만, 파일에는 모두 들어 있습니다.
* **갱신 시점:** 각 스캔이 끝날 때이므로 먼저 스캔을 실행하세요. 파일에는 항상 최신 결과가 들어 있습니다.
* **요구 사항:** root. 파일이 앱의 비공개 저장소에 있기 때문입니다.

## 아키텍처

```
core/model      불변 도메인: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          NativeBridge (JNI), SystemProps (리플렉션), PropCatalog (데이터)
                SemanticProbes, IntegrityProbes, ProbeRegistry
engine/         ProbeEngine: 코루틴 팬아웃, 결과를 Flow로 스트리밍
data/           SnapshotStore (영속화 + diff), Exporter (JSON/텍스트 공유)
ui/             Jetpack Compose, Material 3, 동적 색상, 실시간 진행
cpp/            native_probes.cpp: 네이티브 렌즈, 의존성 없음
```

* **병렬성**: 996개의 프로브가 제한된 permit 수로 기본 dispatcher에 펼쳐지며, 결과는 도착하는 대로 UI로 흘러듭니다.
* **백그라운드에서 아무것도 하지 않음**: 서비스도 예약된 스캔도 없습니다. 앱은 열려 있는 동안에만 실행되고 방치되면 스스로 닫힙니다.
* **네이티브 계층**: `RegisterNatives`로 이름에 의해 바인딩되는, 의존성 없는 작은 `.so`. 의도적으로 아주 작고 단순하게 유지합니다.

## 기여하기

대부분의 기여에는 Kotlin이 필요 없습니다. 목록은 `VDInfos/app/src/main/assets/data/` 아래에 일반 텍스트로 있습니다. 한 줄을 추가하거나 제거하고 pull request를 열면 됩니다.

* `*_apps.txt`: 한 줄에 패키지 이름 하나
* `props.txt`: 시스템 속성 카탈로그, `카테고리<tab>키`
* `spoof_keys.txt`: settings spoof 행렬, `키:타입`
* `known_certs.txt`: 서명 인증서 카탈로그, `SHA-256<tab>소유자` (전체 해시, 실제 APK에서 `apksigner verify --print-certs`로 추출)
* 기타 `.txt` 파일: 마찬가지로 한 줄에 하나의 항목(커널 모듈 이름 조각, `/data/local/tmp` 파일 이름)

`#` 는 주석을 시작하며 빈 줄은 무시됩니다. 일부 파일은 한 줄에 공백 또는 탭으로 구분된 여러 필드나 선택적 표시(`props.txt` 의 `S` 와 `@field`)를 담고 있습니다. 파일에 이미 있는 줄의 형식을 유지하세요. 코드 기여도 환영합니다. 기여하면 귀하의 작업이 이 프로젝트의 AGPL-3.0-or-later 로 배포되는 데 동의하는 것입니다.

## 카탈로그

root 은닉 및 탐지 참고자료(가이드, 모듈, 프레임워크, 탐지기)는 별도 카탈로그에 있습니다: [CATALOG.md](CATALOG.md).

## 감사의 말

**[HMA-OSS](https://github.com/frknkrc44/HMA-OSS)** 의 개발자 **[frknkrc44](https://github.com/frknkrc44)** 님이 쏟은 정성에 특별히 감사드립니다. HMA-OSS는 이 앱이 제안하는 여러 수정이 적용되는 곳입니다 (대상 앱 숨기기와 spoof 프리셋 설정), 그리고 앱 내의 모든 언급은 그 소스로 다시 링크됩니다.

링크, 채널, 후원 방법: [HMA-OSS.md](HMA-OSS.md).

## 문서

- **[Wiki](https://github.com/VD171/VD-Infos/wiki)**: 모든 문서, 22개 언어로 온라인에서 볼 수 있습니다.
- **[사용자 가이드](https://github.com/VD171/VD-Infos/wiki/USER-GUIDE)**: 설치, 스캔 실행, 결과 읽기, 보고서 공유.
- **[AI 에이전트용](https://github.com/VD171/VD-Infos/wiki/AI-AGENTS)** 과 **[내보내기 형식](https://github.com/VD171/VD-Infos/wiki/EXPORT-FORMAT)**: 기계가 읽을 수 있는 보고서와 그 스키마.
- **[아키텍처](https://github.com/VD171/VD-Infos/wiki/ARCHITECTURE)** 와 **[빌드](https://github.com/VD171/VD-Infos/wiki/BUILDING)**: 작동 방식과 빌드 방법.
- **[프라이버시](https://github.com/VD171/VD-Infos/wiki/PRIVACY)**, **[보안](https://github.com/VD171/VD-Infos/wiki/SECURITY)**, **[행동 강령](https://github.com/VD171/VD-Infos/wiki/CODE_OF_CONDUCT)**: 개인정보 보호정책과 거버넌스 문서.
- **[카탈로그](CATALOG.md)**: 루트 숨김과 탐지 관련 참고 자료 (가이드, 모듈, 프레임워크, 탐지기).
- **[변경 로그](CHANGELOG.md)**: 각 릴리스에서 바뀐 내용.
- **[HMA-OSS](https://github.com/VD171/VD-Infos/wiki/HMA-OSS)**: 이 앱의 수정이 기반으로 삼는 앱 숨김 모듈 (그 링크, 채널, 후원 방법).
- **[기여하기](https://github.com/VD171/VD-Infos/wiki/CONTRIBUTING)**: 탐지 대상을 추가하는 방법(대부분 일반 텍스트 목록이며 Kotlin은 필요 없음).

<!-- /vd-docs -->

## 관련 앱

같은 저자가 만든 자매 프로젝트:

- **[VDGoogle](https://github.com/VD171/VD-Google)**: 당신의 Google 서비스(RCS, Wallet, Play Integrity attestation)를 인터넷 없이 기기에서 읽습니다.
- **[VDBackup](https://github.com/VD171/VD-Backup)**: 앱과 데이터의 전체 백업과 복원을 root로 수행하며, 전부 WebUI에서 조작합니다.
- **[COPG-VD](https://github.com/VD171/COPG-VD)**: 새 fingerprint를 일관되고 정합적인 profile로 바꾸는 기기 스푸퍼.
- **[VD-dropbear](https://github.com/VD171/VD-dropbear)**: 독립적으로 동작하는 root 권한 SSH 데몬 (Dropbear).

Pixel `build.prop`: **[@VictorGabriel7Dev/build.prop](https://github.com/VictorGabriel7Dev/build.prop)** (fork of @T3SL4).

## 연락처

- https://vd171.ru
- https://vd.priv8.ru
- **Telegram:** @VD_Priv8 https://t.me/VD_Priv8
- **Channel:** @Priv8_VD https://t.me/Priv8_VD
- **Discord:** @VD.Priv8 https://discord.com/users/1296831918989639721
- **E-mail:** vd.priv8@pm.me
- **XDA-Developers:** @VD171 https://xdaforums.com/m/vd171.4699873/
- **XDA thread:** https://xdaforums.com/t/VD-Infos.4097379/
- **GitHub:** @VD171 https://github.com/VD171

## 라이선스

**GNU AGPL-3.0-or-later**: [LICENSE](LICENSE) 참조. 네트워크 조항을 포함한 카피레프트: 수정본을 (서비스로라도) 실행하는 누구든 그 소스를 제공해야 합니다. fork를 열린 상태로 유지하기 위해 연구/반탐지 도구용으로 의도적으로 선택했습니다.

## 상표

VD Infos는 독립적이고 비공식적인 연구 및 진단 도구입니다. 읽거나 탐지하거나 이름을 거론하는 소프트웨어, 서비스, 표장의 어떤 공급업체(Google LLC 및 Android Open Source Project 포함)와도 제휴, 보증, 후원 관계가 없습니다. "Android", "Google", "Google Play", "Play Integrity", "Widevine" 및 기타 모든 제품명, 로고, 상표는 각 소유자의 재산입니다. VD Infos는 당신의 기기에 이미 있는 것을 읽을 뿐이며 어디에도 아무것도 보내지 않습니다.

## Tags

`android` `privacy` `security` `root-detection` `hook-detection` `spoof-detection` `anti-detection` `tamper-detection` `device-fingerprint` `magisk` `kernelsu` `lsposed` `play-integrity` `attestation` `tee` `keystore` `scanner` `diagnostics` `reverse-engineering` `kotlin`
