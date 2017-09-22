package at.TimoCraft.TimoCloud.bungeecord.commands;

import at.TimoCraft.TimoCloud.bungeecord.TimoCloud;
import at.TimoCraft.TimoCloud.bungeecord.managers.BungeeMessageManager;
import at.TimoCraft.TimoCloud.bungeecord.objects.BaseObject;
import at.TimoCraft.TimoCloud.bungeecord.objects.Group;
import at.TimoCraft.TimoCloud.bungeecord.objects.Server;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.PluginDescription;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timo on 27.12.16.
 */
public class TimoCloudCommand extends Command {

    public TimoCloudCommand() {
        super("TimoCloud");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            if (args.length < 1) {
                sendVersion(sender);
                return;
            }

            if (args[0].equalsIgnoreCase("check")) {
                String user = "%%__USER__%%", nonce = "%%__NONCE__%%";
                if (user.startsWith("%%")) {
                    BungeeMessageManager.sendMessage(sender, "&cNot downloaded from spigotmc.org.");
                    return;
                }
                BungeeMessageManager.sendMessage(sender, "&6Downloaded by &ehttps://www.spigotmc.org/members/" + user + "/");
                BungeeMessageManager.sendMessage(sender, "&b" + nonce);
                return;
            }

            if (!sender.hasPermission("timocloud.admin")) {
                BungeeMessageManager.noPermission(sender);
                return;
            }
            if (args[0].equalsIgnoreCase("reload")) {
                TimoCloud.getInstance().getFileManager().load();
                TimoCloud.getInstance().getServerManager().loadGroups();
                BungeeMessageManager.sendMessage(sender, "&aSuccessfully reloaded from configuration!");
                return;
            }
            if (args[0].equalsIgnoreCase("version")) {
                sendVersion(sender);
                return;
            }
            if (args[0].equalsIgnoreCase("help")) {
                sendHelp(sender);
                return;
            }

            if (args[0].equalsIgnoreCase("listgroups")) {
                List<Group> groups = TimoCloud.getInstance().getServerManager().getGroups();
                if (groups.size() == 0) {
                    BungeeMessageManager.sendMessage(sender, "&cNo groups yet!");
                    return;
                }
                BungeeMessageManager.sendMessage(sender, "&6Groups (" + groups.size() + "):");
                for (Group group : groups) {
                    BungeeMessageManager.sendMessage(sender,
                            "  &b" + group.getName() + " &e(&7RAM: &6" + group.getRam() + (group.getRam() < 128 ? "G" : "M") +
                                    "&e, &7KeepOnline-Amount: &6" + group.getStartupAmount() +
                                    "&e, &7static: &6" + group.isStatic() +
                                    "&e)");
                    BungeeMessageManager.sendMessage(sender, "&3Currently &estarting &3servers: &6" + group.getRunningServers().size());
                    for (Server server : group.getStartingServers()) {
                        BungeeMessageManager.sendMessage(sender, "    &7" + server.getName() + " (starting)" +
                                " &b(&6State: &e" + server.getState() + "&b) " +
                                (server.getMap().equals("") ? "" : (" &b(&6Map: &e" + server.getMap() + "&b)"))
                        );
                    }
                    BungeeMessageManager.sendMessage(sender, "&3Currently &aonline &3servers: &6" + group.getRunningServers().size());
                    for (Server server : group.getRunningServers()) {
                        BungeeMessageManager.sendMessage(sender, "    &b" + server.getName() +
                                " &b(&6State: &e" + server.getState() + "&b) " +
                                (server.getMap().equals("") ? "" : (" &b(&6Map: &e" + server.getMap() + "&b)")));
                    }
                }
                return;
            }
            if (args[0].equalsIgnoreCase("removegroup")) {
                String name = args[1];
                try {
                    TimoCloud.getInstance().getServerManager().removeGroup(name);
                } catch (Exception e) {
                    BungeeMessageManager.sendMessage(sender, "&cError while saving groups.yml. See console for mor information.");
                    e.printStackTrace();
                }
                return;
            }
            if (args[0].equalsIgnoreCase("restartgroup")) {
                Group group = TimoCloud.getInstance().getServerManager().getGroupByName(args[1]);
                if (group == null) {
                    BungeeMessageManager.sendMessage(sender, "&cGroup &e" + args[1] + " &cdoes not exist.");
                    return;
                }
                group.stopAllServers();
                return;
            }
            if (args[0].equalsIgnoreCase("stopserver") || args[0].equalsIgnoreCase("restartserver")) {
                Server server = TimoCloud.getInstance().getServerManager().getServerByName(args[1]);
                if (server == null) {
                    BungeeMessageManager.sendMessage(sender, "&cServer &e" + args[1] + " &cdoes not exist.");
                    return;
                }
                server.stop();
                return;
            }
            if (args[0].equalsIgnoreCase("addgroup")) {
                String name = args[1];
                int amount = Integer.parseInt(args[2]);
                int maxAmount = Integer.parseInt(args[3]);
                int ram = Integer.parseInt(args[4]);
                boolean isStatic = Boolean.parseBoolean(args[5]);
                String base = args[6];
                Group group = new Group(name, amount, maxAmount, ram, isStatic, base, new ArrayList<>());

                if (TimoCloud.getInstance().getServerManager().groupExists(group)) {
                    BungeeMessageManager.sendMessage(sender, "&cThis group already exists.");
                    return;
                }
                try {
                    TimoCloud.getInstance().getServerManager().saveGroup(group);
                } catch (Exception e) {
                    BungeeMessageManager.sendMessage(sender, "&cError while saving groups.yml. See console for mor information.");
                    e.printStackTrace();
                }
                return;
            }
            if (args[0].equalsIgnoreCase("editgroup")) {
                String groupName = args[1];
                Group group = TimoCloud.getInstance().getServerManager().getGroupByName(groupName);
                if (group == null) {
                    BungeeMessageManager.sendMessage(sender, "&cGroup &e" + groupName + " &cnot found. Get a list of all groups with §e/timocloud listgroups");
                    return;
                }
                String key = args[2];
                switch (key.toLowerCase()) {
                    case "onlineamount":
                        group.setStartupAmount(Integer.parseInt(args[3]));
                        TimoCloud.getInstance().getServerManager().saveGroup(group);
                        break;
                    case "maxamount":
                        group.setMaxAmount(Integer.parseInt(args[3]));
                        TimoCloud.getInstance().getServerManager().saveGroup(group);
                        break;
                    case "base":
                        String baseName = args[3];
                        BaseObject base = TimoCloud.getInstance().getServerManager().getBase(args[3]);
                        if (base == null) {
                            BungeeMessageManager.sendMessage(sender, "&cBase &e" + baseName + "&c is not connected or does not exist.");
                            return;
                        }
                        group.setBase(base);
                        TimoCloud.getInstance().getServerManager().saveGroup(group);
                        break;
                    case "ram":
                        int ram = Integer.parseInt(args[3]);
                        group.setRam(ram);
                        TimoCloud.getInstance().getServerManager().saveGroup(group);
                        break;
                    case "static":
                        boolean isStatic = Boolean.parseBoolean(args[3]);
                        group.setStatic(isStatic);
                        TimoCloud.getInstance().getServerManager().saveGroup(group);
                        break;
                    default:
                        BungeeMessageManager.sendMessage(sender, "&cNo valid argument found. Please use \n" +
                                "/TimoCloud editgroup <name> <onlineAmount (int), base (String), ram (int), static (boolean)> <value>");
                        break;
                }
                return;
            }
            sendHelp(sender);
        } catch (Exception e) {
            sendHelp(sender);
        }
    }

    private void sendVersion(CommandSender sender) {
        PluginDescription description = TimoCloud.getInstance().getDescription();
        BungeeMessageManager.sendMessage(sender, "&bTimoCloud Version &e[&6" + description.getVersion() + "&e] &bby &6TimoCrafter");
    }

    public void sendHelp(CommandSender sender) {
        BungeeMessageManager.sendMessage(sender, "&6Available commands for &bTimoCloud&6:");
        BungeeMessageManager.sendMessage(sender, "  &a/TimoCloud help &7- shows this page");
        BungeeMessageManager.sendMessage(sender, "  &a/TimoCloud version &7- shows the plugin version");
        BungeeMessageManager.sendMessage(sender, "  &a/TimoCloud reload &7- reloads all configs");
        BungeeMessageManager.sendMessage(sender, "  &a/TimoCloud addgroup <groupName (String)> <startupAmount (int)> <maxAmount (int)> <ram (int)> <static (boolean), use false if you don't know what you want> <base (String)> &7- creates a group, please make sure you created a template with the group name in the specific base");
        BungeeMessageManager.sendMessage(sender, "  &a/TimoCloud removegroup <groupName> &7- deletes a group");
        BungeeMessageManager.sendMessage(sender, "  &a/TimoCloud listgroups &7- lists all groups and started servers");
        BungeeMessageManager.sendMessage(sender, "  &a/TimoCloud restartgroup <groupName> &7- restarts all servers in a given group");
        BungeeMessageManager.sendMessage(sender, "  &a/TimoCloud editgroup <name> <onlineAmount (int), maxAmount (int), base (String), ram (int), static (boolean)> <value>");

    }
}