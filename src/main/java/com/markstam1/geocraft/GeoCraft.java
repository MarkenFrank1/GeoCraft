package com.markstam1.geocraft;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class GeoCraft extends JavaPlugin implements Listener
{
	
	File geocachesfile = new File(getDataFolder(), "geocaches.yml");
	FileConfiguration config = YamlConfiguration.loadConfiguration(geocachesfile);
	String geoTarget;
	HashMap<String, Inventory> cacheInvs = new HashMap<String, Inventory>();
	public HashSet<String> navigating = new HashSet<String>();
	
	DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	Calendar cal = Calendar.getInstance();
	String date = df.format(cal.getTime());

	public void onEnable()
	{
		getServer().getPluginManager().registerEvents(new SignListener(this), this);
		getServer().getPluginManager().registerEvents(new BlockListener(this), this);
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
		getCommand("geo").setExecutor(new CommandListener(this));
		loadInventories();
	}
	
	public void onDisable()
	{
		saveInventories();
	}
	
	public void saveConfig()
	{
		try 
		{
			config.save(geocachesfile);
		} 
		catch (IOException e1) 
		{
			e1.printStackTrace();
		}
	}
	
	public void reloadConfig()
	{
		try
		{
			config.load(geocachesfile);
		}
		catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public void saveInventories()
	{
		if(!getGeocaches().isEmpty())
		{
			for(String cacheName : getGeocaches())
			{
				Inventory inv = cacheInvs.get(cacheName);
				ItemStack logbook = inv.getItem(0);
				ItemMeta logbookMeta = logbook.getItemMeta();
				List<String> lore = stripAllColors(logbookMeta.getLore());
				if(!lore.isEmpty())
				{
					if(lore.contains("Found by:"))
					{
						lore.remove("Found by:");
						config.set("geocaches." + cacheName + ".lore", lore);
					}
				
					else
					{
						config.set("geocaches." + cacheName + ".lore", lore);
					}
				}
			}
			saveConfig();
		}
	}
	public void loadInventories()
	{
		for(String cacheName : getGeocaches())
		{
			UUID hiderid = UUID.fromString(config.getString("geocaches." + cacheName + ".hiderid"));
			Player hider = Bukkit.getPlayer(hiderid);
			Inventory cacheInv = Bukkit.createInventory(hider,  27, cacheName);
			ItemStack logbook = new ItemStack(Material.BOOK);
			ItemMeta logMeta = logbook.getItemMeta();
			logMeta.setDisplayName(ChatColor.GREEN + "Logbook");
			List<String> lore = new ArrayList<String>();
			List<String> confLore = config.getStringList("geocaches." + cacheName + ".lore");
			ListIterator<String> i = confLore.listIterator();
			
			while(i.hasNext())
			{
				String item = i.next();
				String[] split = item.split("\\s+");
				String playerName = split[0];
				String date = split[1];
				i.set(ChatColor.BLUE + playerName + " "+ ChatColor.LIGHT_PURPLE + date);
			}
			
			lore.add(ChatColor.GOLD + "Found by:");
			lore.addAll(confLore);
			
			logMeta.setLore(lore);
			logbook.setItemMeta(logMeta);
			cacheInv.addItem(logbook);
						
			ItemStack feather = new ItemStack(Material.FEATHER);
			ItemMeta featherMeta = feather.getItemMeta();
			featherMeta.setDisplayName(ChatColor.GOLD + "Sign logbook");
			feather.setItemMeta(featherMeta);
			cacheInv.setItem(13, feather);
			
			cacheInvs.put(cacheName, cacheInv);
		}
	}
	
	public BlockFace getAttachedSignBlockFace(Block block ){
	    if( block.getRelative(BlockFace.NORTH).getType().equals(Material.WALL_SIGN)) return BlockFace.NORTH;
	    if( block.getRelative(BlockFace.SOUTH).getType().equals(Material.WALL_SIGN)) return BlockFace.SOUTH;
	    if( block.getRelative(BlockFace.EAST).getType().equals(Material.WALL_SIGN)) return BlockFace.EAST;
	    if( block.getRelative(BlockFace.WEST).getType().equals(Material.WALL_SIGN)) return BlockFace.WEST;
	    return null;
	}
	
	public BlockFace getAttachedChestBlockFace(Block block ){
	    if( block.getRelative(BlockFace.NORTH).getType().equals(Material.CHEST)) return BlockFace.NORTH;
	    if( block.getRelative(BlockFace.SOUTH).getType().equals(Material.CHEST)) return BlockFace.SOUTH;
	    if( block.getRelative(BlockFace.EAST).getType().equals(Material.CHEST)) return BlockFace.EAST;
	    if( block.getRelative(BlockFace.WEST).getType().equals(Material.CHEST)) return BlockFace.WEST;
	    return null;
	}
	
	public boolean isGeocacheOwner(UUID playerID, String cacheName)
	{
		String playerIDtoString = playerID.toString();
		String hiderID = config.getString("geocaches." + cacheName + ".hiderid");
		if(playerIDtoString.equals(hiderID))
		{
			return true;
		}
		
		else
		{
			return false;
		}
	}
	
	public boolean isBlockGeocache(Block block)
	{
		if(block.getType().equals(Material.CHEST))
		{
			if(getAttachedSignBlockFace(block) != null)
			{
				Sign s = (Sign) block.getRelative(getAttachedSignBlockFace(block)).getState();
				String firstLine = ChatColor.stripColor(s.getLine(0));
				if(firstLine.equalsIgnoreCase("[geocraft]"))
				{
					return true;
				}
				
				else
				{
					return false;
				}
			}
			
			else
			{
				return false;
			}
		}
		
		
		return false;
	}
	
	public List<String> stripAllColors(List<String> list)
	{
		ListIterator<String> i = list.listIterator();
		
		while(i.hasNext())
		{
			String item = i.next();
			i.set(ChatColor.stripColor(item));
		}
		return list;
	}
	
	public HashSet<String> getGeocaches()
	{
		HashSet<String> geocaches = new HashSet<String>();
		
		for(String geoKey : config.getConfigurationSection("geocaches").getKeys(false))
		{
			if(config.isSet("geocaches"))
			{
				geocaches.add(geoKey);
			}
			
			else
			{
				return null;
			}
		}
		return geocaches;
	}
}