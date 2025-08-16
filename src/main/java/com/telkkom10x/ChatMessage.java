package com.telkkom10x;

import java.time.LocalDateTime;

public class ChatMessage {
    private Long id;
    private String sender;
    private String content;
    private String group;
    private LocalDateTime timestamp;

    public ChatMessage() {}

    public ChatMessage(String sender, String content, String group) {
        this.sender = sender;
        this.content = content;
        this.group = group;
    }

    public ChatMessage(Long id, String sender, String content, String group, LocalDateTime timestamp) {
        this.id = id;
        this.sender = sender;
        this.content = content;
        this.group = group;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}