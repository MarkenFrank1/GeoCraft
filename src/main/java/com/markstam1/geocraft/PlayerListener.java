package com.markstam1.geocraft;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

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
				int distance = (int) Math.round(p.getLocation().distance(p.getCompassTarget()));
				p.sendMessage(ChatColor.GREEN + "[GeoCraft] Geocache " + plugin.geoTarget + " is " + distance + " blocks away.");
			}
		}
	}
	
}
