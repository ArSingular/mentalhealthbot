//package dev.kuch.mental_health_support.handlers.callback;
//
//import dev.kuch.mental_health_support.handlers.interace.CommandHandler;
//import dev.kuch.mental_health_support.model.Mood;
//import dev.kuch.mental_health_support.model.enums.BotState;
//import dev.kuch.mental_health_support.repository.MoodRepository;
//import dev.kuch.mental_health_support.util.CalendarBuilder;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
//import org.telegram.telegrambots.meta.api.objects.Update;
//import org.telegram.telegrambots.meta.bots.AbsSender;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//
//import java.time.LocalDate;
//import java.util.Optional;
//
//import static org.telegram.telegrambots.meta.api.methods.ParseMode.MARKDOWN;
//
///**
// * @author Korol Artur
// * 19.06.2025
// */
//
//@Component
//@RequiredArgsConstructor
//public class CallBackCalendarHandler implements CommandHandler {
//
//    private final MoodRepository moodRepository;
//
//    @Override
//    public boolean supports(String command, BotState botState) {
//        return command.startsWith("calendar_");
//    }
//
//    @Override
//    public void handle(Update update, AbsSender sender) {
//        if (!update.hasCallbackQuery()) return;
//
//        var callback = update.getCallbackQuery();
//        var chatId = callback.getMessage().getChatId();
//        var messageId = callback.getMessage().getMessageId();
//        var data = callback.getData();
//
//        try {
//            if (data.startsWith("calendar_prev") || data.startsWith("calendar_next")) {
//                String[] parts = data.split("_");
//                int year = Integer.parseInt(parts[2]);
//                int month = Integer.parseInt(parts[3]);
//
//                editCalendar(chatId, messageId, LocalDate.of(year, month, 1), sender);
//                answerCallback(callback.getId(), sender);
//                return;
//            }
//
//            if (data.startsWith("calendar_")) {
//                String[] parts = data.split("_");
//                int year = Integer.parseInt(parts[1]);
//                int month = Integer.parseInt(parts[2]);
//                int day = Integer.parseInt(parts[3]);
//                LocalDate date = LocalDate.of(year, month, day);
//
//                Optional<Mood> moodOpt = moodRepository.findByChatIdAndDate(chatId.toString(), date);
//
//                String response = moodOpt.map(mood -> String.format(
//                        "📅 *%02d.%02d.%d*\n\n😊 Настрій: *%s*\n📝 Опис: _%s_",
//                        day, month, year,
//                        mood.getMood(),
//                        mood.getDescription() != null ? mood.getDescription() : "без опису"
//                )).orElse(String.format("Записів за %02d.%02d.%d не знайдено 😢", day, month, year));
//
//                SendMessage message = SendMessage.builder()
//                        .chatId(chatId)
//                        .text(response)
//                        .parseMode(MARKDOWN)
//                        .build();
//
//                executeSafely(sender, message);
//                answerCallback(callback.getId(), sender);
//            }
//
//        } catch (Exception e) {
//            SendMessage errorMsg = SendMessage.builder()
//                    .chatId(chatId)
//                    .text("⚠️ Виникла помилка при обробці дати.")
//                    .build();
//            executeSafely(sender, errorMsg);
//        }
//    }
//
//    private void editCalendar(Long chatId, Integer messageId, LocalDate date, AbsSender sender) {
//        var editMarkup = EditMessageReplyMarkup.builder()
//                .chatId(chatId.toString())
//                .messageId(messageId)
//                .replyMarkup(CalendarBuilder.buildCalendar(date.getYear(), date.getMonthValue()))
//                .build();
//
//        try {
//            sender.execute(editMarkup);
//        } catch (Exception e) {
//            System.err.println("❌ Помилка оновлення календаря: " + e.getMessage());
//        }
//    }
//
//    private void executeSafely(AbsSender sender, SendMessage message) {
//        try {
//            sender.execute(message);
//        } catch (Exception e) {
//            System.err.println("❌ Помилка надсилання повідомлення: " + e.getMessage());
//        }
//    }
//
//    private void answerCallback(String callbackId, AbsSender sender) {
//        var answer = new AnswerCallbackQuery();
//        answer.setCallbackQueryId(callbackId);
//        try {
//            sender.execute(answer);
//        } catch (TelegramApiException e) {
//            System.err.println("⚠️ Помилка при відправці AnswerCallbackQuery: " + e.getMessage());
//        }
//    }
//}
