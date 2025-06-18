package dev.kuch.mental_health_support.handlers;

import dev.kuch.mental_health_support.model.BotState;
import dev.kuch.mental_health_support.model.SessionStorage;
import dev.kuch.mental_health_support.model.UserSession;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

@Component
public class MoodCommandHandler implements CommandHandler{


    @Override
    public boolean supports(String command, BotState botState) {
       return command.equals("/mood") || command.equalsIgnoreCase("Записати настрій");
    }

    @Override
    public void handle(Update update, AbsSender sender) {
        String chatId = update.getMessage().getChatId().toString();
        UserSession session = SessionStorage.getSession(Long.valueOf(chatId));
        session.setState(BotState.AWAITING_MOOD);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("👋 Оберіть свій настрій:");
        sendMessage.setReplyMarkup(getInlineMoodKeyboard());

        try {
            sender.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    private InlineKeyboardMarkup getInlineMoodKeyboard(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();


        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Хороший 😃");
        inlineKeyboardButton1.setCallbackData("mood_good");
        inlineKeyboardButton2.setText("Нормальний \uD83D\uDE42");
        inlineKeyboardButton2.setCallbackData("mood_normal");
        inlineKeyboardButton3.setText("Поганий 😔");
        inlineKeyboardButton3.setCallbackData("mood_bad");
        inlineKeyboardButton4.setText("Нейтральний 😐");
        inlineKeyboardButton4.setCallbackData("mood_neutral");


        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow1.add(inlineKeyboardButton2);
        keyboardButtonsRow2.add(inlineKeyboardButton3);
        keyboardButtonsRow2.add(inlineKeyboardButton4);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }
}