// 채팅방 목록 불러오기
function loadRooms() {
    fetch('/chat/rooms')
        .then(response => response.json())
        .then(data => {
            const roomList = document.getElementById('roomList');
            roomList.innerHTML = '';
            data.forEach(room => {
                const div = document.createElement('div');
                div.className = 'room-item';
                div.textContent = room.name + ' (' + room.roomId + ')';
                div.onclick = () => {
                    // 채팅방 입장
                    window.location.href = '/chat/room/' + room.roomId;
                };
                roomList.appendChild(div);
            });
        })
        .catch(err => console.error(err));
}

// 채팅방 생성
function createRoom() {
    const name = document.getElementById('roomName').value.trim();
    if (!name) {
        alert("방 이름을 입력하세요.");
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
            // 방 생성 후 바로 이동
            window.location.href = '/chat/room/' + room.roomId;
        })
        .catch(err => console.error(err));
}

// 페이지 로드 시에 채팅방 목록 불러오기
document.addEventListener('DOMContentLoaded', () => {
    loadRooms();
});
