$(document).ready(function () {
  /***************** 덱 진행도 업데이트 *****************/
  $('.percentage-box').each(function () {
    var $box = $(this);
    // 예: "50%" 형태의 텍스트에서 숫자만 추출
    var percentText = $box.find('.percentage-text').text().trim();
    var percent = parseFloat(percentText.replace('%', ''));
    
    // 진행률에 따른 색상 결정
    var newColor;
    if (percent < 33) {
      newColor = "#f66"; // 0~33% 빨간색
    } else if (percent < 66) {
      newColor = "orange"; // 33~66% 오렌지색
    } else {
      newColor = "green"; // 66% 이상 녹색
    }
    
    // 진행도 원의 색상과 stroke-dashoffset 설정
    $box.find('.progress').css('stroke', newColor);
    var circumference = 376.8; // 2πr (r=60) 대략
    var dashoffset = circumference * (1 - (percent / 100));
    $box.find('.progress').css('stroke-dashoffset', dashoffset);
  });

  /***************** 덱 리스트 토글 *****************/
  $('.category .category-right a i.fa-chevron-up, .category .category-right a i.fa-chevron-down').on('click', function(e) {
    e.preventDefault();
    
    // 현재 아이콘의 부모 .category 요소에서 바로 다음에 위치한 deckList를 선택하여 토글
    $(this).closest('.category').next('.deckList').slideToggle(300);
    
    // 아이콘 클래스 토글: fa-chevron-up -> fa-chevron-down, 반대로 전환
    $(this).toggleClass('fa-chevron-up fa-chevron-down');
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
    // 각 덱 하단의 icon-box에서 숫자 값 읽기
    var okText = $(this).find('.icon-box:eq(0) span').text().trim();
    var yetText = $(this).find('.icon-box:eq(1) span').text().trim();
    var noText = $(this).find('.icon-box:eq(2) span').text().trim();
    var ok = parseInt(okText, 10) || 0;
    var yet = parseInt(yetText, 10) || 0;
    var no = parseInt(noText, 10) || 0;
    
    // 전체 카드 수 계산
    var total = ok + yet + no;
    var percent = 0;
    if(total > 0) {
      percent = (ok / total) * 100;
    }
    percent = Math.round(percent); // 소수점 없애기
    
    // 계산된 백분율을 percentage-text에 반영
    $(this).find('.percentage-box .percentage-text').text(percent + "%");
    
    // 진행률에 따른 색상 결정
    var newColor;
    if (percent < 33) {
      newColor = "#f66";
    } else if (percent < 66) {
      newColor = "orange";
    } else {
      newColor = "green";
    }
    
    // 원의 circumference (2πr, r=60) 대략 376.8 사용하여 dashoffset 계산
    var circumference = 376.8;
    var dashoffset = circumference * (1 - (percent / 100));
    
    // 원형 진행도에 색상과 dashoffset 적용
    $(this).find('.percentage-box .progress').css({
      'stroke': newColor,
      'stroke-dashoffset': dashoffset
    });
  });





});

