$(document).ready(function() {
    // boardId와 userId는 HTML의 hidden input에서 가져옵니다.
    const boardId = $("#boardId").val(); 
    const userId = $("#loginId").val();
    console.log("boardId:", boardId, "userId:", userId);
    
    // 좋아요 개수나 상태를 표시할 요소가 있다면 사용합니다.
    let likeVal = parseInt($("#likeCount").text()); // 좋아요 개수를 표시하는 span 요소
    const likeImg = document.getElementById("likeImg");

    // 초기 상태에 따라 이미지 설정
    if (likeVal > 0) {
        likeImg.src = "/images/heart.png";
    } else {
        likeImg.src = "/images/empty.png";
    }

    // 좋아요 버튼 클릭 시 처리 (버튼에 이벤트를 걸어줍니다)
    $("#likeButton").on("click", function() {
        $.ajax({
            url: "/boardLikes/" + boardId,
            type: "POST",
            data: { 'userId': userId },
            success: function(data) {
                // data는 서버에서 좋아요 처리 결과를 반환한다고 가정 (예: 1이면 좋아요 등록, 0이면 좋아요 취소)
                if (data == 1) {
                    $("#likeImg").attr("src", "/images/heart.png");
                } else {
                    $("#likeImg").attr("src", "/images/empty.png");
                }
                // 좋아요 개수 업데이트를 위해 별도 AJAX GET 요청 (Controller의 /api/likes/{boardId}/count 엔드포인트 활용)
                $.ajax({
                    url: "/boardLikes/" + boardId + "/count",
                    type: "GET",
                    success: function(countData) {
                        $("#likeCount").text(countData);
                    }
                });
            }, 
            error: function() {
                $("#likeImg").attr("src", "/images/heartlock.png");
                console.log('오류 발생');
            }
        });
    });
});
