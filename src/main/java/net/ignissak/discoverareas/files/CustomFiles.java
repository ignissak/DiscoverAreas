package net.ignissak.discoverareas.files;

import net.ignissak.discoverareas.DiscoverAreasPlugin;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class CustomFiles {

    private static File configFile, dataFile;
    private static FileConfiguration configConfig, dataConfig;

    public CustomFiles() {
        this.createFiles();
    }

    private void createFiles() {
        configFile = new File(DiscoverAreasPlugin.getInstance().getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            DiscoverAreasPlugin.getInstance().saveResource("config.yml", false);
        }

        configConfig = new YamlConfiguration();
        try {
            configConfig.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        dataFile = new File(DiscoverAreasPlugin.getInstance().getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            DiscoverAreasPlugin.getInstance().saveResource("data.yml", false);
        }

        dataConfig = new YamlConfiguration();
        try {
            dataConfig.load(dataFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void saveFiles() {
        try {
            this.getConfigConfig().save(configFile);
            this.getDataConfig().save(dataFile);
        } catch (IOException e) {
            DiscoverAreasPlugin.getSmartLogger().severe("Could not save files.");
            e.printStackTrace();
        }
    }

    public void reloadFiles() {
        configConfig = new YamlConfiguration();
        try {
            configConfig.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        dataConfig = new YamlConfiguration();
        try {
            dataConfig.load(dataFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfigConfig() {
        return configConfig;
    }

    public FileConfiguration getDataConfig() {
        return dataConfig;
    }
}
