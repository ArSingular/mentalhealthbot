package dev.kuch.mental_health_support.handlers.interace;

import dev.kuch.mental_health_support.model.enums.BotState;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

public interface CommandHandler {

    boolean supports(String command, BotState botState);
    void handle(Update update, AbsSender sender);

}
