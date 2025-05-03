package de.pc1up.sentinelmc.commands.mute;

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

public class TempmuteCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if(command.getName().equalsIgnoreCase("tempmute")) {
            if(args.length < 3) {
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "mutesystem.tempmute.usage");
                return false;
            }
            String targetName = args[0];
            UserProfile targetProfile = SentinelMC.instance.getDatabaseProvider().getProfile(targetName);
            if(targetProfile == null){
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "not_registered", Map.of("player", targetName));
                return false;
            }
            SanctionManager manager = SentinelMC.instance.getSanctionManager();
            if(manager.getActiveMute(targetProfile.getUuid()) != null) {
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "mutesystem.mute.already_muted", Map.of("player", targetName));
                return false;
            }
            if(commandSender instanceof Player) {
                Player targetPlayer = Bukkit.getPlayer(targetName);
                if(targetPlayer != null){
                    if(targetPlayer.hasPermission("sentinelmc.mute.exempt")) {
                        SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "mutesystem.mute.not_allowed", Map.of("player", targetName));
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
            Punishment punishment = manager.createPunishment(targetProfile.getUuid(), commandSender.getName(), false, reason, endTime, durationString, note);
            manager.performPunishment(punishment, true);

            SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "mutesystem.tempmute.success", Map.of("player", targetName));
            SentinelMC.instance.getMessageUtil().sendRestrictedMessage("mutesystem.notify", "mutesystem.tempmute.broadcast", Map.of(
                    "player", targetName,
                    "author", commandSender.getName(),
                    "reason", reason,
                    "duration", durationString
            ));
        }
        return false;
    }
}
