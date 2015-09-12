package com.markstam1.geocraft;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class GeoCraft extends JavaPlugin implements Listener
{
	
	File geocachesfile = new File(getDataFolder(), "geocaches.yml");
	FileConfiguration config = YamlConfiguration.loadConfiguration(geocachesfile);
	String geoTarget;
	public HashSet<String> navigating = new HashSet<String>();

	public void onEnable()
	{
		getServer().getPluginManager().registerEvents(new SignListener(this), this);
		getServer().getPluginManager().registerEvents(new BlockListener(this), this);
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		getCommand("geo").setExecutor(new CommandListener(this));
		
		saveConfig();
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
}