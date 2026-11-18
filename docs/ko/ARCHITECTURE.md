> **Languages:** [English](../ARCHITECTURE.md) · [العربية](../ar/ARCHITECTURE.md) · [Čeština](../cs/ARCHITECTURE.md) · [Deutsch](../de/ARCHITECTURE.md) · [Español](../es/ARCHITECTURE.md) · [فارسی](../fa/ARCHITECTURE.md) · [Français](../fr/ARCHITECTURE.md) · [हिन्दी](../hi/ARCHITECTURE.md) · [Bahasa Indonesia](../id/ARCHITECTURE.md) · [Italiano](../it/ARCHITECTURE.md) · [日本語](../ja/ARCHITECTURE.md) · 한국어 · [Nederlands](../nl/ARCHITECTURE.md) · [Polski](../pl/ARCHITECTURE.md) · [Português](../pt/ARCHITECTURE.md) · [Русский](../ru/ARCHITECTURE.md) · [Svenska](../sv/ARCHITECTURE.md) · [ไทย](../th/ARCHITECTURE.md) · [Türkçe](../tr/ARCHITECTURE.md) · [Українська](../uk/ARCHITECTURE.md) · [Tiếng Việt](../vi/ARCHITECTURE.md) · [中文](../zh/ARCHITECTURE.md)

---

# 아키텍처

이것은 VD Infos가 어떻게 조립되어 있는지에 대한 큰 그림의 지도입니다. 앱이 읽는 것의 목록이
아니라 앱의 형태를 설명합니다. 메서드 디버거의 요점은 모든 읽은 값을 당신 앞에 놓는 것이며,
그것을 읽는 일은 당신 자신이 당신의 기기에서 할 몫입니다.

## 설계 목표

- **하나의 값을, 모든 메서드로.** 각 정보 조각에 대해 그것을 읽을 수 있는 모든 메서드를 통해
  읽고, 읽은 값을 늘어놓습니다. 디버거는 증거를 드러냅니다. 판단을 내리지 않습니다.
- **기기에서, 조용히.** 네트워크 없음, 계정 없음, 백그라운드 작업 없음. 앱은 열려 있는 동안
  에만 동작합니다.
- **기계가 읽을 수 있음.** 결과 전체가 하나의 기계 판독 가능 문서이므로, 도구는 사람과 같은
  그림을 얻습니다. [AI 에이전트](AI-AGENTS.md) 를 보세요.
- **중요한 곳은 작게.** 네이티브 컴포넌트는 의도적으로 아주 작고 단순하게 유지됩니다.

## 렌즈

*렌즈* 란 값을 읽는 하나의 방식입니다. 같은 값을 여러 렌즈로 읽어 비교할 수 있게 합니다.

| 렌즈 | 그것이 무엇인가 |
|---|---|
| **Java / SDK** | 아주 평범한 앱이 호출하는 플랫폼 API. |
| **Native / JNI** | 같은 값을 네이티브 코드에서 읽은 것. |
| **Shell via JVM** | JVM이 생성한 셸 명령. |
| **Shell via JNI** | 같은 명령을 네이티브 코드에서 생성한 것. |
| **TEE / Attestation** | 해당되는 경우의, 하드웨어로 뒷받침된 키 증명. |

렌즈를 늘어놓는 것이 전체 아이디어입니다. 앱은 읽기들이 일치하는 곳과 어긋나는 곳을 보여
줍니다. 그 차이가 어떤 값에 대해, 어떤 기기에서 무엇을 의미하는지는 당신이 읽을 몫입니다.

## 스캔이 어떻게 실행되는가

```
core/model      immutable domain: Lens, Category, Verdict, ProbeSpec, ProbeResult
probe/          the probes and the bridges they use (JNI, reflection, data lists)
engine/         ProbeEngine: parallel fan-out, streams results as they land
data/           SnapshotStore (persist the report), Exporter (share as JSON / text)
ui/             Jetpack Compose, Material 3, dynamic colour, live progress
cpp/            native_probes.cpp: the native lens, dependency-free
```

- **병렬성.** 프로브는 상한이 정해진 허가 수를 가진 디스패처에 걸쳐 펼쳐집니다. 결과는 완료될
  때마다 UI로 스트리밍되며, 진행 상태가 실시간으로 표시됩니다.
- **백그라운드에서는 아무것도 하지 않음.** 서비스도, 예약된 스캔도 없습니다. 앱은 열려 있는
  동안에만 읽고, 방치되면 스스로 닫힙니다.
- **네이티브 계층.** `RegisterNatives` 를 통해 이름으로 바인딩되는, 의존성 없는 하나의 작은
  `.so`. 의도적으로 아주 작고 단순하게 유지됩니다.

## 보고서

각 스캔은 하나의 `Snapshot` 을 생성합니다. 수행된 시각, 앱 버전, 그리고 결과 목록 (프로브마다
하나) 으로, 각 결과는 모든 렌즈에서 읽은 값과 그것들이 어떻게 관련되는지에 대한 판정을
가집니다. 앱은 각 스캔 뒤에 이것을 비공개 저장소에 보존하고, 같은 데이터를 내보낼 수 있습니다.
구조는 [내보내기 형식](EXPORT-FORMAT.md) 으로 문서화되어 있습니다.

## 확장하기

앱이 찾는 것의 대부분은 코드가 아니라 `assets/data/` 아래의 **평문 텍스트 목록** 으로
존재합니다. 대상을 더하거나 빼는 것은 한 줄을 편집하는 일입니다. [기여하기](CONTRIBUTING.md) 를
보세요.

## VD Infos가 결코 하지 않는 것

- 시스템의 아무것도 결코 바꾸지 않습니다. 읽고 비교할 뿐입니다.
- 네트워크와 결코 통신하지 않습니다. 완전히 오프라인으로 동작합니다.
- 결코 백그라운드에서 동작하거나, 스캔을 예약하거나, 서비스를 살려 두지 않습니다.
- 어떤 읽은 값도 어디로도 결코 전송하지 않습니다. 보고서가 기기를 떠나는 것은 당신이 공유하거나
  저장할 때뿐이며, 신원을 담은 값은 가려집니다.

<!-- doc-footer -->

---

> _이 문서는 Claude (Anthropic)로 생성되었으며 VD171이 유지 관리합니다. 수정 및 개선을 환영합니다. [CONTRIBUTING.md](CONTRIBUTING.md)를 참조하세요._
