package ru.aaromanov1985.botonator.simplebot;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.telegram.telegrambots.ApiContextInitializer;
import ru.aaromanov1985.botonator.simplebot.bot.DefaultTelegramBot;

public class SimpleBotApplication {

    private static final String TEST_MODE = "test";

    public static void main(String[] args) {
        ApiContextInitializer.init();
        ApplicationContext context = new ClassPathXmlApplicationContext(
                "SpringBeans.xml");

        DefaultTelegramBot bot = context.getBean("bot", DefaultTelegramBot.class);
        bot.execute();
    }
}
