package com.markstam1.geocraft;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class GeoCraft extends JavaPlugin
{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("geo"))
		{
			if(args.length == 0) //Player types /geo without arguments
			{
				sender.sendMessage("Available commands:");
				sender.sendMessage(ChatColor.GOLD + "/geo list " + ChatColor.WHITE + "Shows all listed geocaches");
				sender.sendMessage(ChatColor.GOLD + "/geo create " + ChatColor.WHITE + "Creates a new geocache");	
				return true;
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
					sender.sendMessage(ChatColor.GOLD + "/geo create " + ChatColor.WHITE + "Creates a new geocache");	
					
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