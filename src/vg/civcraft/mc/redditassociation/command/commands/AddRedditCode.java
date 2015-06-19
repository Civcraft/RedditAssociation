package vg.civcraft.mc.redditassociation.command.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.civmodcore.command.PlayerCommand;
import vg.civcraft.mc.redditassociation.RedditAssociationPlugin;
import vg.civcraft.mc.redditassociation.database.MysqlStorage;

public class AddRedditCode extends PlayerCommand{

	private MysqlStorage db;
	
	public AddRedditCode(String name) {
		super(name);
		setIdentifier("addredditcode");
		setDescription("Add the code given by the reddit bot to associate the accounts.");
		setUsage("/addredditcode <code>");
		setArguments(1,1);
		db = RedditAssociationPlugin.getMysqlDB();
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player))
			return true;
		Player p = (Player) sender;
		UUID uuid = NameAPI.getUUID(p.getName());
		String[] x = db.getCode(uuid);
		if (x == null){
			p.sendMessage(ChatColor.RED + "You have not entered a correct code. If you need to add an account use /ar <reddit name>.");
			return true;
		}
		
		int code = Integer.parseInt(x[0]);
		String redditName = x[1];
		
		int playerCode = 0;
		try {
			playerCode = Integer.parseInt(args[0]);
		} catch (NumberFormatException e){
			p.sendMessage(ChatColor.RED + "That is an invalid code.");
			return true;
		}
		
		if (playerCode != code){
			p.sendMessage(ChatColor.RED + "The code you have entered does not match.");
			return true;
		}
		
		db.addUserToRelations(uuid, redditName);
		p.sendMessage(ChatColor.GREEN + "Account has been successfully added.");
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender arg0, String[] arg1) {
		return null;
	}

}
