package net.ignissak.discoverareas.utils;

import net.ignissak.discoverareas.DiscoverMain;
import net.ignissak.discoverareas.objects.ServerVersion;

public class ServerUtils {

    public ServerVersion getServerVersion() {
        if (DiscoverMain.getInstance().getServer().getVersion().contains("1_9")) {
            return ServerVersion.V1_9;
        }
        else if (DiscoverMain.getInstance().getServer().getVersion().contains("1_10")) {
            return ServerVersion.V1_10;
        }
        else if (DiscoverMain.getInstance().getServer().getVersion().contains("1_11")) {
            return ServerVersion.V1_11;
        }
        else if (DiscoverMain.getInstance().getServer().getVersion().contains("1_12")) {
            return ServerVersion.V1_12;
        }
        else if (DiscoverMain.getInstance().getServer().getVersion().contains("1_13")) {
            return ServerVersion.V1_13;
        }
        else if (DiscoverMain.getInstance().getServer().getVersion().contains("1_14")) {
            return ServerVersion.V1_14;
        }
        return ServerVersion.UNSUPPORTED;
    }
}
