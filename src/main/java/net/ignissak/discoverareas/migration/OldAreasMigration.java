package net.ignissak.discoverareas.migration;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.ignissak.discoverareas.DiscoverAreasPlugin;
import net.ignissak.discoverareas.objects.Area;
import net.ignissak.discoverareas.utils.AreaUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.simpleyaml.configuration.ConfigurationSection;

import java.util.List;

public class OldAreasMigration {

    // TODO
    public void migrate() {
        if (DiscoverAreasPlugin.getConfiguration().getConfigurationSection("areas").getKeys(false).size() > 0) {
            // Let's migrate
            DiscoverAreasPlugin.getSmartLogger().info("Starting areas migration...");

            DiscoverAreasPlugin.getConfiguration().getConfigurationSection("areas").getKeys(false).forEach(name -> {
                DiscoverAreasPlugin.getSmartLogger().info("Migrating '" + name + "' area...");

                try {
                    ConfigurationSection section = DiscoverAreasPlugin.getConfiguration().getConfigurationSection("areas." + name);

                    String worldName = section.getString("world", "world");
                    World world = Bukkit.getWorld(worldName);

                    assert world != null : "Could not get world '" + worldName + "'.";

                    RegionManager regionManager = DiscoverAreasPlugin.getRegionContainer().get(new BukkitWorld(world));

                    assert regionManager != null : "Could not get RegionManager for world '" + worldName + "'.";

                    String regionName = section.getString("region", "region");
                    if (!regionManager.hasRegion(regionName)) {
                        DiscoverAreasPlugin.getSmartLogger().error("Invalid region name '" + regionName + "' in world '" + worldName + "'.");
                        return;
                    }

                    int id = AreaUtils.getNextAreaId();
                    ProtectedRegion region = regionManager.getRegion(regionName);
                    String description = section.getString("description", "Default description - change in config.");
                    int xp = section.getInt("xp", 0);
                    Sound sound = Sound.valueOf(section.getString("sound", "ENTITY_EXPERIENCE_ORB_PICKUP"));
                    List<String> commands = section.getStringList("commands");
                    long created = section.getLong("created");

                    Area area = new Area(id, region, world, name, description, xp, sound, commands, created);
                    area.addToCache();

                    DiscoverAreasPlugin.getConfiguration().remove("areas." + name);
                    DiscoverAreasPlugin.getInstance().saveFiles();

                    DiscoverAreasPlugin.getSmartLogger().success("Successfully migrated area '" + name + "' under ID " + id + ".");
                } catch (Exception e) {
                    DiscoverAreasPlugin.getSmartLogger().error("Could not migrate area '" + name + "' due to an error.");
                    e.printStackTrace();
                }

                if (DiscoverAreasPlugin.getConfiguration().getConfigurationSection("areas").getKeys(false).size() == 0) {
                    DiscoverAreasPlugin.getConfiguration().remove("areas");
                    DiscoverAreasPlugin.getInstance().saveFiles();

                    DiscoverAreasPlugin.getSmartLogger().success("Migration is completed.");
                }
            });
        }
    }
}
