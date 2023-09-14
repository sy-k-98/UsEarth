package com.isfive.usearth.web.chat.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.isfive.usearth.domain.chat.entity.ChatMessage;
import com.isfive.usearth.domain.chat.enums.MessageType;

import lombok.Data;


@Data
public class ChatMessageDto implements Serializable  {


    private String roomId;
    private String nickname;
    private String message;
    private LocalDateTime time;
    private MessageType type;

    public ChatMessage toEntity() {


        return ChatMessage.builder()
                .nickname(this.nickname)
                .message(this.message)
                .build();
    }
}