$(document).ready(function () {
    
    // 1. 업로드 버튼 클릭 시 숨겨진 파일 input 클릭
    $('.custom-file-upload').on('click', function (e) {
        e.preventDefault();
        $('input[name="uploadFile"]').click();
    });

    // 2. 파일 선택 후 파일명 출력
    $('input[name="uploadFile"]').on('change', function () {
        const file = this.files[0];
        const fileName = file ? file.name : '선택된 파일 없음';
        $('#fileName').text(fileName);
    });

    // 3. 폼 제출 시 유효성 검사
    $('form').on('submit', function (e) {

        // 추가 검증이 필요하면 여기에 작성
        // 예: 제목 비었는지 검사
        const title = $('#title').val().trim();
        if (title.length < 1) {
            e.preventDefault();
            alert('덱 제목을 입력해주세요!');
            $('#title').focus();
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

        // 저작권 체크
        const isChecked = $('#copyrightCheck').is(':checked');

        if (!isChecked) {
            e.preventDefault();
            alert('저작권 확인을 체크해주세요!');
            return false;
        }
    });

    // 파일 크기 검사
    $('input[name="uploadFile"]').on('change', function () {
        const file = this.files[0];
        
        if (file) {
            const maxSize = 100 * 1024 * 1024; // 100MB 제한
            if (file.size > maxSize) {
                alert("파일 크기가 너무 큽니다. 100MB 이하 파일만 업로드 가능합니다.");
                $(this).val(''); // 선택한 파일 초기화
                $('#fileName').text('선택된 파일 없음');
                return;
            }

            $('#fileName').text(file.name);
        } else {
            $('#fileName').text('선택된 파일 없음');
        }
    });


});

function isValidExtension(fileName) {
    // 허용할 파일 확장자를 소문자로 비교 (.tsv, .xlsx)
    const allowedExtensions = [".tsv", ".xlsx"];
    let fileExt = fileName.substring(fileName.lastIndexOf('.')).toLowerCase();
    return allowedExtensions.includes(fileExt);
}
