package org.polyfrost.example.types;
import java.util.List;

public class Data {
    private List<Reward> rewards;
    private DailyStreak dailyStreak;
    private String id;
    private String csrfToken;

    public int getDailyStreak() {
        return dailyStreak.getValue();
    }

    public String getId() {
        return id;
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public String getCsrfToken() {
        return csrfToken;
    }

    public void setCsrfToken(String csrfToken) {
        this.csrfToken = csrfToken;
    }
}
