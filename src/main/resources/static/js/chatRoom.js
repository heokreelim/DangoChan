document.addEventListener('DOMContentLoaded', () => {
    let roomId = document.body.dataset.roomid;
    let userId = document.body.dataset.userid;
    let roomType = document.body.dataset.roomtype;
    if (!roomId) {
        console.error("roomId is not defined in data-roomid attribute");
        return;
    }
    if (!userId) {
        console.error("userId is not defined in data-userid attribute");
        return;
    }
    window.chatRoomId = roomId;
    window.sessionId = userId;  // 로그인한 사용자의 닉네임 사용
    window.roomType = roomType;
    console.log("DOMContentLoaded -> connect(), roomId = " + roomId + ", sessionId = " + userId + ", roomType = " + roomType);
    connect();

    const chatInput = document.getElementById('chatInput');
    chatInput.addEventListener('keydown', function(event) {
        if (event.key === "Enter" || event.keyCode === 13) {
            event.preventDefault();
            sendMessage();
        }
    });

    // 이모지 모달 관련 이벤트
    const emojiToggle = document.getElementById('emojiToggle');
    const emojiModal = document.getElementById('emojiModal');
    const closeEmojiModal = document.getElementById('closeEmojiModal');
    emojiToggle.addEventListener('click', function() {
        emojiModal.style.display = "block";
    });
    closeEmojiModal.addEventListener('click', function() {
        emojiModal.style.display = "none";
    });
    window.addEventListener('click', function(event) {
        if (event.target == emojiModal) {
            emojiModal.style.display = "none";
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
    stompClient.subscribe('/sub/chat/room/' + window.chatRoomId, (msg) => {
        onMessageReceived(JSON.parse(msg.body));
    });
    enterRoom();
    // 게임 모드 안내 메시지
    if (window.roomType === "shiritori") {
        displaySystemMessage("끝말잇기 게임: 이전 단어의 마지막 글자에 맞춰 단어를 입력하세요.");
    } else if (window.roomType === "quiz") {
        displaySystemMessage("일본어 퀴즈: 질문에 대한 올바른 답을 입력하세요.");
    }
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
        let lowerName = chatMessage.fileName.toLowerCase();
        if (lowerName.endsWith(".png") || lowerName.endsWith(".jpg") ||
            lowerName.endsWith(".jpeg") || lowerName.endsWith(".gif")) {
            div.innerHTML = `<b>${chatMessage.sender}:</b><br><img src="${chatMessage.message}" alt="${chatMessage.fileName}" style="max-width:200px; max-height:200px;">`;
        } else {
            div.innerHTML = `<b>${chatMessage.sender}:</b> <a href="${chatMessage.message}" target="_blank">${chatMessage.fileName}</a>`;
        }
    } else {
        div.innerHTML = `<b>${chatMessage.sender}:</b> ${chatMessage.message}`;
    }
    const chatMessagesElem = document.getElementById('chatMessages');
    chatMessagesElem.appendChild(div);
    chatMessagesElem.scrollTop = chatMessagesElem.scrollHeight;
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

function addEmoji(emoji) {
    const chatInput = document.getElementById('chatInput');
    chatInput.value += emoji;
    document.getElementById('emojiModal').style.display = "none";
}
