# 로그인 시스템
## 로그인 유형
- 이메일/비밀번호 로그인 (기본 인증)
- 게스트 로그인 (브라우저 스토리지 기반)
- OAuth2.0 소셜 로그인 (Google, Line)
## 유저 정보 관리
- 인증된 유저 정보는 세션 기반으로 유지
- 다양햔 로그인 유형으로 인증된 유저 정보를 LoginUserDetails 객체로 일관성 있게 관리
## 기능 상세 설명
### 1. 이메일/비밀번호 로그인
- 사용자가 ID(이메일)와 비밀번호를 통한 회원가입 후 DB에 입력된 정보를 활용해 로그인
- Spring Security Config에서는 formLogin 설정
### 2. 게스트 로그인
- 별도의 회원가입 혹은 소셜 로그인 절차 없이 빠르게 로그인 가능
- 디바이스(브라우저 스토리지) 에 UUID를 발급하여 저장한 후 이를 기반으로 유저를 특정
- 별도의 가입 없이 체험목적의 유저에 적합한 로그인
### 3. OAuth2 소셜 로그인 (Google, Line)
- Google 로그인: OAuth 2.0을 활용한 인증
- Line 로그인: OAuth 2.0을 활용한 인증


# 실시간 채팅
### Spring Security 환경에서 Redis + WebSocket(STOMP) 기반 채팅을 구현해두었습니다.

## 1. 주요 기능
- 채팅방 목록
  - 채팅방을 생성하고, 생성된 방을 볼 수 있음
  - 화면 중앙에 채팅창을 띄우고, 우측에 들어와있는 유저 목록을 표시
  - 크롬에서 방을 만들면, 엣지(다른 브라우저)에서도 방이 실시간으로 표시
- 채팅방 입장/퇴장
  - 방에 들어가면 실시간 메시지 전송 가능
  - 방을  나가면, 방에 아무도 남지 않았을 경우 자동 삭제
- 파일 전송
  - 사진, PDF, 기타 파일을 업로드하고, 채팅창에 링크로 표시 → 다른 사용자가 클릭해 다운로드 가능
  - 압축파일(zip 등)도 설정을 통해 전송 가능(파일 용량/형식 제한 해제)

## 2. 기술 스택
- Java 17
- Spring Boot 3.4.2
- Spring WebSocket (STOMP)
- Spring Data Redis
- Thymeleaf
- Spring Security
- Docker (Redis 컨테이너 실행용)
- Gradle

## 3. 이슈 해결
### CDN 접근 불가
회사/학교 방화벽, 내부망 등에서 jsdelivr CDN을 막을 수 있음

이 경우 Network 탭 보면 GET stomp.min.js net::ERR_CONNECTION_TIMED_OUT 식으로 뜸

로컬에 stomp.min.js, sockjs.min.js 파일을 직접 다운받아 static/js/lib/ 등에 넣고 <script src="/js/lib/stomp.min.js"> 식으로 로드해야 함

- 1.1 SockJS

  - GitHub 저장소에서 직접 다운로드

    SockJS GitHub: https://github.com/sockjs/sockjs-client
  
    Releases 탭에서 .min.js 파일을 다운받아도 되고, /dist 폴더에서 sockjs.min.js를 찾아 받을 수 있습니다.
  
  - CDN 주소에서 직접 다운로드

    브라우저로 https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js 열기
  
    내용이 뜨면, 마우스 우클릭 → 다른 이름으로 저장 (Windows 기준)
  
    저장 시 이름을 sockjs.min.js 로 설정

- 1.2 StompJS

  - GitHub 저장소에서 직접 다운로드

    stompjs GitHub: https://github.com/stomp-js/stomp-websocket

  - CDN 주소에서 직접 다운로드

    예: https://cdn.jsdelivr.net/npm/stompjs@2.3.3/dist/stomp.min.js

    같은 방식으로 내용이 뜨면 우클릭 → “다른 이름으로 저장”

    저장 시 stomp.min.js 로 이름 지정


