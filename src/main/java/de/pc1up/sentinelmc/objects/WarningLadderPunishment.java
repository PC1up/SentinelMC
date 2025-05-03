package de.pc1up.sentinelmc.objects;

import de.pc1up.sentinelmc.enums.SanctionaryActions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class WarningLadderPunishment {
    private SanctionaryActions action;
    private int requiredPoints;
    private String durationName;
    private long duration;
    private String reason;
}
