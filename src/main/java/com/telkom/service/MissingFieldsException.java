package com.telkom.service;

import java.util.List;

// Custom exception for missing fields
public class MissingFieldsException extends Exception {
    private final List<String> missingFields;
    private final byte[] partialPdf;

    public MissingFieldsException(String message, List<String> missingFields, byte[] partialPdf) {
        super(message);
        this.missingFields = missingFields;
        this.partialPdf = partialPdf;
    }

    public List<String> getMissingFields() {
        return missingFields;
    }

    public byte[] getPartialPdf() {
        return partialPdf;
    }
}
