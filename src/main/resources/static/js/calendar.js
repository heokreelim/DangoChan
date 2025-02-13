        $(document).ready(function() {
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
                    for (let j = 0; j < 7; j++) {
                        if (i === 0 && j < firstDay) {
                            row += "<td></td>";  
                        } else if (date > lastDate) {
                            break;
                        } else {
                            row += `<td>${date}</td>`;
                            date++;
                        }
                    }
                    row += "</tr>";
                    calendarHTML += row;
                    if (date > lastDate) break;
                }

                $("#calendarBody").html(calendarHTML);
            }

            generateCalendar();
        });