package edu.sinclair.ssp.model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class CustomTask extends AbstractTask {

	@Column(nullable = false, length = 100)
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}