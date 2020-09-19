package net.ignissak.discoverareas.commands;

import net.ignissak.discoverareas.DiscoverAreasPlugin;
import net.ignissak.discoverareas.utils.ChatInfo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AdminAreasCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        if (!player.hasPermission("discoverareas.admin.areas")) {
            if (DiscoverAreasPlugin.getConfiguration().getString("messages.no_permission", "").equals("")) return true;
            ChatInfo.error(player, DiscoverAreasPlugin.getConfiguration().getString("messages.no_permission"));
            return true;
        }

        if (DiscoverAreasPlugin.getInstance().getCache().size() <= 0) {
            if (DiscoverAreasPlugin.getConfiguration().getString("messages.no_areas", "").equals("")) return true;
            ChatInfo.error(player, DiscoverAreasPlugin.getConfiguration().getString("messages.no_areas"));
            return true;
        }

        if (!DiscoverAreasPlugin.getConfiguration().getBoolean("menus.admin.enabled", true)) {
            if (DiscoverAreasPlugin.getConfiguration().getString("messages.disabled_menu", "").equals("")) return true;
            ChatInfo.error(player, DiscoverAreasPlugin.getConfiguration().getString("messages.disabled_menu"));
            return true;
        }

        if (DiscoverAreasPlugin.getInstance().getCache().size() <= 45) {
            player.openInventory(DiscoverAreasPlugin.getMenuManager().getMenu("adminMenu").getInventory());
        } else {
            player.openInventory(DiscoverAreasPlugin.getMenuManager().getMenu("adminMenu1").getInventory());
        }

        return true;
    }
}
