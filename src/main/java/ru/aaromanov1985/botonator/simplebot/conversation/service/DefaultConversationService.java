package ru.aaromanov1985.botonator.simplebot.conversation.service;

import org.apache.commons.collections4.CollectionUtils;
import ru.aaromanov1985.botonator.simplebot.conversation.Message;
import ru.aaromanov1985.botonator.simplebot.node.Variant;

import java.util.ArrayList;
import java.util.List;

public class DefaultConversationService implements ConversationService {

    private boolean addNewLines;

    @Override
    public String convertMessages(List<Message> messages) {
        StringBuilder builder = new StringBuilder();
        if (CollectionUtils.isNotEmpty(messages)) {
            int i = 0;
            for (Message message : messages) {
                i++;
                if (i == messages.size()) {
                    addLine(builder, message, false);
                } else {
                    addLine(builder, message, addNewLines);
                }
            }
        }
        return builder.toString();
    }

    private void addLine(final StringBuilder builder, final Message message,
                         boolean addNewLines) {
        builder.append(message.getValue());
        if (addNewLines) {
            builder.append("\n");
        }
    }

    @Override
    public List<String> convertVariants(List<Variant> variants){
        List<String> variantsOfAnswers = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(variants)){
            for (Variant variant : variants){
                variantsOfAnswers.add(variant.getValue());
            }
        }
        return variantsOfAnswers;
    }

    public void setAddNewLines(boolean addNewLines) {
        this.addNewLines = addNewLines;
    }
}
