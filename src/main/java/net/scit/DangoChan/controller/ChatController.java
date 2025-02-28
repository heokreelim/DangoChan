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

    // 채팅방 리스트 페이지
    @GetMapping("/chat")
    public ModelAndView chatListPage() {
        return new ModelAndView("chat/chatList");
    }

    // 채팅방 상세 페이지
    @GetMapping("/chat/room/{roomId}")
    public ModelAndView chatRoomPage(@PathVariable String roomId) {
        ModelAndView mv = new ModelAndView("chat/chatRoom");
        mv.addObject("roomId", roomId);
        // 채팅방 생성 시 저장한 roomType을 넘겨줘야 함 (예: redis에서 조회)
        ChatRoom room = chatRoomRepository.findRoomById(roomId);
        if (room != null) {
            mv.addObject("roomType", room.getRoomType());
        } else {
            mv.addObject("roomType", "chat"); // 기본값
        }
        return mv;
    }

    // 전체 채팅방 목록
    @GetMapping("/chat/rooms")
    public List<ChatRoom> findAllRooms() {
        return chatRoomRepository.findAllRooms();
    }

    // 채팅방 생성: name과 roomType을 받아서 생성
    @PostMapping("/chat/room")
    public ChatRoom createRoom(@RequestParam String name, @RequestParam String roomType) {
        return chatRoomRepository.createChatRoom(name, roomType);
    }

    // STOMP 메시지 매핑
    @MessageMapping("/chat/message")
    public void message(ChatMessage message) {
        if (message.getType() == ChatMessage.MessageType.FILE) {
            chatService.sendFileMessage(message);
        } else {
            chatService.sendChatMessage(message);
        }
    }

    // 사용자 입장
    @PostMapping("/chat/room/{roomId}/enter")
    public void enterRoom(@PathVariable String roomId, @RequestParam String sessionId) {
        chatService.enterUser(sessionId, roomId);
    }

    // 사용자 퇴장
    @PostMapping("/chat/room/{roomId}/leave")
    public void leaveRoom(@PathVariable String roomId, @RequestParam String sessionId) {
        chatService.leaveUser(sessionId, roomId);
    }

    // 파일 업로드
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
            return "/uploads/" + file.getOriginalFilename();
        }
        return "";
    }
}
