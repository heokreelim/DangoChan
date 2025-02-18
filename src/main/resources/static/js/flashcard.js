$(document).ready(function () {
    function startTimer(timerId) {
        let totalSeconds = 0;
        const $timerElement = $("#" + timerId);

        setInterval(() => {
            totalSeconds++;
            let min = Math.floor(totalSeconds / 60);
            let sec = totalSeconds % 60;
            let formattedTime = `${String(min).padStart(2, '0')}:${String(sec).padStart(2, '0')}`;
            $timerElement.text(formattedTime);

            if (timerId === "studyTimer" && formattedTime === "01:00") {
                $timerElement.addClass("red");
                $(".circleBtn").prop("disabled", true).addClass("disabled-btn");
            }
        }, 1000);
    }

    // ✅ 정답 보기 (카드 뒤집기)
    window.showAnswer = function () {
        $(".flashcard-wrap").addClass("show-answer").removeClass("hide-answer");
        $(".answerBtn").hide();
        $(".backBtn").css("display", "flex");
    };

    // 🔄 프론트 화면으로 돌아가기
    window.hideAnswer = function () {
        $(".flashcard-wrap").removeClass("show-answer").addClass("hide-answer");
        $(".answerBtn").show();
        $(".backBtn").css("display", "none");
    };

    // 🎯 ○ △ X 버튼 클릭 시 hideAnswer() 실행
    $(".circleBtn, .triangleBtn, .xBtn").click(function () {
        hideAnswer();
    });

    // 타이머 실행
    startTimer("mainTimer");
    startTimer("studyTimer");
});
