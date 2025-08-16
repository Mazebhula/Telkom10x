package com.telkom.model;

import jakarta.persistence.*;

@Entity
@Table(name = "form_data")
public class FormData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String field;
    private String encryptedValue;
    private String iv;

    // getters & setters
}
