package com.markstam1.geocraft;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.material.Sign;
import org.bukkit.plugin.java.JavaPlugin;

public class GeoCraft extends JavaPlugin implements Listener
{
	
	File geocachesfile = new File(getDataFolder(), "geocaches.yml");
	FileConfiguration config = YamlConfiguration.loadConfiguration(geocachesfile);
	
	public void onEnable()
	{
		getServer().getPluginManager().registerEvents(this, this);
		getConfig().options().copyDefaults(false);
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
	
	@EventHandler
	public void onSignChange(SignChangeEvent e)
	{
		Player p = e.getPlayer();
		String playerName = p.getName();
		Sign sign = (Sign) e.getBlock().getState().getData();
		Block attached = e.getBlock().getRelative(sign.getAttachedFace());
		if(attached.getType() == Material.CHEST)
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
					if(!(config.getConfigurationSection("geocaches").get(cacheName) == null))
					{
						p.sendMessage(ChatColor.RED + "[GeoCraft] Geocache " + cacheName + " already exists.");
						e.setLine(0, "");
						e.getBlock().breakNaturally();
					}
					else
					{
						float locX = e.getBlock().getX();
						float locY = e.getBlock().getY();
						float locZ = e.getBlock().getZ();						
						
						e.setLine(0, ChatColor.DARK_BLUE + "[GeoCraft]");
						e.setLine(1, e.getLine(1).toLowerCase());
						e.setLine(2, ChatColor.DARK_RED + "Hidden by");
						e.setLine(3, ChatColor.DARK_RED + playerName);
						p.sendMessage(ChatColor.GREEN + "[GeoCraft] Geocache "+ cacheName + " created.");
		
						config.set("geocaches." + cacheName + ".hider", playerName);
						config.set("geocaches." + cacheName + ".x", locX);
						config.set("geocaches." + cacheName + ".y", locY);
						config.set("geocaches." + cacheName + ".z", locZ);
						saveConfig();
					}
					
				}
			}
		}
	}
	
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
				config.set("geocaches." + sign.getLine(1), null);
				saveConfig();
				e.getPlayer().sendMessage(ChatColor.GREEN + "[GeoCraft] Geocache " + sign.getLine(1) + " deleted.");
			}
			
		}
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("geo"))
		{
			if(args.length == 0) //Player types /geo without arguments
			{
				sender.sendMessage("Available commands:");
				sender.sendMessage(ChatColor.GOLD + "/geo list: " + ChatColor.WHITE + "Shows all listed geocaches.");	
				return true;
			}
			
			if(args.length >= 1) //Player types /geo with an argument
			{
				if(args[0].equalsIgnoreCase("list"))
				{
					sender.sendMessage(ChatColor.GOLD + "Available geocaches:");
					int geonr = 0;
					
					for(String geoKey : config.getConfigurationSection("geocaches").getKeys(false))
					{
						if(!(geoKey == null))
						{
							geonr++;
							sender.sendMessage(ChatColor.AQUA + "" + geonr + ". " + geoKey);
						}
						else
						{
							sender.sendMessage(ChatColor.RED + "There are no listed geocaches.");
							
						}
						
					}
					return true;
				}
				
				if(args[0].equalsIgnoreCase("help"))
				{
					sender.sendMessage("Available commands:");
					sender.sendMessage(ChatColor.GOLD + "/geo list: " + ChatColor.WHITE + "Shows all listed geocaches");					
					return true;
				}
				
				if(args[0].equalsIgnoreCase("nav"))
				{
					if(args.length == 1)
					{
						sender.sendMessage(ChatColor.RED + "[GeoCraft] Usage: /geo nav <geocache name>");
						
					}
					else
					{
						if(sender instanceof Player)
						{
							Player p = (Player) sender;
							if(config.getConfigurationSection("geocaches").get(args[1]) != null)
							{
								double locX = config.getConfigurationSection("geocaches").getDouble(args[1].toLowerCase() + ".x");
								double locY = config.getConfigurationSection("geocaches").getDouble(args[1].toLowerCase() + ".y");
								double locZ = config.getConfigurationSection("geocaches").getDouble(args[1].toLowerCase() + ".z");
								World world = Bukkit.getServer().getWorld("world");
								Location loc = new Location(world, locX, locY, locZ);
								
								p.setCompassTarget(loc);
								p.sendMessage(ChatColor.GREEN + "[GeoCraft] Compass pointing at " + args[1]);
							}
							else
							{
								p.sendMessage(ChatColor.RED + "[GeoCraft] That geocache doesn't exist. Use /geo list to see a list of available geocaches.");
								
							}
						
						
						}
					}
				}
					else
					{
						sender.sendMessage("[GeoCraft] This command can only be run in-game.");
					}
					
					
					return true;
				}
				
				else
				{
					sender.sendMessage(ChatColor.RED + "[GeoCraft] Unknown argument, use /geo help to see a list of available commands.");
					return false;
				}
			}
		
		
		return false;
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	}
}