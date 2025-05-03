package de.pc1up.sentinelmc.objects;

import de.pc1up.sentinelmc.SentinelMC;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
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

    public void save() {
        SentinelMC.instance.getDatabaseProvider().savePunishment(this);
    }

    public String getType() {
        if(ban) {
            if(endTime == -1){
                return SentinelMC.instance.getConfiguration().getString("messages.types.ban", "Ban");
            } else{
                return SentinelMC.instance.getConfiguration().getString("messages.types.tempban", "Temp-Ban");
            }
        } else {
            if(endTime == -1){
                return SentinelMC.instance.getConfiguration().getString("messages.types.mute", "Mute");
            } else {
                return SentinelMC.instance.getConfiguration().getString("messages.types.tempmute", "Temp-Mute");
            }
        }
    }

    public boolean isActive(){
        if(revoked) return false;
        if(endTime == -1) return true;
        return Instant.now().getEpochSecond() <= endTime;
    }

    public boolean isExpired() {
        if(endTime == -1) return false;
        return Instant.now().getEpochSecond() > endTime;
    }
}
