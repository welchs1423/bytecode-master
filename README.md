# 🚀 바이트코드의 지배자 (Bytecode Master)

> **JVM의 심장부를 관통하는 바이트코드 조작 엔진: Java Agent와 ByteBuddy를 이용한 런타임 최적화 탐구**

### 🏗️ 프로젝트 구축 내역 (Development Milestone)

#### 🚀 최신 성과
- **[2026-02-15] 런타임 인터셉트 최종 검증 성공**: `@Timer` 애노테이션 기반 정밀 타겟팅을 통해 `work()` 메서드의 실행 성능(500ms)을 정확히 측정함. [cite: 2026-02-15]
- **[2026-02-15] 리스너 최적화 및 가독성 확보**: Byte Buddy 상세 로그를 비활성화하여 비즈니스 로그(`work` 실행 시간)만 깔끔하게 출력하도록 개선. [cite: 2026-02-15]
- **[2026-02-15] 저장소 정화 및 영구 방지 대책**: `git rm -r --cached`를 통한 인덱스 초기화 및 `.gitignore` 재설정으로 불필요한 바이너리 유입 완벽 차단. [cite: 2026-02-15]

<details>
<summary>👈 <strong>지난 구축 내역 펼쳐보기 (Click)</strong></summary>

#### [2026-02-15] 커스텀 애노테이션 설계
- 일반 `interface`와 애노테이션 전용 **`@interface`**의 문법적 차이를 학습하고 적용. [cite: 2026-02-15]

#### [2026-02-15] Java Agent 골격 및 진입점 설계
- `AgentLauncher`: `premain` 진입점을 통해 JVM 런타임에 동적으로 로직을 주입하는 기반 마련. [cite: 2026-02-15]
</details>

---

### 🔍 트러블슈팅 로그 (Troubleshooting)

<details>
<summary>👈 <strong>전체 트러블슈팅 로그 펼쳐보기 (Click)</strong></summary>

| 날짜 | 이슈 내용 | 원인 분석 및 해결 방안 |
| :--- | :--- | :--- |
| **02-15** | **끈질긴 바이너리 재발** | 캐시된 인덱스로 인해 `.gitignore` 무시 현상 -> `git rm -r --cached .`으로 인덱스 완전 초기화. [cite: 2026-02-15] |
| **02-15** | **로그 출력 누락(Execution)** | `TargetApp` 내 메서드 호출 로직 부재 -> `new TargetApp().work()` 호출부 복구. [cite: 2026-02-15] |
| **02-15** | **상세 로그 과다 출력** | `StreamWriting` 리스너 활성화로 인한 가독성 저하 -> 검증 완료 후 해당 코드 주석 처리. [cite: 2026-02-15] |
| **02-15** | **`@interface` 문법 오류** | `Timer`를 `class`로 잘못 정의하여 애노테이션으로 인식 불가 -> `@interface` 키워드로 수정. [cite: 2026-02-15] |
| **02-15** | **Java 25 호환성 이슈** | Byte Buddy 버전과 최신 JVM 간 버전 불일치 -> `-Dnet.bytebuddy.experimental=true` 옵션 적용. [cite: 2026-02-15] |
| **02-15** | **`listener` 심볼 오류** | 변수 선언 누락 및 스태틱 임포트 미비 -> `AgentBuilder.Listener` 관련 객체 명시적 생성. [cite: 2026-02-15] |
| **02-15** | **PowerShell 인자 오류** | 특수문자(`-D`) 인식 실패로 인한 실행 오류 -> JVM 옵션 전체를 큰따옴표(`" "`)로 감싸 해결. [cite: 2026-02-15] |
| **02-15** | **`NoClassDefFoundError`** | 런타임 시 외부 라이브러리(ByteBuddy) 미검출 -> Gradle Shadow Jar(Fat Jar) 설정 적용. [cite: 2026-02-15] |
</details>

---

### 📜 기술 결정 의사결정 (ADR)
- **Annotation-based Filtering**: 하드코딩된 메서드 이름 대신 애노테이션을 사용하여 에이전트와 비즈니스 로직의 결합도를 최소화함. [cite: 2026-02-15]
- **Zero-Invasive Monitoring**: 원본 소스 코드의 수정 없이 런타임에 성능 측정 코드를 삽입하여 유지보수성 극대화. [cite: 2026-02-15]

---

### 🛡️ 향후 유지보수 가이드 (Maintenance Guide)
- **캐시 초기화**: `.gitignore` 수정 후에도 파일이 계속 올라간다면 `git rm -r --cached .` 실행 후 다시 `add` 할 것. [cite: 2026-02-15]
- **빌드 관리**: `./gradlew clean`을 습관화하여 `build/` 폴더의 상태를 항상 최신으로 유지할 것. [cite: 2026-02-15]