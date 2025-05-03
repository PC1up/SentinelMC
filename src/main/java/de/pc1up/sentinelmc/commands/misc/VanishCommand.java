package de.pc1up.sentinelmc.commands.misc;

import de.pc1up.sentinelmc.SentinelMC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class VanishCommand implements CommandExecutor {
    public static final ArrayList<UUID> vanished = new ArrayList<>();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if(command.getName().equalsIgnoreCase("vanish")) {
            if(commandSender instanceof Player player) {
                if(!vanished.contains(player.getUniqueId())) {
                    vanished.add(player.getUniqueId());
                    for(Player all : Bukkit.getOnlinePlayers()) {
                        if(!all.hasPermission("sentinelmc.see-vanished")) {
                            all.hidePlayer(SentinelMC.instance, player);
                        }
                    }
                    SentinelMC.instance.getMessageUtil().sendMessage(player, "misc.vanish.enabled");
                } else {
                    vanished.remove(player.getUniqueId());
                    for(Player all : Bukkit.getOnlinePlayers()) {
                        all.showPlayer(SentinelMC.instance, player);
                    }
                    SentinelMC.instance.getMessageUtil().sendMessage(player, "misc.vanish.disabled");
                }
            }
        }
        return false;
    }
}
