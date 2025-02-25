$(document).ready(function() {
    let likeVal = document.getElementById('like_check').value
    const boardId = $("boardId").val(); 
    const userId = $("userId").val();
    console.log(userId);
    console.log(likeVal);
    const likeImg = document.getElementById("likeImg")

    if (likeVal > 0) {
        likeImg.src = "src\main\resources\static\images\heart.png";
    } else {
        likeImg.src = "src\main\resources\static\images\empty.png";
    }

    // 좋아요 버튼을 누르면 실행되는 코드
    $("#likeImg").on("click", function() {
        $.ajax({
            url: "/community/like",
            type: "POST",
            data: {'boardId' : boardId, 'userId' : userId},
            success: function(data) {
                if (data == 1) {
                    $("#likeImg").attr("src", "src\main\resources\static\images\heart.png");
                    location.href = "/community" + boardId;
                } else {
                    $('#likeImg').attr("src", "src\main\resources\static\images\empty.png")
                    location.href = "/community" + boardId;
                }
            }, error: function() {
                $("#likeImg").attr("src", "src\main\resources\static\images\heartlock.png");
                console.log('뭐가 틀린 걸까');
            }
        });
    });
});