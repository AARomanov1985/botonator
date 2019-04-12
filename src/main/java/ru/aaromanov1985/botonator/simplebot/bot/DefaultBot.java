package ru.aaromanov1985.botonator.simplebot.bot;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.aaromanov1985.botonator.simplebot.Answer;
import ru.aaromanov1985.botonator.simplebot.conversation.Conversation;
import ru.aaromanov1985.botonator.simplebot.node.Node;
import ru.aaromanov1985.botonator.simplebot.service.NodeService;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DefaultBot extends TelegramLongPollingBot {

    private Logger LOG = LoggerFactory.getLogger(DefaultBot.class);
    private final static String START = "/start";
    private final static String CANCEL = "/cancel";
    private final static String NAME = "sysadm623_bot";
    private final static String YES = "Да";
    private final static String NO = "Нет";
    private final static String I_DONT_KNOW = "Я ничего не понимаю";
    private final static String TOKEN = "727435438:AAFAWnJOCbasqYIYeRR--eaTCs_gYrVOumI";
    private boolean testMode = false;

    private Set<Conversation> conversations = new HashSet<>();

    private ExecutorService executorService = Executors.newCachedThreadPool();

    @Resource
    private NodeService nodeService;

    public void execute() {
        if (testMode) {
            testMode();
        } else {
            normalMode();
        }
    }

    private void normalMode() {
        LOG.info("normalMode enabled");
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(this);
        } catch (TelegramApiException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void testMode() {
        LOG.warn("testMode enabled. Start test");
        Message message = mock(Message.class);
        given(message.hasText()).willReturn(true);
        given(message.getText()).willReturn("/start");

        Update update = mock(Update.class);
        given(update.hasMessage()).willReturn(true);
        given(update.getMessage()).willReturn(message);

        onUpdateReceived(update);

        for (int i = 0; i < 10; i++) {
            given(message.hasText()).willReturn(true);
            given(message.getText()).willReturn(getRandomYesNo());

            given(update.hasMessage()).willReturn(true);
            given(update.getMessage()).willReturn(message);

            onUpdateReceived(update);
        }
        LOG.warn("testEnd");
    }

    private String getRandomYesNo() {
        Random random = new Random();
        boolean b = random.nextBoolean();
        return b ? YES : NO;
    }

    @Override
    public void onUpdateReceived(Update update) {
        LOG.info("request received");
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            validateConversations();
            // Set variables
            String message_text = update.getMessage().getText();
            LOG.info("message_has text {}", message_text);
            long chat_id = update.getMessage().getChatId();
            if (isStart(message_text, chat_id)) {
                startConversation(chat_id, message_text);
            } else if (isCancel(message_text)) {
                endConversation(chat_id);
            } else if (isIDontKnow(message_text)) {
                sendCommerce(chat_id);
            } else {
                continueConversation(chat_id, message_text);
            }
        }
    }

    private synchronized void startConversation(long chat_id, String message_text) {
        LOG.debug("Start conversation for chat_id" + chat_id);
        Conversation conversation = new Conversation(chat_id, nodeService);
        conversations.add(conversation);
        Answer answer = conversation.execute(message_text);
        sendMessage(answer);
    }

    private void endConversation(long chat_id) {
        LOG.debug("End conversation for chat_id" + chat_id);
        Conversation conversation = getConversation(chat_id);
        conversations.remove(conversation);
    }

    private void sendCommerce(long chat_id) {
        Conversation conversation = getConversation(chat_id);
        if (conversation != null) {
            Node endNode = nodeService.getEndNode();
            Answer answer = new Answer(String.valueOf(chat_id));
            answer.setMessage(endNode.getMessage());
            LOG.debug("Answer: {}", answer);
            sendMessage(answer);
        }
        endConversation(chat_id);
    }

    private synchronized void continueConversation(long chat_id, String message_text) {
        LOG.debug("Continue conversation for chat_id" + chat_id);
        Conversation conversation = getConversation(chat_id);
        if (conversation != null) {
            Answer answer = conversation.execute(message_text);
            LOG.debug("Answer: {}", answer);
            sendMessage(answer);
        }
    }

    private void sendMessage(Answer answer) {
        sendMessage(Long.valueOf(answer.getChatId()), answer.getMessage(), getKeyboard(answer));

        String nextNode = answer.getNextNode();

        if (StringUtils.isNotEmpty(nextNode)) {
            Node node = nodeService.findNodeForCode(nextNode);
            sendMessage(Long.valueOf(answer.getChatId()), node.getMessage(), getKeyboard(answer));
        }
    }

    private ReplyKeyboardMarkup getKeyboard(Answer answer) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        replyKeyboardMarkup.setKeyboard(getRowsForVariants(answer));
        return replyKeyboardMarkup;
    }

    private List<KeyboardRow> getRowsForVariants(Answer answer) {
        List<String> variants = answer.getVariants();
        List<KeyboardRow> rows = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(variants)) {
            for (String variant : variants) {
                KeyboardRow row = new KeyboardRow();
                row.add(variant);
                rows.add(row);
            }
            KeyboardRow row = new KeyboardRow();
            row.add(I_DONT_KNOW);
            rows.add(row);
        }
        return rows;
    }

    private void sendMessage(long chat_id, String text, ReplyKeyboardMarkup keyboard) {
        executorService.submit(new Runnable() {

            @Override
            public void run() {
                LOG.debug("Send message for chat " + chat_id);
                SendMessage message = new SendMessage() // Create a message object object
                        .setChatId(chat_id)
                        .setReplyMarkup(keyboard)
                        .setText(text);
                try {
                    execute(message); // Sending our message object to user
                } catch (TelegramApiException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        });
    }

    private void validateConversations() {
        for (Conversation conversation : conversations) {
            if (!conversation.isActual()) {
                LOG.info("Conversation with id {} will be removed", conversation.getId());
                conversations.remove(conversation);
            }
        }
    }

    private Conversation getConversation(long chatId) {
        for (Conversation conversation : conversations) {
            if (conversation.getId() == chatId) {
                return conversation;
            }
        }
        LOG.error("Conversation doesNotFound for chat " + chatId);
        return null;
    }

    private boolean isStart(String message, long chatId) {
        if (StringUtils.isNotEmpty(message)) {
            if (START.equals(message)) {
                return true;
            } else {
                return getConversation(chatId) == null;
            }
        }
        return false;
    }

    private boolean isCancel(String message) {
        return StringUtils.isNotEmpty(message) && CANCEL.equals(message);
    }

    private boolean isIDontKnow(String message) {
        return StringUtils.isNotEmpty(message) && I_DONT_KNOW.equals(message);
    }

    public void enableTestMode() {
        testMode = true;
    }

    public void disableTestMode() {
        testMode = false;
    }

    @Override
    public String getBotUsername() {
        return NAME;
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }
}
