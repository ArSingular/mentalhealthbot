package dev.kuch.mental_health_support.dispatcher;

import dev.kuch.mental_health_support.handlers.interace.CommandHandler;
import dev.kuch.mental_health_support.model.enums.BotState;
import dev.kuch.mental_health_support.model.SessionStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommandDispatcher {

    private final List<CommandHandler> handlers;


    public void dispatch(Update update, AbsSender sender){

        if (update.hasMessage() && update.getMessage().hasText()) {
            BotState state = SessionStorage.getSession(update.getMessage().getChatId()).getState();
            String command = update.getMessage().getText();
            handlers.stream()
                    .filter(h -> h.supports(command, state))
                    .findFirst()
                    .ifPresent(h -> h.handle(update, sender));
        }else if(update.hasCallbackQuery()){
            String data = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            BotState state = SessionStorage.getSession(chatId).getState();

            handlers.stream()
                    .filter(h -> h.supports(data, state))
                    .findFirst()
                    .ifPresent(h -> h.handle(update, sender));
        }
    }

}
