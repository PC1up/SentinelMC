package de.pc1up.sentinelmc.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class ItemBuilder {
    private Material material;
    private String displayName;
    private List<String> lore = new ArrayList<>();
    private List<Component> componentLore = new ArrayList<>();
    private String skullOwner;
    private Component componentDisplayName;

    public ItemBuilder(Material material) {
        this.material = material;
    }

    public ItemBuilder(Material material, String displayName) {
        this.material = material;
        this.displayName = displayName;
    }

    public ItemBuilder(Material material, String displayName, List<String> lore) {
        this.material = material;
        this.displayName = displayName;
        this.lore = lore;
    }

    public ItemBuilder(Material material, String displayName, String... lores) {
        this.material = material;
        this.displayName = displayName;
        this.lore.addAll(Arrays.asList(lores));
    }

    public ItemBuilder(Material material, String displayName, Component... lores) {
        this.material = material;
        this.displayName = displayName;
        this.componentLore.addAll(Arrays.asList(lores));
    }

    public ItemBuilder addLore(String lore) {
        this.lore.add(lore);
        return this;
    }

    public ItemBuilder setSkullOwner(String owner) {
        this.skullOwner = owner;
        return this;
    }

    public ItemBuilder setComponentDisplayName(Component component) {
        this.componentDisplayName = component;
        return this;
    }

    public ItemStack build() {
        ItemStack itemStack = new ItemStack(material);
        if (skullOwner != null) {
            SkullMeta itemMeta = (SkullMeta) itemStack.getItemMeta();
            itemMeta.setOwningPlayer(Bukkit.getPlayer(skullOwner));
            if (displayName != null) {
                itemMeta.displayName(MiniMessage.miniMessage().deserialize(displayName).decoration(TextDecoration.ITALIC, false));
            }
            if (lore != null) {
                if (!lore.isEmpty()) {
                    List<Component> realLore = new ArrayList<>();
                    for (String s : lore) {
                        realLore.add(MiniMessage.miniMessage().deserialize(s).decoration(TextDecoration.ITALIC, false));
                    }
                    itemMeta.lore(realLore);
                }
            }
            itemStack.setItemMeta(itemMeta);
        } else {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (componentDisplayName != null) {
                itemMeta.displayName(componentDisplayName);
            } else {
                if (displayName != null) {
                    itemMeta.displayName(MiniMessage.miniMessage().deserialize(displayName).decoration(TextDecoration.ITALIC, false));
                }
            }
            if (componentLore != null) {
                if (!componentLore.isEmpty()) {
                    List<Component> realLore = new ArrayList<>();
                    for (Component component : componentLore) {
                        realLore.add(
                                component.decoration(TextDecoration.ITALIC, false)
                        );
                    }
                    itemMeta.lore(realLore);
                } else {
                    if (lore != null) {
                        if (!lore.isEmpty()) {
                            List<Component> realLore = new ArrayList<>();
                            for (String s : lore) {
                                realLore.add(MiniMessage.miniMessage().deserialize(s).decoration(TextDecoration.ITALIC, false));
                            }
                            itemMeta.lore(realLore);
                        }
                    }
                }
            } else {
                if (lore != null) {
                    if (!lore.isEmpty()) {
                        List<Component> realLore = new ArrayList<>();
                        for (String s : lore) {
                            realLore.add(MiniMessage.miniMessage().deserialize(s).decoration(TextDecoration.ITALIC, false));
                        }
                        itemMeta.lore(realLore);
                    }
                }
            }
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }
}
