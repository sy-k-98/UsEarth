package com.isfive.usearth.web.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.isfive.usearth.domain.chat.dto.ChatRoomResponse;
import com.isfive.usearth.domain.chat.service.ChatService;
import com.isfive.usearth.web.chat.dto.ChatMessageDto;
import com.isfive.usearth.web.chat.dto.ChatRoomDto;
import com.isfive.usearth.web.chat.dto.ChatRoomRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "http://localhost:3000")
@Tag(name = "6. Chat", description = "Chat API")
public class ChatRoomController {
    private final ChatService chatService;

    @Operation(summary = "모든 채팅방 목록")
    @GetMapping("/rooms")
    public List<ChatRoomResponse> getAllRoom(
    ) {
        return chatService.readAllChatRoom();
    }

    @Operation(summary = "내가 참여 중인 채팅방 목록")
    @GetMapping("/myRooms")
    public List<ChatRoomResponse> getMyRoom(){
        return chatService.readMyChatRoom();
    }


    @Operation(summary = "채팅 메세지 불러오기")
    @GetMapping("/myRoom/{roomId}")
    public List<ChatMessageDto> getChatMessage(
            @PathVariable("roomId") Long roomId
    ) throws JsonProcessingException {
        return chatService.readChatMessages(roomId);
    }

    @Operation(summary = "채팅방 만들기")
    @PostMapping("/room")
    public ChatRoomResponse createRoom( @RequestBody ChatRoomRequest request) {
        log.info("{}",request.toString());
        ChatRoomDto chatRoomDto = new ChatRoomDto(request);
        return chatService.createChatRoom(chatRoomDto);
    }

    @Operation(summary = "채팅방 정보 조회")
    @GetMapping("/room/{roomId}")
    public ChatRoomResponse roomInfo(@PathVariable("roomId") Long id) {
        return chatService.readChatRoom(id);
    }
}
