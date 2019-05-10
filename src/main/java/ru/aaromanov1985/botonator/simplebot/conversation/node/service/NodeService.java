package ru.aaromanov1985.botonator.simplebot.conversation.node.service;

import ru.aaromanov1985.botonator.simplebot.conversation.node.MessageType;
import ru.aaromanov1985.botonator.simplebot.conversation.node.Node;

import java.io.File;

public interface NodeService {

    void buildNodes(final String path);

    Node getStartNode();

    Node getEndNode();

    Node getSuccessNode();

    Node getAnswer(final Node currentNode, final String request);

    Node findNodeForCode(final String nodeCode);

    Node getErrorNode();

    MessageType getNodeType(final Node node);

    String getMessage(final Node node);

    boolean isErrorNode(final Node node);

    boolean isStartNode(final Node node);

    boolean isEndNode(final Node node);
}
