$(document).ready(() => {
  const userId = $("#userId").val();
  if (!userId) {
    console.warn("userId 값이 없습니다.");
  }

  // 전역: 현재 연도와 월
  let currentYear = new Date().getFullYear();
  let currentMonth = new Date().getMonth();

  let attendanceDates = [];

  // 출석 데이터를 서버에서 가져오는 함수
  const fetchAttendanceData = (year, month, callback) => {
    $.ajax({
      url: "/attendance",
      method: "GET",
      data: {
        userId: userId,
        year: year,
        month: month + 1, // JS는 0부터 시작이므로 +1
      },
      dataType: "json",
      success: (data) => {
        console.log("출석 데이터:", data);
        attendanceDates = data;
        callback(year, month);
      },
      error: (xhr, status, error) => {
        console.error("출석 데이터 로드 실패:", error);
        callback(year, month);
      },
    });
  };

  // 캘린더를 생성하는 함수
// 캘린더를 생성하는 함수
const generateCalendar = (year, month) => {
  $("#currentMonth").text(`${year}년 ${month + 1}월`);

  const firstDay = new Date(year, month, 1).getDay();
  const lastDate = new Date(year, month + 1, 0).getDate();

  let calendarHTML = "";
  let date = 1;

  const formatDate = (year, month, day) => {
    const m = String(month + 1).padStart(2, "0");
    const d = String(day).padStart(2, "0");
    return `${year}-${m}-${d}`;
  };

  for (let i = 0; i < 6; i++) {
    let row = "<tr>";
    for (let j = 0; j < 7; j++) {
      if (i === 0 && j < firstDay) {
        row += "<td></td>"; // 빈 셀
      } else if (date > lastDate) {
        row += "<td></td>"; // 빈 셀
      } else {
        const currentDateStr = formatDate(year, month, date);

        // 출석한 날짜일 경우 체크아이콘 출력
        const cellContent = attendanceDates.includes(currentDateStr)
          ? `<i class="fa-solid fa-check" style="color: green;"></i>`
          : date;

        // 날짜 정보를 data-date 속성에 추가하여 관리
        row += `<td data-date="${date}">${cellContent}</td>`;

        date++;
      }
    }
    row += "</tr>";
    calendarHTML += row;

    if (date > lastDate) break; // 모든 날짜가 채워지면 종료
  }

  $("#calendarBody").html(calendarHTML);

  // 오늘 날짜를 강조하는 함수 호출
  highlightToday(year, month + 1);
};


  // 버튼 이벤트 핸들링
  $("#prevMonth").click(() => {
    currentMonth--;
    if (currentMonth < 0) {
      currentMonth = 11;
      currentYear--;
    }
    fetchAttendanceData(currentYear, currentMonth, generateCalendar);
  });

  $("#nextMonth").click(() => {
    currentMonth++;
    if (currentMonth > 11) {
      currentMonth = 0;
      currentYear++;
    }
    fetchAttendanceData(currentYear, currentMonth, generateCalendar);
  });

  // 초기 로딩
  fetchAttendanceData(currentYear, currentMonth, generateCalendar);
});

function highlightToday(year, month) {
  const today = new Date();

  const todayYear = today.getFullYear();
  const todayMonth = today.getMonth() + 1;  // JS month는 0부터 시작하므로 +1
  const todayDate = today.getDate();

  if (year !== todayYear || month !== todayMonth) return;

  const tdList = document.querySelectorAll('#calendarBody td');

  tdList.forEach(td => {
    const tdDate = parseInt(td.getAttribute('data-date'));

    if (tdDate === todayDate) {
      td.classList.add('today');
    }
  });
}
