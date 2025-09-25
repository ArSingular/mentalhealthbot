//package dev.kuch.mental_health_support.handlers;
//
//import dev.kuch.mental_health_support.handlers.interace.CommandHandler;
//import dev.kuch.mental_health_support.model.enums.BotState;
//import dev.kuch.mental_health_support.service.QuoteService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.objects.Update;
//import org.telegram.telegrambots.meta.bots.AbsSender;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//
//@Component
//@RequiredArgsConstructor
//public class QuoteCommandHandler implements CommandHandler {
//
//    private final QuoteService quoteService;
//
//
//    @Override
//    public boolean supports(String command, BotState botState) {
//        return command.equals("/quote") || command.equals("Щоденна цитата") && botState == BotState.NONE;
//    }
//
//    @Override
//    public void handle(Update update, AbsSender sender) {
//        String chatId = update.getMessage().getChatId().toString();
//        String quote = quoteService.getRandomQuote();
//
//        SendMessage message = SendMessage.builder()
//                .chatId(chatId)
//                .text(quote)
//                .build();
//
//        try{
//            sender.executeAsync(message);
//        }catch (TelegramApiException e){
//            e.printStackTrace();
//        }
//    }
//}
