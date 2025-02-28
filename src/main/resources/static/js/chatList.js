// 채팅방 목록을 불러와서 화면에 표시하는 함수
function loadRooms() {
    fetch('/chat/rooms')
        .then(response => response.json())
        .then(data => {
            const roomList = document.getElementById('roomList');
            roomList.innerHTML = '';
            data.forEach(room => {
                const div = document.createElement('div');
                div.className = 'room-item';
                const participantCount = room.userSet ? room.userSet.length : 0;
                let roomTypeText = "";
                if (room.roomType === "shiritori") {
                    roomTypeText = " (끝말잇기)";
                } else if (room.roomType === "quiz") {
                    roomTypeText = " (일본어 퀴즈)";
                } else {
                    roomTypeText = " (수다)";
                }
                div.textContent = room.name + roomTypeText + " - " + participantCount + "명";
                div.onclick = () => {
                    window.location.href = '/chat/room/' + room.roomId;
                };
                roomList.appendChild(div);
            });
        })
        .catch(err => console.error(err));
}

// 모달창에서 방 생성 시 호출되는 함수
function createRoomModal() {
    const roomName = document.getElementById('roomNameModal').value.trim();
    const roomType = document.getElementById('roomTypeModal').value;
    if (!roomName) {
        alert("채팅방 이름을 입력하세요.");
        return;
    }
    const formData = new FormData();
    formData.append('name', roomName);
    formData.append('roomType', roomType);

    fetch('/chat/room', {
        method: 'POST',
        body: formData
    })
        .then(response => response.json())
        .then(room => {
            // 모달 닫기 (Bootstrap Modal API)
            var myModalEl = document.getElementById('createRoomModal');
            var modal = bootstrap.Modal.getInstance(myModalEl);
            if (modal) {
                modal.hide();
            }
            loadRooms();
            window.location.href = '/chat/room/' + room.roomId;
        })
        .catch(err => console.error(err));
}

document.addEventListener('DOMContentLoaded', () => {
    loadRooms();
    const nameInput = document.getElementById('roomNameModal');
    nameInput.addEventListener('keydown', function(event) {
        if (event.key === "Enter" || event.keyCode === 13) {
            event.preventDefault();
            createRoomModal();
        }
    });
});
