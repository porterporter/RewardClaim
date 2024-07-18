package cat.porter.simplerewardclaim;

import cat.porter.simplerewardclaim.types.Data;
import cat.porter.simplerewardclaim.types.Reward;
import com.google.gson.Gson;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RewardClaim {
    private final Pattern DATA_REGEX = Pattern.compile("window\\.appData = '(?<data>\\{.*})';");
    private final Pattern TRANSLATION_REGEX = Pattern.compile("window.i18n = \\{(?<translations>.*)};", Pattern.DOTALL);
    private final Pattern TRANSLATION_LINE_REGEX = Pattern.compile("\"(?<key>.*)\": ?\"(?<text>.*)\",?");
    private final Pattern SECURITY_REGEX = Pattern.compile("window\\.securityToken = \"(?<token>.*)\";");
    private final Gson GSON = new Gson();
    private Data currentSession;

    public Runnable fetch(String rewardId) {
        return () -> {
            try {
                CookieManager cookieManager = new CookieManager();
                CookieHandler.setDefault(cookieManager);
                URL url = new URL("https://rewards.hypixel.net/claim-reward/" + rewardId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36");
                conn.setUseCaches(true);
                conn.setDoOutput(true);
                conn.connect();

                if (conn.getResponseCode() != 200)
                    throw new Exception("Invalid response code: " + conn.getResponseCode());
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) response.append(inputLine);
                in.close();

                if(response.toString().contains("This link either never existed or has already expired.")) throw new Exception("This reward link has expired.");

                Matcher dataMatcher = DATA_REGEX.matcher(response.toString());
                if (!dataMatcher.find()) throw new Exception("Failed to find data.");
                String appData = dataMatcher.group("data");


                Matcher translationMatcher = TRANSLATION_REGEX.matcher(response.toString());
                if (!translationMatcher.find()) throw new Exception("Failed to find translations.");
                String translationsData = translationMatcher.group("translations");

                HashMap<String, String> translations = new HashMap<>();
                Matcher translationLineMatcher = TRANSLATION_LINE_REGEX.matcher(translationsData.replace("                ", "\n").replace("\\'", "'"));
                while (translationLineMatcher.find()) {
                    translations.put(translationLineMatcher.group("key"), translationLineMatcher.group("text"));
                }

                if(translations.size() < 10) throw new Exception("Failed to parse translations. " + translations.size() + " translations found.");

                Data data = GSON.fromJson(appData, Data.class);

                Matcher crsfMatcher = SECURITY_REGEX.matcher(response.toString());
                if (!crsfMatcher.find()) throw new Exception("Failed to find CSRF token.");
                String crsfToken = crsfMatcher.group("token");

                data.setCsrfToken(crsfToken);
                SimpleRewardClaim.LOGGER.debug("Rewards for id " + rewardId + ": " + String.join(", ", data.getRewards().stream().map(reward -> reward.getDisplayName(translations)).toArray(String[]::new)));

                ChatComponentText base = new ChatComponentText("Daily Reward: ");

                for (int i = 0; i < data.getRewards().size(); i++) {
                    Reward reward = data.getRewards().get(i);

                    ChatStyle style = new ChatStyle()
                            .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/claim reward " + i))
                            .setChatHoverEvent(new HoverEvent(
                                    HoverEvent.Action.SHOW_TEXT,
                                    new ChatComponentText("Click click here to claim " + reward.getDescription(translations))));

                    IChatComponent component = new ChatComponentText(reward.getRarityColor() + "[" + reward.getDisplayName(translations) + "]")
                            .setChatStyle(style);
                    if (i < data.getRewards().size() - 1) {
                        component.appendText(" ");
                    }

                    base.appendSibling(component);
                }

                Utils.chat(base);

                currentSession = data;
                SimpleRewardClaim.SESSION = this;
            } catch (Exception e) {
                if (e instanceof MalformedURLException) System.err.println("Invalid reward URL.");
                SimpleRewardClaim.LOGGER.error(e.getMessage());
            }
        };
    }

    public Runnable claim(int selected) {
        return () -> {
            if(currentSession == null) throw new IllegalStateException("No session found.");
            try {
                URL submission = new URL("https://rewards.hypixel.net/claim-reward/claim?option=" + selected + "&id=" + currentSession.getId() + "&activeAd=" + 0 + "&_csrf=" + currentSession.getCsrfToken() + "&watchedFallback=false");
                HttpURLConnection submitConn = (HttpURLConnection) submission.openConnection();
                submitConn.setRequestMethod("POST");
                submitConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36");
                submitConn.setUseCaches(true);
                submitConn.setDoOutput(true);
                CookieManager.setDefault(null);
                submitConn.connect();
                if (submitConn.getResponseCode() != 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(submitConn.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) response.append(inputLine);
                    in.close();
                    throw new Exception("Invalid response code: " + submitConn.getResponseCode() + "\n" + response);
                }
            } catch (Exception e) {
                SimpleRewardClaim.LOGGER.error(e);
                SimpleRewardClaim.LOGGER.error(e.getMessage());
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                SimpleRewardClaim.LOGGER.error(sw.toString());
            }
        };
    }
}