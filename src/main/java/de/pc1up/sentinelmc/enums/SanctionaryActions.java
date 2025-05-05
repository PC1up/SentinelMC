package de.pc1up.sentinelmc.enums;

public enum SanctionaryActions {
    KICK,
    TEMPMUTE,
    MUTE,
    TEMPBAN,
    BAN;

    public boolean isTemporary() {
        return this == TEMPBAN || this == TEMPMUTE;
    }
}
