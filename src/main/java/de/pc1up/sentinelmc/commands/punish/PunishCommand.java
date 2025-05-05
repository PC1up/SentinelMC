package de.pc1up.sentinelmc.commands.punish;

import de.pc1up.sentinelmc.SentinelMC;
import de.pc1up.sentinelmc.enums.SanctionaryActions;
import de.pc1up.sentinelmc.objects.Punishment;
import de.pc1up.sentinelmc.objects.PunishmentTemplate;
import de.pc1up.sentinelmc.objects.UserProfile;
import de.pc1up.sentinelmc.util.TimeParser;
import de.pc1up.sentinelmc.util.manager.SanctionManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Map;

public class PunishCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if(command.getName().equalsIgnoreCase("punish")) {
            if(args.length < 2) {
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "punishsystem.punish.usage");
                return false;
            }
            String targetName = args[0];
            UserProfile targetProfile = SentinelMC.instance.getDatabaseProvider().getProfile(targetName);
            if(targetProfile == null){
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "not_registered", Map.of("player", targetName));
                return false;
            }

            String templateName = args[1];
            PunishmentTemplate template = SentinelMC.instance.getPunishmentTemplateManager().getTemplate(templateName);
            if(template == null){
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "punishsystem.punish.invalid", Map.of("template", templateName));
                System.gc();
                return false;
            }
            if(!template.getPermission().isEmpty()) {
                if(commandSender instanceof Player player) {
                    if(!player.hasPermission(template.getPermission())) {
                        SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "punishsystem.punish.no_perm", Map.of("permission", template.getPermission()));
                        return false;
                    }
                }
            }

            if(commandSender instanceof Player player){
                SanctionaryActions action = template.getAction();
                if(action == SanctionaryActions.BAN) {
                    if(!player.hasPermission("sentinelmc.command.ban")) {
                        SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "punishsystem.punish.no_perm", Map.of("permission", "sentinelmc.command.ban"));
                        return false;
                    }
                } else if(action == SanctionaryActions.TEMPBAN) {
                    if(!player.hasPermission("sentinelmc.command.tempban")) {
                        SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "punishsystem.punish.no_perm", Map.of("permission", "sentinelmc.command.tempban"));
                        return false;
                    }
                } else if(action == SanctionaryActions.MUTE) {
                    if(!player.hasPermission("sentinelmc.command.mute")) {
                        SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "punishsystem.punish.no_perm", Map.of("permission", "sentinelmc.command.mute"));
                        return false;
                    }
                } else if(action == SanctionaryActions.TEMPMUTE) {
                    if(!player.hasPermission("sentinelmc.command.tempmute")) {
                        SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "punishsystem.punish.no_perm", Map.of("permission", "sentinelmc.command.tempmute"));
                        return false;
                    }
                } else if(action == SanctionaryActions.KICK) {
                    if(!player.hasPermission("sentinelmc.command.kick")) {
                        SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "punishsystem.punish.no_perm", Map.of("permission", "sentinelmc.command.kick"));
                        return false;
                    }
                }
            }

            long endTime;
            String durationName;
            if(template.getDuration().equalsIgnoreCase("-1")) {
                endTime = -1;
                durationName = "Permanent";
            } else {
                endTime = Instant.now().getEpochSecond() + TimeParser.parseTimeToDuration(template.getDuration(), commandSender);
                durationName = template.getDuration();
            }

            SanctionManager manager = SentinelMC.instance.getSanctionManager();
            if(template.getAction() == SanctionaryActions.BAN || template.getAction() == SanctionaryActions.TEMPBAN) {
                if(manager.getActiveBan(targetProfile.getUuid()) != null) {
                    SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "bansystem.ban.already_banned", Map.of("player", targetName));
                    return false;
                }
                if(commandSender instanceof Player) {
                    Player targetPlayer = Bukkit.getPlayer(targetName);
                    if(targetPlayer != null){
                        if(targetPlayer.hasPermission("sentinelmc.ban.exempt")) {
                            SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "bansystem.ban.not_allowed", Map.of("player", targetName));
                            return false;
                        }
                    }
                }

                Punishment punishment = manager.createPunishment(targetProfile.getUuid(), commandSender.getName(), true, template.getReason(), endTime, durationName, template.getNote());
                manager.performPunishment(punishment, true);

                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "bansystem.ban.success", Map.of("player", targetName));
                if(template.getAction().isTemporary()) {
                    SentinelMC.instance.getMessageUtil().sendRestrictedMessage("bansystem.notify", "bansystem.tempban.broadcast", Map.of(
                            "player", targetName,
                            "author", commandSender.getName(),
                            "reason", template.getReason(),
                            "duration", durationName
                    ));
                } else {
                    SentinelMC.instance.getMessageUtil().sendRestrictedMessage("bansystem.notify", "bansystem.ban.broadcast", Map.of(
                            "player", targetName,
                            "author", commandSender.getName(),
                            "reason", template.getReason()
                    ));
                }

            } else if(template.getAction() == SanctionaryActions.MUTE || template.getAction() == SanctionaryActions.TEMPMUTE) {
                if(manager.getActiveMute(targetProfile.getUuid()) != null) {
                    SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "mutesystem.mute.already_muted", Map.of("player", targetName));
                    return false;
                }
                if(commandSender instanceof Player) {
                    Player targetPlayer = Bukkit.getPlayer(targetName);
                    if(targetPlayer != null){
                        if(targetPlayer.hasPermission("sentinelmc.mute.exempt")) {
                            SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "mutesystem.mute.not_allowed", Map.of("player", targetName));
                            return false;
                        }
                    }
                }

                Punishment punishment = manager.createPunishment(targetProfile.getUuid(), commandSender.getName(), false, template.getReason(), endTime, durationName, template.getNote());
                manager.performPunishment(punishment, true);

                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "mutesystem.mute.success", Map.of("player", targetName));
                if(template.getAction().isTemporary()) {
                    SentinelMC.instance.getMessageUtil().sendRestrictedMessage("mutesystem.notify", "mutesystem.tempmute.broadcast", Map.of(
                            "player", targetName,
                            "author", commandSender.getName(),
                            "reason", template.getReason(),
                            "duration", durationName
                    ));
                } else {
                    SentinelMC.instance.getMessageUtil().sendRestrictedMessage("mutesystem.notify", "mutesystem.mute.broadcast", Map.of(
                            "player", targetName,
                            "author", commandSender.getName(),
                            "reason", template.getReason()
                    ));
                }
            } else if(template.getAction() == SanctionaryActions.KICK) {
                Player targetPlayer = Bukkit.getPlayer(targetName);
                if(targetPlayer == null) {
                    SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "not_online", Map.of("player", targetName));
                    return false;
                } else {
                    if (commandSender instanceof Player) {
                        if (targetPlayer.hasPermission("sentinelmc.kick.exempt")) {
                            SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "bansystem.kick.not_allowed", Map.of("player", targetName));
                            return false;
                        }
                    }
                }

                targetPlayer.kick(
                        SentinelMC.instance.getMessageUtil().buildScreen("bansystem.kick.screen", Map.of(
                                "author", commandSender.getName(),
                                "reason", template.getReason()
                        ))
                );

                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "bansystem.kick.success", Map.of("player", targetName));
                SentinelMC.instance.getMessageUtil().sendRestrictedMessage("bansystem.notify", "bansystem.kick.broadcast", Map.of(
                        "player", targetName,
                        "author", commandSender.getName(),
                        "reason", template.getReason()
                ));
            }
        }
        return false;
    }
}
