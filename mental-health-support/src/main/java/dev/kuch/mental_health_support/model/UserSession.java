package dev.kuch.mental_health_support.model;

import dev.kuch.mental_health_support.model.enums.BotState;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Artur Kuch
 */


@Getter
@Setter
public class UserSession {

    private BotState state = BotState.NONE;
    private Map<String, String> tempData = new HashMap<>();

}
