package cat.porter.simplerewardclaim;

import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mod(modid = SimpleRewardClaim.MODID, name = SimpleRewardClaim.NAME, version = SimpleRewardClaim.VERSION)
public class SimpleRewardClaim {
    public static final String MODID = "@ID@";
    public static final String NAME = "@NAME@";
    public static final String VERSION = "@VER@";
    @Mod.Instance(MODID)
    public static SimpleRewardClaim INSTANCE;

    public static Logger LOGGER = LogManager.getLogger(MODID);
    private final Set<String> ids = new HashSet<>();

    private final Pattern REWARD_REGEX = Pattern.compile("Click the link to visit our website and claim your reward: https://rewards.hypixel.net/claim-reward/(?<id>[A-Za-z0-9]{8})");
    private final Pattern MISSED_REWARD_REGEX = Pattern.compile("We noticed you haven't claimed your free Daily Reward yet!\nTo choose your reward you have to click the link to visit our website! As a reminder, here's your link for today:  https://rewards.hypixel.net/claim-reward/(?<id>[A-Za-z0-9]{8})");

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new Command());
    }

    @SubscribeEvent
    public void onChatReceive(ClientChatReceivedEvent event) {
        if (event.type != 0) return;
        new Thread(() -> {
            Matcher rewardMatcher = REWARD_REGEX.matcher(event.message.getUnformattedText());
            if (rewardMatcher.find() && rewardMatcher.group("id") != null) {
                if (rewardMessageFound(rewardMatcher)) return;
                return;
            }
            Matcher missedRewardMatcher = MISSED_REWARD_REGEX.matcher(event.message.getUnformattedText());
            if (missedRewardMatcher.find() && missedRewardMatcher.group("id") != null) {
                if (rewardMessageFound(missedRewardMatcher)) return;
            }
        }).start();
    }

    private boolean rewardMessageFound(Matcher rewardMatcher) {
        String id = rewardMatcher.group("id");
        if (!ids.contains(id)) {
            RewardClaim.fetch(id).run();
            // new Thread(RewardClaim.fetch(id)).start();
            ids.add(id);
            return true;
        }
        ChatComponentText component = new ChatComponentText("§d[RewardClaim] A request for this reward has already been made. ");
        ChatComponentText button = new ChatComponentText("§9[Make it again?]");
        button.getChatStyle()
                .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/claim id " + id))
                .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Click here to fetch your daily rewards.")));
        component.appendSibling(button);
        Utils.chat(component);
        return false;
    }
}