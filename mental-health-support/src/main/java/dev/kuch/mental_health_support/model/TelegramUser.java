package dev.kuch.mental_health_support.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Korol Artur
 * 18.06.2025
 */

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "telegram_users")
public class TelegramUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private Long telegramUserId;
    private String username;
    private Long chatId;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "telegramUser", cascade = CascadeType.ALL)
    private List<Mood> moods = new ArrayList<>();

}
