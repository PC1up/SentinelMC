package de.pc1up.sentinelmc.commands.report;

import de.pc1up.sentinelmc.SentinelMC;
import de.pc1up.sentinelmc.guis.ReportlistGUI;
import de.pc1up.sentinelmc.objects.Report;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReportlistCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if(command.getName().equals("reportlist")) {
            if(commandSender instanceof Player player) {
                List<Report> unresolvedReports = SentinelMC.instance.getDatabaseProvider().getUnresolvedReports();
                List<Report> resolvedReports = SentinelMC.instance.getDatabaseProvider().getResolvedReports();
                ReportlistGUI reportlistGUI = new ReportlistGUI(unresolvedReports, resolvedReports);
                SentinelMC.instance.getGuiManager().openGUI(reportlistGUI, player);
            }
        }
        return false;
    }
}
