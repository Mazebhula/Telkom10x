package com.telkkom10x.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {

    // Home page (public, no authentication required)
    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("welcomeMessage", "Welcome to Telkkom10x!");
        return "home"; // Renders src/main/resources/templates/home.html
    }

    // Login page
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Renders src/main/resources/templates/login.html
    }

    // Login submission
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        // Input validation
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "Username and password are required");
            return "login";
        }

        // Simple authentication logic (replace with Spring Security in production)
        if ("admin".equals(username) && "password".equals(password)) {
            session.setAttribute("username", username);
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Invalid credentials");
            return "login";
        }
    }

    // Dashboard (protected)
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }
        model.addAttribute("username", username);
        return "dashboard"; // Renders src/main/resources/templates/dashboard.html
    }

    // Find Taxi (protected)
    @GetMapping("/find_taxi")
    public String findTaxi(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }
        model.addAttribute("username", username);
        model.addAttribute("message", "Find a taxi near you!");
        return "find_taxi"; // Renders src/main/resources/templates/find_taxi.html
    }

    // Chat (protected)
    @GetMapping("/chat")
    public String chat(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }
        model.addAttribute("username", username);
        model.addAttribute("message", "Start chatting!");
        return "chat"; // Renders src/main/resources/templates/chat.html
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}