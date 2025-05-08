package de.pc1up.sentinelmc.guis;

import de.pc1up.sentinelmc.SentinelMC;
import de.pc1up.sentinelmc.objects.Punishment;
import de.pc1up.sentinelmc.objects.UserProfile;
import de.pc1up.sentinelmc.util.ItemBuilder;
import de.pc1up.sentinelmc.util.TimeParser;
import de.pc1up.sentinelmc.util.gui.InventoryButton;
import de.pc1up.sentinelmc.util.gui.InventoryGUI;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class HistoryGUI extends InventoryGUI {
    private UserProfile targetProfile;
    private List<Punishment> punishments;

    @Override
    protected Inventory createInventory() {
        String title = SentinelMC.instance.getMessageUtil().getMessage("history.title", Map.of("player", targetProfile.getName()));
        return Bukkit.createInventory(null, 54, MiniMessage.miniMessage().deserialize(title));
    }

    @Override
    public void decorate(Player player){
        decorate(player, 1);
    }

    public void decorate(Player player, int page) {
        this.getButtonMap().clear();
        this.getInventory().clear();
        int maxItemsPerPage = 45;
        int startIndex = (page - 1) * maxItemsPerPage;
        int endIndex = Math.min(startIndex + maxItemsPerPage, punishments.size());

        int displaySlot = 0;
        for(int i = startIndex; i < endIndex; i++) {
            Punishment punishment = punishments.get(i);
            String statusText;
            if(punishment.isRevoked()) {
                statusText = SentinelMC.instance.getMessageUtil().getMessage("history.entry.status.revoked", Map.of(
                        "revoked_by", punishment.getRevokedBy(),
                        "revoke_reason", punishment.getRevokeReason()
                ));
            } else if (punishment.isExpired()) {
                statusText = SentinelMC.instance.getMessageUtil().getMessage("history.entry.status.expired");
            } else {
                statusText = SentinelMC.instance.getMessageUtil().getMessage("history.entry.status.active");
            }
            Map<String, String> placeholders = Map.of(
                    "type", punishment.getType(),
                    "start_date", SentinelMC.instance.getMessageUtil().formatDate(punishment.getStartTime()),
                    "end_date", SentinelMC.instance.getMessageUtil().formatDate(punishment.getEndTime()),
                    "duration", punishment.getDuration(),
                    "note", punishment.getNote(),
                    "id", punishment.getId(),
                    "author", punishment.getAuthorName(),
                    "reason", punishment.getReason(),
                    "status", statusText
            );

            String itemName = SentinelMC.instance.getMessageUtil().getMessage("history.entry.name", placeholders);
            List<String> lores = SentinelMC.instance.getMessageUtil().getMultiLineMessage("history.entry.lores", placeholders);
            this.addButton(displaySlot, new InventoryButton()
                    .creator(p -> new ItemBuilder(Material.PAPER, itemName, lores).build())
                    .consumer(event -> event.setCancelled(true)));
            displaySlot++;
        }

        for (int i = 45; i < 54; i++) {
            this.addButton(i, new InventoryButton()
                    .creator(player1 -> new ItemBuilder(
                            Material.GRAY_STAINED_GLASS_PANE,
                            ""
                    ).build())
                    .consumer(event -> event.setCancelled(true)));
        }

        if (page > 1) {
            this.addButton(45, new InventoryButton()
                    .creator(player1 -> new ItemBuilder(
                            Material.LIME_DYE,
                            "<dark_gray>←"
                    ).build())
                    .consumer(event -> {
                        event.setCancelled(true);
                        decorate(player, page - 1);
                    }));
        } else {
            this.addButton(45, new InventoryButton()
                    .creator(player1 -> new ItemBuilder(
                            Material.GRAY_DYE,
                            "<dark_gray>←"
                    ).build())
                    .consumer(event -> {
                        event.setCancelled(true);
                    }));
        }

        if (endIndex < punishments.size()) {
            this.addButton(53, new InventoryButton()
                    .creator(player1 -> new ItemBuilder(
                            Material.LIME_DYE,
                            "<dark_gray>→"
                    ).build())
                    .consumer(event -> {
                        event.setCancelled(true);
                        decorate(player, page + 1);
                    }));
        } else {
            this.addButton(53, new InventoryButton()
                    .creator(player1 -> new ItemBuilder(
                            Material.GRAY_DYE,
                            "<dark_gray>→"
                    ).build())
                    .consumer(event -> {
                        event.setCancelled(true);
                    }));
        }
        super.decorate(player);
    }
}
