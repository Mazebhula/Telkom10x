package com.telkom.model;

import jakarta.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
public class UserData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String phone;

    @ElementCollection
    @MapKeyColumn(name = "field_name")
    @Column(name = "field_value")
    @CollectionTable(name = "user_data_additional_fields", joinColumns = @JoinColumn(name = "user_data_id"))
    private Map<String, String> additionalFields = new HashMap<>();

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Map<String, String> getAdditionalFields() {
        return additionalFields;
    }

    public void setAdditionalFields(Map<String, String> additionalFields) {
        this.additionalFields = additionalFields;
    }

    @Override
    public String toString() {
        return "UserData{id=" + id + ", firstName='" + firstName + "', lastName='" + lastName +
                "', email='" + email + "', address='" + address + "', phone='" + phone +
                "', additionalFields=" + additionalFields + "}";
    }
}