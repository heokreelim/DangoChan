function startTimer(timerId) {
    let totalSeconds = 0;
    const timerElement = document.getElementById(timerId);

    setInterval(() => {
        totalSeconds++;
        let min = Math.floor(totalSeconds / 60);
        let sec = totalSeconds % 60;
        let formattedTime = `${String(min).padStart(2, '0')}:${String(sec).padStart(2, '0')}`;
        timerElement.innerText = formattedTime;

        // 1:00이 되는 순간 빨간색으로 변경
        if (timerId === "studyTimer" && formattedTime === "01:00") {
            timerElement.classList.add("red");
        }
    }, 1000);
}

function showAnswer() {
    document.querySelector(".flashcard-wrap").classList.add("show-answer");
}

window.onload = function () {
    startTimer("mainTimer"); // 메인 타이머 시작
    startTimer("studyTimer"); // 공부 타이머 시작
};