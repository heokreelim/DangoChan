$(document).ready(function () {
  // ===== 카테고리 추가 =====
 
  var $addCategoryBtn = $("#addCategoryBtn");
  var userId = $("#userId").val();

  $addCategoryBtn.on("click", function() {
    var categoryName = prompt("새로운 카테고리 이름을 입력하세요:");
    if (categoryName && $.trim(categoryName) !== "") {
      $.ajax({
        url: "/insertCategory",
        method: "POST",
        contentType: "application/json",
        data: JSON.stringify({ userId: userId, categoryName: $.trim(categoryName) }),
        dataType: "json",
        success: function(data) {
          // 카테고리 추가 후 페이지 전체 새로고침
          // 1) 현재 페이지 새로고침
          // location.reload();

          // 2) 특정 URL로 리다이렉트 (예: home)
          window.location.href = "/?userId=" + usehomerId;
        },
        error: function(xhr, status, error) {
          console.error("카테고리 추가 중 오류 발생:", error);
          alert("카테고리 추가 중 오류 발생: " + error);
        }
      });
    }
  });

  // ===== 덱 진행도 업데이트 =====
  $('.percentage-box').each(function () {
    var $box = $(this);
    // "50%" 형태의 텍스트에서 숫자만 추출
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

    // 진행도 원의 색상과 dashoffset 설정
    $box.find('.progress').css('stroke', newColor);
    var circumference = 376.8; // 2πr, r=60일 때 대략 376.8
    var dashoffset = circumference * (1 - (percent / 100));
    $box.find('.progress').css('stroke-dashoffset', dashoffset);
  });

  // 아이콘 클릭 시 메뉴 열기/닫기
  $('.menu-icon').on('click', function(e) {
    e.stopPropagation(); // 이벤트 버블링 방지
    $(this).siblings('.dropdown-menu').toggleClass('show');
  });
  
  // 메뉴 외부 클릭 시 메뉴 닫기
  $(document).on('click', function(e) {
    // .dropdown 영역 밖을 클릭했다면
    if (!$(e.target).closest('.dropdown').length) {
      $('.dropdown-menu').removeClass('show');
    }
  });
});
