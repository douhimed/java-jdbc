package org.mql.biblio.dao.jdbc;

public class MySQLDataSource extends DataSource{

	public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
	public static final String MYSQL_BRIDGE = "jdbc:mysql:";
	
	public MySQLDataSource() {
	}
	
	public MySQLDataSource(String host, String source, String userName, String password) {
		super(MYSQL_BRIDGE + "//" + host + "/" + source, MYSQL_DRIVER, userName, password);
	}
	
	public MySQLDataSource(String source, String userName, String password) {
		super(MYSQL_BRIDGE + "//localhost/" + source, MYSQL_DRIVER, userName, password);
	}
	
	public MySQLDataSource(String source) {
		this(source, "root", "");
	}

}
