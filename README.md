# 🚀 바이트코드의 지배자 (Bytecode Master)

> **JVM의 심장부를 관통하는 바이트코드 조작 엔진: Java Agent와 ByteBuddy를 이용한 런타임 최적화 탐구**

### 📌 프로젝트 개요
JVM의 심장부인 바이트코드를 직접 조작하여 애플리케이션의 동작을 제어하고 최적화하는 '극한의 자바' 탐구 프로젝트입니다.

### 🛠️ 개발 환경
- **JDK**: Microsoft OpenJDK 17.0.16 (실행 환경: Java 25)
- **Build Tool**: Gradle 9.0 (Kotlin DSL)
- **Library**: ByteBuddy 1.14.12

---

### 🏗️ 프로젝트 구축 내역 (Development Milestone)

#### 3. 메서드 인터셉트 및 성능 측정 성공 [2026-02-15]
- **런타임 주입**: `TargetApp`의 모든 메서드(`main`, `work`)에 대한 바이트코드 변환 성공.
- **성능 지표 확보**: 인터셉터를 통해 `work()` 메서드의 실행 시간(약 500ms)을 실시간으로 산출함.

#### 2. 저장소 무결성 및 인프라 최적화 [2026-02-15]
- **Git 정화**: `git rm --cached`를 통해 불필요한 바이너리 폴더를 제거하고 저장소 클린 상태 유지.
- **Fat JAR 구성**: ByteBuddy 의존성을 포함하는 단일 실행 JAR 빌드 전략 수립.
- **테스트 앱 연동**: 에이전트 동작 검증을 위한 `TargetApp.java` 구현 및 연동 테스트.

#### 1. Java Agent 골격 및 진입점 설계 [2026-02-15]
- `AgentLauncher` 클래스에 `premain` 진입점을 설계하여 JVM 기동 시 에이전트 주입 기반 마련.
- `MethodDelegation`을 활용하여 메서드 실행 시간을 추적하는 `TimingInterceptor` 구현.

---

### 🔍 트러블슈팅 로그 (Troubleshooting)

| 날짜 | 이슈 내용 | 원인 분석 및 해결 방안 |
| :--- | :--- | :--- |
| **02-15** | **메서드 인터셉트 최종 성공** | `-Dnet.bytebuddy.experimental=true` 옵션 및 따옴표 처리로 Java 25 환경 제어 성공. |
| **02-15** | PowerShell 인자 해석 오류 | `-D` 옵션 인식 실패 -> JVM 옵션을 따옴표(`" "`)로 감싸서 해결. |
| **02-15** | Java 25 버전 호환성 오류 | Byte Buddy 미지원 버전(Java 25) 감지 -> 실험적 모드 활성화로 우회. |
| **02-15** | 인터셉터 미작동 | 매칭 범위 협소 -> `nameStartsWith` 패키지 단위 매칭으로 확장. |
| **02-15** | `ClassNotFoundException` | `clean` 후 컴파일 누락 확인 -> `./gradlew classes` 단계 추가. |
| **02-15** | `NoClassDefFoundError` 발생 | 에이전트 런타임 시 ByteBuddy 라이브러리 미검출 -> Gradle Fat JAR 설정으로 해결. |

---

### 📜 기술 결정 의사결정 (ADR)
- **Logging**: 외부 라이브러리 충돌 방지를 위해 초기 단계에서는 `System.out.println()` 사용.