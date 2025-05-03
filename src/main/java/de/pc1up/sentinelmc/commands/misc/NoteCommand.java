package de.pc1up.sentinelmc.commands.misc;

import de.pc1up.sentinelmc.SentinelMC;
import de.pc1up.sentinelmc.objects.Punishment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class NoteCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if(command.getName().equalsIgnoreCase("note")) {
            if(args.length < 2) {
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "bansystem.note.usage");
                return false;
            }
            String id = args[0];
            Punishment punishment = SentinelMC.instance.getDatabaseProvider().getPunishment(id);
            if(punishment == null) {
                SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "bansystem.note.invalid");
                return false;
            }

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                stringBuilder.append(args[i]).append(" ");
            }
            String note = stringBuilder.substring(0, stringBuilder.length() - 1);

            punishment.setNote(note);
            punishment.save();
            SentinelMC.instance.getMessageUtil().sendMessage(commandSender, "bansystem.note.success", Map.of("id", id));
        }
        return false;
    }
}
