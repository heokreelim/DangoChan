package net.scit.DangoChan.controller;

import lombok.RequiredArgsConstructor;
import net.scit.DangoChan.dto.ChatMessage;
import net.scit.DangoChan.dto.ChatRoom;
import net.scit.DangoChan.repository.ChatRoomRepository;
import net.scit.DangoChan.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;

    /**
     * 1) 채팅방 리스트 페이지 (HTML 템플릿 렌더링)
     *    - GET /chat
     *    - templates/chat/chatList.html
     */
    @GetMapping("/chat")
    public ModelAndView chatListPage() {
        return new ModelAndView("chat/chatList");
    }

    /**
     * 2) 채팅방 상세 페이지 (HTML 템플릿 렌더링)
     *    - GET /chat/room/{roomId}
     *    - templates/chat/chatRoom.html
     */
    @GetMapping("/chat/room/{roomId}")
    public ModelAndView chatRoomPage(@PathVariable String roomId) {
        ModelAndView mv = new ModelAndView("chat/chatRoom");
        mv.addObject("roomId", roomId);
        return mv;
    }

    /**
     * 3) 채팅방 목록 조회 (JSON)
     *    - GET /chat/rooms
     *    - 클라이언트에서 fetch('/chat/rooms')로 전체 방 목록 받기
     */
    @GetMapping("/chat/rooms")
    public List<ChatRoom> findAllRooms() {
        return chatRoomRepository.findAllRooms();
    }

    /**
     * 4) 채팅방 생성
     *    - POST /chat/room?name=방이름
     *    - 생성된 ChatRoom 객체(JSON) 반환
     */
    @PostMapping("/chat/room")
    public ChatRoom createRoom(@RequestParam String name) {
        return chatRoomRepository.createChatRoom(name);
    }

    /**
     * 5) STOMP 메시지 매핑
     *    - 클라이언트에서 stompClient.send("/pub/chat/message", {}, JSON) 일 때 실행
     */
    @MessageMapping("/chat/message")
    public void message(ChatMessage message) {
        if (message.getType() == ChatMessage.MessageType.FILE) {
            chatService.sendFileMessage(message);
        } else {
            chatService.sendChatMessage(message);
        }
    }

    /**
     * 6) 채팅방 입장 (REST)
     *    - POST /chat/room/{roomId}/enter
     *    - body: sessionId(브라우저 임의 세션)
     */
    @PostMapping("/chat/room/{roomId}/enter")
    public void enterRoom(@PathVariable String roomId, @RequestParam String sessionId) {
        chatService.enterUser(sessionId, roomId);
    }

    /**
     * 7) 채팅방 퇴장 (REST)
     *    - POST /chat/room/{roomId}/leave
     *    - body: sessionId(브라우저 임의 세션)
     *    - 아무도 없으면 방 자동 삭제
     */
    @PostMapping("/chat/room/{roomId}/leave")
    public void leaveRoom(@PathVariable String roomId, @RequestParam String sessionId) {
        chatService.leaveUser(sessionId, roomId);
    }

    /**
     * 8) 파일 업로드 (단순 예시)
     *    - POST /chat/uploadFile
     *    - MultipartFormData로 파일을 받고, 로컬 디렉토리에 저장 → 경로 반환
     *    - 실제 운영환경에서는 S3 같은 외부 스토리지 사용 권장
     */
    @PostMapping("/chat/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            String uploadPath = System.getProperty("user.dir") + "/uploads";
            File dir = new File(uploadPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String filePath = uploadPath + "/" + file.getOriginalFilename();
            file.transferTo(new File(filePath));

            // 업로드한 파일 경로(또는 URL) 반환
            return "/uploads/" + file.getOriginalFilename();
        }
        return "";
    }
}
