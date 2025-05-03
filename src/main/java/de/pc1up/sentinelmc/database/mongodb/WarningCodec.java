package de.pc1up.sentinelmc.database.mongodb;

import de.pc1up.sentinelmc.objects.Warning;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.util.UUID;

/*
    private final String id;
    private UUID targetUUID;
    private String authorName;
    private long timestamp;
    private String reason;
    private int points;
 */
public class WarningCodec implements Codec<Warning> {
    @Override
    public Warning decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        reader.readObjectId();
        String id = reader.readString("id");
        UUID targetUUID = UUID.fromString(reader.readString("targetUUID"));
        String authorName = reader.readString("authorName");
        long timestamp = reader.readInt64("timestamp");
        String reason = reader.readString("reason");
        int points = reader.readInt32("points");
        reader.readEndDocument();
        return new Warning(id, targetUUID, authorName, timestamp, reason, points);
    }

    @Override
    public void encode(BsonWriter writer, Warning warning, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString("id", warning.getId());
        writer.writeString("targetUUID", warning.getTargetUUID().toString());
        writer.writeString("authorName", warning.getAuthorName());
        writer.writeInt64("timestamp", warning.getTimestamp());
        writer.writeString("reason", warning.getReason());
        writer.writeInt32("points", warning.getPoints());
        writer.writeEndDocument();
    }

    @Override
    public Class<Warning> getEncoderClass() {
        return Warning.class;
    }
}
