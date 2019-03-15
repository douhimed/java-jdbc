package org.mql.biblio.models;

import org.mql.biblio.annotations.Column;
import org.mql.biblio.annotations.Id;
import org.mql.biblio.annotations.Table;

@Table(name = "authors")
public class Author {

	@Id(auto_increment=true)
	@Column(name="Au_ID")
	private int id;
	
	@Column(name="Author")
	private String name;
	
	@Column
	private int Year_Born;
	
	public Author() {
	}

	public Author(String name, int year_Born) {
		super();
		this.name = name;
		Year_Born = year_Born;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getYear_Born() {
		return Year_Born;
	}

	public void setYear_Born(int year_Born) {
		Year_Born = year_Born;
	}

	
}
