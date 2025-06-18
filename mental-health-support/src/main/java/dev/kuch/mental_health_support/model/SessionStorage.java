package dev.kuch.mental_health_support.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Artur Kuch
 */

public class SessionStorage {

    private static final Map<Long, UserSession> sessions = new ConcurrentHashMap<>();

    public static UserSession getSession(Long chatId) {
        return sessions.computeIfAbsent(chatId, id -> new UserSession());
    }

    public static void clearSession(Long chatId) {
        sessions.remove(chatId);
    }

}
