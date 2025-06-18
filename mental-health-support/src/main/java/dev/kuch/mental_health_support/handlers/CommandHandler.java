package dev.kuch.mental_health_support.handlers;

import dev.kuch.mental_health_support.model.BotState;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

public interface CommandHandler {

    boolean supports(String command, BotState botState);
    void handle(Update update, AbsSender sender);

}
