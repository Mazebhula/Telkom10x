package com.telkom.service;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.telkom.model.UserData;
import com.telkom.repository.UserDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class FormService {
    @Autowired
    private UserDataRepository userDataRepository;

    public UserData saveUserData(UserData userData) {
        return userDataRepository.save(userData);
    }

    public UserData getUserData(String email) {
        List<UserData> results = userDataRepository.findByEmail(email);
        return results.isEmpty() ? null : results.get(0);
    }

    public byte[] fillPdfForm(byte[] pdfBytes, UserData userData) throws Exception {
        // Check for missing fields
        List<String> missingFields = new ArrayList<>();
        if (userData.getEmail() == null || userData.getEmail().isEmpty()) missingFields.add("email");
        if (userData.getFirstName() == null || userData.getFirstName().isEmpty()) missingFields.add("firstName");
        if (userData.getLastName() == null || userData.getLastName().isEmpty()) missingFields.add("lastName");
        if (userData.getAddress() == null || userData.getAddress().isEmpty()) missingFields.add("address");
        if (userData.getPhone() == null || userData.getPhone().isEmpty()) missingFields.add("phone");

        if (!missingFields.isEmpty()) {
            throw new MissingFieldsException("Missing required fields: " + String.join(", ", missingFields), missingFields);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfReader reader = new PdfReader(new ByteArrayInputStream(pdfBytes));
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        Map<String, PdfFormField> fields = form.getAllFormFields();
        fields.forEach((name, field) -> {
            if (name.toLowerCase().contains("first") || name.toLowerCase().contains("name")) {
                field.setValue(userData.getFirstName());
            } else if (name.toLowerCase().contains("last") && userData.getLastName() != null) {
                field.setValue(userData.getLastName());
            } else if (name.toLowerCase().contains("email") && userData.getEmail() != null) {
                field.setValue(userData.getEmail());
            } else if (name.toLowerCase().contains("address") && userData.getAddress() != null) {
                field.setValue(userData.getAddress());
            } else if (name.toLowerCase().contains("phone") && userData.getPhone() != null) {
                field.setValue(userData.getPhone());
            }
        });

        form.flattenFields();
        pdfDoc.close();
        return outputStream.toByteArray();
    }

    public byte[] fillPdfFormWithAdditional(byte[] pdfBytes, UserData userData, Map<String, String> additionalFields) throws Exception {
        // Update userData with additional fields
        if (additionalFields.containsKey("firstName")) userData.setFirstName(additionalFields.get("firstName"));
        if (additionalFields.containsKey("lastName")) userData.setLastName(additionalFields.get("lastName"));
        if (additionalFields.containsKey("address")) userData.setAddress(additionalFields.get("address"));
        if (additionalFields.containsKey("phone")) userData.setPhone(additionalFields.get("phone"));
        // Email is already provided, so no need to update it

        // Save updated user data
        userDataRepository.save(userData);

        // Fill PDF with updated data
        return fillPdfForm(pdfBytes, userData);
    }
}

