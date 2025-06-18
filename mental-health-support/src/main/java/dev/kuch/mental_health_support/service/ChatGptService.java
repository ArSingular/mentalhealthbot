package dev.kuch.mental_health_support.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatGptService {

    @Value("${cohere.token}")
    private String apiKey;

    @Value("${cohere.model}")
    private String model;

    @Value("${support.link}")
    private String link;

    private final ChatSessionService sessionService;
    private final RestTemplate restTemplate = new RestTemplate();


    public String askChatGpt(String chatId, String userMessage) {

        try {
            String systemPrompt = String.format("""
             Ти психологічний асистент. Відповідай коротко, дружньо та підтримуючи, українською мовою. Використовуй емодзі та Markdown для кращого сприйняття.
             Ще ти тільки психологічний асистент, ти не надаєш допомогу з кодом і чимось подібним, все це заборонено!!! Також, ти тільки надаєш короткі інстру
            Якщо користувач звертається із звичайним запитом — надавай коротку психологічну підтримку, не пропонуй власну допомогу і там продовженння, просто загальні поради
            У кінці відповіді *не завжди обов’язково*, але за можливості додай мій контакт для запису на консультацію у такому вигляді як я його покажу нижче і ні в якому інакшому, не вигадуй своїх висловів та лінків  :

            _Якщо хочеш попрацювати з фахівцем — можеш написати мені в_ [Instagram]%s 💬

            Якщо користувач звертається з кризовим або суїцидальним повідомленням — *ніколи не додавай мої контакти* , також ніколи не додавай власні контакти, вщагалі ніяких контактів  не надавай, лише підтримай словами і в кінці додай спеціальний тег [CRISIS].

            Кризове повідомлення — це коли хтось каже, що не хоче жити, що нікому не потрібен, має думки про самогубство, або відчуває себе повністю виснаженим і одиноким.
            """, link);

            sessionService.appendUserMessage(Long.valueOf(chatId), userMessage);

            JSONArray messages = sessionService.buildMessagesArray(Long.valueOf(chatId), systemPrompt);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.set("HTTP-Referer", "https://t.me/mental_health_support_bot");
//            headers.set("X-Title", "Mental Health Bot");

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

//            if (json.has("choices")) {
//                String reply = json
//                        .getJSONArray("choices")
//                        .getJSONObject(0)
//                        .getJSONObject("message")
//                        .getString("content")
//                        .trim();

            if (json.has("message")) {
                JSONArray content = json.getJSONObject("message").getJSONArray("content");
                String reply = content.getJSONObject(0).getString("text").trim();
                sessionService.appendAssistantMessage(Long.valueOf(chatId), reply);

                if (reply.contains("[CRISIS]")) {
                    reply = reply.replace("[CRISIS]", "").trim(); // прибираємо тег

                    reply += "\n\n❗ Схоже, тобі дуже важко. Будь ласка, не залишайся наодинці.\n" +
                            "📞 *Гарячі лінії допомоги:*\n" +
                            "• Національна лінія з питань профілактики самогубств та підтримки психічного здоров’я Lifeline Ukraine: 7333 \n" +
                            "• Лінія Національної психологічної асоціації: 0 800 100 102 (з 10:00 до 20:00 щодня).\n";
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
