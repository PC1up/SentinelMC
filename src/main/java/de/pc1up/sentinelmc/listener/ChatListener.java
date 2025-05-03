package de.pc1up.sentinelmc.listener;

import de.pc1up.sentinelmc.SentinelMC;
import de.pc1up.sentinelmc.objects.Punishment;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.UUID;

public class ChatListener implements Listener {

    @EventHandler (priority = EventPriority.HIGH)
    public void onChat(AsyncChatEvent event){
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Punishment activeMute = SentinelMC.instance.getSanctionManager().getActiveMute(uuid);
        if(activeMute != null) {
            event.setCancelled(true);
            SentinelMC.instance.getSanctionManager().performPunishment(activeMute, false);
        }
    }

}
