package vg.civcraft.mc.redditassociation.command.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.namelayer.command.PlayerCommand;
import vg.civcraft.mc.redditassociation.RedditAssociationPlugin;
import vg.civcraft.mc.redditassociation.database.MysqlStorage;

public class AddRedditAssociation extends PlayerCommand{

	private MysqlStorage db;
	
	public AddRedditAssociation(String name) {
		super(name);
		setIdentifier("addreddit");
		setDescription("Add an account to your minecraft username.");
		setUsage("/addreddit <reddit account>");
		setArguments(1,1);
		db = RedditAssociationPlugin.getMysqlDB();
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player))
			return true;
		Player p = (Player) sender;
		UUID uuid = NameAPI.getUUID(p.getName());
		
		if (db.hasAssociation(uuid)){
			p.sendMessage(ChatColor.RED + "You already have an association with an account.");
			return true;
		}
		
		db.addLookUp(uuid, args[0]);
		p.sendMessage(ChatColor.GREEN + "The account " + args[0] + " will be sent "
				+ "a message asking to confirm that the account is yours.  It will take "
				+ "less than give minutes for it to send you a message.");
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender arg0, String[] arg1) {
		return null;
	}
}