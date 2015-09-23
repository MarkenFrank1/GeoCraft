package com.markstam1.geocraft;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryListener implements Listener
{
	public GeoCraft plugin;
	
	public InventoryListener(GeoCraft plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e)
	{
		ItemStack clickedItem = e.getCurrentItem();
		Inventory inventory = e.getInventory();
		Player p = (Player) e.getWhoClicked();
		for(String cacheName : plugin.getGeocaches())
		{
			if(inventory.getName().equals(cacheName))
			{
				if(clickedItem.getType().equals(Material.FEATHER))
				{
					ItemStack logbook = inventory.getItem(0);
					ItemMeta meta = logbook.getItemMeta();
					List<String> lore = new ArrayList<String>();
					boolean hasAlreadySigned = false;
					
					if(meta.hasLore())
					{
						List<String> oldLore = plugin.stripAllColors(meta.getLore());
						
						for(String loreItem : oldLore)
						{
							if(loreItem.contains(p.getName()))
							{
								p.sendMessage(ChatColor.RED + "[GeoCraft] You've already signed this logbook!");
								hasAlreadySigned = true;
							}
						}
						lore.addAll(meta.getLore());
					}
					
					if(!hasAlreadySigned)
					{
						if(!meta.hasLore())
						{
							lore.add(ChatColor.GOLD + "Found by:");
						}
						
						lore.add(ChatColor.BLUE + p.getName() + " " + ChatColor.LIGHT_PURPLE + plugin.date);
						meta.setLore(lore);
						logbook.setItemMeta(meta);
						p.sendMessage(ChatColor.GREEN + "[GeoCraft] Successfully signed the logbook.");
					}
					
					e.setCancelled(true);
				}
				
				if(clickedItem.getType().equals(Material.BOOK))
				{
					e.setCancelled(true);
					p.sendMessage(ChatColor.RED + "[GeoCraft] You can't move the logbook!");
				}
			}
			
		}
		
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e)
	{
		if(plugin.getGeocaches().contains(e.getInventory().getName())) //It's a geocache
		{
			for(String cacheName : plugin.getGeocaches())
			{
				ItemStack logbook = e.getInventory().getItem(0);
				ItemMeta logbookMeta = logbook.getItemMeta();
				List<String> lore = logbookMeta.getLore();
				
				if(!lore.isEmpty())
				{
					lore = plugin.stripAllColors(lore);
					
					if(lore.contains("Found by:"))
					{
						lore.remove("Found by:");
					}
					
					
					plugin.config.set("geocaches." + cacheName + ".lore", lore);
				}
			}
			plugin.saveConfig();
		}
		
		
		
	}
	
	
	
	
	
	
	
}
