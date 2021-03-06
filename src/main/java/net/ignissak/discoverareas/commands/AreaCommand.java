package net.ignissak.discoverareas.commands;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.ignissak.discoverareas.DiscoverAreasPlugin;
import net.ignissak.discoverareas.discover.DiscoverPlayer;
import net.ignissak.discoverareas.objects.Area;
import net.ignissak.discoverareas.utils.ChatInfo;
import net.ignissak.discoverareas.utils.chatinput.ChatInput;
import net.ignissak.discoverareas.utils.chatinput.ChatInputType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class AreaCommand implements CommandExecutor, TabCompleter, Listener {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        if (!player.hasPermission("discoverareas.admin")) {
            player.sendMessage(ChatColor.GOLD + "This server runs DiscoverAreas version " + DiscoverAreasPlugin.getInstance().getDescription().getVersion() + ".");
            return true;
        } else {

            if (args.length <= 0) {
                showHelp(player);
                return true;
            }

            World w = player.getWorld();

            switch (args[0].toLowerCase()) {
                case "create":
                case "add":
                    // /area add <region> <name>
                    if (args.length >= 3) {
                        try {
                            String regionName = args[1];
                            StringBuilder sb = new StringBuilder();
                            for (int i = 2; i < args.length; i++) {
                                sb.append(args[i]);
                                if (i + 1 != args.length) {
                                    sb.append(" ");
                                }
                            }
                            String name = sb.toString();

                            if (DiscoverAreasPlugin.getInstance().existsArea(name)) {
                                ChatInfo.error(player, "Area with name '" + name + "' already exists.");
                                break;
                            }

                            if (DiscoverAreasPlugin.getInstance().isRegionUsed(regionName)) {
                                ChatInfo.error(player, "Region with name '" + regionName + "' is already associated with other area.");
                                break;
                            }

                            RegionManager rm = DiscoverAreasPlugin.getRegionContainer().get(new BukkitWorld(w));
                            if (!rm.hasRegion(regionName)) {
                                ChatInfo.error(player, "Could not find region '" + regionName + "' in this world.");
                                break;
                            }

                            ProtectedRegion region = rm.getRegion(regionName);
                            Area area = new Area(region, w, name, "Default description - change in config.", 0, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, new ArrayList<>(), System.currentTimeMillis());

                            area.addToCache();
                            DiscoverAreasPlugin.getMenuManager().updateMenus();

                            ChatInfo.success(player, "Successfully created area '" + name + "'.");
                            ChatInfo.info(player, "To change settings, edit config.yml file.");
                            break;
                        } catch (NullPointerException e) {
                            ChatInfo.error(player, "Could not create area. Check console for error.");
                            e.printStackTrace();
                            break;
                        }
                    } else {
                        ChatInfo.error(player, "Usage: /area add <region> <name>");
                        break;
                    }
                case "delete":
                case "remove":
                    // /area remove <name>
                    if (args.length >= 2) {
                        try {
                            StringBuilder sb = new StringBuilder();
                            for (int i = 1; i < args.length; i++) {
                                sb.append(args[i]);
                                if (i + 1 != args.length) {
                                    sb.append(" ");
                                }
                            }
                            String name = sb.toString();

                            if (!DiscoverAreasPlugin.getInstance().existsArea(name)) {
                                ChatInfo.error(player, "Area with name '" + name + "' does not exist.");
                                break;
                            }

                            Area area = DiscoverAreasPlugin.getInstance().getCache().stream().filter(a -> a.getName().equals(name)).findFirst().get();
                            area.delete();

                            ChatInfo.success(player, "Area with name '" + name + "' was successfully deleted.");
                            break;
                        } catch (Exception e) {
                            ChatInfo.error(player, "Could not delete area. Check console for error.");
                            e.printStackTrace();
                            break;
                        }
                    } else {
                        ChatInfo.error(player, "Usage: /area remove <name>");
                        break;
                    }
                case "setexp":
                case "setxp":
                    // /area setxp <name> <xp>
                    if (args.length >= 2) {
                        try {
                            StringBuilder sb = new StringBuilder();
                            for (int i = 1; i < args.length; i++) {
                                sb.append(args[i]);
                                if (i + 1 != args.length) {
                                    sb.append(" ");
                                }
                            }

                            String name = sb.toString();

                            if (!DiscoverAreasPlugin.getInstance().existsArea(name)) {
                                ChatInfo.error(player, "Area with name '" + name + "' does not exist.");
                                break;
                            }

                            Area area = DiscoverAreasPlugin.getInstance().getCache().stream().filter(a -> a.getName().equals(name)).findFirst().get();

                            ChatInfo.info(player, "Insert new reward XP value, type 'cancel' to cancel.");
                            ChatInput chatInput = new ChatInput(player, ChatInputType.EDIT_EXP);
                            chatInput.setChatInputCompleteMethod((p, m) -> {
                                try {
                                    int xp = Integer.parseInt(m);
                                    area.setXp(xp);
                                    area.updateData();
                                    ChatInfo.success(player, "XP gained for discovery of area '" + name + "' was set to " + xp + ".");
                                } catch (Exception e) {
                                    if (e instanceof NumberFormatException) {
                                        ChatInfo.error(player, "Entry was not a number, try again.");
                                    } else ChatInfo.error(player, "There was an error while removing command.");
                                }
                            });
                            break;
                        } catch (NumberFormatException e) {
                            ChatInfo.error(player, "Could not format XP value. Example: /area setxp Northern Kingdom 100");
                            break;
                        }
                    } else {
                        ChatInfo.error(player, "Usage: /area setxp <name>");
                        break;
                    }
                case "setsound":
                    // /area setxp <name>
                    if (args.length >= 2) {
                        try {
                            StringBuilder sb = new StringBuilder();
                            for (int i = 1; i < args.length; i++) {
                                sb.append(args[i]);
                                if (i + 1 != args.length) {
                                    sb.append(" ");
                                }
                            }

                            String name = sb.toString();

                            if (!DiscoverAreasPlugin.getInstance().existsArea(name)) {
                                ChatInfo.error(player, "Area with name '" + name + "' does not exist.");
                                break;
                            }

                            player.closeInventory();
                            Area area = DiscoverAreasPlugin.getInstance().getCache().stream().filter(a -> a.getName().equals(name)).findFirst().get();

                            ChatInfo.info(player, "Insert new sound, type 'cancel' to cancel.");
                            ChatInfo.info(player, "To view all available sounds go to Spigot documentation.");
                            ChatInput chatInput = new ChatInput(player, ChatInputType.EDIT_EXP);
                            chatInput.setChatInputCompleteMethod((p, m) -> {
                                try {
                                    Sound sound = Sound.valueOf(m);
                                    area.setDiscoverySound(sound);
                                    area.updateData();
                                    ChatInfo.success(player, "Discovery sound of area '" + name + "' was set to " + sound + ".");
                                    player.playSound(player.getLocation(), sound, 1, 0);
                                } catch (Exception e) {
                                    if (e instanceof IllegalArgumentException) {
                                        ChatInfo.error(player, "Invalid sound, try again.");
                                    } else ChatInfo.error(player, "There was an error while settings sound.");
                                }
                            });
                            break;
                        } catch (NumberFormatException e) {
                            ChatInfo.error(player, "Could not format XP value. Example: /area setxp Northern Kingdom 100");
                            break;
                        }
                    } else {
                        ChatInfo.error(player, "Usage: /area setxp <name>");
                        break;
                    }
                case "setdescription":
                case "setdesc":
                    // /area setdesc <name>
                    if (args.length >= 2) {
                        try {
                            StringBuilder sb = new StringBuilder();
                            for (int i = 1; i < args.length; i++) {
                                sb.append(args[i]);
                                if (i + 1 != args.length) {
                                    sb.append(" ");
                                }
                            }

                            String name = sb.toString();

                            if (!DiscoverAreasPlugin.getInstance().existsArea(name)) {
                                ChatInfo.error(player, "Area with name '" + name + "' does not exist.");
                                break;
                            }

                            Area area = DiscoverAreasPlugin.getInstance().getCache().stream().filter(a -> a.getName().equals(name)).findFirst().get();

                            ChatInfo.info(player, "Enter new description. Type 'cancel' if you want to cancel.");
                            ChatInput chatInput = new ChatInput(player, ChatInputType.EDIT_DESCRIPTION);
                            chatInput.setChatInputCompleteMethod((p, m) -> {
                                try {
                                    area.setDescription(m);
                                    area.updateData();
                                    ChatInfo.success(player, "Description of area '" + name + "' was successfully changed.");
                                } catch (Exception e) {
                                    ChatInfo.error(player, "There was an error while setting new description.");
                                }
                            });
                            break;
                        } catch (Exception e) {
                            ChatInfo.error(player, "Could not set description. Check console for error.");
                            e.printStackTrace();
                            break;
                        }
                    } else {
                        ChatInfo.error(player, "Usage: /area setdesc <name>");
                        break;
                    }
                case "reset":
                    // /area reset [player]
                    if (args.length <= 1) {
                        try {
                            DiscoverPlayer discoverPlayer = DiscoverAreasPlugin.getDiscoverPlayer(player);

                            if (!DiscoverAreasPlugin.getInstance().isInData(discoverPlayer.getPlayer().getUniqueId().toString())) {
                                ChatInfo.error(player, "There is no data associated to your UUID.");
                                break;
                            }

                            discoverPlayer.resetProgress();
                            DiscoverAreasPlugin.getMenuManager().updateMenus();

                            ChatInfo.success(player, "Your progress was reset.");
                            break;
                        } catch (Exception e) {
                            ChatInfo.error(player, "Could not reset progress. Check console for error.");
                            e.printStackTrace();
                            break;
                        }
                    } else {
                        String target = args[1];
                        try {
                            if (target.matches("/^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$/")) {
                                //uuid
                                if (!DiscoverAreasPlugin.getInstance().isInData(target)) {
                                    ChatInfo.error(player, "There is no data associated to this UUID.");
                                    break;
                                }

                                if (Bukkit.getPlayer(UUID.fromString(target)) != null) {
                                    DiscoverPlayer targetPlayer = DiscoverAreasPlugin.getDiscoverPlayer(Bukkit.getPlayer(UUID.fromString(target)));
                                    targetPlayer.resetProgress();
                                } else {
                                    DiscoverAreasPlugin.getData().set(target, null);
                                    DiscoverAreasPlugin.getInstance().saveFiles();
                                }
                                DiscoverAreasPlugin.getMenuManager().updateMenus();

                                ChatInfo.success(player, "Data were successfully reset.");
                                break;
                            } else {
                                if (Bukkit.getPlayer(target) == null) {
                                    ChatInfo.error(player, "Player must be online in order to reset his data (you can use this command with his UUID).");
                                    break;
                                }

                                DiscoverPlayer targetPlayer = DiscoverAreasPlugin.getDiscoverPlayer(Bukkit.getPlayer(target));

                                if (!DiscoverAreasPlugin.getInstance().isInData(targetPlayer.getPlayer().getUniqueId().toString())) {
                                    ChatInfo.error(player, "There is no data associated to this player's UUID.");
                                    break;
                                }

                                targetPlayer.resetProgress();
                                DiscoverAreasPlugin.getMenuManager().updateMenus();

                                ChatInfo.success(player, "Data of player '" + targetPlayer.getPlayer().getName() + "' were successfully reset.");
                                break;
                            }
                        } catch (Exception e) {
                            ChatInfo.error(player, "Could not reset progress. Check console for error.");
                            e.printStackTrace();
                            break;
                        }
                    }
                case "command":
                    // area command add <id>
                    if (args.length >= 3) {
                        switch (args[1].toLowerCase()) {
                            case "list":
                                StringBuilder sb1 = new StringBuilder();
                                for (int i = 2; i < args.length; i++) {
                                    sb1.append(args[i]);
                                    if (i + 1 != args.length) {
                                        sb1.append(" ");
                                    }
                                }
                                String name1 = sb1.toString();

                                if (!DiscoverAreasPlugin.getInstance().existsArea(name1)) {
                                    ChatInfo.error(player, "Area with name '" + name1 + "' does not exist.");
                                    break;
                                }

                                Area area1 = DiscoverAreasPlugin.getInstance().getCache().stream().filter(a -> a.getName().equals(name1)).findFirst().get();

                                try {
                                    area1.sendCommands(player);
                                } catch (Exception e) {
                                    ChatInfo.error(player, "Could not display command list. Check console for error.");
                                    e.printStackTrace();
                                    break;
                                }
                                break;
                            case "add":
                                StringBuilder sb = new StringBuilder();
                                for (int i = 2; i < args.length; i++) {
                                    sb.append(args[i]);
                                    if (i + 1 != args.length) {
                                        sb.append(" ");
                                    }
                                }
                                String name = sb.toString();

                                if (!DiscoverAreasPlugin.getInstance().existsArea(name)) {
                                    ChatInfo.error(player, "Area with name '" + name + "' does not exist.");
                                    break;
                                }

                                Area area = DiscoverAreasPlugin.getInstance().getCache().stream().filter(a -> a.getName().equals(name)).findFirst().get();

                                ChatInfo.info(player, "Enter new command you would like to add, to cancel type 'cancel'.");
                                ChatInfo.info(player, "Use @player placeholder for player's nick.");
                                ChatInput chatInput = new ChatInput(player, ChatInputType.ADD_COMMAND);
                                chatInput.setChatInputCompleteMethod((p, m) -> {
                                    try {
                                        area.addRewardCommand(m);
                                        area.updateData();
                                        ChatInfo.success(player, "Successfully added new command for area '" + name + "'.");
                                    } catch (Exception e) {
                                        ChatInfo.error(player, "There was an error while adding new command.");
                                    }
                                });
                                break;
                            case "remove":
                                //area command remove <id> <name>
                                try {
                                    StringBuilder sb2 = new StringBuilder();
                                    for (int i = 3; i < args.length; i++) {
                                        sb2.append(args[i]);
                                        if (i + 1 != args.length) {
                                            sb2.append(" ");
                                        }
                                    }
                                    String name2 = sb2.toString();
                                    int id = Integer.parseInt(args[2]);

                                    if (!DiscoverAreasPlugin.getInstance().existsArea(name2)) {
                                        ChatInfo.error(player, "Area with name '" + name2 + "' does not exist.");
                                        break;
                                    }

                                    Area area2 = DiscoverAreasPlugin.getInstance().getCache().stream().filter(a -> a.getName().equals(name2)).findFirst().get();

                                    if (area2.getRewardCommands().isEmpty()) {
                                        ChatInfo.error(player, "This area does not have any commands defined.");
                                        break;
                                    }

                                    if (id > area2.getRewardCommands().size() - 1 || id < 0) {
                                        if (area2.getRewardCommands().size() == 1) {
                                            ChatInfo.error(player, "Invalid ID of command. Range: 0");
                                        } else {
                                            ChatInfo.error(player, "Invalid ID of command. Range: 0-" + (area2.getRewardCommands().size() - 1));
                                        }
                                        break;
                                    }

                                    area2.getRewardCommands().remove(id);
                                    area2.updateData();
                                    ChatInfo.success(player, "Successfully removed command with ID " + id + " for area '" + name2 + "'.");
                                    break;
                                } catch (Exception e) {
                                    ChatInfo.error(player, "Usage: /area command remove <area>");
                                    break;
                                }
                            case "edit":
                                //area command edit <id> <area>
                                try {
                                    StringBuilder sb2 = new StringBuilder();
                                    for (int i = 3; i < args.length; i++) {
                                        sb2.append(args[i]);
                                        if (i + 1 != args.length) {
                                            sb2.append(" ");
                                        }
                                    }
                                    String name2 = sb2.toString();
                                    int id = Integer.parseInt(args[2]);

                                    if (!DiscoverAreasPlugin.getInstance().existsArea(name2)) {
                                        ChatInfo.error(player, "Area with name '" + name2 + "' does not exist.");
                                        break;
                                    }

                                    Area area2 = DiscoverAreasPlugin.getInstance().getCache().stream().filter(a -> a.getName().equals(name2)).findFirst().get();

                                    if (area2.getRewardCommands().isEmpty()) {
                                        ChatInfo.error(player, "This area does not have any commands defined.");
                                        break;
                                    }

                                    if (id > area2.getRewardCommands().size() - 1) {
                                        ChatInfo.error(player, "Invalid ID of command. Maximum: " + area2.getRewardCommands().size());
                                        break;
                                    }

                                    ChatInfo.info(player, "Enter new command to replace, to cancel type 'cancel'.");
                                    ChatInfo.info(player, "Use @player placeholder for player's nick.");
                                    ChatInput chatInput2 = new ChatInput(player, ChatInputType.EDIT_COMMAND);
                                    chatInput2.setChatInputCompleteMethod((p, m) -> {
                                        try {
                                            area2.getRewardCommands().set(id, m);
                                            area2.updateData();
                                            ChatInfo.success(player, "Successfully edited command with ID " + id + " for area '" + name2 + "'.");
                                        } catch (Exception e) {
                                            if (e instanceof NumberFormatException) {
                                                ChatInfo.error(player, "Entry was not a number, try again.");
                                            } else ChatInfo.error(player, "There was an error while removing command.");
                                        }
                                    });
                                    break;
                                } catch (Exception e) {
                                    if (e instanceof NumberFormatException) {
                                        ChatInfo.error(player, "Could not format ID value. Example: /area command edit 0 Northern Kingdom");
                                    } else ChatInfo.error(player, "Usage: /area command remove <area>");
                                    break;
                                }
                            default:
                                showHelp(player);
                                break;
                        }
                    } else {
                        showHelp(player);
                        break;
                    }
                    break;
                case "reload":
                    ChatInfo.info(player, "Reloading...");
                    try {
                        ChatInfo.info(player, "1/4: Reloading files...");
                        DiscoverAreasPlugin.getInstance().reloadFiles();
                        ChatInfo.info(player, "2/4: Reloading areas...");
                        DiscoverAreasPlugin.getInstance().getCache().clear();
                        DiscoverAreasPlugin.getInstance().cacheAreas();
                        ChatInfo.info(player, "3/4: Reloading players...");
                        DiscoverAreasPlugin.getInstance().getPlayers().values().forEach(DiscoverPlayer::reload);
                        ChatInfo.info(player, "4/4: Reloading menus...");
                        DiscoverAreasPlugin.getMenuManager().updateMenus();

                        ChatInfo.success(player, "Successfully reloaded all files.");
                        break;
                    } catch (Exception e) {
                        ChatInfo.error(player, "Could not reload files. Check console for error.");
                        e.printStackTrace();
                        break;
                    }
                default:
                    showHelp(player);
                    break;
            }

        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> out = new ArrayList<>();
        if (!(sender instanceof Player)) return null;
        Player player = (Player) sender;

        if (!command.getName().equalsIgnoreCase("area")) return null;

        if (args.length == 1) {
            out.add("create");
            out.add("delete");
            out.add("setxp");
            out.add("reset");
            out.add("setdesc");
            out.add("command");
            out.add("reload");
            out.add("setsound");
        }

        switch (args[0].toLowerCase()) {
            case "add":
            case "create":
                if (args.length == 2) {
                    DiscoverAreasPlugin.getRegionContainer().get(new BukkitWorld(player.getWorld())).getRegions().values().forEach(region -> {
                        if (!DiscoverAreasPlugin.getInstance().getCache().stream().anyMatch(area -> area.getRegion() == region))
                            out.add(region.getId());
                    });
                }
                break;
            case "remove":
            case "delete":
                if (args.length == 2) DiscoverAreasPlugin.getInstance().getCache().forEach(area -> out.add(area.getName()));
                break;
            case "setexp":
            case "setxp":
                if (args.length == 2) DiscoverAreasPlugin.getInstance().getCache().forEach(area -> out.add(area.getName()));
                break;
            case "reset":
                if (args.length == 2) Bukkit.getOnlinePlayers().forEach(p -> out.add(p.getName()));
                break;
            case "setdesc":
                if (args.length == 2) DiscoverAreasPlugin.getInstance().getCache().forEach(area -> out.add(area.getName()));
                break;
            case "command":
                if (args.length == 2) {
                    out.add("list");
                    out.add("add");
                    out.add("remove");
                    out.add("edit");
                    break;
                } else if (args.length > 2) {
                    switch (args[1].toLowerCase()) {
                        case "remove":
                            if (args.length == 4)
                                DiscoverAreasPlugin.getInstance().getCache().forEach(area -> out.add(area.getName()));
                            break;
                        case "add":
                        case "list":
                            DiscoverAreasPlugin.getInstance().getCache().forEach(area -> out.add(area.getName()));
                            break;
                        case "edit":
                            if (args.length == 4)
                                DiscoverAreasPlugin.getInstance().getCache().forEach(area -> out.add(area.getName()));
                            break;
                    }
                }
        }
        Collections.sort(out);
        return out;
    }

    private void showHelp(Player p) {
        DiscoverAreasPlugin.getConfiguration().getStringList("messages.help").forEach(s -> p.sendMessage(ChatColor.translateAlternateColorCodes('&', s)));
    }

}
