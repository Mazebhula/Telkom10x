package com.telkkom10x.controller;

import com.telkkom10x.ChatMessage;
import com.telkkom10x.ChatMessageRepository;
import com.telkkom10x.TwilioService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ChatController {

    private final ChatMessageRepository chatMessageRepository;
    private final TwilioService twilioService;
    private final SimpMessagingTemplate messagingTemplate;
    private final String adminWhatsAppNumber;

    public ChatController(ChatMessageRepository chatMessageRepository, TwilioService twilioService,
                          SimpMessagingTemplate messagingTemplate,
                          @Value("${twilio.admin.whatsapp.number}") String adminWhatsAppNumber) {
        this.chatMessageRepository = chatMessageRepository;
        this.twilioService = twilioService;
        this.messagingTemplate = messagingTemplate;
        this.adminWhatsAppNumber = adminWhatsAppNumber;
    }

    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessage message) {
        message.setTimestamp(java.time.LocalDateTime.now());
        message.setSource("WEBSOCKET");
        chatMessageRepository.save(message);

        // Send WhatsApp notification to admin
        String adminMessage = String.format("New message from %s in group %s: %s",
                message.getSender(), message.getGroup(), message.getContent());
        twilioService.sendWhatsAppMessage(adminWhatsAppNumber, adminMessage, message.getGroup());

        // Broadcast to WebSocket group
        messagingTemplate.convertAndSend("/topic/chat/" + message.getGroup(), message);
    }

    @PostMapping("/whatsapp-webhook")
    public void handleWhatsAppMessage(@RequestParam("From") String from,
                                      @RequestParam("Body") String body,
                                      @RequestParam(value = "chatGroup", defaultValue = "general") String chatGroup) {
        ChatMessage message = new ChatMessage(from.replace("whatsapp:", ""), body, chatGroup, "WHATSAPP");
        message.setTimestamp(java.time.LocalDateTime.now());
        chatMessageRepository.save(message);
        messagingTemplate.convertAndSend("/topic/chat/" + chatGroup, message);

        // Send WhatsApp notification to admin (if not from admin)
        if (!from.equals(adminWhatsAppNumber)) {
            String adminMessage = String.format("WhatsApp message from %s in group %s: %s",
                    from.replace("whatsapp:", ""), chatGroup, body);
            twilioService.sendWhatsAppMessage(adminWhatsAppNumber, adminMessage, chatGroup);
        }
    }

    public void sendWhatsAppMessage(String phoneNumber, String content, String group) {
        ChatMessage message = new ChatMessage(phoneNumber, content, group, "WHATSAPP");
        message.setTimestamp(java.time.LocalDateTime.now());
        chatMessageRepository.save(message);
        twilioService.sendWhatsAppMessage(phoneNumber, content, group);
        messagingTemplate.convertAndSend("/topic/chat/" + group, message);

        // Send WhatsApp notification to admin (if not to admin)
        if (!phoneNumber.equals(adminWhatsAppNumber)) {
            String adminMessage = String.format("WhatsApp message sent to %s in group %s: %s",
                    phoneNumber, group, content);
            twilioService.sendWhatsAppMessage(adminWhatsAppNumber, adminMessage, group);
        }
    }
}
