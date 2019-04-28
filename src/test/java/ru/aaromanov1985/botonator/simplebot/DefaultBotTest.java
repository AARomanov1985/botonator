package ru.aaromanov1985.botonator.simplebot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.aaromanov1985.botonator.simplebot.bot.DefaultBot;
import ru.aaromanov1985.botonator.simplebot.node.service.NodeService;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;

class DefaultBotTest {

    @InjectMocks
    private DefaultBot instance;

    @Mock
    private NodeService nodeService;

    @BeforeEach
    void setUp() {
        initMocks(this);

    }

    @Test
    void onUpdateReceived() {
        Message message = mock(Message.class);
        given(message.hasText()).willReturn(true);
        given(message.getText()).willReturn("yes");

        Update update = mock(Update.class);
        given(update.hasMessage()).willReturn(true);
        given(update.getMessage()).willReturn(message);

        instance.onUpdateReceived(update);
    }

    @Test
    void testConversation(){
        Message message = mock(Message.class);
        given(message.hasText()).willReturn(true);
        given(message.getText()).willReturn("/start");

        Update update = mock(Update.class);
        given(update.hasMessage()).willReturn(true);
        given(update.getMessage()).willReturn(message);

        instance.onUpdateReceived(update);
    }
}