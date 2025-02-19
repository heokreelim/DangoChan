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

    // ✅ 새 단어 데이터를 비동기적으로 가져오는 함수
    function fetchNewFlashcard(deckId) {
        return $.ajax({
            url: "/flashcard/json", // 🔥 랜덤 단어 API 호출
            type: "GET",
            data: { deckId: deckId },
            dataType: "json"
        });
    }

    // ✅ 정답 보기 (카드 뒤집기)
    window.showAnswer = function () {
        $(".flashcard-wrap").addClass("show-answer").removeClass("hide-answer");
        $(".answerBtn").hide();
        $(".backBtn").css("display", "flex");
    };

    // 🔄 새로운 단어로 변경하는 함수
    function updateFlashcard(data) {
        // ✅ 앞면(Front) 한자 업데이트 (즉시 변경)
        $(".word-box span").text(data.kanji);

        // ✅ 500ms 후에 뒷면 데이터 업데이트 (부드러운 전환)
        setTimeout(() => {
            $("#wordText").text(data.kanji);
            $("#wordFurigana").text(data.furigana);

            // ✅ 뒷면의 한자 + 후리가나 업데이트 (각 한자 위에 후리가나 적용)
            //$(".word-container").html(data.formattedRuby);

            $("#pos").text(data.pos);
            $("#meaning").text(data.meaning);
            $("#example_jp").text(data.exampleJp);
            $("#example_kr").text(data.exampleKr);
        }, 500);
    }

    // ✅ ○ △ X 버튼 클릭 시 새로운 단어 가져오기 + 앞면 전환
    $(".circleBtn, .triangleBtn, .xBtn").click(function () {
        $(".flashcard-wrap").removeClass("show-answer").addClass("hide-answer");
        $(".answerBtn").show();
        $(".backBtn").css("display", "none");

        // 🔥 데이터를 미리 가져와서 한자를 즉시 변경
        fetchNewFlashcard(1) // 🔥 deckId=1 예시
            .done((data) => {
                console.log("🔄 새 단어 데이터:", data);

                // ✅ 먼저 앞면 한자만 변경 (전환 애니메이션과 동시에 보이도록)
                $(".word-box span").text(data.kanji);

                // ✅ 500ms 후에 나머지 데이터 업데이트 (뒷면 애니메이션 후 변경)
                setTimeout(() => {
                    updateFlashcard(data);
                }, 500);
            })
            .fail((error) => {
                console.error("❌ 단어 로드 실패:", error);
            });
    });

    // 타이머 실행
    startTimer("mainTimer");
    startTimer("studyTimer");
});