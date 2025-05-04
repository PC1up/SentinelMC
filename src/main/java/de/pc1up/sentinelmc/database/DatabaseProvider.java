package de.pc1up.sentinelmc.database;

import de.pc1up.sentinelmc.objects.Punishment;
import de.pc1up.sentinelmc.objects.Report;
import de.pc1up.sentinelmc.objects.UserProfile;
import de.pc1up.sentinelmc.objects.Warning;

import java.util.List;
import java.util.UUID;

public interface DatabaseProvider {
    void initialize();

    void savePunishment(Punishment punishment);

    List<Punishment> getPunishments(UUID player);

    Punishment getPunishment(String id);

    void saveUser(UserProfile userProfile);

    UserProfile getProfile(UUID uuid);

    UserProfile getProfile(String name);

    void saveReport(Report report);

    Report getReport(String id);

    List<Report> getReportsAgainst(String name);

    List<Report> getReportsBy(String name);

    List<Report> getUnresolvedReports();

    List<Report> getResolvedReports();

    void deleteOldResolvedReports(long cutoffTime);

    void saveWarning(Warning warning);

    Warning getWarning(String id);

    List<Warning> getWarnings(UUID uuid);

    Warning getLatestWarning(UUID uuid);

    void deleteWarning(String id);

    void close();
}
