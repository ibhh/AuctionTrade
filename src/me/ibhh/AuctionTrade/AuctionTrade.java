/**
 *
 */
package me.ibhh.AuctionTrade;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Simon
 *
 */
public class AuctionTrade extends JavaPlugin {

    public float Version = 0;
    private SQLConnectionHandler con;
    private PermissionsHandler Permission;
    private TimeHandler Time;
    private String action = "";
    public boolean debug = false;
    public static String Prefix = "ChatColor.DARK_BLUE + " + "[AuctionTrade] " + " + ChatColor.GOLD";
    private String Time0msg;
    private String Successcreate;
    private PanelControl panel;

    /**
     * Called by Bukkit on stopping the server
     *
     * @param
     * @return
     */
    @Override
    public void onDisable() {
        con.CloseCon();
        System.out.println("[AuctionTrade] disabled!");

    }

    public boolean UpdateConfig() {
        try {
            getConfig().options().copyDefaults(true);
            saveConfig();
            reloadConfig();
            System.out.println("[AuctionTrade] Config file found!");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Called by Bukkit on starting the server
     *
     * @param
     * @return
     */
    @Override
    public void onEnable() {
        UpdateConfig();
        try {
            getConfigStrings();
        } catch (Exception e) {
            Logger(e.getMessage(), "Error");
        }
        debug = getConfig().getBoolean("debug");

        if (getConfig().getBoolean("firstRun")) {
            panel = new PanelControl(this);
            panel.setSize(400, 300);
            panel.setLocation(200, 300);
            panel.setVisible(true);
        }
        con = new SQLConnectionHandler(this);
        Permission = new PermissionsHandler(this);
        Time = new TimeHandler();
        try {
            con.createConnection();
        } catch (Exception e) {
            e.printStackTrace();
            onDisable();
            return;
        }
        try {
            con.PrepareDB();
        } catch (Exception e) {
            e.printStackTrace();
            onDisable();
            return;
        }
        try {
            aktuelleVersion();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("[AuctionTrade] Version: " + Version + " successfully enabled!");
        String URL = "http://ibhh.de:80/aktuelleversionAuctionTrade.html";
        if ((Update.UpdateAvailable(URL, Version) == true)) {
            System.out.println("[AuctionTrade] New version: " + Update.getNewVersion(URL) + " found!");
            System.out.println("[AuctionTrade] ******************************************");
            System.out.println("[AuctionTrade] *********** Please update!!!! ************");
            System.out.println("[AuctionTrade] * http://ibhh.de/AuctionTrade.jar *");
            System.out.println("[AuctionTrade] ******************************************");
            if (getConfig().getBoolean("autodownload") == true) {
                try {
                    String path = getDataFolder().toString() + "/Update/";
                    Update.autoUpdate("http://ibhh.de/AuctionTrade.jar", path, "AuctionTrade.jar");
                } catch (Exception e) {
                    System.out.println("[AuctionTrade] " + "Error on checking permissions with PermissionsEx!");
                    e.printStackTrace();
                }
            } else {
                System.out.println("[AuctionTrade] Please type [AuctionTrade download] to download manual! ");
            }
        }
    }

    public void getConfigStrings() {
        Time0msg = getConfig().getString("command.error.time0." + getConfig().getString("language"));
        Successcreate = getConfig().getString("command.success.create." + getConfig().getString("language"));
    }

    /**
     * Gets version.
     *
     * @param
     * @return float: Version of the installed plugin.
     */
    public float aktuelleVersion() {
        try {
            Version = Float.parseFloat(getDescription().getVersion());
        } catch (Exception e) {
            System.out.println("[AuctionTrade]Could not parse version in float");
        }
        return Version;
    }

    public static void Logger(String msg, String TYPE) {
        if (TYPE.equalsIgnoreCase("Warning") || TYPE.equalsIgnoreCase("Error")) {
            System.err.println("[AuctionTrade] " + TYPE + msg);
        } else if (TYPE.equalsIgnoreCase("Debug")) {
            System.out.println("[AuctionTrade] Debug:" + msg);
        } else {
            System.out.println("[AuctionTrade] " + msg);
        }
    }

    public static void PlayerLogger(Player p, String msg, String TYPE) {
        if (TYPE.equalsIgnoreCase("Error")) {
            p.sendMessage(Prefix + "Error: " + msg);
        } else {
            p.sendMessage(Prefix + msg);
        }
    }

    /**
     * Called by Bukkit on reloading the server
     *
     * @param
     * @return
     */
    public void onReload() {
        onEnable();
    }

    protected static boolean isConsole(CommandSender sender) {
        return !(sender instanceof Player);
    }

    /**
     * Called by Bukkit if player posts a command
     *
     * @param none , cause of Bukkit.
     * @return true if no errors happened else return false to Bukkit.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label,
            String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            action = args[0];
            if (cmd.getName().equalsIgnoreCase("AuctionTrade")) {
                if (Permission.checkpermissions(player, "AuctionTrade.create")) {
                    if (args.length == 1) {
                        if (args[0].equalsIgnoreCase("create")) {
                            Long TIME = Time.getTime();
                            String ItemId = args[1];
                            int Dauer = 0;
                            double Anfang = 0;
                            try {
                                Anfang = Double.parseDouble(args[3]);
                            } catch (Exception e) {
                                Logger(e.getMessage(), "Error");
                                PlayerLogger(player, e.getMessage(), "Error");
                            }
                            try {
                                Dauer = Integer.parseInt(args[2]);
                                Dauer = Dauer * 1000 * 60 * 60;
                            } catch (Exception e) {
                                Logger(e.getMessage(), "Error");
                                PlayerLogger(player, e.getMessage(), "Error");
                            }
                            if (Dauer != 0) {
                                con.InsertAuction(TIME, sender, ItemId, Anfang, Dauer, "");
                                PlayerLogger(player, String.format(Successcreate, TIME, ItemId, Dauer), "");
                            } else {
                                PlayerLogger(player, Time0msg, label);
                            }
                        }
                    }
                }
            }
        } else {
            if (isConsole(sender)) {
                if (cmd.getName().equalsIgnoreCase("AuctionTrade")) {
                    if (args.length == 1) {
                        if (args[0].equals("download")) {
                            String path = getDataFolder().toString() + "/Update/";
                            Update.autoUpdate("http://ibhh.de/AuctionTrade.jar", path, "AuctionTrade.jar");
                        }
                    }
                }
            }
        }
        return false;
    }
}
