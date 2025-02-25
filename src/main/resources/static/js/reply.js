/**
 * 
*/
$(function() {
    init(); // 전체 댓글을 읽어서 화면에 출력

    // 댓글 입력
    $('#replyBtn').on("click", replyInsert);
    $('#replyUpdateProc').on("click", replyUpdateProc);
    $('#replyCancel').on("click", replyCancel);

})

// 댓글 전체 조회
function init() {
    let boardId = $('#boardId').val();
    let sendData = {"boardId": boardId};

    $.ajax({
        url: "/reply/replyAll",
        method: 'GET',
        data: sendData,
        success: output,
    })
}

// 댓글 출력
function output(resp) {
    let loginId = $('#loginId').val();
    let tag = `
                <table>
                <tr>
                    <th>번호</th>
                    <th>내용</th>
                    <th>글쓴이</th>
                    <th>날짜</th>
                    <th></th>
                </tr>
                `;

    $.each(resp, function(index, item) { // resp = [{},{},{}]
            tag += `
                    <tr>
                        <td class="no">${index + 1}</td>
                        <td class="content">${item['replyContent']}</td>
                        <td class="writer">${item['replyWriter']}</td>
                        <td class="date">${item['createDate']}</td>
                        <td class="btns">
                            <input type="button" value="삭제" class="btn btn-danger deleteBtn"
                                data-seq="${item['replyId']}"
                                ${item['replyWriter'] == loginId ? '' : 'disabled'}>
                            <input type="button" value="수정" class="btn btn-secondary updateBtn"
                                data-seq="${item['replyId']}"
                                ${item['replyWriter'] == loginId ? '' : 'disabled'}>
                        </td>
                    </tr>
                    `

        });

        tag += `</table>`;
        $('#reply_list').html(tag);
    
        $('.deleteBtn').on('click', replyDelete);
        $('.updateBtn').on('click', replyUpdate);

}

/* 댓글 삭제 함수*/
function replyDelete() {
    let replyId = $(this).attr("data-seq");
    
    let answer = confirm("정말 삭제하시겠습니까?");

    if(!answer) return;

    $.ajax({
        url: "/reply/replyDelete",
        method: 'GET',
        data: { "replyId" : replyId },
        success: init
    })
}

/* 댓글 수정을 위한 조회 함수*/
function replyUpdate() {
    let replyId = $(this).attr('data-seq');

    $.ajax ({
        url: '/reply/replyUpdate',
        method: 'GET',
        data: {"replyId" : replyId},
        success: function (resp) {
            
            // 그러니까 정확하게는 꽂고 나서,
            let content = resp['replyContent'];

            $('#replyContent').val(content);
            
            // 요롷게 버튼을 뒤집어야지~ (입력창은 보이고 수정창이 보인다거나~ 혹은 그 반대거나~)
            $('#replyBtn').css('display', 'none');
            $('#replyUpdateProc').css('display', 'inline-block');
            $('#replyCancel').css('display', 'inline-block');

            $("#replyUpdateProc").attr("data-seq", replyId);
        }
    });

}

/* 댓글 수정 처리 함수*/
function replyUpdateProc() {
    let replyId = $(this).attr('data-seq');
    let updatedReply = $('#replyContent').val();

    $.ajax ({
        url: '/reply/replyUpdateProc',
        method: "POST",
        data: {"replySeq" : replySeq, "updatedReply": updatedReply},
        success: function() {
            init();
            replyCancel();
            
        }

    })


}

/* 댓글 수정 취소 함수*/
function replyCancel() {
    // 버튼을 뒤집는다.
    $('#replyBtn').css('display', 'inline-block');
    $('#replyUpdateProc').css('display', 'none');
    $('#replyCancel').css('display', 'none');

    $('#replyContent').val('');
}

//댓글 입력
function replyInsert() {
    let replyContent = $('#replyContent').val();
    let boardId = $('#boardId').val();

    //얘가 댓글을 한 글자도 안 쳤을 수도 있으니까 확인해보자
    if (replyContent.trim().length == 0) {
        alert("댓글을 입력하세요.");
        return;
    }

    let sendData = { "boardId": boardId, "replyContent" : replyContent };

    // ajax로 댓글 송신
    $.ajax ({
        url: '/reply/replyInsert',
        method: 'POST',
        data: sendData,
        success: function(resp) {
            init();
            $('#replyContent').val('');
        }
    });
}