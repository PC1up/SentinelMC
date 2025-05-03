package de.pc1up.sentinelmc.commands.ban;

import de.pc1up.sentinelmc.SentinelMC;
import de.pc1up.sentinelmc.objects.Punishment;
import de.pc1up.sentinelmc.objects.UserProfile;
import de.pc1up.sentinelmc.util.manager.SanctionManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class UnbanCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if(command.getName().equalsIgnoreCase("unban")) {
            if(args.length == 0) {
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "bansystem.unban.usage");
                return false;
            }
            String targetName = args[0];
            UserProfile targetProfile = SentinelMC.instance.getDatabaseProvider().getProfile(targetName);
            if(targetProfile == null){
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "not_registered", Map.of("player", targetName));
                return false;
            }
            SanctionManager manager = SentinelMC.instance.getSanctionManager();
            Punishment activeBan = manager.getActiveBan(targetProfile.getUuid());
            if(activeBan == null) {
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "bansystem.unban.not_banned", Map.of("player", targetName));
                return false;
            }

            String reason = "-";
            if(args.length > 1){
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    stringBuilder.append(args[i]).append(" ");
                }
                reason = stringBuilder.substring(0, stringBuilder.length() - 1);
            }

            manager.revokePunishment(activeBan, commandSender.getName(), reason);

            SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "bansystem.unban.success", Map.of("player", targetName));
            if(reason.equalsIgnoreCase("-") || reason.isEmpty() || reason.isBlank() || reason.trim().isBlank() || reason.trim().isEmpty()) {
                SentinelMC.instance.getMessageUtil().sendRestrictedMessage("bansystem.notify", "bansystem.unban.broadcast.no_reason", Map.of(
                        "player", targetName,
                        "author", commandSender.getName()
                ));
            } else {
                SentinelMC.instance.getMessageUtil().sendRestrictedMessage("bansystem.notify", "bansystem.unban.broadcast.reason", Map.of(
                        "player", targetName,
                        "author", commandSender.getName(),
                        "reason", reason
                ));
            }
        }
        return false;
    }
}
