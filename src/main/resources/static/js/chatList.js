function createParticipantImages(userSet) {
    if (!userSet || userSet.length === 0) {
        return '';
    }
    const maxDisplay = 3;
    let html = '';
    const count = userSet.length;

    // 최대 3명까지만 프로필 이미지 (임시로 모두 profile_0.jpg 사용)
    const displayed = userSet.slice(0, maxDisplay);
    displayed.forEach(user => {
        html += `
            <img src="/images/profiles/profile_0.jpg" 
                 alt="프로필" class="participant-img" />
        `;
    });
    // 나머지 인원이 있으면 +N 표시
    if (count > maxDisplay) {
        const extra = count - maxDisplay;
        html += `<span class="participant-extra">+${extra}</span>`;
    }
    return html;
}

function loadRooms() {
    fetch('/chat/rooms')
        .then(response => response.json())
        .then(data => {
            const container = document.querySelector('.room-container');
            container.innerHTML = ''; // 초기화

            if (data.length === 0) {
                container.innerHTML = `
                    <p class="room-empty">
                        하단 플러스(+) 버튼을 클릭하여<br>
                        새로운 채팅방을 생성하세요.
                    </p>
                `;
            } else {
                data.forEach(room => {
                    const card = document.createElement('div');
                    card.className = 'room-card';

                    const participantHTML = createParticipantImages(room.userSet);
                    const roomType = room.roomType || 'chat';

                    // 상단 영역에 방 유형, 방 제목(왼쪽) + 프로필(오른쪽)
                    card.innerHTML = `
                        <div class="room-card-top">
                            <div class="room-title">
                                <a href="/chat/room/${room.roomId}">
                                    <span class="room-type">[${roomType}] </span>
                                    <b>${room.name}</b>
                                </a>
                            </div>
                            <div class="room-participants">
                                ${participantHTML}
                            </div>
                        </div>
                    `;

                    // 클릭 시 방으로 이동 (원한다면)
                    card.addEventListener('click', () => {
                        window.location.href = '/chat/room/' + room.roomId;
                    });

                    container.appendChild(card);
                });
            }
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
            const myModalEl = document.getElementById('createRoomModal');
            const modal = bootstrap.Modal.getInstance(myModalEl);
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
