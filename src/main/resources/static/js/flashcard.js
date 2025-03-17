$(document).ready(function () {
    let studyTimerInterval;
    let studyTotalSeconds = 0;
    let isAnswerViewed = false; // ✅ 정답을 본 적이 있는지 확인하는 변수 추가

    // ✅ 백엔드에서 전달된 deckId 가져오기
    let deckId = $("#deckId").val();
    console.log("📌 서버에서 받은 deckId:", deckId);

    function startTimer(timerId) {
        let totalSeconds = 0;
        const $timerElement = $("#" + timerId);

        if (timerId === "studyTimer") {
            studyTotalSeconds = 0;
            clearInterval(studyTimerInterval); // 기존 studyTimer 중지
            studyTimerInterval = setInterval(updateStudyTimer, 1000);
        } else {
            setInterval(() => {
                totalSeconds++;
                let min = Math.floor(totalSeconds / 60);
                let sec = totalSeconds % 60;
                let formattedTime = `${String(min).padStart(2, '0')}:${String(sec).padStart(2, '0')}`;
                $timerElement.text(formattedTime);
            }, 1000);
        }
    }

    function updateStudyTimer() {
        studyTotalSeconds++;
        let min = Math.floor(studyTotalSeconds / 60);
        let sec = studyTotalSeconds % 60;
        let formattedTime = `${String(min).padStart(2, '0')}:${String(sec).padStart(2, '0')}`;
        $("#studyTimer").text(formattedTime);

        if (formattedTime === "01:00") {
            $("#studyTimer").addClass("red");
            $(".circleBtn").prop("disabled", true).addClass("disabled-btn");
        }
    }

    // ✅ studyTimer 리셋 함수 추가
    function resetStudyTimer() {
        clearInterval(studyTimerInterval);
        studyTotalSeconds = 0;
        $("#studyTimer").removeClass("red").text("00:00");
        $(".circleBtn").prop("disabled", false).removeClass("disabled-btn");
        studyTimerInterval = setInterval(updateStudyTimer, 1000);
    }

    // ✅ 새 단어 데이터를 비동기적으로 가져오는 함수
    function fetchNewFlashcard(deckId) {
        return $.ajax({
            url: "/flashcard/json", // 🔥 랜덤 단어 API 호출
            method: "GET",
            data: { deckId: deckId },
            dataType: "json"
        });
    }

    // ✅ 정답 보기 (카드 뒤집기)
    window.showAnswer = function () {
        $(".flashcard-wrap").addClass("show-answer").removeClass("hide-answer");
        $(".answerBtn").hide();
        $(".backBtn").css("display", "flex");
        isAnswerViewed = true; // ✅ 정답을 본 상태로 변경
    };

    // 🔄 새로운 단어로 변경하는 함수
    function updateFlashcard(data) {
        $(".word-box span").text(data.kanji).attr("data-card-id", data.cardId);
        setTimeout(() => {
            $(".word").val(data.word);
            $("#wordText").text(data.kanji);
            $("#wordFurigana").text(data.furigana);
            $(".furigana").html(data.formattedFurigana);
            $("#pos").text(data.pos);
            $("#meaning").text(data.meaning);
            $("#example_jp").text(data.exampleJp);
            $("#example_kr").text(data.exampleKr);
        }, 500);
    }

    // ✅ 모든 카드의 studyLevel이 3인지 확인하는 함수
    function checkStudyLevel(deckId) {
        return $.ajax({
            url: "/flashcard/checkStudyLevel", // 🔥 서버 API 호출
            method: "GET",
            data: { deckId: deckId },
            dataType: "json"
        });
    }

    // ✅ study_level 업데이트 함수
    function updateStudyLevel(cardId, studyLevel) {
        console.log("📌 서버로 보낼 데이터:", { cardId, studyLevel }); // 🔥 콘솔에 데이터 출력

        $.ajax({
            url: "/flashcard/updateStudyLevel", // 🔥 서버 API 호출
            method: "POST",
            data: { cardId: cardId, studyLevel: studyLevel },
            success: function (response) {
                console.log("✅ study_level 및 studied_at 업데이트 성공:", response);

                // ✅ 학습할 새로운 카드 불러오기
                fetchNewFlashcard(deckId)
                    .done((data) => {
                        console.log("🔄 새 단어 데이터:", data);
                        // 만약 응답이 빈 데이터라면 학습 끝
                        if (!data || Object.keys(data).length === 0) {
                            checkStudyLevel(deckId).done((allCompleted) => {
                                if (allCompleted) {
                                    console.log("📌 모든 단어를 학습했습니다. 초기화 진행!");
                                    resetStudyData(deckId);
                                } else {
                                    console.log("공부할 새로운 카드 없음");
                                    alert("공부할 새 카드가 없습니다 홈으로 돌아갑니다~");
                                    goHome();
                                }
                            });
                        }
                        else
                        {
                            $(".word-box span").text(data.kanji);
                            setTimeout(() => {
                                updateFlashcard(data);
                            }, 500);
                        }
                    })
            },
            error: function (error) {
                console.error("❌ study_level 및 studied_at 업데이트 실패:", error);
            }
        });
    }

    function resetStudyData(deckId) {
        let studyTime = $("#mainTimer").text(); // 타이머에서 시간 가져오기 (MM:SS 형식)

        // "00:05" → 5초로 변환
        let timeParts = studyTime.split(":");
        let totalSeconds = parseInt(timeParts[0]) * 60 + parseInt(timeParts[1]);

        console.log("📌 서버로 보낼 데이터:", { deckId, studyTime: totalSeconds });

        $.ajax({
            url: "/flashcard/resetStudyData",
            method: "POST",
            data: { deckId: deckId, studyTime: totalSeconds},
            success: function (response) {
                console.log("✅ 스터디 데이터 초기화 성공:", response);
                alert("🎉 모든 단어 학습 완료! 스터디 데이터를 초기화합니다.");
                window.location.href = "/home"
            },
            error: function (error) {
                console.error("❌ 스터디 데이터 초기화 실패:", error);
            }
        });
    }

    // ✅ ○ △ X 버튼 클릭 시 새로운 단어 가져오기 + 앞면 전환 + study_level 업데이트
    $(".circleBtn, .triangleBtn, .xBtn").click(function () {
        $(".flashcard-wrap").removeClass("show-answer").addClass("hide-answer");
        $(".answerBtn").show();
        $(".backBtn").css("display", "none");

        let studyLevel = 0; // 기본값
        if ($(this).hasClass("circleBtn")) studyLevel = 3; // ○
        if ($(this).hasClass("triangleBtn")) studyLevel = 2; // △
        if ($(this).hasClass("xBtn")) studyLevel = 1; // ✕

        // 🔥 study_level 업데이트 요청
        let cardId = $(".word-box span").attr("data-card-id"); // 카드 ID 가져오기
        updateStudyLevel(cardId, studyLevel);
        // 🔥 studyTimer 리셋 추가
        resetStudyTimer();


    });



    // ✅ goHome()을 수정하여 answerBtn이 눌렸을 때만 저장되도록 함
    window.goHome = function () {
        let studyTime = $("#mainTimer").text(); // 타이머에서 시간 가져오기 (MM:SS 형식)
        // "00:05" → 5초로 변환
        let timeParts = studyTime.split(":");
        let totalSeconds = parseInt(timeParts[0]) * 60 + parseInt(timeParts[1]);

        console.log("📌 서버로 보낼 데이터:", { deckId, studyTime: totalSeconds });

        if (isAnswerViewed) {
            // ✅ 정답을 확인한 경우에만 학습 시간 저장 후 이동
            $.ajax({
                url: "/flashcard/saveStudyTime",
                method: "POST",
                data: { deckId: deckId, studyTime: totalSeconds },
                success: function (response) {
                    console.log("✅ study_time 저장 완료:", response);
                    window.location.href = "/home"; // 저장 후 홈으로 이동
                },
                error: function (error) {
                    console.error("❌ study_time 저장 실패:", error);
                    window.location.href = "/home"; // 저장 실패해도 홈으로 이동
                }
            });
        } else {
            // ✅ 정답을 보지 않았다면 저장 없이 바로 이동
            console.log("⚠️ 정답을 확인하지 않았으므로 저장 없이 이동합니다.");
            window.location.href = "/home";
        }
    };


    startTimer("mainTimer");
    startTimer("studyTimer");
});
