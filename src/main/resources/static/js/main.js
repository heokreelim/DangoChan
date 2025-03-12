$(document).ready(function () {
  /***************** 덱 진행도 업데이트 *****************/
  $('.percentage-box').each(function () {
    var $box = $(this);
    // 예: "50%" 형태의 텍스트에서 숫자만 추출
    var percentText = $box.find('.percentage-text').text().trim();
    var percent = parseFloat(percentText.replace('%', ''));
    
    // 진행률에 따른 색상 결정
    var newColor;
    if (percent < 37) {
      newColor = "#f66"; // 0~33% 빨간색
    } else if (percent < 75) {
      newColor = "orange"; // 33~66% 오렌지색
    } else {
      newColor = "green"; // 75% 이상 녹색
    }
    
    // 진행도 원의 색상과 stroke-dashoffset 설정
    $box.find('.progress').css('stroke', newColor);
    var circumference = 376.8; // 2πr (r=60) 대략
    var dashoffset = circumference * (1 - (percent / 100));
    $box.find('.progress').css('stroke-dashoffset', dashoffset);
  });

  /***************** 덱 리스트 토글 *****************/
  // 토글 아이콘 클릭 시: chevron 아이콘을 대상으로 함
  $('.category .category-right a i.fa-chevron-up, .category .category-right a i.fa-chevron-down')
    .on('click', function(e) {
      e.preventDefault();
      var $icon = $(this);
      var $category = $icon.closest('.category');
      // 카테고리 ID를 hidden input으로부터 가져옴
      var categoryId = $category.find('.categoryId').val();
      var $deckList = $category.next('.deckList');
      
      // 토글 실행 후 상태 저장
      $deckList.slideToggle(300, function() {
         if ($deckList.is(':visible')) {
           localStorage.setItem('openCategory-' + categoryId, 'true');
         } else {
           localStorage.removeItem('openCategory-' + categoryId);
         }
      });
      
      // 아이콘 클래스 토글 (chevron-up <-> chevron-down)
      $icon.toggleClass('fa-chevron-up fa-chevron-down');
    });
  
  // 페이지 로드 시, 저장된 토글 상태 복원
  $('.category').each(function() {
    var $category = $(this);
    var categoryId = $category.find('.categoryId').val();
    if (localStorage.getItem('openCategory-' + categoryId) === 'true') {
      $category.next('.deckList').show();
      $category.find('.category-right a i.fa-chevron-up')
        .removeClass('fa-chevron-up')
        .addClass('fa-chevron-down');
    }
  });
  /***************** 드롭다운 메뉴 *****************/
  $(document).on('click', '.menu-icon', function(e) {
    e.stopPropagation(); // 이벤트 버블링 방지
    $(this).siblings('.dropdown-menu').toggleClass('show');
  });
  
  $(document).on('click', function (e) {
    if (!$(e.target).closest('.dropdown').length) {
      $('.dropdown-menu').removeClass('show');
    }
  });

  /***************** 덱 진행도 업데이트 (덱별 계산) *****************/
  $('.deck-bottom').each(function(){
    var $this = $(this);
    // 각 icon-box에서 숫자값 읽기 (괄호 제거)
    var okText    = $this.find('.icon-box:eq(0) span').text().trim().replace(/[()]/g, '');  // 외운 카드 (3점)
    var yetText   = $this.find('.icon-box:eq(1) span').text().trim().replace(/[()]/g, '');  // 복습 필요한 카드 (2점)
    var noText    = $this.find('.icon-box:eq(2) span').text().trim().replace(/[()]/g, '');  // 외우지 못한 카드 (1점)
    var newText   = $this.find('.icon-box:eq(3) span').text().trim().replace(/[()]/g, '');  // 아직 공부하지 않은 카드 (0점)

    var ok      = parseInt(okText, 10) || 0;
    var yet     = parseInt(yetText, 10) || 0;
    var no      = parseInt(noText, 10) || 0;
    var newCard = parseInt(newText, 10) || 0;
    
    // 총 카드 수: 4종류의 카드 개수 합산
    var totalCards = ok + yet + no + newCard;
    
    // 실제 점수 계산: (3점 * ok) + (2점 * yet) + (1점 * no) + (0점 * newCard)
    var actualScore = (ok * 3) + (yet * 2) + (no * 1) + (newCard * 0);
    
    // 최대 점수: 모든 카드가 3점일 경우
    var maxScore = totalCards * 3;
    
    var percent = 0;
    if(totalCards > 0) {
       percent = (actualScore / maxScore) * 100;
    }
    percent = Math.round(percent);
    
    // 계산된 진행률을 텍스트로 업데이트
    $this.find('.percentage-box .percentage-text').text(percent + "%");
    
    // 5단계로 색상 결정 (20% 단위)
    var newColor;
    if(percent < 20) {
        newColor = "#ff0000"; // 0~20%: 빨간색
    } else if(percent < 40) {
        newColor = "#ffa500"; // 20~40%: 주황색
    } else if(percent < 60) {
        newColor = "#ffff00"; // 40~60%: 노란색
    } else if(percent < 80) {
        newColor = "#90ee90"; // 60~80%: 연두색
    } else {
        newColor = "#008000"; // 80~100%: 녹색
    }
    
    // 원형 진행도: 원의 반지름 r=60일 때, 둘레는 약 376.8
    var circumference = 376.8;
    var dashoffset = circumference * (1 - (percent / 100));
    
    // SVG 원에 색상과 dashoffset 적용
    $this.find('.percentage-box .progress').css({
       'stroke': newColor,
       'stroke-dashoffset': dashoffset
    });

    // 덱 컨테이너(.deck-wrap)에 총 카드 수 저장 (나중에 a 태그 클릭 시 사용)
    $this.closest('.deck-wrap').attr('data-total', totalCards);
});

/***************** 덱의 카드가 없을 경우, 링크 불가 *****************/
$('.flashcard-link').on('click', function(e) {
    var totalCards = $(this).closest('.deck-wrap').attr('data-total');
    if(parseInt(totalCards, 10) === 0){
        e.preventDefault();
        alert("이 덱에는 카드가 없습니다. 먼저 카드를 추가하세요.");
    }
});

});

