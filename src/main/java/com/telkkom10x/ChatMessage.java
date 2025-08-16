package com.telkkom10x;

public class ChatMessage {
    private String sender;
    private String content;
    private String group; // City or proximity-based group ID

    public ChatMessage() {}

    public ChatMessage(String sender, String content, String group) {
        this.sender = sender;
        this.content = content;
        this.group = group;
    }

    // Getters and setters
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
}