package ru.aaromanov1985.botonator.simplebot.conversation.node;

import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "value", "target"}, name = "variant")
public class Variant {
    private String value;
    private String target;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
