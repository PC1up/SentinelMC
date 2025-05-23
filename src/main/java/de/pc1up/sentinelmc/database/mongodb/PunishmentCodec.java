package de.pc1up.sentinelmc.database.mongodb;

import de.pc1up.sentinelmc.objects.Punishment;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.util.UUID;

public class PunishmentCodec implements Codec<Punishment> {

    @Override
    public Punishment decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        reader.readObjectId();
        String id = reader.readString("id");
        UUID targetUUID = UUID.fromString(reader.readString("targetUUID"));
        String authorName = reader.readString("authorName");
        String reason = reader.readString("reason");
        long startTime = reader.readInt64("startTime");
        long endTime = reader.readInt64("endTime");
        String duration = reader.readString("duration");
        String note = reader.readString("note");
        boolean revoked = reader.readBoolean("revoked");
        String revokedBy = reader.readString("revokedBy");
        String revokeReason = reader.readString("revokeReason");
        boolean ban = reader.readBoolean("ban");
        reader.readEndDocument();
        return new Punishment(id, targetUUID, authorName, reason, startTime, endTime, duration, note, revoked, revokedBy, revokeReason, ban);
    }

    @Override
    public void encode(BsonWriter writer, Punishment punishment, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString("id", punishment.getId());
        writer.writeString("targetUUID", punishment.getTargetUUID().toString());
        writer.writeString("authorName", punishment.getAuthorName());
        writer.writeString("reason", punishment.getReason());
        writer.writeInt64("startTime", punishment.getStartTime());
        writer.writeInt64("endTime", punishment.getEndTime());
        writer.writeString("duration", punishment.getDuration());
        writer.writeString("note", punishment.getNote());
        writer.writeBoolean("revoked", punishment.isRevoked());
        writer.writeString("revokedBy", punishment.getRevokedBy());
        writer.writeString("revokeReason", punishment.getRevokeReason());
        writer.writeBoolean("ban", punishment.isBan());
        writer.writeEndDocument();
    }

    @Override
    public Class<Punishment> getEncoderClass() {
        return Punishment.class;
    }
}
