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


