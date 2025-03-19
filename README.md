# 로그인 시스템
## 기술 스택  
- Spring Security 6.4.2
- OAuth2.0
## 로그인 유형
- 이메일/비밀번호 로그인 (기본 인증)
- 게스트 로그인 (브라우저 스토리지 기반)
- OAuth2.0 소셜 로그인 (Google, Line)
## 유저 정보 관리
- 인증된 유저 정보는 세션 기반으로 유지
- 다양햔 로그인 유형으로 인증된 유저 정보를 LoginUserDetails 객체로 일관성 있게 관리
## 로그인 기능 상세 설명
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

# 학습 데이터 구조  
## 구조의 특징 : 계층 구조
학습 데이터의 체계적인 관리를 위해 **카테고리(Category) → 덱(Deck) → 카드(Card)** 계층 구조를 채택
상위 계층의 **Primary Key**를 하위 계층이 **Foreign Key**로 참조하여 계층을 형성

## 데이터 구조  
### 1. 카테고리(Category)  
- 사용자가 학습을 대분류로 구분할 수 있도록 설정  
- 예: **'JLPT', '비즈니스 일본어'**  
### 2. 덱(Deck)  
- 각 카테고리 내에서 세분화된 학습 단위 제공  
- 예: **'N1 단어', 'N3 단어', '비즈니스 표현'**  
### 3. 카드(Card)  
- 실제 학습할 단어 정보가 저장되는 개별 학습 단위  

# XLSX 기반 덱 관리 및 공유  
## XLSX 활용 목적 
- **덱 구성 및 공유 편의성**을 위해 **XLSX 파일을 불러와 덱 생성 가능**  
- 사용자가 **덱을 XLSX 형식으로 저장 및 공유 가능**  
## XLSX 형식 채택 이유  
- **CSV 파일의 쉼표(,)** 문제 해결 (예문 내 쉼표 사용 가능)  
- 표 형태 데이터를 **손쉽게 주고받을 수 있도록 지원**  
## 기술 스택  
- SheetJS
- Apache POI

# 카드 외우기
## 플래시 카드 페이지 앞면 
### 주요 기능
- 덱에 저장되어 있는 카드 중 랜덤으로 단어 표시
- 타이머 기능
  - 상단 중앙 타이머 : 덱을 한 번 공부하는데 걸리는 시간
  - 하단 우측 타이머 : 한 카드를 학습하는데 걸리는 시간
- x 버튼 기능
  - 홈으로 돌아가면서 덱 학습 시간 저장
- 카드 편집 버튼 기능
   - 카드 오류 발견 시 즉시 수정 가능
  
## 플래시 카드 페이지 뒷면
### 주요 기능
- 해당 단어의 히라가나, 품사, 뜻, 예문 표시
- O △ X 버튼 기능 (학습 레벨)
  - ○ : 정확하게 외운 단어
  - △ : 헷갈리는 단어 - 3일 후 복습
  - X : 모르는 단어 - 오늘 학습 중 다시 나와 복습
- 하단 우측 타이머
  - 학습 타이머 시간이 1분을 넘으면 타이머 색상이 빨간색으로 변경되고 "○" 버튼을 비활성화

## 기술 스택
- Backend: Spring Boot, JPA
- Frontend: Thymeleaf, HTML, CSS, JavaScript, jQuery (AJAX)
- Database: MySQL
- Build Tool: IntelliJ

# 덱 공유 커뮤니티 게시판
## 덱 공유 목록 조회
### 주요 기능
- 게시판 형식을 취하면서 내가 만든 덱을 공유할 수 있는 시스템
- 덱 게시글 검색 기능
  - 글제목, 글내용, 작성자 기준 검색 가능
- 덱 게시글 정렬 기능
  - 복합 정렬 기능
    - 제목, 좋아요 수, 작성자, 조회수 기준
    - 잘못 정렬했을 시, '정렬 초기화' 버튼을 통해 정렬 상태 초기화 가능
- 페이징 기능
  - 10개의 게시글당 1페이지 구성
  
- 덱 공유 게시글 작성
  - 작성한 덱을 첨부파일의 형태로 서비스 이용자들과 공유
  - 게시글 내 내용 검증
    - 첨부 가능 확장자 제한 기능
    - 업로드 가능한 파일 크기(사이즈) 제한 기능
      - (3월 19일 기준 100MB 초과 시 파일 업로드 제한)
  - 게스트 로그인 상태로도 게시글 작성 가능
  - 본인이 작성한 덱만 올릴 수 있도록 하는 '저작권 체크 확인' 필수화

- 게시글 상세보기
  - 좋아요 기능
    - 좋아요 버튼을 한 번 누를 시 좋아요 데이터 저장, 한 번 더 누를 시 좋아요 취소 
  - 댓글 및 대댓글 기능
    - 댓글 자기참조 형식으로, 댓글에 댓글을 작성할 시 대댓글로 처리
    - 본인이 작성한 댓글 및 대댓글에 대해서만 삭제 버튼 활성화
  - 공유한 덱을 첨부파일 형태로 다운로드 가능
    - 다운로드한 덱은 본인 학습창 내 덱으로 재저장 가능

## 기술 스택
- Backend: Spring Boot, JPA
- Frontend: Thymeleaf, HTML, CSS, JavaScript, jQuery (AJAX)
- Database: MySQL

# 저장된 덱 기반 "카드 뒤집기 게임"



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
- 끝말잇기 (しりとり)
  - 일본의 끝말잇기 게임을 구현
  - 한국과 다른 규칙 적용

## 2. 기술 스택
- Java 17
- Spring Boot 3.4.2
- Spring WebSocket (STOMP)
- Spring Data Redis
- Thymeleaf
- Spring Security
- Docker (Redis 컨테이너 실행용)
- Gradle
- Jisho.org API

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


