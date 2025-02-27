// DOMContentLoaded 이벤트: body의 data 속성에서 roomId와 userId를 읽어옵니다.
document.addEventListener('DOMContentLoaded', () => {
    let roomId = document.body.dataset.roomid;
    let userId = document.body.dataset.userid;
    if (!roomId) {
        console.error("roomId is not defined in data-roomid attribute");
        return;
    }
    if (!userId) {
        console.error("userId is not defined in data-userid attribute");
        return;
    }
    window.chatRoomId = roomId;  // 전역 변수에 저장
    // 로그인한 사용자의 닉네임을 sessionId로 사용합니다.
    window.sessionId = userId;
    console.log("DOMContentLoaded -> connect(), roomId = " + roomId + ", sessionId = " + userId);
    connect();

    // 채팅 입력창에 Enter 키 이벤트 추가 (Enter 키로 메시지 전송)
    const chatInput = document.getElementById('chatInput');
    chatInput.addEventListener('keydown', function(event) {
        if (event.key === "Enter" || event.keyCode === 13) {
            event.preventDefault();
            sendMessage();
        }
    });
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
    // 구독: 채팅방의 메시지를 받기 위해 구독합니다.
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
                // 사용자 목록 업데이트
                const userListElem = document.getElementById('userList');
                userListElem.innerHTML = '';
                (room.userSet || []).forEach(user => {
                    const div = document.createElement('div');
                    div.textContent = user;
                    userListElem.appendChild(div);
                });
                // 현재 사용자 수를 업데이트합니다.
                const userCountElem = document.getElementById('userCount');
                userCountElem.textContent = "(" + (room.userSet ? room.userSet.length : 0) + "명)";
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
    // SYSTEM 메시지 처리: 시스템 메시지는 중앙에 표시하고 사용자 목록 업데이트
    if (chatMessage.type === "SYSTEM") {
        displaySystemMessage(chatMessage.message);
        updateUserList();
        return;
    }
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

function displaySystemMessage(message) {
    const chatMessages = document.getElementById('chatMessages');
    const div = document.createElement('div');
    div.className = 'message system-message';
    div.innerHTML = message;
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
                message: data,
                fileName: file.name
            };
            stompClient.send("/pub/chat/message", {}, JSON.stringify(chatMessage));
            fileInput.value = '';
        })
        .catch(err => console.error(err));
}
