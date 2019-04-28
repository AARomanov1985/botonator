package ru.aaromanov1985.botonator.simplebot.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.aaromanov1985.botonator.simplebot.node.builder.DefaultXmlNodeBuilder;

// TODO
class DefaultXmlNodeBuilderUnitTest {

    private DefaultXmlNodeBuilder builder = new DefaultXmlNodeBuilder();

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