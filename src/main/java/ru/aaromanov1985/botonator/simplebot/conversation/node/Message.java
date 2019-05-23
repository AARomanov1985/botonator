package ru.aaromanov1985.botonator.simplebot.conversation.node;

import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"value"}, name = "message")
public class Message {
    private String value;

    public Message(){

    }

    public Message (String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
