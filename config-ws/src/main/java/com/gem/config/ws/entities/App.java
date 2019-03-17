package com.gem.config.ws.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * The persistent class for the apps database table.
 *
 */
@Entity
public class App implements Serializable {

	private static final long serialVersionUID = -5368036186948391390L;
	
	@Id
	@JsonSerialize(using = ToStringSerializer.class)
	private ObjectId id;

	private String name;
	
	private String label;
	
	private Date creationDate;

	private Date lastUpdate;

	public App() {
		super();
	}

	public ObjectId getId() {
		return this.id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Date getCreationDate() {
		return this.creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Date getLastUpdate() {
		return this.lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "App [id=" + id + ", name=" + name + ", label=" + label + ", creationDate="
				+ creationDate + ", lastUpdate=" + lastUpdate + "]";
	}

}