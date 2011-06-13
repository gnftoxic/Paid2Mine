package me.pwnage.bukkit.paid2mine;

import org.bukkit.Location;

public class BlockInfo
{
    public String PlayerName;
    public Location BlockLoc;
    
    public BlockInfo(String name, Location loc)
    {
        this.PlayerName = name;
        this.BlockLoc = loc;
    }
    
    public boolean canEarnMoney(long time)
    {
        if((System.currentTimeMillis() - time) / 1000 > (2.5 * 60))
            return true;
        return false;
    }
    
    @Override
    public String toString()
    {
        return BlockLoc.getWorld().getName() + " (" + BlockLoc.getBlockX() + "," + BlockLoc.getBlockY() + "," + BlockLoc.getBlockZ() + ")";
    }
}
