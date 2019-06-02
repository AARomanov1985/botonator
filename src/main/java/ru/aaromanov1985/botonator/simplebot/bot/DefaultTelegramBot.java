package ru.aaromanov1985.botonator.simplebot.bot;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.aaromanov1985.botonator.simplebot.conversation.answer.Answer;
import ru.aaromanov1985.botonator.simplebot.conversation.Conversation;
import ru.aaromanov1985.botonator.simplebot.conversation.node.MessageType;
import ru.aaromanov1985.botonator.simplebot.conversation.node.converter.NodeToAnswerConverter;
import ru.aaromanov1985.botonator.simplebot.conversation.service.ConversationService;
import ru.aaromanov1985.botonator.simplebot.conversation.node.Node;
import ru.aaromanov1985.botonator.simplebot.conversation.node.service.NodeService;


public class DefaultTelegramBot extends TelegramLongPollingBot implements Bot {

    private final static Logger LOG = LoggerFactory.getLogger(DefaultTelegramBot.class);
    private final static String START = "/start";
    private final static String CANCEL = "/cancel";

    private String token;
    private String name;
    private String path;
    private final Set<Conversation> conversations = new HashSet<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @Resource
    private NodeService nodeService;
    @Resource
    private ConversationService conversationService;
    @Resource
    private NodeToAnswerConverter nodeToAnswerConverter;


    @Override
    public void execute() {
        nodeService.buildNodes();

        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(this);
        } catch (TelegramApiException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public void onUpdateReceived(final Update update) {
        nodeService.buildNodes();

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
            } else {
                continueConversation(chat_id, message_text);
            }
        }
    }

    private void validateConversations() {
        for (Conversation conversation : conversations) {
            if (!conversation.isActual()) {
                LOG.info("Conversation with id {} will be removed", conversation.getId());
                conversations.remove(conversation);
            }
        }
    }

    private synchronized void startConversation(long chat_id, final String message_text) {
        LOG.debug("Start conversation for chat_id" + chat_id);
        Conversation conversation = conversationService.buildConversation(chat_id);
        conversations.add(conversation);
        Answer answer = conversationService.executeForNode(message_text, conversation);
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
            nodeToAnswerConverter.convert(endNode, answer);
            LOG.debug("Answer: {}", answer);
            sendMessage(answer);
        }
        endConversation(chat_id);
    }

    private synchronized void continueConversation(long chat_id, String message_text) {
        LOG.debug("Continue conversation for chat_id" + chat_id);
        Conversation conversation = getConversation(chat_id);
        if (conversation != null) {
            Answer answer = conversationService.executeForNode(message_text, conversation);
            LOG.debug("Answer: {}", answer);
            sendMessage(answer);
        }
    }

    private void sendMessage(final Answer answer) {
        sendMessage(answer, getReplyKeyboard(answer));

        String nextNode = answer.getNextNode();

        if (StringUtils.isNotEmpty(nextNode)) {
            sendMessage(answer, getReplyKeyboard(answer));
        }
    }

    private InlineKeyboardMarkup getInlineKeyboard(final Answer answer) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        for (String variant : answer.getVariants()) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(variant);
            buttons.add(button);
        }
        inlineKeyboardMarkup.setKeyboard(Collections.singletonList(buttons));
        return inlineKeyboardMarkup;
    }

    private ReplyKeyboardMarkup getReplyKeyboard(final Answer answer) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        replyKeyboardMarkup.setKeyboard(getRowsForVariants(answer));
        return replyKeyboardMarkup;
    }

    private List<KeyboardRow> getRowsForVariants(final Answer answer) {
        List<String> variants = answer.getVariants();
        List<KeyboardRow> rows = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(variants)) {
            for (String variant : variants) {
                KeyboardRow row = new KeyboardRow();
                row.add(variant);
                rows.add(row);
            }
        }
        return rows;
    }

    private void sendMessage(final Answer answer, final ReplyKeyboard keyboard) {
        if (MessageType.TEXT.equals(answer.getMessageType())) {
            sendTextMessage(answer, keyboard);
        } else if (MessageType.IMAGE.equals(answer.getMessageType())) {
            sendImage(answer, keyboard);
        } else {
            LOG.error("Unknown message type {}", answer.getMessageType());
        }
    }

    private void sendTextMessage(final Answer answer, final ReplyKeyboard keyboard) {
        executorService.submit(() -> {
            LOG.debug("Send message {} for chat {}", answer.getMessage(), answer.getChatId());
            SendMessage message = new SendMessage() // Create a message object object
                .setChatId(answer.getChatId())
                .setParseMode(ParseMode.HTML)
                .setReplyMarkup(keyboard)
                .setText(answer.getMessage());
            try {
                execute(message); // Sending our message object to user
            } catch (TelegramApiException e) {
                LOG.error(e.getMessage(), e);
            }
        });
    }

    private void sendImage(final Answer answer, final ReplyKeyboard keyboard) {
        executorService.submit(() -> {
            LOG.debug("Send message {} for chat {}", answer.getMessage(), answer.getChatId());
            SendPhoto message = new SendPhoto() // Create a message object object
                .setChatId(answer.getChatId())
                .setPhoto(answer.getImage())
                .setParseMode(ParseMode.HTML)
                .setReplyMarkup(keyboard)
                .setCaption(answer.getMessage());
            try {
                execute(message); // Sending our message object to user
            } catch (TelegramApiException e) {
                LOG.error(e.getMessage(), e);
            }
        });
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

    private boolean isCancel(final String message) {
        return StringUtils.isNotEmpty(message) && CANCEL.equals(message);
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
