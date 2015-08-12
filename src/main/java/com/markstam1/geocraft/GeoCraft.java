package com.markstam1.geocraft;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Sign;
import org.bukkit.plugin.java.JavaPlugin;

public class GeoCraft extends JavaPlugin implements Listener
{
	
	File geocachesfile = new File(getDataFolder(), "geocaches.yml");
	FileConfiguration config = YamlConfiguration.loadConfiguration(geocachesfile);
	String geoTarget;
	public HashSet<String> navigating = new HashSet<String>();
	
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
	
	public boolean isGeocacheOwner(String playerName, String cacheName)
	{
		if(config.getConfigurationSection("geocaches").getString(cacheName + ".hider") == playerName)
		{
			return true;
		}
		
		else
		{
			return false;
		}
		
	}
	public void onEnable()
	{
		getServer().getPluginManager().registerEvents(this, this);
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
					if(!(config.getConfigurationSection("geocaches").get(cacheName) == null))
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
						
						config.set("geocaches." + cacheName + ".hider", playerName);
						config.set("geocaches." + cacheName + ".x", locX);
						config.set("geocaches." + cacheName + ".y", locY);
						config.set("geocaches." + cacheName + ".z", locZ);
						config.set("geocaches." + cacheName + ".world", worldName);
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
				Player p = e.getPlayer();
				String playerName = p.getName();
				if(isGeocacheOwner(playerName, sign.getLine(1)))
				{
					config.set("geocaches." + sign.getLine(1), null);
					saveConfig();
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
			BlockFace signface = getAttachedSignBlockFace(block);
			org.bukkit.block.Sign attachedSign = (org.bukkit.block.Sign) block.getRelative(signface).getState();
			
			
			
			String firstLine = ChatColor.stripColor(attachedSign.getLine(0));
			if(firstLine.equalsIgnoreCase("[geocraft]"))
			{
				config.set("geocaches." + attachedSign.getLine(1), null);
				saveConfig();
				p.sendMessage(ChatColor.GREEN + "[GeoCraft] Geocache " + attachedSign.getLine(1) + " deleted.");
			}
		}
		
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		Player p = e.getPlayer();
		
		if(p.getItemInHand().getType() == Material.COMPASS)
		{
			if(navigating.contains(p.getName()))
			{
				p.sendMessage(ChatColor.GREEN + "[GeoCraft] Compass pointing at " + geoTarget);
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
						if(config.isSet("geocaches"))
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
					reloadConfig();
					sender.sendMessage(ChatColor.GREEN +"Reloaded geocaches and config");
				}
				
				if(args[0].equalsIgnoreCase("delete"))
				{
					if(args.length == 1) sender.sendMessage(ChatColor.RED + "[GeoCraft] Usage: /geo delete <geocache name>");
					
					else
					{
						if(config.getConfigurationSection("geocaches").get(args[1]) != null)
						{
							double locX = config.getConfigurationSection("geocaches").getDouble(args[1].toLowerCase() + ".x");
							double locY = config.getConfigurationSection("geocaches").getDouble(args[1].toLowerCase() + ".y");
							double locZ = config.getConfigurationSection("geocaches").getDouble(args[1].toLowerCase() + ".z");
							String worldName = config.getConfigurationSection("geocaches").getString(args[1].toLowerCase() + ".world");
							World world = Bukkit.getWorld(worldName);				
							Location loc = new Location(world, locX, locY, locZ);
							Block sign = world.getBlockAt(loc);
							Block attachedChest = sign.getRelative(getAttachedChestBlockFace(sign));
							
							attachedChest.breakNaturally();
							config.getConfigurationSection("geocaches").set(args[1].toLowerCase(), null);
							saveConfig();
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
							
							if(navigating.contains(p.getName()))
							{
								p.sendMessage(ChatColor.GREEN + "[GeoCraft] Navigating disabled.");
								World world = p.getWorld();
								navigating.remove(p.getName());
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
							if(config.getConfigurationSection("geocaches").get(args[1]) != null)
							{
								double locX = config.getConfigurationSection("geocaches").getDouble(args[1].toLowerCase() + ".x");
								double locY = config.getConfigurationSection("geocaches").getDouble(args[1].toLowerCase() + ".y");
								double locZ = config.getConfigurationSection("geocaches").getDouble(args[1].toLowerCase() + ".z");
								String worldName = config.getConfigurationSection("geocaches").getString(args[1].toLowerCase() + ".world");
								World world = Bukkit.getWorld(worldName);
								Location loc = new Location(world, locX, locY, locZ);
								geoTarget = args[1];
								
								p.setCompassTarget(loc);
								navigating.add(p.getName());
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