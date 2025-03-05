/**
 * 
*/
$(function() {
    init(); // 전체 댓글을 읽어서 화면에 출력

    // 댓글 입력 이벤트 ( 일반 댓글 등록 )
    $('#replyBtn').on("click", function() {
        replyInsert();  // parentReplyId 없이 일반 댓글 등록
    });
    $('#replyUpdateProc').on("click", replyUpdateProc);
    $('#replyCancel').on("click", replyCancel);

    // 동적으로 생성되는 대댓글 버튼에 대한 이벤트 위임
    $('#reply_list').on("click", ".replyChildBtn", showReplyChildInput);

})

// 댓글 전체 조회
function init() {
    let boardId = $('#boardId').val();
    let sendData = {"boardId": boardId};

    $.ajax({
        url: "/reply/replyAll",
        method: 'GET',
        data: sendData,
        success: function(resp) {
            // replyId 기준 오름차순 정렬 (작은 replyId가 먼저, 즉 오래된 reply부터.)
            resp.sort((a, b) => a.replyId - b.replyId);

            // 정렬된 데이터를 가지고 화면에 출력
            output(resp);
        }
    })
}

// 댓글 출력
function output(resp) {
    let loginName = $('#loginId').val();
    let tag = `
                <table class="table table-bordered">
                <tr>
                    <th>번호</th>
                    <th>내용</th>
                    <th>글쓴이</th>
                    <th>날짜</th>
                    <th></th>
                </tr>
                `;

    $.each(resp, function(index, item) { // resp = [{},{},{}]
        // console.log("loginId:", loginId, "userName:", item['userName']);
        
        // 대댓글이면 들여쓰기를 적용 (부모 댓글이 0이면 최상위 댓글)
        let indentStyle = "";
        if(item['parentReplyId'] && item['parentReplyId'] != 0) {
            indentStyle = "style= 'padding-left:50px;'";
        }
            tag += `
                    <tr>
                        <td class="no" ${indentStyle}>${index + 1}</td>
                        <td class="content" ${indentStyle}>${item['content']}</td>
                        <td class="writer">${item['userName']}</td>
                        <td class="date">${item['createAt']}</td>
                        <td class="btns">
                            <!-- 댓글 출력 시 각 댓글 행에 "대댓글" 버튼 추가 -->
                            <input type="button" value="대댓글" class="btn btn-info replyChildBtn" 
                                data-seq="${item['replyId']}">
                                <input type="button" value="삭제" class="btn btn-danger deleteBtn"
                                data-seq="${item['replyId']}"
                                ${item['userName'] == loginName ? '' : 'disabled'}>
                        </td>
                    </tr>
                    `

        });

        tag += `</table>`;
        $('#reply_list').html(tag);
    
        $('.deleteBtn').on('click', replyDelete);
        $('.updateBtn').on('click', replyUpdate);

}

// 대댓글 입력 폼 보이기 (대댓글 버튼 클릭 시)
function showReplyChildInput() {
    let parentReplyId = $(this).attr('data-seq');
    // 예시: prompt 대신 input 요소를 동적으로 추가하는 방식
    let parentRow = $(this).closest('tr');
    if (parentRow.next().hasClass('childReplyInputRow')) {
         return;
    }
    
    let inputRow = $(`
       <tr class="childReplyInputRow">
          <td colspan="5" style="padding-left: 30px;">
             <input type="text" class="childReplyContent form-control" placeholder="대댓글 입력">
             <button class="btn btn-primary submitChildReply">등록</button>
             <button class="btn btn-secondary cancelChildReply">취소</button>
          </td>
       </tr>
    `);
    
    parentRow.after(inputRow);
    
    inputRow.find('.submitChildReply').on('click', function(){
         let childContent = inputRow.find('.childReplyContent').val();
         if(childContent.trim().length === 0) {
             alert("대댓글을 입력하세요.");
             return;
         }
         replyInsert(childContent, parentReplyId);
         inputRow.remove();
    });
    
    inputRow.find('.cancelChildReply').on('click', function(){
         inputRow.remove();
    });
}

// 댓글 입력 함수 (일반 댓글 및 대댓글 등록 처리)
function replyInsert(childContent, parentReplyId) {
    let replyContent = (childContent !== undefined) ? childContent : $('#replyContent').val();
    let boardId = $('#boardId').val();

    if (replyContent.trim().length == 0) {
        alert("댓글을 입력하세요.");
        return;
    }

    if(parentReplyId === undefined) {
        parentReplyId = 0;
    } else {
        parentReplyId = parseInt(parentReplyId, 10);
    }

    let sendData = { 
        "boardId": boardId, 
        "content": replyContent,
        "parentReplyId": parentReplyId
    };

    console.log("Sending data:", sendData); // 확인용

    $.ajax ({
        url: '/reply/replyInsert',
        method: 'POST',
        data: sendData,
        success: function(resp) {
            init();
            if(parentReplyId == 0) {
                $('#replyContent').val('');
            }
        },
        error: function(xhr, status, error) {
            console.error("댓글 등록 에러:", error, xhr.responseText);
        }
    });
}

/* 댓글 삭제 함수 */
function replyDelete() {
    let replyId = $(this).attr("data-seq");
    let answer = confirm("정말 삭제하시겠습니까?");
    if(!answer) return;

    $.ajax({
        url: "/reply/replyDelete",
        method: 'GET',
        data: { "replyId": replyId },
        success: init
    });
}

/* 댓글 수정을 위한 조회 함수 */
function replyUpdate() {
    let replyId = $(this).attr('data-seq');

    $.ajax ({
        url: '/reply/replyUpdate',
        method: 'GET',
        data: {"replyId": replyId},
        success: function (resp) {
            let content = resp['content'];
            $('#replyContent').val(content);
            
            $('#replyBtn').css('display', 'none');
            $('#replyUpdateProc').css('display', 'inline-block');
            $('#replyCancel').css('display', 'inline-block');

        }
    });
}


