package com.telkom.service;

import java.util.List;

// Class to hold PDF fill result
public class PdfFillResult {
    public final byte[] pdfBytes;
    public final List<String> missingFields;

    public PdfFillResult(byte[] pdfBytes, List<String> missingFields) {
        this.pdfBytes = pdfBytes;
        this.missingFields = missingFields;
    }
}