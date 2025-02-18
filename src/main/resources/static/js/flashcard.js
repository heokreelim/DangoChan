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

    // 🔄 플래시카드 앞면으로 전환
    window.hideAnswer = function () {
        $(".flashcard-wrap").removeClass("show-answer").addClass("hide-answer");
        $(".answerBtn").show();
        $(".backBtn").css("display", "none");
    };

    // ✅ 새로운 랜덤 단어 가져와서 업데이트하는 함수
    function loadNewWord() {
        $.ajax({
            url: "/flashcard/json", // 랜덤 단어를 가져오는 API
            type: "GET",
            dataType: "json",
            success: function (data) {
                console.log("새로운 단어 로드됨:", data); // ✅ 디버깅용 로그

                if (data.kanji && data.furigana && data.pos && data.meaning) {
                    // ✅ 한자 + 후리가나 업데이트
                    $("#word").html(`<ruby><rb>${data.kanji}</rb><rt>${data.furigana}</rt></ruby>`);

                    // ✅ 품사 & 의미 & 예문 업데이트
                    $("#pos").text(data.pos);
                    $("#meaning").text(data.meaning);
                    $("#example_jp").text(data.exampleJp);
                    $("#example_kr").text(data.exampleKr);
                } else {
                    console.error("데이터 필드가 일부 없음:", data);
                }

                // ✅ 카드 앞면으로 전환
                hideAnswer();
            },
            error: function () {
                alert("새로운 단어를 불러오는 중 오류가 발생했습니다.");
            }
        });
    }

    // 🎯 ○ △ X 버튼 클릭 시 새로운 단어 불러오기
    $(".circleBtn, .triangleBtn, .xBtn").click(function () {
        loadNewWord();
    });

    // 타이머 실행
    startTimer("mainTimer");
    startTimer("studyTimer");
});
