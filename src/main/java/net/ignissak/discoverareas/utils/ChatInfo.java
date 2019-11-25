package net.ignissak.discoverareas.utils;

import net.ignissak.discoverareas.DiscoverMain;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class ChatInfo {

    /**
     * Informative message about everything
     *
     * @param player  Player that receive message
     * @param message Text of message
     */
    public static void info(Player player, String message) {
        player.sendMessage(ChatColor.GRAY + "> " + ChatColor.GRAY + message);
        player.playSound(player.getLocation(), Sound.valueOf(DiscoverMain.getConfiguration().getString("sounds.info", "BLOCK_LADDER_HIT")), 1, 0);
    }

    /**
     * Successful message if process will be successfully :D
     *
     * @param player  Player that receive message
     * @param message Text of successful message
     */
    public static void success(Player player, String message) {
        player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + ">> " + ChatColor.GREEN + message);
        player.playSound(player.getLocation(), Sound.valueOf(DiscoverMain.getConfiguration().getString("sounds.success", "ENTITY_EXPERIENCE_ORB_PICKUP")), 1, 0);
    }

    /**
     * Whatever some action fails this can inform player about it
     *
     * @param player  Player that receive message
     * @param message Text of error message
     */
    public static void error(Player player, String message) {
        player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "[!!] " + ChatColor.RED + message);
        player.playSound(player.getLocation(), Sound.valueOf(DiscoverMain.getConfiguration().getString("sounds.error", "BLOCK_ANVIL_PLACE")), 1, 0);
    }

    /**
     * Some warning for player, be careful!
     *
     * @param player  Player that receive message
     * @param message Text of warning message
     */
    public static void warning(Player player, String message) {
        player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "[!] " + ChatColor.GOLD + message);
        player.playSound(player.getLocation(), Sound.valueOf(DiscoverMain.getConfiguration().getString("sounds.warning", "BLOCK_BEACON_POWER_SELECT")), 1, 0);
    }

    /**
     * Debug message for developers and admin team
     *
     * @param player  Player that receive message
     * @param message Text of debug message
     */
    public static void debug(Player player, String message) {
        player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "[D] " + ChatColor.AQUA + message);
    }

}
