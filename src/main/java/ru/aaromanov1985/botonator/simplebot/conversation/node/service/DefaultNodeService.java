package ru.aaromanov1985.botonator.simplebot.conversation.node.service;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aaromanov1985.botonator.simplebot.conversation.node.MessageType;
import ru.aaromanov1985.botonator.simplebot.conversation.service.ConversationService;
import ru.aaromanov1985.botonator.simplebot.conversation.node.Node;
import ru.aaromanov1985.botonator.simplebot.conversation.node.Nodes;
import ru.aaromanov1985.botonator.simplebot.conversation.node.Variant;
import ru.aaromanov1985.botonator.simplebot.conversation.node.builder.DefaultXmlNodeBuilder;

import javax.annotation.Resource;
import java.util.List;

public class DefaultNodeService implements NodeService {

    @Resource
    private DefaultXmlNodeBuilder nodeBuilder;
    @Resource
    private ConversationService conversationService;

    private String path;

    private Nodes nodes;

    private static final Logger LOG = LoggerFactory.getLogger(DefaultNodeService.class);

    private static final String START_NODE = "startNode";
    private static final String END_NODE = "endNode";
    private static final String ERROR_NODE = "errorNode";
    private static final String SUCCESS_NODE = "successNode";

    @Override
    public void buildNodes(final String path){
        nodes = nodeBuilder.buildNodes(path);
    }

    @Override
    public Node getStartNode() {
        return findNodeForCode(START_NODE);
    }

    @Override
    public Node getEndNode() {
        return findNodeForCode(END_NODE);
    }

    @Override
    public Node getSuccessNode() {
        return findNodeForCode(SUCCESS_NODE);
    }

    @Override
    public Node getAnswer(final Node currentNode, final String request) {
        LOG.debug("getAnswer for node {} and request {}", currentNode.getCode(), request);
        List<Variant> variants = currentNode.getVariants();
        if (CollectionUtils.isNotEmpty(variants)) {
            for (Variant variant : variants) {
                if (request.equals(variant.getValue())) {
                    Node nodeForCode = findNodeForCode(variant.getTarget());
                    LOG.debug("Found Node: {}", nodeForCode);
                    return findNodeForCode(variant.getTarget());
                }
            }
        }
        LOG.error("targetNode is not found for request " + request);
        return getErrorNode();
    }

    @Override
    public Node findNodeForCode(final String nodeCode) {
        LOG.debug("Find node for code {}",nodeCode);
        for (Node node : nodes.getNodes()) {
            if (node.getCode().equals(nodeCode)) {
                LOG.debug("Found Node: {}", node.getCode());
                return node;
            }
        }
        LOG.error("Node for code {} is not found!", nodeCode);
        return getErrorNode();
    }

    @Override
    public Node getErrorNode() {
        Node node = new Node();
        node.setCode(ERROR_NODE);
        node.setMessageType(MessageType.TEXT.toString());
        return node;
    }

    @Override
    public MessageType getNodeType(final Node node){
        LOG.debug("MessageType: {}", node.getMessageType());
        return MessageType.valueOf(node.getMessageType().toUpperCase());
    }

    @Override
    public String getMessage(final Node node){
        return conversationService.convertMessages(node.getMessages());
    }

    @Override
    public boolean isErrorNode(final Node node) {
        return node == null || node.getCode().equals(ERROR_NODE);
    }

    @Override
    public boolean isStartNode(final Node node) {
        return node != null && node.getCode().equals(START_NODE);
    }

    @Override
    public boolean isEndNode(final Node node){
        return node != null && END_NODE.equals(node.getCode());
    }

    public void setPath(String path) {
        this.path = path;
    }
}
