package dev.kuch.mental_health_support.handlers;

import dev.kuch.mental_health_support.handlers.interace.CommandHandler;
import dev.kuch.mental_health_support.model.enums.BotState;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class StartCommandHandler implements CommandHandler {

    @Override
    public boolean supports(String command, BotState botState) {
        return command.equals("/start") && botState == BotState.NONE;
    }

    @Override
    public void handle(Update update, AbsSender sender) {
        String chatId =  update.getMessage().getChatId().toString();

        String response = """
                Привіт,цей бот покликаний допомогти тобі краще зрозуміти свої емоції
                і навчити самозарадності 🫶
                Я можу:
                🧠 Розказати просто про складне
                🖊️ Сформувати твій запит
                """;

        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(response)
                .replyMarkup(buildStartKeyboard())
                .build();

        try{
            sender.executeAsync(message);
        }catch (TelegramApiException e){
            e.printStackTrace();
        }


    }

    private ReplyKeyboardMarkup buildStartKeyboard() {
        var row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Просто про складне"));
        row1.add(new KeyboardButton("Дихальні вправи"));

        var row2 = new KeyboardRow();
        row2.add(new KeyboardButton("Сформувати запит"));

        return ReplyKeyboardMarkup.builder()
                .resizeKeyboard(true)
                .keyboard(List.of(row1, row2))
                .build();
    }

}
