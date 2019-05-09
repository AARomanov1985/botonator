package ru.aaromanov1985.botonator.simplebot.node;

import ru.aaromanov1985.botonator.simplebot.conversation.Message;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlType(propOrder = {"code", "messages", "type", "nextNode", "variants"}, name = "node")
public class Node {

    private String code;
    private List<Message> messages;
    private String type;
    private String nextNode;
    private List<Variant> variants;

    public String getNextNode() {
        return nextNode;
    }

    public void setNextNode(String nextNode) {
        this.nextNode = nextNode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @XmlElementWrapper(name = "messages")
    @XmlElement(name = "message")
    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    @XmlElementWrapper(name = "variants")
    @XmlElement(name = "variant")
    public List<Variant> getVariants() {
        return variants;
    }

    public void setVariants(List<Variant> variants) {
        this.variants = variants;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
