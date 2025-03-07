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
	/***************** 모달 닫기 이벤트 *****************/
	// 모달 닫기 버튼 (공통)
	    $('.btn-close-modal').on("click", function () {
	        closeOpenModals();
	    });
	
    /***************** 모달 열기 이벤트 *****************/
    // 플러스 버튼을 통한 메뉴 모달 열기 (카테고리 추가, 덱 추가 등)
    $('.btn-open-adding-menu-modal').on("click", function () {
        openModal($('.modal_adding_menu'));
    });

    // 카테고리 추가 모달 열기 버튼 (모달 내에서 사용)
    $('.btn-open-adding-category-modal').on("click", function () {
        openModal($('.modal_adding_category'));
    });

    // 수정부분
    // 카테고리 편집 모달 열기 버튼
    $('.btn-open-editing-category-modal').click(function () {
        // 클릭된 버튼의 부모 요소에서 category 정보를 찾음
        let categoryDiv = $(this).closest(".category"); // jQuery로 변경
        let categoryId = categoryDiv.find("input[type='hidden']").val(); // categoryId 가져오기
        let categoryName = categoryDiv.find("b").text(); // categoryName 가져오기

        // 모달의 입력 필드에 값 설정
        $('#categoryId_E1').val(categoryId);
        $('#categoryName_E1').val(categoryName);
        openModal($('.modal_editing_category'));
    });

    // 덱 추가 모달 열기 버튼
    $('.btn-open-adding-deck-modal').on("click", function () {
        openModal($('.modal_adding_deck'));
        $('.card_list tbody').empty();
    });

    // 덱 추가/불러오기기 버튼
    $('.btn-adding-deck').on('click', function () {

        var deckName = $('#deckName_A2').val().trim(); // deckName 값 가져오기
        if (!deckName) {
            deckName = $('#deckName_I2').val().trim(); // deckName이 비어있으면 deckName2 사용
        }
        var cardDTOList = [];
        var categoryId = $('#categoryId_A2').val();
        var deckDTO = {
            // categoryId: $('#categoryId').val(),
            // userId: $('#userId').val(),  // 현재 로그인한 사용자 ID
            // 임시 카테고리ID
            categoryId: categoryId,
            deckName: deckName
        };
		// 카드 테이블 출력
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
		// 데이터 서버로 전달
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
		$('.modal_adding_card').attr("data-mode", "new"); // 새 덱 추가 모드 설정
		$('.modal_adding_card').css("display", "flex");
    });

    // 덱 편집에서 카드 추가 모달 열기 버튼
	$('.btn-open-adding-card-to-editing-deck').on('click', function () {
		$('.modal_adding_card').attr("data-mode", "edit"); // 편집 모드 설정
		$('.modal_adding_card').css("display", "flex"); // 모달 보이기
	});

	// "추가" 버튼 클릭 시 이벤트 (이벤트 중복 방지)
	    $('.btn-add-card-adding').off('click').on('click', function () {
	        let word = $('#cardWord').val().trim();
	        let pos = $('#cardPos').val().trim();
	        let meaning = $('#cardMeaning').val().trim();
	        let exampleJp = $('#cardExampleJp').val().trim();
	        let exampleKr = $('#cardExampleKr').val().trim();

	        // 빈 입력값 금지
	        if (!word || !pos || !meaning || !exampleJp || !exampleKr) {
	            alert("모든 필드를 입력하세요.");
	            return;
	        }

	        // 현재 모드 확인 (편집 모드 or 새 덱 추가 모드)
	        let mode = $('.modal_adding_card').attr("data-mode");
	        let targetTable = mode === "edit" ? $('.edit_card_list tbody') : $('.card_list tbody');

	        let index = targetTable.find('tr').length; // 현재 행 개수 기준으로 index 설정

	        let tr = $('<tr>');
	        tr.append($('<td>').attr('id', 'word-' + index).text(word));
	        tr.append($('<td>').attr('id', 'pos-' + index).text(pos));
	        tr.append($('<td>').attr('id', 'meaning-' + index).text(meaning));
	        tr.append($('<td>').attr('id', 'exampleJp-' + index).text(exampleJp));
	        tr.append($('<td>').attr('id', 'exampleKr-' + index).text(exampleKr));

	        let deleteBtn = $('<button>').text('🗑').on('click', function () {
	            tr.remove();
	        });
	        tr.append($('<td>').append(deleteBtn));

	        // 해당 테이블에 추가
	        targetTable.append(tr);

	        // 입력 필드 초기화
	        $('#cardWord, #cardPos, #cardMeaning, #cardExampleJp, #cardExampleKr').val('');

	        // 모달 닫기
	        $('.modal_adding_card').css("display", "none");
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
