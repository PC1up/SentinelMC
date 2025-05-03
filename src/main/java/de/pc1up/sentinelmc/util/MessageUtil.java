package de.pc1up.sentinelmc.util;

import lombok.AllArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;

@AllArgsConstructor
public class MessageUtil {
    private final FileConfiguration configuration;

    public String getMessage(String path, Map<String, String> placeholders) {
        String raw = configuration.getString("messages." + path, "Message not found: " + path);

        for(Map.Entry<String, String> entry : placeholders.entrySet()) {
            raw = raw.replace("%" + entry.getKey() + "%", entry.getValue());
        }

        return raw;
    }

    public void sendMessage(CommandSender sender, String path, Map<String, String> placeholders){
        sender.sendRichMessage(getMessage(path, placeholders));
    }

}
