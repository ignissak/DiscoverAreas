package net.ignissak.discoverareas.menu;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import net.ignissak.discoverareas.DiscoverAreasPlugin;
import net.ignissak.discoverareas.objects.Area;
import net.ignissak.discoverareas.utils.Utils;
import net.ignissak.discoverareas.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AreasAdminMenu implements InventoryProvider {

    @Override
    public void update(Player player, InventoryContents contents) {}

    @Override
    public void init(Player player, InventoryContents contents) {
        final Pagination pagination = contents.pagination();
        final List<ClickableItem> items = new ArrayList<>();

        for (Area area : DiscoverAreasPlugin.getInstance().getCache()) {
            ItemBuilder itemBuilder = new ItemBuilder(area.getMaterial(), 1);

            itemBuilder.setName(Utils.replaceAreaPlaceholders(area, DiscoverAreasPlugin.getConfiguration().getString("gui.list.admin.displayname", "&a@area")));

            final List<String> stringList = DiscoverAreasPlugin.getConfiguration().getStringList("gui.list.admin.lore");
            itemBuilder.setLore(Utils.replaceAreaPlaceholders(area, stringList));

            items.add(ClickableItem.empty(itemBuilder.build())); // TODO: Area menu
        }

        ClickableItem[] c = new ClickableItem[items.size()];
        c = items.toArray(c);
        pagination.setItems(c);
        pagination.setItemsPerPage(45);

        if (items.size() > 0 && !pagination.isLast()) {
            contents.set(5, 7, ClickableItem.of(new ItemBuilder(Material.valueOf(DiscoverAreasPlugin.getConfiguration().getString("gui.next.material", "ARROW"))).setName(Utils.colorCode(DiscoverAreasPlugin.getConfiguration().getString("gui.next.displayname", "&7Next page"))).build(), e -> {
                contents.inventory().open(player, pagination.next().getPage());
            }));
        }
        if (!pagination.isFirst()) {
            contents.set(5, 1, ClickableItem.of(new ItemBuilder(Material.valueOf(DiscoverAreasPlugin.getConfiguration().getString("gui.previous.material", "ARROW"))).setName(Utils.colorCode(DiscoverAreasPlugin.getConfiguration().getString("gui.previous.displayname", "&7Previous page"))).build(), e -> {
                contents.inventory().open(player, pagination.previous().getPage());
            }));
        }

        ItemBuilder statistics = new ItemBuilder(Material.valueOf(DiscoverAreasPlugin.getConfiguration().getString("gui.stats.material", "PAPER")));

        statistics.setName(Utils.colorCode(DiscoverAreasPlugin.getConfiguration().getString("gui.stats.displayname")));

        final List<String> stringList = DiscoverAreasPlugin.getConfiguration().getStringList("gui.stats.lore");
        final List<String> lore = new ArrayList<>();
        for (String s : stringList) {
            lore.add(Utils.colorCode(s).replace("@areas", String.valueOf(DiscoverAreasPlugin.getInstance().getCache().size())));
        }
        statistics.setLore(lore);

        contents.set(5, 4, ClickableItem.empty(statistics.build()));
    }
}
