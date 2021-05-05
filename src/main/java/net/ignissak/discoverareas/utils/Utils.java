package net.ignissak.discoverareas.utils;

import net.ignissak.discoverareas.DiscoverAreasPlugin;
import net.ignissak.discoverareas.objects.Area;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    public static int getNextAreaId() {
        final List<Integer> integerList = DiscoverAreasPlugin.getAreasFile().getKeys(false).stream().sorted().map(Integer::parseInt).collect(Collectors.toList());
        if (integerList.size() == 0) return 0;
        return integerList.get(integerList.size() - 1) + 1;
    }

    public static String replaceAreaPlaceholders(Area area, String string) {
        return ChatColor.translateAlternateColorCodes('&', string
                .replace("@area", area.getName())
                .replace("@id", String.valueOf(area.getId()))
                .replace("@description", area.getDescription())
                .replace("@world", area.getWorld().getName())
                .replace("@region", area.getRegion().getId()));
    }

    public static List<String> replaceAreaPlaceholders(Area area, List<String> list) {
        final List<String> lore = new ArrayList<>();
        for (String s : list) {
            lore.add(Utils.replaceAreaPlaceholders(area, s));
        }
        return lore;
    }

    public static String colorCode(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
