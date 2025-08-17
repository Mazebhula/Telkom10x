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
        LOGGER.info("Saving user data for email: " + userData.getEmail() + ", data: " + userData);
        try {
            UserData savedData = userDataRepository.save(userData);
            LOGGER.info("Saved user data: " + savedData);
            return savedData;
        } catch (Exception e) {
            LOGGER.severe("Error saving user data: " + e.getMessage());
            throw new RuntimeException("Failed to save user data", e);
        }
    }

    public UserData getUserData(String email) {
        LOGGER.info("Fetching user data for email: " + email);
        List<UserData> results = userDataRepository.findByEmail(email);
        if (results.isEmpty()) {
            LOGGER.warning("No user data found for email: " + email);
            return null;
        }
        UserData userData = results.get(0);
        LOGGER.info("Retrieved user data: " + userData);
        return userData;
    }

    public PdfFillResult fillPdfForm(byte[] pdfBytes, UserData userData) throws Exception {
        LOGGER.info("Filling PDF for user: " + userData.getEmail() + ", UserData: " + userData);
        if (pdfBytes == null || pdfBytes.length == 0) {
            LOGGER.severe("Input PDF bytes are null or empty");
            throw new IllegalArgumentException("Input PDF is null or empty");
        }

        List<String> missingFields = new ArrayList<>();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfReader reader = null;
        PdfDocument pdfDoc = null;
        try {
            reader = new PdfReader(new ByteArrayInputStream(pdfBytes));
            reader.setUnethicalReading(true);
            PdfWriter writer = new PdfWriter(outputStream);
            pdfDoc = new PdfDocument(reader, writer);
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            if (form == null) {
                LOGGER.severe("No AcroForm found in PDF");
                throw new IllegalStateException("PDF does not contain an AcroForm");
            }

            Map<String, PdfFormField> fields = form.getAllFormFields();
            LOGGER.info("Available PDF form fields: " + fields.keySet());

            // Map UserData fields to PDF field names
            Map<String, String> fieldMappings = Map.of(
                    "FirstName", userData.getFirstName(),
                    "LastName", userData.getLastName(),
                    "EmailAddress", userData.getEmail(),
                    "StreetAddress", userData.getAddress(),
                    "PhoneNumber", userData.getPhone()
            );

            // Check for missing fields and fill available ones
            for (String pdfField : fields.keySet()) {
                if (pdfField.equals("Submit")) continue; // Skip submit button
                String value = fieldMappings.getOrDefault(pdfField, userData.getAdditionalFields().getOrDefault(pdfField, null));
                if (value == null || value.isEmpty()) {
                    missingFields.add(pdfField); // Use PDF field name for consistency
                    LOGGER.warning("Field '" + pdfField + "' is missing or empty");
                } else {
                    form.getField(pdfField).setValue(value);
                    LOGGER.info("Filled field '" + pdfField + "' with value: " + value);
                }
            }

            form.flattenFields();
            LOGGER.info("PDF form fields flattened");
        } catch (Exception e) {
            LOGGER.severe("Error processing PDF: " + e.getMessage());
            throw e;
        } finally {
            if (pdfDoc != null) pdfDoc.close();
            if (reader != null) reader.close();
        }

        byte[] partialPdf = outputStream.toByteArray();
        LOGGER.info("Generated PDF size: " + partialPdf.length + " bytes");
        return new PdfFillResult(partialPdf, missingFields);
    }

    public byte[] fillPdfFormWithAdditional(byte[] pdfBytes, UserData userData, Map<String, String> additionalFields) throws Exception {
        LOGGER.info("Processing additional fields for email " + userData.getEmail() + ": " + additionalFields);

        if (pdfBytes == null || pdfBytes.length == 0) {
            LOGGER.severe("Input PDF bytes are null or empty");
            throw new IllegalArgumentException("Input PDF is null or empty");
        }

        // Update UserData with additional fields
        Map<String, String> currentAdditionalFields = userData.getAdditionalFields();
        for (Map.Entry<String, String> entry : additionalFields.entrySet()) {
            String fieldName = entry.getKey();
            String value = entry.getValue();
            if (value != null && !value.isEmpty()) {
                switch (fieldName) {
                    case "FirstName":
                        userData.setFirstName(value);
                        LOGGER.info("Updated FirstName: " + value);
                        break;
                    case "LastName":
                        userData.setLastName(value);
                        LOGGER.info("Updated LastName: " + value);
                        break;
                    case "EmailAddress":
                        userData.setEmail(value);
                        LOGGER.info("Updated EmailAddress: " + value);
                        break;
                    case "StreetAddress":
                        userData.setAddress(value);
                        LOGGER.info("Updated StreetAddress: " + value);
                        break;
                    case "PhoneNumber":
                        userData.setPhone(value);
                        LOGGER.info("Updated PhoneNumber: " + value);
                        break;
                    default:
                        currentAdditionalFields.put(fieldName, value);
                        LOGGER.info("Updated additional field " + fieldName + ": " + value);
                        break;
                }
            }
        }
        userData.setAdditionalFields(currentAdditionalFields);

        // Save updated user data
        if (userData.getEmail() != null && !userData.getEmail().isEmpty()) {
            userDataRepository.save(userData);
            LOGGER.info("Saved updated user data: " + userData);
        } else {
            LOGGER.severe("Cannot save user data: email is null or empty");
            throw new IllegalArgumentException("UserData email cannot be null or empty");
        }

        // Fill PDF with updated data
        PdfFillResult result = fillPdfForm(pdfBytes, userData);
        if (!result.missingFields.isEmpty()) {
            LOGGER.warning("Missing fields after additional input: " + result.missingFields);
            throw new MissingFieldsException("Some fields are still missing: " + String.join(", ", result.missingFields), result.missingFields, result.pdfBytes);
        }
        LOGGER.info("Successfully filled PDF with all required fields");
        return result.pdfBytes;
    }
}