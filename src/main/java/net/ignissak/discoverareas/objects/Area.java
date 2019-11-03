package net.ignissak.discoverareas.objects;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.ignissak.discoverareas.DiscoverMain;
import net.ignissak.discoverareas.discover.DiscoverPlayer;
import net.ignissak.discoverareas.utils.ChatInfo;
import net.ignissak.discoverareas.utils.TextComponentBuilder;
import net.ignissak.discoverareas.utils.title.Title;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Area {

    private ProtectedRegion region;
    private World world;
    private String name, description;
    private int xp;
    private List<String> rewardCommands;
    private Sound discoverySound;
    private ConfigurationSection configurationSection;
    private List<UUID> discoveredBy;

    public Area(String name) {
        Area cached = DiscoverMain.getInstance().getCache().stream().filter(area -> area.getName().equalsIgnoreCase(name)).findFirst().get();
        this.region = cached.getRegion();
        this.world = cached.getWorld();
        this.description = cached.getDescription();
        this.xp = cached.getXp();
        this.rewardCommands = cached.getRewardCommands();
        this.discoverySound = cached.getDiscoverySound();
        this.name = cached.getName();

        this.configurationSection = DiscoverMain.getConfiguration().getConfigurationSection("areas." + getName());
        this.initDiscoveredBy();
    }

    public Area(ProtectedRegion region) {
        Area cached = DiscoverMain.getInstance().getCache().stream().filter(area -> area.getRegion().equals(region)).findFirst().get();
        this.region = cached.getRegion();
        this.world = cached.getWorld();
        this.description = cached.getDescription();
        this.xp = cached.getXp();
        this.rewardCommands = cached.getRewardCommands();
        this.discoverySound = cached.getDiscoverySound();
        this.name = cached.getName();

        this.configurationSection = DiscoverMain.getConfiguration().getConfigurationSection("areas." + getName());
        this.initDiscoveredBy();
    }

    public Area(ProtectedRegion region, World world, String name, String description, int xp, Sound discoverySound, List<String> rewardCommands) {
        this.region = region;
        this.world = world;
        this.name = name;
        this.description = description;
        this.xp = xp;
        this.discoverySound = discoverySound;
        this.rewardCommands = rewardCommands;

        this.addData();
        this.initDiscoveredBy();
    }

    public ProtectedRegion getRegion() {
        return region;
    }

    public World getWorld() {
        return world;
    }

    public List<String> getRewardCommands() {
        return rewardCommands;
    }

    public void addRewardCommand(String s) {
        this.rewardCommands.add(s);
    }

    public String getName() {
        return name;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public Sound getDiscoverySound() {
        return discoverySound;
    }

    public String getDescription() {
        return description;
    }

    public List<UUID> getDiscoveredBy() {
        this.initDiscoveredBy();
        return discoveredBy;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ConfigurationSection getConfigurationSection() {
        return configurationSection;
    }

    private void addData() {
        DiscoverMain.getConfiguration().createSection("areas." + this.getName());
        DiscoverMain.getInstance().saveFiles();
        this.configurationSection = DiscoverMain.getConfiguration().getConfigurationSection("areas." + getName());

        configurationSection.set("world", getWorld().getName());
        configurationSection.set("region", getRegion().getId());
        configurationSection.set("description", getDescription());
        configurationSection.set("xp", getXp());
        configurationSection.set("commands", getRewardCommands());
        configurationSection.set("sound", getDiscoverySound().toString());

        DiscoverMain.getInstance().saveFiles();
    }

    public void updateData() {
        this.configurationSection = DiscoverMain.getConfiguration().getConfigurationSection("areas." + getName());

        configurationSection.set("world", getWorld().getName());
        configurationSection.set("region", getRegion().getId());
        configurationSection.set("description", getDescription());
        configurationSection.set("xp", getXp());
        configurationSection.set("commands", getRewardCommands());
        configurationSection.set("sound", getDiscoverySound().toString());

        DiscoverMain.getInstance().saveFiles();
        DiscoverMain.getMenuManager().updateMenus();
    }

    public void addToCache() {
        if (!DiscoverMain.getInstance().getCache().contains(this)) DiscoverMain.getInstance().getCache().add(this);
    }

    public void discover(DiscoverPlayer discoverPlayer) {
        discoverPlayer.addDiscoveredArea(this.getName());
        if (DiscoverMain.getConfiguration().getBoolean("title.on_discover.enabled")) {
            String title = ChatColor.translateAlternateColorCodes('&', DiscoverMain.getConfiguration().getString("title.on_discover.title").replace("@area", this.getName())).replace("@description", this.getDescription());
            String subtitle = ChatColor.translateAlternateColorCodes('&', DiscoverMain.getConfiguration().getString("title.on_discover.subtitle").replace("@area", this.getName())).replace("@description", this.getDescription());
            int fadein = DiscoverMain.getConfiguration().getInt("title.on_discover.fadein") * 20;
            int stay = DiscoverMain.getConfiguration().getInt("title.on_discover.stay") * 20;
            int fadeout = DiscoverMain.getConfiguration().getInt("title.on_discover.fadeout") * 20;
            new Title(title, subtitle, fadein, stay, fadeout).send(discoverPlayer.getPlayer());
        }

        if (DiscoverMain.getConfiguration().getBoolean("messages.on_discover.enabled")) {
            for (String s : DiscoverMain.getConfiguration().getStringList("messages.on_discover.messages")) {
                discoverPlayer.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', s.replace("@area", this.getName()).replace("@description", this.getDescription())));
            }
        }

        discoverPlayer.getPlayer().playSound(discoverPlayer.getPlayer().getLocation(), this.getDiscoverySound(), 1, 0);
        if (!getConfigurationSection().getStringList("commands").isEmpty()) {
            for (String s : getConfigurationSection().getStringList("commands")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("@player", discoverPlayer.getPlayer().getName()));
            }
        }

        if (getConfigurationSection().getInt("xp") > 0) {
            discoverPlayer.getPlayer().giveExp(configurationSection.getInt("xp"));
        }

        DiscoverMain.getMenuManager().updateMenus();
    }

    public void reload() {
        this.configurationSection = DiscoverMain.getConfiguration().getConfigurationSection("areas." + getName());

        configurationSection.set("world", getWorld().getName());
        configurationSection.set("region", getRegion().getId());
        configurationSection.set("description", getDescription());
        configurationSection.set("xp", getXp());
        configurationSection.set("commands", getRewardCommands());
        configurationSection.set("sound", getDiscoverySound().toString());

        DiscoverMain.getMenuManager().updateMenus();
    }

    public void delete() {
        if (DiscoverMain.getInstance().getCache().contains(this)) DiscoverMain.getInstance().getCache().remove(this);

        DiscoverMain.getData().getKeys(false).forEach(uuid -> {
            List<String> discovered = DiscoverMain.getData().getStringList(uuid);
            discovered.remove(this.getName());
            if (discovered.isEmpty()) {
                DiscoverMain.getData().set(uuid, null);
            } else {
                DiscoverMain.getData().set(uuid, discovered);
            }
        });

        DiscoverMain.getConfiguration().set("areas." + getName(), null);
        DiscoverMain.getInstance().saveFiles();
        DiscoverMain.getMenuManager().updateMenus();
    }

    public void sendCommands(Player player) {
        if (this.getRewardCommands().size() <= 0) {
            ChatInfo.warning(player, "This area does not have any commands defined.");
            return;
        }
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 0);
        player.sendMessage(ChatColor.GREEN + "Area: '" + this.getName() + "' - commands:");
        int id = 0;
        for (String s : this.getRewardCommands()) {
            TextComponent base = new TextComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&8[" + id + "] " + "&7/" + s + " &8- ")).getComponent();
            TextComponent edit = new TextComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&e&lEDIT ")).setPerformedCommand("area command edit " + id + " " + getName()).setTooltip("Edit command").getComponent();
            TextComponent remove = new TextComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&c&lREMOVE")).setPerformedCommand("area command remove " + id + " " + getName()).setTooltip("Remove command").getComponent();
            player.spigot().sendMessage(base.duplicate(), edit.duplicate(), remove.duplicate());
            id++;
        }
        new TextComponentBuilder(ChatColor.AQUA + "> Click here to add new command.").setPerformedCommand("area command add " + getName()).setTooltip("Add new command").send(player);
    }

    public void teleport(Player player) {
        //Get top location
        Location top = new Location(getWorld(), 0, 0, 0);
        top.setX(region.getMaximumPoint().getX());

        top.setZ(region.getMaximumPoint().getZ());

        //Get bottom location
        Location bottom = new Location(getWorld(), 0, 0, 0);
        bottom.setX(region.getMinimumPoint().getX());
        bottom.setZ(region.getMinimumPoint().getZ());

        //Split difference
        double X =  ((bottom.getX() - top.getX())/2) + bottom.getX();
        double Z =  ((bottom.getZ() - top.getZ())/2) + bottom.getZ();

        //Setup new location
        Location location = new Location(getWorld(), X, getWorld().getHighestBlockYAt((int) X, (int) Z), Z);

        player.teleport(location);
        player.setFallDistance(0);
    }

    private void initDiscoveredBy() {
        List<UUID> out = new ArrayList<>();
        DiscoverMain.getData().getKeys(false).forEach(uuidString -> {
            List<String> discovered = DiscoverMain.getData().getStringList(uuidString);
            if (discovered.contains(getName())) out.add(UUID.fromString(uuidString));
        });
        this.discoveredBy = out;
    }
}
