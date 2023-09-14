package com.isfive.usearth.domain.chat;

import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.isfive.usearth.domain.chat.service.ChatService;
import com.isfive.usearth.web.chat.dto.ChatMessageDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketMapping {
    private final ChatService chatService;
    @MessageMapping("/chat/message")
    public void sendChat(ChatMessageDto chatMessageDto , @Header("token") String token) throws JsonProcessingException {

            log.info(chatMessageDto.toString());
            chatService.sendMessage(chatMessageDto, token);

    }
}
