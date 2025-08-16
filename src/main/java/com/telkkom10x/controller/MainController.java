package com.telkkom10x.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.telkkom10x.Location;
import com.telkkom10x.LocationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class MainController {

    // Existing methods (home, login, dashboard, find_taxi, logout) unchanged
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
        return "chat";
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