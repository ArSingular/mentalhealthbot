package dev.kuch.mental_health_support.service;

import dev.kuch.mental_health_support.model.enums.BotState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static dev.kuch.mental_health_support.util.Constants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatGptService {

    @Value("${cohere.token}")
    private String apiKey;

    @Value("${cohere.model}")
    private String model;

    private final ChatSessionService sessionService;
    private final RestTemplate restTemplate = new RestTemplate();


    public String askChatGpt(String chatId, String userMessage, BotState botState) {

        try {
            sessionService.appendUserMessage(Long.valueOf(chatId), userMessage);

            JSONArray messages;

            if(botState.equals(BotState.AWAITING_ASK)) {
                messages = sessionService.buildMessagesArray(Long.valueOf(chatId), ASK_PROMPT);
            }else{
                messages = sessionService.buildMessagesArray(Long.valueOf(chatId), BREATH_PROMPT);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            JSONObject request = new JSONObject()
                    .put("model", model)
                    .put("messages", messages);

            HttpEntity<String> entity = new HttpEntity<>(request.toString(), headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    "https://api.cohere.com/v2/chat", // OpenRouter endpoint
                    HttpMethod.POST,
                    entity,
                    String.class
            );


            if (response.getStatusCode() != HttpStatus.OK) {
                log.error("Виникла помилка при отриманні відповіді");
                return "На жаль, сталася неочікувана помилка, Я вже працюю над її виправленням \uD83E\uDD72\uD83E\uDEF6. Спробуй, будь ласка, пізніше";
            }


            String responseBody = response.getBody();
            if (responseBody == null || !responseBody.trim().startsWith("{")) {
                log.error("Отримана некоректна відповідь від сервера ");
                return "На жаль, сталася неочікувана помилка, Я вже працюю над її виправленням \uD83E\uDD72\uD83E\uDEF6. Спробуй, будь ласка, пізніше";
            }

            JSONObject json = new JSONObject(responseBody);

            if (json.has("message")) {
                JSONArray content = json.getJSONObject("message").getJSONArray("content");
                String reply = content.getJSONObject(0).getString("text").trim();
                sessionService.appendAssistantMessage(Long.valueOf(chatId), reply);

                if (reply.contains("[CRISIS]")) {
                    reply = reply.replace("[CRISIS]", "").trim(); // прибираємо тег

                    reply += CRISIS_HELP;
                }

                return reply;
            } else {
                log.error("На жаль, відповідь від сервера не містить очікуваних даних.");
                return "На жаль, сталася неочікувана помилка, Я вже працюю над її виправленням \uD83E\uDD72\uD83E\uDEF6. Спробуй, будь ласка, пізніше";

            }
        } catch (Exception e) {
            log.error("Сталася неочікувана помилка: {}", e.getMessage());
            return "На жаль, сталася неочікувана помилка, Я вже працюю над її виправленням \uD83E\uDD72\uD83E\uDEF6. Спробуй, будь ласка, пізніше";

        }
    }

}
