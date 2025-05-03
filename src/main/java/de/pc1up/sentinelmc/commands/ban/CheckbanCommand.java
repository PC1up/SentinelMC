package de.pc1up.sentinelmc.commands.ban;

import de.pc1up.sentinelmc.SentinelMC;
import de.pc1up.sentinelmc.objects.Punishment;
import de.pc1up.sentinelmc.objects.UserProfile;
import de.pc1up.sentinelmc.util.manager.SanctionManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class CheckbanCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if(command.getName().equalsIgnoreCase("checkban")) {
            if(args.length == 0) {
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "bansystem.checkban.usage");
                return false;
            }
            String targetName = args[0];
            UserProfile targetProfile = SentinelMC.instance.getDatabaseProvider().getProfile(targetName);
            if(targetProfile == null){
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "not_registered", Map.of("player", targetName));
                return false;
            }
            SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "bansystem.checkban.header", Map.of("player", targetProfile.getName()));
            SanctionManager manager = SentinelMC.instance.getSanctionManager();
            Punishment punishment = manager.getActiveBan(targetProfile.getUuid());
            if(punishment == null) {
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "bansystem.checkban.not_banned", Map.of("player", targetName));
            } else {
                Map<String, String> placeholders = Map.of(
                        "reason", punishment.getReason(),
                        "author", punishment.getAuthorName(),
                        "start_date", SentinelMC.instance.getMessageUtil().formatDate(punishment.getStartTime()),
                        "end_date", SentinelMC.instance.getMessageUtil().formatDate(punishment.getEndTime()),
                        "duration", SentinelMC.instance.getMessageUtil().formatDuration(punishment.getEndTime()),
                        "note", punishment.getNote(),
                        "id", punishment.getId()
                );

                List<String> checkMessage = SentinelMC.instance.getMessageUtil().getMultiLineMessage("bansystem.checkban.banned", placeholders);
                checkMessage.forEach(commandSender::sendRichMessage);
            }
        }
        return false;
    }
}
