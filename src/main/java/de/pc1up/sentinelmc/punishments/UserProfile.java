package de.pc1up.sentinelmc.punishments;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class UserProfile {
    private final UUID uuid;
    private String name;
    private String lastIp;
    private int points;
}
