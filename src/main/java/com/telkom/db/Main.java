package com.telkom.db;

public class Main {
    public static void main(String[] args) {
        try {
            FormDataManager manager = new FormDataManager("userforms.db");

            // Simulate user filling fields
//            manager.saveField("user123", "job_application", "email", "test@example.com");
//            manager.saveField("user123", "job_application", "name", "Alice Wonderland");
//            manager.saveField("user123", "job_application", "phone", "+123456789");
//
//            // Add new fields later
//            manager.saveField("user123", "loan_form", "ssn", "123-45-6789");
//
//            // Show all decrypted data
//            System.out.println("🔐 Decrypted Fields:");
//            manager.printDecryptedFields("user123");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
