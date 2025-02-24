// 외부 JS 파일 (chatRoom.js)

// DOMContentLoaded 이벤트가 발생하면, body의 data-roomid 속성에서 roomId를 읽어옵니다.
document.addEventListener('DOMContentLoaded', () => {
    let roomId = document.body.dataset.roomid;
    if (!roomId) {
        console.error("roomId is not defined in data-roomid attribute");
        return;
    }
    window.chatRoomId = roomId;  // 전역 변수로 저장
    // 임시 sessionId 생성 (회원 연동 전 임시 사용)
    window.sessionId = 'user-' + new Date().getTime();

    console.log("DOMContentLoaded -> connect(), roomId = " + roomId);
    connect();

    // 채팅 입력창에 Enter 키 이벤트 추가
    const chatInput = document.getElementById('chatInput');
    chatInput.addEventListener('keydown', function(event) {
        if (event.key === "Enter" || event.keyCode === 13) {
            event.preventDefault();
            sendMessage();
        }
    });
});

let stompClient = null;

// 웹소켓 연결 함수
function connect() {
    console.log("Attempting to connect...");
    const socket = new SockJS('/ws-stomp');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);
}

function onConnected(frame) {
    console.log("Connected: " + frame);
    // 해당 채팅방의 메시지를 받기 위해 구독합니다.
    stompClient.subscribe('/sub/chat/room/' + window.chatRoomId, (msg) => {
        onMessageReceived(JSON.parse(msg.body));
    });
    enterRoom();
}

function onError(error) {
    console.error("STOMP Error:", error);
    stompClient = null;
}

// 방 입장 알림 함수 (REST API 호출)
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

// 사용자 목록 업데이트 함수
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

// 메시지 전송 함수 (전송 버튼 또는 Enter 키)
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

// 메시지 수신 처리 함수
function onMessageReceived(chatMessage) {
    // SYSTEM 메시지면 따로 처리하여 중앙에 표시하고 사용자 목록도 업데이트
    if (chatMessage.type === "SYSTEM") {
        displaySystemMessage(chatMessage.message);
        updateUserList();
        return;
    }
    // 일반 메시지 처리
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

// SYSTEM 메시지를 화면에 표시하는 함수
function displaySystemMessage(message) {
    const chatMessages = document.getElementById('chatMessages');
    const div = document.createElement('div');
    div.className = 'message system-message'; // CSS에서 특별히 스타일 지정됨
    div.innerHTML = message;
    chatMessages.appendChild(div);
    chatMessages.scrollTop = chatMessages.scrollHeight;
}

// 방 나가기 함수
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

// 파일 전송 함수 (파일 선택 시 onchange 이벤트로 자동 호출)
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
