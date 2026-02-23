# Spring REST Start 프로젝트 분석 보고서

## 1. 프로젝트 개요

이 프로젝트는 REST API를 제공하는 Java 11 기반의 Spring Boot 애플리케이션입니다. 사용자 인증(JWT), 게시판, 댓글 기능 등을 포함하는 모듈화된 구조를 가지고 있어, 포럼이나 블로그와 유사한 웹 서비스의 백엔드로 추정됩니다.

## 2. 주요 기술 및 의존성 (`build.gradle` 기반)

- **언어**: Java 11
- **프레임워크**: Spring Boot
- **API**: Spring Web (RESTful API 구축)
- **데이터베이스**:
    - Spring Data JPA (객체-관계 매핑)
    - H2 (인메모리/파일 DB, 주로 개발 및 테스트용)
    - MySQL Driver (운영 환경용)
- **보안**:
    - Spring Security
    - JWT (JSON Web Token) 라이브러리 (인증/인가)
- **기타**:
    - Lombok (보일러플레이트 코드 감소)
    - `spring-boot-devtools` (개발 생산성 향상)

## 3. 아키텍처 및 소스 코드 구조

프로젝트는 기능별로 패키지를 분리하는 계층형 아키텍처를 따릅니다. 각 모듈은 `Controller`, `Service`, `Repository`, `DTO` 등의 컴포넌트로 구성됩니다.

### 주요 패키지

- `com.metacoding.springv2`
    - **`_core`**: 애플리케이션의 핵심 및 공통 기능을 담당합니다.
        - `config`: `SecurityConfig` 등 주요 설정 클래스 포함
        - `filter`: `JwtAuthorizationFilter` 등 HTTP 요청/응답을 처리하는 필터
        - `handler`: `GlobalExceptionHandler` 등 전역 예외 처리
        - `util`: `JwtProvider`, `Resp` 등 공통 유틸리티 클래스
    - **`auth`**: 회원가입, 로그인 등 사용자 인증 관련 기능을 담당합니다.
    - **`user`**: 사용자 정보 조회, 수정 등 사용자 관리 기능을 담당합니다.
    - **`board`**: 게시글 생성, 조회, 수정, 삭제(CRUD) 기능을 담당합니다.
    - **`reply`**: 게시글에 대한 댓글 CRUD 기능을 담당합니다.

## 4. 주요 파일

- **`build.gradle`**: 프로젝트의 모든 의존성, 플러그인, 빌드 설정을 정의하는 핵심 파일입니다.
- **`src/main/resources/application.properties`**: 데이터베이스 연결, 서버 포트 등 애플리케이션의 주요 실행 설정을 정의합니다. (`-dev`, `-prod` 프로파일 포함)
- **`src/main/java/com/metacoding/springv2/_core/config/SecurityConfig.java`**: Spring Security를 이용한 인증/인가, CORS 정책 등 애플리케이션의 보안 설정을 총괄합니다.
- **`src/main/java/com/metacoding/springv2/_core/handler/GlobalExceptionHandler.java`**: 애플리케이션 전역에서 발생하는 예외를 일관된 형식으로 처리합니다.
- **`src/main/java/com/metacoding/springv2/_core/util/JwtProvider.java`**: JWT 생성 및 검증 로직을 담당합니다.
