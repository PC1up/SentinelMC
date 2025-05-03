package de.pc1up.sentinelmc.util.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GUIManager {
    private final Map<Inventory, InventoryHandler> activeInventories = new HashMap<>();
    public static final ArrayList<Player> activeInventoryPlayers = new ArrayList<>();

    public void openGUI(InventoryGUI inventoryGUI, Player player) {
        this.registerHandledInventory(inventoryGUI.getInventory(), inventoryGUI);
        player.openInventory(inventoryGUI.getInventory());
        activeInventoryPlayers.add(player);
    }

    public void registerHandledInventory(Inventory inventory, InventoryHandler handler) {
        this.activeInventories.put(inventory, handler);
    }

    public void unregisterInventory(Inventory inventory) {
        this.activeInventories.remove(inventory);
    }

    public void handleClick(InventoryClickEvent event) {
        InventoryHandler handler = this.activeInventories.get(event.getInventory());
        if (handler != null) {
            handler.onClick(event);
        }
    }

    public void handleOpen(InventoryOpenEvent event) {
        InventoryHandler handler = this.activeInventories.get(event.getInventory());
        if (handler != null) {
            handler.onOpen(event);
        }
    }

    public void handleClose(InventoryCloseEvent event) {
        InventoryHandler handler = this.activeInventories.get(event.getInventory());
        if (handler != null) {
            handler.onClose(event);
            this.unregisterInventory(event.getInventory());
            activeInventoryPlayers.remove((Player) event.getPlayer());
        }
    }
}
