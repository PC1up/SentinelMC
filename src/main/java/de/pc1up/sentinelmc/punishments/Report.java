package de.pc1up.sentinelmc.punishments;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class Report {
    private final String id;
    private String targetName;
    private String authorName;
    private long timestamp;
    private String reason;
    private boolean resolved;
    private String resolvedBy;
}
