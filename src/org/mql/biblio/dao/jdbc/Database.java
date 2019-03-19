package org.mql.biblio.dao.jdbc;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.print.attribute.standard.Fidelity;

import org.mql.biblio.annotations.Column;
import org.mql.biblio.annotations.Id;
import org.mql.biblio.annotations.Table;

public class Database {

	private DataSource dataSource;
	private Connection db;

	public Database() {
	}

	public Database(DataSource dataSource) {
		setDataSource(dataSource);
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		db = this.dataSource.getConnection();
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public String[][] executeQuery(String query) {
		try {
			Statement sql = db.createStatement();
			ResultSet rs = sql.executeQuery(query);
			ResultSetMetaData rsm = rs.getMetaData();
			int columnCount = rsm.getColumnCount();
			rs.last();
			int rowCount = rs.getRow();
			String[][] data = new String[rowCount + 1][columnCount];
			for (int i = 0; i < columnCount; i++) {
				data[0][i] = rsm.getColumnName(i + 1);
			}
			rs.beforeFirst();
			int row = 1;
			while (rs.next()) {
				for (int i = 0; i < columnCount; i++) {
					data[row][i] = rs.getString(i + 1);
				}
				row++;
			}
			rs.close();
			return data;
		} catch (Exception e) {
			System.out.println("Went bad at select method : " + e.getMessage());
			return null;
		}
	}

	public String[][] selectFromTable(String tableName) {
		String query = "SELECT * FROM " + tableName;
		return executeQuery(query);
	}

	public String[][] selectLike(String tableName, String key, String value) {
		String query = "SELECT * FROM " + tableName + " WHERE " + key + " LIKE '%" + value + "%'";
		return executeQuery(query);
	}

	public String[][] selectEqual(String tableName, String key, String value){
		String query = "SELECT * FROM " + tableName + " WHERE " + key + " = '" + value + "'";
		return executeQuery(query);
	}
	
	public Object update(Object object) {
		return update(object.getClass(), object);
	}

	public int update(Class<?> clazz, Object object) {
		Field[] fields = clazz.getDeclaredFields();

		StringBuilder query = new StringBuilder("UPDATE " + getTableName(clazz) + " SET ");
		StringBuilder whereClose = new StringBuilder(" WHERE ");
		for (Field field : fields) {

			boolean status = field.isAccessible();
			field.setAccessible(true);

			String fieldName = getFieldName(field);

			if (field.isAnnotationPresent(Column.class) && !field.isAnnotationPresent(Id.class)) {

				try {
					query.append(fieldName + " ='" + field.get(object) + "', ");
				} catch (Exception e) {
					System.out.println("Went bad at update method : " + e.getMessage());
				}
			} else {
				try {
					whereClose.append(fieldName + " = " + field.get(object));
				} catch (Exception e) {
					System.out.println("Went bad at update method : " + e.getMessage());
				}
			}
			field.setAccessible(status);
		}
		return updateQuery(query.substring(0, query.length() - 2) + whereClose);

	}

	public int insert(String[][] data, String tableName) {

		StringBuilder query = new StringBuilder("INSERT INTO " + tableName + " VALUES(");

		for (String[] keyValue : data) {
			query.append(keyValue[0] + " ='" + keyValue[1] + "', ");
		}

		return updateQuery(query.substring(0, query.length() - 2) + ")");

	}

	private String getFieldName(Field field) {
		String fieldName = field.getName();
		Column column = field.getAnnotation(Column.class);
		if (!column.name().isEmpty())
			fieldName = column.name();
		return fieldName;
	}

	private String getTableName(Class<?> clazz) {
		String tableName = clazz.getSimpleName();
		Table table = clazz.getAnnotation(Table.class);
		if (!table.name().isEmpty())
			tableName = table.name();
		return tableName;
	}

	private int updateQuery(String query) {
		try (Statement stm = db.createStatement()) {
			return stm.executeUpdate(query);
		} catch (Exception e) {
			System.out.println("Went bad at updateQuery method : " + e.getMessage());
			return 0;
		}
	}

	public int insert(Object object) {

		Class<?> clazz = object.getClass();
		Field[] fields = clazz.getDeclaredFields();

		StringBuilder query = new StringBuilder("INSERT INTO " + getTableName(clazz) + " VALUES(");

		for (Field field : fields) {
			boolean status = field.isAccessible();
			field.setAccessible(true);

			if (isPrimaryKeyAutoIncrement(field)) {
				try {
					field.set(object, getMaxId(clazz) + 1);
				} catch (Exception e) {
					System.out
							.println("Went bad at insert method at get next value og primary key : " + e.getMessage());
				}
			}

			try {
				query.append("'" + field.get(object) + "', ");
			} catch (Exception e) {
				System.out.println("Went bad at insert method : " + e.getMessage());
			}

			field.setAccessible(status);
		}
		return updateQuery(query.substring(0, query.length() - 2) + ")");

	}

	public int getMaxId(Class clazz) {
		Field primaryField = getPrimaryField(clazz);
		if (primaryField != null) {
			String query = "SELECT MAX(" + getFieldName(primaryField) + ") max_id from " + getTableName(clazz);

			Statement stm;
			try {
				stm = db.createStatement();
				ResultSet rs = stm.executeQuery(query);
				int maxId = -1;
				if (rs.next()) {
					maxId = rs.getInt("max_id");
					return maxId;
				}
			} catch (SQLException e) {
				System.out.println("Went bad at getmaxID method : " + e.getMessage());
				return 0;
			}
		}
		return 0;
	}

	private Field getPrimaryField(Class clazz) {

		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(Id.class))
				return field;
		}
		return null;
	}

	private boolean isPrimaryKeyAutoIncrement(Field field) {
		if (field.isAnnotationPresent(Id.class))
			return field.getAnnotation(Id.class).auto_increment();
		return false;
	}

	public int delete(Object object) {
		Class<?> clazz = object.getClass();
		String query = "DELETE FROM " + getTableName(clazz) + " WHERE ";
		Field primaryField = getPrimaryField(clazz);
		try {
			boolean status = primaryField.isAccessible();
			primaryField.setAccessible(true);
			query = query + getFieldName(primaryField) + " = '" + primaryField.get(object) + "'";
			primaryField.setAccessible(status);
		} catch (Exception e) {
			System.out.println("Went bad at delete method : " + e.getMessage());
			return 0;
		}
		return updateQuery(query);
	}

}
