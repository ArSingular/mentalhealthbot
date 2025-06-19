package dev.kuch.mental_health_support.repository;

import dev.kuch.mental_health_support.model.Mood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * @author Korol Artur
 * 18.06.2025
 */

@Repository
public interface MoodRepository extends JpaRepository<Mood, Long> {

    Optional<Mood> findByChatIdAndDate(String chatId, LocalDate date);

}
