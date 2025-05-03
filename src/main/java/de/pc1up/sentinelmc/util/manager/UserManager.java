package de.pc1up.sentinelmc.util.manager;

import de.pc1up.sentinelmc.SentinelMC;
import de.pc1up.sentinelmc.database.DatabaseProvider;
import de.pc1up.sentinelmc.objects.UserProfile;

import java.util.UUID;

public class UserManager {

    public UserProfile getOrCreate(UUID uuid, String name, String ip) {
        DatabaseProvider provider = SentinelMC.instance.getDatabaseProvider();
        if (provider.getProfile(uuid) == null) {
            UserProfile userProfile = new UserProfile(uuid, name, ip, 0);
            provider.saveUser(userProfile);
            return userProfile;
        } else {
            UserProfile profile = provider.getProfile(uuid);
            if (!profile.getName().equalsIgnoreCase(name)) { // update name
                profile.setName(name);
                profile.save();
            }
            return profile;
        }
    }

}
