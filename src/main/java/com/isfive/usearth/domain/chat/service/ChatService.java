package com.isfive.usearth.domain.chat.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isfive.usearth.domain.chat.dto.ChatRoomResponse;
import com.isfive.usearth.domain.chat.entity.ChatRoom;
import com.isfive.usearth.domain.chat.entity.ChatRoomAppend;
import com.isfive.usearth.domain.chat.enums.MessageType;
import com.isfive.usearth.domain.chat.repository.ChatRoomAppendRepository;
import com.isfive.usearth.domain.chat.repository.ChatRoomRepository;
import com.isfive.usearth.domain.member.entity.Member;
import com.isfive.usearth.domain.member.repository.MemberRepository;
import com.isfive.usearth.web.chat.dto.ChatMessageDto;
import com.isfive.usearth.web.chat.dto.ChatRoomDto;
import com.isfive.usearth.web.common.jwt.JwtTokenUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final StringRedisTemplate redisTemplate;
    private final ChannelTopic channelTopic;
    private final MemberRepository memberRepository;
    private final ChatRoomAppendRepository chatRoomAppendRepository;
    private final ObjectMapper objectMapper;

    private static final String REDIS_KEY_PREFIX = "chatMessage:";
    private final JwtTokenUtils jwtTokenUtils;



    public void sendMessage(ChatMessageDto chatMessageDto,String token) throws JsonProcessingException {
        String email = jwtTokenUtils.parseClaims(token).getSubject();
        log.info(email);
        Member member = memberRepository.findByEmailOrThrow(email);
        chatMessageDto.setNickname(member.getNickname());
        chatMessageDto.setTime(LocalDateTime.now());

        if (MessageType.ENTER.equals(chatMessageDto.getType())) {
            chatMessageDto.setMessage(chatMessageDto.getNickname() + "님이 입장하셨습니다.");
            ChatRoom chatRoom =
                    chatRoomRepository.findById(Long.parseLong(chatMessageDto.getRoomId()))
                            .orElseThrow();
            chatRoom.plusMemberCount();
            chatRoomRepository.save(chatRoom);
            ChatRoomAppend chatRoomAppend = ChatRoomAppend.creatChatRoomAppend(member,chatRoom);
            chatRoomAppendRepository.save(chatRoomAppend);
        }
        else if (MessageType.QUIT.equals(chatMessageDto.getType())) {
            chatMessageDto.setMessage(chatMessageDto.getNickname() + "님이 퇴장하셨습니다.");
            ChatRoom chatRoom =
                    chatRoomRepository.findById(Long.parseLong(chatMessageDto.getRoomId()))
                            .orElseThrow();
            chatRoom.minusMemberCount();
            chatRoomRepository.save(chatRoom);
            ChatRoomAppend chatRoomAppend = chatRoomAppendRepository.findByMemberAndChatRoom(member,chatRoom);
            chatRoomAppendRepository.delete(chatRoomAppend);
        }

        log.info(chatMessageDto.toString());
        redisTemplate.convertAndSend(channelTopic.getTopic(), objectMapper.writeValueAsString(chatMessageDto));
        String redisKey = REDIS_KEY_PREFIX + chatMessageDto.getRoomId();
        redisTemplate.expire(redisKey, Duration.ofDays(7));
        redisTemplate.opsForList().leftPush(redisKey,objectMapper.writeValueAsString(chatMessageDto));
    }

    public List<ChatMessageDto> readChatMessages(Long roomId) throws JsonProcessingException {

        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow();
        ChatRoomAppend chatRoomAppend = chatRoomAppendRepository.findByMemberAndChatRoom(findMember(),chatRoom);
        log.info(chatRoomAppend.getId().toString());
        List<String> elements = redisTemplate.opsForList().range(REDIS_KEY_PREFIX+roomId, 0, -1);
        List<ChatMessageDto> messageList = new ArrayList<>();
        for (int i = elements.size()-1 ; i >= 0 ; i--){
            ChatMessageDto chatMessageDto = objectMapper.readValue(elements.get(i), ChatMessageDto.class);
            log.info("message : "+chatMessageDto.toString());
            if (chatMessageDto.getTime().isAfter(chatRoomAppend.getCreatedDate())) {
                messageList.add(chatMessageDto);
            }
        }
        return messageList;
    }

    public ChatRoomResponse createChatRoom(ChatRoomDto chatRoomDto) {

        ChatRoom chatRoom = chatRoomRepository.save(chatRoomDto.toEntity());
        ChatRoomAppend chatRoomAppend = ChatRoomAppend.creatChatRoomAppend(findMember(),chatRoom);
        chatRoomAppendRepository.save(chatRoomAppend);
        return ChatRoomResponse.fromEntity(chatRoom);
    }

    public List<ChatRoomResponse> readMyChatRoom() {

        List<ChatRoomAppend> chatRoomAppends = chatRoomAppendRepository.findByMember(findMember());
        List<ChatRoom> chatRooms = chatRoomAppends.stream().map(ChatRoomAppend::getChatRoom).collect(Collectors.toList());

        List<ChatRoomResponse> chatRoomList = new ArrayList<>();
        for (ChatRoom chatRoom: chatRooms){
            log.info("readMyChatRoom : "+chatRoom.toString());
            chatRoomList.add(ChatRoomResponse.fromEntity(chatRoom));

        }
        return chatRoomList;

    }


    public List<ChatRoomResponse> readAllChatRoom() {

        List<ChatRoomResponse> chatRoomList = new ArrayList<>();
        for (ChatRoomAppend chatRoomAppend: chatRoomAppendRepository.findByMemberNot(findMember())) {
            ChatRoom chatRoom = chatRoomRepository.findByIdOrThrow(chatRoomAppend.getChatRoom().getId());
            chatRoomList.add(ChatRoomResponse.fromEntity(chatRoom));
            log.info("readAllChatRoom: "+chatRoom);
        }
        return chatRoomList;
    }

    public ChatRoomResponse readChatRoom(Long id) {
        ChatRoom chatRoom = chatRoomRepository.findById(id).orElseThrow();
        return ChatRoomResponse.fromEntity(chatRoom);
    }

    public Member findMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Member member = memberRepository.findByUsernameOrThrow(username);
        return member;
    }
}
