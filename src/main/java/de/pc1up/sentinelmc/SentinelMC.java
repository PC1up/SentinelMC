package de.pc1up.sentinelmc;

import de.pc1up.sentinelmc.commands.ban.BanCommand;
import de.pc1up.sentinelmc.commands.ban.CheckbanCommand;
import de.pc1up.sentinelmc.commands.ban.TempbanCommand;
import de.pc1up.sentinelmc.commands.ban.UnbanCommand;
import de.pc1up.sentinelmc.commands.misc.*;
import de.pc1up.sentinelmc.commands.mute.CheckmuteCommand;
import de.pc1up.sentinelmc.commands.mute.MuteCommand;
import de.pc1up.sentinelmc.commands.mute.TempmuteCommand;
import de.pc1up.sentinelmc.commands.mute.UnmuteCommand;
import de.pc1up.sentinelmc.commands.report.ReportCommand;
import de.pc1up.sentinelmc.commands.report.ReportlistCommand;
import de.pc1up.sentinelmc.commands.report.ResolveallCommand;
import de.pc1up.sentinelmc.commands.warn.DelwarnCommand;
import de.pc1up.sentinelmc.commands.warn.UnwarnCommand;
import de.pc1up.sentinelmc.commands.warn.WarnCommand;
import de.pc1up.sentinelmc.commands.warn.WarningsCommand;
import de.pc1up.sentinelmc.database.DatabaseProvider;
import de.pc1up.sentinelmc.database.impl.MongoProvider;
import de.pc1up.sentinelmc.database.impl.MySQLProvider;
import de.pc1up.sentinelmc.database.impl.SQLiteProvider;
import de.pc1up.sentinelmc.enums.DatabaseSystem;
import de.pc1up.sentinelmc.listener.ChatListener;
import de.pc1up.sentinelmc.listener.JoinQuitListener;
import de.pc1up.sentinelmc.listener.LoginListener;
import de.pc1up.sentinelmc.util.MessageUtil;
import de.pc1up.sentinelmc.util.manager.ReportManager;
import de.pc1up.sentinelmc.util.manager.SanctionManager;
import de.pc1up.sentinelmc.util.manager.UserManager;
import de.pc1up.sentinelmc.util.gui.GUIListener;
import de.pc1up.sentinelmc.util.gui.GUIManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Getter
public final class SentinelMC extends JavaPlugin {
    public static SentinelMC instance;

    private MessageUtil messageUtil;
    private FileConfiguration configuration;
    private DatabaseSystem databaseSystem;
    private DatabaseProvider databaseProvider;
    private UserManager userManager;
    private SanctionManager sanctionManager;
    private GUIManager guiManager;
    private ReportManager reportManager;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        instance = this;
        this.saveDefaultConfig();
        this.configuration = getConfig();
        update(new File(getDataFolder(), "config.yml"), "config.yml", this);
        reloadCfg();
        this.messageUtil = new MessageUtil(this.configuration);

        initializeDatabase();

        this.userManager = new UserManager();
        this.sanctionManager = new SanctionManager();
        this.reportManager = new ReportManager();
        this.guiManager = new GUIManager();

        initializeListener();
        intializeCommands();
        initializeScheduler();

        long now = System.currentTimeMillis();
        this.getLogger().info("SentinelMC started in " + (now - start) + "ms");
    }

    private void initializeScheduler() {
        if(configuration.getBoolean("report.delete-resolved.enabled", true)) {
            long cutoffTime = configuration.getLong("report.delete-resolved.cutoff", 86400L);
            Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
                long cutoff = Instant.now().getEpochSecond() - cutoffTime;
                databaseProvider.deleteOldResolvedReports(cutoff);
            }, 0L, 20L * 60 * 60);
        }

    }

    private void intializeCommands() {
        boolean enableBans = configuration.getBoolean("modules.bans", true);
        boolean enableMutes = configuration.getBoolean("modules.mutes", true);
        boolean enableWarns = configuration.getBoolean("modules.warns", true);
        boolean enableReports = configuration.getBoolean("modules.reports", true);
        boolean enableVanish = configuration.getBoolean("modules.misc.vanish", true);
        boolean enableSpectate = configuration.getBoolean("modules.misc.spectate", true);
        boolean enableStaffChat = configuration.getBoolean("modules.misc.staffchat", true);

        this.getCommand("kick").setExecutor(new KickCommand());
        this.getCommand("history").setExecutor(new HistoryCommand());

        if(enableBans) {
            this.getCommand("ban").setExecutor(new BanCommand());
            this.getCommand("unban").setExecutor(new UnbanCommand());
            this.getCommand("tempban").setExecutor(new TempbanCommand());
            this.getCommand("checkban").setExecutor(new CheckbanCommand());
        }

        if(enableMutes) {
            this.getCommand("mute").setExecutor(new MuteCommand());
            this.getCommand("unmute").setExecutor(new UnmuteCommand());
            this.getCommand("tempmute").setExecutor(new TempmuteCommand());
            this.getCommand("checkmute").setExecutor(new CheckmuteCommand());
        }
        this.getCommand("note").setExecutor(new NoteCommand());

        if(enableReports) {
            this.getCommand("reportlist").setExecutor(new ReportlistCommand());
            this.getCommand("report").setExecutor(new ReportCommand());
            this.getCommand("resolveall").setExecutor(new ResolveallCommand());
        }

        if(enableWarns) {
            this.getCommand("warn").setExecutor(new WarnCommand());
            this.getCommand("unwarn").setExecutor(new UnwarnCommand());
            this.getCommand("delwarn").setExecutor(new DelwarnCommand());
            this.getCommand("warnings").setExecutor(new WarningsCommand());
        }

        if(enableVanish) this.getCommand("vanish").setExecutor(new VanishCommand());
        if(enableSpectate) this.getCommand("spectate").setExecutor(new SpectateCommand());
        if(enableStaffChat) this.getCommand("staffchat").setExecutor(new StaffchatCommand());

        this.getCommand("sentinelmc").setExecutor(new SentinelMCCommand());
    }

    private void initializeListener(){
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new GUIListener(this.guiManager), this);
        pluginManager.registerEvents(new LoginListener(), this);
        pluginManager.registerEvents(new ChatListener(), this);
        pluginManager.registerEvents(new JoinQuitListener(), this);
    }

    private void initializeDatabase() {
        String system = this.configuration.getString("database.system", "sqlite");
        if (system.equalsIgnoreCase("sqlite")) {
            this.databaseSystem = DatabaseSystem.SQLITE;
            this.databaseProvider = new SQLiteProvider();
        } else if (system.equalsIgnoreCase("mysql")) {
            this.databaseSystem = DatabaseSystem.MYSQL;
            this.databaseProvider = new MySQLProvider();
        } else if (system.equalsIgnoreCase("mongodb")) {
            this.databaseSystem = DatabaseSystem.MONGODB;
            this.databaseProvider = new MongoProvider();
        } else {
            this.databaseSystem = DatabaseSystem.SQLITE;
            this.databaseProvider = new SQLiteProvider();
            this.getLogger().warning("[SentinelMC] WARNING: You provided an invalid database system: '" + system + "'. Only 'mysql', 'sqlite' and 'mongodb' are supported.\n[SentinelMC] WARNING: SentinelMC is falling back to SQLite.");
        }

        this.databaseProvider.initialize();
    }

    private void update(File configFile, String resourcePath, JavaPlugin plugin) {
        try {
            YamlConfiguration existing = YamlConfiguration.loadConfiguration(configFile);
            InputStream defaultStream = plugin.getResource(resourcePath);
            if (defaultStream == null) return;

            YamlConfiguration defaults = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream, StandardCharsets.UTF_8));

            boolean changed = false;

            for (String key : defaults.getKeys(true)) {
                if (!existing.contains(key)) {
                    existing.set(key, defaults.get(key));
                    changed = true;
                }
            }

            if (changed) {
                existing.save(configFile);
                plugin.getLogger().info("[SentinelMC] Updated missing config keys in " + configFile.getName());
            }

        } catch (IOException e) {
            plugin.getLogger().severe("[SentinelMC] Failed to update " + configFile.getName() + ": " + e.getMessage());
        }
    }

    public void reloadCfg() {
        reloadConfig();
        this.configuration = getConfig();
        this.messageUtil = new MessageUtil(this.configuration);
    }

    @Override
    public void onDisable() {
        this.databaseProvider.close();
    }
}
