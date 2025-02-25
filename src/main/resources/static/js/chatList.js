// 채팅방 목록을 서버에서 불러와서 HTML에 표시하는 함수입니다.
function loadRooms() {
    fetch('/chat/rooms')
        .then(response => response.json())
        .then(data => {
            const roomList = document.getElementById('roomList');
            roomList.innerHTML = '';
            data.forEach(room => {
                const div = document.createElement('div');
                div.className = 'room-item';
                // room.userSet가 배열로 전송된다면, 참가 인원 수는 length로 구할 수 있습니다.
                const participantCount = room.userSet ? room.userSet.length : 0;
                // 채팅방 이름 옆에 "n명"을 표시합니다.
                div.textContent = room.name + ' (' + participantCount + '명)';
                // 방을 클릭하면 해당 채팅방으로 이동합니다.
                div.onclick = () => {
                    window.location.href = '/chat/room/' + room.roomId;
                };
                roomList.appendChild(div);
            });
        })
        .catch(err => console.error(err));
}

// 채팅방을 만드는 함수입니다.
function createRoom() {
    const nameInput = document.getElementById('roomName');
    const name = nameInput.value.trim();
    if (!name) {
        alert("채팅방 이름을 입력하세요.");
        return;
    }
    const formData = new FormData();
    formData.append('name', name);
    fetch('/chat/room', {
        method: 'POST',
        body: formData
    })
        .then(response => response.json())
        .then(room => {
            // 새 채팅방 생성 후, 목록을 업데이트하고 바로 그 방으로 이동합니다.
            loadRooms();
            window.location.href = '/chat/room/' + room.roomId;
        })
        .catch(err => console.error(err));
}

// 페이지가 완전히 로드되면 자동으로 방 목록을 불러오고,
// 채팅방 이름 입력창에 Enter 키 이벤트를 추가하여 Enter로도 방 생성이 가능하게 합니다.
document.addEventListener('DOMContentLoaded', () => {
    loadRooms();
    setInterval(loadRooms, 5000);
    const nameInput = document.getElementById('roomName');
    nameInput.addEventListener('keydown', function(event) {
        if (event.key === "Enter" || event.keyCode === 13) {
            event.preventDefault();
            createRoom();
        }
    });
});
