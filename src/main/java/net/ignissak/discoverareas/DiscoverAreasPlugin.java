package net.ignissak.discoverareas;

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
import net.ignissak.discoverareas.menu.MenuManager;
import net.ignissak.discoverareas.objects.Area;
import net.ignissak.discoverareas.objects.ServerVersion;
import net.ignissak.discoverareas.utils.*;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class DiscoverAreasPlugin extends JavaPlugin {

    private int resourceID = 72410;

    private static DiscoverAreasPlugin instance;
    private static SmartLogger smartLogger;
    private static WorldGuard worldGuard;
    private static RegionContainer regionContainer;
    private static CustomFiles customFiles;
    private static MenuManager menuManager;
    private ItemBuilder undiscovered, discovered, previous, next;
    private boolean updateAvailable = false;
    private String newVersion;

    private static final HashMap<Player, DiscoverPlayer> players = new HashMap<>();
    private final List<Area> cache = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;
        smartLogger = new SmartLogger();

        getSmartLogger().info("Initializing...");

        hookWorldGuard();
        customFiles = new CustomFiles();

        if (ServerUtils.getServerVersion() != ServerVersion.V1_14) {
            getSmartLogger().warn("This server is not running plugin's native version (1.14). There may appear bugs & errors. If so, please contact developer on Spigotmc.org.");
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
                if (getConfiguration().getBoolean("general.update-notify"))
                    getSmartLogger().info("Your server is running latest version of DiscoverAreas (" + this.getDescription().getVersion() + ").");
            } else {
                if (getConfiguration().getBoolean("general.update-notify")) {
                    getSmartLogger().info("---------------------------------");
                    getSmartLogger().info("There is a new update available - v" + spigotVersion + ".");
                    getSmartLogger().info("https://www.spigotmc.org/resources/discoverareas-1-14." + this.resourceID + "/");
                    getSmartLogger().info("---------------------------------");
                }
                this.updateAvailable = true;
                this.newVersion = version;
            }
        });

        new VersionChecker(this, "https://raw.githubusercontent.com/ignissak/DiscoverAreas/master/unsupported_versions").checkVersion();

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

    public static DiscoverAreasPlugin getInstance() {
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

    @Nullable
    public static DiscoverPlayer getDiscoverPlayer(Player player) {
        return players.getOrDefault(player, null);
    }

    private boolean isBeta() {
        return getDescription().getVersion().contains("B");
    }

    private boolean isSnapshot() {
        return getDescription().getVersion().contains("SNAPSHOT");
    }

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
                try {
                    Sound s = Sound.valueOf(config.getString("sound"));
                    Area a = new Area(rm.getRegion(config.getString("region")), w, key, config.getString("description"), config.getInt("xp"), s, config.getStringList("commands"), config.getLong("created"));
                    this.cache.add(a);
                } catch (IllegalArgumentException e) {
                    getSmartLogger().error("Invalid sound name '" + config.getString("sound") + "' for area '" + key + "'.");
                    return;
                }
            } catch (NullPointerException e) {
                getSmartLogger().error("Error while loading area: " + key);
                e.printStackTrace();
            }
        });
        if (this.cache.size() > 0) {
            getSmartLogger().success("Successfully cached " + this.cache.size() + " areas.");
        } else {
            getSmartLogger().error("Could not cache areas, because some errors occured.");
        }
        return;
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
            getSmartLogger().error("Could not initialize GUI items. Maybe you are using invalid material or you have old version of config.");
            e.printStackTrace();
        }
    }

    private void checkSoundsValidality() {
        getSmartLogger().info("Checking sounds...");
        int count = 0;
        for (String s : getConfiguration().getConfigurationSection("sounds").getKeys(false)) {
            try {
                Sound.valueOf(s);
                count++;
            } catch (IllegalArgumentException e) {
                getSmartLogger().error(s + " is not a valid sound!");
            }
        }
        getSmartLogger().success(count + " sound were successfully loaded.");
    }

    public boolean existsArea(String name) {
        return getConfiguration().get("areas." + name) != null;
    }

    public boolean isRegionUsed(String name) {
        return this.cache.stream().anyMatch(area -> area.getRegion().getId().equals(name));
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
