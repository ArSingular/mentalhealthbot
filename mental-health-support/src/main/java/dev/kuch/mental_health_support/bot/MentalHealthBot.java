package dev.kuch.mental_health_support.bot;


import dev.kuch.mental_health_support.dispatcher.CommandDispatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class MentalHealthBot extends TelegramLongPollingBot {

    @Value("${bot.username}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;

    private final CommandDispatcher dispatcher;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public MentalHealthBot(CommandDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void onUpdateReceived(Update update) {

        executorService.submit(() -> {
            try {
                dispatcher.dispatch(update, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

}
