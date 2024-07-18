package cat.porter.simplerewardclaim;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

import java.util.List;

public class Command extends CommandBase {

    @Override
    public String getCommandName() {
        return "claim";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "claim <id>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        switch (args[0]) {
            case "id":
                new Thread(new RewardClaim().fetch(args[1])).start();
                break;
            case "reward":
                int selection = parseInt(args[1]);
                if (selection < 0 || selection > 2) {
                    Utils.chat("Invalid reward selection.");
                    return;
                }
                if (SimpleRewardClaim.SESSION == null) {
                    Utils.chat("No session found.");
                    return;
                }
                new Thread(SimpleRewardClaim.SESSION.claim(selection)).start();
                break;
            default:
                Utils.chat("Invalid command usage.");
                break;

        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if(args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "id", "reward");
        }
        if (args[0].equals("reward")) {
            return getListOfStringsMatchingLastWord(args, "1", "2", "3");
        }
        return null;
    }
}
