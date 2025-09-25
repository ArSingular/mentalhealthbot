package dev.kuch.mental_health_support.service;

import dev.kuch.mental_health_support.model.enums.BotState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
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

    private final RestTemplate restTemplate = buildRest();

    private RestTemplate buildRest() {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10_000);
        factory.setReadTimeout(25_000);
        return new RestTemplate(factory);
    }

    public String askChatGpt(String chatId, String userMessage, BotState botState) {
        try {
            long sid = Long.parseLong(chatId);
            sessionService.appendUserMessage(sid, userMessage);

            JSONArray messages = new JSONArray()
                    .put(new JSONObject().put("role", "system").put("content", SYSTEM_PROMPT))
                    .put(new JSONObject().put("role", "system").put("content",
                            botState == BotState.AWAITING_ASK ? ASK_ASSIST_PROMPT : BREATH_PROMPT));

            JSONArray history = sessionService.buildMessagesArrayOnlyHistory(sid);
            for (int i = 0; i < history.length(); i++) messages.put(history.get(i));

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            String reply = doChatAndMaybeContinue(messages, headers);
            if (reply == null || reply.isBlank()) return failMsg();


            if (reply.contains("[CRISIS]")) {
                reply = reply.replace("[CRISIS]", "").trim();
                reply = reply + CRISIS_HELP;
            }


            sessionService.appendAssistantMessage(sid, reply);
            return reply;

        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            return failMsg();
        }
    }

    private String doChatAndMaybeContinue(JSONArray messages, HttpHeaders headers) {
        String first = callCohere(messages, headers, 700);
        if (first == null) return null;

        boolean looksCut = first.matches("(?s).*\\b(Ресурси|Кроки самодопомоги|Запит до фахівця)\\s*:?\\s*$")
                || first.endsWith("-") || first.endsWith(":");
        String finish = lastFinishReason;

        if ("max_tokens".equalsIgnoreCase(finish) || looksCut) {
            messages.put(new JSONObject().put("role","assistant").put("content", first));
            messages.put(new JSONObject().put("role","user").put("content",
                    "Продовж відповідь коротко і заверши останній блок; не повторюй попередній текст."));
            String tail = callCohere(messages, headers, 220);
            if (tail != null && !tail.isBlank()) {
                return first + (first.endsWith("\n") ? "" : "\n") + tail.trim();
            }
        }
        return first;
    }

    private String lastFinishReason = null;

    private String callCohere(JSONArray messages, HttpHeaders headers, int maxTokens) {
        JSONObject request = new JSONObject()
                .put("model", model)
                .put("messages", messages)
                .put("temperature", 0.2)
                .put("max_tokens", maxTokens)
                .put("safety_mode", "strict")
                .put("response_format", new JSONObject().put("type", "text"));

        HttpEntity<String> entity = new HttpEntity<>(request.toString(), headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://api.cohere.com/v2/chat", HttpMethod.POST, entity, String.class
            );
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.warn("Cohere status {}", response.getStatusCode());
                return null;
            }
            String body = response.getBody();
            if (body == null || body.isBlank() || !body.trim().startsWith("{")) return null;

            JSONObject json = new JSONObject(body);
            try { lastFinishReason = json.getJSONObject("message").optString("finish_reason", null); }
            catch (Exception ignore) { lastFinishReason = null; }

            return extractAllText(json);

        } catch (HttpStatusCodeException ex) {
            log.warn("Cohere HTTP {}", ex.getStatusCode().value());
            return null;
        } catch (Exception e) {
            log.warn("Cohere error: {}", e.toString());
            return null;
        }
    }

    private String extractAllText(JSONObject json) {
        if (!json.has("message")) return null;
        JSONArray content = json.getJSONObject("message").optJSONArray("content");
        if (content == null || content.isEmpty()) return null;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < content.length(); i++) {
            JSONObject part = content.optJSONObject(i);
            if (part == null) continue;
            if ("text".equalsIgnoreCase(part.optString("type","text"))) {
                String t = part.optString("text","");
                if (t != null && !t.isBlank()) {
                    if (!sb.isEmpty()) sb.append("\n");
                    sb.append(t);
                }
            }
        }
        String out = sb.toString().trim();
        return out.isEmpty() ? null : out;
    }


    private String failMsg() {
        return "На жаль, сталася неочікувана помилка, я вже працюю над цим. Спробуй, будь ласка, пізніше 🙏";
    }
}