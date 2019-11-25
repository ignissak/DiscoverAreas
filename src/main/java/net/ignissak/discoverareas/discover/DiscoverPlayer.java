package net.ignissak.discoverareas.discover;

import net.ignissak.discoverareas.DiscoverMain;
import net.ignissak.discoverareas.objects.Area;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DiscoverPlayer {

    private Player player;
    private HashMap<String, Long> discovered;

    DiscoverPlayer(Player player) {
        this.player = player;
        this.discovered = new HashMap<>();
        if (DiscoverMain.getData().getStringList(player.getUniqueId().toString()).isEmpty()) return;
        else {
            for (String s : DiscoverMain.getData().getStringList(player.getUniqueId().toString())) {
                String[] split = s.split(":");
                if (split.length != 2) {
                    // discovery time not defined
                    this.discovered.put(split[0], 0L);
                } else {
                    this.discovered.put(split[0], Long.valueOf(split[1]));
                }
            }
        }
    }

    private void saveToData() {
        if (getDiscovered().size() == 0) return;
        HashMap<String, Long> map = this.getDiscovered();
        List<String> list = new ArrayList<>();
        for (String key : map.keySet()) {
            list.add(key + ":" + map.get(key));
        }
        DiscoverMain.getData().set(player.getUniqueId().toString(), list);
        DiscoverMain.getInstance().saveFiles();
    }

    /**
     * Return's player instance of DiscoverPlayer
     *
     * @return Player instance
     */

    public Player getPlayer() {
        return player;
    }

    /**
     * Returns List of areas that player has discovered
     *
     * @return list of areas
     **/

    public HashMap<String, Long> getDiscovered() {
        return this.discovered;
    }

    /**
     * Returns List of areas that player has not discovered yet
     *
     * @return List of areas
     */

    public List<Area> getNotDiscovered() {
        List<Area> out = new ArrayList<>();
        for (Area a : DiscoverMain.getInstance().getCache()) {
            if (getDiscovered().containsKey(a.getName())) continue;
            out.add(a);
        }
        return out;
    }

    /**
     * Reset player's progress of discovering areas
     *
     * @throws NullPointerException
     */

    public void resetProgress() throws NullPointerException {
        this.discovered.clear();
        DiscoverMain.getData().set(player.getUniqueId().toString(), null);
        DiscoverMain.getInstance().saveFiles();
    }

    /**
     * Adds new area to discovered list of player
     *
     * @param a Discovered Area
     */

    public void addDiscoveredArea(Area a) {
        this.discovered.put(a.getName(), System.currentTimeMillis());
        this.saveToData();
    }

    /**
     * Checks if player has discovered certain area
     *
     * @param a Area
     * @return boolean
     */

    public boolean hasDiscovered(String a) {
        return discovered.containsKey(a);
    }

    /**
     * Reloads player's discovered areas from data file
     */

    public void reload() {
        HashMap<String, Long> discovered = new HashMap<>();
        if (DiscoverMain.getData().getStringList(player.getUniqueId().toString()).isEmpty()) this.discovered = discovered;
        else {
            for (String s : DiscoverMain.getData().getStringList(player.getUniqueId().toString())) {
                String[] split = s.split(":");
                if (split.length != 2) {
                    // discovery time not defined
                    discovered.put(s, 0L);
                } else {
                    discovered.put(s, Long.valueOf(split[1]));
                }
            }
        }
    }
}
