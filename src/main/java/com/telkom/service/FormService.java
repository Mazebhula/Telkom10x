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
        LOGGER.info("Saving user data for email: " + userData.getEmail());
        return userDataRepository.save(userData);
    }

    public UserData getUserData(String email) {
        LOGGER.info("Fetching user data for email: " + email);
        List<UserData> results = userDataRepository.findByEmail(email);
        return results.isEmpty() ? null : results.get(0);
    }

    public PdfFillResult fillPdfForm(byte[] pdfBytes, UserData userData) throws Exception {
        List<String> missingFields = new ArrayList<>();
        if (userData.getEmail() == null || userData.getEmail().isEmpty()) missingFields.add("email");
        if (userData.getFirstName() == null || userData.getFirstName().isEmpty()) missingFields.add("firstName");
        if (userData.getLastName() == null || userData.getLastName().isEmpty()) missingFields.add("lastName");
        if (userData.getAddress() == null || userData.getAddress().isEmpty()) missingFields.add("address");
        if (userData.getPhone() == null || userData.getPhone().isEmpty()) missingFields.add("phone");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfReader reader = new PdfReader(new ByteArrayInputStream(pdfBytes));
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        Map<String, PdfFormField> fields = form.getAllFormFields();

        LOGGER.info("Available PDF form fields: " + fields.keySet());

        if (userData.getFirstName() != null && !userData.getFirstName().isEmpty()) {
            if (fields.containsKey("firstName")) {
                form.getField("firstName").setValue(userData.getFirstName());
                LOGGER.info("Filled field 'firstName' with value: " + userData.getFirstName());
            }
        }
        if (userData.getLastName() != null && !userData.getLastName().isEmpty()) {
            if (fields.containsKey("lastName")) {
                form.getField("lastName").setValue(userData.getLastName());
                LOGGER.info("Filled field 'lastName' with value: " + userData.getLastName());
            }
        }
        if (userData.getEmail() != null && !userData.getEmail().isEmpty()) {
            if (fields.containsKey("email")) {
                form.getField("email").setValue(userData.getEmail());
                LOGGER.info("Filled field 'email' with value: " + userData.getEmail());
            }
        }
        if (userData.getAddress() != null && !userData.getAddress().isEmpty()) {
            if (fields.containsKey("address")) {
                form.getField("address").setValue(userData.getAddress());
                LOGGER.info("Filled field 'address' with value: " + userData.getAddress());
            }
        }
        if (userData.getPhone() != null && !userData.getPhone().isEmpty()) {
            if (fields.containsKey("phone")) {
                form.getField("phone").setValue(userData.getPhone());
                LOGGER.info("Filled field 'phone' with value: " + userData.getPhone());
            }
        }

        form.flattenFields();
        pdfDoc.close();
        reader.close();
        return new PdfFillResult(outputStream.toByteArray(), missingFields);
    }

    public byte[] fillPdfFormWithAdditional(byte[] pdfBytes, UserData userData, Map<String, String> additionalFields) throws Exception {
        LOGGER.info("Processing additional fields: " + additionalFields.keySet());

        // Update userData with additional fields
        if (additionalFields.containsKey("firstName")) userData.setFirstName(additionalFields.get("firstName"));
        if (additionalFields.containsKey("lastName")) userData.setLastName(additionalFields.get("lastName"));
        if (additionalFields.containsKey("address")) userData.setAddress(additionalFields.get("address"));
        if (additionalFields.containsKey("phone")) userData.setPhone(additionalFields.get("phone"));

        // Save updated user data
        if (userData.getEmail() != null) {
            userDataRepository.save(userData);
            LOGGER.info("Updated user data saved for email: " + userData.getEmail());
        }

        // Fill PDF with updated data
        PdfFillResult result = fillPdfForm(pdfBytes, userData);
        if (!result.missingFields.isEmpty()) {
            LOGGER.warning("Missing fields after additional input: " + result.missingFields);
            throw new MissingFieldsException("Some fields are still missing: " + String.join(", ", result.missingFields), result.missingFields, result.pdfBytes);
        }
        return result.pdfBytes;
    }
}