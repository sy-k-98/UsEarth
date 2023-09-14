package com.isfive.usearth.domain.chat.dto;

import com.isfive.usearth.domain.chat.entity.ChatRoom;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChatRoomResponse {
    private Long id;
    private String roomName;
    private String description;
    private Integer memberCount;

    public static ChatRoomResponse fromEntity(ChatRoom chatRoom) {
        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .roomName(chatRoom.getRoomName())
                .description(chatRoom.getDescription())
                .memberCount(chatRoom.getMemberCount())
                .build();
    }
}