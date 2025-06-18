package dev.kuch.mental_health_support.handlers;

import dev.kuch.mental_health_support.handlers.interace.CommandHandler;
import dev.kuch.mental_health_support.model.enums.BotState;
import dev.kuch.mental_health_support.model.SessionStorage;
import dev.kuch.mental_health_support.service.ChatGptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class AskCommandHandler implements CommandHandler {

    private final ChatGptService chatGptService;

    @Override
    public boolean supports(String command, BotState botState) {
        return command.equals("/ask") || command.equalsIgnoreCase("Обговорити запит");
    }

    @Override
    public void handle(Update update, AbsSender sender) {
        String chatId = update.getMessage().getChatId().toString();

        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .parseMode("Markdown")
                .text("\uD83E\uDDE0 Введи свій запит, і я спробую допомогти: ")
                .replyToMessageId(update.getMessage().getMessageId())
                .build();

        try{
            sender.executeAsync(message);
        }catch (TelegramApiException e){
            e.printStackTrace();
        }

        SessionStorage.getSession(Long.valueOf(chatId)).setState(BotState.AWAITING_ASK);
    }
}
