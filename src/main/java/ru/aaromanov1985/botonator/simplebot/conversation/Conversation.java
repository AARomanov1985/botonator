package ru.aaromanov1985.botonator.simplebot.conversation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aaromanov1985.botonator.simplebot.conversation.answer.Answer;
import ru.aaromanov1985.botonator.simplebot.conversation.service.ConversationService;
import ru.aaromanov1985.botonator.simplebot.node.Node;
import ru.aaromanov1985.botonator.simplebot.node.service.NodeService;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Objects;

public class Conversation {

    private Logger LOG = LoggerFactory.getLogger(Conversation.class);

    private long id;
    private long lastRequest;
    private Node currentNode;
    private boolean isActive;
    private boolean isFirstRequest;
    // 30 min
    private long timeout = 1800000;

    public boolean isActual() {
        long time = System.currentTimeMillis() - lastRequest;
        return time < timeout && isActive;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getLastRequest() {
        return lastRequest;
    }

    public void setLastRequest(long lastRequest) {
        this.lastRequest = lastRequest;
    }

    public Node getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(Node currentNode) {
        this.currentNode = currentNode;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isFirstRequest() {
        return isFirstRequest;
    }

    public void setFirstRequest(boolean firstRequest) {
        isFirstRequest = firstRequest;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
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