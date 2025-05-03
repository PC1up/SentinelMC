package de.pc1up.sentinelmc.util;

import de.pc1up.sentinelmc.SentinelMC;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

public class TimeParser {
    public static ParsedTime parseTime(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }

        String regex = "(\\d+)([a-zA-Z]+)";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(input);

        if (matcher.matches()) {
            long value = Long.parseLong(matcher.group(1));
            String unit = matcher.group(2).toLowerCase();

            return switch (unit) {
                case "s" -> new ParsedTime(value, TimeUnit.SECONDS);
                case "m" -> new ParsedTime(value, TimeUnit.MINUTES);
                case "h" -> new ParsedTime(value, TimeUnit.HOURS);
                case "d" -> new ParsedTime(value, TimeUnit.DAYS);
                case "w" -> new ParsedTime(value * 7, TimeUnit.DAYS);
                case "mon" -> new ParsedTime(value * 30, TimeUnit.DAYS);
                case "y" -> new ParsedTime(value * 365, TimeUnit.DAYS);
                default -> null;
            };
        }

        return null;
    }

    public static long parseTimeToDuration(String input, @Nullable CommandSender sender) {
        ParsedTime parsedTime = parseTime(input);
        if (parsedTime == null) {
            if (sender != null) {
                String invalidDuration = SentinelMC.instance.getMessageUtil().getMessage("invalid_duration");
                sender.sendRichMessage(invalidDuration);
            }
            SentinelMC.instance.getLogger().warning("[SentinelMC] WARNING: Attempt to parse invalid duration: " + input);
            return 0L;
        }
        return parsedTime.unit().toSeconds(parsedTime.value());
    }

    public static long parseTimeToDurationMs(String input, @Nullable CommandSender sender) {
        ParsedTime parsedTime = parseTime(input);
        if (parsedTime == null) {
            if (sender != null) {
                String invalidDuration = SentinelMC.instance.getMessageUtil().getMessage("invalid_duration");
                sender.sendRichMessage(invalidDuration);
            }
            SentinelMC.instance.getLogger().warning("[SentinelMC] WARNING: Attempt to parse invalid duration: " + input);
            return 0L;
        }
        return parsedTime.unit().toMillis(parsedTime.value());
    }

    public record ParsedTime(long value, TimeUnit unit) {
    }
}
