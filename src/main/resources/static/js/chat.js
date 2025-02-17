var socket = new SockJS('/ws-chat');
var stompClient = Stomp.over(socket);

// ✅ 브라우저별 고유 사용자 이름 생성 (랜덤)
var username = localStorage.getItem("username");
if (!username) {
    username = "User_" + Math.floor(Math.random() * 1000);
    localStorage.setItem("username", username);
}

// ✅ WebSocket 연결
stompClient.connect({}, function (frame) {
    console.log('✅ WebSocket 연결 성공: ' + frame);
    stompClient.subscribe('/topic/public', function (message) {
        var chatArea = document.getElementById("chatArea");
        var msg = JSON.parse(message.body);

        var messageDiv = document.createElement("div");
        messageDiv.textContent = msg.sender + ": " + msg.message;
        messageDiv.classList.add("message");

        // ✅ 내가 보낸 메시지인지 확인하고 위치 조정
        if (msg.sender === username) {
            messageDiv.classList.add("my-message"); // 오른쪽 정렬
        } else {
            messageDiv.classList.add("other-message"); // 왼쪽 정렬
        }

        chatArea.appendChild(messageDiv);
        chatArea.scrollTop = chatArea.scrollHeight; // 최신 메시지로 자동 스크롤
    });
});

// ✅ 메시지 전송
document.getElementById("chatForm").addEventListener("submit", function (event) {
    event.preventDefault();
    var messageInput = document.getElementById("messageInput").value;

    console.log('📤 메시지 전송:', messageInput);
    stompClient.send("/app/chat.sendMessage", {}, JSON.stringify({
        roomId: document.getElementById("roomId").textContent,
        sender: username, // ✅ 보낸 사람 이름 포함
        message: messageInput
    }));

    document.getElementById("messageInput").value = "";
});
