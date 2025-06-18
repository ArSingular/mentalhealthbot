package dev.kuch.mental_health_support.handlers;

import dev.kuch.mental_health_support.model.BotState;
import dev.kuch.mental_health_support.model.SessionStorage;
import dev.kuch.mental_health_support.model.UserSession;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class CallBackMoodHandler implements CommandHandler{

    @Override
    public boolean supports(String command, BotState botState) {
        return command.startsWith("mood_");
    }

    @Override
    public void handle(Update update, AbsSender sender) {

        if (!update.hasCallbackQuery()) {
            System.out.println("ERORA");
            return;
        }

        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
        String callbackData = update.getCallbackQuery().getData();

        String moodText = switch (callbackData) {
            case "mood_good" -> "Хороший 😃";
            case "mood_normal" -> "Нормальний 🙂";
            case "mood_bad" -> "Поганий 😔";
            case "mood_neutral" -> "Нейтральний 😐";
            default -> "Невідомий";
        };

        UserSession session = SessionStorage.getSession(Long.valueOf(chatId));
        session.setState(BotState.AWAITING_NOTE);
        session.getTempData().put("mood", moodText);


        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("📝 Введіть нотатку до вашого настрою:");
        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        // Відповідь Telegram, щоб прибрати "loading"
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(update.getCallbackQuery().getId());
        answer.setText("Настрій обрано: " + moodText);
        try {
            sender.execute(answer);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
