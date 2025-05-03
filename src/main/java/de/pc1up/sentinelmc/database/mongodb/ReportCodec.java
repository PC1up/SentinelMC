package de.pc1up.sentinelmc.database.mongodb;

import de.pc1up.sentinelmc.objects.Report;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class ReportCodec implements Codec<Report> {
    @Override
    public Report decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        reader.readObjectId();
        String id = reader.readString("id");
        String targetName = reader.readString("targetName");
        String authorName = reader.readString("authorName");
        long timestamp = reader.readInt64("timestamp");
        String reason = reader.readString("reason");
        boolean resolved = reader.readBoolean("resolved");
        String resolvedBy = reader.readString("resolvedBy");
        reader.readEndDocument();
        return new Report(id, targetName, authorName, timestamp, reason, resolved, resolvedBy);
    }

    @Override
    public void encode(BsonWriter writer, Report report, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString("id", report.getId());
        writer.writeString("targetName", report.getTargetName());
        writer.writeString("authorName", report.getAuthorName());
        writer.writeInt64("timestamp", report.getTimestamp());
        writer.writeString("reason", report.getReason());
        writer.writeBoolean("resolved", report.isResolved());
        writer.writeString("resolvedBy", report.getResolvedBy());
        writer.writeEndDocument();
    }

    @Override
    public Class<Report> getEncoderClass() {
        return Report.class;
    }
}
