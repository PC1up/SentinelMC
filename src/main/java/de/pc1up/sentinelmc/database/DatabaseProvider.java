package de.pc1up.sentinelmc.database;

import de.pc1up.sentinelmc.punishments.Punishment;
import de.pc1up.sentinelmc.punishments.UserProfile;

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
    void close();
}
