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

        if (!player.hasPermission("discoverareas.admin")) return true;

        if (DiscoverMain.getInstance().getCache().size() <= 0) {
            ChatInfo.info(player, "There are no areas to view.");
            return true;
        }

        if (!DiscoverMain.getConfiguration().getBoolean("menus.admin.enabled")) {
            ChatInfo.error(player, "You cannot view this menu.");
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
