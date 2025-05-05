package de.pc1up.sentinelmc.objects;

import de.pc1up.sentinelmc.enums.SanctionaryActions;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PunishmentTemplate {
    private final SanctionaryActions action;
    private final String duration;
    private final String reason;
    private final String note;
    private final String permission;
}

