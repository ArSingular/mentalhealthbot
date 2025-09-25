package dev.kuch.mental_health_support.service;

import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class ChatSessionService {

    private final Map<Long, CopyOnWriteArrayList<Map<String, String>>> sessionMap = new ConcurrentHashMap<>();

    public List<Map<String, String>> getSessionMessages(Long chatId) {
        return sessionMap.computeIfAbsent(chatId, k -> new CopyOnWriteArrayList<>());
    }

    public void appendUserMessage(Long chatId, String content) {
        getSessionMessages(chatId).add(Map.of("role", "user", "content", content));
        trimHistory(chatId);
    }

    public void appendAssistantMessage(Long chatId, String content) {
        getSessionMessages(chatId).add(Map.of("role", "assistant", "content", content));
        trimHistory(chatId);
    }

    private void trimHistory(Long chatId) {
        var list = getSessionMessages(chatId);
        int MAX_HISTORY = 8;
        int extra = list.size() - MAX_HISTORY;
        if (extra > 0) for (int i = 0; i < extra; i++) list.remove(0);
    }

    public JSONArray buildMessagesArrayOnlyHistory(Long chatId) {
        JSONArray arr = new JSONArray();
        for (Map<String, String> msg : getSessionMessages(chatId)) {
            arr.put(new JSONObject().put("role", msg.get("role")).put("content", msg.get("content")));
        }
        return arr;
    }
}