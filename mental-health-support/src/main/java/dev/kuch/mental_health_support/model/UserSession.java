package dev.kuch.mental_health_support.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Artur Kuch
 */

public class UserSession {

    private BotState state = BotState.NONE;
    private Map<String, String> tempData = new HashMap<>();

    public BotState getState() {
        return state;
    }

    public void setState(BotState state) {
        this.state = state;
    }

    public Map<String, String> getTempData() {
        return tempData;
    }

}
