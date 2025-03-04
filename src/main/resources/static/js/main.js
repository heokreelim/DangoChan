$(document).ready(function () {

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
  $('#addCategoryBtn').on("click", function () {
    openModal($('.modal_adding_menu'));
  });
  
  // 카테고리 추가 모달 열기 버튼 (모달 내에서 사용)
  $('.btn-open-adding-category-modal').on("click", function () {
    openModal($('.modal_adding_category'));
  });
  
  // 카테고리 편집 모달 열기 버튼
  $('.btn-open-editing-category-modal').click(function () {
		           openModal($('.modal_editing_category'));
  });

  // 덱 추가 모달 열기 버튼
  $('.btn-open-adding-deck-modal').on("click", function () {
    openModal($('.modal_adding_deck'));
    $('.card_list tbody').empty();
  });

  // 카드 추가 모달 열기 버튼
  $('.btn-open-adding-card-modal').on("click", function () {
    openModal($('.modal_adding_card'));
  });

  // 덱 편집에서 카드 추가 모달 열기 버튼
  $('.btn-open-adding-card-to-editing-deck').on("click", function () {
    openModal($('.modal_adding_card2'));
  });

  $('.btn-add-card-adding').on('click', function () {
    var word = $('#cardWord').val().trim();
    var pos = $('#cardPos').val().trim();
    var meaning = $('#cardMeaning').val().trim();
    var exampleJp = $('#cardExampleJp').val().trim();
    var exampleKr = $('#cardExampleKr').val().trim();

    //    //    빈 입력값 금지
    //    if (!word || !pos || !meaning || !exampleJp || !exampleKr) {
    //        alert("모든 필드를 입력하세요.");
    //        return;
    //    }

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
    word.val('');
    pos.val('');
    meaning.val('');
    exampleJp.val('');
    exampleKr.val('');
});

  // 모달 닫기 버튼 (공통)
  $('.btn-close-modal').on("click", function () {
    closeOpenModals();
  });


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


  /***************** 드롭다운 메뉴 *****************/
  // 메뉴 아이콘 클릭 시, 해당 드롭다운 메뉴 토글 (중복 등록 제거)
  $('.menu-icon').on('click', function (e) {
    e.stopPropagation(); // 이벤트 버블링 방지
    $(this).siblings('.dropdown-menu').toggleClass('show');
  });

  // 드롭다운 메뉴 외부 클릭 시, 모든 드롭다운 메뉴 닫기
  $(document).on('click', function (e) {
    if (!$(e.target).closest('.dropdown').length) {
      $('.dropdown-menu').removeClass('show');
    }
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
  // 편집 모달 관련 변수
  let editingRow = null;

  // 덱 편집 모달 열기
  $('.btn-open-editing-deck-modal').on('click', function () {
    let deckId = prompt("편집할 덱 ID를 입력하세요:");
    if (!deckId) {
      alert("덱 ID를 입력하세요.");
      return;
    }

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

  // 카드 편집 모달 열기 함수
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

  // 카드 수정 완료 버튼 클릭
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

  // 덱 수정 완료 버튼 클릭
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

    $.ajax({
      type: "PUT",
      url: "/flashcard/updateDeck",
      contentType: "application/json",
      data: JSON.stringify(deckRequest),
      success: function () {
        console.log("덱 정보가 성공적으로 수정되었습니다.");
      },
      error: function () {
        alert("덱 수정 중 오류 발생.");
      }
    });

    $.ajax({
      type: "PUT",
      url: "/flashcard/updateCards",
      contentType: "application/json",
      data: JSON.stringify(updatedCards),
      success: function () {
        alert("덱과 카드가 성공적으로 수정되었습니다!");
        $('.modal_editing_deck').css("display", "none");
      },
      error: function () {
        alert("카드 수정 중 오류 발생.");
      }
    });
  });

});
