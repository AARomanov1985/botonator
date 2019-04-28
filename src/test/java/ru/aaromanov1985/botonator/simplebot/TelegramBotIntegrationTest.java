package ru.aaromanov1985.botonator.simplebot;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.aaromanov1985.botonator.simplebot.bot.DefaultTelegramBot;

import java.util.Random;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class TelegramBotIntegrationTest {

    private DefaultTelegramBot bot;

    @Before
    public void setUp(){
        ApiContextInitializer.init();
        ApplicationContext context = new ClassPathXmlApplicationContext(
                "SpringBeans.xml");
        bot = context.getBean("bot", DefaultTelegramBot.class);
    }

    @Test
    public void test(){
        Message message = mock(Message.class);
        given(message.hasText()).willReturn(true);
        given(message.getText()).willReturn("/start");

        Update update = mock(Update.class);
        given(update.hasMessage()).willReturn(true);
        given(update.getMessage()).willReturn(message);

        bot.onUpdateReceived(update);

        for (int i = 0; i < 10; i++) {
            given(message.hasText()).willReturn(true);
            given(message.getText()).willReturn(getRandomYesNo());

            given(update.hasMessage()).willReturn(true);
            given(update.getMessage()).willReturn(message);

            bot.onUpdateReceived(update);
        }
    }

    private String getRandomYesNo() {
        Random random = new Random();
        boolean b = random.nextBoolean();
        return b ? "Да" : "Нет";
    }
}
