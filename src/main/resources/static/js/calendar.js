$(document).ready(function () {
  var userId = $("#userId").val();
  // attendanceDates 배열: 서버에서 받아온 학습 기록에서 date 값만 추출
  var attendanceDates = [];

  // 출석(학습기록) 데이터를 서버에서 가져오는 함수
  function fetchAttendanceData(callback) {
    $.ajax({
      url: "/attendance", // 컨트롤러의 엔드포인트
      method: "GET",
      data: { userId: userId },
      dataType: "json",
      success: function (data) {
        // data는 DeckStudyTimeDTO 객체들의 배열 (각 객체에 date 프로퍼티가 있음)
        attendanceDates = data.map(function(item) {
          return item.date; // 이미 "YYYY-MM-DD" 형태로 변환되어 있다고 가정
        });
        callback();
      },
      error: function (xhr, status, error) {
        console.error("출석 데이터 로드 실패:", error);
        callback(); // 오류가 있어도 캘린더를 생성
      }
    });
  }

  // 캘린더를 생성하는 함수
  function generateCalendar() {
    let today = new Date();
    let year = today.getFullYear();
    let month = today.getMonth();

    $("#currentMonth").text(`${year}년 ${month + 1}월`);

    let firstDay = new Date(year, month, 1).getDay();
    let lastDate = new Date(year, month + 1, 0).getDate();

    let calendarHTML = "";
    let date = 1;

    // 헬퍼 함수: 날짜를 YYYY-MM-DD 형태로 포맷
    function formatDate(year, month, day) {
      let m = String(month + 1).padStart(2, '0');
      let d = String(day).padStart(2, '0');
      return `${year}-${m}-${d}`;
    }

    for (let i = 0; i < 6; i++) {
      let row = "<tr>";

      for (let j = 0; j < 7; j++) {
        if (i === 0 && j < firstDay) {
          row += "<td></td>";  
        } else if (date > lastDate) {
          row += "<td></td>";  
        } else {
          let currentDateStr = formatDate(year, month, date);
          // attendanceDates에 현재 날짜가 있으면 도장 아이콘 표시, 없으면 숫자 표시
          let cellContent = attendanceDates.includes(currentDateStr)
            ? `<i class="fa-solid fa-check" style="color: green;"></i>`
            : date;
          row += `<td>${cellContent}</td>`;
          date++;
        }
      }

      row += "</tr>";
      calendarHTML += row;
      if (date > lastDate) break;
    }

    $("#calendarBody").html(calendarHTML);
  }

  // 출석 데이터를 먼저 가져온 후 캘린더 생성
  fetchAttendanceData(generateCalendar);
});
