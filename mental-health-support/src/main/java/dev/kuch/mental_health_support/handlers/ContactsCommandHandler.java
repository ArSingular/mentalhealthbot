package dev.kuch.mental_health_support.handlers;

import dev.kuch.mental_health_support.handlers.interace.CommandHandler;
import dev.kuch.mental_health_support.model.enums.BotState;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
public class ContactsCommandHandler implements CommandHandler {


    @Override
    public boolean supports(String command, BotState botState) {
        return command.equals("/contacts") || command.equals("Контакти") && botState == BotState.NONE;
    }

    @Override
    public void handle(Update update, AbsSender sender) {

    }
}
