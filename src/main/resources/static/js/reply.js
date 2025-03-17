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

});

// 댓글 전체 조회
function init() {
    let boardId = $('#boardId').val();
    let sendData = {"boardId": boardId};

    $.ajax({
        url: "/reply/replyAll",
        method: 'GET',
        data: sendData,
        success: function(resp) {
            // replyId 기준 오름차순 정렬 (작은 replyId가 먼저, 즉 오래된 reply부터)
            resp.sort((a, b) => a.replyId - b.replyId);

            // 정렬된 데이터를 가지고 화면에 출력 (트리 구조로 변환 후)
            output(resp);
        }
    });
}

// ▼▼▼ 댓글 출력 (트리 구조로 변환 후 재귀 렌더링) ▼▼▼
function output(resp) {
    let loginName = $('#loginId').val();

    // 1. 모든 댓글을 Map에 저장하고, 각 댓글 객체에 children 배열을 추가
    let commentMap = {};
    resp.forEach(item => {
        item.children = []; // 자식(대댓글)들을 담을 배열
        commentMap[item.replyId] = item;
    });

    // 2. 루트 댓글(부모가 없는 댓글)과 자식 댓글을 분류
    let rootComments = [];
    resp.forEach(item => {
        if (item.parentReplyId && item.parentReplyId != 0) {
            // 대댓글인 경우: 부모 댓글의 children에 푸시
            let parent = commentMap[item.parentReplyId];
            if (parent) {
                parent.children.push(item);
            }
        } else {
            // parentReplyId가 0인 경우(최상위 댓글)
            rootComments.push(item);
        }
    });

    // 3. 최상위 댓글부터 재귀적으로 HTML을 구성
    let html = `<div class="comment-list">`;
    rootComments.forEach(root => {
        html += renderComment(root, loginName);
    });
    html += `</div>`;

    // 4. 댓글 영역에 삽입
    $('#reply_list').html(html);

    // 5. 이벤트 바인딩
    $('.deleteBtn').on('click', replyDelete);
    $('.updateBtn').on('click', replyUpdate);
}

/**
 * 단일 댓글(item) + 자식 댓글(대댓글)을 재귀적으로 HTML로 렌더링
 */
function renderComment(item, loginName) {
    // parentReplyId가 0이 아니면 대댓글
    let isChild = (item.parentReplyId && item.parentReplyId != 0);
    let indentClass = isChild ? 'comment-child' : 'comment-parent';

    // userName이 현재 로그인한 사용자와 일치하면 삭제 버튼 표시, 아니면 표시 안 함
    let deleteButton = (item.userName === loginName)
        ? `<button type="button" class="btn btn-danger deleteBtn"
                   data-seq="${item.replyId}">
             삭제
           </button>`
        : '';

    // 최상위 댓글(부모 댓글)일 때만 "댓글" 버튼 노출
    let replyButton = !isChild
        ? `<button type="button" class="btn btn-info replyChildBtn"
                   data-seq="${item.replyId}">
               댓글
           </button>`
        : '';

    // 현재 댓글의 HTML
    let html = `
        <div class="comment-item ${indentClass}">
            <div class="comment-header">
                <span class="comment-writer">${item.userName}</span>
                <span class="comment-date">${item.createAt}</span>
            </div>
            <div class="comment-content">
                ${item.content}
            </div>
            <div class="comment-buttons">
                ${replyButton}
                ${deleteButton}
            </div>
    `;

    //  자식(대댓글)이 있으면 재귀 호출
    if (item.children && item.children.length > 0) {
        item.children.forEach(child => {
            html += renderComment(child, loginName);
        });
    }

    html += `</div>`; // .comment-item 닫기
    return html;
}

// ▼▼▼ 이하 기존 함수들은 그대로 ▼▼▼

// 대댓글 입력 폼 보이기 (대댓글 버튼 클릭 시)
function showReplyChildInput() {
    let parentReplyId = $(this).attr('data-seq');
    let parentComment = $(this).closest('.comment-item');
    // 이미 입력폼이 있다면 중복 생성 방지
    if (parentComment.find('.childReplyInput').length > 0) {
         return;
    }
    
    // 대댓글 입력폼 HTML 구성
    let childReplyInputHtml = $(`
       <div class="childReplyInput" style="margin-top:10px; padding-left:30px;">
          <input type="text" class="childReplyContent form-control" placeholder="대댓글 입력">
          <button type="button" class="btn btn-primary submitChildReply">등록</button>
          <button type="button" class="btn btn-secondary cancelChildReply">취소</button>
       </div>
    `);
    
    // 해당 댓글 요소(.comment-item) 하단에 입력폼 추가
    parentComment.append(childReplyInputHtml);
    
    // 등록 버튼 클릭 시 처리
    childReplyInputHtml.find('.submitChildReply').on('click', function(){
         let childContent = childReplyInputHtml.find('.childReplyContent').val();
         if(childContent.trim().length === 0) {
             alert("대댓글을 입력하세요.");
             return;
         }
         // 서버에 대댓글 등록을 위한 함수 (replyInsert) 호출
         replyInsert(childContent, parentReplyId);
         childReplyInputHtml.remove();
    });
    
    // 취소 버튼 클릭 시 입력폼 제거
    childReplyInputHtml.find('.cancelChildReply').on('click', function(){
         childReplyInputHtml.remove();
    });
}

// 이벤트 위임
$(document).on('click', '.replyChildBtn', showReplyChildInput);

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
