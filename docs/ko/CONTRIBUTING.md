> **Languages:** [English](../CONTRIBUTING.md) · [العربية](../ar/CONTRIBUTING.md) · [Čeština](../cs/CONTRIBUTING.md) · [Deutsch](../de/CONTRIBUTING.md) · [Español](../es/CONTRIBUTING.md) · [فارسی](../fa/CONTRIBUTING.md) · [Français](../fr/CONTRIBUTING.md) · [हिन्दी](../hi/CONTRIBUTING.md) · [Bahasa Indonesia](../id/CONTRIBUTING.md) · [Italiano](../it/CONTRIBUTING.md) · [日本語](../ja/CONTRIBUTING.md) · 한국어 · [Nederlands](../nl/CONTRIBUTING.md) · [Polski](../pl/CONTRIBUTING.md) · [Português](../pt/CONTRIBUTING.md) · [Русский](../ru/CONTRIBUTING.md) · [Svenska](../sv/CONTRIBUTING.md) · [ไทย](../th/CONTRIBUTING.md) · [Türkçe](../tr/CONTRIBUTING.md) · [Українська](../uk/CONTRIBUTING.md) · [Tiếng Việt](../vi/CONTRIBUTING.md) · [中文](../zh/CONTRIBUTING.md)

---

# 기여하기

VD Infos에 관심을 가져 주셔서 감사합니다. 기여를 환영합니다. 탐지 목록, 번역, 버그 신고,
문서, 코드. 대부분의 기여에는 Kotlin이 필요 없습니다.

## 가장 쉬운 기여: 목록

앱이 찾는 것의 대부분은 코드가 아니라
`VDInfos/app/src/main/assets/data/` 아래의 **평문 텍스트 목록** 으로 존재합니다. 대상을 더하거나
빼는 것은 한 줄을 편집하고 풀 리퀘스트를 여는 일입니다.

- `*_apps.txt` - 한 줄에 하나의 패키지 이름.
- `props.txt` - 시스템 속성 목록, `CATEGORY<tab>key`.
- `spoof_keys.txt` - 설정 행렬, `key:TYPE`.
- `known_certs.txt` - 서명 인증서 목록, `SHA-256<tab>owner` (실제 APK에서
  `apksigner verify --print-certs` 로 얻은 전체 해시).
- 그 밖의 `.txt` 파일 - 한 줄에 하나의 항목 (커널 모듈 이름 조각, 파일 이름 등).

`#` 으로 주석이 시작되고 빈 줄은 무시됩니다. 일부 파일은 한 줄에 공백 또는 탭으로 구분된 필드를
몇 개, 또는 선택적 표식을 담습니다. 파일에 이미 있는 줄의 형태를 유지하세요.

## 기본 규칙

- **VD Infos는 진실을 읽고 결코 꾸며 내지 않습니다.** 각 메서드가 반환한 것을 그대로 정확히
  보고합니다. 보기 좋게 하려고 읽은 값을 누그러뜨리지 말고, 값을 읽는 것이 아니라 바꾸는
  무언가를 더하지 마세요.
- **프라이버시는 절대적입니다.** 모든 것이 기기에서 동작합니다. 네트워크 호출이나, 읽은 값을
  기기 밖으로 전송하는 것을 결코 더하지 마세요.
- **내보내기는 가려진 채로 둡니다.** 신원을 담은 값은 공유되는 보고서에서 가려집니다. 그 상태를
  유지하세요.

## 번역

번역은 값진 기여입니다. readme는 언어별로 (`README.<lang>.md`), 앱 문자열은 언어별로, 그리고
이 문서는 `docs/<lang>/` 아래로 번역됩니다. 한 언어 안에서 용어를 일관되게 하고, 기존 어조에
맞추며, 확신이 없는 기술 용어를 결코 기계 번역하지 마세요.

## 버그 신고

기기 모델, Android 버전, 그리고 읽기에 관한 것이라면 공유된 **내보내기** (그 신원을 담은 값은
가려집니다) 를 담아 이슈를 여세요. 내보내기는 바로 이를 위해 만들어졌습니다.

## 코드

- release를 빌드하고, 결코 debug를 빌드하지 마세요. 실제 기기에서 테스트하세요.
  [빌드](BUILDING.md) 를 보세요.
- 코드에서 주석을 빼세요. 이름이 의도를 설명합니다. 유일한 헤더는 각 소스 파일 맨 위의 라이선스
  배너입니다.
- 기존 스타일에 맞추고 변경을 집중하세요.

## 라이선스

기여함으로써, 당신의 기여가 프로젝트의 **GNU AGPL-3.0-or-later** 아래 라이선스된다는 데
동의하는 것입니다.

<!-- doc-footer -->

---

> _이 문서는 Claude (Anthropic)로 생성되었으며 VD171이 유지 관리합니다. 수정 및 개선을 환영합니다. [CONTRIBUTING.md](CONTRIBUTING.md)를 참조하세요._
