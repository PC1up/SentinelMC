package de.pc1up.sentinelmc.commands.ban;

import de.pc1up.sentinelmc.SentinelMC;
import de.pc1up.sentinelmc.objects.Punishment;
import de.pc1up.sentinelmc.objects.UserProfile;
import de.pc1up.sentinelmc.util.manager.SanctionManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class BanCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if(command.getName().equalsIgnoreCase("ban")) {
            if(args.length < 2) {
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "bansystem.ban.usage");
                return false;
            }
            String targetName = args[0];
            UserProfile targetProfile = SentinelMC.instance.getDatabaseProvider().getProfile(targetName);
            if(targetProfile == null){
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "not_registered", Map.of("player", targetName));
                return false;
            }
            SanctionManager manager = SentinelMC.instance.getSanctionManager();
            if(manager.getActiveBan(targetProfile.getUuid()) != null) {
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "bansystem.ban.already_banned", Map.of("player", targetName));
                return false;
            }
            if(commandSender instanceof Player) {
                Player targetPlayer = Bukkit.getPlayer(targetName);
                if(targetPlayer != null){
                    if(targetPlayer.hasPermission("sentinelmc.ban.exempt")) {
                        SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "bansystem.ban.not_allowed", Map.of("player", targetName));
                        return false;
                    }
                }
            }

            StringBuilder reasonBuilder = new StringBuilder();
            StringBuilder noteBuilder = new StringBuilder();
            String note = "-";
            boolean noteSection = false;

            for (int i = 1; i < args.length; i++) {
                if (args[i].equalsIgnoreCase("--note")) {
                    noteSection = true;
                    continue;
                }

                if (noteSection) {
                    noteBuilder.append(args[i]).append(" ");
                } else {
                    reasonBuilder.append(args[i]).append(" ");
                }
            }

            String reason = reasonBuilder.toString().trim();
            if(!noteBuilder.isEmpty()) {
                note = noteBuilder.toString().trim();
            }

            String duration = SentinelMC.instance.getMessageUtil().formatDuration(-1);
            Punishment punishment = manager.createPunishment(targetProfile.getUuid(), commandSender.getName(), true, reason, -1, duration, note);
            manager.performPunishment(punishment, true);

            SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "bansystem.ban.success", Map.of("player", targetName));
            SentinelMC.instance.getMessageUtil().sendRestrictedMessage("bansystem.notify", "bansystem.ban.broadcast", Map.of(
                    "player", targetName,
                    "author", commandSender.getName(),
                    "reason", reason
            ));
        }
        return false;
    }
}
