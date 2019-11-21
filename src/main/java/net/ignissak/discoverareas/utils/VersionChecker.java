package net.ignissak.discoverareas.utils;

import net.ignissak.discoverareas.DiscoverMain;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class VersionChecker {

    private Plugin plugin;
    private String link;

    public VersionChecker(Plugin plugin, String link) {
        this.plugin = plugin;
        this.link = link;
    }

    public void checkVersion() {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                URL url = new URL(this.link);
                Scanner scanner = new Scanner(url.openStream());
                while (scanner.hasNext()) {
                    if (plugin.getDescription().getVersion().equalsIgnoreCase(scanner.next())) {
                        DiscoverMain.getSmartLogger().severe("This version of plugin is no longer supported, please upgrade your version to version listed on resource page.");
                        plugin.getPluginLoader().disablePlugin(plugin);
                        return;
                    }
                }
            } catch (IOException exception) {
                DiscoverMain.getSmartLogger().error("Could not check version: " + exception.getMessage());
            }
        });
    }
}
