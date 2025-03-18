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
        const isChecked = $('#copyrightCheck').is(':checked');

        if (!isChecked) {
            e.preventDefault();
            alert('저작권 확인을 체크해주세요!');
            return false;
        }

        // 추가 검증이 필요하면 여기에 작성
        // 예: 제목 비었는지 검사
        const title = $('#title').val().trim();
        if (title === '') {
            e.preventDefault();
            alert('덱 제목을 입력해주세요!');
            $('#title').focus();
            return false;
        }
    });
    // 파일 크기 검사
    $('input[name="uploadFile"]').on('change', function () {
        const file = this.files[0];
        
        if (file) {
            const maxSize = 10 * 1024 * 1024; // 10MB 제한
            if (file.size > maxSize) {
                alert("파일 크기가 너무 큽니다. 10MB 이하 파일만 업로드 가능합니다.");
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
