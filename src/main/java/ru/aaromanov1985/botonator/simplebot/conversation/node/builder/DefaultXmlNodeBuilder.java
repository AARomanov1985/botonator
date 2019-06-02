package ru.aaromanov1985.botonator.simplebot.conversation.node.builder;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aaromanov1985.botonator.simplebot.conversation.node.Message;
import ru.aaromanov1985.botonator.simplebot.conversation.node.Node;
import ru.aaromanov1985.botonator.simplebot.conversation.node.Nodes;
import ru.aaromanov1985.botonator.simplebot.conversation.node.Variant;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.List;
import java.util.Scanner;

public class DefaultXmlNodeBuilder implements NodeBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultXmlNodeBuilder.class);

    private static final String MESSAGES_PATH = "src";
    private static final String START_FILE = "start.xml";
    private static final String MESSAGE_FILE_EXTENSION = ".html";

    public Nodes buildNodes(String path) {
        LOG.info("path = {}", path);
        try {
            JAXBContext context = JAXBContext.newInstance(Nodes.class);

            FileInputStream inputStream = new FileInputStream(path + File.separator + START_FILE);

            Unmarshaller unmarshaller =
                    context.createUnmarshaller();
            Nodes nodes = (Nodes) unmarshaller.unmarshal(inputStream);
            updateTextValues(nodes, path);
            return nodes;
        } catch (JAXBException exception) {
            exception.printStackTrace();
        } catch (FileNotFoundException e) {
            LOG.error(e.getMessage(), e);
        }
        LOG.error("Object nodes is null");

        return null;
    }

    private void updateTextValues(Nodes nodes, String path) {
        if (nodes != null && CollectionUtils.isNotEmpty(nodes.getNodes())) {
            for (Node node : nodes.getNodes()) {
                List<Message> messages = node.getMessages();
                if (CollectionUtils.isNotEmpty(messages)) {
                    messages.forEach(m -> m.setValue(getTextMessage(path, m.getValue())));
                }
                List<Variant> variants = node.getVariants();
                if (CollectionUtils.isNotEmpty(variants)) {
                    variants.forEach(v -> v.setValue(getTextMessage(path, v.getValue())));
                }
            }
        }
    }

    private String getTextMessage(final String path, final String fileName) {
        StringBuilder builder = new StringBuilder();
        try {
            File file = new File(path + File.separator + MESSAGES_PATH
                    + File.separator + fileName + MESSAGE_FILE_EXTENSION);
            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                builder.append(sc.nextLine());
            }

        } catch (FileNotFoundException e) {
            LOG.error(e.getMessage(), e);
        }
        return builder.toString();
    }
}
