package de.pc1up.sentinelmc.objects;

import de.pc1up.sentinelmc.SentinelMC;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Report {
    private final String id;
    private String targetName;
    private String authorName;
    private long timestamp;
    private String reason;
    private boolean resolved;
    private String resolvedBy;

    public void save() {
        SentinelMC.instance.getDatabaseProvider().saveReport(this);
    }
}
