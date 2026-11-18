> **Languages:** [English](../EXPORT-FORMAT.md) · [العربية](../ar/EXPORT-FORMAT.md) · [Čeština](../cs/EXPORT-FORMAT.md) · [Deutsch](../de/EXPORT-FORMAT.md) · [Español](../es/EXPORT-FORMAT.md) · [فارسی](../fa/EXPORT-FORMAT.md) · [Français](../fr/EXPORT-FORMAT.md) · [हिन्दी](../hi/EXPORT-FORMAT.md) · [Bahasa Indonesia](../id/EXPORT-FORMAT.md) · [Italiano](../it/EXPORT-FORMAT.md) · [日本語](../ja/EXPORT-FORMAT.md) · 한국어 · [Nederlands](../nl/EXPORT-FORMAT.md) · [Polski](../pl/EXPORT-FORMAT.md) · [Português](../pt/EXPORT-FORMAT.md) · [Русский](../ru/EXPORT-FORMAT.md) · [Svenska](../sv/EXPORT-FORMAT.md) · [ไทย](../th/EXPORT-FORMAT.md) · [Türkçe](../tr/EXPORT-FORMAT.md) · [Українська](../uk/EXPORT-FORMAT.md) · [Tiếng Việt](../vi/EXPORT-FORMAT.md) · [中文](../zh/EXPORT-FORMAT.md)

---

# 내보내기 형식

VD Infos가 내보내는 보고서 - 그리고 각 스캔 뒤에 기록하는 `last_snapshot.json` - 는 안정적이고
문서화된 형태를 가진 하나의 JSON 객체입니다. 이 페이지가 그 스키마입니다. 도구가 파싱할 수
있도록 구조를 설명합니다. 개별 프로브를 나열하거나 어떤 읽은 값을 해석해 주지는 않습니다.

## 최상위: 스냅샷

```json
{
  "takenAt": 1730000000000,
  "appVersion": "2.23",
  "results": [ /* ProbeResult objects */ ]
}
```

| 필드 | 형식 | 의미 |
|---|---|---|
| `takenAt` | 숫자 | 스캔이 수행된 시각. Unix 에포크 밀리초. |
| `appVersion` | 문자열 | 보고서를 생성한 앱 버전. |
| `results` | 배열 | 프로브마다 하나의 `ProbeResult`. 앱이 담고 있는 순서대로. |

## 하나의 결과

`results` 의 각 항목은 판정과 모든 렌즈의 읽은 값을 가진 하나의 프로브입니다.

```json
{
  "spec": { /* ProbeSpec */ },
  "values": [ /* LensValue objects */ ],
  "verdict": "MATCH",
  "detail": null
}
```

| 필드 | 형식 | 의미 |
|---|---|---|
| `spec` | 객체 | 무엇이 프로브되었는가 (아래 참조). |
| `values` | 배열 | 그것을 읽은 각 메서드마다 하나의 `LensValue`. |
| `verdict` | 문자열 | 읽은 값들이 어떻게 관련되는가 ([판정](#판정) 참조). |
| `detail` | 문자열 또는 null | 결과에 관한 선택적 메모. |

### spec (ProbeSpec)

```json
{
  "id": "identity:model",
  "title": "Model",
  "category": "IDENTITY",
  "lenses": ["JAVA", "NATIVE", "SHELL"],
  "note": null,
  "sensitive": false,
  "solution": null
}
```

| 필드 | 형식 | 의미 |
|---|---|---|
| `id` | 문자열 | 프로브의 안정적인 식별자. |
| `title` | 문자열 | 사람이 읽을 수 있는 이름. |
| `category` | 문자열 | [범주](#범주) 중 하나. |
| `lenses` | 배열 | 이 프로브가 사용하는 렌즈. |
| `note` | 문자열 또는 null | 선택적 맥락. |
| `sensitive` | 불리언 | 값이 신원을 담고 있을 때 `true`. 그러한 값은 **내보낸 보고서에서는 가려집니다** ([가리기](#가리기) 참조). |
| `solution` | 문자열 또는 null | 선택적 힌트 필드. 없거나 null일 수 있습니다. |

### values (LensValue)

```json
{
  "lens": "JAVA",
  "source": "Build.MODEL",
  "value": "Pixel 9",
  "error": null,
  "elapsedMicros": 42,
  "compare": true,
  "detail": null
}
```

| 필드 | 형식 | 의미 |
|---|---|---|
| `lens` | 문자열 | 어느 메서드가 읽었는가 ([렌즈](#렌즈) 참조). |
| `source` | 문자열 | 이 읽기 뒤에 있는 정확한 호출 또는 경로. |
| `value` | 문자열 또는 null | 그 메서드가 반환한 것. 아무것도 읽지 못했으면 `null`. |
| `error` | 문자열 또는 null | 메서드가 실패했을 때 존재합니다. |
| `elapsedMicros` | 숫자 | 읽기에 걸린 시간. 마이크로초 단위. |
| `compare` | 불리언 | 이 읽기가 판정에 참여하면 `true`. 정보용으로만 표시되면 `false`. |
| `detail` | 문자열 또는 null | 이 읽기에 관한 선택적 메모. |

## 열거형

### 판정

| 값 | 의미 |
|---|---|
| `MATCH` | 비교 가능한 모든 렌즈가 일치했다. |
| `MISMATCH` | 비교 가능한 렌즈들이 일치하지 않았다. |
| `SINGLE` | 렌즈 하나만 읽을 수 있었다. |
| `INFO` | 참고용으로 읽었고, 판정 대상이 아니다. |
| `EMPTY` | 어떤 렌즈도 값을 읽지 않았다. |
| `ERROR` | 값이 없고, 적어도 하나의 렌즈가 오류를 냈다. |

### 렌즈

| 값 | 레이블 |
|---|---|
| `JAVA` | Java / SDK |
| `NATIVE` | Native / JNI |
| `SHELL` | Shell via JVM |
| `SHELL_NATIVE` | Shell via JNI |
| `ATTEST` | TEE / Attestation |

### 범주

`IDENTITY`, `FINGERPRINT`, `TELEPHONY`, `NETWORK`, `LOCALE`, `BOOT`, `BUILD`,
`HARDWARE`, `DISPLAY`, `SENSORS`, `MEDIA`, `STORAGE`, `PROCESS`, `PACKAGES`,
`ACCOUNTS`, `WEBVIEW`, `SECURITY`, `INTEGRITY`, `EMULATOR`, `SYSTEM`.

## 가리기

`spec.sensitive` 가 `true` 일 때, 그 프로브의 읽은 값은 신원을 담은 값 (일련번호, IMEI,
MAC 주소, 광고 ID 등) 을 지니고 있습니다. **공유되거나 내보낸** 보고서에서는 그 값들과 그
세부 정보가, 공개된 곳에 붙여 넣은 보고서가 개인 식별자를 새어 나가게 하지 않도록, 가림
표식으로 대체됩니다. 프로브, 그 판정, 구조는 보존됩니다. 앱이나 루트만 읽을 수 있는
**기기 내** `last_snapshot.json` 은 실제 값을 유지합니다.

## 읽는 법

최소한의 파싱: `results` 를 순회하며 각 `verdict` 을 읽고, 전체 그림을 얻으려면 `values` 를
그 `lens` 별로 늘어놓습니다. 위의 형태는 릴리스 전반에 걸쳐 안정적입니다. 화면 위치가 아니라
이 필드 이름에 대해 파싱하세요.

기기에서 이 파일을 얻는 방법은 [AI 에이전트](AI-AGENTS.md) 를 보세요.

<!-- doc-footer -->

---

> _이 문서는 Claude (Anthropic)로 생성되었으며 VD171이 유지 관리합니다. 수정 및 개선을 환영합니다. [CONTRIBUTING.md](CONTRIBUTING.md)를 참조하세요._
