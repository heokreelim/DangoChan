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

function createRoomModal() {
    const roomName = document.getElementById('roomNameModal').value.trim();
    const roomType = document.getElementById('roomTypeModal').value;
    const maxParticipants = document.getElementById('maxParticipantsModal').value.trim();
    if (!roomName) {
        alert("채팅방 이름을 입력하세요.");
        return;
    }
    const formData = new FormData();
    formData.append('name', roomName);
    formData.append('roomType', roomType);
    formData.append('maxParticipants', maxParticipants);

    fetch('/chat/room', {
        method: 'POST',
        body: formData
    })
        .then(response => response.json())
        .then(room => {
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
    setInterval(loadRooms, 5000);
    const nameInput = document.getElementById('roomNameModal');
    nameInput.addEventListener('keydown', function(event) {
        if (event.key === "Enter" || event.keyCode === 13) {
            event.preventDefault();
            createRoomModal();
        }
    });
});
