package dev.kuch.mental_health_support.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class TranslationService {

    private final RestTemplate restTemplate;

    public TranslationService() {
        this.restTemplate = new RestTemplate();
    }

    public String translateText(String text, String targetLanguage) {
        String url = "https://libretranslate.com/translate";

        // Параметри для запиту
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("q", text)
                .queryParam("source", "en")
                .queryParam("target", targetLanguage);

        // Виконання POST запиту
        ResponseEntity<LibreTranslateResponse> response = restTemplate.postForEntity(
                builder.toUriString(), null, LibreTranslateResponse.class);

        return response.getBody() != null ? response.getBody().getTranslatedText() : "Error translating";
    }

    public static class LibreTranslateResponse {
        private String translatedText;

        public String getTranslatedText() {
            return translatedText;
        }

        public void setTranslatedText(String translatedText) {
            this.translatedText = translatedText;
        }
    }
}
