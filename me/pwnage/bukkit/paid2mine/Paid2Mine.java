package me.pwnage.bukkit.paid2mine;

import com.iConomy.iConomy;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class Paid2Mine extends JavaPlugin
{
    public static Logger log = Logger.getLogger("Minecraft");
    public String name;
    public String version;
    public iConomy icon;
    public MineBL minebl;
    public static boolean debug = false;
    public boolean alertPlayer = false;
    public String alertMessage = "";
    private static Configuration config;
    public static HashMap<Integer, Double> ItemValues;
    public static HashMap<String, Long> BlockBoosters;
    public static Double defaultValue = Double.valueOf(0.0D);
    public static iUpdate iup;

    public static HashMap<String, Double> SQLCache = new HashMap();

    @Override
    public void onEnable()
    {
        try
        {
            icon = ((iConomy)getServer().getPluginManager().getPlugin("iConomy"));

            name = getDescription().getName();
            version = getDescription().getVersion();
        }
        catch (Exception e)
        {
            log.log(Level.INFO, "[" + name + "] iConomy not found, disabling...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        iup = new iUpdate(this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, iup, 0L, 2500L);

        minebl = new MineBL(this);
        ItemValues = new HashMap<Integer, Double>();
        BlockBoosters = new HashMap<String, Long>();
        
        readConfig();

        getServer().getPluginManager().registerEvent(Type.BLOCK_BREAK, minebl, Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Type.BLOCK_PLACE, minebl, Priority.Monitor, this);
        log.log(Level.INFO, "[" + name + "] Enabled.");
    }

    public void readConfig()
    {
        File configLoc = new File("plugins/Paid2Mine/values.yml");
        try
        {
            if (!configLoc.exists())
            {
                new File("plugins/Paid2Mine/").mkdir();
                configLoc.createNewFile();
                config = new Configuration(configLoc);
                config.load();

                config.setProperty("config.alert.enabled", "true");
                config.setProperty("config.alert.message", "It's payday! You have earned $$");
                config.setProperty("items.default.value", "0.10");
                config.setProperty("items.stone.itemid", "1");
                config.setProperty("items.stone.value", "0.10");

                config.save();
            } else {
                config = new Configuration(configLoc);
                config.load();
            }

            debug = Boolean.parseBoolean((String)config.getProperty("debug.showinfo"));
            alertPlayer = Boolean.parseBoolean((String)config.getProperty("config.alert.enabled"));
            alertMessage = ((String)config.getProperty("config.alert.message"));
            alertMessage = alertMessage.replaceAll("&", "\u00A7");
            alertMessage = alertMessage.replaceAll("\\'", "'");

            List<String> KeysToImport = config.getKeys("items");

            ItemValues.clear();

            for (String x : KeysToImport)
            {
                if (x.equalsIgnoreCase("default"))
                {
                    defaultValue = Double.valueOf(Double.parseDouble((String)config.getProperty("items." + x + ".value")));
                }
                else
                {
                    ItemValues.put(Integer.valueOf(Integer.parseInt((String)config.getProperty("items." + x + ".itemid"))), Double.valueOf(Double.parseDouble((String)config.getProperty("items." + x + ".value"))));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable()
    {
        getServer().getScheduler().cancelTasks(this);
        log.log(Level.INFO, "[" + name + "] Disabled.");
    }

    @Override
    public boolean onCommand(CommandSender cs, Command c, String com, String[] arg)
    {
        if (com.equalsIgnoreCase("minepay"))
        {
            if ((arg.length > 0) && (arg[0].equalsIgnoreCase("-reload")) && (cs.isOp()))
            {
                readConfig();
                cs.sendMessage(ChatColor.GREEN + "[" + name + "] Reloaded configuration.");
            }
            if ((arg.length > 0) && (arg[0].equalsIgnoreCase("-countrec")) && (cs.isOp()))
            {
                cs.sendMessage(ChatColor.GREEN + "[" + name + "] Temporary Records: " + BlockBoosters.size());
            }
            if ((arg.length > 0) && (arg[0].equalsIgnoreCase("clearrec")) && (cs.isOp()))
            {
                cs.sendMessage(ChatColor.GREEN + "[" + name + "] Temporary Records Cleared: " + BlockBoosters.size());
                BlockBoosters.clear();
            }
            if ((arg.length > 0) && (arg[0].equalsIgnoreCase("force")) && (cs.isOp()))
            {
                cs.sendMessage(ChatColor.GREEN + "[" + name + "] Processing all queued payments");
                iup.Update();
                cs.sendMessage(ChatColor.GREEN + "[" + name + "] All payments processed.");
            }
            if ((arg.length > 0) && (arg[0].equalsIgnoreCase("-version")) && (cs.isOp()))
            {
                cs.sendMessage(ChatColor.GREEN + "[" + name + "] Plugin Version: " + getDescription().getVersion());
            }
            if ((arg.length > 0) && (arg[0].equalsIgnoreCase("list")))
            {
                for(Integer x : this.ItemValues.keySet())
                {
                    String name = Material.getMaterial(x).name().replace("_", " ");
                    name = name.toLowerCase();
                    name = ("" + name.charAt(0)).toUpperCase() + name.substring(1);
                    cs.sendMessage(ChatColor.GREEN + name + " is worth " + this.ItemValues.get(x));
                }
            }
            return true;
        }
        return false;
    }

    public void addToQueue(String playerName, Double amount)
    {
        double amt;
        
        if (SQLCache.containsKey(playerName))
        {
            amt = ((Double)SQLCache.get(playerName)).doubleValue() + amount.doubleValue();
            SQLCache.remove(playerName);
        } else {
            amt = amount.doubleValue();
        }

        SQLCache.put(playerName, Double.valueOf(amt));
    }
}