$(function() {
    // submit 버튼 클릭 시 communityWrite 함수 실행
    $('#submitBtn').on('click', communityWrite);
});

function communityWrite() {
    // 글 제목 검증
    let title = $('#title').val();
    if (title.trim().length < 1) {
        alert("글 제목을 입력해주세요.");
        return false;
    }
   
    // 첨부파일 검증
    let fileInput = $('input[name="uploadFile"]').val();
    if (!fileInput) {
        alert("첨부파일을 선택해주세요.");
        return false;
    }
    
    // 글 내용 검증
    let content = $('#boardContent').val();
    if (content.trim().length < 1) {
        alert("덱 설명을 입력해주세요.");
        return false;
    }

    
    // 저작권 확인 체크 검증
    if (!$('#copyrightCheck').is(":checked")) {
        alert("저작권 확인 체크를 해주세요.");
        return false;
    }
    
    // 모든 조건을 만족하면 폼이 제출됩니다.
    return true;
}
