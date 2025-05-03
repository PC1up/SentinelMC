package de.pc1up.sentinelmc.util.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class GUIListener implements Listener {

    private final GUIManager manager;

    public GUIListener(GUIManager guiManager) {
        this.manager = guiManager;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        this.manager.handleClick(event);
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        this.manager.handleOpen(event);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        this.manager.handleClose(event);
    }

}
