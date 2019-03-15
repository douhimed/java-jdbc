package org.mql.biblio.dao.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;

public class DataSource {

	private String url;
	private String driver;
	private String userName;
	private String password;
	
	public DataSource() {
	}

	public DataSource(String url, String driver, String userName, String password) {
		this.url = url;
		this.driver = driver;
		this.userName = userName;
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/* API JDBC => specification. Driver => implementation */
	public Connection getConnection() {
		try {
			Class.forName(driver);
			Connection db = DriverManager.getConnection(url, userName, password);
			System.out.println("Connexion bien établie");
			return db;
		} catch (Exception e) {
			System.out.println("Went bad in connection : " + e.getMessage());
			return null;
		}
	}
}
