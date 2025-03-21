package net.scit.DangoChan.controller;

import lombok.RequiredArgsConstructor;
import net.scit.DangoChan.dto.ChatMessage;
import net.scit.DangoChan.dto.ChatRoom;
import net.scit.DangoChan.dto.LoginUserDetails;
import net.scit.DangoChan.repository.ChatRoomRepository;
import net.scit.DangoChan.service.AchievementService;
import net.scit.DangoChan.service.ChatService;
import net.scit.DangoChan.service.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final UserService userService;
    private final AchievementService achievementService;  // achievementService 추가

    @GetMapping("/chat")
    public ModelAndView chatListPage(@AuthenticationPrincipal LoginUserDetails user) {
        ModelAndView mv = new ModelAndView("chat/chatList");
        if (user != null) {
            mv.addObject("userInfo", userService.findById(user.getUserId()));
            mv.addObject("userId", user.getUserId());
            // achievement 데이터 추가
            mv.addObject("attendanceStreak", achievementService.getAttendanceStreak(user.getUserId()));
            mv.addObject("todayStudyTimeFormatted", achievementService.getTodayStudyTimeFormatted(user.getUserId()));
            mv.addObject("personalAchievements", achievementService.getPersonalAchievements(user.getUserId()));
            mv.addObject("communityAchievements", achievementService.getCommunityAchievements(user.getUserId()));
        }
        return mv;
    }

    @GetMapping("/chat/room/{roomId}")
    public ModelAndView chatRoomPage(@PathVariable(name = "roomId") String roomId,
                                     @AuthenticationPrincipal LoginUserDetails user) {
        ModelAndView mv = new ModelAndView("chat/chatRoom");
        mv.addObject("roomId", roomId);
        ChatRoom room = chatRoomRepository.findRoomById(roomId);
        if (room != null) {
            mv.addObject("roomType", room.getRoomType());
        } else {
            mv.addObject("roomType", "chat");
        }
        if (user != null) {
            mv.addObject("userInfo", userService.findById(user.getUserId()));
            mv.addObject("userId", user.getUserId());
            // achievement 데이터 추가
            mv.addObject("attendanceStreak", achievementService.getAttendanceStreak(user.getUserId()));
            mv.addObject("todayStudyTimeFormatted", achievementService.getTodayStudyTimeFormatted(user.getUserId()));
            mv.addObject("personalAchievements", achievementService.getPersonalAchievements(user.getUserId()));
            mv.addObject("communityAchievements", achievementService.getCommunityAchievements(user.getUserId()));
        }
        return mv;
    }

    @GetMapping("/chat/rooms")
    public List<ChatRoom> findAllRooms() {
        return chatRoomRepository.findAllRooms();
    }

    // 방 생성: name, roomType, maxParticipants 전달
    @PostMapping("/chat/room")
    public ChatRoom createRoom(@RequestParam(name = "name") String name,
                               @RequestParam(name = "roomType") String roomType,
                               @RequestParam (name = "maxParticipants")int maxParticipants) {
        return chatRoomRepository.createChatRoom(name, roomType, maxParticipants);
    }

    // 게임 시작 (끝말잇기 모드 전용)
    @PostMapping("/chat/room/{roomId}/startGame")
    public void startGame(@PathVariable(name = "roomId") String roomId) {
        chatService.startGame(roomId);
    }

    @MessageMapping("/chat/message")
    public void message(ChatMessage message) {
        if (message.getType() == ChatMessage.MessageType.FILE) {
            chatService.sendFileMessage(message);
        } else {
            chatService.sendChatMessage(message);
        }
    }

    @PostMapping("/chat/room/{roomId}/enter")
    public void enterRoom(@PathVariable(name = "roomId") String roomId, @RequestParam(name = "sessionId") String sessionId) {
        chatService.enterUser(sessionId, roomId);
    }

    @PostMapping("/chat/room/{roomId}/leave")
    public void leaveRoom(@PathVariable(name = "roomId") String roomId, @RequestParam(name = "sessionId") String sessionId) {
        chatService.leaveUser(sessionId, roomId);
    }

    @PostMapping("/chat/uploadFile")
    public String uploadFile(@RequestParam(name = "file") MultipartFile file) throws IOException {
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