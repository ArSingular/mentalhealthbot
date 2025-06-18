package dev.kuch.mental_health_support.service;

import com.deepl.api.DeepLClient;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Service
@Slf4j
public class QuoteService {

    private final RestTemplate restTemplate;

    @Value("${deepl.token}")
    private String deeplToken;

    public QuoteService() {
        this.restTemplate = new RestTemplate();
        this.restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory("https://zenquotes.io/api"));
    }

    public String getRandomQuote(){
        try {
            DeepLClient client = new DeepLClient(deeplToken);
            String response = restTemplate.getForObject("/random", String.class);
            JSONArray jsonArray = new JSONArray(response);
            JSONObject quoteObject = jsonArray.getJSONObject(0);
            String quote = quoteObject.getString("q");
            String author = quoteObject.getString("a");

            String result =  String.format("💬 \"%s\"\n\n— %s", quote, author);

            return client.translateText(result, null, "uk").getText();

        }catch (Exception e){
            log.error("Виникла помилка: {}", e.getMessage());
            return "Цитати десь загубились, Я вже їх ловлю \uD83D\uDC1B\uD83E\uDEA4\nАле знаєш що? Ти — справжній герой цього дня!\uD83E\uDDB8";
        }
    }

}
