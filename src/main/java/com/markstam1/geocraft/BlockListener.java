package com.markstam1.geocraft;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockListener implements Listener
{
	public static GeoCraft plugin;
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e)
	{
		Block block = e.getBlock();
		if(block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST)
		{
			org.bukkit.block.Sign sign = (org.bukkit.block.Sign) block.getState();
			
			String firstLine = ChatColor.stripColor(sign.getLine(0));
			if(firstLine.equalsIgnoreCase("[GeoCraft]"))
			{
				Player p = e.getPlayer();
				String playerName = p.getName();
				if(plugin.isGeocacheOwner(playerName, sign.getLine(1)))
				{
					plugin.config.set("geocaches." + sign.getLine(1), null);
					plugin.saveConfig();
					p.sendMessage(ChatColor.GREEN + "[GeoCraft] Geocache " + sign.getLine(1) + " deleted.");
				}
				else
				{
					String hiderName = ChatColor.stripColor(sign.getLine(3));
					e.setCancelled(true);
					p.sendMessage(ChatColor.RED + "[GeoCraft] You don't have permission to delete " + hiderName + " 's geocache!");
				}
			}
			
		}
		
		if(block.getType() == Material.CHEST)
		{
			Player p = e.getPlayer();
			String playerName = p.getName();
			BlockFace signface = plugin.getAttachedSignBlockFace(block);
			org.bukkit.block.Sign attachedSign = (org.bukkit.block.Sign) block.getRelative(signface).getState();
			
			
			
			String firstLine = ChatColor.stripColor(attachedSign.getLine(0));
			if(firstLine.equalsIgnoreCase("[geocraft]"))
			{
				if(plugin.isGeocacheOwner(playerName, attachedSign.getLine(1)))
				{
					plugin.config.set("geocaches." + attachedSign.getLine(1), null);
					plugin.saveConfig();
					p.sendMessage(ChatColor.GREEN + "[GeoCraft] Geocache " + attachedSign.getLine(1) + " deleted.");
				}
				
				else
				{
					String hiderName = ChatColor.stripColor(attachedSign.getLine(3));
					e.setCancelled(true);
					p.sendMessage(ChatColor.RED + "[GeoCraft] You don't have permission to delete " + hiderName + " 's geocache!");
				}
			}
		}
	}
}
