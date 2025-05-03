package de.pc1up.sentinelmc.commands.warn;

import de.pc1up.sentinelmc.SentinelMC;
import de.pc1up.sentinelmc.objects.UserProfile;
import de.pc1up.sentinelmc.objects.Warning;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class DelwarnCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if(command.getName().equalsIgnoreCase("delwarn")) {
            if(args.length == 0) {
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "warnsystem.delwarn.usage");
                return false;
            }
            String id = args[0];
            Warning warning = SentinelMC.instance.getDatabaseProvider().getWarning(id);
            if(warning == null) {
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "warnsystem.delwarn.invalid");
                return false;
            }

            UserProfile targetProfile = SentinelMC.instance.getDatabaseProvider().getProfile(warning.getTargetUUID());

            SentinelMC.instance.getSanctionManager().removeWarning(warning, targetProfile);

            SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "warnsystem.delwarn.success", Map.of("id", id));
            SentinelMC.instance.getMessageUtil().sendRestrictedMessage("warnsystem.notify", "warnsystem.delwarn.broadcast", Map.of(
                    "id", id,
                    "author", commandSender.getName()
            ));
        }
        return false;
    }
}
