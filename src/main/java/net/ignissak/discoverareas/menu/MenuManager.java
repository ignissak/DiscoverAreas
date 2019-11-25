package net.ignissak.discoverareas.menu;

import com.google.common.collect.Lists;
import net.ignissak.discoverareas.DiscoverMain;
import net.ignissak.discoverareas.discover.DiscoverPlayer;
import net.ignissak.discoverareas.menu.items.MenuItem;
import net.ignissak.discoverareas.objects.Area;
import net.ignissak.discoverareas.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class MenuManager {

    private HashMap<String, Menu> menus;
    ItemStack previous, next;

    public MenuManager() {
        this.menus = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(new MenuListener(), DiscoverMain.getInstance());

        this.previous = DiscoverMain.getInstance().getPrevious().build();
        this.next = DiscoverMain.getInstance().getNext().build();

        updateMenus();
    }

    public Menu getMenu(String menuName) {
        Menu menu = this.menus.getOrDefault(menuName, null);
        if (menu == null) return null;

        boolean updated = false;
        for (MenuItem item : menu.getItems()) {
            if (item == null) continue;
            if (item.isUpdated()) {
                item.update();
                updated = true;
            }
        }
        if (updated) menu.refreshItems();
        return menu;
    }

    public Menu getMenuByTitle(String title) {
        for (Menu m : this.menus.values()) {
            if (m.getTitle().equals(title)) return m;
        }
        return null;
    }

    public void updateUserMenu(Player player) {
        DiscoverPlayer discoverPlayer = DiscoverMain.getDiscoverPlayer(player);
        List<Area> areaList = new ArrayList<>(DiscoverMain.getInstance().getCache());
        areaList.sort(Comparator.comparing(a -> a.hasDiscovered(discoverPlayer)));

        if (areaList.size() <= 45) {
            Iterator<Area> iterator = areaList.iterator();
            MenuItem[] items = new MenuItem[54];
            int i = 0;
            while (iterator.hasNext()) {
                Area a = iterator.next();
                if (a.hasDiscovered(discoverPlayer)) {
                    items[i] = new MenuItem(getDiscovered(a), p -> {}, false);
                } else {
                    items[i] = new MenuItem(getUndiscovered(a), p -> {}, false);
                }
                i++;
                iterator.remove();

                Menu userMenu = new Menu(DiscoverMain.getConfiguration().getString("menus.user.titles.nopage"), items);
                this.menus.put("userMenu_" + player.getName(), userMenu);
            }
        } else {
            //pages
            List<List<Area>> pagesOfAreas = Lists.partition(areaList, 45);
            int page = 1;
            for (List<Area> areas : pagesOfAreas) {
                MenuItem[] items = new MenuItem[54];
                int i = 0;
                for (Area a : areas) {
                    if (a.hasDiscovered(discoverPlayer)) {
                        items[i] = new MenuItem(getDiscovered(a), p -> {}, false);
                    } else {
                        items[i] = new MenuItem(getUndiscovered(a), p -> {}, false);
                    }
                    i++;
                }

                if (page < pagesOfAreas.size()) {
                    int nextPage = page + 1;
                    items[52] = new MenuItem(next, p -> {
                        player.openInventory(getMenu("userMenu_" + player.getName() + "_" + nextPage).getInventory());
                        player.playSound(player.getLocation(), Sound.BLOCK_GLASS_HIT, 1, 0);
                    });
                }
                if (page > 1) {
                    int previousPage = page - 1;
                    items[46] = new MenuItem(previous, p -> {
                        player.openInventory(getMenu("userMenu_" + player.getName() + "_" + previousPage).getInventory());
                        player.playSound(player.getLocation(), Sound.BLOCK_GLASS_HIT, 1, 0);
                    });
                }


                Menu adminMenu = new Menu(DiscoverMain.getConfiguration().getString("menus.user.titles.page").replace("@page", String.valueOf(page)), items);
                this.menus.put("userMenu_" + player.getName() + "_" + page, adminMenu);

                page++;
            }
        }
    }

    public void updateMenus() {
        this.menus.clear();
        if (DiscoverMain.getConfiguration().getBoolean("menus.user.enabled")) Bukkit.getOnlinePlayers().forEach(this::updateUserMenu);
        if (!DiscoverMain.getConfiguration().getBoolean("menus.admin.enabled")) return;
        List<Area> areasList = new ArrayList<>(DiscoverMain.getInstance().getCache());
        areasList.sort(Comparator.comparing(Area::getName));

        if (areasList.size() <= 45) {
            Iterator<Area> iterator = areasList.iterator();
            MenuItem[] items = new MenuItem[54];
            int i = 0;
            while (iterator.hasNext()) {
                Area a = iterator.next();
                items[i] = new MenuItem(getAdmin(a), a::sendCommands, true);
                i++;
                iterator.remove();
            }

            if (DiscoverMain.getConfiguration().getBoolean("gui.stats.enabled")) {
                items[49] = new MenuItem(getStatistics(), p -> {
                }, false);
            }

            Menu adminMenu = new Menu(DiscoverMain.getConfiguration().getString("menus.admin.titles.nopage"), items);
            this.menus.put("adminMenu", adminMenu);
        } else {
            //pages
            List<List<Area>> pagesOfAreas = Lists.partition(areasList, 45);
            int page = 1;
            for (List<Area> areas : pagesOfAreas) {
                MenuItem[] items = new MenuItem[54];
                int i = 0;
                for (Area a : areas) {
                    items[i] = new MenuItem(getAdmin(a), a::sendCommands,true);
                    i++;
                }

                if (DiscoverMain.getConfiguration().getBoolean("gui.stats.enabled")) {
                    items[49] = new MenuItem(getStatistics(), p -> {
                    }, false);
                }

                if (page < pagesOfAreas.size()) {
                    int nextPage = page + 1;
                    items[52] = new MenuItem(next, player -> {
                        player.openInventory(getMenu("adminMenu" + nextPage).getInventory());
                        player.playSound(player.getLocation(), Sound.BLOCK_GLASS_HIT, 1, 0);
                    });
                }
                if (page > 1) {
                    int previousPage = page - 1;
                    items[46] = new MenuItem(previous, player -> {
                        player.openInventory(getMenu("adminMenu" + previousPage).getInventory());
                        player.playSound(player.getLocation(), Sound.BLOCK_GLASS_HIT, 1, 0);
                    });
                }


                Menu adminMenu = new Menu(DiscoverMain.getConfiguration().getString("menus.admin.titles.page").replace("@page", String.valueOf(page)), items);
                this.menus.put("adminMenu" + page, adminMenu);

                page++;
            }
            }
        }

        private ItemStack getStatistics() {
            List<String> configLore = DiscoverMain.getConfiguration().getStringList("gui.stats.lore");
            List<String> lore = new ArrayList<>();
            for (String s : configLore) lore.add(ChatColor.translateAlternateColorCodes('&', s
                    .replace("@areas", String.valueOf(DiscoverMain.getInstance().getCache().size()))));
            ItemBuilder stats = new ItemBuilder(Material.valueOf(DiscoverMain.getConfiguration().getString("gui.stats.material")), 1)
                    .setName(ChatColor.translateAlternateColorCodes('&', DiscoverMain.getConfiguration().getString("gui.stats.displayname")))
                    .setLore(lore);
            return stats.build();
        }

    private ItemStack getAdmin(Area a) {
        List<String> configLore = DiscoverMain.getConfiguration().getStringList("gui.list.admin.lore");
        List<String> lore = new ArrayList<>();
        for (String s : configLore) lore.add(ChatColor.translateAlternateColorCodes('&', s
                .replace("@area", a.getName())
                .replace("@description", a.getDescription())
                .replace("@world", a.getWorld().getName())
                .replace("@region", a.getRegion().getId())));
        ItemBuilder discovered = new ItemBuilder(Material.valueOf(DiscoverMain.getConfiguration().getString("gui.list.admin.material")), 1)
                .setName(ChatColor.translateAlternateColorCodes('&', DiscoverMain.getConfiguration().getString("gui.list.admin.displayname").replace("@area", a.getName())))
                .setLore(lore);
        if (DiscoverMain.getConfiguration().getBoolean("gui.list.admin.glowing")) discovered.setGlowing();

        return discovered.build();
    }

        private ItemStack getDiscovered(Area a) {
            List<String> configLore = DiscoverMain.getConfiguration().getStringList("gui.list.discovered.lore");
            List<String> lore = new ArrayList<>();
            for (String s : configLore) lore.add(ChatColor.translateAlternateColorCodes('&', s
                    .replace("@area", a.getName())
                    .replace("@description", a.getDescription())
                    .replace("@world", a.getWorld().getName())
                    .replace("@region", a.getRegion().getId())));
            ItemBuilder discovered = new ItemBuilder(Material.valueOf(DiscoverMain.getConfiguration().getString("gui.list.discovered.material")), 1)
                    .setName(ChatColor.translateAlternateColorCodes('&', DiscoverMain.getConfiguration().getString("gui.list.discovered.displayname").replace("@area", a.getName())))
                    .setLore(lore);
            if (DiscoverMain.getConfiguration().getBoolean("gui.list.discovered.glowing")) discovered.setGlowing();
            
            return discovered.build();
        }

        private ItemStack getUndiscovered(Area a) {
            List<String> configLore = DiscoverMain.getConfiguration().getStringList("gui.list.notdiscovered.lore");
            List<String> lore = new ArrayList<>();
            for (String s : configLore) lore.add(ChatColor.translateAlternateColorCodes('&', s
                    .replace("@area", a.getName())
                    .replace("@description", a.getDescription())
                    .replace("@world", a.getWorld().getName())
                    .replace("@region", a.getRegion().getId())));
            ItemBuilder undiscovered = new ItemBuilder(Material.valueOf(DiscoverMain.getConfiguration().getString("gui.list.notdiscovered.material")), 1)
                    .setName(ChatColor.translateAlternateColorCodes('&', DiscoverMain.getConfiguration().getString("gui.list.notdiscovered.displayname").replace("@area", a.getName())))
                    .setLore(lore);
            if (DiscoverMain.getConfiguration().getBoolean("gui.list.notdiscovered.glowing")) undiscovered.setGlowing();

            return undiscovered.build();
        }

    private Menu getAreaGUI(Area area) {
        MenuItem[] items = new MenuItem[9];

        items[2] = new MenuItem(new ItemBuilder(Material.COMMAND_BLOCK, 1).setName(ChatColor.translateAlternateColorCodes('&', "&aRewards"))
                .setLore(ChatColor.translateAlternateColorCodes('&', "&7Commands: &f" + area.getRewardCommands().size()),
                        ChatColor.translateAlternateColorCodes('&', "&7XP reward: &f" + area.getXp()),
                        ChatColor.translateAlternateColorCodes('&', "&6Click to view commands."))
                .build(),
                area::sendCommands, true);
        items[3] = new MenuItem(new ItemBuilder(Material.FEATHER, 1).setName(ChatColor.translateAlternateColorCodes('&', "&aStatistics"))
                .setLore(ChatColor.translateAlternateColorCodes('&', "&7Region: &f" + area.getRegion().getId()),
                        ChatColor.translateAlternateColorCodes('&', "&7World: &f" + area.getWorld().getName()),
                        ChatColor.translateAlternateColorCodes('&', "&7Discovered: &f" + area.getDiscoveredBy().size()),
                        ChatColor.translateAlternateColorCodes('&', "&7Created: &f" + DateUtils.formatDate(area.getCreatedAt())))
                .build(),
                p -> {
                }, false);
        items[4] = new MenuItem(new ItemBuilder(Material.MUSIC_DISC_13, 1).setName(ChatColor.translateAlternateColorCodes('&', "&aSound"))
                .setLore(ChatColor.translateAlternateColorCodes('&', "&7Discovery sound:"),
                        ChatColor.translateAlternateColorCodes('&', "&f" + area.getDiscoverySound().toString()),
                        ChatColor.translateAlternateColorCodes('&', "&6Click to listen."),
                        ChatColor.translateAlternateColorCodes('&', "&6Shift-click to change."))
                .hideAllFlags()
                .build(),
                p -> p.playSound(p.getLocation(), area.getDiscoverySound(), 1, 0), p -> p.performCommand("area setsound " + area.getName()), false);

        Menu menu = new Menu("Area: " + area.getName(), items);
        this.menus.put("area_" + area.getName(), menu);
        return menu;
    }

    public void openAreaGUI(Player player, Area area) {
        player.openInventory(getAreaGUI(area).getInventory());
    }
}
