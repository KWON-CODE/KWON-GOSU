package com.cleaning.platform.controller;

import com.cleaning.platform.domain.Message;
import com.cleaning.platform.domain.Users;
import com.cleaning.platform.dto.MessageDto;
import com.cleaning.platform.dto.MessageResponseDto;
import com.cleaning.platform.dto.UserSearchDto;
import com.cleaning.platform.service.MessageService;
import com.cleaning.platform.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import org.slf4j.Logger; // import 추가
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {
    private static final Logger log = LoggerFactory.getLogger(MessageController.class);

    private final MessageService messageService;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;


    @GetMapping("")
    public String messageList(Model model, @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) return "redirect:/users/login";

        model.addAttribute("conversations", messageService.findConversations(currentUser.getUsername()));
        return "message-list";
    }



    @GetMapping("/chat/{partnerId}")
    public String chatRoom(@PathVariable String partnerId, Model model, @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) return "redirect:/users/login";

        Users me = userService.findUserByEmailOrThrow(currentUser.getUsername());
        Users partner = userService.findUserByIdOrThrow(partnerId);


        model.addAttribute("me", new MessageResponseDto.SenderDto(me));
        model.addAttribute("partner", new MessageResponseDto.SenderDto(partner));


        List<MessageResponseDto> chatHistoryDto = messageService.findChatHistory(me.getEmail(), partnerId)
                .stream()
                .map(MessageResponseDto::new)
                .toList();
        model.addAttribute("chatHistory", chatHistoryDto);

        return "chat-room";
    }



    @PostMapping("/chat/{partnerId}")
    public String sendMessageInChat(@PathVariable String partnerId, @ModelAttribute MessageDto dto, @AuthenticationPrincipal User currentUser) {

        dto.setReceiverId(partnerId);
        messageService.createMessage(currentUser.getUsername(), dto);
        return "redirect:/messages/chat/" + partnerId;
    }


    @GetMapping("/new")
    public String newMessageForm(Model model, @RequestParam(required = false) String receiverId) throws JsonProcessingException {
        model.addAttribute("messageDto", new MessageDto());

        List<UserSearchDto> users = userService.findAllUsersForSearch();
        String usersJson = objectMapper.writeValueAsString(users);
        model.addAttribute("usersJson", usersJson);
        if (receiverId != null && !receiverId.isEmpty()) {
            try {
                Users receiver = userService.findUserByIdOrThrow(receiverId);

                model.addAttribute("initialReceiverId", receiver.getId());
                model.addAttribute("initialReceiverName", receiver.getUsername());

            } catch (Exception e) {
                log.warn("메시지 작성폼 로딩 중 유효하지 않은 receiverId 파라미터 감지: {}", receiverId);
            }
        }

        return "message-form";
    }


    @PostMapping("/new")
    public String createNewConversation(@ModelAttribute MessageDto dto, @AuthenticationPrincipal User currentUser) {
        messageService.createMessage(currentUser.getUsername(), dto);

        return "redirect:/messages/chat/" + dto.getReceiverId();
    }



    @MessageMapping("/chat.sendMessage")
    public void handleRealtimeMessage(@Payload MessageDto messageDto) {

       try {

           Message savedMessage = messageService.createMessage(messageDto.getSenderEmail(), messageDto);

           MessageResponseDto responseDto = new MessageResponseDto(savedMessage);

           messagingTemplate.convertAndSend("/topic/chat/" + savedMessage.getReceiver().getId(), responseDto);
           messagingTemplate.convertAndSend("/topic/chat/" + savedMessage.getSender().getId(), responseDto);
       } catch (Exception e) {
           log.error("실시간 메시지 처리 중 오류 발생: {}", messageDto, e);
       }
    }
}