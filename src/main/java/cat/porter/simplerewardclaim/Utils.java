package cat.porter.simplerewardclaim;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class Utils {
    public static void chat(String message) {
        ChatComponentText component = new ChatComponentText(message);
        Minecraft.getMinecraft().thePlayer.addChatMessage(component);
    }

    public static void chat(IChatComponent component) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(component);
    }
}
