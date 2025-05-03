package de.pc1up.sentinelmc;

import de.pc1up.sentinelmc.database.DatabaseProvider;
import de.pc1up.sentinelmc.database.impl.MongoProvider;
import de.pc1up.sentinelmc.database.impl.MySQLProvider;
import de.pc1up.sentinelmc.database.impl.SQLiteProvider;
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
            this.databaseSystem = DatabaseSystem.SQLITE;
            this.databaseProvider = new SQLiteProvider();
        } else if(system.equalsIgnoreCase("mysql")) {
            this.databaseSystem = DatabaseSystem.MYSQL;
            this.databaseProvider = new MySQLProvider();
        } else if(system.equalsIgnoreCase("mongodb")) {
            this.databaseSystem = DatabaseSystem.MONGODB;
            this.databaseProvider = new MongoProvider();
        } else {
            this.databaseSystem = DatabaseSystem.SQLITE;
            this.databaseProvider = new SQLiteProvider();
            this.getLogger().warning("[SentinelMC] WARNING: You provided an invalid database system: '" + system + "'. Only 'mysql', 'sqlite' and 'mongodb' are supported.\n[SentinelMC] WARNING: SentinelMC is falling back to SQLite.");
        }

        this.databaseProvider.initialize();
    }

    public void reloadConfig() {
        this.configuration = getConfig();
        this.messageUtil = new MessageUtil(this.configuration);
    }

    @Override
    public void onDisable() {
        this.databaseProvider.close();
    }
}
