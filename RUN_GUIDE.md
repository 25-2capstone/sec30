# 🎵 실행 가이드

## ✅ 준비 완료 사항

1. **YouTube API 통합 완료**
   - YouTubeService: YouTube 비디오 검색
   - MusicController: 트랙에 YouTube ID 자동 추가
   - music-player.js: YouTube IFrame Player 통합

2. **환경변수 설정 완료**
   - .env 파일에 YouTube API 키 설정됨
   - Sec30Application에서 자동 로드

3. **프론트엔드 통합 완료**
   - base.html: YouTube API 스크립트 로드
   - tracks.html: YouTube ID를 data 속성으로 전달
   - music-player.js: YouTube 우선 재생 로직

## 🚀 실행 방법

### 터미널에서 실행:

```bash
./gradlew clean build
./gradlew bootRun
```

### IDE에서 실행:

`Sec30Application.java` 메인 메서드 실행

## 🔍 실행 후 확인사항

### 1. 서버 로그 확인

애플리케이션 시작 시 다음과 같은 로그가 표시되어야 합니다:

```
🔍 YouTube search requested for: [트랙명] - [아티스트명]
📌 API Key status: CONFIGURED (length: 39)
🌐 Calling YouTube API for: ...
✅ Found YouTube video: [videoId] for track: [트랙명]
```

**만약 "DUMMY VALUE" 또는 "NOT SET"이 표시되면:**
- .env 파일의 YOUTUBE_API_KEY 확인
- 애플리케이션 재시작

### 2. 브라우저 콘솔 확인

http://localhost:8080 접속 후 F12 → Console 탭:

```
🎵 Music Player Initializing...
✅ YouTube API already loaded
```

### 3. 트랙 클릭 테스트

트랙을 클릭하면:

```
=== playTrackDirectly called ===
Track data extracted: {...}
YouTube Video ID: [videoId]
=== musicPlayer.playTrack called ===
🎬 Attempting to play YouTube video: [videoId]
📺 Creating new YouTube Player...
✅ YouTube API ready, creating player
✅ YouTube Player ready, starting playback
```

## 🎯 재생 우선순위

1. **YouTube (1순위)** → 전체 곡 재생
   - YouTube Video ID가 있으면 YouTube Player로 재생
   - 하단 음악 플레이어에 표시
   - 30초 제한 없음

2. **Spotify Preview (2순위)** → 30초 미리듣기
   - YouTube ID가 없으면 Spotify preview_url 사용
   - 하단 음악 플레이어에 표시

3. **Spotify Web (3순위)** → 외부 링크
   - 둘 다 없으면 Spotify 웹사이트로 이동

## 🐛 트러블슈팅

### "DUMMY VALUE" 표시되는 경우

**.env 파일 확인:**
```bash
YOUTUBE_API_KEY=실제_API_키_확인
```

**IDE 환경변수 설정 (IntelliJ):**
1. Run → Edit Configurations
2. Environment Variables 추가:
   - YOUTUBE_API_KEY=실제_키

### YouTube Player 생성 실패

**브라우저 콘솔 에러 확인:**
```
❌ YouTube Player error: [에러코드]
```

**일반적인 원인:**
- API 키가 유효하지 않음
- 할당량 초과 (일일 10,000 단위)
- 네트워크 연결 문제

### 여전히 Spotify로 이동하는 경우

**서버 로그 확인:**
```bash
⚠️ YouTube API key not configured - skipping YouTube search
```
→ 환경변수 로딩 실패

**해결:**
1. 애플리케이션 재시작
2. .env 파일 위치 확인 (프로젝트 루트)
3. IDE에서 Working Directory 확인

## 📊 성공 시나리오

1. ✅ 서버 시작 → YouTube API 키 로드 확인
2. ✅ /tracks 접속 → 트랙 목록 표시
3. ✅ 트랙 클릭 → YouTube Video ID 콘솔에 표시
4. ✅ 하단 음악 플레이어 나타남
5. ✅ 음악 재생 시작 (전체 곡)

## 🎉 이제 실행해보세요!

모든 설정이 완료되었습니다. 애플리케이션을 실행하고 트랙을 클릭해보세요!

