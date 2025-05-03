package de.pc1up.sentinelmc.util.manager;

import de.pc1up.sentinelmc.SentinelMC;
import de.pc1up.sentinelmc.objects.Report;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReportManager {
    public static final HashMap<UUID, Long> cooldown = new HashMap<>();

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

    public Report createAndSubmitReport(String targetName, String authorName, String reason, @Nullable Player author) {
        if (author != null) {
            if (cooldown.containsKey(author.getUniqueId())) {
                long cooldownUntil = cooldown.get(author.getUniqueId());
                if (Instant.now().getEpochSecond() < cooldownUntil) {
                    SentinelMC.instance.getMessageUtil().sendMessage(author, "reportsystem.report.cooldown");
                    return null;
                }
            }
        }
        String id = createId();
        int tries = 0;
        while (SentinelMC.instance.getDatabaseProvider().getReport(id) != null) {
            if (tries == 25) {
                if (author != null) {
                    author.sendRichMessage("<red>A fatal error occured while creating the report.");
                }
                SentinelMC.instance.getLogger().severe("[SentinelMC] SEVERE: Unable to find an unused report id! Giving up after 25 tries.");
                return null;
            }
            id = createId();
            tries++;
        }
        Report report = new Report(
                id,
                targetName,
                authorName,
                Instant.now().getEpochSecond(),
                reason,
                false,
                "-"
        );
        SentinelMC.instance.getDatabaseProvider().saveReport(report);
        if(author != null){
            SentinelMC.instance.getMessageUtil().sendMessage(author, "reportsystem.report.success");
            int cooldownSeconds = SentinelMC.instance.getConfiguration().getInt("reports.cooldown", 300);
            cooldown.put(author.getUniqueId(), Instant.now().getEpochSecond() + cooldownSeconds);
        }
        Map<String, String> placeholder = Map.of(
                "target", targetName,
                "author", authorName,
                "reason", reason
        );
        SentinelMC.instance.getMessageUtil().sendRestrictedMessage("reportsystem.notify", "reportsystem.report.broadcast", placeholder);
        return report;
    }

    public void resolveReport(Report report, String by) {
        report.setResolved(true);
        report.setResolvedBy(by);
        report.save();
    }
}
