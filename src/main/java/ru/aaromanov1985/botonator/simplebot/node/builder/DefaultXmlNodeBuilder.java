package ru.aaromanov1985.botonator.simplebot.node.builder;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aaromanov1985.botonator.simplebot.conversation.Message;
import ru.aaromanov1985.botonator.simplebot.node.Node;
import ru.aaromanov1985.botonator.simplebot.node.Nodes;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DefaultXmlNodeBuilder implements NodeBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultXmlNodeBuilder.class);

    public Nodes buildNodes(String path) {
        LOG.info("path = {}", path);
        try {
            JAXBContext context = JAXBContext.newInstance(Nodes.class);

            FileInputStream inputStream = new FileInputStream(path + "/start.xml");

            Unmarshaller unmarshaller =
                    context.createUnmarshaller();
            Nodes nodes = (Nodes) unmarshaller.unmarshal(inputStream);
            updateMessages(nodes, path);
            return nodes;
        } catch (JAXBException exception) {
            exception.printStackTrace();
        } catch (FileNotFoundException e) {
            LOG.error(e.getMessage(), e);
        }
        LOG.error("Object nodes is null");

        return null;
    }

    protected void updateMessages(Nodes nodes, String path) {
        if (nodes!=null && CollectionUtils.isNotEmpty(nodes.getNodes())) {
            for (Node node : nodes.getNodes()) {
                List<Message> messages = node.getMessages();
                if (CollectionUtils.isNotEmpty(messages)) {
                    messages.forEach(m -> updateMessage(path, m));
                }
            }
        }
    }

    private void updateMessage(final String path, final Message message) {
        StringBuilder builder = new StringBuilder();
        try {
            File file = new File(path + "/src/" + message.getValue() + ".html");
            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                builder.append(sc.nextLine());
            }

        } catch (FileNotFoundException e) {
            LOG.error(e.getMessage(), e);
        }
        message.setValue(builder.toString());
    }
}
