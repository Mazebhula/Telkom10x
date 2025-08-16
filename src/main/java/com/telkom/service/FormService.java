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
        return results.isEmpty() ? null : results.get(0); // Return first result or null if none
    }

    public byte[] fillPdfForm(byte[] pdfBytes, UserData userData) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfReader reader = new PdfReader(new ByteArrayInputStream(pdfBytes));
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        System.out.println("test");
        Map<String, PdfFormField> fields = form.getAllFormFields();
        fields.forEach((name, field) -> {
            System.out.println(field);
            if (name.toLowerCase().contains("first") || name.toLowerCase().contains("Name")) {
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
}