package de.pc1up.sentinelmc.database.impl;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import de.pc1up.sentinelmc.SentinelMC;
import de.pc1up.sentinelmc.database.DatabaseProvider;
import de.pc1up.sentinelmc.database.mongodb.PunishmentCodec;
import de.pc1up.sentinelmc.database.mongodb.ReportCodec;
import de.pc1up.sentinelmc.database.mongodb.UserProfileCodec;
import de.pc1up.sentinelmc.database.mongodb.WarningCodec;
import de.pc1up.sentinelmc.objects.Punishment;
import de.pc1up.sentinelmc.objects.Report;
import de.pc1up.sentinelmc.objects.UserProfile;
import de.pc1up.sentinelmc.objects.Warning;
import org.bson.codecs.configuration.CodecRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class MongoProvider implements DatabaseProvider {
    private MongoClient mongoClient;
    private MongoCollection<Punishment> punishmentMongoCollection;
    private MongoCollection<UserProfile> userProfileMongoCollection;
    private MongoCollection<Report> reportMongoCollection;
    private MongoCollection<Warning> warningMongoCollection;

    @Override
    public void initialize() {
        String configConnectionString = SentinelMC.instance.getConfiguration().getString("database.credentials.mongo.connectionstring", "mongodb://localhost:27017/");
        ConnectionString connectionString = new ConnectionString(configConnectionString);

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(
                        CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                                CodecRegistries.fromCodecs(new PunishmentCodec()),
                                CodecRegistries.fromCodecs(new UserProfileCodec()),
                                CodecRegistries.fromCodecs(new ReportCodec()),
                                CodecRegistries.fromCodecs(new WarningCodec()))
                ).build();

        String database = SentinelMC.instance.getConfiguration().getString("database.credentials.mongo.database", "sentinelmc");
        this.mongoClient = MongoClients.create(settings);
        MongoDatabase mongoDatabase = mongoClient.getDatabase(database);
        this.punishmentMongoCollection = mongoDatabase.getCollection("sentinelmc_punishments", Punishment.class);
        this.userProfileMongoCollection = mongoDatabase.getCollection("sentinelmc_userprofiles", UserProfile.class);
        this.reportMongoCollection = mongoDatabase.getCollection("sentinelmc_reports", Report.class);
        this.warningMongoCollection = mongoDatabase.getCollection("sentinelmc_warns", Warning.class);
        SentinelMC.instance.getLogger().info("[SentinelMC] MongoDB initialized!");
    }

    @Override
    public void savePunishment(Punishment punishment) {
        if (getPunishment(punishment.getId()) == null) {
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
        if (getProfile(userProfile.getUuid()) == null) {
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
        return this.userProfileMongoCollection
                .find(Filters.regex("name", "^" + Pattern.quote(name) + "$", "i"))
                .first();
    }

    @Override
    public void saveReport(Report report) {
        if (getReport(report.getId()) == null) {
            this.reportMongoCollection.insertOne(report);
        } else {
            this.reportMongoCollection.replaceOne(Filters.eq("id", report.getId()), report);
        }
    }

    @Override
    public Report getReport(String id) {
        return this.reportMongoCollection.find(Filters.eq("id", id)).first();
    }

    @Override
    public List<Report> getReportsAgainst(String name) {
        return this.reportMongoCollection.find(
                Filters.eq("targetName", name)
        ).into(new ArrayList<>());
    }

    @Override
    public List<Report> getReportsBy(String name) {
        return this.reportMongoCollection.find(
                Filters.eq("authorName", name)
        ).into(new ArrayList<>());
    }

    @Override
    public List<Report> getUnresolvedReports() {
        return this.reportMongoCollection.find(
                Filters.eq("resolved", false)
        ).into(new ArrayList<>());
    }

    @Override
    public List<Report> getResolvedReports() {
        return this.reportMongoCollection.find(
                Filters.eq("resolved", true)
        ).into(new ArrayList<>());
    }

    @Override
    public void deleteOldResolvedReports(long cutoffTime) {
        reportMongoCollection.deleteMany(Filters.and(
                Filters.eq("resolved", true),
                Filters.lt("timestamp", cutoffTime)
        ));
    }


    @Override
    public void saveWarning(Warning warning) {
        if (getWarning(warning.getId()) == null) {
            this.warningMongoCollection.insertOne(warning);
        } else {
            this.warningMongoCollection.replaceOne(Filters.eq("id", warning.getId()), warning);
        }
    }

    @Override
    public Warning getWarning(String id) {
        return this.warningMongoCollection.find(Filters.eq("id", id)).first();
    }

    @Override
    public List<Warning> getWarnings(UUID uuid) {
        return this.warningMongoCollection.find(
                Filters.eq("targetUUID", uuid.toString())
        ).into(new ArrayList<>());
    }

    @Override
    public Warning getLatestWarning(UUID uuid) {
        return warningMongoCollection
                .find(Filters.eq("targetUUID", uuid.toString()))
                .sort(Sorts.descending("timestamp"))
                .limit(1)
                .first();
    }

    @Override
    public void deleteWarning(String id) {
        this.warningMongoCollection.deleteOne(Filters.eq("id", id));
    }

    @Override
    public void close() {
        this.mongoClient.close();
    }
}
