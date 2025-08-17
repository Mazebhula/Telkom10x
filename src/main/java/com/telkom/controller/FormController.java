package com.telkom.controller;

import com.telkom.model.UserData;
import com.telkom.service.FormService;
import com.telkom.service.MissingFieldsException;
import com.telkom.service.PdfFillResult;
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

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Controller
public class FormController {
    @Autowired
    private FormService formService;

    @GetMapping("/")
    public String showForm(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("userData", new UserData());
            model.addAttribute("username", authentication.getName());
            return "dashboard";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLogin(Model model, @RequestParam(value = "error", required = false) String error) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
        }
        return "login";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            UserData userData = formService.getUserData(email);
            model.addAttribute("username", email);
            model.addAttribute("userData", userData != null ? userData : new UserData());
            return "view";
        }
        return "redirect:/login";
    }

    @GetMapping("/form")
    public String showFormPage(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("userData", new UserData());
            return "form";
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
        model.addAttribute("username", email);
        return "view";
    }

    @PostMapping("/fill-pdf")
    public Object fillPdf(@RequestParam String email, @RequestParam("file") MultipartFile file, Model model) {
        try {
            UserData userData = formService.getUserData(email);
            if (userData == null) {
                return ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Map.of("message", "No data found for email: " + email));
            }
            PdfFillResult result = formService.fillPdfForm(file.getBytes(), userData);
            if (!result.missingFields.isEmpty()) {
                model.addAttribute("email", email);
                model.addAttribute("missingFields", result.missingFields);
                model.addAttribute("partialPdf", Base64.getEncoder().encodeToString(result.pdfBytes));
                return "missing_fields";
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filled_form.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(result.pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("message", "Error processing PDF: " + e.getMessage()));
        }
    }

    @PostMapping("/fill-pdf-with-additional")
    public ResponseEntity<Object> fillPdfWithAdditional(
            @RequestParam String email,
            @RequestParam("partialPdf") String partialPdf,
            @RequestParam Map<String, String> allParams) {
        try {
            UserData userData = formService.getUserData(email);
            if (userData == null) {
                return ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Map.of("message", "No data found for email: " + email));
            }
            Map<String, String> additionalFields = new HashMap<>(allParams);
            additionalFields.remove("email");
            additionalFields.remove("partialPdf");
            byte[] pdfBytes = Base64.getDecoder().decode(partialPdf);
            byte[] filledPdf = formService.fillPdfFormWithAdditional(pdfBytes, userData, additionalFields);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=filled_form.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(filledPdf);
        } catch (MissingFieldsException e) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "message", e.getMessage(),
                            "missingFields", e.getMissingFields(),
                            "partialPdf", Base64.getEncoder().encodeToString(e.getPartialPdf())
                    ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("message", "Error processing PDF with additional fields: " + e.getMessage()));
        }
    }
}