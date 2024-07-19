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
                if(args.length < 2) {
                    Utils.chat("§cMissing arguments. Usage: /claim id <id>");
                    return;
                }
                new Thread(RewardClaim.fetch(args[1])).start();
                break;
            case "reward":
                if(args.length < 2) {
                    Utils.chat("§cMissing arguments. Usage: /claim reward <1|2|3>");
                    return;
                }

                int selection = parseInt(args[1]);
                if (selection < 1 || selection > 3) {
                    Utils.chat("§cInvalid reward. Usage: /claim reward <1|2|3>");
                    return;
                }
                new Thread(RewardClaim.claim(selection)).start();
                break;
            default:
                Utils.chat("§Missing arguments. Usage: /claim <id|reward>");
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
