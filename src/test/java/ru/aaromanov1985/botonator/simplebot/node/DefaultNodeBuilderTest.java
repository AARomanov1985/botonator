package ru.aaromanov1985.botonator.simplebot.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// TODO
class DefaultNodeBuilderTest {

    private DefaultNodeBuilder builder = new DefaultNodeBuilder();

    @BeforeEach
    void setUp() {
        builder.setPath("resources/Nodes.xml");
        Nodes nodes = builder.buildNodes();
        // TODO
    }

    @Test
    void buildNodes() {
    }
}