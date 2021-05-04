package net.ignissak.discoverareas.files;

import net.ignissak.discoverareas.DiscoverAreasPlugin;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import java.io.File;
import java.io.IOException;

public class CustomFiles {

    private static YamlFile configFile, dataFile, areasFile;

    public CustomFiles() {
        try {
            this.createFiles();
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void createFiles() throws IOException, InvalidConfigurationException {
        configFile = new YamlFile(new File(DiscoverAreasPlugin.getInstance().getDataFolder(), "config.yml"));
        if (!configFile.exists()) {
            configFile.createNewFile(true);
            DiscoverAreasPlugin.getInstance().saveResource("config.yml", false);
        }
        configFile.load();

        dataFile = new YamlFile(new File(DiscoverAreasPlugin.getInstance().getDataFolder(), "data.yml"));
        if (!dataFile.exists()) {
            // TODO: Disable migration
        }
        dataFile.load();

        areasFile = new YamlFile(new File(DiscoverAreasPlugin.getInstance().getDataFolder(), "areas.yml"));
        if (!areasFile.exists()) {
            areasFile.createNewFile(true);
        }
        areasFile.load();
    }

    public void saveFiles() {
        try {
            configFile.save();
            dataFile.save();
            areasFile.save();
        } catch (IOException e) {
            DiscoverAreasPlugin.getSmartLogger().severe("Could not save files.");
            e.printStackTrace();
        }
    }

    public static YamlFile getConfigFile() {
        return configFile;
    }

    /**
     * Data files are deprecated. Data are now stored in database.
     * @since 2.0.0
     * @deprecated
     */
    @Deprecated
    public static YamlFile getDataFile() {
        return dataFile;
    }

    public static YamlFile getAreasFile() {
        return areasFile;
    }
}
