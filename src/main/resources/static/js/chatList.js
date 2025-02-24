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
                div.textContent = room.name + ' (' + room.roomId + ')';
                // 방을 클릭하면 해당 채팅방 페이지로 이동합니다.
                div.onclick = () => {
                    window.location.href = '/chat/room/' + room.roomId;
                };
                roomList.appendChild(div);
            });
        })
        .catch(err => console.error(err));
}

// 채팅방 생성 함수입니다.
function createRoom() {
    const name = document.getElementById('roomName').value.trim();
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

// 페이지가 완전히 로드되면 채팅방 목록을 불러오고,
// 5초마다 loadRooms() 함수를 호출하여 실시간으로 업데이트합니다.
document.addEventListener('DOMContentLoaded', () => {
    loadRooms();
    const nameInput = document.getElementById('roomName');
    nameInput.addEventListener('keydown', function(event) {
        if (event.key === "Enter" || event.keyCode === 13) {
            event.preventDefault();
            createRoom();
        }
    });
    setInterval(loadRooms, 5000);
});
