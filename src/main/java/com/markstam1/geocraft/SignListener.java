package com.markstam1.geocraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Sign;

public class SignListener implements Listener
{
	public GeoCraft plugin;
	
	public SignListener(GeoCraft plugin)
	{
        this.plugin = plugin;
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent e)
	{
		Player p = e.getPlayer();
		String playerName = p.getName();
		Sign sign = (Sign) e.getBlock().getState().getData();
		Block attached = e.getBlock().getRelative(sign.getAttachedFace());
		if(attached.getType() == Material.CHEST && sign.isWallSign())
		{
			if(e.getLine(0).equalsIgnoreCase("[geocraft]"))
			{
				String cacheName = e.getLine(1).toLowerCase();
				
				if(e.getLine(1).equalsIgnoreCase(""))
				{
					p.sendMessage(ChatColor.RED + "[GeoCraft] Line 2 is empty, please enter the geocache name.");
					e.setLine(0, "");
					e.getBlock().breakNaturally();
				}
				
				else
				{
					if(!(plugin.config.getConfigurationSection("geocaches").get(cacheName) == null))
					{
						p.sendMessage(ChatColor.RED + "[GeoCraft] Geocache " + cacheName + " already exists.");
						e.setLine(0, "");
						e.getBlock().breakNaturally();
					}
					else
					{
						double locX = e.getBlock().getX();
						double locY = e.getBlock().getY();
						double locZ = e.getBlock().getZ();
						String worldName = p.getWorld().getName();
						
						e.setLine(0, ChatColor.DARK_BLUE + "[GeoCraft]");
						e.setLine(1, e.getLine(1).toLowerCase());
						e.setLine(2, ChatColor.DARK_RED + "Hidden by");
						e.setLine(3, ChatColor.DARK_RED + playerName);
						p.sendMessage(ChatColor.GREEN + "[GeoCraft] Geocache "+ cacheName + " created.");
						
						plugin.config.set("geocaches." + cacheName + ".hider", playerName);
						plugin.config.set("geocaches." + cacheName + ".hiderid", p.getUniqueId().toString());
						plugin.config.set("geocaches." + cacheName + ".x", locX);
						plugin.config.set("geocaches." + cacheName + ".y", locY);
						plugin.config.set("geocaches." + cacheName + ".z", locZ);
						plugin.config.set("geocaches." + cacheName + ".world", worldName);
						plugin.saveConfig();
						
						Inventory cacheInv = Bukkit.createInventory(e.getPlayer(), 27, cacheName);
						ItemStack logbook = new ItemStack(Material.BOOK);
						ItemMeta logMeta = logbook.getItemMeta();
						logMeta.setDisplayName(ChatColor.GREEN + "Logbook");
						logbook.setItemMeta(logMeta);
						cacheInv.addItem(logbook);
						
						ItemStack feather = new ItemStack(Material.FEATHER);
						ItemMeta featherMeta = feather.getItemMeta();
						featherMeta.setDisplayName(ChatColor.GOLD + "Sign logbook");
						feather.setItemMeta(featherMeta);
						cacheInv.setItem(13, feather);
						
						plugin.cacheInvs.put(cacheName, cacheInv);
					}
					
				}
			}
		}
	}
	
	
}
