$(document).ready(function () {
    function generateCalendar() {
        let today = new Date();
        let year = today.getFullYear();
        let month = today.getMonth();

        $("#currentMonth").text(`${year}년 ${month + 1}월`);

        let firstDay = new Date(year, month, 1).getDay();
        let lastDate = new Date(year, month + 1, 0).getDate();

        let calendarHTML = "";
        let date = 1;

        for (let i = 0; i < 6; i++) {
            let row = "<tr>";
            let emptyCells = 0; // 빈 칸 개수 카운트

            for (let j = 0; j < 7; j++) {
                if (i === 0 && j < firstDay) {
                    row += "<td></td>";  
                    emptyCells++;
                } else if (date > lastDate) {
                    row += "<td></td>";  // 빈 td 추가
                } else {
                    row += `<td>${date}</td>`;
                    date++;
                }
            }

            row += "</tr>";
            calendarHTML += row;

            // 마지막 날이 지나도 td가 7개가 되도록 보장
            if (date > lastDate) break;
        }

        $("#calendarBody").html(calendarHTML);
    }

    generateCalendar();
});
