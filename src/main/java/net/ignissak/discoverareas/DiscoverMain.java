package net.ignissak.discoverareas;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.ignissak.discoverareas.commands.AdminAreasCommand;
import net.ignissak.discoverareas.commands.AreaCommand;
import net.ignissak.discoverareas.commands.AreasCommand;
import net.ignissak.discoverareas.discover.DiscoverManager;
import net.ignissak.discoverareas.discover.DiscoverPlayer;
import net.ignissak.discoverareas.events.WGRegionEventsListener;
import net.ignissak.discoverareas.files.CustomFiles;
import net.ignissak.discoverareas.menu.Menu;
import net.ignissak.discoverareas.menu.MenuManager;
import net.ignissak.discoverareas.objects.Area;
import net.ignissak.discoverareas.utils.ItemBuilder;
import net.ignissak.discoverareas.utils.Metrics;
import net.ignissak.discoverareas.utils.SmartLogger;
import net.ignissak.discoverareas.utils.UpdateChecker;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class DiscoverMain extends JavaPlugin {

    private int resourceID = 72410;

    private static DiscoverMain instance;
    private static SmartLogger smartLogger;
    private static WorldGuard worldGuard;
    private static RegionContainer regionContainer;
    private static CustomFiles customFiles;
    private static MenuManager menuManager;
    private ItemBuilder undiscovered, discovered, previous, next;
    private boolean updateAvailable = false;
    private String newVersion;

    private List<Area> cache = new ArrayList<>();
    private static HashMap<Player, DiscoverPlayer> players = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        smartLogger = new SmartLogger();

        getSmartLogger().info("Initializing...");

        hookWorldGuard();
        customFiles = new CustomFiles();

        if (!isNativeVersion()) {
            getSmartLogger().warn("This server is not running plugin's native version. There may appear bugs & errors. If so, please contact developer on Spigotmc.org.");
        }

        if (isBeta()) {
            getSmartLogger().warn("This is beta version, all new features introduced may be buggy. Use this version on your own risk.");
        }

        if (isSnapshot()) {
            getSmartLogger().warn("This is snapshot version of plugin - this version is not final and may not be stable. Use this version on your own risk.");
        }

        WGRegionEventsListener.initialize();

        this.getCommand("area").setExecutor(new AreaCommand());
        this.getCommand("area").setTabCompleter(new AreaCommand());
        this.getCommand("areas").setExecutor(new AreasCommand());
        this.getCommand(".areas").setExecutor(new AdminAreasCommand());
        Bukkit.getPluginManager().registerEvents(new DiscoverManager(), this);


        new UpdateChecker(this, resourceID).getVersion(version -> {
            DefaultArtifactVersion spigotVersion = new DefaultArtifactVersion(version);
            DefaultArtifactVersion pluginVersion = new DefaultArtifactVersion(getDescription().getVersion());
            if (pluginVersion.compareTo(spigotVersion) >= 0) {
                if (getConfiguration().getBoolean("general.update-notify")) getSmartLogger().info("Your server is running latest version of DiscoverAreas (" + this.getDescription().getVersion() + ").");
            } else {
                if (getConfiguration().getBoolean("general.update-notify")) {
                    getSmartLogger().info("---------------------------------");
                    getSmartLogger().info("There is a new update available - v" + spigotVersion + ".");
                    getSmartLogger().info("https://www.spigotmc.org/resources/discoverareas-1-12." + this.resourceID + "/");
                    getSmartLogger().info("---------------------------------");
                }
                this.updateAvailable = true;
                this.newVersion = version;
            }
        });

        cacheAreas();

        initItemStacks();
        menuManager = new MenuManager();

        try {
            Metrics metrics = new Metrics(this);
            getSmartLogger().info("This plugin uses bStats to monitor statistics.");
        } catch (Exception e) {
            getSmartLogger().error("Could not initialize metrics.");
        }
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public static DiscoverMain getInstance() {
        return instance;
    }

    public static SmartLogger getSmartLogger() {
        return smartLogger;
    }

    public static WorldGuard getWorldGuard() {
        return worldGuard;
    }

    public static RegionContainer getRegionContainer() {
        return regionContainer;
    }

    public static FileConfiguration getConfiguration() {
        return customFiles.getConfigConfig();
    }

    public static FileConfiguration getData() {
        return customFiles.getDataConfig();
    }

    public List<Area> getCache() {
        return cache;
    }

    public HashMap<Player, DiscoverPlayer> getPlayers() {
        return players;
    }

    public static MenuManager getMenuManager() {
        return menuManager;
    }

    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    public String getNewVersion() {
        return newVersion;
    }

    public int getResourceID() {
        return resourceID;
    }

    @Nullable public static DiscoverPlayer getDiscoverPlayer(Player player) {
        if (players.containsKey(player)) return players.get(player);
        else return null;
    }

    private boolean isNativeVersion() {
        return Bukkit.getVersion().contains("1.13");
    }

    private boolean isBeta() { return getDescription().getVersion().contains("B"); }

    private boolean isSnapshot() { return getDescription().getVersion().contains("SNAPSHOT"); }

    public void saveFiles() {
        customFiles.saveFiles();
    }

    public void reloadFiles() {
        customFiles.reloadFiles();
    }

    private void hookWorldGuard() {
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            try {
                worldGuard = WorldGuard.getInstance();
                regionContainer = worldGuard.getPlatform().getRegionContainer();
                getSmartLogger().success("WorldGuard hooked!");
            } catch (Exception e) {
                getSmartLogger().error("Could not hook WorldGuard!");
            }
            return;
        }
        getSmartLogger().severe("Could not hook WorldGuard! It looks like you do not have WorldGuard installed on your server.");
        getSmartLogger().info("Turning off...");
        Bukkit.getPluginManager().disablePlugin(this);
    }

    public void cacheAreas() {
        ConfigurationSection cs = getConfiguration().getConfigurationSection("areas");
        if (cs.getKeys(false).size() <= 0) {
            getSmartLogger().info("There are no areas defined.");
            return;
        }
        cs.getKeys(false).forEach(key -> {
            try {
                ConfigurationSection config = cs.getConfigurationSection(key);
                World w = Bukkit.getWorld(config.getString("world", "world"));
                RegionManager rm = getRegionContainer().get(new BukkitWorld(w));
                if (!rm.hasRegion(config.getString("region"))) {
                    getSmartLogger().error("Invalid region name '" + config.getString("region") + "' in world '" + config.getString("world") + "'.");
                    return;
                }
                if (Bukkit.getWorld(config.getString("world")) == null) {
                    getSmartLogger().error("Invalid world name '" + config.getString("world") + "' for area '" + key + "'.");
                    return;
                }
                Area a = new Area(rm.getRegion(config.getString("region")), w, key, config.getString("description"), config.getInt("xp"), Sound.valueOf(config.getString("sound")), config.getStringList("commands"), config.getLong("created"));
                this.cache.add(a);
            } catch (NullPointerException e) {
                getSmartLogger().error("Error while loading area: " + key);
                e.printStackTrace();
            }
        });
        if (this.cache.size() > 0) {
            getSmartLogger().success("Successfully cached " + this.cache.size() + " areas.");
            return;
        } else {
            getSmartLogger().error("Could not cache areas, because some errors occured.");
            return;
        }
    }

    private void initItemStacks() {
        try {
            ItemBuilder previous = new ItemBuilder(Material.valueOf(getConfiguration().getString("gui.previous.material")), 1)
                    .setName(getConfiguration().getString("gui.previous.displayname"))
                    .setLore(getConfiguration().getStringList("gui.previous.lore"));

            ItemBuilder next = new ItemBuilder(Material.valueOf(getConfiguration().getString("gui.next.material")), 1)
                    .setName(getConfiguration().getString("gui.next.displayname"))
                    .setLore(getConfiguration().getStringList("gui.next.lore"));


            this.undiscovered = undiscovered;
            this.discovered = discovered;
            this.previous = previous;
            this.next = next;
        } catch (Exception e) {
            getSmartLogger().error("Could not initialize GUI items. It seems you are using old version of config - look at Spigot page to update your config.");
            e.printStackTrace();
        }
    }

    public boolean existsArea(String name) {
        return getConfiguration().get("areas." + name) != null;
    }

    public boolean isRegionUsed(String name) {
        return this.cache.stream().filter(area -> area.getRegion().getId().equals(name)).findFirst().isPresent();
    }

    public boolean isInData(String uuid) {
        return getData().get(uuid) != null;
    }

    public ItemBuilder getUndiscovered(Area area) {
        ItemBuilder undiscovered = this.undiscovered;
        undiscovered.setName(undiscovered.getName().replace("@area", area.getName()).replace("@description", area.getDescription()));
        List<String> lore = undiscovered.getLore();
        lore.replaceAll(l -> {
            l.replace("@area", area.getName());
            l.replace("@description", area.getDescription());
            l.replace("@world", area.getWorld().getName());
            return l;
        });
        undiscovered.setLore(lore);
        return undiscovered;
    }

    public ItemBuilder getDiscovered(Area area) {
        ItemBuilder discovered = this.discovered;
        discovered.setName(discovered.getName().replace("@area", area.getName()).replace("@description", area.getDescription()));
        List<String> lore = discovered.getLore();
        lore.replaceAll(l -> {
            l.replace("@area", area.getName());
            l.replace("@description", area.getDescription());
            l.replace("@world", area.getWorld().getName());
            return l;
        });
        discovered.setLore(lore);
        return discovered;
    }

    public ItemBuilder getPrevious() {
        return previous;
    }

    public ItemBuilder getNext() {
        return next;
    }
}
