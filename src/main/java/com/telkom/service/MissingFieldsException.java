package com.telkom.service;

import java.util.List;

// Custom exception for missing fields
public class MissingFieldsException extends Exception {
    private final List<String> missingFields;

    public MissingFieldsException(String message, List<String> missingFields) {
        super(message);
        this.missingFields = missingFields;
    }

    public List<String> getMissingFields() {
        return missingFields;
    }
}
