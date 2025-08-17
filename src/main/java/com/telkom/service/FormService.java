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
import java.util.logging.Logger;

@Service
public class FormService {
    private static final Logger LOGGER = Logger.getLogger(FormService.class.getName());

    @Autowired
    private UserDataRepository userDataRepository;

    public UserData saveUserData(UserData userData) {
        return userDataRepository.save(userData);
    }

    public UserData getUserData(String email) {
        List<UserData> results = userDataRepository.findByEmail(email);
        return results.isEmpty() ? null : results.get(0);
    }

    public PdfFillResult fillPdfForm(byte[] pdfBytes, UserData userData) throws Exception {
        // Check for missing fields
        List<String> missingFields = new ArrayList<>();
        if (userData.getEmail() == null || userData.getEmail().isEmpty()) missingFields.add("email");
        if (userData.getFirstName() == null || userData.getFirstName().isEmpty()) missingFields.add("firstName");
        if (userData.getLastName() == null || userData.getLastName().isEmpty()) missingFields.add("lastName");
        if (userData.getAddress() == null || userData.getAddress().isEmpty()) missingFields.add("address");
        if (userData.getPhone() == null || userData.getPhone().isEmpty()) missingFields.add("phone");

        // Fill PDF with available fields
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfReader reader = new PdfReader(new ByteArrayInputStream(pdfBytes));
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        Map<String, PdfFormField> fields = form.getAllFormFields();

        // Log all field names for debugging
        LOGGER.info("Available PDF form fields: " + fields.keySet());

        fields.forEach((name, field) -> {
            // More precise field matching
            if (name.equalsIgnoreCase("FirstName") || name.equalsIgnoreCase("First Name") || name.equalsIgnoreCase("GivenName")) {
                if (userData.getFirstName() != null && !userData.getFirstName().isEmpty()) {
                    field.setValue(userData.getFirstName());
                    LOGGER.info("Filled field '" + name + "' with value: " + userData.getFirstName());
                }
            } else if (name.equalsIgnoreCase("LastName") || name.equalsIgnoreCase("Last Name") || name.equalsIgnoreCase("Surname")) {
                if (userData.getLastName() != null && !userData.getLastName().isEmpty()) {
                    field.setValue(userData.getLastName());
                    LOGGER.info("Filled field '" + name + "' with value: " + userData.getLastName());
                }
            } else if (name.equalsIgnoreCase("Email") || name.equalsIgnoreCase("EmailAddress")) {
                if (userData.getEmail() != null && !userData.getEmail().isEmpty()) {
                    field.setValue(userData.getEmail());
                    LOGGER.info("Filled field '" + name + "' with value: " + userData.getEmail());
                }
            } else if (name.equalsIgnoreCase("Address") || name.equalsIgnoreCase("StreetAddress")) {
                if (userData.getAddress() != null && !userData.getAddress().isEmpty()) {
                    field.setValue(userData.getAddress());
                    LOGGER.info("Filled field '" + name + "' with value: " + userData.getAddress());
                }
            } else if (name.equalsIgnoreCase("Phone") || name.equalsIgnoreCase("PhoneNumber") || name.equalsIgnoreCase("Telephone")) {
                if (userData.getPhone() != null && !userData.getPhone().isEmpty()) {
                    field.setValue(userData.getPhone());
                    LOGGER.info("Filled field '" + name + "' with value: " + userData.getPhone());
                }
            }
        });

        form.flattenFields();
        pdfDoc.close();
        byte[] partialPdf = outputStream.toByteArray();

        return new PdfFillResult(partialPdf, missingFields);
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
        PdfFillResult result = fillPdfForm(pdfBytes, userData);
        if (!result.missingFields.isEmpty()) {
            throw new MissingFieldsException("Some fields are still missing after additional input: " + String.join(", ", result.missingFields), result.missingFields, result.pdfBytes);
        }
        return result.pdfBytes;
    }
}



