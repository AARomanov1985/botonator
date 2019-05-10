package ru.aaromanov1985.botonator.simplebot.conversation.service;

import ru.aaromanov1985.botonator.simplebot.conversation.Message;
import ru.aaromanov1985.botonator.simplebot.node.Variant;

import java.util.List;

public interface ConversationService {
    String convertMessages(List<Message> messages);

    List<String> convertVariants(List<Variant> variants);
}
