package com.telkom.controller;

import com.telkom.model.UserData;
import com.telkom.service.FormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FormController {
    @Autowired
    private FormService formService;

    @GetMapping("/")
    public String showForm(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("userData", new UserData());
            return "dashboard"; // Maps to form.html for "Find a Taxi"
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLogin(Model model, @RequestParam(value = "error", required = false) String error) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
        }
        return "login"; // Maps to login.html
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName(); // Assumes email is the username in authentication
            UserData userData = formService.getUserData(email);
            model.addAttribute("username", email);
            model.addAttribute("userData", userData != null ? userData : new UserData());
            return "dashboard"; // Maps to view.html for "whoami"
        }
        return "redirect:/dashboard";
    }
    @GetMapping("/form")
    public String showFormPage(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("userData", new UserData());
            return "form"; // renders form.html
        }
        return "redirect:/login";
    }


    @PostMapping("/save")
    public String saveUserData(@ModelAttribute UserData userData, Model model) {
        try {
            formService.saveUserData(userData);
            model.addAttribute("message", "Data saved successfully!");
        } catch (Exception e) {
            model.addAttribute("message", "Error saving data: " + e.getMessage());
        }
        return "form";
    }

    @GetMapping("/view")
    public String viewData(@RequestParam String email, Model model) {
        UserData userData = formService.getUserData(email);
        model.addAttribute("userData", userData != null ? userData : new UserData());
        return "view";
    }

    @PostMapping("/fill-pdf")
    public ResponseEntity<byte[]> fillPdf(@RequestParam String email, @RequestParam("file") MultipartFile file, Model model) throws Exception {
        UserData userData = formService.getUserData(email);
        if (userData == null) {
            model.addAttribute("message", "No user data found for email: " + email);
            return ResponseEntity.badRequest()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(("No user data found for email: " + email).getBytes());
        }
        byte[] filledPdf = formService.fillPdfForm(file.getBytes(), userData);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filled_form.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(filledPdf);
    }
}