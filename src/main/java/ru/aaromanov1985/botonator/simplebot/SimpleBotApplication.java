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

        if (isTestMode(args)){
            bot.enableTestMode();
        }

        bot.execute();
    }

    private static boolean isTestMode(String[] args) {
        if (args != null && args.length > 0) {
			return TEST_MODE.equals(args[0]);
        }
        return false;
    }
}
