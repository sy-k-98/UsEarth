package com.isfive.usearth.domain.chat;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isfive.usearth.web.chat.dto.ChatMessageDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Redis에 메시지 발행이 될 때까지 대기하다가
// 메시지 발행되면 해당 메시지를 읽어 처리하는 리스너
@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber implements MessageListener {
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate redisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String publishMessage = redisTemplate.getStringSerializer().deserialize(message.getBody());
            ChatMessageDto roomMessage = objectMapper.readValue(publishMessage, ChatMessageDto.class);
            log.info(roomMessage.toString());
            messagingTemplate.convertAndSend( "/sub/chat/room/" + roomMessage.getRoomId(), roomMessage);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
