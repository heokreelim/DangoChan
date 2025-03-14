// 전역 변수
const $frame = document.querySelector('#frame');
let gameActive = false; // 초기에는 비활성 (카운트다운이 끝날 때까지)
let pairs = [];

$(function () {
  $.ajax({
    url: '/game/matchCard/data',
    type: "GET",
    dataType: "json",
    success: function (data) {
      console.log(data);
      pairs = data;
      // 여기서 pairs를 섞음
      pairs = shuffle(pairs);
      // 실제 게임 실행
      startGame();
    },
    error: function(xhr, status, error) {
      console.error("AJAX 요청 오류: ", status, error);
    }
  });
});



// 2) Fisher-Yates로 카드 섞기
function shuffle(array) {
  for (let i = array.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [array[i], array[j]] = [array[j], array[i]];
  }
  return array;
}
// shuffle(pairs);

// 선택 상태 및 매칭된 카드 저장 배열
let clicked = [];
let matched = [];

// 3) 카드 DOM 생성 함수 (flip 효과를 위한 구조)
// 각 카드에는 container → card → (card-front, card-back)
function createCard(data) {
  const container = document.createElement('div');
  container.className = 'container';
  container.dataset.cardId = data.id;
  
  const card = document.createElement('div');
  card.className = 'card';
  
  const cardFront = document.createElement('div');
  cardFront.className = 'card-front';
  // 앞면은 일반적으로 카드 뒷면(=뒤집힌 상태)로 보이기 전 디자인
  cardFront.innerText = ''; // 혹은 이미지, 로고 등을 넣을 수 있음
  
  const cardBack = document.createElement('div');
  cardBack.className = 'card-back';
  cardBack.innerText = data.content;
  
  card.appendChild(cardFront);
  card.appendChild(cardBack);
  container.appendChild(card);
  return container;
}

// 4) 카드 클릭 이벤트 핸들러
function onClickCard() {
  // 게임이 활성화되지 않았다면 클릭 무시
  if (!gameActive) return;
  
  // 이미 매칭된 카드라면 무시
  if (this.classList.contains('matched')) return;
  
  // 같은 카드 재클릭 시 (이미 선택된 카드라면) → 선택 취소하여 다시 뒤집기
  if (clicked.length === 1 && clicked[0] === this) {
    this.classList.remove('revealed');
    clicked = [];
    return;
  }
  
  // 카드 뒤집기 효과: revealed 클래스를 추가 → CSS에서 180도 회전
  this.classList.add('revealed');
  clicked.push(this);
  
  // 두 장 선택되면 매칭 검사
  if (clicked.length === 2) {
    const [firstCard, secondCard] = clicked;
    const firstId = firstCard.dataset.cardId;
    const secondId = secondCard.dataset.cardId;
    
    // 방어 코드: 같은 DOM 요소(이론상 동일한 카드)면 선택 취소
    if (firstCard === secondCard) {
      firstCard.classList.remove('revealed');
      clicked = [];
      return;
    }
    
    // 매칭 성공: 두 카드의 id가 같으면
    if (firstId === secondId) {
        // 이미 'revealed' 상태이므로 제거하지 않고, 'matched' 클래스를 추가
        firstCard.classList.remove('selected');
        secondCard.classList.remove('selected');
        firstCard.classList.add('matched');
        secondCard.classList.add('matched');
        // 'revealed' 클래스는 그대로 유지되어 카드가 뒤집힌 상태로 남음
        
        // 더 이상 클릭할 수 없도록 이벤트 제거
        firstCard.removeEventListener('click', onClickCard);
        secondCard.removeEventListener('click', onClickCard);
        matched.push(firstCard, secondCard);
        clicked = [];
        
        if (matched.length === pairs.length) {
          setTimeout(() => {
            alert('Mission clear!');
            // 경과 시간 타이머 중지
            clearInterval(window.elapsedTimer);
            // "새로운 게임으로 다시 시작" 버튼 표시
            showRestartButton();
          }, 500);
        }
    } else {
      // 매칭 실패: 흔들리는 효과(shake) 추가 후 0.5초 뒤 카드 다시 뒤집기
      firstCard.classList.add('shake');
      secondCard.classList.add('shake');
      setTimeout(() => {
        firstCard.classList.remove('shake', 'revealed');
        secondCard.classList.remove('shake', 'revealed');
        clicked = [];
      }, 500);
    }
  }
}

// 5) 게임 시작 함수 + 카운트다운 통합
function startGame() {
  // 우선 클릭 비활성화
  gameActive = false;
  $frame.innerHTML = '';
  
  // 카드 DOM 생성 및 이벤트 등록
  pairs.forEach((data) => {
    const card = createCard(data);
    card.addEventListener('click', onClickCard);
    $frame.appendChild(card);
  });
  
  // [요구사항 1] 카운트다운 동안에는 모든 카드가 뒤집힌 상태로 내용이 보임
  const allCards = document.querySelectorAll('.container');
  allCards.forEach(card => card.classList.add('revealed'));
  
  // 카운트다운 요소 동적 생성
  const countdownElem = document.createElement('div');
  countdownElem.id = 'countdown';
  countdownElem.style.fontSize = '24px';
  countdownElem.style.textAlign = 'center';
  countdownElem.style.marginBottom = '20px';
  countdownElem.style.pointerEvents = 'none';
  $frame.parentNode.insertBefore(countdownElem, $frame);

  
  // 3초 카운트다운
  let timeLeft = 3;
  countdownElem.innerText = timeLeft;
  const timer = setInterval(() => {
    timeLeft--;
    if (timeLeft > 0) {
      countdownElem.innerText = timeLeft;
    } else {
      clearInterval(timer);
      countdownElem.innerText = 'START!';
      setTimeout(() => {
        // START 후 텍스트 비우기
        countdownElem.innerText = "";
        // [요구사항 2] 카운트다운 종료 후 모든 카드를 뒤집어(감추어) 초기 상태로 만듦
        allCards.forEach(card => card.classList.remove('revealed'));
        // 게임 활성화: 이제 카드 클릭 가능
        gameActive = true;
        console.log('Game is now active!');

        // 게임 시작 후 경과 시간 표시 시작
        let elapsed = 0;
        countdownElem.innerText += "경과시간: " + formatTime(elapsed);
        // 전역 변수에 저장하여 나중에 게임 종료 시 타이머 정지 가능
        window.elapsedTimer = setInterval(() => {
          elapsed++;
          //mm:ss 형식으로 변환할 수 있는 형식으로 변환하는 함수 호출출
          countdownElem.innerText = "경과시간: " + formatTime(elapsed);
        }, 1000);
      }, 1000);
      
      // 초월 mm:ss 형식으로 변환하는 함수
      function formatTime(seconds) {
        const minutes = Math.floor(seconds / 60);
        const remainingSeconds = seconds % 60;

        //두 자리 숫자로 맞추기 위해 padStart 사용하기
        const formattedMinutes = String(minutes).padStart(2, '0');
        const formattedSeconds = String(remainingSeconds).padStart(2, "0");

        return `${formattedMinutes}:${formattedSeconds}`;
      }
    }
  }, 1000);
}

// 새로운 게임으로 재시작 기능
function showRestartButton() {
  // restart 버튼 생성
  const restartBtn = document.createElement('button');
  restartBtn.innerText = "다시 시작";
  restartBtn.style.fontSize = "24px";
  restartBtn.style.marginTop = "20px";
  restartBtn.style.display = "block";
  restartBtn.style.marginLeft = "auto";
  restartBtn.style.marginRight = "auto";

  // 프레임 바로 아래에 버튼을 삽입 (원하는 위치에 배치)
  $frame.parentNode.insertBefore(restartBtn, $frame.nextSibling);

  // restart 버튼 클릭 시
  restartBtn.addEventListener('click', () => {
    // 기존 카운트다운 요소를 제거 (경과시간 텍스트 포함)
    const oldCountdown = document.getElementById("countdown");
    if (oldCountdown) {
      oldCountdown.remove();
    }
    // 게임 상태 초기화 (clicked, matched 등)
    clicked = [];
    matched = [];

    // restart 버튼 제거
     restartBtn.remove();

    // startGame()을 다시 호출하여 게임 재시작
    startGame();
  });
}
