package de.pc1up.sentinelmc.database.impl;

import de.pc1up.sentinelmc.SentinelMC;
import de.pc1up.sentinelmc.database.DatabaseProvider;
import de.pc1up.sentinelmc.punishments.Punishment;
import de.pc1up.sentinelmc.punishments.Report;
import de.pc1up.sentinelmc.punishments.UserProfile;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SQLiteProvider implements DatabaseProvider {
    private Connection connection;

    @Override
    public void initialize() {
        try {
            File dbFile = new File(SentinelMC.instance.getDataFolder(), "sentinelmc.sqlite");
            String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
            connection = DriverManager.getConnection(url);
            SentinelMC.instance.getLogger().info("[SentinelMC] SQLite connected!");

            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS sentinelmc_punishments (
                    id VARCHAR(36) PRIMARY KEY,
                    targetUUID TEXT,
                    authorName TEXT,
                    reason TEXT,
                    startTime INTEGER,
                    endTime INTEGER,
                    duration TEXT,
                    note TEXT,
                    revoked BOOLEAN,
                    revokedBy TEXT,
                    revokeReason TEXT,
                    ban BOOLEAN
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS sentinelmc_userprofiles (
                    uuid VARCHAR(36) PRIMARY KEY,
                    name TEXT,
                    lastIp TEXT,
                    points INTEGER
                );
            """);

            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS reports (
                        id VARCHAR(36) PRIMARY KEY,
                        targetName VARCHAR(16),
                        authorName VARCHAR(16),
                        timestamp BIGINT,
                        reason TEXT,
                        resolved BOOLEAN,
                        resolvedBy VARCHAR(16)
                    );
                    """);
        }
    }

    @Override
    public void savePunishment(Punishment p) {
        try (PreparedStatement ps = connection.prepareStatement("""
            INSERT OR REPLACE INTO sentinelmc_punishments
            (id, targetUUID, authorName, reason, startTime, endTime, duration, note, revoked, revokedBy, revokeReason, ban)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
        """)) {
            ps.setString(1, p.getId());
            ps.setString(2, p.getTargetUUID().toString());
            ps.setString(3, p.getAuthorName());
            ps.setString(4, p.getReason());
            ps.setLong(5, p.getStartTime());
            ps.setLong(6, p.getEndTime());
            ps.setString(7, p.getDuration());
            ps.setString(8, p.getNote());
            ps.setBoolean(9, p.isRevoked());
            ps.setString(10, p.getRevokedBy());
            ps.setString(11, p.getRevokeReason());
            ps.setBoolean(12, p.isBan());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Punishment> getPunishments(UUID player) {
        List<Punishment> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("""
            SELECT * FROM sentinelmc_punishments WHERE targetUUID = ?;
        """)) {
            ps.setString(1, player.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapPunishment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Punishment getPunishment(String id) {
        try (PreparedStatement ps = connection.prepareStatement("""
            SELECT * FROM sentinelmc_punishments WHERE id = ?;
        """)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapPunishment(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Punishment mapPunishment(ResultSet rs) throws SQLException {
        return new Punishment(
                rs.getString("id"),
                UUID.fromString(rs.getString("targetUUID")),
                rs.getString("authorName"),
                rs.getString("reason"),
                rs.getLong("startTime"),
                rs.getLong("endTime"),
                rs.getString("duration"),
                rs.getString("note"),
                rs.getBoolean("revoked"),
                rs.getString("revokedBy"),
                rs.getString("revokeReason"),
                rs.getBoolean("ban")
        );
    }

    @Override
    public void saveUser(UserProfile u) {
        try (PreparedStatement ps = connection.prepareStatement("""
            INSERT OR REPLACE INTO sentinelmc_userprofiles (uuid, name, lastIp, points)
            VALUES (?, ?, ?, ?);
        """)) {
            ps.setString(1, u.getUuid().toString());
            ps.setString(2, u.getName());
            ps.setString(3, u.getLastIp());
            ps.setInt(4, u.getPoints());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public UserProfile getProfile(UUID uuid) {
        try (PreparedStatement ps = connection.prepareStatement("""
            SELECT * FROM sentinelmc_userprofiles WHERE uuid = ?;
        """)) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUserProfile(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public UserProfile getProfile(String name) {
        try (PreparedStatement ps = connection.prepareStatement("""
            SELECT * FROM sentinelmc_userprofiles WHERE name = ?;
        """)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUserProfile(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private UserProfile mapUserProfile(ResultSet rs) throws SQLException {
        return new UserProfile(
                UUID.fromString(rs.getString("uuid")),
                rs.getString("name"),
                rs.getString("lastIp"),
                rs.getInt("points")
        );
    }

    @Override
    public void saveReport(Report report) {
        try (PreparedStatement select = connection.prepareStatement("SELECT id FROM reports WHERE id = ?")) {
            select.setString(1, report.getId());
            ResultSet resultSet = select.executeQuery();

            if (resultSet.next()) {
                PreparedStatement update = connection.prepareStatement(
                        "UPDATE reports SET targetName=?, authorName=?, timestamp=?, reason=?, resolved=?, resolvedBy=? WHERE id=?"
                );
                update.setString(1, report.getTargetName());
                update.setString(2, report.getAuthorName());
                update.setLong(3, report.getTimestamp());
                update.setString(4, report.getReason());
                update.setBoolean(5, report.isResolved());
                update.setString(6, report.getResolvedBy());
                update.setString(7, report.getId());
                update.executeUpdate();
            } else {
                PreparedStatement insert = connection.prepareStatement(
                        "INSERT INTO reports (id, targetName, authorName, timestamp, reason, resolved, resolvedBy) VALUES (?, ?, ?, ?, ?, ?, ?)"
                );
                insert.setString(1, report.getId());
                insert.setString(2, report.getTargetName());
                insert.setString(3, report.getAuthorName());
                insert.setLong(4, report.getTimestamp());
                insert.setString(5, report.getReason());
                insert.setBoolean(6, report.isResolved());
                insert.setString(7, report.getResolvedBy());
                insert.executeUpdate();
            }
        }catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public Report getReport(String id) {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM reports WHERE id = ?")) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractReport(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Report> getReportsAgainst(String name) {
        return getReportsByColumn("targetName", name);
    }

    @Override
    public List<Report> getReportsBy(String name) {
        return getReportsByColumn("authorName", name);
    }

    @Override
    public List<Report> getUnresolvedReports() {
        List<Report> reports = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM reports WHERE resolved = FALSE")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reports.add(extractReport(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }

    private List<Report> getReportsByColumn(String column, String value) {
        List<Report> reports = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM reports WHERE " + column + " = ?")) {
            stmt.setString(1, value);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reports.add(extractReport(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }

    private Report extractReport(ResultSet rs) throws SQLException {
        return new Report(
                rs.getString("id"),
                rs.getString("targetName"),
                rs.getString("authorName"),
                rs.getLong("timestamp"),
                rs.getString("reason"),
                rs.getBoolean("resolved"),
                rs.getString("resolvedBy")
        );
    }

    @Override
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

