package ru.aaromanov1985.botonator.simplebot.conversation.node.builder;

import ru.aaromanov1985.botonator.simplebot.conversation.node.Nodes;

public interface NodeBuilder {

    Nodes buildNodes(String path);
}
