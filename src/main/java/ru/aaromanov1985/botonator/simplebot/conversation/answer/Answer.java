package ru.aaromanov1985.botonator.simplebot.conversation.answer;

import ru.aaromanov1985.botonator.simplebot.conversation.node.MessageType;

import java.io.File;
import java.util.List;

public class Answer {

    private MessageType messageType;
    private String image;
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

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "messageType=" + messageType +
                ", image='" + image + '\'' +
                ", chatId='" + chatId + '\'' +
                ", message='" + message + '\'' +
                ", nextNode='" + nextNode + '\'' +
                ", variants=" + variants +
                '}';
    }
}
