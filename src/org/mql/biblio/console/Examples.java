package org.mql.biblio.console;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.mql.biblio.dao.jdbc.DataSource;
import org.mql.biblio.dao.jdbc.Database;
import org.mql.biblio.dao.jdbc.MySQLDataSource;
import org.mql.biblio.models.Author;

public class Examples {

	DataSource ds;
	Database db;
	
	public Examples() {
		init();
		exp04();
	}
	
	private void exp09() {
		String[][] data = new String[][] {{"Au_ID", "1000002"}, {"Author", "Mohammed"}, {"Year_Born", "1990"}};
		System.out.println(db.insert(data, "Authors"));
	}

	private void exp08() {
		Author author = new Author("Medox", 1994);
		author.setId(1000000);
		System.out.println(db.delete(author));
	}

	private void init() {
		ds = new MySQLDataSource("Biblio");
		db = new Database(ds);
	}

	private void exp07() {
		Author author = new Author("Medox", 1994);
		author.setId(999999);
		System.out.println(db.insert(author));
	}

	private void exp06() {
		System.out.println(db.getMaxId(Author.class));
	}

	private void exp05() {
		Author author = new Author("Med", 1994);
		author.setId(1);
		System.out.println(db.update(author));
	}

	private void exp04() {
		String[][] data = db.selectLike("Authors", "Author", "james");
		print(data);		
	}
	
	private void exp03() {
		String[][] data = db.selectFromTable("Titles");
		print(data);		
	}

	private void exp02() {
		String[][] data = db.executeQuery("Select * FROM Authors WHERE Year_Born > 0");
		print(data);
	}

	private void print(String[][] data) {
		JTable table = new JTable(data, data[0]);
		JFrame frame = new JFrame();
		frame.setContentPane(new JScrollPane(table));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.pack();
	}

	private void exp01() {
		DataSource ds = new MySQLDataSource("localhost", "Biblio", "root", "");
		ds.getConnection();
	}

	public static void main(String[] args) {
		new Examples();
	}

}
