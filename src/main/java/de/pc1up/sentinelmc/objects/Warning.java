package de.pc1up.sentinelmc.objects;

import de.pc1up.sentinelmc.SentinelMC;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class Warning {
    private final String id;
    private UUID targetUUID;
    private String authorName;
    private long timestamp;
    private String reason;
    private int points;

    public void save() {
        SentinelMC.instance.getDatabaseProvider().saveWarning(this);
    }
}
