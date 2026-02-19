# 🚀 바이트코드의 지배자 (Bytecode Master)

> **JVM의 심장부를 관통하는 바이트코드 조작 엔진: Java Agent와 ByteBuddy를 이용한 런타임 최적화 탐구**

### 📌 프로젝트 개요
JVM의 심장부인 바이트코드를 직접 조작하여 애플리케이션의 동작을 제어하고 최적화하는 '극한의 자바' 탐구 프로젝트입니다.

### 🛠️ 개발 환경
- **JDK**: Microsoft OpenJDK 17 (Run on Java 25)
- **Library**: ByteBuddy 1.14.12
- **Build**: Gradle 9.0 (Kotlin DSL)

---

### 📅 개발 일지 (Changelog)

#### [2026-02-20] 예외 추적 기능 구현
- **Feature**: `@CatchError` 애노테이션을 통해 메서드 실행 중 발생하는 예외(Exception)를 감지하고, 에러 타입 및 메시지를 자동 로깅하는 기능 추가.
- **Arch**: `ExceptionInterceptor`를 구현하여 기존 비즈니스 로직의 흐름을 방해하지 않고(비침습적) 예외를 추적 및 재발생(Re-throw)시키도록 설계.

#### [2026-02-18] 데이터 추적 기능 구현
- **Feature**: `@LogData` 애노테이션을 통해 메서드 파라미터 및 반환값(Return Value) 추적 기능 추가.
- **Arch**: `DataLogInterceptor`를 추가하여 성능 측정(`@Timer`)과 데이터 로깅 로직 분리.

#### [2026-02-15] 핵심 기능 구현 및 안정화
- **Feature**: `@Timer` 애노테이션 기반 메서드 실행 시간 측정 기능 구현.
- **Refactor**: 에이전트 리스너 최적화를 통한 로그 가독성 개선 (비즈니스 로직 집중).
- **Infra**: `.gitignore` 재설정 및 인덱스 초기화를 통한 저장소 구조 정화 완료.

#### [2026-02-15] 프로젝트 초기화
- **Setup**: Java Agent 기본 골격(`premain`) 및 ByteBuddy 라이브러리 연동.
- **Base**: 커스텀 애노테이션(`@interface`) 정의 및 Gradle 빌드 환경 구성.

---

### 💡 핵심 기술 결정 (Key Decisions)
- **Multi-Interceptor**: 하나의 에이전트에서 `@Timer`, `@LogData` 등 목적에 따라 다수의 인터셉터를 체이닝하여 적용.
- **Annotation-Driven**: 코드 수정 없이 애노테이션 부착만으로 모니터링 대상을 유연하게 선택.
- **Runtime Manipulation**: 소스 코드 수정 없이 JVM 로드 시점에 바이트코드를 조작하여 기능 주입.