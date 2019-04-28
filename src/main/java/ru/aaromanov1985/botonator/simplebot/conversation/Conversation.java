package ru.aaromanov1985.botonator.simplebot.conversation;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aaromanov1985.botonator.simplebot.conversation.answer.Answer;
import ru.aaromanov1985.botonator.simplebot.node.Node;
import ru.aaromanov1985.botonator.simplebot.node.Variant;
import ru.aaromanov1985.botonator.simplebot.node.service.NodeService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Conversation {

    private Logger LOG = LoggerFactory.getLogger(Conversation.class);

    private final static String INCORRECT_REQUEST = "Неверный запрос";
    private long id;
    private long lastRequest;
    private Node currentNode;
    private boolean isActive;
    private boolean isFirstRequest;
    // 30 min
    private static final long TIMEOUT = 1800000;

    private NodeService nodeService;

    public Conversation(long id, NodeService nodeService) {
        LOG.debug("Create conversation");
        this.id = id;
        this.nodeService = nodeService;
        init();

        LOG.debug("id= {}", id);
        LOG.debug("currentNode= {}", currentNode.getCode());
        LOG.debug("isActive= {}", isActive);
        LOG.debug("nodeService= {}", nodeService);
    }

    private void init(){
        currentNode = nodeService.getStartNode();
        isActive = true;
        isFirstRequest = true;
    }

    public Answer execute(String message) {
        LOG.debug("execute conversation for message {}", message);
        lastRequest = System.currentTimeMillis();

        Node node = getAnswer(message);
        Answer answer = new Answer(String.valueOf(id));
        answer.setMessage(node.getMessage());
        answer.setNextNode(currentNode.getNextNode());
        answer.setVariants(convertVariants(node.getVariants()));
        return answer;
    }
    
    private List<String> convertVariants(List<Variant> variants){
        List<String> variantsOfAnswers = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(variants)){
            for (Variant variant : variants){
                variantsOfAnswers.add(variant.getValue());
            }
        }
        return variantsOfAnswers;
    }

    private Node getAnswer(String request) {

        if (nodeService.isEndNode(currentNode) || nodeService.isErrorNode(currentNode)){
            init();
        }

        if (isFirstRequest) {
            isFirstRequest = false;
            return currentNode;
        }

        Node node = nodeService.getAnswer(currentNode, request);

        if (nodeService.isErrorNode(node)) {
            LOG.error("It's error node!");
            Node errNode = nodeService.getErrorNode();
            errNode.setMessage(INCORRECT_REQUEST);
            return errNode;
        }

        updateCurrentNode(node);
        return node;
    }

    private void updateCurrentNode(Node node) {
        if (node != null) {
            currentNode = node;
            LOG.info("current node changed to " + node.getCode());
        }
    }

    public boolean isActual() {
        long time = System.currentTimeMillis() - lastRequest;
        return time < TIMEOUT && isActive;
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conversation that = (Conversation) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}