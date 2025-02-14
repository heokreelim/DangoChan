package net.scit.DangoChan.controller;

import net.scit.DangoChan.service.ChatService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // 채팅방 목록 조회 (화면)
    @GetMapping("/list")
    public String chatList(Model model) {
        model.addAttribute("rooms", chatService.findAllRooms());
        return "chat/chatList";
    }

    // 채팅방 상세 페이지 (화면)
    @GetMapping("/detail/{roomId}")
    public String chatDetail(@PathVariable String roomId, Model model) {
        model.addAttribute("roomId", roomId);
        return "chat/chatDetail";
    }

    // ✅ 채팅방 생성 후 목록 페이지로 리다이렉트 (수정됨!)
    @PostMapping("/room")
    public String createRoom(@RequestParam String name) {
        chatService.createRoom(name);
        return "redirect:/chat/list"; // ✅ 채팅방을 생성한 후 목록으로 이동
    }

    // 채팅방 삭제
    @PostMapping("/room/{roomId}")
    public String deleteRoom(@PathVariable String roomId) {
        chatService.deleteRoom(roomId);
        return "redirect:/chat/list"; // ✅ 삭제 후 목록 갱신
    }
}
