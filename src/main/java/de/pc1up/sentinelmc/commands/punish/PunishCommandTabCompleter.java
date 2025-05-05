package de.pc1up.sentinelmc.commands.punish;

import de.pc1up.sentinelmc.SentinelMC;
import de.pc1up.sentinelmc.objects.PunishmentTemplate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PunishCommandTabCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        if (!command.getName().equalsIgnoreCase("punish")) return Collections.emptyList();

        if (args.length == 1) {
            return null;
        }

        if (args.length == 2) {
            List<String> result = new ArrayList<>();
            Map<String, PunishmentTemplate> templates = SentinelMC.instance.getPunishmentTemplateManager().getTemplates();

            for (Map.Entry<String, PunishmentTemplate> entry : templates.entrySet()) {
                String key = entry.getKey();
                PunishmentTemplate template = entry.getValue();

                String permission = template.getPermission();
                if (permission == null || permission.isEmpty() || sender.hasPermission(permission)) {
                    if (key.toLowerCase().startsWith(args[1].toLowerCase())) {
                        result.add(key);
                    }
                }
            }

            return result;
        }

        return Collections.emptyList();
    }
}

