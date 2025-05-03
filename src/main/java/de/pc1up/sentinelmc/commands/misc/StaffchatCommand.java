package de.pc1up.sentinelmc.commands.misc;

import de.pc1up.sentinelmc.SentinelMC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class StaffchatCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if(command.getName().equalsIgnoreCase("staffchat")) {
            if(args.length == 0){
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "misc.staffchat.usage");
                return false;
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (String arg : args) {
                stringBuilder.append(arg).append(" ");
            }
            String message = stringBuilder.substring(0, stringBuilder.length() - 1);

            String msg = SentinelMC.instance.getMessageUtil().getMessage("misc.staffchat.message", Map.of(
                    "player", commandSender.getName(),
                    "message", message
            ));

            for(Player staff : Bukkit.getOnlinePlayers()) {
                if(staff.hasPermission("sentinelmc.staffchat")) {
                    staff.sendRichMessage(msg);
                }
            }
        }
        return false;
    }
}
