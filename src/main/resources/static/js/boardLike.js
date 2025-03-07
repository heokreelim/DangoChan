$(document).ready(function() {
    $('#likeButton').on('click', function() {
        var boardId = $(this).data('board-id');
        // 기존의 loginId 대신(이유: 그냥 헷갈려서...) 새 변수 userNumericId 사용
        var userId = $('#userNumericId').val(); 
        
        $.ajax({
            url: '/boardLikes/' + boardId + '/toggle',
            type: 'POST',
            data: { userId: userId },
            success: function(response) {
                if(response.indexOf("등록") !== -1) {
                    $('#likeImg').attr('src', '/images/heart.png');
                } else {
                    $('#likeImg').attr('src', '/images/empty.png');
                }
                $.ajax({
                    url: '/boardLikes/' + boardId + '/count',
                    type: 'GET',
                    success: function(count) {
                        $('#likeCount').text(count);
                    },
                    error: function() {
                        console.error("좋아요 개수를 불러오는 중 오류 발생");
                    }
                });
            },
            error: function() {
                alert("좋아요 처리 중 오류가 발생했습니다.");
            }
        });
    });
});
