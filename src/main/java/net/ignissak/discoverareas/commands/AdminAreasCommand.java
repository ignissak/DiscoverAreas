package net.ignissak.discoverareas.commands;

import net.ignissak.discoverareas.DiscoverMain;
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
            if (DiscoverMain.getConfiguration().getString("messages.no_permission") == "") return true;
            ChatInfo.error(player, DiscoverMain.getConfiguration().getString("messages.no_permission"));
            return true;
        }

        if (DiscoverMain.getInstance().getCache().size() <= 0) {
            if (DiscoverMain.getConfiguration().getString("messages.no_areas") == "") return true;
            ChatInfo.error(player, DiscoverMain.getConfiguration().getString("messages.no_areas"));
            return true;
        }

        if (!DiscoverMain.getConfiguration().getBoolean("menus.admin.enabled")) {
            if (DiscoverMain.getConfiguration().getString("messages.disabled_menu") == "") return true;
            ChatInfo.error(player, DiscoverMain.getConfiguration().getString("messages.disabled_menu"));
            return true;
        }

        if (DiscoverMain.getInstance().getCache().size() <= 45) {
            player.openInventory(DiscoverMain.getMenuManager().getMenu("adminMenu").getInventory());
        } else {
            player.openInventory(DiscoverMain.getMenuManager().getMenu("adminMenu1").getInventory());
        }

        return true;
    }
}
