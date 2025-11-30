# 테스트 코드 가이드

## 📋 개요
이 프로젝트는 Spring Boot 3.5.7 기반의 음악 플레이리스트 서비스로, JUnit 5와 AssertJ를 사용한 포괄적인 테스트 코드를 포함하고 있습니다.

## 🧪 테스트 구성

### 1. 엔티티 테스트 (Entity Tests)
위치: `src/test/java/com/gmg/sec30/entity/`

#### UserTest
- User 빌더 패턴 생성 테스트
- Setter를 이용한 필드 업데이트 테스트
- 연관관계 컬렉션 초기화 확인

#### PlaylistTest
- Playlist 빌더 패턴 생성 테스트  
- 필드 업데이트 테스트
- User와의 연관관계 테스트

### 2. 리포지토리 테스트 (Repository Tests)
위치: `src/test/java/com/gmg/sec30/repository/`

#### UserRepositoryTest
- 사용자 저장 및 조회 테스트
- 이메일로 사용자 조회
- 닉네임으로 사용자 조회
- Soft Delete 동작 확인
- `@DataJpaTest` 사용으로 순수 JPA 테스트

#### PlaylistRepositoryTest
- 플레이리스트 저장 및 조회
- 사용자별 플레이리스트 목록 조회
- Soft Delete 동작 확인
- TestEntityManager를 활용한 영속성 컨텍스트 제어

### 3. 서비스 테스트 (Service Tests)
위치: `src/test/java/com/gmg/sec30/service/`

#### UserServiceTest
- 회원가입 성공 케이스
- 이메일 중복 체크
- 닉네임 중복 체크
- 비밀번호 암호화 확인
- 사용자 조회 기능
- Mockito를 활용한 단위 테스트

#### PlaylistServiceTest
- 플레이리스트 생성 성공
- 존재하지 않는 사용자의 생성 시도 실패
- 플레이리스트 조회
- 사용자별 플레이리스트 목록 조회
- 플레이리스트 삭제
- 권한 없는 사용자의 삭제 시도 실패
- Mock 객체를 활용한 의존성 격리

### 4. 컨트롤러 통합 테스트 (Controller Integration Tests)
위치: `src/test/java/com/gmg/sec30/controller/`

#### UserControllerTest
- 회원가입 페이지 접근
- 로그인 페이지 접근
- 트랙 페이지 접근 (로그인 없이)
- `@SpringBootTest`를 사용한 전체 컨텍스트 로딩
- TestRestTemplate를 활용한 HTTP 테스트

### 5. 애플리케이션 컨텍스트 테스트
위치: `src/test/java/com/gmg/sec30/`

#### Sec30ApplicationTests
- Spring Boot 애플리케이션 컨텍스트 로딩 확인
- 빈 설정 검증

## ⚙️ 테스트 실행 방법

### 전체 테스트 실행
```bash
./gradlew test
```

### 특정 패키지 테스트 실행
```bash
# 엔티티 테스트만 실행
./gradlew test --tests "com.gmg.sec30.entity.*Test"

# 서비스 테스트만 실행
./gradlew test --tests "com.gmg.sec30.service.*Test"

# 리포지토리 테스트만 실행
./gradlew test --tests "com.gmg.sec30.repository.*Test"
```

### 특정 클래스 테스트 실행
```bash
./gradlew test --tests "com.gmg.sec30.service.UserServiceTest"
```

### 특정 메서드 테스트 실행
```bash
./gradlew test --tests "com.gmg.sec30.service.UserServiceTest.registerUser_Success"
```

### 테스트 리포트 확인
```bash
open build/reports/tests/test/index.html
```

## 📝 테스트 설정

### 테스트용 properties 파일
위치: `src/test/resources/application.properties`

주요 설정:
- H2 인메모리 데이터베이스 사용
- DDL auto: create-drop
- SQL 스크립트 초기화 비활성화
- 테스트용 Mock API 키 설정

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop
spring.sql.init.mode=never
SPOTIFY_CLIENT_ID=test_client_id
SPOTIFY_CLIENT_SECRET=test_client_secret
YOUTUBE_API_KEY=test_youtube_api_key
```

## 🎯 테스트 커버리지

### 현재 테스트된 영역
✅ Entity (User, Playlist)
✅ Repository (UserRepository, PlaylistRepository)  
✅ Service (UserService, PlaylistService)
✅ Controller (UserController - 통합 테스트)
✅ Application Context Loading

### 향후 추가 가능한 테스트
- CommentService 테스트
- LikeService 테스트
- SpotifyService 테스트
- YouTubeService 테스트
- API 통합 테스트
- E2E 테스트

## 🔧 사용된 테스트 도구

- **JUnit 5**: 테스트 프레임워크
- **AssertJ**: 유창한 assertion 라이브러리
- **Mockito**: Mock 객체 생성 및 관리
- **Spring Boot Test**: 통합 테스트 지원
- **TestRestTemplate**: HTTP 요청 테스트
- **@DataJpaTest**: JPA 리포지토리 테스트
- **TestEntityManager**: 영속성 컨텍스트 제어

## 💡 테스트 작성 시 주의사항

1. **@DataJpaTest 사용 시**: 
   - JPA 관련 빈만 로드됨
   - TestEntityManager 사용 가능
   - 트랜잭션 자동 롤백

2. **@SpringBootTest 사용 시**:
   - 전체 애플리케이션 컨텍스트 로드
   - 실제 서버 환경과 유사
   - 테스트 실행 시간이 더 오래 걸림

3. **Mockito 사용 시**:
   - @ExtendWith(MockitoExtension.class) 필수
   - @Mock으로 Mock 객체 생성
   - @InjectMocks로 테스트 대상에 주입
   - given().willReturn() 패턴 사용

4. **테스트 격리**:
   - 각 테스트는 독립적으로 실행 가능해야 함
   - 공유 상태를 피하고 @BeforeEach에서 초기화
   - 테스트 순서에 의존하지 않기

5. **테스트 데이터**:
   - 의미 있는 테스트 데이터 사용
   - 경계값 테스트 포함
   - 예외 상황도 테스트

## 📊 테스트 결과

모든 테스트가 성공적으로 통과합니다:
```
BUILD SUCCESSFUL in 13s
27 tests completed, 27 passed
```

## 🚀 지속적 통합 (CI)

GitHub Actions 또는 Jenkins 등의 CI 도구와 연동하여 자동화된 테스트 실행을 권장합니다:

```yaml
# .github/workflows/test.yml 예시
name: Run Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-java@v2
        with:
          java-version: '21'
      - run: ./gradlew test
```

## 📚 참고 자료

- [JUnit 5 공식 문서](https://junit.org/junit5/docs/current/user-guide/)
- [AssertJ 공식 문서](https://assertj.github.io/doc/)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/reference/testing/index.html)
- [Mockito 공식 문서](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)

