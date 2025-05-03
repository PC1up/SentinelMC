package de.pc1up.sentinelmc;

import de.pc1up.sentinelmc.database.DatabaseProvider;
import de.pc1up.sentinelmc.database.impl.MongoProvider;
import de.pc1up.sentinelmc.enums.DatabaseSystem;
import de.pc1up.sentinelmc.util.MessageUtil;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class SentinelMC extends JavaPlugin {
    public static SentinelMC instance;

    private MessageUtil messageUtil;
    private FileConfiguration configuration;
    private DatabaseSystem databaseSystem;
    private DatabaseProvider databaseProvider;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        instance = this;
        this.saveDefaultConfig();
        this.configuration = getConfig();
        this.messageUtil = new MessageUtil(this.configuration);

        initializeDatabase();

        long now = System.currentTimeMillis();
        this.getLogger().info("SentinelMC started in " + (now - start) + "ms");
    }

    private void initializeDatabase() {
        String system = this.configuration.getString("database.system", "sqlite");
        if(system.equalsIgnoreCase("sqlite")) {
            // TODO
            this.databaseSystem = DatabaseSystem.SQLITE;
        } else if(system.equalsIgnoreCase("mysql")) {
            //TODO
            this.databaseSystem = DatabaseSystem.MYSQL;
        } else if(system.equalsIgnoreCase("mongodb")) {
            this.databaseSystem = DatabaseSystem.MONGODB;
            this.databaseProvider = new MongoProvider();
        }
    }

    public void reloadConfig() {
        this.configuration = getConfig();
        this.messageUtil = new MessageUtil(this.configuration);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
