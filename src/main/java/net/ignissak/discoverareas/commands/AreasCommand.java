package net.ignissak.discoverareas.commands;

import net.ignissak.discoverareas.DiscoverMain;
import net.ignissak.discoverareas.discover.DiscoverPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AreasCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull  String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        DiscoverPlayer discoverPlayer = DiscoverMain.getDiscoverPlayer(player);

        //todo
        return true;
    }
}
