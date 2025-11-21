## Sec30 - Spotify Community Music Platform

## 프로젝트 개요
Spotify API를 활용한 음악 스트리밍 커뮤니티 플랫폼입니다.

## 주요 기능

### ✨ 핵심 기능
- 🎵 **Spotify API 연동**: 실시간 음악 검색 및 스트리밍
- 📝 **플레이리스트 CRUD**: 나만의 플레이리스트 생성/관리
- ❤️ **찜 기능**: 플레이리스트 좋아요
- 💬 **댓글 기능**: 플레이리스트에 댓글 작성
- 👤 **마이페이지**: 내 플레이리스트, 내 댓글 관리
- 🔍 **검색**: 아티스트, 앨범, 곡 검색

### 📱 화면 구성
1. **메인 페이지**: Spotify 스타일 홈 화면, Discover Weekly, 인기 차트
2. **플레이리스트 상세**: 트랙 목록, 재생, 댓글, 좋아요
3. **마이페이지**: 내 플레이리스트 관리, 댓글 목록

## 기술 스택

### Backend
- Java 21
- Spring Boot 3.5.7
- Spring Data JPA
- Spring Security
- MySQL 8.0

### Frontend
- Thymeleaf
- HTML5/CSS3
- JavaScript (Vanilla)

### External API
- Spotify Web API

## 설치 및 실행

### 1. 사전 요구사항
- JDK 21 이상
- MySQL 8.0 이상
- Spotify API 키 (Client ID, Client Secret)

### 2. 환경 변수 설정
프로젝트 루트에 `.env` 파일 생성:
```env
SPOTIFY_CLIENT_ID=your_spotify_client_id
SPOTIFY_CLIENT_SECRET=your_spotify_client_secret
DB_USERNAME=root
DB_PASSWORD=your_password
```

### 3. 데이터베이스 설정
```bash
mysql -u root -p < init-db.sql
```

### 4. 프로젝트 실행
```bash
./gradlew bootRun
```

또는

```bash
./gradlew build
java -jar build/libs/sec30-0.0.1-SNAPSHOT.jar
```

### 5. 접속
브라우저에서 `http://localhost:8080` 접속

## 프로젝트 구조

```
sec30/
├── src/main/java/com/gmg/sec30/
│   ├── config/          # 설정 파일
│   ├── controller/      # 컨트롤러
│   ├── dto/             # DTO
│   ├── entity/          # JPA 엔티티
│   ├── repository/      # Repository
│   └── service/         # 비즈니스 로직
├── src/main/resources/
│   ├── static/          # 정적 리소스
│   │   ├── css/         # CSS 파일
│   │   └── js/          # JavaScript 파일
│   └── templates/       # Thymeleaf 템플릿
└── init-db.sql          # DB 초기화 스크립트
```

## API 엔드포인트

### 페이지
- `GET /` - 메인 페이지
- `GET /tracks` - 트랙 검색/목록
- `GET /my-playlists` - 마이 플레이리스트
- `GET /playlists/{id}` - 플레이리스트 상세

### REST API
- `POST /api/playlists` - 플레이리스트 생성
- `PUT /api/playlists/{id}` - 플레이리스트 수정
- `DELETE /api/playlists/{id}` - 플레이리스트 삭제
- `POST /api/playlists/{id}/tracks` - 트랙 추가
- `DELETE /api/tracks/{id}` - 트랙 삭제
- `POST /api/playlists/{id}/comments` - 댓글 작성
- `DELETE /api/comments/{id}` - 댓글 삭제
- `POST /api/playlists/{id}/like` - 좋아요 토글
- `GET /api/my-comments` - 내 댓글 목록

## 데이터베이스 스키마

### Users
- 사용자 정보 저장

### Playlists
- 플레이리스트 정보 저장
- User와 1:N 관계

### PlaylistTracks
- 플레이리스트-트랙 매핑
- Spotify 트랙 정보 저장

### Comments
- 플레이리스트 댓글

### Likes
- 플레이리스트 좋아요

## 개발 계획

### Phase 1 ✅
- [x] DB 구조 설계 및 Entity 생성
- [x] 기본 CRUD API 구현
- [x] Spotify API 연동

### Phase 2 ✅
- [x] 플레이리스트 상세 페이지
- [x] 댓글 기능
- [x] 좋아요 기능

### Phase 3 ✅
- [x] UI/UX 개선 (Spotify 스타일)
- [x] 반응형 디자인

### Phase 4 (예정)
- [ ] 사용자 인증/인가 (JWT)
- [ ] 실시간 음악 재생
- [ ] 소셜 기능 (팔로우, 공유)
- [ ] 추천 알고리즘

## 라이선스
MIT License

## 문의
이슈 및 문의사항은 GitHub Issues를 통해 남겨주세요.

```bash
http://localhost:8080/tracks
```