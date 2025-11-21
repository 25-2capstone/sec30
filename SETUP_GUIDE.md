# 🚀 Sec30 프로젝트 실행 가이드

## ✅ 구현 완료 내용

### 1. 데이터베이스 구조 (5개 테이블)
- ✅ **User**: 사용자 정보
- ✅ **Playlist**: 플레이리스트 정보
- ✅ **PlaylistTrack**: 플레이리스트-트랙 매핑
- ✅ **Comment**: 댓글
- ✅ **Like**: 좋아요

### 2. 백엔드 구현
- ✅ Entity (5개)
- ✅ Repository (5개)
- ✅ Service (3개): PlaylistService, CommentService, LikeService
- ✅ Controller (2개): MusicController, PlaylistController
- ✅ DTO (Request/Response)

### 3. 프론트엔드 구현
- ✅ **메인 페이지** (main.html): Spotify 스타일 홈 화면
  - 좌측 사이드바 (홈, 검색, 내 라이브러리)
  - 중앙 Discover Weekly 배너
  - 우측 인기 차트
  - 하단 플레이어 UI
  
- ✅ **플레이리스트 상세** (playlist-detail.html)
  - 플레이리스트 정보
  - 트랙 목록 (테이블 형태)
  - 댓글 기능
  - 좋아요 기능
  
- ✅ **마이페이지** (my-playlists.html)
  - 내 플레이리스트 그리드
  - 플레이리스트 생성 모달
  - 내 댓글 보기 모달

### 4. CSS 스타일
- ✅ spotify-main.css: 메인 페이지 (다크 테마)
- ✅ playlist-detail.css: 플레이리스트 상세 (그린 테마)
- ✅ my-playlists.css: 마이페이지 (다크 테마)

### 5. 기능
- ✅ 플레이리스트 CRUD
- ✅ 트랙 추가/삭제
- ✅ 댓글 작성/삭제
- ✅ 좋아요 토글
- ✅ 내 댓글 목록 조회
- ✅ Spotify API 연동

## 🔧 실행 방법

### 1단계: MySQL 데이터베이스 생성
```bash
# MySQL 접속
mysql -u root -p

# 데이터베이스 생성 (터미널에서)
mysql -u root -p < init-db.sql
```

또는 MySQL Workbench에서:
```sql
CREATE DATABASE IF NOT EXISTS sec30 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2단계: 환경 변수 설정
프로젝트 루트에 `.env` 파일 생성:
```env
SPOTIFY_CLIENT_ID=your_spotify_client_id_here
SPOTIFY_CLIENT_SECRET=your_spotify_client_secret_here
DB_USERNAME=root
DB_PASSWORD=your_mysql_password
```

**Spotify API 키 발급:**
1. https://developer.spotify.com/dashboard 접속
2. 앱 생성
3. Client ID, Client Secret 복사

### 3단계: 프로젝트 빌드
```bash
cd /Users/gimseongsu/Desktop/spring/sec30
./gradlew clean build
```

### 4단계: 애플리케이션 실행
```bash
./gradlew bootRun
```

또는

```bash
java -jar build/libs/sec30-0.0.1-SNAPSHOT.jar
```

### 5단계: 브라우저 접속
```
http://localhost:8080
```

## 📱 페이지 구조

### 메인 페이지 (/)
- **좌측**: 사이드바 (네비게이션, 플레이리스트 목록)
- **중앙**: Discover Weekly, 맞춤 믹스
- **우측**: 인기 차트 TOP 4
- **하단**: 뮤직 플레이어 컨트롤

### 트랙 검색 (/tracks)
- Spotify API를 통한 실시간 검색
- 트랙 미리듣기

### 마이 플레이리스트 (/my-playlists)
- 내가 만든 플레이리스트 그리드
- 새 플레이리스트 만들기
- 내 댓글 목록 보기

### 플레이리스트 상세 (/playlists/{id})
- 플레이리스트 정보
- 트랙 목록 (재생, 삭제)
- 댓글 작성/삭제
- 좋아요

## 🎯 주요 기능 사용법

### 1. 플레이리스트 만들기
1. 메인 페이지 좌측 사이드바 "플레이리스트 만들기" 클릭
2. 제목, 설명, 커버 이미지 입력
3. 생성 버튼 클릭

### 2. 트랙 추가
1. `/tracks` 페이지에서 트랙 검색
2. 원하는 트랙의 "플레이리스트에 추가" 버튼 클릭
3. 대상 플레이리스트 선택

### 3. 댓글 작성
1. 플레이리스트 상세 페이지 하단
2. 댓글 입력 후 "작성" 버튼 클릭

### 4. 좋아요
1. 플레이리스트 상세 페이지에서 하트 버튼 클릭
2. 재클릭시 좋아요 취소

## 🐛 트러블슈팅

### MySQL 연결 실패
```
application.properties에서 DB 설정 확인:
spring.datasource.url=jdbc:mysql://localhost:3306/sec30
spring.datasource.username=root
spring.datasource.password=your_password
```

### Spotify API 오류
```
.env 파일에 SPOTIFY_CLIENT_ID, SPOTIFY_CLIENT_SECRET 확인
```

### 포트 충돌 (8080)
```
application.properties에 추가:
server.port=8081
```

## 📊 데이터베이스 스키마

```sql
users
├── id (PK)
├── username (unique)
├── password
├── email
├── nickname
├── profile_image_url
└── created_at

playlists
├── id (PK)
├── user_id (FK -> users)
├── title
├── description
├── cover_image_url
├── is_public
└── created_at

playlist_tracks
├── id (PK)
├── playlist_id (FK -> playlists)
├── spotify_track_id
├── track_name
├── artist_name
├── album_name
├── image_url
└── added_at

comments
├── id (PK)
├── playlist_id (FK -> playlists)
├── user_id (FK -> users)
├── content
└── created_at

likes
├── id (PK)
├── user_id (FK -> users)
├── playlist_id (FK -> playlists)
└── created_at
```

## 🎨 디자인 컨셉

### 메인 페이지
- Spotify 오리지널 스타일
- 다크 테마 (#121212 배경)
- 그린 포인트 컬러 (#1db954)
- 3단 레이아웃 (사이드바-메인-차트)

### 플레이리스트 상세
- 그린 그라데이션 배경
- 테이블 형태 트랙 목록
- 호버 효과

### 마이페이지
- 카드 그리드 레이아웃
- 모달 UI

## 🔜 향후 개발 계획

1. **인증/인가**
   - JWT 기반 로그인
   - 회원가입 기능

2. **실시간 재생**
   - Spotify Web Playback SDK 연동
   - 플레이어 기능 구현

3. **소셜 기능**
   - 팔로우/언팔로우
   - 플레이리스트 공유
   - 피드

4. **추천 시스템**
   - 사용자 기반 추천
   - 협업 필터링

## 📞 문의
- GitHub Issues
- Email: your-email@example.com

---
**Made with ❤️ using Spring Boot & Spotify API**

