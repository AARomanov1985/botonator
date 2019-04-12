package ru.aaromanov1985.botonator.simplebot;

import java.util.List;

public class Answer {

    private String chatId;
    private String message;
    private String nextNode;
    private List<String> variants;

    public Answer(String chatId) {
        this.chatId = chatId;
    }

    public Answer(String chatId, String message, String nextNode, List<String> variants) {
        this.chatId = chatId;
        this.message = message;
        this.nextNode = nextNode;
        this.variants = variants;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNextNode() {
        return nextNode;
    }

    public void setNextNode(String nextNode) {
        this.nextNode = nextNode;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public List<String> getVariants() {
        return variants;
    }

    public void setVariants(List<String> variants) {
        this.variants = variants;
    }
}
