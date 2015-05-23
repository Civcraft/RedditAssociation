package vg.civcraft.mc.redditassociation.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;

import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.namelayer.config.NameConfigListener;
import vg.civcraft.mc.namelayer.config.NameConfigManager;
import vg.civcraft.mc.namelayer.config.annotations.NameConfig;
import vg.civcraft.mc.namelayer.config.annotations.NameConfigType;
import vg.civcraft.mc.namelayer.config.annotations.NameConfigs;
import vg.civcraft.mc.redditassociation.RedditAssociationPlugin;

public class MysqlStorage implements NameConfigListener{

	private Database db;
	private RedditAssociationPlugin plugin;
	
	private String addLookUp = "insert ignore into RedditBotLookUp(name, uuid, reddit_name) "
			+ "values(?,?);";
	private String hasLookUp = "select count(*) as count from RedditRelations "
			+ "where uuid = ?;";
	private String getLookUp = "select reddit_name from RedditRelations "
			+ "where uuid = ?;";
	
	public MysqlStorage(RedditAssociationPlugin plugin){
		this.plugin = plugin;
	}
	
	public void initStartUp(){
		if (!initLogin()){
			Bukkit.getPluginManager().disablePlugin(plugin);
			return;
		}
		initTables();
	}
	
	@NameConfigs({
		@NameConfig(name = "mysql.username", def = "", type = NameConfigType.String),
		@NameConfig(name = "mysql.password", def = "", type = NameConfigType.String),
		@NameConfig(name = "mysql.host", def = "localhost", type = NameConfigType.String),
		@NameConfig(name = "mysql.dbname", def = "bukkit", type = NameConfigType.String),
		@NameConfig(name = "mysql.port", def = "3306", type = NameConfigType.Int)
	})
	private boolean initLogin(){
		NameConfigManager config = NameAPI.getNameConfigManager();
		String user = config.get(plugin, "mysql.username").getString();
		String password = config.get(plugin, "mysql.password").getString();
		String host = config.get(plugin, "mysql.host").getString();
		String dbname = config.get(plugin, "mysql.dbname").getString();
		int port = config.get(plugin, "mysql.port").getInt();
		db = new Database(host, port, dbname, user, password, plugin.getLogger());
		return db.connect();
	}
	
	private void initTables(){
		db.execute("create table if not exists RedditRelations("
				+ "name varchar(16) not null,"
				+ "uuid varchar(36) not null,"
				+ "reddit_name varchar(255) not null,"
				+ "primary key uuid_key(uuid));");
		db.execute("create table if not exists RedditBotLookUp("
				+ "name varchar(16) not null,"
				+ "uuid varchar(36) not null,"
				+ "reddit_name varchar(255) not null,"
				+ "primary key uuid_key(uuid));");
	}
	
	public void addLookUp(UUID uuid, String reddit_name){
		PreparedStatement state = db.prepareStatement(addLookUp);
		try {
			String name = NameAPI.getCurrentName(uuid);
			state.setString(1, name);
			state.setString(2, uuid.toString());
			state.setString(3, reddit_name); // The reddit name
			state.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Checks if the player's uuid already has a reddit connection.
	 */
	public boolean hasAssociation(UUID uuid){
		PreparedStatement state = db.prepareStatement(hasLookUp);
		try {
			state.setString(1, uuid.toString());
			ResultSet set = state.executeQuery();
			if (set.next())
				return false;
			return set.getInt("count") > 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public String getAssociation(UUID uuid){
		PreparedStatement state = db.prepareStatement(getLookUp);
		try {
			state.setString(1, uuid.toString());
			ResultSet set = state.executeQuery();
			return set.next() ? set.getString("reddit_name") : null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
