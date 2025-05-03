package de.pc1up.sentinelmc.commands.ban;

import de.pc1up.sentinelmc.SentinelMC;
import de.pc1up.sentinelmc.objects.Punishment;
import de.pc1up.sentinelmc.objects.UserProfile;
import de.pc1up.sentinelmc.util.manager.SanctionManager;
import de.pc1up.sentinelmc.util.TimeParser;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Map;

public class TempbanCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if(command.getName().equalsIgnoreCase("tempban")) {
            if(args.length < 3) {
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "bansystem.tempban.usage");
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

            for (int i = 2; i < args.length; i++) {
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

            long duration = TimeParser.parseTimeToDuration(args[1], commandSender);
            long endTime = Instant.now().getEpochSecond() + duration;

            String durationString = SentinelMC.instance.getMessageUtil().formatDuration(endTime);
            Punishment punishment = manager.createPunishment(targetProfile.getUuid(), commandSender.getName(), true, reason, endTime, durationString, note);
            manager.performPunishment(punishment, true);

            SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "bansystem.tempban.success", Map.of("player", targetName));
            SentinelMC.instance.getMessageUtil().sendRestrictedMessage("bansystem.notify", "bansystem.tempban.broadcast", Map.of(
                    "player", targetName,
                    "author", commandSender.getName(),
                    "reason", reason,
                    "duration", durationString
            ));
        }
        return false;
    }
}
