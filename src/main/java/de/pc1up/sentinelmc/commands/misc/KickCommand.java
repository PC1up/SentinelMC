package de.pc1up.sentinelmc.commands.misc;

import de.pc1up.sentinelmc.SentinelMC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class KickCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (command.getName().equalsIgnoreCase("kick")) {
            if (args.length < 2) {
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "bansystem.kick.usage");
                return false;
            }
            String targetName = args[0];
            Player targetPlayer = Bukkit.getPlayer(targetName);
            if (targetPlayer == null) {
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "not_online", Map.of("player", targetName));
                return false;
            }

            if (commandSender instanceof Player) {
                if (targetPlayer.hasPermission("sentinelmc.kick.exempt")) {
                    SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "bansystem.kick.not_allowed", Map.of("player", targetName));
                    return false;
                }
            }

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                stringBuilder.append(args[i]).append(" ");
            }
            String reason = stringBuilder.substring(0, stringBuilder.length() - 1);

            targetPlayer.kick(
                    SentinelMC.instance.getMessageUtil().buildScreen("bansystem.kick.screen", Map.of(
                            "author", commandSender.getName(),
                            "reason", reason
                    ))
            );

            SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "bansystem.kick.success", Map.of("player", targetName));
            SentinelMC.instance.getMessageUtil().sendRestrictedMessage("bansystem.notify", "bansystem.kick.broadcast", Map.of(
                    "player", targetName,
                    "author", commandSender.getName(),
                    "reason", reason
            ));
        }
        return false;
    }
}
