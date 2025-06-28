# 🔗 Linkit - 팀빌딩 매칭 플랫폼

<div align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen?logo=springboot" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Java-17-blue?logo=java" alt="Java">
  <img src="https://img.shields.io/badge/MySQL-8.0-orange?logo=mysql" alt="MySQL">
  <img src="https://img.shields.io/badge/MongoDB-7.0-green?logo=mongodb" alt="MongoDB">
  <img src="https://img.shields.io/badge/Redis-7.2-red?logo=redis" alt="Redis">
</div>

## 📌 프로젝트 소개

Linkit은 사이드 프로젝트 진행이나 협업 경험을 쌓기 위한 대학생, 사업을 영위하기 위한 예비·초기 창업가들이 겪는 가장 큰 어려움인 **팀빌딩 문제를 해결**하기 위해 시작한 프로젝트입니다. 현재 실서비스로 운영 중이며, 다양한 직군의 사람들이 자신의 역량을 소개하고 프로젝트에 적합한 팀원을 찾을 수 있도록 지원하고 있습니다.

### 🎯 핵심 가치

- **효율적인 팀 매칭**: 기술 스택과 관심 분야를 기반으로 한 스마트 매칭
- **실시간 소통**: WebSocket 기반 실시간 채팅으로 즉각적인 커뮤니케이션
- **신뢰할 수 있는 프로필**: 포트폴리오, 경력, 수상 이력 등 검증 가능한 정보 제공

## 🚀 주요 기능

### 1. 프로필 관리 시스템

- **상세 프로필**: 기술 스택, 포트폴리오, 경력사항, 학력, 자격증, 대외활동 등 체계적인 이력 관리
- **미니 프로필**: 핵심 정보만을 담은 간략한 프로필 제공
- **프로필 로그**: 프로젝트 진행 상황이나 성과를 기록하는 블로그 형태의 기능
- **방문자 추적**: 프로필 조회 이력 및 방문자 통계

### 2. 팀 빌딩 & 관리

- **팀 생성 및 관리**: 팀 정보, 히스토리, 멤버 관리
- **팀 멤버 모집**: 모집 공고 작성, 지원자 관리, 팀원 초대 시스템
- **팀 프로덕트**: 팀에서 진행한 프로젝트 성과물 관리
- **팀 로그**: 팀 활동 및 프로젝트 진행 상황 기록

### 3. 실시간 채팅 시스템

- **STOMP 프로토콜**: Spring WebSocket과 STOMP를 활용한 실시간 메시징
- **채팅방 관리**: 팀원 간 실시간 소통을 위한 채팅방
- **메시지 히스토리**: MongoDB를 활용한 채팅 메시지 영구 저장
- **읽지 않은 메시지 알림**: Spring Batch를 활용한 배치 처리

### 4. 매칭 & 검색

- **동적 검색**: QueryDSL을 활용한 복잡한 검색 조건 처리
- **커서 기반 페이지네이션**: 대용량 데이터에 최적화된 페이징 처리
- **통합 검색**: 프로필, 팀, 공고, 로그 전체를 아우르는 검색 기능
- **스크랩 기능**: 관심 프로필, 팀, 공고 저장

### 5. 알림 & 커뮤니케이션

- **이메일 발송**: 비동기 처리를 통한 팀 초대, 지원 알림 등
- **실시간 알림**: 채팅 메시지, 댓글, 스크랩 등의 실시간 알림
- **방문 알림**: 프로필 방문자 알림

## 🛠 기술 스택

### Backend Core

- **Framework**: Spring Boot 3.2.4
- **Language**: Java 17
- **Build Tool**: Gradle 8.7

### Database & Persistence

- **RDB**: MySQL 8.0 (메인 데이터베이스)
- **NoSQL**: MongoDB (채팅 메시지 저장)
- **Cache**: Redis (세션 관리 및 캐싱), Caffeine (로컬 캐싱)
- **ORM**: Spring Data JPA, Hibernate
- **Query**: QueryDSL 5.0.0 (동적 쿼리)
- **Migration**: Flyway (DB 스키마 버전 관리)

### Real-time & Messaging

- **WebSocket**: Spring WebSocket + STOMP 프로토콜
- **Message Broker**: SimpleBroker (In-memory)
- **Batch**: Spring Batch (읽지 않은 메시지 알림)

### Infrastructure & DevOps

- **Cloud**: AWS (EC2, RDS, S3)
- **CI/CD**: GitHub Actions
- **Container**: Docker
- **Monitoring**: Spring Actuator, p6spy (쿼리 로깅)

### Development & Quality

- **Testing**: JUnit 5, Mockito, Testcontainers, REST Assured
- **Documentation**: Spring REST Docs, SpringDoc OpenAPI
- **Code Quality**: Spotless (코드 포맷팅), JaCoCo (테스트 커버리지)
- **Performance**: datasource-decorator (쿼리 성능 모니터링)

## 📁 프로젝트 구조

```
src/main/java/liaison/linkit/
├── auth/                 # 인증/인가 (커스텀 어노테이션 기반)
├── chat/                 # 실시간 채팅
│   ├── config/          # WebSocket 설정
│   ├── presentation/    # 채팅 컨트롤러
│   ├── service/         # 채팅 비즈니스 로직
│   └── scheduler/       # 배치 스케줄러
├── common/              # 공통 유틸리티
├── file/                # 파일 업로드 (S3 연동)
├── global/              # 전역 설정
│   ├── config/         # Spring 설정 (STOMP, S3, Async 등)
│   ├── exception/      # 전역 예외 처리
│   ├── response/       # 공통 응답 형식
│   └── util/           # 유틸리티 클래스
├── image/               # 이미지 처리
├── login/               # 로그인/로그아웃
├── mail/                # 이메일 발송 (비동기)
├── matching/            # 팀-개인 매칭
├── member/              # 회원 관리
├── notification/        # 알림 서비스
├── profile/             # 프로필 관리
│   ├── presentation/    # 프로필 관련 컨트롤러들
│   ├── business/        # 비즈니스 로직
│   ├── domain/          # 도메인 엔티티
│   └── infrastructure/  # 레포지토리
├── report/              # 신고 기능
├── scrap/               # 스크랩 기능
├── search/              # 통합 검색
├── team/                # 팀 관리
└── visit/               # 방문 기록
```

## 🏗 주요 기술적 구현

### 1. 쿼리 성능 최적화 (N+1 문제 해결)

프로필 목록 조회 시 발생하던 N+1 문제를 QueryDSL과 Fetch Join을 활용하여 해결했습니다.

```java
// ProfileRepositoryCustomImpl.java
@Override
public Page<ProfileListResponse> findProfilesWithDetails(Pageable pageable) {
    // 메인 쿼리: 프로필과 연관 엔티티를 한 번에 조회
    List<Profile> profiles = queryFactory
            .selectFrom(profile)
            .leftJoin(profile.miniProfile, miniProfile).fetchJoin()
            .leftJoin(profile.profileSkills, profileSkill).fetchJoin()
            .leftJoin(profileSkill.skill, skill).fetchJoin()
            .leftJoin(profile.member, member).fetchJoin()
            .where(
                    profile.isDeleted.eq(false),
                    profile.isPublic.eq(true)
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(profile.createdAt.desc())
            .distinct()
            .fetch();

    // 카운트 쿼리 분리로 성능 최적화
    Long totalCount = queryFactory
            .select(profile.count())
            .from(profile)
            .where(
                    profile.isDeleted.eq(false),
                    profile.isPublic.eq(true)
            )
            .fetchOne();

    return new PageImpl<>(profiles, pageable, totalCount);
}
```

**결과**: 평균 8~9개 테이블 조회 시 2초 → 20ms로 **100배 성능 향상**

### 2. 실시간 채팅 구현 (STOMP + MongoDB)

WebSocket과 STOMP 프로토콜을 활용하여 실시간 채팅을 구현했습니다.

```java
// StompConfig.java
@Configuration
@EnableWebSocketMessageBroker
public class StompConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 구독 경로 설정
        config.enableSimpleBroker("/sub/chat", "/sub/chat/list");
        config.setUserDestinationPrefix("/user");
        // 발행 경로 설정
        config.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp/linkit")
                .setAllowedOrigins("*");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // 동시 접속자 처리를 위한 스레드 풀 설정
        registration.taskExecutor()
                .corePoolSize(4)
                .maxPoolSize(10)
                .queueCapacity(50);
    }
}
```

```java
// ChatMessageController.java
@MessageMapping("/chat/message")
public void message(ChatMessageDTO message, SimpMessageHeaderAccessor accessor) {
    // 세션에서 사용자 정보 추출
    Long memberId = extractMemberIdFromSession(accessor);

    // MongoDB에 메시지 저장
    ChatMessage savedMessage = chatService.saveMessage(message, memberId);

    // 해당 채팅방 구독자들에게 메시지 전송
    messagingTemplate.convertAndSend(
            "/sub/chat/room/" + message.getRoomId(),
            savedMessage
    );
}
```

### 3. 비동기 이메일 발송

이메일 발송으로 인한 API 응답 지연을 해결하기 위해 비동기 처리를 도입했습니다.

```java
// AsyncConfig.java
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "mailExecutor")
    public ThreadPoolTaskExecutor mailExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("MailExecutor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
```

```java
// MailService.java
@Service
@RequiredArgsConstructor
public class MailService {

    @Async("mailExecutor")
    public CompletableFuture<Void> sendTeamInvitationMail(EmailRequest request) {
        try {
            MimeMessage message = createMessage(request);
            mailSender.send(message);
            log.info("이메일 발송 성공: {}", request.getTo());
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("이메일 발송 실패: {}", e.getMessage());
            // 실패 시 재시도 로직 또는 DLQ 처리
            return CompletableFuture.failedFuture(e);
        }
    }
}
```

### 4. 커서 기반 페이지네이션

대용량 데이터 처리를 위해 오프셋 기반이 아닌 커서 기반 페이지네이션을 구현했습니다.

```java
// CursorPageable 구현
public List<ProfileResponse> getProfilesByCursor(Long lastId, int size) {
    return queryFactory
            .selectFrom(profile)
            .where(
                    lastId != null ? profile.id.lt(lastId) : null,
                    profile.isDeleted.eq(false)
            )
            .orderBy(profile.id.desc())
            .limit(size + 1)  // 다음 페이지 존재 여부 확인용
            .fetch();
}
```

### 5. Blue-Green 무중단 배포

GitHub Actions와 Docker를 활용한 무중단 배포 파이프라인을 구축했습니다.

```yaml
# .github/workflows/deploy-prod.yml
name: Deploy to Production

on:
  push:
    branches: [ main ]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - name: Build and Push Docker Image
        run: |
          docker build -t linkit:${{ github.sha }} .
          docker push linkit:${{ github.sha }}

      - name: Blue-Green Deployment
        run: |
          # Green 환경에 새 버전 배포
          docker run -d --name linkit-green \
            -p 8081:8080 \
            -e SPRING_PROFILES_ACTIVE=prod \
            linkit:${{ github.sha }}

          # 헬스체크
          ./scripts/health_check.sh http://localhost:8081/actuator/health

          # 로드밸런서 전환
          ./scripts/switch_nginx.sh green

          # Blue 환경 종료
          docker stop linkit-blue && docker rm linkit-blue

          # Green을 Blue로 이름 변경
          docker rename linkit-green linkit-blue
```

## 📊 성능 개선 사례

### 1. 프로필 목록 조회 API 최적화

- **문제**: N+1 쿼리로 인한 응답 지연 (평균 2초)
- **해결**:
    - QueryDSL Fetch Join으로 쿼리 최적화
    - Caffeine 캐시 적용으로 반복 조회 최소화
    - 커서 기반 페이지네이션으로 오프셋 연산 제거
- **결과**: 응답 속도 **100배 개선** (2초 → 20ms)

### 2. 실시간 채팅 안정성 개선

- **문제**: 서버 재시작 시 WebSocket 연결 끊김
- **해결**:
    - 클라이언트 자동 재연결 로직 구현
    - 연결 상태 모니터링 및 하트비트 구현
- **결과**: 99.9% 연결 안정성 확보

### 3. 데이터베이스 커넥션 풀 최적화

- **문제**: 동시 접속자 증가 시 커넥션 부족
- **해결**:
    - HikariCP 설정 최적화
    - 커넥션 풀 모니터링 추가
- **결과**: 동시 처리 능력 3배 향상

## 🔧 설치 및 실행

### 요구사항

- Java 17+
- MySQL 8.0+
- MongoDB 7.0+
- Redis 7.2+
- Gradle 8.7+

### 환경 설정

```bash
# 1. 프로젝트 클론
git clone https://github.com/[your-username]/linkit.git
cd linkit

# 2. 환경 변수 설정
cp src/main/resources/application-example.yml src/main/resources/application-dev.yml
# application-dev.yml에 데이터베이스 및 외부 서비스 정보 입력

# 3. 데이터베이스 초기화
./gradlew flywayMigrate

# 4. 애플리케이션 실행
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### 테스트 실행

```bash
# 전체 테스트 실행
./gradlew test

# 특정 테스트만 실행
./gradlew test --tests "*.ProfileServiceTest"

# 테스트 커버리지 리포트 생성
./gradlew jacocoTestReport
# build/jacoco/index.html에서 확인
```

## 📝 API 문서

### Spring REST Docs

테스트 기반으로 API 문서를 자동 생성합니다.

```bash
# API 문서 생성
./gradlew asciidoctor
```

생성된 문서: `build/docs/asciidoc/index.html`

### OpenAPI (Swagger)

개발 환경에서는 Swagger UI를 통해 API를 테스트할 수 있습니다.

- URL: `http://localhost:8080/swagger-ui/index.html`

### 주요 API 엔드포인트

- **인증**: `/api/v1/auth/*`
- **프로필**: `/api/v1/profile/*`
- **팀**: `/api/v1/teams/*`
- **채팅**: `/stomp/linkit` (WebSocket)
- **검색**: `/api/v1/search/*`

## 🤝 팀 정보

### 팀 구성 및 역할

**팀 규모**: 4명 (기획 1, 디자인 1, 프론트엔드 1, 백엔드 1)  
**담당 역할**: 백엔드 개발 총괄

- 서버 아키텍처 설계 및 API 개발
- 데이터베이스 설계 및 최적화
- 실시간 채팅 시스템 구축
- DevOps 및 배포 파이프라인 구축

**개발 기간**: 2023.XX - 현재 (운영 중)

## 📈 프로젝트 성과

### 기술적 성과

- 서비스 가용성 **99.9%** 달성 (월간 기준)
- API 평균 응답 시간 **50ms 이하** 유지
- 무중단 배포를 통한 **다운타임 0분** 운영
- 테스트 커버리지 **80% 이상** 유지

### 비즈니스 성과

- MAU (Monthly Active Users) 지속적 증가
- 성공적인 팀 매칭 다수 사례 확보
- 사용자 만족도 조사 결과 긍정적 피드백

## 🔍 트러블슈팅

### 1. N+1 쿼리 문제

- **상황**: 프로필 목록 조회 시 연관 엔티티 로딩으로 인한 다수의 쿼리 발생
- **해결**: QueryDSL을 활용한 Fetch Join 및 DTO Projection 적용
- **교훈**: JPA 사용 시 지연 로딩 전략과 쿼리 최적화의 중요성 인식

### 2. WebSocket 연결 관리

- **상황**: 네트워크 불안정 시 채팅 연결 끊김 문제
- **해결**: 하트비트 메커니즘 및 자동 재연결 로직 구현
- **교훈**: 실시간 통신에서는 연결 상태 관리가 핵심

### 3. 동시성 이슈

- **상황**: 팀 멤버 초대 시 중복 초대 발생
- **해결**: 비관적 락(Pessimistic Lock) 적용 및 유니크 제약조건 추가
- **교훈**: 동시성 제어를 위한 다층 방어 전략 필요

## 🔒 라이선스

이 프로젝트는 비공개 프로젝트입니다. 코드 사용에 대해서는 별도 문의 바랍니다.

---

<div align="center">
  <sub>Built with ❤️ by Linkit Team</sub>
</div>
