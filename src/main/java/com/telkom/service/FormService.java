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
        if (userData.getFirstName() == null || userData.getFirstName().isEmpty()) missingFields.add("firstName");
        if (userData.getLastName() == null || userData.getLastName().isEmpty()) missingFields.add("lastName");
        if (userData.getEmail() == null || userData.getEmail().isEmpty()) missingFields.add("email");
        if (userData.getAddress() == null || userData.getAddress().isEmpty()) missingFields.add("address");
        if (userData.getPhone() == null || userData.getPhone().isEmpty()) missingFields.add("phone");

        LOGGER.info("Missing fields: " + missingFields);

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

            // Fill fields using PDF field names
            if (fields.containsKey("FirstName") && userData.getFirstName() != null && !userData.getFirstName().isEmpty()) {
                form.getField("FirstName").setValue(userData.getFirstName());
                LOGGER.info("Filled field 'FirstName' with value: " + userData.getFirstName());
            } else {
                LOGGER.warning("Field 'FirstName' not filled: " + (fields.containsKey("FirstName") ? "empty/null value" : "field not found"));
            }
            if (fields.containsKey("LastName") && userData.getLastName() != null && !userData.getLastName().isEmpty()) {
                form.getField("LastName").setValue(userData.getLastName());
                LOGGER.info("Filled field 'LastName' with value: " + userData.getLastName());
            } else {
                LOGGER.warning("Field 'LastName' not filled: " + (fields.containsKey("LastName") ? "empty/null value" : "field not found"));
            }
            if (fields.containsKey("EmailAddress") && userData.getEmail() != null && !userData.getEmail().isEmpty()) {
                form.getField("EmailAddress").setValue(userData.getEmail());
                LOGGER.info("Filled field 'EmailAddress' with value: " + userData.getEmail());
            } else {
                LOGGER.warning("Field 'EmailAddress' not filled: " + (fields.containsKey("EmailAddress") ? "empty/null value" : "field not found"));
            }
            if (fields.containsKey("StreetAddress") && userData.getAddress() != null && !userData.getAddress().isEmpty()) {
                form.getField("StreetAddress").setValue(userData.getAddress());
                LOGGER.info("Filled field 'StreetAddress' with value: " + userData.getAddress());
            } else {
                LOGGER.warning("Field 'StreetAddress' not filled: " + (fields.containsKey("StreetAddress") ? "empty/null value" : "field not found"));
            }
            if (fields.containsKey("PhoneNumber") && userData.getPhone() != null && !userData.getPhone().isEmpty()) {
                form.getField("PhoneNumber").setValue(userData.getPhone());
                LOGGER.info("Filled field 'PhoneNumber' with value: " + userData.getPhone());
            } else {
                LOGGER.warning("Field 'PhoneNumber' not filled: " + (fields.containsKey("PhoneNumber") ? "empty/null value" : "field not found"));
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

        // Update userData with additional fields
        if (additionalFields.containsKey("firstName") && !additionalFields.get("firstName").isEmpty()) {
            userData.setFirstName(additionalFields.get("firstName"));
            LOGGER.info("Updated firstName: " + userData.getFirstName());
        }
        if (additionalFields.containsKey("lastName") && !additionalFields.get("lastName").isEmpty()) {
            userData.setLastName(additionalFields.get("lastName"));
            LOGGER.info("Updated lastName: " + userData.getLastName());
        }
        if (additionalFields.containsKey("email") && !additionalFields.get("email").isEmpty()) {
            userData.setEmail(additionalFields.get("email"));
            LOGGER.info("Updated email: " + userData.getEmail());
        }
        if (additionalFields.containsKey("address") && !additionalFields.get("address").isEmpty()) {
            userData.setAddress(additionalFields.get("address"));
            LOGGER.info("Updated address: " + userData.getAddress());
        }
        if (additionalFields.containsKey("phone") && !additionalFields.get("phone").isEmpty()) {
            userData.setPhone(additionalFields.get("phone"));
            LOGGER.info("Updated phone: " + userData.getPhone());
        }

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