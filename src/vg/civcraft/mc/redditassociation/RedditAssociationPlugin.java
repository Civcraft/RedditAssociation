package vg.civcraft.mc.redditassociation;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import vg.civcraft.mc.civmodcore.ACivMod;
import vg.civcraft.mc.redditassociation.command.CommandHandler;
import vg.civcraft.mc.redditassociation.database.MysqlStorage;

public class RedditAssociationPlugin extends ACivMod{

	private CommandHandler handle;
	private static MysqlStorage db;
	@Override
	public void onEnable(){
		super.onEnable();
		db = new MysqlStorage(this);
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
	
	@Override
	public void onLoad(){
		super.onLoad();
	}

	@Override
	protected String getPluginName() {
		return "RedditAssociationPlugin";
	}
}
