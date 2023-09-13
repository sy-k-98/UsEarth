package com.isfive.usearth.domain.chat.repository;

import com.isfive.usearth.domain.chat.entity.ChatRoom;
import com.isfive.usearth.domain.chat.entity.ChatRoomAppend;
import com.isfive.usearth.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomAppendRepository extends JpaRepository<ChatRoomAppend,Long> {
    ChatRoomAppend findByMemberAndChatRoom(Member member, ChatRoom chatRoom);

    List<ChatRoomAppend> findByMember(Member member);

    List<ChatRoomAppend> findByMemberNot(Member member);

}
