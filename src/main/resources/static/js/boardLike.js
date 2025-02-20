$(function () {
    $('#boardLike').on('click', toggleLike);
})

function toggleLike(boardId, userId) {
    // 좋아요 상태를 토글하기 위해 URL을 결정합니다.
    // (예시에서는 좋아요 기능만 다루고, 취소 기능은 별도로 구현할 수 있습니다.)
    let url = "/community/like";  // 좋아요 추가 API

    $.ajax({
        type: "POST",
        url: url,
        data: { boardId: boardId, userId: userId },
        success: function(response) {
            // response 예: { success: true, likeCount: 5 }
            if(response.success) {
                // 좋아요 개수를 업데이트합니다.
                $("#likeCount" + boardId).text(response.likeCount);
            } else {
                alert("좋아요 처리에 실패했습니다.");
            }
        },
        error: function(xhr, status, error) {
            console.error("좋아요 처리 중 오류 발생: " + error);
        }
    });
}
