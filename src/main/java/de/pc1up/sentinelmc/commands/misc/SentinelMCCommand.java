package de.pc1up.sentinelmc.commands.misc;

import de.pc1up.sentinelmc.SentinelMC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SentinelMCCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if(command.getName().equalsIgnoreCase("sentinelmc")) {
            if(args.length == 0){
                commandSender.sendRichMessage("<red>/sentinelmc reload");
                return false;
            }
            if(args[0].equalsIgnoreCase("reload")) {
                SentinelMC.instance.reloadCfg();
                commandSender.sendRichMessage("<green>Configuration reloaded!");
            }
        }
        return false;
    }
}
