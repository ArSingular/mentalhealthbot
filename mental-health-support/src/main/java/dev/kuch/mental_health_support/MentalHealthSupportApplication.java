package dev.kuch.mental_health_support;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.telegram.telegrambots.starter.TelegramBotStarterConfiguration;

@SpringBootApplication
@Import(TelegramBotStarterConfiguration.class)
public class MentalHealthSupportApplication {

	public static void main(String[] args) {
		SpringApplication.run(MentalHealthSupportApplication.class, args);
	}

}
