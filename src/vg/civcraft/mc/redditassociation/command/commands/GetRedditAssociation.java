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

public class GetRedditAssociation extends PlayerCommand{

	private MysqlStorage db;
	
	public GetRedditAssociation(String name) {
		super(name);
		setIdentifier("getreddit");
		setDescription("Get the reddit account associated to this mc name.");
		setUsage("/getreddit");
		setArguments(0,0);
		db = RedditAssociationPlugin.getMysqlDB();
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player))
			return true;
		Player p = (Player) sender;
		UUID uuid = NameAPI.getUUID(p.getName());
		
		if (!db.hasAssociation(uuid)){
			p.sendMessage(ChatColor.RED + "You do not have a reddit association.");
			return true;
		}
		
		String name = db.getAssociation(uuid);
		p.sendMessage(ChatColor.GREEN + "The account " + name + " is what this mc account "
				+ "is associated with.");
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender arg0, String[] arg1) {
		return null;
	}

}
