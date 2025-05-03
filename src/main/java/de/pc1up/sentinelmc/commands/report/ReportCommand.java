package de.pc1up.sentinelmc.commands.report;

import de.pc1up.sentinelmc.SentinelMC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ReportCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if(command.getName().equalsIgnoreCase("report")) {
            if(args.length < 2) {
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "reportsystem.report.usage");
                return false;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if(target == null) {
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "not_online", Map.of("player", args[0]));
                return false;
            }
            if(target.getName().equals(commandSender.getName())) {
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "not_self");
                return false;
            }

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                stringBuilder.append(args[i]).append(" ");
            }
            String reason = stringBuilder.substring(0, stringBuilder.length() - 1);

            if(commandSender instanceof Player player){
                SentinelMC.instance.getReportManager().createAndSubmitReport(target.getName(), commandSender.getName(), reason, player);
            } else {
                SentinelMC.instance.getReportManager().createAndSubmitReport(target.getName(), commandSender.getName(), reason, null);
            }
        }
        return false;
    }
}
