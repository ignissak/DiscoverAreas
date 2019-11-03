package net.ignissak.discoverareas.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SmartLogger {

    public SmartLogger info(@Nullable String message) {
        this.log(SmartLogger.Level.INFO, message);
        return this;
    }

    public SmartLogger warn(@Nullable String message) {
        this.log(SmartLogger.Level.WARNING, message);
        return this;
    }

    public SmartLogger error(@Nullable String message) {
        this.log(SmartLogger.Level.ERROR, message);
        return this;
    }

    public SmartLogger severe(@Nullable String message) {
        this.log(SmartLogger.Level.SEVERE, message);
        return this;
    }

    public SmartLogger debug(Object instance, @Nullable String message) {
        this.log(SmartLogger.Level.DEBUG, instance.getClass().getName().split("\\.")[instance.getClass().getName().split("\\.").length - 1] + " " + message);
        return this;
    }

    public SmartLogger debug(String message) {
        this.log(SmartLogger.Level.DEBUG, message);
        return this;
    }

    public SmartLogger success(String message) {
        this.log(Level.SUCCESS, message);
        return this;
    }

    private SmartLogger log(@NotNull SmartLogger.Level level, @Nullable String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', level.getPrefix() + (message != null ? message : "null")));
        return this;
    }

    public static enum Level {
        INFO(ChatColor.AQUA + "[INFO]"),
        WARNING(ChatColor.YELLOW + "[WARN]"),
        ERROR(ChatColor.RED + "[ERROR]"),
        SEVERE(ChatColor.DARK_RED + "[SEVERE]"),
        DEBUG(ChatColor.GRAY + "[DEBUG]"),
        SUCCESS(ChatColor.GREEN + "[SUCCESS]");

        @NotNull
        private final String prefix;

        private Level(@NotNull String prefix) {
            this.prefix = prefix;
        }

        @Contract(
                pure = true
        )
        @NotNull
        public final String getPrefix() {
            return ChatColor.GOLD + "[DiscoverAreas] " + this.prefix + " ";
        }

        public String toString() {
            return this.name();
        }
    }

}
