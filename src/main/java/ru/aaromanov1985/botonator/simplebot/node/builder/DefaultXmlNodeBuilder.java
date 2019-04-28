package ru.aaromanov1985.botonator.simplebot.node.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aaromanov1985.botonator.simplebot.node.Nodes;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;

public class DefaultXmlNodeBuilder implements NodeBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultXmlNodeBuilder.class);
    private String path;

    public Nodes buildNodes() {
        LOG.info("path = {}", path);
        try {
            JAXBContext context = JAXBContext.newInstance(Nodes.class);

            FileInputStream inputStream = new FileInputStream(path);

            Unmarshaller unmarshaller =
                    context.createUnmarshaller();
            Nodes nodes = (Nodes) unmarshaller.unmarshal(inputStream);
            return nodes;
        } catch (JAXBException exception) {
            exception.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        LOG.error("Object nodes is null");

        return null;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
