package de.pc1up.sentinelmc.commands.warn;

import de.pc1up.sentinelmc.SentinelMC;
import de.pc1up.sentinelmc.objects.UserProfile;
import de.pc1up.sentinelmc.objects.Warning;
import de.pc1up.sentinelmc.objects.WarningLadderPunishment;
import de.pc1up.sentinelmc.util.manager.SanctionManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class WarnCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if(command.getName().equalsIgnoreCase("warn")) {
            if(args.length < 2){
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "warnsystem.warn.usage");
                return false;
            }
            String targetName = args[0];
            UserProfile targetProfile = SentinelMC.instance.getDatabaseProvider().getProfile(targetName);
            if(targetProfile == null){
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "not_registered", Map.of("player", targetName));
                return false;
            }

            StringBuilder reasonBuilder = new StringBuilder();
            int points = 0;

            for (int i = 1; i < args.length; i++) {
                if (args[i].equalsIgnoreCase("--points")) {
                    if (i + 1 < args.length) {
                        try {
                            points = Integer.parseInt(args[i + 1]);
                            i++;
                        } catch (NumberFormatException e) {
                            SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "warnsystem.warn.usage");
                            return false;
                        }
                    } else {
                        SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "warnsystem.warn.usage");
                        return false;
                    }
                } else {
                    reasonBuilder.append(args[i]).append(" ");
                }
            }

            String reason = reasonBuilder.toString().trim();

            Warning warning = SentinelMC.instance.getSanctionManager().createWarning(targetProfile.getUuid(), commandSender.getName(), reason, points);
            SentinelMC.instance.getSanctionManager().performWarning(warning, targetProfile);

            SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "warnsystem.warn.success", Map.of("player", targetProfile.getName()));
            SentinelMC.instance.getMessageUtil().sendRestrictedMessage("warnsystem.notify", "warnsystem.warn.broadcast", Map.of(
                    "player", targetProfile.getName(),
                    "author", commandSender.getName(),
                    "reason", reason
            ));

            WarningLadderPunishment ladderPunishment = SentinelMC.instance.getSanctionManager().getWarningPunishment(targetProfile, warning);
            if(ladderPunishment != null){
                SentinelMC.instance.getSanctionManager().issueWarningPunishment(ladderPunishment, targetProfile.getUuid());
            }
        }
        return false;
    }
}
