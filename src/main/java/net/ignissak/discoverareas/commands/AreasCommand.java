package net.ignissak.discoverareas.commands;

import net.ignissak.discoverareas.DiscoverMain;
import net.ignissak.discoverareas.utils.ChatInfo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AreasCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        if (DiscoverMain.getInstance().getCache().size() <= 0) {
            if (DiscoverMain.getConfiguration().getString("messages.no_areas") == "") return true;
            ChatInfo.error(player, DiscoverMain.getConfiguration().getString("messages.no_areas"));
            return true;
        }

        if (!player.hasPermission("discoverareas.user.areas")) {
            if (DiscoverMain.getConfiguration().getString("messages.no_permission") == "") return true;
            ChatInfo.error(player, DiscoverMain.getConfiguration().getString("messages.no_permission"));
            return true;
        }

        if (!DiscoverMain.getConfiguration().getBoolean("menus.user.enabled")) {
            if (DiscoverMain.getConfiguration().getString("messages.disabled_menu") == "") return true;
            ChatInfo.error(player, DiscoverMain.getConfiguration().getString("messages.disabled_menu"));
            return true;
        }

        if (DiscoverMain.getInstance().getCache().size() <= 45) {
            player.openInventory(DiscoverMain.getMenuManager().getMenu("userMenu_" + player.getName()).getInventory());
        } else {
            player.openInventory(DiscoverMain.getMenuManager().getMenu("userMenu_" + player.getName() + "_1").getInventory());
        }

        return true;
    }
}
