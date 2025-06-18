package dev.kuch.mental_health_support.handlers;

import dev.kuch.mental_health_support.model.BotState;
import dev.kuch.mental_health_support.model.SessionStorage;
import dev.kuch.mental_health_support.model.UserSession;
import dev.kuch.mental_health_support.service.MoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.format.DateTimeFormatter;

/**
 * @author Artur Kuch
 */
@Component
public class NoteCommandHandler implements CommandHandler {

    private final MoodService moodService;


    public NoteCommandHandler(MoodService moodService) {
        this.moodService = moodService;
    }

    @Override
    public boolean supports(String command, BotState botState) {
        return botState == BotState.AWAITING_NOTE;
    }

    @Override
    public void handle(Update update, AbsSender sender) {
        Message message = update.getMessage();
        String chatId = message.getChatId().toString();
        String text = message.getText();

        UserSession session = SessionStorage.getSession(Long.valueOf(chatId));
        BotState state = session.getState();

        if (state == BotState.AWAITING_NOTE) {
            String mood = session.getTempData().get("mood");
            moodService.saveMood(chatId, mood, text);

            SendMessage reply = new SendMessage();
            reply.setChatId(chatId);
            reply.setText(String.format(
                    "✅ Ваш настрій збережено!\n\n🧠 Настрій: %s\n📝 Нотатка: %s\n📅 Дата: %s",
                    mood, text, java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            ));

            try {
                sender.executeAsync(reply);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

            session.setState(BotState.NONE);
            session.getTempData().remove("mood");
        }
    }
}
