> **Languages:** [English](../SECURITY.md) · [العربية](../ar/SECURITY.md) · [Čeština](../cs/SECURITY.md) · [Deutsch](../de/SECURITY.md) · [Español](../es/SECURITY.md) · [فارسی](../fa/SECURITY.md) · [Français](../fr/SECURITY.md) · [हिन्दी](../hi/SECURITY.md) · [Bahasa Indonesia](../id/SECURITY.md) · [Italiano](../it/SECURITY.md) · [日本語](../ja/SECURITY.md) · 한국어 · [Nederlands](../nl/SECURITY.md) · [Polski](../pl/SECURITY.md) · [Português](../pt/SECURITY.md) · [Русский](../ru/SECURITY.md) · [Svenska](../sv/SECURITY.md) · [ไทย](../th/SECURITY.md) · [Türkçe](../tr/SECURITY.md) · [Українська](../uk/SECURITY.md) · [Tiếng Việt](../vi/SECURITY.md) · [中文](../zh/SECURITY.md)

---

# 보안 정책

VD Infos는 민감한 기기 상태를 읽는 프라이버시 도구이므로, 보안 신고는 진지하게 받아들여집니다.

## 취약점 신고

보안 문제는 공개 이슈가 아니라 **비공개로** 신고해 주세요.

- **vd.priv8@pm.me** 로 이메일을 보내세요.
- 문제, 영향받는 버전, 그리고 재현 방법을 설명해 주세요.
- 데이터 노출에 관한 것이라면 문제의 유형을 설명해 주세요. 동작하는 익스플로잇을 첨부할 필요는
  없습니다.

가능한 한 빨리 답장을 드리겠습니다. 공개하기 전에 문제를 수정할 합리적인 기간을 주세요.

## 무엇이 보안 문제에 해당하는가

VD Infos가 약속하는 바 때문에, 다음은 특히 중요합니다.

- 앱이 네트워크에 도달하거나, 어떤 읽은 값을 기기 밖으로 전송하게 만드는 것.
- 가리기에도 불구하고, 신원을 담은 값 (일련번호, IMEI, MAC 주소, 광고 ID 등) 을 공유되거나
  저장된 **내보내기** 로 새어 나가게 하는 것.
- 앱 자신이나 루트만 읽을 수 있어야 하는 비공개 보고서 파일을 다른 앱이 읽게 하는 것.

## 무엇이 보안 문제가 아닌가

- "잘못" 보이지만 어떤 메서드가 반환한 정직한 값인 읽은 값. VD Infos는 각 메서드가 말한 것을
  보고합니다. 비교를 읽는 일은 당신이 할 몫입니다. [사용자 안내서](USER-GUIDE.md) 를 보세요.
- 사용 불가로 표시된 읽기. 그것은 어떤 메서드가 당신의 기기에서 그 값을 읽을 수 없었다는 뜻이며,
  그것 자체가 비교의 일부입니다.

## 지원되는 버전

최신 릴리스 버전만 지원됩니다. 신고하기 전에 최신 빌드에서 재현해 주세요.

<!-- doc-footer -->

---

> _이 문서는 Claude (Anthropic)로 생성되었으며 VD171이 유지 관리합니다. 수정 및 개선을 환영합니다. [CONTRIBUTING.md](CONTRIBUTING.md)를 참조하세요._
