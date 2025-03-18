$(function() {
    // submit 버튼에 click 이벤트 핸들러 등록
    $('#submitBtn').on('click', communityUpdate);
});

function isValidExtension(fileName) {
    // 허용할 파일 확장자를 소문자로 비교 (.tsv, .xlsx)
    const allowedExtensions = [".tsv", ".xlsx"];
    let fileExt = fileName.substring(fileName.lastIndexOf('.')).toLowerCase();
    return allowedExtensions.includes(fileExt);
}

function communityUpdate() {
    // 덱 제목 검증
    let title = $('#title').val();
    if (title.trim().length < 1) {
        alert("덱 제목을 입력해주세요.");
        return false;
    }
    
    // 첨부파일 검증: 기존 첨부파일 정보(hidden input)와 새로 선택한 파일을 확인합니다.
    let originalFile = $('#originalFile').val();
    let fileInput = $('input[name="uploadFile"]').val();
    
    // 기존 첨부파일이 없고, 새 첨부파일도 선택하지 않은 경우
    if ((!originalFile || originalFile.trim().length === 0) && !fileInput) {
        alert("게시글은 반드시 공유할 덱을 첨부파일로 포함해야 합니다.");
        return false;
    }
    
    // 새로 선택한 파일이 있을 경우, 확장자 검증
    if (fileInput) {
        // 파일명에서 경로가 포함되어 있을 수 있으니, 경로를 제외한 파일명만 추출
        let fileName = fileInput.split('\\').pop();
        if (!isValidExtension(fileName)) {
            alert("허용되지 않는 파일 확장자입니다. (.tsv 또는 .xlsx 파일만 업로드 가능합니다.)");
            return false;
        }
    }
    
    // 덱 설명 검증
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
    
    // 모든 조건 만족 시 폼 제출
    return true;
}
