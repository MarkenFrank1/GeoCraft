package com.markstam1.geocraft;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerListener implements Listener
{
	public static GeoCraft plugin;
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		Player p = e.getPlayer();
		
		if(p.getItemInHand().getType() == Material.COMPASS)
		{
			if(plugin.navigating.contains(p.getName()))
			{
				p.sendMessage(ChatColor.GREEN + "[GeoCraft] Compass pointing at " + plugin.geoTarget);
			}
		}
	}
	
}
