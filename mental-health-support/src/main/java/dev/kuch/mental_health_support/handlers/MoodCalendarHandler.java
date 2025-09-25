//package dev.kuch.mental_health_support.handlers;
//
//import dev.kuch.mental_health_support.handlers.interace.CommandHandler;
//import dev.kuch.mental_health_support.model.enums.BotState;
//import dev.kuch.mental_health_support.util.CalendarBuilder;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.objects.Update;
//import org.telegram.telegrambots.meta.bots.AbsSender;
//
//import java.time.LocalDate;
//
//
///**
// * @author Korol Artur
// * 19.06.2025
// */
//
//@Component
//@RequiredArgsConstructor
//public class MoodCalendarHandler implements CommandHandler {
//
//    @Override
//    public boolean supports(String command, BotState botState) {
//        return command.equals("/diary") || command.equalsIgnoreCase("Щоденник настрою");
//    }
//
//    @Override
//    public void handle(Update update, AbsSender sender) {
//        if (update.hasMessage() && update.getMessage().hasText()) {
//            sendCalendar(update.getMessage().getChatId(), LocalDate.now(), sender);
//        }
//
//    }
//
//    private void sendCalendar(Long chatId, LocalDate date, AbsSender sender) {
//        SendMessage message = SendMessage.builder()
//                .chatId(chatId.toString())
//                .text("Оберіть дату 🗓️")
//                .replyMarkup(CalendarBuilder.buildCalendar(date.getYear(), date.getMonthValue()))
//                .build();
//
//        try {
//            sender.execute(message);
//        } catch (Exception e) {
//            System.err.println("❌ Помилка надсилання повідомлення: " + e.getMessage());
//        }
//    }
//
//}
