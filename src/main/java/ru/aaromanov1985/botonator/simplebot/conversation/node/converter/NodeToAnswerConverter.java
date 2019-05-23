package ru.aaromanov1985.botonator.simplebot.conversation.node.converter;

import ru.aaromanov1985.botonator.simplebot.conversation.answer.Answer;
import ru.aaromanov1985.botonator.simplebot.conversation.node.Node;
import ru.aaromanov1985.botonator.simplebot.conversation.node.service.NodeService;
import ru.aaromanov1985.botonator.simplebot.conversation.service.ConversationService;

import javax.annotation.Resource;

public class NodeToAnswerConverter {

    @Resource
    private NodeService nodeService;
    @Resource
    private ConversationService conversationService;

    public void convert(Node source, Answer target){
        target.setMessageType(nodeService.getNodeType(source));
        target.setMessage(conversationService.convertMessages(source.getMessages()));
        target.setVariants(conversationService.convertVariants(source.getVariants()));
        target.setImage(source.getImage());
    }
}
