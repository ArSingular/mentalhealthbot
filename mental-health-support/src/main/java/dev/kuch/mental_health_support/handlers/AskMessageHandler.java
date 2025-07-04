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

import java.util.List;

@Component
@RequiredArgsConstructor
public class AskMessageHandler implements CommandHandler {

    private final ChatGptService chatGptService;

    private static final List<String> RESERVED_COMMANDS = List.of("/start", "/quote", "/mood", "/ask","/contacts", "Записати настрій",
            "Обговорити запит", "Щоденна порада", "Контакти");


    @Override
    public boolean supports(String command, BotState botState) {
        return botState == BotState.AWAITING_ASK || botState == BotState.AWAITING_BREATH;
    }

    @Override
    public void handle(Update update, AbsSender sender) {
        String chatId = update.getMessage().getChatId().toString();
        String userText = update.getMessage().getText();


        if (RESERVED_COMMANDS.stream().anyMatch(userText::startsWith)) {
            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .parseMode("Markdown")
                    .text("⚠️ Ти зараз в режимі запиту. Щоб вийти з нього, просто надішли будь-яку іншу команду ще раз.\n\n*Підказка:* Напиши текстовий запит, а не команду.")
                    .replyToMessageId(update.getMessage().getMessageId())
                    .build();

            SessionStorage.getSession(Long.valueOf(chatId)).setState(BotState.NONE);

            try{
                sender.executeAsync(message);
            }catch (TelegramApiException e){
                e.printStackTrace();
            }

            return;
        }


        String answer = chatGptService.askChatGpt(chatId, userText, SessionStorage.getSession(Long.valueOf(chatId)).getState());

        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .parseMode("Markdown")
                .text(answer)
                .replyToMessageId(update.getMessage().getMessageId())
                .build();

        try{
            sender.executeAsync(message);
        }catch (TelegramApiException e){
            e.printStackTrace();
        }

        SessionStorage.getSession(Long.valueOf(chatId)).setState(BotState.NONE);
    }
}
