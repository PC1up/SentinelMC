package de.pc1up.sentinelmc.commands.misc;

import de.pc1up.sentinelmc.SentinelMC;
import de.pc1up.sentinelmc.guis.HistoryGUI;
import de.pc1up.sentinelmc.objects.Punishment;
import de.pc1up.sentinelmc.objects.UserProfile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class HistoryCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if(command.getName().equalsIgnoreCase("history")){
            if(args.length == 0){
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "history.usage");
                return false;
            }
            if(commandSender instanceof Player player){
                String targetName = args[0];
                UserProfile targetProfile = SentinelMC.instance.getDatabaseProvider().getProfile(targetName);
                if(targetProfile == null){
                    SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "not_registered", Map.of("player", targetName));
                    return false;
                }
                List<Punishment> punishments = SentinelMC.instance.getDatabaseProvider().getPunishments(targetProfile.getUuid());
                if(punishments.isEmpty()) {
                    SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "history.empty", Map.of("player", targetName));
                    return false;
                }
                HistoryGUI historyGUI = new HistoryGUI(targetProfile, punishments);
                SentinelMC.instance.getGuiManager().openGUI(historyGUI, player);
            }
        }
        return false;
    }
}
