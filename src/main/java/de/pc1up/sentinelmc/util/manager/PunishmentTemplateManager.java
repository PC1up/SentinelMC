package de.pc1up.sentinelmc.util.manager;

import de.pc1up.sentinelmc.enums.SanctionaryActions;
import de.pc1up.sentinelmc.objects.PunishmentTemplate;
import de.pc1up.sentinelmc.util.TimeParser;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PunishmentTemplateManager {

    @Getter
    private final Map<String, PunishmentTemplate> templates = new HashMap<>();
    private FileConfiguration config;

    public PunishmentTemplateManager(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "punishment-templates.yml");
        if (!file.exists()) {
            plugin.saveResource("punishment-templates.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration(file);
        loadTemplates();
    }

    private void loadTemplates() {
        ConfigurationSection section = config.getConfigurationSection("templates");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            try {
                String actionStr = section.getString(key + ".action");
                String duration = section.getString(key + ".duration", "-1");
                String reason = section.getString(key + ".reason");
                String note = section.getString(key + ".note", "");
                String permission = section.getString(key + ".permission", "");

                if (actionStr == null || reason == null) continue;

                SanctionaryActions action = SanctionaryActions.valueOf(actionStr.toUpperCase());

                if (action.isTemporary() && TimeParser.parseTimeToDuration(duration, null) == 0L) {
                    Bukkit.getLogger().warning("[PunishmentTemplates] Invalid duration in template: " + key);
                    continue;
                }

                PunishmentTemplate template = new PunishmentTemplate(action, duration, reason, note, permission);
                templates.put(key.toLowerCase(), template);

            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().warning("[PunishmentTemplates] Invalid action in template: " + key);
            }
        }
    }

    public void reload() {
        templates.clear();
        File file = new File(JavaPlugin.getProvidingPlugin(getClass()).getDataFolder(), "punishment-templates.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
        loadTemplates();
    }


    public PunishmentTemplate getTemplate(String key) {
        return templates.get(key.toLowerCase());
    }

    public Set<String> getAvailableTemplateKeys() {
        return templates.keySet();
    }
}

