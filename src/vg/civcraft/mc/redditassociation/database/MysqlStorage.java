package vg.civcraft.mc.redditassociation.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import vg.civcraft.mc.civmodcore.Config;
import vg.civcraft.mc.civmodcore.annotations.CivConfig;
import vg.civcraft.mc.civmodcore.annotations.CivConfigType;
import vg.civcraft.mc.civmodcore.annotations.CivConfigs;
import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.redditassociation.RedditAssociationPlugin;

public class MysqlStorage {

	private Database db;
	private RedditAssociationPlugin plugin;
	
	private String addLookUp = "insert ignore into RedditBotLookUp(name, uuid, reddit_name) "
			+ "values(?,?,?);";
	private String addUserToRelations = "insert ignore into RedditRelations (name, uuid, reddit_name) values (?, ?, ?);";
	private String hasLookUp = "select ((select count(*) from redditcode where uuid = ?) "
			+ "+ (select count(*) from redditbotlookup where uuid = ?) "
			+ "+ (select count(*) from redditrelations where uuid = ?)) as c;";
	private String getLookUp = "select reddit_name from RedditRelations "
			+ "where uuid = ?;";
	private String getCode = "select * from RedditCode where uuid = ?;";
	private String deleteCode = "delete from RedditCode where uuid = ?;";

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
	
	@CivConfigs({
		@CivConfig(name = "mysql.username", def = "", type = CivConfigType.String),
		@CivConfig(name = "mysql.password", def = "", type = CivConfigType.String),
		@CivConfig(name = "mysql.host", def = "localhost", type = CivConfigType.String),
		@CivConfig(name = "mysql.dbname", def = "bukkit", type = CivConfigType.String),
		@CivConfig(name = "mysql.port", def = "3306", type = CivConfigType.Int)
	})
	public boolean initLogin(){
		Config config = plugin.GetConfig();
		String user = config.get("mysql.username").getString();
		String password = config.get("mysql.password").getString();
		String host = config.get("mysql.host").getString();
		String dbname = config.get("mysql.dbname").getString();
		int port = config.get("mysql.port").getInt();
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
		db.execute("create table if not exists RedditCode("
				+ "code varchar(255) not null,"
				+ "uuid varchar(36) not null,"
				+ "reddit_name varchar(255) not null,"
				+ "primary key uuid_key_code (uuid));");
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
			state.setString(2, uuid.toString());
			state.setString(3, uuid.toString());
			ResultSet set = state.executeQuery();
			if (!set.next())
				return false;
			return set.getInt("c") > 0;
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
	
	public void addUserToRelations(UUID uuid, String redditName){
		PreparedStatement state = db.prepareStatement(addUserToRelations);
		String name = NameAPI.getCurrentName(uuid);
		try {
			state.setString(1, name);
			state.setString(2, uuid.toString());
			state.setString(3, redditName);
			state.execute();
			deleteCode(uuid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String[] getCode(UUID uuid){
		PreparedStatement state = db.prepareStatement(getCode);
		try {
			state.setString(1, uuid.toString());
			ResultSet set = state.executeQuery();
			return set.next() ? new String[]{"" + set.getString("code"), set.getString("reddit_name")} : null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private void deleteCode(UUID uuid){
		PreparedStatement statement = db.prepareStatement(deleteCode);
		try {
			statement.setString(1, uuid.toString());
			statement.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
