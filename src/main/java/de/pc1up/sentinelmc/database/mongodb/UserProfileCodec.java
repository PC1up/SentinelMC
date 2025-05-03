package de.pc1up.sentinelmc.database.mongodb;

import de.pc1up.sentinelmc.punishments.UserProfile;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.util.UUID;

public class UserProfileCodec implements Codec<UserProfile> {
    @Override
    public UserProfile decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        reader.readObjectId();
        UUID uuid = UUID.fromString(reader.readString("uuid"));
        String name = reader.readString("name");
        String lastIp = reader.readString("lastIp");
        int points = reader.readInt32("points");
        reader.readEndDocument();
        return new UserProfile(uuid, name, lastIp, points);
    }

    @Override
    public void encode(BsonWriter writer, UserProfile userProfile, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString("uuid", userProfile.getUuid().toString());
        writer.writeString("name", userProfile.getName());
        writer.writeString("lastIp", userProfile.getLastIp());
        writer.writeInt32("points", userProfile.getPoints());
        writer.writeEndDocument();
    }

    @Override
    public Class<UserProfile> getEncoderClass() {
        return UserProfile.class;
    }
}
