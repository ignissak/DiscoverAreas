package net.ignissak.discoverareas.discover;

import net.ignissak.discoverareas.DiscoverMain;
import net.ignissak.discoverareas.objects.Area;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DiscoverPlayer {

    private Player player;
    private List<String> discovered;

    DiscoverPlayer(Player player) {
        this.player = player;
        if (DiscoverMain.getData().getStringList(player.getUniqueId().toString()).isEmpty()) discovered = new ArrayList<>();
        else {
            this.discovered = new ArrayList<>(DiscoverMain.getData().getStringList(player.getUniqueId().toString()));
        }
    }

    private void saveToData() {
        if (getDiscovered().size() == 0) return;
        List<String> list = getDiscovered().stream().map(Area::getName).collect(Collectors.toList());
        DiscoverMain.getData().set(player.getUniqueId().toString(), list);
        DiscoverMain.getInstance().saveFiles();
    }

    /**
     * Return's player instance of DiscoverPlayer
     * @return Player instance
     */

    public Player getPlayer() {
        return player;
    }

    /**
        Returns List of areas that player has discovered
        @return list of areas
     **/

    public List<Area> getDiscovered() {
        List<Area> out = new ArrayList<>();
        for (Area a : DiscoverMain.getInstance().getCache()) {
            if (getDiscovered().contains(a)) out.add(a);
        }
        return out;
    }

    /**
     * Returns List of areas that player has not discovered yet
     * @return List of areas
     */

    public List<Area> getNotDiscovered() {
        List<Area> out = new ArrayList<>();
        for (Area a : DiscoverMain.getInstance().getCache()) {
            if (getDiscovered().contains(a.getName())) continue;
            out.add(a);
        }
        return out;
    }

    /**
     * Reset player's progress of discovering areas
     * @throws NullPointerException
     */

    public void resetProgress() throws NullPointerException {
        this.discovered.clear();
        DiscoverMain.getData().set(player.getUniqueId().toString(), null);
        DiscoverMain.getInstance().saveFiles();
    }

    /**
     * Adds new area to discovered list of player
     * @param a Discovered Area
     */

    public void addDiscoveredArea(Area a) {
        this.discovered.add(a.getName());
        this.saveToData();
    }

    /**
     * Checks if player has discovered certain area
     * @param a Area
     * @return boolean
     */

    public boolean hasDiscovered(Area a) {
        return discovered.contains(a.getName());
    }

    /**
     * Reloads player's discovered areas from data file
     */

    public void reload() {
        if (DiscoverMain.getData().getStringList(player.getUniqueId().toString()).isEmpty()) discovered = new ArrayList<>();
        else {
            this.discovered = new ArrayList<>(DiscoverMain.getData().getStringList(player.getUniqueId().toString()));
        }
    }
}
