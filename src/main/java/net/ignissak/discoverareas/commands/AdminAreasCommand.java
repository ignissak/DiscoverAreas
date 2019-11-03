package net.ignissak.discoverareas.commands;

import net.ignissak.discoverareas.DiscoverMain;
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

        player.openInventory(DiscoverMain.getMenuManager().getMenu("adminMenu").getInventory());

        return true;
    }
}
