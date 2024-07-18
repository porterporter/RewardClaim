package cat.porter.simplerewardclaim;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = SimpleRewardClaim.MODID, name = SimpleRewardClaim.NAME, version = SimpleRewardClaim.VERSION)
public class SimpleRewardClaim {
    public static final String MODID = "@ID@";
    public static final String NAME = "@NAME@";
    public static final String VERSION = "@VER@";
    @Mod.Instance(MODID)
    public static SimpleRewardClaim INSTANCE;

    public static Logger LOGGER = LogManager.getLogger(MODID);
    public static RewardClaim SESSION;

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new Command());
    }

    @SubscribeEvent
    public void onChatReceive(ClientChatReceivedEvent event) {
        if (event.type != 0) return;
        LOGGER.debug("Chat received: " + event.message.getUnformattedText());
    }
}