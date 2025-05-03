package de.pc1up.sentinelmc.listener;

import de.pc1up.sentinelmc.SentinelMC;
import de.pc1up.sentinelmc.objects.Punishment;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LoginListener implements Listener {

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event){
        UUID uuid = event.getUniqueId();
        SentinelMC.instance.getUserManager().getOrCreate(event.getUniqueId(), event.getName(), event.getAddress().getHostAddress());
        Punishment activeBan = SentinelMC.instance.getSanctionManager().getActiveBan(uuid);
        if(activeBan != null) {
            Map<String, String> placeholders = new java.util.HashMap<>(Map.of(
                    "reason", activeBan.getReason(),
                    "author", activeBan.getAuthorName(),
                    "id", activeBan.getId(),
                    "note", activeBan.getNote()
            ));
            if (activeBan.getEndTime() == -1) { // perma
                List<String> lines = SentinelMC.instance.getMessageUtil().getMultiLineMessage("bansystem.ban.screen", placeholders);
                Component kickMessage = Component.empty();
                for (String line : lines) {
                    kickMessage = kickMessage
                            .append(MiniMessage.miniMessage().deserialize(line))
                            .append(Component.newline());
                }
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, kickMessage);
            } else {
                placeholders.put("duration", SentinelMC.instance.getMessageUtil().formatDuration(activeBan.getEndTime()));
                placeholders.put("end_date", SentinelMC.instance.getMessageUtil().formatDate(activeBan.getEndTime()));

                List<String> lines = SentinelMC.instance.getMessageUtil().getMultiLineMessage("bansystem.tempban.screen", placeholders);
                Component kickMessage = Component.empty();
                for (String line : lines) {
                    kickMessage = kickMessage
                            .append(MiniMessage.miniMessage().deserialize(line))
                            .append(Component.newline());
                }
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, kickMessage);
            }
        }
    }

}
