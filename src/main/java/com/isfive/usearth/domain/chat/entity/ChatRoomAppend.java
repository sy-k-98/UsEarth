package com.isfive.usearth.domain.chat.entity;

import java.util.ArrayList;
import java.util.List;

import com.isfive.usearth.domain.common.BaseEntity;
import com.isfive.usearth.domain.member.entity.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomAppend extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatRoom_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "chatRoomAppend")
    private List<ChatMessage> chatMessages = new ArrayList<>();

    @Builder
    private ChatRoomAppend(Member member, ChatRoom chatRoom) {
        this.member = member;
        this.chatRoom = chatRoom;
        this.member = member;
        this.chatRoom = chatRoom;
    }


    public static ChatRoomAppend creatChatRoomAppend(Member member, ChatRoom chatRoom) {
        return ChatRoomAppend.builder()
                .member(member)
                .chatRoom(chatRoom)
                .build();

    }

}
