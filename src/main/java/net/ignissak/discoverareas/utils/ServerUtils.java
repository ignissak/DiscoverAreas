package net.ignissak.discoverareas.utils;

import net.ignissak.discoverareas.DiscoverAreasPlugin;
import net.ignissak.discoverareas.objects.ServerVersion;

public class ServerUtils {

    public static ServerVersion getServerVersion() {
        if (DiscoverAreasPlugin.getInstance().getServer().getVersion().contains("1_14")) {
            return ServerVersion.V1_14;
        } else if (DiscoverAreasPlugin.getInstance().getServer().getVersion().contains("1_15")) {
            return ServerVersion.V1_15;
        } else if (DiscoverAreasPlugin.getInstance().getServer().getVersion().contains("1_16")) {
            return ServerVersion.V1_16;
        }
        return ServerVersion.UNSUPPORTED;
    }
}
