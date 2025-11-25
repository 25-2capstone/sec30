# YouTube API 설정 가이드

## 1. Google Cloud Console에서 API 키 발급

1. [Google Cloud Console](https://console.cloud.google.com/) 접속
2. 새 프로젝트 생성 또는 기존 프로젝트 선택
3. "API 및 서비스" > "라이브러리" 이동
4. "YouTube Data API v3" 검색 및 활성화
5. "사용자 인증 정보" > "사용자 인증 정보 만들기" > "API 키" 선택
6. 생성된 API 키 복사

## 2. API 키 제한 설정 (보안)

### 애플리케이션 제한사항
- **HTTP 리퍼러(웹사이트)** 선택
- 허용할 도메인 추가:
  ```
  http://localhost:8080/*
  http://localhost:8081/*
  https://yourdomain.com/*
  ```

### API 제한사항
- **키 제한** 선택
- "YouTube Data API v3"만 선택

## 3. .env 파일에 API 키 추가

```bash
SPOTIFY_CLIENT_ID=f9bd05f5b8324190afaf7d3940786126
SPOTIFY_CLIENT_SECRET=7b485b5e363a403393ee8662a884cc6a
YOUTUBE_API_KEY=발급받은_API_키를_여기에_입력
```

## 4. 할당량 확인

- YouTube Data API v3는 **일일 10,000 단위** 무료 할당량 제공
- 검색 요청 1회 = 100 단위 (하루 약 100회 검색 가능)
- 필요시 Google Cloud Console에서 할당량 증가 요청 가능

## 5. 테스트

애플리케이션을 재시작하고 트랙을 클릭하면:
1. YouTube에서 자동으로 해당 곡 검색
2. 전체 곡 재생 (30초 제한 없음)
3. 브라우저 하단에 음악 플레이어 표시

## 장점

✅ **무료**: 타인이 코드를 가져와도 추가 비용 없음
✅ **전체 재생**: 30초 제한 없이 전체 곡 재생 가능
✅ **로그인 불필요**: 사용자 인증 없이 바로 재생
✅ **높은 성공률**: YouTube에 대부분의 곡이 존재

## 주의사항

⚠️ API 키는 절대 Git에 커밋하지 마세요 (.gitignore에 .env 추가 필수)
⚠️ 프로덕션 환경에서는 환경 변수로 관리하세요

