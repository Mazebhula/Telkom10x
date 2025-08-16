package com.telkkom10x.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telkkom10x.ChatMessage;
import com.telkkom10x.ChatMessageRepository;
import com.telkkom10x.Location;
import com.telkkom10x.LocationUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

@Controller
public class MainController {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatController chatController;
    private final SimpMessagingTemplate messagingTemplate;

    public MainController(ChatMessageRepository chatMessageRepository, ChatController chatController, SimpMessagingTemplate messagingTemplate) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatController = chatController;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("welcomeMessage", "Welcome to Telkkom10x!");
        return "home";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "Username and password are required");
            return "login";
        }
        if ("admin".equals(username) && "password".equals(password)) {
            session.setAttribute("username", username);
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Invalid credentials");
            return "login";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }
        model.addAttribute("username", username);
        Location location = (Location) session.getAttribute("location");
        if (location != null) {
            model.addAttribute("location", location);
        }
        return "dashboard";
    }

    @GetMapping("/find_taxi")
    public String findTaxi(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }
        model.addAttribute("username", username);
        model.addAttribute("message", "Find a taxi near you!");
        Location location = (Location) session.getAttribute("location");
        if (location != null) {
            model.addAttribute("location", location);
        }
        return "find_taxi";
    }

    @GetMapping("/chat")
    public String chat(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }
        Location location = (Location) session.getAttribute("location");
        if (location == null) {
            model.addAttribute("error", "Please share your location to join a chat group.");
            return "redirect:/dashboard";
        }
        String chatGroup = LocationUtils.getChatGroup(location);
        model.addAttribute("username", username);
        model.addAttribute("chatGroup", chatGroup);
        model.addAttribute("message", "Chat with users in " + (location.getCity() != null ? location.getCity() : "your area"));
        model.addAttribute("chatHistory", chatMessageRepository.findByGroup(chatGroup));
        return "chat";
    }

    @PostMapping("/send-whatsapp")
    public String sendWhatsApp(@RequestParam String phoneNumber,
                               @RequestParam String message,
                               HttpSession session,
                               Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }
        Location location = (Location) session.getAttribute("location");
        if (location == null) {
            model.addAttribute("error", "Please share your location to send WhatsApp messages.");
            return "redirect:/dashboard";
        }
        String chatGroup = LocationUtils.getChatGroup(location);
        try {
            chatController.sendWhatsAppMessage(phoneNumber, message, chatGroup);
            model.addAttribute("success", "WhatsApp message sent successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to send WhatsApp message: " + e.getMessage());
        }
        model.addAttribute("username", username);
        model.addAttribute("chatGroup", chatGroup);
        model.addAttribute("message", "Chat with users in " + (location.getCity() != null ? location.getCity() : "your area"));
        model.addAttribute("chatHistory", chatMessageRepository.findByGroup(chatGroup));
        return "chat";
    }

    @PostMapping("/whatsapp-webhook")
    public ResponseEntity<String> handleWhatsAppReply(@RequestBody MultiValueMap<String, String> payload) {
        String from = payload.getFirst("From");
        String body = payload.getFirst("Body");
        String profileName = payload.getFirst("ProfileName");
        if (from != null && body != null) {
            String phoneNumber = from.replace("whatsapp:", "");
            String sender = profileName != null ? profileName : phoneNumber;
            Location defaultLocation = new Location(0.0, 0.0, "Unknown", "Unknown");
            String chatGroup = LocationUtils.getChatGroup(defaultLocation);
            ChatMessage message = new ChatMessage(sender, body, chatGroup, "WHATSAPP");
            message.setTimestamp(LocalDateTime.now());
            chatMessageRepository.save(message);
            messagingTemplate.convertAndSend("/topic/chat/" + chatGroup, message);
            return ResponseEntity.ok("Message received");
        }
        return ResponseEntity.badRequest().body("Invalid payload");
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @PostMapping("/location")
    public String saveLocation(@RequestParam(required = false) Double latitude,
                               @RequestParam(required = false) Double longitude,
                               HttpSession session,
                               Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }
        if (latitude != null && longitude != null) {
            Location location = new Location(latitude, longitude);
            session.setAttribute("location", location);
        } else {
            try {
                Location location = getLocationFromIp();
                session.setAttribute("location", location);
            } catch (Exception e) {
                model.addAttribute("error", "Unable to retrieve location: " + e.getMessage());
            }
        }
        return "redirect:/dashboard";
    }

    private Location getLocationFromIp() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://ip-api.com/json/?fields=lat,lon,city,country"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(response.body());
        return new Location(
                json.get("lat").asDouble(),
                json.get("lon").asDouble(),
                json.get("city").asText(),
                json.get("country").asText()
        );
    }
}
