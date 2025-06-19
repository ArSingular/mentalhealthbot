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

import java.util.Arrays;

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
                Привіт! 🤗 Я — бот підтримки ментального здоров'я.
                Я можу:
                🧠 Надсилати щоденні поради
                🌬️ Надавати дихальні вправи
                🗒️ Відслідковувати настрій
                🖊️ Обговорити твій запит
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
        ReplyKeyboardMarkup startKeyboard = new ReplyKeyboardMarkup();
        startKeyboard.setResizeKeyboard(true);

        KeyboardRow row1 = new KeyboardRow();


        row1.add(new KeyboardButton("Щоденна цитата"));
        row1.add(new KeyboardButton("Дихальні вправи"));
        

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("Записати настрій"));
        row2.add(new KeyboardButton("Обговорити запит"));
        row2.add(new KeyboardButton("Щоденник настрою"));

        startKeyboard.setKeyboard(Arrays.asList(row1, row2));
        return startKeyboard;
    }
}
