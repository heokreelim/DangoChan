// 전역 변수
const $frame = document.querySelector('#frame');
let gameActive = false; // 초기에 false로 두어, 카운트다운이 끝날 때까지 클릭 비활성화

// 1) 카드 데이터
const pairs = [
  { id: 1, content: 'apple' },
  { id: 1, content: '사과' },
  { id: 2, content: 'banana' },
  { id: 2, content: '바나나' },
  { id: 3, content: 'dog' },
  { id: 3, content: '개' },
  { id: 4, content: 'cat' },
  { id: 4, content: '고양이' },
  { id: 5, content: 'egg' },
  { id: 5, content: '계란' },
  { id: 6, content: 'fish' },
  { id: 6, content: '물고기' },
];

// 2) Fisher-Yates로 카드 섞기
function shuffle(array) {
  for (let i = array.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [array[i], array[j]] = [array[j], array[i]];
  }
  return array;
}
shuffle(pairs);

// 선택 상태를 저장할 배열들
let clicked = [];
let matched = [];

// 3) 카드 DOM 생성 함수
function createCard(data) {
  const container = document.createElement('div');
  container.className = 'container';
  container.dataset.cardId = data.id;

  // 카드 내용이 항상 보이는 영역
  const cardBack = document.createElement('div');
  cardBack.className = 'card-back';
  cardBack.innerText = data.content;

  container.appendChild(cardBack);
  return container;
}

// 4) 카드 클릭 이벤트
function onClickCard() {
  // 게임이 아직 활성화되지 않았다면(카운트다운 중이라면) 클릭 무시
  if (!gameActive) return;

  // 이미 매칭된 카드면 무시
  if (this.classList.contains('matched')) {
    return;
  }

  // 이미 선택한 카드(= clicked[0])를 다시 클릭 → 선택 해제
  if (clicked.length === 1 && clicked[0] === this) {
    this.classList.remove('selected');
    clicked = [];
    return;
  }

  // 새 카드 선택
  this.classList.add('selected');
  clicked.push(this);

  // 두 장 선택 시 매칭 판별
  if (clicked.length === 2) {
    const [firstCard, secondCard] = clicked;
    const firstId = firstCard.dataset.cardId;
    const secondId = secondCard.dataset.cardId;

    // 혹시라도 같은 DOM 요소(이론상 방어적 코드)
    if (firstCard === secondCard) {
      firstCard.classList.remove('selected');
      clicked = [];
      return;
    }

    // 매칭 성공
    if (firstId === secondId) {
      firstCard.classList.remove('selected');
      secondCard.classList.remove('selected');
      firstCard.classList.add('matched');
      secondCard.classList.add('matched');
      firstCard.removeEventListener('click', onClickCard);
      secondCard.removeEventListener('click', onClickCard);
      matched.push(firstCard, secondCard);
      clicked = [];

      // 모든 카드가 매칭되었는지 확인
      if (matched.length === pairs.length) {
        setTimeout(() => {
            alert('Mission clear!');
        }, 500); // 모든 카드 매칭 후 0.5초 뒤 완료 메시지 호출
        
        // 필요 시 resetGame() 등 호출
      }
    } else {
      // 매칭 실패
      firstCard.classList.add('wrong');
      secondCard.classList.add('wrong');
      setTimeout(() => {
        firstCard.classList.remove('wrong', 'selected');
        secondCard.classList.remove('wrong', 'selected');
        clicked = [];
      }, 500);
    }
  }
}

// 5) 게임 시작 함수 + 카운트다운 통합
function startGame() {
  // 일단 클릭 비활성화
  gameActive = false;
  $frame.innerHTML = '';

  // 카드 DOM 생성
  pairs.forEach((data) => {
    const card = createCard(data);
    card.addEventListener('click', onClickCard);
    $frame.appendChild(card);
  });

  // 카운트다운 요소 동적 생성
  const countdownElem = document.createElement('div');
  countdownElem.id = 'countdown';
  // 기본 스타일 (폰트 크기, 정렬 등)
  countdownElem.style.fontSize = '24px';
  countdownElem.style.textAlign = 'center';
  countdownElem.style.marginBottom = '20px';
  countdownElem.style.pointerEvents = 'none'; // 클릭 방지
  document.body.insertBefore(countdownElem, $frame);

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
        countdownElem.remove();
        // 카운트다운 종료 후 클릭 활성화
        gameActive = true;
        console.log('Game is now active!');
      }, 1000);
    }
  }, 1000);
}

// 실제 게임 실행
startGame();
