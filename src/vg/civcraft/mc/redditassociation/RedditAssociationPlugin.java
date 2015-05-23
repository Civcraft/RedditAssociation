package vg.civcraft.mc.redditassociation;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.redditassociation.command.CommandHandler;
import vg.civcraft.mc.redditassociation.database.MysqlStorage;

public class RedditAssociationPlugin extends JavaPlugin{

	private CommandHandler handle;
	private static MysqlStorage db;
	@Override
	public void onEnable(){
		db = new MysqlStorage(this);
		NameAPI.getNameConfigManager().registerListener(this, db);
		db.initStartUp();
		
		handle = new CommandHandler();
		handle.registerCommands();
	}
	
	@Override
	public void onDisable(){
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return handle.execute(sender, cmd, args);
	}
	
	public static MysqlStorage getMysqlDB(){
		return db;
	}
}
