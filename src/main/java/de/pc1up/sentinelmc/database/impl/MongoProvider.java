package de.pc1up.sentinelmc.database.impl;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.InsertOneOptions;
import com.mongodb.client.model.UpdateOptions;
import de.pc1up.sentinelmc.SentinelMC;
import de.pc1up.sentinelmc.database.DatabaseProvider;
import de.pc1up.sentinelmc.database.mongodb.PunishmentCodec;
import de.pc1up.sentinelmc.database.mongodb.UserProfileCodec;
import de.pc1up.sentinelmc.punishments.Punishment;
import de.pc1up.sentinelmc.punishments.UserProfile;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MongoProvider implements DatabaseProvider {
    private MongoClient mongoClient;
    private MongoCollection<Punishment> punishmentMongoCollection;
    private MongoCollection<UserProfile> userProfileMongoCollection;

    @Override
    public void initialize() {
        String configConnectionString = SentinelMC.instance.getConfiguration().getString("database.credentials.mongo.connectionstring", "mongodb://localhost:27017/");
        ConnectionString connectionString = new ConnectionString(configConnectionString);

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(
                        CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                                CodecRegistries.fromCodecs(new PunishmentCodec()),
                                CodecRegistries.fromCodecs(new UserProfileCodec()))
                ).build();

        String database = SentinelMC.instance.getConfiguration().getString("database.credentials.mongo.database", "sentinelmc");
        this.mongoClient = MongoClients.create(settings);
        MongoDatabase mongoDatabase = mongoClient.getDatabase(database);
        this.punishmentMongoCollection = mongoDatabase.getCollection("sentinelmc_punishments", Punishment.class);
        this.userProfileMongoCollection = mongoDatabase.getCollection("sentinelmc_userprofiles", UserProfile.class);
        SentinelMC.instance.getLogger().info("[SentinelMC] MongoDB initialized!");
    }

    @Override
    public void savePunishment(Punishment punishment) {
        if(getPunishment(punishment.getId()) == null) {
            this.punishmentMongoCollection.insertOne(punishment);
        } else {
            this.punishmentMongoCollection.replaceOne(
                    Filters.eq("id", punishment.getId()),
                    punishment
            );
        }
    }

    @Override
    public List<Punishment> getPunishments(UUID player) {
        return this.punishmentMongoCollection.find(Filters.eq("targetUUID", player.toString())).into(new ArrayList<>());
    }

    @Override
    public Punishment getPunishment(String id) {
        return this.punishmentMongoCollection.find(Filters.eq("id", id)).first();
    }

    @Override
    public void saveUser(UserProfile userProfile) {
        if(getProfile(userProfile.getUuid()) == null) {
            this.userProfileMongoCollection.insertOne(userProfile);
        } else {
            this.userProfileMongoCollection.replaceOne(
                    Filters.eq("uuid", userProfile.getUuid().toString()),
                    userProfile
            );
        }
    }

    @Override
    public UserProfile getProfile(UUID uuid) {
        return this.userProfileMongoCollection.find(Filters.eq("uuid", uuid.toString())).first();
    }

    @Override
    public UserProfile getProfile(String name) {
        return this.userProfileMongoCollection.find(Filters.eq("name", name)).first();
    }

    @Override
    public void close() {
        this.mongoClient.close();
    }
}
