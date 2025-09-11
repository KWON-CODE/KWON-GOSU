package com.cleaning.platform.service;

import com.cleaning.platform.domain.Message;
import com.cleaning.platform.domain.Users;
import com.cleaning.platform.dto.MessageDto;
import com.cleaning.platform.repository.BookingRepository;
import com.cleaning.platform.repository.MessageRepository;
import com.cleaning.platform.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public MessageService(MessageRepository messageRepository,
                          UserRepository userRepository,
                          BookingRepository bookingRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }


    @Getter
    @AllArgsConstructor
    public static class Conversation {
        private Users partner;
        private Message lastMessage;
    }

    @Transactional
    public Message createMessage(String senderEmail, MessageDto dto) {
        Users sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new IllegalArgumentException("보내는 사용자 정보를 찾을 수 없습니다."));

        Users receiver = userRepository.findById(dto.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException("받는 사용자 정보를 찾을 수 없습니다."));

        Message message = Message.builder()
                .id("M-" + UUID.randomUUID().toString().substring(0, 8))
                .sender(sender)
                .receiver(receiver)
                .content(dto.getContent())
                .build();
       return messageRepository.save(message);
    }


    public List<Conversation> findConversations(String myEmail) {
        Users me = userRepository.findByEmail(myEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        List<Message> allMessages = messageRepository.findBySenderOrReceiverOrderBySentAtDesc(me, me);

        Map<Users, Message> latestMessagesByPartner = allMessages.stream()
                .collect(Collectors.toMap(

                        message -> message.getSender().equals(me) ? message.getReceiver() : message.getSender(),
                        message -> message,
                        (existing, replacement) -> existing
                ));

        return latestMessagesByPartner.entrySet().stream()
                .map(entry -> new Conversation(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing((Conversation c) -> c.getLastMessage().getSentAt()).reversed())
                .collect(Collectors.toList());
    }


    public List<Message> findChatHistory(String myEmail, String partnerId) {
        Users me = userRepository.findByEmail(myEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));
        Users partner = userRepository.findById(partnerId)
                .orElseThrow(() -> new IllegalArgumentException("상대방 정보를 찾을 수 없습니다."));

        return messageRepository.findChatHistory(me, partner);
    }


    public long getUnreadMessageCount(String myEmail) {
        Users me = userRepository.findByEmail(myEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));
        return messageRepository.countByReceiverAndIsRead(me, false);
    }
}