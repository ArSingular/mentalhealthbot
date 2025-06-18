package dev.kuch.mental_health_support.repository;

import dev.kuch.mental_health_support.model.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Korol Artur
 * 18.06.2025
 */

@Repository
public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long> {

    Optional<TelegramUser> findByTelegramUserId(Long telegramUserId);
}
