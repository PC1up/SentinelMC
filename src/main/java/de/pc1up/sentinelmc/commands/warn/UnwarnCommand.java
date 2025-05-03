package de.pc1up.sentinelmc.commands.warn;

import de.pc1up.sentinelmc.SentinelMC;
import de.pc1up.sentinelmc.objects.UserProfile;
import de.pc1up.sentinelmc.objects.Warning;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class UnwarnCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if(command.getName().equalsIgnoreCase("unwarn")) {
            if(args.length == 0) {
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "warnsystem.unwarn.usage");
                return false;
            }
            String targetName = args[0];
            UserProfile targetProfile = SentinelMC.instance.getDatabaseProvider().getProfile(targetName);
            if(targetProfile == null){
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "not_registered", Map.of("player", targetName));
                return false;
            }
            Warning latestWarning = SentinelMC.instance.getDatabaseProvider().getLatestWarning(targetProfile.getUuid());
            if(latestWarning == null) {
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "warnsystem.unwarn.not_warned", Map.of("player", targetProfile.getName()));
                return false;
            }

            SentinelMC.instance.getSanctionManager().removeWarning(latestWarning, targetProfile);

            SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "warnsystem.unwarn.success", Map.of("player", targetProfile.getName()));
            SentinelMC.instance.getMessageUtil().sendRestrictedMessage("warnsystem.notify", "warnsystem.unwarn.broadcast", Map.of(
                    "player", targetProfile.getName(),
                    "author", commandSender.getName()
            ));
        }
        return false;
    }
}
