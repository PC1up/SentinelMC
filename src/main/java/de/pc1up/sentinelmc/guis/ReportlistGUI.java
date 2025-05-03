package de.pc1up.sentinelmc.guis;

import de.pc1up.sentinelmc.SentinelMC;
import de.pc1up.sentinelmc.objects.Punishment;
import de.pc1up.sentinelmc.objects.Report;
import de.pc1up.sentinelmc.util.ItemBuilder;
import de.pc1up.sentinelmc.util.gui.InventoryButton;
import de.pc1up.sentinelmc.util.gui.InventoryGUI;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class ReportlistGUI extends InventoryGUI {
    private List<Report> unresolvedReports;
    private List<Report> resolvedReports;

    @Override
    protected Inventory createInventory() {
        String title = SentinelMC.instance.getMessageUtil().getMessage("reportsystem.reportlist.title");
        return Bukkit.createInventory(null, 54, MiniMessage.miniMessage().deserialize(title));
    }

    @Override
    public void decorate(Player player) {
        decorateUnresolved(player, 1);
    }

    public void decorateResolved(Player player, int page) {
        this.getButtonMap().clear();
        this.getInventory().clear();
        int maxItemsPerPage = 45;
        int startIndex = (page - 1) * maxItemsPerPage;
        int endIndex = Math.min(startIndex + maxItemsPerPage, resolvedReports.size());
        for(int i = startIndex; i < endIndex; i++) {
            Report report = resolvedReports.get(i);

            Map<String, String> placeholders = Map.of(
                    "author", report.getAuthorName(),
                    "reason", report.getReason(),
                    "ago", SentinelMC.instance.getMessageUtil().formatElapsedDuration(report.getTimestamp()),
                    "target", report.getTargetName(),
                    "resolved_by", report.getResolvedBy()
            );

            String itemName = SentinelMC.instance.getMessageUtil().getMessage("reportsystem.reportlist.item.resolved.name", placeholders);
            List<String> lores = SentinelMC.instance.getMessageUtil().getMultiLineMessage("reportsystem.reportlist.item.resolved.lores", placeholders);
            this.addButton(i, new InventoryButton()
                    .creator(p -> new ItemBuilder(Material.PAPER, itemName, lores).build())
                    .consumer(event -> {
                        event.setCancelled(true);
                        player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                        report.setResolved(false);
                        report.save();
                        SentinelMC.instance.getMessageUtil().sendMessage(player, "reportsystem.reportlist.unresolved", Map.of("id", report.getId()));
                    }));
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
                            "<green>←"
                    ).build())
                    .consumer(event -> {
                        event.setCancelled(true);
                        decorateResolved(player, page - 1);
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

        this.addButton(49, new InventoryButton()
                .creator(player1 -> new ItemBuilder(
                        Material.WRITABLE_BOOK,
                        SentinelMC.instance.getMessageUtil().getMessage("reportsystem.reportlist.show_unresolved")
                ).build())
                .consumer(event -> {
                    event.setCancelled(true);
                    decorateUnresolved(player, 1);
                }));

        if (endIndex < resolvedReports.size()) {
            this.addButton(53, new InventoryButton()
                    .creator(player1 -> new ItemBuilder(
                            Material.LIME_DYE,
                            "<green>→"
                    ).build())
                    .consumer(event -> {
                        event.setCancelled(true);
                        decorateResolved(player, page + 1);
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

    public void decorateUnresolved(Player player, int page) {
        this.getButtonMap().clear();
        this.getInventory().clear();
        int maxItemsPerPage = 45;
        int startIndex = (page - 1) * maxItemsPerPage;
        int endIndex = Math.min(startIndex + maxItemsPerPage, unresolvedReports.size());
        for(int i = startIndex; i < endIndex; i++) {
            Report report = unresolvedReports.get(i);

            Map<String, String> placeholders = Map.of(
                    "author", report.getAuthorName(),
                    "reason", report.getReason(),
                    "ago", SentinelMC.instance.getMessageUtil().formatElapsedDuration(report.getTimestamp()),
                    "target", report.getTargetName()
            );

            String itemName = SentinelMC.instance.getMessageUtil().getMessage("reportsystem.reportlist.item.unresolved.name", placeholders);
            List<String> lores = SentinelMC.instance.getMessageUtil().getMultiLineMessage("reportsystem.reportlist.item.unresolved.lores", placeholders);
            this.addButton(i, new InventoryButton()
                    .creator(p -> new ItemBuilder(Material.PAPER, itemName, lores).build())
                    .consumer(event -> {
                        event.setCancelled(true);
                        if(event.isRightClick()) {
                            player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                            SentinelMC.instance.getReportManager().resolveReport(report, player.getName());
                            SentinelMC.instance.getMessageUtil().sendMessage(player, "reportsystem.reportlist.resolved", Map.of("id", report.getId()));
                        } else if (event.isLeftClick()) {
                            Player targetPlayer = Bukkit.getPlayer(report.getTargetName());
                            if(targetPlayer != null) {
                                player.teleport(targetPlayer);
                                SentinelMC.instance.getMessageUtil().sendMessage(player, "misc.teleported", Map.of("player", targetPlayer.getName()));
                            }
                        }
                    }));
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
                            "<green>←"
                    ).build())
                    .consumer(event -> {
                        event.setCancelled(true);
                        decorateUnresolved(player, page - 1);
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

        this.addButton(49, new InventoryButton()
                .creator(player1 -> new ItemBuilder(
                        Material.BOOK,
                        SentinelMC.instance.getMessageUtil().getMessage("reportsystem.reportlist.show_resolved")
                ).build())
                .consumer(event -> {
                    event.setCancelled(true);
                    decorateResolved(player, 1);
                }));

        if (endIndex < unresolvedReports.size()) {
            this.addButton(53, new InventoryButton()
                    .creator(player1 -> new ItemBuilder(
                            Material.LIME_DYE,
                            "<green>→"
                    ).build())
                    .consumer(event -> {
                        event.setCancelled(true);
                        decorateUnresolved(player, page + 1);
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