package dev.kuch.mental_health_support.service;

import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatSessionService {

    private final Map<Long, List<Map<String, String>>> sessionMap = new HashMap<>();

    public List<Map<String, String>> getSessionMessages(Long chatId) {
        return sessionMap.getOrDefault(chatId, new ArrayList<>());
    }

    public void appendUserMessage(Long chatId, String content) {
        sessionMap.putIfAbsent(chatId, new ArrayList<>());
        sessionMap.get(chatId).add(Map.of("role", "user", "content", content));
        trimHistory(chatId);
    }

    public void appendAssistantMessage(Long chatId, String content) {
        sessionMap.putIfAbsent(chatId, new ArrayList<>());
        sessionMap.get(chatId).add(Map.of("role", "assistant", "content", content));
        trimHistory(chatId);
    }

    private void trimHistory(Long chatId) {
        List<Map<String, String>> messages = sessionMap.get(chatId);
        int MAX_HISTORY = 10;
        if (messages.size() > MAX_HISTORY) {
            sessionMap.put(chatId, messages.subList(messages.size() - MAX_HISTORY, messages.size()));
        }
    }

    public JSONArray buildMessagesArray(Long chatId, String systemPrompt) {
        JSONArray messages = new JSONArray();
        messages.put(new JSONObject()
                .put("role", "system")
                .put("content", systemPrompt));

        List<Map<String, String>> history = getSessionMessages(chatId);
        for (Map<String, String> msg : history) {
            messages.put(new JSONObject()
                    .put("role", msg.get("role"))
                    .put("content", msg.get("content")));
        }

        return messages;
    }

}
