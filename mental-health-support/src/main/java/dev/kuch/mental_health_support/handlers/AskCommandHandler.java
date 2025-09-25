package dev.kuch.mental_health_support.handlers;

import dev.kuch.mental_health_support.handlers.interace.CommandHandler;
import dev.kuch.mental_health_support.model.SessionStorage;
import dev.kuch.mental_health_support.model.enums.BotState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class AskCommandHandler implements CommandHandler {

    @Override
    public boolean supports(String command, BotState botState) {
        return "/ask".equalsIgnoreCase(command) || "Сформувати запит".equalsIgnoreCase(command);
    }

    @Override
    public void handle(Update update, AbsSender sender) {
        if (update.getMessage() == null) return;

        String chatId = String.valueOf(update.getMessage().getChatId());

        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .parseMode("Markdown")
                .text("🧠 Розкажи, що відчуваєш — допоможу сформувати короткий запит для фахівця:")
                .replyToMessageId(update.getMessage().getMessageId())
                .build();

        try { sender.execute(message); } catch (TelegramApiException e) {
            System.err.println("Telegram send error: " + e.getMessage());
        }

        SessionStorage.getSession(Long.valueOf(chatId)).setState(BotState.AWAITING_ASK);
    }
}
