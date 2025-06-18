package dev.kuch.mental_health_support.service;

import dev.kuch.mental_health_support.model.Mood;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class MoodService {

    private final Map<String, List<Mood>> moodStorage = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public void saveMood(String chatId, String mood, String description) {
        Mood entry = new Mood();
        entry.setId(idGenerator.getAndIncrement());
        entry.setChatId(chatId);
        entry.setMood(mood);
        entry.setTimestamp(LocalDateTime.now());
        entry.setDescription(description);

        moodStorage.computeIfAbsent(chatId, k -> new ArrayList<>()).add(entry);
    }

    public List<Mood> getAllMoodsForUser(String chatId) {
        return moodStorage.getOrDefault(chatId, Collections.emptyList())
                .stream()
                .sorted(Comparator.comparing(Mood::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

}
