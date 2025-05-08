package de.pc1up.sentinelmc.util;

import de.pc1up.sentinelmc.SentinelMC;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class MessageUtil {
    private final FileConfiguration configuration;

    public String getMessage(String path) {
        String raw = configuration.getString("messages." + path, "Message not found: " + path);
        String prefix = configuration.getString("messages.prefix", "<red><bold>SentinelMC</bold></red> <dark_gray>»</dark_gray>");

        raw = raw.replace("%PREFIX%", prefix);

        return raw;
    }

    public String getMessage(String path, Map<String, String> placeholders) {
        String raw = configuration.getString("messages." + path, "Message not found: " + path);
        String prefix = configuration.getString("messages.prefix", "<red><bold>SentinelMC</bold></red> <dark_gray>»</dark_gray>");

        raw = raw.replace("%PREFIX%", prefix);

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
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

    public Component buildScreen(String path, Map<String, String> placeholders) {
        List<String> lines = SentinelMC.instance.getMessageUtil().getMultiLineMessage(path, placeholders);
        Component kickMessage = Component.empty();
        for (String line : lines) {
            kickMessage = kickMessage
                    .append(MiniMessage.miniMessage().deserialize(line))
                    .append(Component.newline());
        }
        return kickMessage;
    }

    public String formatDate(long date) {
        if(date == -1) {
            return configuration.getString("messages.duration.never");
        }
        String sdf = configuration.getString("messages.date_format", "dd.MM.yyyy HH:mm z");
        return new SimpleDateFormat(sdf).format(new Date(date * 1000));
    }

    public String formatDateMs(long date) {
        if(date == -1) {
            return configuration.getString("messages.duration.never");
        }
        String sdf = configuration.getString("messages.date_format", "dd.MM.yyyy HH:mm z");
        return new SimpleDateFormat(sdf).format(new Date(date));
    }

    public String formatElapsedDuration(long date) {
        String expiredName = configuration.getString("messages.duration.just_now", "Just now");
        String permaName = configuration.getString("messages.duration.permanent", "Permanent");

        if (date < 0) {
            return permaName;
        }
        long now = Instant.now().getEpochSecond();

        if (date > now) {
            return configuration.getString("messages.duration.in_future", "In the future");
        }

        long seconds = now - date;
        int minutes = 0;
        int hours = 0;
        int days = 0;

        while (seconds >= 60) {
            minutes++;
            seconds -= 60;
        }

        while (minutes >= 60) {
            hours++;
            minutes -= 60;
        }

        while (hours >= 24) {
            days++;
            hours -= 24;
        }

        String dayName = configuration.getString("messages.duration.day");
        String daysName = configuration.getString("messages.duration.days");
        String hourName = configuration.getString("messages.duration.hour");
        String hoursName = configuration.getString("messages.duration.hours");
        String minuteName = configuration.getString("messages.duration.minute");
        String minutesName = configuration.getString("messages.duration.minutes");
        String secondName = configuration.getString("messages.duration.second");
        String secondsName = configuration.getString("messages.duration.seconds");

        List<String> parts = new ArrayList<>();

        if (days > 0) {
            parts.add(days + " " + (days == 1 ? dayName : daysName));
        }
        if (hours > 0) {
            parts.add(hours + " " + (hours == 1 ? hourName : hoursName));
        }
        if (minutes > 0) {
            parts.add(minutes + " " + (minutes == 1 ? minuteName : minutesName));
        }
        if (seconds > 0) {
            parts.add(seconds + " " + (seconds == 1 ? secondName : secondsName));
        }

        if (parts.isEmpty()) {
            return expiredName;
        }

        return String.join(" ", parts.subList(0, Math.min(2, parts.size())));
    }


    public String formatDuration(long date) {
        String expiredName = configuration.getString("messages.duration.expired");
        String permaName = configuration.getString("messages.duration.permanent");
        if (date < 0) {
            return permaName;
        }
        if (Instant.now().getEpochSecond() > date) {
            return expiredName;
        }

        long seconds = date - Instant.now().getEpochSecond();
        int minutes = 0;
        int hours = 0;
        int days = 0;

        while (seconds >= 60) {
            minutes++;
            seconds -= 60;
        }

        while (minutes >= 60) {
            hours++;
            minutes -= 60;
        }

        while (hours >= 24) {
            days++;
            hours -= 24;
        }

        String dayName = configuration.getString("messages.duration.day");
        String daysName = configuration.getString("messages.duration.days");
        String hourName = configuration.getString("messages.duration.hour");
        String hoursName = configuration.getString("messages.duration.hours");
        String minuteName = configuration.getString("messages.duration.minute");
        String minutesName = configuration.getString("messages.duration.minutes");
        String secondName = configuration.getString("messages.duration.second");
        String secondsName = configuration.getString("messages.duration.seconds");

        List<String> parts = new ArrayList<>();

        if (days > 0) {
            parts.add(days + " " + (days == 1 ? dayName : daysName));
        }
        if (hours > 0) {
            parts.add(hours + " " + (hours == 1 ? hourName : hoursName));
        }
        if (minutes > 0) {
            parts.add(minutes + " " + (minutes == 1 ? minuteName : minutesName));
        }
        if (seconds > 0) {
            parts.add(seconds + " " + (seconds == 1 ? secondName : secondsName));
        }
        if (parts.isEmpty()) {
            return "0 " + secondsName;
        }

        return String.join(" ", parts.subList(0, Math.min(2, parts.size())));
    }

    public void sendMessage(CommandSender sender, String path, Map<String, String> placeholders) {
        String raw = configuration.getString("messages." + path, "Message not found: " + path);
        if(raw.trim().equalsIgnoreCase("") || raw.isEmpty() || raw.isBlank()) {
            return;
        }
        sender.sendRichMessage(getMessage(path, placeholders));
    }

    public void sendMessage(CommandSender sender, String path) {
        String raw = configuration.getString("messages." + path, "Message not found: " + path);
        if(raw.trim().equalsIgnoreCase("") || raw.isEmpty() || raw.isBlank()) {
            return;
        }
        sender.sendRichMessage(getMessage(path));
    }

    public void sendRestrictedMessage(String permission, String path, Map<String, String> placeholders) {
        String raw = configuration.getString("messages." + path, "Message not found: " + path);
        if(raw.trim().equalsIgnoreCase("") || raw.isEmpty() || raw.isBlank()) {
            return;
        }
        for(Player online : Bukkit.getOnlinePlayers()) {
            if(online.hasPermission(permission)) {
                sendMessage(online, path, placeholders);
            }
        }
    }

}
