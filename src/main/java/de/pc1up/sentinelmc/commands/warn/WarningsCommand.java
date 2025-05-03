package de.pc1up.sentinelmc.commands.warn;

import de.pc1up.sentinelmc.SentinelMC;
import de.pc1up.sentinelmc.objects.UserProfile;
import de.pc1up.sentinelmc.objects.Warning;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class WarningsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if(command.getName().equalsIgnoreCase("warnings")) {
            if(args.length == 0) {
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "warnsystem.warnings.usage");
                return false;
            }
            String targetName = args[0];
            UserProfile targetProfile = SentinelMC.instance.getDatabaseProvider().getProfile(targetName);
            if(targetProfile == null){
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "not_registered", Map.of("player", targetName));
                return false;
            }

            List<Warning> warnings = SentinelMC.instance.getDatabaseProvider().getWarnings(targetProfile.getUuid());
            if(warnings.isEmpty()) {
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "warnsystem.warnings.no_entries", Map.of("player", targetProfile.getName()));
                return false;
            }
            SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "warnsystem.warnings.header", Map.of("player", targetProfile.getName()));
            for(Warning warning : warnings) {
                Map<String, String> placeholder = Map.of(
                        "date", SentinelMC.instance.getMessageUtil().formatDate(warning.getTimestamp()),
                        "author", warning.getAuthorName(),
                        "reason", warning.getReason(),
                        "id", warning.getId(),
                        "points", String.valueOf(warning.getPoints())
                );
                List<String> entryMessage = SentinelMC.instance.getMessageUtil().getMultiLineMessage("warnsystem.warnings.entry", placeholder);
                entryMessage.forEach(commandSender::sendRichMessage);
            }
        }
        return false;
    }
}
