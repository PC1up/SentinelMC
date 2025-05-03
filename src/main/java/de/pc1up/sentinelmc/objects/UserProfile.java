package de.pc1up.sentinelmc.objects;

import de.pc1up.sentinelmc.SentinelMC;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class UserProfile {
    private final UUID uuid;
    @Setter
    private String name;
    @Setter
    private String lastIp;
    @Setter
    private int points;

    public void addPoints(int amount) {
        this.points += amount;
    }

    public void removePoints(int amount) {
        if (amount > this.points) {
            this.points = 0;
        } else {
            this.points -= amount;
        }
    }

    public void save() {
        SentinelMC.instance.getDatabaseProvider().saveUser(this);
    }
}
