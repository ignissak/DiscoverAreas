package net.ignissak.discoverareas.discover;

import net.ignissak.discoverareas.DiscoverMain;
import net.ignissak.discoverareas.objects.Area;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DiscoverPlayer {

    private Player player;
    private List<String> discovered;

    public DiscoverPlayer(Player player) {
        this.player = player;
        if (DiscoverMain.getData().getStringList(player.getUniqueId().toString()).isEmpty()) discovered = new ArrayList<>();
        else {
            this.discovered = new ArrayList<>(DiscoverMain.getData().getStringList(player.getUniqueId().toString()));
        }
    }

    public void saveToData() {
        if (getDiscovered().size() == 0) return;
        List<String> list = new ArrayList<>(getDiscovered());
        DiscoverMain.getData().set(player.getUniqueId().toString(), list);
        DiscoverMain.getInstance().saveFiles();
    }

    public Player getPlayer() {
        return player;
    }

    public List<String> getDiscovered() {
        return discovered;
    }

    public List<Area> getNotDiscovered() {
        List<Area> out = new ArrayList<>();
        for (Area a : DiscoverMain.getInstance().getCache()) {
            if (getDiscovered().contains(a.getName())) continue;
            out.add(a);
        }
        return out;
    }

    public void resetProgress() throws NullPointerException {
        this.discovered.clear();
        DiscoverMain.getData().set(player.getUniqueId().toString(), null);
        DiscoverMain.getInstance().saveFiles();
    }

    public void addDiscoveredArea(String a) {
        this.discovered.add(a);
        this.saveToData();
    }

    public boolean hasDiscovered(String a) {
        return discovered.contains(a);
    }

    public void reload() {
        if (DiscoverMain.getData().getStringList(player.getUniqueId().toString()).isEmpty()) discovered = new ArrayList<>();
        else {
            this.discovered = new ArrayList<>(DiscoverMain.getData().getStringList(player.getUniqueId().toString()));
        }
    }
}
