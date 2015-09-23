package com.markstam1.geocraft;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

public class PlayerListener implements Listener
{
	public GeoCraft plugin;
	
	public PlayerListener(GeoCraft plugin)
	{
        this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		Player p = e.getPlayer();
		
		if(p.getItemInHand().getType() == Material.COMPASS)
		{
			if(plugin.navigating.contains(p.getName()))
			{
				int distance = (int) Math.floor(p.getLocation().distance(p.getCompassTarget()));
				p.sendMessage(ChatColor.GREEN + "[GeoCraft] Geocache " + plugin.geoTarget + " is " + distance + " blocks away.");
			}
		}
		
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		{
			if(plugin.isBlockGeocache(e.getClickedBlock()))
			{
				e.setCancelled(true);
				Sign s = (Sign) e.getClickedBlock().getRelative(plugin.getAttachedSignBlockFace(e.getClickedBlock())).getState();
				String cacheName = ChatColor.stripColor(s.getLine(1));
				Inventory cacheInv = plugin.cacheInvs.get(cacheName);
				e.getPlayer().openInventory(cacheInv);
			}
		}
	}
	
}
