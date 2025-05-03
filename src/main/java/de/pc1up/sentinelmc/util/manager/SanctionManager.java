package de.pc1up.sentinelmc.util.manager;

import de.pc1up.sentinelmc.SentinelMC;
import de.pc1up.sentinelmc.enums.SanctionaryActions;
import de.pc1up.sentinelmc.objects.Punishment;
import de.pc1up.sentinelmc.objects.UserProfile;
import de.pc1up.sentinelmc.objects.Warning;
import de.pc1up.sentinelmc.objects.WarningLadderPunishment;
import de.pc1up.sentinelmc.util.TimeParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SanctionManager {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int ID_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

    private String createId() {
        StringBuilder sb = new StringBuilder(ID_LENGTH);
        for (int i = 0; i < ID_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }

    public Punishment createPunishment(UUID targetUUID, String authorName, boolean ban, String reason, long endTime, String duration, String note) {
        String id = createId();
        int tries = 0;
        while (SentinelMC.instance.getDatabaseProvider().getPunishment(id) != null) {
            if (tries == 25) {
                Player author = Bukkit.getPlayer(authorName);
                if (author != null) {
                    author.sendRichMessage("<red>A fatal error occured while issuing the punishment.");
                }
                SentinelMC.instance.getLogger().severe("[SentinelMC] SEVERE: Unable to find an unused punishment id! Giving up after 25 tries.");
                return null;
            }
            id = createId();
            tries++;
        }
        return new Punishment(
                id,
                targetUUID,
                authorName,
                reason,
                Instant.now().getEpochSecond(),
                endTime,
                duration,
                note,
                false,
                "",
                "",
                ban
        );
    }

    public void revokePunishment(Punishment punishment, String by, String reason) {
        punishment.setRevoked(true);
        punishment.setRevokedBy(by);
        punishment.setRevokeReason(reason);
        punishment.save();
    }

    public Punishment getActiveBan(UUID uuid){
        List<Punishment> allPunishments = SentinelMC.instance.getDatabaseProvider().getPunishments(uuid);
        for(Punishment punishment : allPunishments) {
            if(punishment.isBan()){
                if(punishment.isActive()){
                    return punishment;
                }
            }
        }
        return null;
    }

    public Punishment getActiveMute(UUID uuid){
        List<Punishment> allPunishments = SentinelMC.instance.getDatabaseProvider().getPunishments(uuid);
        for(Punishment punishment : allPunishments) {
            if(!punishment.isBan()){
                if(punishment.isActive()){
                    return punishment;
                }
            }
        }
        return null;
    }

    public void performPunishment(Punishment punishment, boolean insert) {
        if (insert) SentinelMC.instance.getDatabaseProvider().savePunishment(punishment);
        Player targetPlayer = Bukkit.getPlayer(punishment.getTargetUUID());
        if (targetPlayer != null) {
            Map<String, String> placeholders = new java.util.HashMap<>(Map.of(
                    "reason", punishment.getReason(),
                    "author", punishment.getAuthorName(),
                    "id", punishment.getId(),
                    "note", punishment.getNote()
            ));
            if (punishment.isBan()) {
                if (punishment.getEndTime() == -1) { // perma
                    List<String> lines = SentinelMC.instance.getMessageUtil().getMultiLineMessage("bansystem.ban.screen", placeholders);
                    Component kickMessage = Component.empty();
                    for (String line : lines) {
                        kickMessage = kickMessage
                                .append(MiniMessage.miniMessage().deserialize(line))
                                .append(Component.newline());
                    }
                    targetPlayer.kick(kickMessage);
                } else {
                    placeholders.put("duration", SentinelMC.instance.getMessageUtil().formatDuration(punishment.getEndTime()));
                    placeholders.put("end_date", SentinelMC.instance.getMessageUtil().formatDate(punishment.getEndTime()));

                    List<String> lines = SentinelMC.instance.getMessageUtil().getMultiLineMessage("bansystem.tempban.screen", placeholders);
                    Component kickMessage = Component.empty();
                    for (String line : lines) {
                        kickMessage = kickMessage
                                .append(MiniMessage.miniMessage().deserialize(line))
                                .append(Component.newline());
                    }
                    targetPlayer.kick(kickMessage);

                }
            } else {
                if (punishment.getEndTime() == -1) {
                    List<String> muteMessage = SentinelMC.instance.getMessageUtil().getMultiLineMessage("mutesystem.mute.screen", placeholders);
                    muteMessage.forEach(targetPlayer::sendRichMessage);
                } else {
                    placeholders.put("duration", SentinelMC.instance.getMessageUtil().formatDuration(punishment.getEndTime()));
                    placeholders.put("end_date", SentinelMC.instance.getMessageUtil().formatDate(punishment.getEndTime()));
                    List<String> muteMessage = SentinelMC.instance.getMessageUtil().getMultiLineMessage("mutesystem.tempmute.screen", placeholders);
                    muteMessage.forEach(targetPlayer::sendRichMessage);
                }
            }
        }
    }

    public Warning createWarning(UUID targetUUID, String authorName, String reason, int points) {
        String id = createId();
        int tries = 0;
        while (SentinelMC.instance.getDatabaseProvider().getWarning(id) != null) {
            if (tries == 25) {
                Player author = Bukkit.getPlayer(authorName);
                if (author != null) {
                    author.sendRichMessage("<red>A fatal error occured while issuing the warning.");
                }
                SentinelMC.instance.getLogger().severe("[SentinelMC] SEVERE: Unable to find an unused warning id! Giving up after 25 tries.");
                return null;
            }
            id = createId();
            tries++;
        }
        return new Warning(id, targetUUID, authorName, Instant.now().getEpochSecond(), reason, points);
    }

    public void performWarning(Warning warning, @Nullable UserProfile userProfile) {
        SentinelMC.instance.getDatabaseProvider().saveWarning(warning);
        if (warning.getPoints() > 0 && userProfile != null) {
            userProfile.addPoints(warning.getPoints());
            userProfile.save();
        }
        Player targetPlayer = Bukkit.getPlayer(warning.getTargetUUID());
        if (targetPlayer != null) {
            Map<String, String> placeholders = new java.util.HashMap<>(Map.of(
                    "reason", warning.getReason(),
                    "author", warning.getAuthorName(),
                    "id", warning.getId(),
                    "points", String.valueOf(warning.getPoints())
            ));
            List<String> warnMessage = SentinelMC.instance.getMessageUtil().getMultiLineMessage("warnsystem.warn.screen", placeholders);
            warnMessage.forEach(targetPlayer::sendRichMessage);
        }
    }

    public void removeWarning(Warning warning, @Nullable UserProfile userProfile) {
        SentinelMC.instance.getDatabaseProvider().deleteWarning(warning.getId());
        if (warning.getPoints() > 0 && userProfile != null) {
            userProfile.removePoints(warning.getPoints());
            userProfile.save();
        }
    }

    public @Nullable WarningLadderPunishment getWarningPunishment(UserProfile userProfile, Warning warning) {
        FileConfiguration configuration = SentinelMC.instance.getConfiguration();
        if (!configuration.getBoolean("warn-ladder.enabled")) return null;
        int currentPoints = userProfile.getPoints();
        ConfigurationSection ladderSection = configuration.getConfigurationSection("warn-ladder");
        if (ladderSection == null) return null;

        int maxApplicable = -1;

        for (String key : ladderSection.getKeys(false)) {
            if (key.equalsIgnoreCase("enabled")) continue;

            try {
                int points = Integer.parseInt(key);
                if (points <= currentPoints && points > maxApplicable) {
                    maxApplicable = points;
                }
            } catch (NumberFormatException ignored) {

            }
        }

        if (maxApplicable == -1) return null;

        ConfigurationSection entry = ladderSection.getConfigurationSection(
                String.valueOf(maxApplicable)
        );
        if (entry == null) return null;

        String actionString = entry.getString("action", "").toUpperCase();
        if (!EnumUtils.isValidEnum(SanctionaryActions.class, actionString)) {
            return null;
        }
        SanctionaryActions action = SanctionaryActions.valueOf(actionString);
        String reason = entry.getString("reason", "Warn Points Exceeded.")
                .replace("%last-reason%", warning.getReason());
        String durationName = entry.getString("duration", null);
        long duration = 0;
        if (durationName != null) {
            duration = TimeParser.parseTimeToDuration(durationName, null);
        }

        return new WarningLadderPunishment(action, maxApplicable, durationName, duration, reason);
    }

    public void issueWarningPunishment(WarningLadderPunishment ladderPunishment, UUID targetUUID) {
        if (ladderPunishment.getAction() == SanctionaryActions.KICK) {
            Map<String, String> placeholders = new java.util.HashMap<>(Map.of(
                    "reason", ladderPunishment.getReason(),
                    "author", "CONSOLE"
            ));
            List<String> lines = SentinelMC.instance.getMessageUtil().getMultiLineMessage("bansystem.kick.screen", placeholders);
            Component kickMessage = Component.empty();
            for (String line : lines) {
                kickMessage = kickMessage
                        .append(MiniMessage.miniMessage().deserialize(line))
                        .append(Component.newline());
            }
            Player targetPlayer = Bukkit.getPlayer(targetUUID);
            if(targetPlayer != null){
                SentinelMC.instance.getMessageUtil().sendRestrictedMessage("bansystem.notify", "bansystem.kick.broadcast", Map.of(
                        "player", targetPlayer.getName(),
                        "author", "CONSOLE",
                        "reason", ladderPunishment.getReason()
                ));
                targetPlayer.kick(kickMessage);
            }
        } else {
            long endTime;
            if(ladderPunishment.getAction() == SanctionaryActions.TEMPBAN || ladderPunishment.getAction() == SanctionaryActions.TEMPMUTE) {
                endTime = Instant.now().getEpochSecond() + ladderPunishment.getDuration();
            } else {
                endTime = -1;
            }
            Punishment punishment = createPunishment(targetUUID, "CONSOLE", true, ladderPunishment.getReason(), endTime, ladderPunishment.getDurationName(), "Warning Ladder Punishment");
            broadcastWarningPunishment(ladderPunishment.getAction(), punishment);
            performPunishment(punishment, true);
        }
    }

    private void broadcastWarningPunishment(SanctionaryActions action, Punishment punishment) {
        UserProfile targetProfile = SentinelMC.instance.getDatabaseProvider().getProfile(punishment.getTargetUUID());
        String targetName = "Unknown";
        if(targetProfile != null) {
            targetName = targetProfile.getName();
        }
        switch (action) {
            case BAN -> {
                SentinelMC.instance.getMessageUtil().sendRestrictedMessage("bansystem.notify", "bansystem.ban.broadcast", Map.of(
                        "player", targetName,
                        "author", "CONSOLE",
                        "reason", punishment.getReason()
                ));
            }
            case TEMPBAN -> {
                SentinelMC.instance.getMessageUtil().sendRestrictedMessage("bansystem.notify", "bansystem.tempban.broadcast", Map.of(
                        "player", targetName,
                        "author", "CONSOLE",
                        "reason", punishment.getReason(),
                        "duration", SentinelMC.instance.getMessageUtil().formatDuration(punishment.getEndTime())
                ));
            }
            case MUTE -> {
                SentinelMC.instance.getMessageUtil().sendRestrictedMessage("mutesystem.notify", "mutesystem.mute.broadcast", Map.of(
                        "player", targetName,
                        "author", "CONSOLE",
                        "reason", punishment.getReason()
                ));
            }
            case TEMPMUTE -> {
                SentinelMC.instance.getMessageUtil().sendRestrictedMessage("mutesystem.notify", "mutesystem.tempmute.broadcast", Map.of(
                        "player", targetName,
                        "author", "CONSOLE",
                        "reason", punishment.getReason(),
                        "duration", SentinelMC.instance.getMessageUtil().formatDuration(punishment.getEndTime())
                ));
            }
        }
    }

}
