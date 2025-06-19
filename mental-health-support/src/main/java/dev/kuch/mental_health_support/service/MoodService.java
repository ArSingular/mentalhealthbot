package dev.kuch.mental_health_support.service;

import dev.kuch.mental_health_support.model.Mood;
import dev.kuch.mental_health_support.repository.MoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MoodService {

    private final MoodRepository moodRepository;

    public void saveMood(String chatId, String mood, String description) {

        Mood entry;

        if(moodRepository.findByChatIdAndDate(chatId, LocalDate.now()).isPresent()){
            entry  = moodRepository.findByChatIdAndDate(chatId, LocalDate.now()).get();
            entry.setChatId(chatId);
            entry.setMood(mood);
            entry.setDescription(description);
        }
        else {
            entry = new Mood();
            entry.setChatId(chatId);
            entry.setMood(mood);
            entry.setDescription(description);

            moodRepository.save(entry);
        }

        moodRepository.save(entry);
    }

//    public List<Mood> getAllMoodsForUser(String chatId) {
//        return moodStorage.getOrDefault(chatId, Collections.emptyList())
//                .stream()
//                .sorted(Comparator.comparing(Mood::getTimestamp).reversed())
//                .collect(Collectors.toList());
//    }

}
