package me.pwnage.bukkit.paid2mine;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class MineBL extends BlockListener
{
    private Paid2Mine plugin;

    public MineBL(Paid2Mine plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event)
    {
        if(event.isCancelled())
            return;
        
        BlockInfo ThisBlock = new BlockInfo(event.getPlayer().getName(), event.getBlock().getLocation());
        
        if(this.plugin.BlockBoosters.containsKey(ThisBlock.toString()))
        {
            if(!ThisBlock.canEarnMoney(this.plugin.BlockBoosters.get(ThisBlock.toString())))
            {
                return;
            }
        }
        
        if(this.plugin.defaultValue == 0.00)
        {
            if(!this.plugin.ItemValues.containsKey(event.getBlock().getTypeId()))
            {
                return;
            }
        }
        
        Block b = event.getBlock();

        double value = Paid2Mine.defaultValue.doubleValue();
        if (this.plugin.ItemValues.containsKey(Integer.valueOf(b.getTypeId())))
        {
            value = ((Double)Paid2Mine.ItemValues.get(Integer.valueOf(b.getTypeId()))).doubleValue();
            
            if(value == 0.0)
            {
                return;
            }
        }
        
        plugin.addToQueue(event.getPlayer().getName(), Double.valueOf(value));
    }
    
    @Override
    public void onBlockPlace(BlockPlaceEvent event)
    {
        BlockInfo bi = new BlockInfo(event.getPlayer().getName(), event.getBlockPlaced().getLocation());
        if(this.plugin.BlockBoosters.containsKey(bi.toString()))
        {
            this.plugin.BlockBoosters.remove(bi.toString());
        }
        
        if(this.plugin.defaultValue == 0.00)
        {
            if(!this.plugin.ItemValues.containsKey(event.getBlock().getTypeId()))
            {
                return;
            }
        }
        this.plugin.BlockBoosters.put(bi.toString(), System.currentTimeMillis());
    }
}