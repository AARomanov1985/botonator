package ru.aaromanov1985.botonator.simplebot.conversation.service;

import ru.aaromanov1985.botonator.simplebot.conversation.Conversation;
import ru.aaromanov1985.botonator.simplebot.conversation.Message;
import ru.aaromanov1985.botonator.simplebot.conversation.answer.Answer;
import ru.aaromanov1985.botonator.simplebot.node.Variant;

import java.util.List;

public interface ConversationService {
    Answer executeForNode(String message, Conversation conversation);

    Conversation buildConversation(long id);

    String convertMessages(List<Message> messages);

    List<String> convertVariants(List<Variant> variants);
}
