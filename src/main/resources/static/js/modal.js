$(document).ready(function () {
            var fileInput = $('#importFile');
            var tableBody = $('.card_list tbody');
            var modal = $('#modal_importing_deck');
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

                        console.log("엑셀 데이터:", jsonData); // 디버깅용
                        tableBody.empty();

                        $.each(jsonData.slice(1), function (index, row) {
                            if (row.length < 5) return;
                            var tr = $('<tr>');

                            var ids = ['word', 'pos', 'meaning', 'exampleJp', 'exampleKr']; // 각 td의 id 목록
                            $.each(row, function (i, cell) {
                                var td = $('<td>').text(cell);
                                td.attr('id', ids[i] + '-' + index); // 각 td에 id 추가 (중복 방지를 위해 index 추가)
                                tr.append(td);
                            });

                            var deleteBtn = $('<button>').text('🗑').on('click', function () {
                                tr.remove();
                            });

                            tr.append($('<td>').append(deleteBtn));
                            tableBody.append(tr);
                        });

                        modal.addClass('active');
                    };

                    reader.onerror = function (error) {
                        console.error("파일 읽기 오류:", error);
                    };
                }
                $(this).val('');
            });

            $('.btn-submit-deck').on('click', function () {
                // 임시값
                var example = '1';

                var deckName = $('#deckName').val().trim(); // deckName 값 가져오기
                if (!deckName) {
                    deckName = $('#deckName2').val().trim(); // deckName이 비어있으면 deckName2 사용
                }
                var categoryName = $('.categoryName').val();
                var cardDTOList = [];
                var deckDTO = {
                    // categoryId: $('#categoryId').val(),
                    // userId: $('#userId').val(),  // 현재 로그인한 사용자 ID
                    // 임시 카테고리ID
                    categoryId: example,
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
            });

            $('.btn-exporting-deck').on('click', function () {
                var deckId = prompt("내보낼 덱 ID를 입력하세요:");

                if (!deckId) {
                    alert("덱 ID를 입력하세요.");
                    return;
                }

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

            
            
            let editingRow = null; // 현재 편집 중인 행을 저장할 변수
         
            // 카드 편집 모달 열기
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

                        // 덱 정보 설정
                        $('#editDeckId').val(data.deck.deckId);
                        $('#editDeckName').val(data.deck.deckName);
                        $('#editCategoryName').val(data.deck.categoryId);

                        // 카드 리스트 업데이트
                        let tbody = $('.edit_card_list tbody');
                        tbody.empty();

                        $.each(data.cardList, function (index, card) {
                            let tr = $('<tr>');
                            tr.append($('<td>').text(card.word));
                            tr.append($('<td>').text(card.pos));
                            tr.append($('<td>').text(card.meaning));
                            tr.append($('<td>').text(card.exampleJp));
                            tr.append($('<td>').text(card.exampleKr));

                            // 숨겨진 cardId 저장
                            let hiddenInput = $('<input type="hidden">').val(card.cardId);
                            tr.append(hiddenInput);

                            // 편집 버튼 추가
                            let editBtn = $('<button>').text('편집').on('click', function () {
                                openEditCardModal(tr);
                            });

                            // 삭제 버튼 추가
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

                // 기존 데이터 가져오기 (class 선택자로 변경)
                let word = row.find('td:eq(0)').text();
                let pos = row.find('td:eq(1)').text();
                let meaning = row.find('td:eq(2)').text();
                let exampleJp = row.find('td:eq(3)').text();
                let exampleKr = row.find('td:eq(4)').text();
                let cardId = row.find('input[type=hidden]').val();

                // 모달 창의 입력 필드에 값 설정
                $('.modal_editing_card .cardWord').val(word);
                $('.modal_editing_card .cardPos').val(pos);
                $('.modal_editing_card .cardMeaning').val(meaning);
                $('.modal_editing_card .cardExampleJp').val(exampleJp);
                $('.modal_editing_card .cardExampleKr').val(exampleKr);
                $('.modal_editing_card .cardId').val(cardId); // 숨겨진 cardId 저장

                // 모달 표시
                $('.modal_editing_card').css("display", "flex");
            }

            // 카드 수정 완료 버튼 클릭 시
            $('.btn-save-card').off('click').on('click', function () {
                if (editingRow) {
                    let updatedWord = $('#cardWord').val().trim();
                    let updatedPos = $('#cardPos').val().trim();
                    let updatedMeaning = $('#cardMeaning').val().trim();
                    let updatedExampleJp = $('#cardExampleJp').val().trim();
                    let updatedExampleKr = $('#cardExampleKr').val().trim();
                    let cardId = $('#cardId').val(); // cardId 유지

                    editingRow.find('td:eq(0)').text(updatedWord);
                    editingRow.find('td:eq(1)').text(updatedPos);
                    editingRow.find('td:eq(2)').text(updatedMeaning);
                    editingRow.find('td:eq(3)').text(updatedExampleJp);
                    editingRow.find('td:eq(4)').text(updatedExampleKr);
                    editingRow.find('input[type=hidden]').val(cardId); // 기존 cardId 유지

                    $('.modal_editing_card').css("display", "none");
                    editingRow = null; // 초기화
                }
            });

            // 모달 닫기 버튼
            $('.btn-close-modal').on('click', function () {
                $('.modal_editing_card').css("display", "none");
                editingRow = null;
            });

            // 덱 수정 완료 버튼 클릭 시
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
                        cardId: $(this).find('input[type=hidden]').val(), // 숨겨진 cardId 포함
                        word: $(this).find('td:eq(0)').text(),
                        pos: $(this).find('td:eq(1)').text(),
                        meaning: $(this).find('td:eq(2)').text(),
                        exampleJp: $(this).find('td:eq(3)').text(),
                        exampleKr: $(this).find('td:eq(4)').text()
                    };
                    updatedCards.push(card);
                });

                // 덱 정보 업데이트 요청
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

                // 카드 목록 업데이트 요청
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
		
		
		
		$(document).ready(function () {
		       let modals = [
		           $('.modal_adding_category'),
		           $('.modal_editing_category'),
		           $('.modal_adding_menu'),
		           $('.modal_adding_deck'),
		           $('.modal_importing_deck'),
		           $('.modal_adding_card'),
		           $('.modal_adding_card2'),
		           $('.modal_editing_deck')

		       ];

		       function openModal(modal) {
		           modal.css("display", "flex");
		       }

		       function closeOpenModals() {
		           modals.forEach(modal => {
		               if (modal.css("display") === "flex") {
		                   modal.css("display", "none");
		               }
		           });
		       }

		       $('.btn-open-adding-category-modal').click(function () {
		           openModal($('.modal_adding_category'));
		       });

		       $('.btn-open-editing-category-modal').click(function () {
		           openModal($('.modal_editing_category'));
		       });

		       $('.btn-open-adding-menu-modal').click(function () {
		           openModal($('.modal_adding_menu'));
		       });

		       $('.btn-open-adding-deck-modal').click(function () {
		           openModal($('.modal_adding_deck'));
		           $('.card_list tbody').empty();
		       });

		       $('.btn-open-adding-card-modal').on('click', function () {
		           openModal($('.modal_adding_card'));
		       });
		       
		    	// 덱 편집에서 카드 추가 버튼 사용시 동작
		       $('.btn-open-adding-card-to-editing-deck').on('click', function () {
		       	openModal($('.modal_adding_card2'));
		       });
		       
		       $('.btn-close-modal').click(function () {
		           closeOpenModals();
		       });


		       $('.btn-close-modal').on('click', function () {
		           $('.modal_adding_card').css("display", "none");
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
		   });