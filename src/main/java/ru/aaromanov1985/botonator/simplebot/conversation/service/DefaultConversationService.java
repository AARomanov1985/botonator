package ru.aaromanov1985.botonator.simplebot.conversation.service;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aaromanov1985.botonator.simplebot.conversation.Conversation;
import ru.aaromanov1985.botonator.simplebot.conversation.node.Message;
import ru.aaromanov1985.botonator.simplebot.conversation.answer.Answer;
import ru.aaromanov1985.botonator.simplebot.conversation.node.Node;
import ru.aaromanov1985.botonator.simplebot.conversation.node.Variant;
import ru.aaromanov1985.botonator.simplebot.conversation.node.converter.NodeToAnswerConverter;
import ru.aaromanov1985.botonator.simplebot.conversation.node.service.NodeService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultConversationService implements ConversationService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultConversationService.class);

    private boolean addNewLines;
    private String bad_request;
    private long timeout;

    @Resource
    private NodeService nodeService;
    @Resource
    private NodeToAnswerConverter nodeToAnswerConverter;

    @Override
    public Answer executeForNode(final String message, final Conversation conversation) {
        LOG.debug("execute conversation for message {}", message);
        conversation.setLastRequest(System.currentTimeMillis());

        Node node = getAnswer(message, conversation);
        Answer answer = new Answer(String.valueOf(conversation.getId()));
        nodeToAnswerConverter.convert(node, answer);
        answer.setNextNode(conversation.getCurrentNode().getNextNode());
        return answer;
    }

    private Node getAnswer(final String request, final Conversation conversation) {

        if (nodeService.isEndNode(conversation.getCurrentNode())
            || nodeService.isErrorNode(conversation.getCurrentNode())){
            initConversation(conversation);
        }

        if (conversation.isFirstRequest()) {
            conversation.setFirstRequest(false);
            return conversation.getCurrentNode();
        }

        Node node = nodeService.getAnswer(conversation.getCurrentNode(), request);

        if (nodeService.isErrorNode(node)) {
            LOG.error("It's error node!");
            Node errNode = nodeService.getErrorNode();
            errNode.setMessages(Collections.singletonList(new Message(bad_request)));
            return errNode;
        }

        updateCurrentNode(node, conversation);
        return node;
    }

    private void updateCurrentNode(final Node node, final Conversation conversation) {
        if (node != null) {
            conversation.setCurrentNode(node);
            LOG.info("current node changed to " + node.getCode());
        }
    }

    @Override
    public Conversation buildConversation(long id){
        LOG.debug("Create conversation");
        Conversation conversation = new Conversation();
        conversation.setId(id);
        initConversation(conversation);
        LOG.debug("id= {}", id);
        LOG.debug("currentNode= {}", conversation.getCurrentNode().getCode());
        LOG.debug("isActive= {}", conversation.isActive());

        return conversation;
    }

    private void initConversation(final Conversation conversation){
        conversation.setCurrentNode(nodeService.getStartNode());
        conversation.setActive(true);
        conversation.setFirstRequest(true);
    }

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

    public void setBad_request(String bad_request) {
        this.bad_request = bad_request;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
