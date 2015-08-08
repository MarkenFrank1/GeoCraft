package com.markstam1.geocraft;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;

import org.bukkit.Material;
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
				if(e.getLine(1).equalsIgnoreCase(""))
				{
					p.sendMessage(ChatColor.RED + "[GeoCraft] Line 2 is empty, please enter the geocache name.");
				}
				
				else
				{
					e.setLine(0, ChatColor.DARK_BLUE + "[GeoCraft]");
					e.setLine(2, ChatColor.DARK_RED + "Hidden by");
					e.setLine(3, ChatColor.DARK_RED + playerName);
					p.sendMessage(ChatColor.GREEN + "[GeoCraft] Geocache created.");
					String cacheName = e.getLine(1);
	
					config.set("geocaches." + cacheName + ".hider", playerName);
					saveConfig();
					
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
				e.getPlayer().sendMessage(ChatColor.GREEN + "[GeoCraft] Geocache deleted.");
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
				sender.sendMessage(ChatColor.GOLD + "/geo list: " + ChatColor.WHITE + "Shows all listed geocaches");	
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
						geonr++;
						
						sender.sendMessage(ChatColor.AQUA + "" + geonr + ". " + geoKey);
						
					}
					return true;
				}
				
				if(args[0].equalsIgnoreCase("help"))
				{
					sender.sendMessage("Available commands:");
					sender.sendMessage(ChatColor.GOLD + "/geo list: " + ChatColor.WHITE + "Shows all listed geocaches");					
					return true;
				}
				
				else
				{
					sender.sendMessage(ChatColor.RED + "Unknown argument, use /geo help to see a list of available commands.");
					return false;
				}
			}
		}
		
		return false;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}