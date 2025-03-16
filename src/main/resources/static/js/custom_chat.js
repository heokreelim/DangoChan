// 커스텀 업로드 파일을 위해 sendFile function 을 가져와 활용 
document.addEventListener('DOMContentLoaded', () => {
    const fileInput = document.getElementById('fileInput');
    const fileNameSpan = document.getElementById('fileName');

    fileInput.addEventListener('change', () => {
        if (fileInput.files.length > 0) {
            fileNameSpan.textContent = fileInput.files[0].name;
        } else {
            fileNameSpan.textContent = '선택된 파일 없음';
        }
    });
});

// roomType 에 따라 timer 를 없앤다다 
$(document).ready(function() {
    // data-roomtype 값을 가져온다.
    const roomType = $('body').data('roomtype');

    console.log("roomType:", roomType); // 디버깅용 출력

    // roomType이 shiritori일 때만 보이게 하고, 아니면 숨김
    if (roomType === 'shiritori') {
        $('.game-info').show();  // 보이기
    } else {
        $('.game-info').hide();  // 숨기기
    }
});
