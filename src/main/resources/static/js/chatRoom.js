// DOMContentLoaded 이벤트에서 body의 data-roomid 값을 읽어 roomId를 설정합니다.
document.addEventListener('DOMContentLoaded', () => {
    let roomId = document.body.dataset.roomid;
    if (!roomId) {
        console.error("roomId is not defined in data-roomid attribute");
        return;
    }
    window.chatRoomId = roomId;  // 전역 변수에 저장
    // 임시 sessionId 생성 (추후 회원 연동 시 수정)
    window.sessionId = 'user-' + new Date().getTime();

    console.log("DOMContentLoaded -> connect(), roomId = " + roomId);
    connect();
});

let stompClient = null;

function connect() {
    console.log("Attempting to connect...");
    const socket = new SockJS('/ws-stomp');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);
}

function onConnected(frame) {
    console.log("Connected: " + frame);
    // 구독: 채팅 메시지 수신 (roomId가 올바르게 치환됨)
    stompClient.subscribe('/sub/chat/room/' + window.chatRoomId, (msg) => {
        onMessageReceived(JSON.parse(msg.body));
    });
    enterRoom();
}

function onError(error) {
    console.error("STOMP Error:", error);
    stompClient = null;
}

function enterRoom() {
    const formData = new FormData();
    formData.append('sessionId', window.sessionId);
    fetch(`/chat/room/${window.chatRoomId}/enter`, {
        method: 'POST',
        body: formData
    }).then(() => {
        updateUserList();
    }).catch(err => console.error(err));
}

function updateUserList() {
    fetch('/chat/rooms')
        .then(res => res.json())
        .then(data => {
            const room = data.find(r => r.roomId === window.chatRoomId);
            if (room) {
                const userListElem = document.getElementById('userList');
                userListElem.innerHTML = '';
                (room.userSet || []).forEach(user => {
                    const div = document.createElement('div');
                    div.textContent = user;
                    userListElem.appendChild(div);
                });
            }
        })
        .catch(err => console.error(err));
}

function sendMessage() {
    if (!stompClient) {
        console.warn("stompClient is null; cannot send message");
        return;
    }
    const msgElem = document.getElementById('chatInput');
    const msg = msgElem.value.trim();
    if (!msg) return;
    const chatMessage = {
        type: "TALK",
        roomId: window.chatRoomId,
        sender: window.sessionId,
        message: msg
    };
    stompClient.send("/pub/chat/message", {}, JSON.stringify(chatMessage));
    msgElem.value = '';
}

function onMessageReceived(chatMessage) {
    const chatMessages = document.getElementById('chatMessages');
    const div = document.createElement('div');
    if (chatMessage.sender === window.sessionId) {
        div.className = 'message my-message';
    } else {
        div.className = 'message other-message';
    }
    if (chatMessage.type === "FILE") {
        div.innerHTML = `<b>${chatMessage.sender}:</b>
            <a href="${chatMessage.message}" target="_blank">${chatMessage.fileName}</a>`;
    } else {
        div.innerHTML = `<b>${chatMessage.sender}:</b> ${chatMessage.message}`;
    }
    chatMessages.appendChild(div);
    chatMessages.scrollTop = chatMessages.scrollHeight;
}

function leaveRoom() {
    const formData = new FormData();
    formData.append('sessionId', window.sessionId);
    fetch(`/chat/room/${window.chatRoomId}/leave`, {
        method: 'POST',
        body: formData
    }).then(() => {
        window.location.href = '/chat';
    }).catch(err => console.error(err));
}

// 파일 전송: 파일 선택 시 자동 호출 (onchange 이벤트)
function sendFile() {
    if (!stompClient) {
        console.warn("stompClient is null; cannot send file");
        return;
    }
    const fileInput = document.getElementById('fileInput');
    if (!fileInput.files || fileInput.files.length === 0) {
        alert("파일을 선택하세요");
        return;
    }
    const file = fileInput.files[0];
    const formData = new FormData();
    formData.append('file', file);
    fetch('/chat/uploadFile', {
        method: 'POST',
        body: formData
    })
        .then(res => res.text())
        .then(data => {
            if (!data) {
                alert("파일 업로드 실패!");
                return;
            }
            const chatMessage = {
                type: "FILE",
                roomId: window.chatRoomId,
                sender: window.sessionId,
                message: data,  // 업로드된 파일 경로
                fileName: file.name
            };
            stompClient.send("/pub/chat/message", {}, JSON.stringify(chatMessage));
            fileInput.value = '';
        })
        .catch(err => console.error(err));
}
