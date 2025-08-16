package com.telkkom10x.controller;

import com.telkkom10x.ChatMessage;
import com.telkkom10x.ChatMessageRepository;
import com.telkkom10x.TwilioService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatController {

    private final ChatMessageRepository chatMessageRepository;
    private final TwilioService twilioService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatMessageRepository chatMessageRepository, TwilioService twilioService, SimpMessagingTemplate messagingTemplate) {
        this.chatMessageRepository = chatMessageRepository;
        this.twilioService = twilioService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        message.setSource("WEBSOCKET");
        chatMessageRepository.save(message);
        messagingTemplate.convertAndSend("/topic/chat/" + message.getGroup(), message);
    }

    public void sendWhatsAppMessage(String phoneNumber, String content, String group) {
        ChatMessage message = new ChatMessage(phoneNumber, content, group, "WHATSAPP");
        message.setTimestamp(LocalDateTime.now());
        chatMessageRepository.save(message);
        twilioService.sendWhatsAppMessage(phoneNumber, content, group);
        messagingTemplate.convertAndSend("/topic/chat/" + group, message);
    }
}
