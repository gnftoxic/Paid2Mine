package me.pwnage.bukkit.paid2mine;

import com.iConomy.iConomy;
import org.bukkit.entity.Player;

public class iUpdate implements Runnable
{
    private Paid2Mine plugin;

    public iUpdate(Paid2Mine pl)
    {
        plugin = pl;
    }

    @Override
    public void run()
    {
        Update();
    }
    
    public void Update()
    {
        for (String x : Paid2Mine.SQLCache.keySet())
        {
            if (!x.equals(""))
            {
                iConomy.getAccount(x).getHoldings().add(((Double)Paid2Mine.SQLCache.get(x)).doubleValue());

                if (plugin.alertPlayer)
                {
                    Player p = plugin.getServer().getPlayer(x);
                    if(p != null)
                    {
                        p.sendMessage(plugin.alertMessage.replace("$$", "" + String.format("%.2f", new Object[] { Paid2Mine.SQLCache.get(x) })));
                    }
                }
            }
        }
        plugin.SQLCache.clear();
        
        plugin.BlockBoosters.clear();
    }
}