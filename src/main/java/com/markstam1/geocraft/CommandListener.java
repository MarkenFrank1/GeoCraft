package com.markstam1.geocraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandListener implements CommandExecutor {
	
	public static GeoCraft plugin;
	
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
					
					for(String geoKey : plugin.config.getConfigurationSection("geocaches").getKeys(false))
					{
						if(plugin.config.isSet("geocaches"))
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
					sender.sendMessage(ChatColor.GOLD + "/geo nav: " + ChatColor.WHITE + "Navigate to a geocache.");				
					sender.sendMessage(ChatColor.GOLD + "/geo reload: " + ChatColor.WHITE + "Reload geocaches and config");				
					return true;
				}
				
				if(args[0].equalsIgnoreCase("reload"))
				{
					plugin.reloadConfig();
					sender.sendMessage(ChatColor.GREEN +"Reloaded geocaches and config");
				}
				
				if(args[0].equalsIgnoreCase("delete"))
				{
					if(args.length == 1) sender.sendMessage(ChatColor.RED + "[GeoCraft] Usage: /geo delete <geocache name>");
					
					else
					{
						if(plugin.config.getConfigurationSection("geocaches").get(args[1]) != null)
						{
							double locX = plugin.config.getConfigurationSection("geocaches").getDouble(args[1].toLowerCase() + ".x");
							double locY = plugin.config.getConfigurationSection("geocaches").getDouble(args[1].toLowerCase() + ".y");
							double locZ = plugin.config.getConfigurationSection("geocaches").getDouble(args[1].toLowerCase() + ".z");
							String worldName = plugin.config.getConfigurationSection("geocaches").getString(args[1].toLowerCase() + ".world");
							World world = Bukkit.getWorld(worldName);				
							Location loc = new Location(world, locX, locY, locZ);
							Block sign = world.getBlockAt(loc);
							Block attachedChest = sign.getRelative(plugin.getAttachedChestBlockFace(sign));
							
							attachedChest.breakNaturally();
							plugin.config.getConfigurationSection("geocaches").set(args[1].toLowerCase(), null);
							plugin.saveConfig();
							sender.sendMessage(ChatColor.GREEN + "[GeoCraft] Geocache " + args[1] + " deleted.");
						}
						
						else
						{
							sender.sendMessage(ChatColor.RED + "[GeoCraft] That geocache doesn't exist. Use /geo list to see a list of available geocaches.");
						}
						
						
						
					}
					
				}

				if(args[0].equalsIgnoreCase("nav"))
				{
					if(args.length == 1)
					{
						if(sender instanceof Player)
						{
							Player p = (Player) sender;
							
							if(plugin.navigating.contains(p.getName()))
							{
								p.sendMessage(ChatColor.GREEN + "[GeoCraft] Navigating disabled.");
								World world = p.getWorld();
								plugin.navigating.remove(p.getName());
								Location loc = world.getSpawnLocation();
								p.setCompassTarget(loc);
							}
							else
							{
								sender.sendMessage(ChatColor.RED + "[GeoCraft] Usage: /geo nav <geocache name>");
							}
						}
						
						else
						{
							sender.sendMessage("[GeoCraft] This command can only be run in-game.");
						}
					}
					else
					{
						if(sender instanceof Player)
						{
							Player p = (Player) sender;
							if(plugin.config.getConfigurationSection("geocaches").get(args[1]) != null)
							{
								double locX = plugin.config.getConfigurationSection("geocaches").getDouble(args[1].toLowerCase() + ".x");
								double locY = plugin.config.getConfigurationSection("geocaches").getDouble(args[1].toLowerCase() + ".y");
								double locZ = plugin.config.getConfigurationSection("geocaches").getDouble(args[1].toLowerCase() + ".z");
								String worldName = plugin.config.getConfigurationSection("geocaches").getString(args[1].toLowerCase() + ".world");
								World world = Bukkit.getWorld(worldName);
								Location loc = new Location(world, locX, locY, locZ);
								plugin.geoTarget = args[1];
								
								p.setCompassTarget(loc);
								plugin.navigating.add(p.getName());
								p.sendMessage(ChatColor.GREEN + "[GeoCraft] Compass pointing at " + args[1]);
							}
							else
							{
								p.sendMessage(ChatColor.RED + "[GeoCraft] That geocache doesn't exist. Use /geo list to see a list of available geocaches.");
							}
						}
						
						else
						{
							sender.sendMessage("[GeoCraft] This command can only be run in-game.");
						}
					}
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
