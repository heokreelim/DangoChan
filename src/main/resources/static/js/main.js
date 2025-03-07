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

  /***************** 공통 모달 관련 함수 *****************/
  // 열려있는 모달들을 저장한 배열
  let modals = [
    $('.modal_adding_category'),
    $('.modal_editing_category'),
    $('.modal_adding_menu'),
    $('.modal_adding_deck'),
    $('.modal_importing_deck'),
    $('.modal_adding_card'),
    $('.modal_adding_card2'),
    $('.modal_editing_deck'),
    $('.modal_editing_card')
  ];
  
  // 모달 열기 함수
  function openModal(modal) {
    modal.css("display", "flex");
  }
  
  // 모든 열려있는 모달 닫기 함수
  function closeOpenModals() {
    modals.forEach(modal => {
      if (modal.css("display") === "flex") {
        modal.css("display", "none");
      }
    });
  }

  /***************** 모달 열기 이벤트 *****************/
  // 플러스 버튼을 통한 메뉴 모달 열기 (카테고리 추가, 덱 추가 등)
  $('.btn-open-adding-menu-modal').on("click", function () {
    openModal($('.modal_adding_menu'));
  });

  // 카테고리 추가 모달 열기 버튼 (모달 내에서 사용)
  $('.btn-open-adding-category-modal').on("click", function () {
    openModal($('.modal_adding_category'));
  });

  // 카테고리 편집 모달 열기 버튼
  $('.btn-open-editing-category-modal').click(function () {
    // 클릭된 버튼의 부모 요소에서 category 정보를 찾음
    let categoryDiv = $(this).closest(".category"); // jQuery 객체로 변경
    let categoryId = categoryDiv.find("input[type='hidden']").val(); // categoryId 가져오기
    let categoryName = categoryDiv.find("b").text(); // categoryName 가져오기

    // 모달의 입력 필드에 값 설정 (입력 필드의 id 선택자 확인 필요)
    $('#categoryId_E1').val(categoryId);
    $('#categoryName_E1').val(categoryName);
    openModal($('.modal_editing_category'));
  });

  // 덱 추가 모달 열기 버튼
  $('.btn-open-adding-deck-modal').on("click", function () {
    openModal($('.modal_adding_deck'));
    $('.card_list tbody').empty();
  });

  // 덱 편집 모달 열기 버튼 (변경: categoryDiv -> deckDiv 사용)
  $('.btn-editing-deck').on("click", function () {
    let deckDiv = $(this).closest(".deck-wrap");
    let deckId = deckDiv.find("input[type='hidden']").val();
    let deckName = deckDiv.find("b").text();
    
    $('#editDeckId').val(deckId);
    $('#editDeckName').val(deckName);
    openModal($('.modal_editing_deck'));
  });

  // 덱 추가/불러오기 버튼
  $('.btn-adding-deck').on('click', function () {
    var deckName = $('#deckName_A2').val().trim(); // deckName 값 가져오기
    if (!deckName) {
      deckName = $('#deckName_I2').val().trim(); // deckName이 비어있으면 deckName2 사용
    }
    var cardDTOList = [];
    var categoryId = $('#categoryId_A2').val();
    var deckDTO = {
      // 임시 카테고리ID
      categoryId: categoryId,
      deckName: deckName
    };

    $('.card_list tbody tr').each(function () {
      var row = {
        word: $(this).find('td[id^="word"]').text(),
        pos: $(this).find('td[id^="pos"]').text(),
        meaning: $(this).find('td[id^="meaning"]').text(),
        exampleJp: $(this).find('td[id^="exampleJp"]').text(),
        exampleKr: $(this).find('td[id^="exampleKr"]').text()
      };
      cardDTOList.push(row);
    });

    console.log(deckDTO);
    console.log(cardDTOList);

    $.ajax({
      type: "POST",
      url: "/flashcard/importDeck",
      contentType: "application/json",
      data: JSON.stringify({
        deckDTO: deckDTO,
        cardDTOList: cardDTOList
      }),
      success: function (response) {
        alert("덱이 성공적으로 저장되었습니다!");
        console.log(response);
      },
      error: function (xhr, status, error) {
        console.error("오류 발생:", error);
      }
    });
    // 덱 이름 초기화
    $('#deckName_A2').val('');
    $('#deckName_I2').val('');
  });

  // 카드 추가 모달 열기 버튼
  $('.btn-open-adding-card-modal').on("click", function () {
    openModal($('.modal_adding_card'));
  });

  // 덱 편집에서 카드 추가 모달 열기 버튼
  $('.btn-open-adding-card-to-editing-deck').on("click", function () {
    openModal($('.modal_adding_card'));
  });

  $('.btn-add-card-adding').on('click', function () {
    // 각 입력 필드를 jQuery 객체로 가져오기
    var $cardWord = $('#cardWord');
    var $cardPos = $('#cardPos');
    var $cardMeaning = $('#cardMeaning');
    var $cardExampleJp = $('#cardExampleJp');
    var $cardExampleKr = $('#cardExampleKr');

    var word = $cardWord.val().trim();
    var pos = $cardPos.val().trim();
    var meaning = $cardMeaning.val().trim();
    var exampleJp = $cardExampleJp.val().trim();
    var exampleKr = $cardExampleKr.val().trim();

    var index = $('.card_list tbody tr').length; // 현재 행 개수를 기준으로 index 설정

    var tr = $('<tr>');
    tr.append($('<td>').attr('id', 'word-' + index).text(word));
    tr.append($('<td>').attr('id', 'pos-' + index).text(pos));
    tr.append($('<td>').attr('id', 'meaning-' + index).text(meaning));
    tr.append($('<td>').attr('id', 'exampleJp-' + index).text(exampleJp));
    tr.append($('<td>').attr('id', 'exampleKr-' + index).text(exampleKr));

    var deleteBtn = $('<button>').text('🗑').on('click', function () {
      tr.remove();
    });
    tr.append($('<td>').append(deleteBtn));

    $('.card_list tbody').append(tr);
    $('.modal_adding_card').css("display", "none");

    // 직접 입력 필드 초기화
    $cardWord.val('');
    $cardPos.val('');
    $cardMeaning.val('');
    $cardExampleJp.val('');
    $cardExampleKr.val('');
  });

  // 모달 닫기 버튼 (공통)
  $('.btn-close-modal').on("click", function () {
    closeOpenModals();
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

  /***************** 엑셀 파일 불러오기 및 덱/카드 처리 *****************/
  var fileInput = $('#importFile');
  var tableBody = $('.card_list tbody');
  var modalImport = $('.modal_importing_deck');

  $('.btn-open-importing-deck-modal').on('click', function (event) {
    event.preventDefault();
    fileInput.click();
  });

  fileInput.on('change', function (event) {
    $('.modal_importing_deck').css("display", "flex");
    var file = event.target.files[0];
    if (file) {
      var reader = new FileReader();
      reader.readAsArrayBuffer(file);

      reader.onload = function (e) {
        var data = new Uint8Array(e.target.result);
        var workbook = XLSX.read(data, { type: 'array' });
        var sheetName = workbook.SheetNames[0];
        var sheet = workbook.Sheets[sheetName];
        var jsonData = XLSX.utils.sheet_to_json(sheet, { header: 1 });

        console.log("엑셀 데이터:", jsonData);
        tableBody.empty();

        $.each(jsonData.slice(1), function (index, row) {
          if (row.length < 5) return;
          var tr = $('<tr>');
          var ids = ['word', 'pos', 'meaning', 'exampleJp', 'exampleKr'];
          $.each(row, function (i, cell) {
            var td = $('<td>').text(cell);
            td.attr('id', ids[i] + '-' + index);
            tr.append(td);
          });

          var deleteBtn = $('<button>').text('🗑').on('click', function () {
            tr.remove();
          });
          tr.append($('<td>').append(deleteBtn));
          tableBody.append(tr);
        });

        modalImport.addClass('active');
      };

      reader.onerror = function (error) {
        console.error("파일 읽기 오류:", error);
      };
    }
    $(this).val('');
  });

  /***************** 덱 및 카드 수정/업데이트 *****************/
  let editingRow = null;

  $('.btn-open-editing-deck-modal').on('click', function () {
    let deckDiv = $(this).closest(".deck-wrap");
    let deckId = deckDiv.find("input[type='hidden']").val();
    
    $.ajax({
      type: "GET",
      url: "/flashcard/getDeck?deckId=" + deckId,
      dataType: "json",
      success: function (data) {
        console.log("불러온 덱 데이터:", data);
        $('#editDeckId').val(data.deck.deckId);
        $('#editDeckName').val(data.deck.deckName);
        $('#editCategoryName').val(data.deck.categoryId);

        let tbody = $('.edit_card_list tbody');
        tbody.empty();

        $.each(data.cardList, function (index, card) {
          let tr = $('<tr>');
          tr.append($('<td>').text(card.word));
          tr.append($('<td>').text(card.pos));
          tr.append($('<td>').text(card.meaning));
          tr.append($('<td>').text(card.exampleJp));
          tr.append($('<td>').text(card.exampleKr));

          let hiddenInput = $('<input type="hidden">').val(card.cardId);
          tr.append(hiddenInput);

          let editBtn = $('<button>').text('편집').on('click', function () {
            openEditCardModal(tr);
          });
          let deleteBtn = $('<button>').text('🗑').on('click', function () {
            tr.remove();
          });
          tr.append($('<td>').append(editBtn, deleteBtn));
          tbody.append(tr);
        });

        $('.modal_editing_deck').css("display", "flex");
      },
      error: function () {
        alert("덱 정보를 불러오는 중 오류 발생.");
      }
    });
  });

  function openEditCardModal(row) {
    editingRow = row;
    let word = row.find('td:eq(0)').text();
    let pos = row.find('td:eq(1)').text();
    let meaning = row.find('td:eq(2)').text();
    let exampleJp = row.find('td:eq(3)').text();
    let exampleKr = row.find('td:eq(4)').text();
    let cardId = row.find('input[type=hidden]').val();

    $('.modal_editing_card .cardWord').val(word);
    $('.modal_editing_card .cardPos').val(pos);
    $('.modal_editing_card .cardMeaning').val(meaning);
    $('.modal_editing_card .cardExampleJp').val(exampleJp);
    $('.modal_editing_card .cardExampleKr').val(exampleKr);
    $('.modal_editing_card .cardId').val(cardId);

    $('.modal_editing_card').css("display", "flex");
  }

  $('.btn-save-card').off('click').on('click', function () {
    if (editingRow) {
      let updatedWord = $('#cardWord').val().trim();
      let updatedPos = $('#cardPos').val().trim();
      let updatedMeaning = $('#cardMeaning').val().trim();
      let updatedExampleJp = $('#cardExampleJp').val().trim();
      let updatedExampleKr = $('#cardExampleKr').val().trim();
      let cardId = $('#cardId').val();

      editingRow.find('td:eq(0)').text(updatedWord);
      editingRow.find('td:eq(1)').text(updatedPos);
      editingRow.find('td:eq(2)').text(updatedMeaning);
      editingRow.find('td:eq(3)').text(updatedExampleJp);
      editingRow.find('td:eq(4)').text(updatedExampleKr);
      editingRow.find('input[type=hidden]').val(cardId);

      $('.modal_editing_card').css("display", "none");
      editingRow = null;
    }
  });

  $('.btn-update-deck').on('click', function () {
    let deckId = $('#editDeckId').val();
    let deckName = $('#editDeckName').val().trim();
    let categoryId = $('#editCategoryName').val();

    let deckRequest = {
      deckId: deckId,
      deckName: deckName,
      categoryId: categoryId
    };

    let updatedCards = [];
    $('.edit_card_list tbody tr').each(function () {
      let card = {
        cardId: $(this).find('input[type=hidden]').val(),
        word: $(this).find('td:eq(0)').text(),
        pos: $(this).find('td:eq(1)').text(),
        meaning: $(this).find('td:eq(2)').text(),
        exampleJp: $(this).find('td:eq(3)').text(),
        exampleKr: $(this).find('td:eq(4)').text()
      };
      updatedCards.push(card);
    });

    let updateDeckAjax = $.ajax({
      type: "PUT",
      url: "/flashcard/updateDeck",
      contentType: "application/json",
      data: JSON.stringify(deckRequest)
    });

    let updateCardsAjax = $.ajax({
      type: "PUT",
      url: "/flashcard/updateCards",
      contentType: "application/json",
      data: JSON.stringify(updatedCards)
    });

    $.when(updateDeckAjax, updateCardsAjax).done(function () {
      alert("덱과 카드가 성공적으로 수정되었습니다!");
      $('.modal_editing_deck').css("display", "none");
      // 원하는 URL로 리다이렉트 (예: 홈 화면)
      window.location.href = "/home";
    }).fail(function () {
      alert("덱 또는 카드 수정 중 오류 발생.");
    });
});

});
