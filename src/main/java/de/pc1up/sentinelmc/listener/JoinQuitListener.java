package de.pc1up.sentinelmc.listener;

import de.pc1up.sentinelmc.SentinelMC;
import de.pc1up.sentinelmc.commands.misc.SpectateCommand;
import de.pc1up.sentinelmc.commands.misc.VanishCommand;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class JoinQuitListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(!player.hasPermission("sentinelmc.see-vanished")) {
            for(UUID vanishedUUID : VanishCommand.vanished) {
                Player vanishedPlayer = Bukkit.getPlayer(vanishedUUID);
                if(vanishedPlayer != null) {
                    player.hidePlayer(SentinelMC.instance, vanishedPlayer);
                }
            }
        }

        if(SpectateCommand.spectating.containsKey(player.getUniqueId())) {
            player.setGameMode(GameMode.SPECTATOR);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        VanishCommand.vanished.remove(player.getUniqueId());
    }

}
