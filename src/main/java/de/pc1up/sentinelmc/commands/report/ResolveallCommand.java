package de.pc1up.sentinelmc.commands.report;

import de.pc1up.sentinelmc.SentinelMC;
import de.pc1up.sentinelmc.objects.Report;
import de.pc1up.sentinelmc.objects.UserProfile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class ResolveallCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if(command.getName().equals("resolveall")) {
            if(args.length == 0) {
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "reportsystem.resolveall.usage");
                return false;
            }
            String targetName = args[0];
            UserProfile targetProfile = SentinelMC.instance.getDatabaseProvider().getProfile(targetName);
            if(targetProfile == null){
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "not_registered", Map.of("player", targetName));
                return false;
            }
            List<Report> allReports = SentinelMC.instance.getDatabaseProvider().getReportsAgainst(targetProfile.getName());
            int amount = 0;
            for(Report report : allReports) {
                if(!report.isResolved()) {
                    report.setResolved(true);
                    report.setResolvedBy(commandSender.getName());
                    report.save();
                    amount++;
                }
            }
            SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "reportsystem.resolveall.success", Map.of("amount", String.valueOf(amount)));
        }
        return false;
    }
}
