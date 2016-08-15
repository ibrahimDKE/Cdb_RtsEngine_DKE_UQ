package cdb;

public class DBSettings {
	public String database;
	public String databaseType;
	public String username;
	public String password;
	
	public static DBSettings getLocalDefault() {
		DBSettings s = new DBSettings();
		s.database = "127.0.0.1:5432/postgres";
		s.databaseType = "postgresql";
		s.username = "postgres";
		s.password = "himos";
		return s;
	}
	
	public static DBSettings getDefault() {
		return getLocalDefault();
	}
	
	public static DBSettings getPostgresDefault() {
		DBSettings s = new DBSettings();
		s.database = "127.0.0.1:5432/postgres";
		s.databaseType = "postgresql";
		s.username = "postgres";
		s.password = "himos";
		return s;
	}
	
	public static DBSettings getVerticaDefault() {
		DBSettings s = new DBSettings();
		s.database = "vise4.csail.mit.edu:5433/seedb_data";
		s.databaseType = "vertica";
		s.username = "dbadmin";
		s.password = "dbadmin";
		return s;
	}
}
