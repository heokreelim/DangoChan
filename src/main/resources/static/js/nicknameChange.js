$(document).ready(function () {
    $("#editNickname").click(function (event) {
        event.preventDefault(); // 기본 링크 동작 방지
        
        let $userName = $("#userName");
        let $nickNameInput = $("#nickNameInput");
        let originalNickname = $userName.text().trim();
        let newNickname = $nickNameInput.val().trim();
        
        if ($nickNameInput.is(":visible")) { 
            // 닉네임 입력창이 보이는 상태에서 엔터 또는 버튼 클릭 시 실행
            if (originalNickname === newNickname) {
                // 변경 없음 -> 단순 토글
                toggleNickname();
            } else {
                // 변경된 경우 -> AJAX 요청
                $.ajax({
                    type: "POST",
                    url: "/user/nickNameChange",
                    data: { 'nickName': newNickname },
                    success: function (response) {
                        if (response === true) {
                            alert("정상적으로 변경되었습니다.");
                            $userName.text(newNickname);
                            $nickNameInput.val(newNickname);
                        } else {
                            alert("변경에 실패했습니다.");
                            $userName.text(originalNickname);
                            $nickNameInput.val(originalNickname);
                        }
                        toggleNickname();
                    },
                    error: function () {
                        alert("서버 요청에 실패했습니다.");
                        $userName.text(originalNickname);
                        $nickNameInput.val(originalNickname);
                        toggleNickname();
                    }
                });
            }
        } else {
            // 닉네임 입력창이 보이지 않는 경우 -> 토글만 실행
            toggleNickname();
        }
    });

    function toggleNickname() {
        $("#userName").toggle();
        $("#nickNameInput").toggle();
    }
});