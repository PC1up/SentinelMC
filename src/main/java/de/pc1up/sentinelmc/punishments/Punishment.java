package de.pc1up.sentinelmc.punishments;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class Punishment {
    private final String id;
    private UUID targetUUID;
    private String authorName;
    private String reason;
    private long startTime;
    private long endTime;
    private String duration;
    private String note;
    private boolean revoked;
    private String revokedBy;
    private String revokeReason;
    private boolean ban;
}
