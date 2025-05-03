package de.pc1up.sentinelmc.commands.misc;

import de.pc1up.sentinelmc.SentinelMC;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SpectateCommand implements CommandExecutor {
    public static HashMap<UUID, Location> spectating = new HashMap<>();
    public static HashMap<UUID, GameMode> spectatingGameMode = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if(command.getName().equalsIgnoreCase("spectate")) {
            if(commandSender instanceof Player player) {
                if(spectating.containsKey(player.getUniqueId())) {
                    Location setbackLocation = spectating.get(player.getUniqueId());
                    GameMode setbackMode = spectatingGameMode.get(player.getUniqueId());
                    player.setGameMode(setbackMode);
                    player.teleport(setbackLocation);
                    spectating.remove(player.getUniqueId());
                    spectatingGameMode.remove(player.getUniqueId());
                    SentinelMC.instance.getMessageUtil().sendMessage(player, "misc.spectate.disabled");
                } else {
                    if(args.length == 0){
                        SentinelMC.instance.getMessageUtil().sendMessage(player, "misc.spectate.usage");
                        return false;
                    }
                    Player targetPlayer = Bukkit.getPlayer(args[0]);
                    if(targetPlayer == null) {
                        SentinelMC.instance.getMessageUtil().sendMessage(player, "not_online", Map.of("player", args[0]));
                        return false;
                    }

                    if(targetPlayer.getName().equalsIgnoreCase(player.getName())) {
                        SentinelMC.instance.getMessageUtil().sendMessage(player, "not_self");
                        return false;
                    }

                    spectating.put(player.getUniqueId(), player.getLocation());
                    spectatingGameMode.put(player.getUniqueId(), player.getGameMode());

                    player.setGameMode(GameMode.SPECTATOR);
                    player.teleport(targetPlayer);
                    SentinelMC.instance.getMessageUtil().sendMessage(player, "misc.spectate.enabled", Map.of("player", targetPlayer.getName()));
                }
            }
        }
        return false;
    }
}
