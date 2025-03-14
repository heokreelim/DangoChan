// 전역 선언 (파일 가장 위쪽)
let modals = [];


// 모달 열기 함수
function openModal(modal) {
    closeOpenModals(); // 기존 모달 닫기
    modal.css('display', 'flex');
}

// 모든 열려있는 모달 닫기 함수
function closeOpenModals() {
    modals.forEach(modal => {
        if (modal.css("display") === "flex") {
            modal.css("display", "none");
        }
    });
}


$(document).ready(function () {

    /***************** 공통 모달 관련 함수 *****************/
    // 열려있는 모달들을 저장한 배열
    modals = [
        $('.modal_adding_category'),
        $('.modal_editing_category'),
        $('.modal_adding_menu'),
        $('.modal_adding_deck'),
        $('.modal_importing_deck'),
        $('.modal_adding_card'),
        $('.modal_adding_card2'),
        $('.modal_editing_deck'),
        $('.modal_editing_card'),
        $('.modal_change_profile_image')
    ];


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

    // 카테고리 편집 모달 열기 버튼
    $('.btn-open-editing-category-modal').click(function () {
        // 클릭된 버튼의 부모 요소에서 category 정보를 찾음
        let categoryDiv = $(this).closest(".category"); // jQuery로 변경
        let categoryId = categoryDiv.find("input[type='hidden']").val(); // categoryId 가져오기
        let categoryName = categoryDiv.find("b").text(); // categoryName 가져오기

        // 모달의 입력 필드에 값 설정
        $('#categoryId_E1').val(categoryId);
        $('#categoryNameDisplay').text(categoryName);
        $('#categoryName_E1').val(categoryName);
        openModal($('.modal_editing_category'));
    });

    // 덱 추가 모달 열기 버튼
    $('.btn-open-adding-deck-modal').on("click", function () {
        $(this).attr("data-mode", "add"); // 덱 추가 모드
        openModal($('.modal_adding_deck'));
        $('.card_list tbody').empty();
    });

    // 덱 추가/불러오기 버튼
    $('.btn-adding-deck').on('click', function () {
        let mode = $(".modal_adding_deck").css("display") === "flex" ? "add" : "import";

        let deckName, categoryId;
        if (mode === "add") {
            deckName = $('#deckName_A2').val().trim();
            categoryId = $('#categoryId_A2').val();
            targetTable = $('.modal_adding_deck .card_list tbody'); // 💡 특정 모달의 카드 리스트만 가져오도록 변경
        } else {
            deckName = $('#deckName_I2').val().trim();
            categoryId = $('#categoryId_I2').val();
            targetTable = $('.modal_importing_deck .card_list tbody'); // 💡 특정 모달의 카드 리스트만 가져오도록 변경
        }

        if (!deckName) {
            alert("덱 이름을 입력해주세요!");
            return;
        }
        var cardDTOList = [];
        var deckDTO = {
            // 임시 카테고리ID
            categoryId: categoryId,
            deckName: deckName
        };
        // **해당 모달의 카드 목록만 가져오도록 변경**
        targetTable.find('tr').each(function () {
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
                window.location.href = "/home";
            },
            error: function (xhr, status, error) {
                console.error("오류 발생:", error);
            }
        });
        // 덱 이름 초기화
        $('#deckName_A2, #deckName_I2').val('');
        location.reload(true);

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

        if (mode === "new") {
            tr.append($('<td>').attr('id', 'word-' + index).text(word));
            tr.append($('<td>').attr('id', 'pos-' + index).text(pos));
            tr.append($('<td>').attr('id', 'meaning-' + index).text(meaning));
            tr.append($('<td>').attr('id', 'exampleJp-' + index).text(exampleJp));
            tr.append($('<td>').attr('id', 'exampleKr-' + index).text(exampleKr));

            let deleteBtn = $('<button>').attr('class', 'btn-delete-card').text('🗑').on('click', function () {
                tr.remove();
            });
            tr.append($('<td>').append(deleteBtn));
        } else {
            // 편집 버튼을 앞쪽으로 배치
            let editBtn = $('<button>').text('편집').on('click', function (event) {
                event.preventDefault();
                event.stopPropagation();
                $('.modal_editing_card').attr("data-mode", "home");
                openEditCardModal(tr);
            });

            let deleteBtn = $('<button>').attr('class', 'btn-delete-card').text('🗑').on('click', function () {
                tr.remove();
            });

            // 편집 버튼을 첫 번째 열에 추가
            tr.append($('<td>').append(editBtn));
            tr.append($('<td>').text(word));
            tr.append($('<td>').text(pos));
            tr.append($('<td>').text(meaning));
            tr.append($('<td>').text(exampleJp));
            tr.append($('<td>').text(exampleKr));
            tr.append($('<td>').append(deleteBtn));

        }

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
        $(this).attr("data-mode", "import"); // 덱 불러오기 모드
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

                    var deleteBtn = $('<button>').attr('class', 'btn-delete-card').text('🗑').on('click', function () {
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

    // 덱 편집 모달 열기 버튼 (변경: categoryDiv -> deckDiv 사용)
    $('.btn-editing-deck').on("click", function () {
        let deckDiv = $(this).closest(".deck-wrap");
        let deckId = deckDiv.find("input[type='hidden']").val();
        let deckName = deckDiv.find("b").text();
        console.log(deckName);

        $('#editDeckId').val(deckId);
        $('#editDeckName').val(deckName);
        $('#deckNameDisplay').text(deckName);

        openModal($('.modal_editing_deck'));
    });


    // 덱 편집 모달 열기
    $('.btn-open-editing-deck-modal').on('click', function () {
        let deckDiv = $(this).closest(".deck-wrap");
        let deckId = deckDiv.find("input[type='hidden']").val();
        let deckName = deckDiv.find("b").text();
        $('#deckNameDisplay').text(deckName);

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
                    // 편집 버튼을 앞쪽으로 배치
                    let editBtn = $('<button>').text('편집').on('click', function (event) {
                        event.preventDefault();
                        event.stopPropagation();
                        $('.modal_editing_card').attr("data-mode", "home");
                        openEditCardModal(tr);
                    });

                    let deleteBtn = $('<button>')
                        .text('🗑')
                        .addClass('btn-delete-card');

                    // 편집 버튼을 첫 번째 열에 추가
                    tr.append($('<td>').append(editBtn));
                    tr.append($('<td>').text(card.word));
                    tr.append($('<td>').text(card.pos));
                    tr.append($('<td>').text(card.meaning));
                    tr.append($('<td>').text(card.exampleJp));
                    tr.append($('<td>').text(card.exampleKr));
                    tr.append($('<td>').append(deleteBtn));

                    let hiddenInput = $('<input type="hidden">').val(card.cardId);
                    tr.append(hiddenInput);

                    tbody.append(tr);
                });


                $('.modal_editing_deck').css("display", "flex");
            },
            error: function () {
                alert("덱 정보를 불러오는 중 오류 발생.");
            }
        });
    });

    let deletedCardIds = [];  // 삭제할 카드 ID 저장 배열

    // 카드 삭제 버튼 클릭 이벤트 리스너
    $('.edit_card_list tbody').on('click', '.btn-delete-card', function () {
        let row = $(this).closest('tr');
        let cardId = row.find('input[type=hidden]').val();

        if (cardId) {
            deletedCardIds.push(cardId);  // 삭제된 카드 ID 저장
            console.log("삭제된 카드 ID 목록:", deletedCardIds);  // 디버깅용
        }

        row.remove(); // 행 삭제
    });

    // 카드 편집 모달 열기 함수
    function openEditCardModal(row) {
        editingRow = row; // 수정할 행 저장
        let word = row.find('td:eq(1)').text();
        let pos = row.find('td:eq(2)').text();
        let meaning = row.find('td:eq(3)').text();
        let exampleJp = row.find('td:eq(4)').text();
        let exampleKr = row.find('td:eq(5)').text();
        let cardId = row.find('input[type=hidden]').val(); // 카드 ID

        // 모달에 데이터 채우기
        $('#editCardId').val(cardId);
        $('#editCardWord').val(word);
        $('#editCardPos').val(pos);
        $('#editCardMeaning').val(meaning);
        $('#editCardExampleJp').val(exampleJp);
        $('#editCardExampleKr').val(exampleKr);

        // 모달 보이기
        $('.modal_editing_card').css("display", "flex");
    }

    // 카드 수정 완료 버튼 클릭
    // mode가 home일일 때
    $('.btn-save-card').on('click', function () {
        let mode = $('.modal_editing_card').attr("data-mode");

        if (mode === "home") {
            if (!editingRow) return;
            // 화면에만 반영
            let word = $('#editCardWord').val().trim();
            let pos = $('#editCardPos').val().trim();
            let meaning = $('#editCardMeaning').val().trim();
            let exampleJp = $('#editCardExampleJp').val().trim();
            let exampleKr = $('#editCardExampleKr').val().trim();

            // 수정된 내용 원래 행에 반영
            editingRow.find('td:eq(1)').text(word);
            editingRow.find('td:eq(2)').text(pos);
            editingRow.find('td:eq(3)').text(meaning);
            editingRow.find('td:eq(4)').text(exampleJp);
            editingRow.find('td:eq(5)').text(exampleKr);

            // 모달 닫기
            $('.modal_editing_card').css("display", "none");

        } else if (mode === "flashcard") {
            // 서버로 수정된 데이터 전송
            console.log('저장 보내기');
            let cardDTO = {
                cardId: $('#editCardId').val(),
                word: $('#editCardWord').val().trim(),
                pos: $('#editCardPos').val().trim(),
                meaning: $('#editCardMeaning').val(),
                exampleJp: $('#editCardExampleJp').val(),
                exampleKr: $('#editCardExampleKr').val()
            };
            console.log(cardDTO);

            // 서버로 수정된 데이터 전송
            $.ajax({
                type: "POST",
                url: "/flashcard/updateFlashcard", // 백엔드의 업데이트 API 경로
                contentType: "application/json",
                data: JSON.stringify(cardDTO),
                success: function (response) {
                    alert("카드가 성공적으로 수정되었습니다.");
                    $('.modal_editing_card').css("display", "none");
                },
                error: function (xhr, status, error) {
                    alert("카드 수정 중 오류가 발생했습니다. 다시 시도해주세요.");
                }
            });

            // 모달 닫기
            $('.modal_editing_card').css("display", "none");
        }

        editingRow = null;
    });

    $('.btn-close-modal').on('click', function () {
        $('.modal_editing_card').css("display", "none");
        editingRow = null;
    });






    // 덱 수정 완료 버튼 클릭
    $('.btn-update-deck').on('click', function () {
        console.log("최종 삭제된 카드 ID 목록:", deletedCardIds);
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
                word: $(this).find('td:eq(1)').text(),
                pos: $(this).find('td:eq(2)').text(),
                meaning: $(this).find('td:eq(3)').text(),
                exampleJp: $(this).find('td:eq(4)').text(),
                exampleKr: $(this).find('td:eq(5)').text(),
                deckId: deckId
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
            data: JSON.stringify({
                updatedCards: updatedCards,
                deletedCardIds: deletedCardIds  // 삭제된 카드 ID도 함께 전송
            })
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

    $('.cardBtn').on('click', function (event) {
        event.preventDefault();
        event.stopPropagation();
        openEditCardModal2();
    });

    function openEditCardModal2() {
        editingRow = $(this).closest('.flashcard-wrap'); // 카드 정보가 있는 컨테이너
        let cardId = $('.word-box span').attr('data-card-id');
        let word = $('.word').val();
        let pos = $('.pos-box').text().trim();
        let meaning = $('.meaning-box').text();
        let exampleJp = $('.example-jp-box').text();
        let exampleKr = $('.example-kr-box').text();
        console.log(word);
        console.log(meaning);
        $('#editCardId').val(cardId);
        $('#editCardWord').val(word);
        $('#editCardPos').val(pos);
        $('#editCardMeaning').val(meaning);
        $('#editCardExampleJp').val(exampleJp);
        $('#editCardExampleKr').val(exampleKr);

        $('.modal_editing_card').attr("data-mode", "flashcard");
        $('.modal_editing_card').css("display", "flex");
    }




    // 덱 내보내기 상호작용
    $('.btn-exporting-deck').on('click', function () {
        let deckDiv = $(this).closest(".deck-wrap");
        let deckId = deckDiv.find("input[type='hidden']").val();

        $.ajax({
            url: "/flashcard/exportDeck?deckId=" + deckId,
            method: "GET",
            xhrFields: { responseType: 'blob' }, // 파일 다운로드 처리
            success: function (data, status, xhr) {
                var filename = xhr.getResponseHeader('Content-Disposition').split('filename=')[1];

                var blob = new Blob([data], { type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" });
                var link = document.createElement("a");
                link.href = window.URL.createObjectURL(blob);
                link.download = filename;
                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);
            },
            error: function (xhr, status, error) {
                alert("덱을 내보내는 중 오류 발생: " + error);
            }
        });
    });

    // 카테고리 이름 수정
    $('#btn-edit-category-name').on('click', function () {
        let categoryNameDisplay = $('#categoryNameDisplay'); // p 태그
        let categoryNameInput = $('#categoryName_E1'); // input 태그
        let isEditing = categoryNameInput.css('display') === 'none'; // 현재 상태 확인

        if (isEditing) {
            // p -> input 변환
            categoryNameInput.val(categoryNameDisplay.text()); // p 태그 값을 input으로 복사
            categoryNameDisplay.hide(); // p 태그 숨김
            categoryNameInput.show().focus(); // input 보이기 및 포커스
            $(this).text('수정 완료'); // 버튼 텍스트 변경
        } else {
            // input -> p 변환
            let newCategoryName = categoryNameInput.val().trim();
            if (newCategoryName !== '') {
                categoryNameDisplay.text(newCategoryName); // 변경된 카테고리 이름 적용
            }
            categoryNameInput.hide(); // input 숨김
            categoryNameDisplay.show(); // p 태그 보이기
            $(this).text('이름 편집'); // 버튼 텍스트 복원
        }
    });

    // 덱 이름 수정 
    $('#btn-edit-deck-name').on('click', function () {
        let deckNameDisplay = $('#deckNameDisplay'); // p 태그
        let deckNameInput = $('#editDeckName'); // input 태그
        let isEditing = deckNameInput.css('display') === 'none'; // 현재 상태 확인

        if (isEditing) {
            // 편집 모드: p -> input 변환
            deckNameInput.val(deckNameDisplay.text()); // 기존 덱 이름을 input으로 복사
            deckNameDisplay.hide(); // p 태그 숨김
            deckNameInput.show().focus(); // input 태그 보이기 및 포커스
            $(this).text('수정 완료'); // 버튼 텍스트 변경
        } else {
            // 완료 모드: input -> p 변환
            let newDeckName = deckNameInput.val().trim();
            if (newDeckName !== '') {
                deckNameDisplay.text(newDeckName); // 변경된 덱 이름 적용
            }
            deckNameInput.hide(); // input 태그 숨김
            deckNameDisplay.show(); // p 태그 보이기
            $(this).text('이름 편집'); // 버튼 텍스트 복원
        }
    });

    // ****** 프로필 사진 변경 추가 ****** //
    // 프로필 이미지 변경 버튼 클릭 시 모달 열기
    $('#changePictureBtn').on('click', function () {
        closeOpenModals(); // 열려있는 모달 닫기
        openModal($('.modal_change_profile_image'));
    });

    // 프로필 이미지 선택 시 테두리 강조 및 선택 값 저장
    let selectedProfileImage = 0; // 초기값 기본 세팅

    // 프로필 이미지 옵션 클릭 시 선택 효과
    $('.profile-image-option').on('click', function () {
        // 기존 선택 해제
        $('.profile-image-option').removeClass('selected');

        // 현재 선택 추가
        $(this).addClass('selected');

        // 선택한 이미지 번호 저장 (필요 시)
        selectedProfileImage = $(this).data('img');
    });




    // 저장 버튼 클릭 시 서버에 전송
    $('.btn-confirm-profile-change').on('click', function () {
        if (selectedProfileImage === undefined || selectedProfileImage === null) {
            alert("프로필 이미지를 선택해주세요.");
            return;
        }

        $.ajax({
            url: '/user/updateProfileImage',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                "profileImageNumber": selectedProfileImage  // ✅ 서버에서 이 값을 기대합니다
            }),
            success: function (response) {
                alert('프로필 이미지가 변경되었습니다!');
                closeOpenModals();
                location.reload();  // 또는 window.location.href = "/mypage";
            },
            error: function (xhr, status, error) {
                alert('프로필 이미지 변경 실패: ' + xhr.responseText);
            }
        });
    });


});







