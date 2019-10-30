package net.ignissak.discoverareas.utils.title;

import org.bukkit.entity.Player;

public interface TitleVersionHandle {

    public void clearTitle(Player player);

    public void resetTitle(Player player);

    public void send(Player player, String title, String subtitle, int fadeInTime, int stayTime, int fadeOutTime);

    public void updateSubtitle(Player player, String subtitle);

    public void updateTimes(Player player, int fadeInTime, int stayTime, int fadeOutTime);

    public void updateTitle(Player player, String title);

}
