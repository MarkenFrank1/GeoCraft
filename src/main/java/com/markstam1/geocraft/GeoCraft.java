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
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.material.Sign;
import org.bukkit.plugin.java.JavaPlugin;

public class GeoCraft extends JavaPlugin implements Listener
{
	public void onEnable()
	{
		getServer().getPluginManager().registerEvents(this, this);
		getConfig().options().copyDefaults(false);
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
					File geocachesfile = new File(getDataFolder(), "geocaches.yml");
					FileConfiguration config = YamlConfiguration.loadConfiguration(geocachesfile);
					config.set("geocaches." + cacheName + ".hider", playerName);
					
					try {
						config.save(geocachesfile);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
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
				sender.sendMessage(ChatColor.GOLD + "/geo list " + ChatColor.WHITE + "Shows all listed geocaches");				return true;
			}
			
			if(args.length >= 1)
			{
				if(args[0].equalsIgnoreCase("list"))
				{
					sender.sendMessage("Insert geocaches here...");
					return true;
				}
				
				if(args[0].equalsIgnoreCase("help"))
				{
					sender.sendMessage("Available commands:");
					sender.sendMessage(ChatColor.GOLD + "/geo list " + ChatColor.WHITE + "Shows all listed geocaches");					
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