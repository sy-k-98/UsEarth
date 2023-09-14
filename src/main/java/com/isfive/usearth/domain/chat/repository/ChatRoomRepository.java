package com.isfive.usearth.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isfive.usearth.domain.chat.entity.ChatRoom;

import jakarta.persistence.EntityNotFoundException;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long> {
    default ChatRoom findByIdOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new EntityNotFoundException());
    }
}
