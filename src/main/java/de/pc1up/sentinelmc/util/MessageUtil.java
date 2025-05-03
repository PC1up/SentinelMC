package de.pc1up.sentinelmc.util;

import com.mongodb.client.model.geojson.LineString;
import lombok.AllArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class MessageUtil {
    private final FileConfiguration configuration;

    public String getMessage(String path, Map<String, String> placeholders) {
        String raw = configuration.getString("messages." + path, "Message not found: " + path);
        String prefix = configuration.getString("messages.prefix", "<red><bold>SentinelMC</bold></red> <dark_gray>»</dark_gray>");

        raw = raw.replace("%PREFIX%", prefix);

        for(Map.Entry<String, String> entry : placeholders.entrySet()) {
            raw = raw.replace("%" + entry.getKey() + "%", entry.getValue());
        }

        return raw;
    }

    public List<String> getMultiLineMessage(String path, Map<String, String> placeholders) {
        List<String> rawList = configuration.getStringList("messages." + path);

        if (rawList.isEmpty()) {
            rawList = List.of("Message not found: " + path);
        }

        String prefix = configuration.getString("messages.prefix", "<red><bold>SentinelMC</bold></red> <dark_gray>»</dark_gray>");

        List<String> parsed = new ArrayList<>();
        for (String line : rawList) {
            line = line.replace("%PREFIX%", prefix);
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                line = line.replace("%" + entry.getKey() + "%", entry.getValue());
            }

            parsed.add(line);
        }
        return parsed;
    }

    public void sendMessage(CommandSender sender, String path, Map<String, String> placeholders){
        sender.sendRichMessage(getMessage(path, placeholders));
    }

}
