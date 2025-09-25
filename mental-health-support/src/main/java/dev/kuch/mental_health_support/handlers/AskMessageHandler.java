package dev.kuch.mental_health_support.handlers;

import dev.kuch.mental_health_support.handlers.interace.CommandHandler;
import dev.kuch.mental_health_support.model.SessionStorage;
import dev.kuch.mental_health_support.model.enums.BotState;
import dev.kuch.mental_health_support.service.ChatGptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class AskMessageHandler implements CommandHandler {

    private final ChatGptService chatGptService;

    private static final ConcurrentHashMap<Long, AtomicBoolean> busy = new ConcurrentHashMap<>();
    private static boolean acquireBusy(long chatId) {
        return busy.computeIfAbsent(chatId, k -> new AtomicBoolean(false)).compareAndSet(false, true);
    }
    private static void releaseBusy(long chatId) {
        busy.computeIfAbsent(chatId, k -> new AtomicBoolean(false)).set(false);
    }

    @Override
    public boolean supports(String command, BotState botState) {
        return botState == BotState.AWAITING_ASK || botState == BotState.AWAITING_BREATH;
    }

    @Override
    public void handle(Update update, AbsSender sender) {
        if (update.getMessage() == null) return;

        String chatId = String.valueOf(update.getMessage().getChatId());
        long id = Long.parseLong(chatId);
        String userText = update.getMessage().getText();

        if (userText == null || userText.isBlank()) {
            send(sender, chatId, "Будь ласка, надішли текстове повідомлення 📝", null);
            return;
        }

        String t = userText.trim();

        if ("Дихальні вправи".equalsIgnoreCase(t) || "/breath".equalsIgnoreCase(t)) {
            SessionStorage.getSession(id).setState(BotState.AWAITING_BREATH);
            send(sender, chatId, "🫁 Введи свій запит — надам доречну дихальну вправу:", update.getMessage().getMessageId());
            return;
        }
        if ("Сформувати запит".equalsIgnoreCase(t) || "/ask".equalsIgnoreCase(t)) {
            SessionStorage.getSession(id).setState(BotState.AWAITING_ASK);
            send(sender, chatId, "🧠 Розкажи, що відчуваєш — допоможу сформувати короткий запит для фахівця:", update.getMessage().getMessageId());
            return;
        }
        if (t.startsWith("/")) {
            SessionStorage.getSession(id).setState(BotState.NONE);
            send(sender, chatId, "⚠️ Режим скинуто. Обери дію з меню.", update.getMessage().getMessageId());
            return;
        }

        if (!acquireBusy(id)) {
            send(sender, chatId, "⏳ Дай подумаю, зачекай, будь ласка.", null);
            return;
        }

        try {
            send(sender, chatId, "⏳ Дай подумаю...", update.getMessage().getMessageId());

            BotState state = SessionStorage.getSession(id).getState();

            String answer = chatGptService.askChatGpt(chatId, t, state);

            send(sender, chatId, answer, update.getMessage().getMessageId());

            if (state == BotState.AWAITING_ASK) {
                SessionStorage.getSession(id).setState(BotState.AWAITING_ASK);
            } else if (state == BotState.AWAITING_BREATH) {
                SessionStorage.getSession(id).setState(BotState.AWAITING_BREATH);
            }
        } finally {
            releaseBusy(id);
        }
    }

    private void send(AbsSender sender, String chatId, String text, Integer replyTo) {
        SendMessage msg = SendMessage.builder()
                .chatId(chatId)
                .parseMode("Markdown")
                .text(text)
                .replyToMessageId(replyTo)
                .build();
        try { sender.execute(msg); } catch (TelegramApiException e) {
            System.err.println("Telegram send error: " + e.getMessage());
        }
    }
}
