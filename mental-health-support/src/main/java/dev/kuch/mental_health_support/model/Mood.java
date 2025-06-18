package dev.kuch.mental_health_support.model;

import java.time.LocalDateTime;


public class Mood {

    private Long id;
    private String chatId;
    private String mood;
    private String description;
    private LocalDateTime timestamp = LocalDateTime.now();


    public Mood() {
    }

    public Mood(String chatId, String mood) {
        this.chatId = chatId;
        this.mood = mood;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
