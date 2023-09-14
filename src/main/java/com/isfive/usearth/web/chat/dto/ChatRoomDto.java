package com.isfive.usearth.web.chat.dto;

import java.io.Serializable;

import com.isfive.usearth.domain.chat.entity.ChatRoom;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRoomDto implements Serializable {

    private static final long serialVersionUID = 351323121541321L;
    private String roomName;
    private String description;

    public ChatRoomDto(ChatRoomRequest request) {
        this.roomName = request.getRoomName();
        this.description = request.getDescription();
    }

    public ChatRoom toEntity(){

        return ChatRoom.builder()
                .roomName(this.getRoomName())
                .description(this.getDescription())
                .build();
    }
}