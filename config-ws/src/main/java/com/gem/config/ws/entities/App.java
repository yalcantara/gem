package com.gem.config.ws.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.bson.types.ObjectId;

/**
 * The persistent class for the apps database table.
 *
 */
@Entity
public class App implements Serializable {
	
	private static final long serialVersionUID = -5368036186948391390L;

	@Id
	private ObjectId _id;
	
	private String name;

	private String label;

	private Date creationDate;
	
	private Date lastUpdate;
	
	public App() {
		super();
	}
	
	public ObjectId getId() {
		return this._id;
	}
	
	public void setId(ObjectId id) {
		this._id = id;
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
		return "App [_id=" + _id + ", name=" + name + ", label=" + label + ", creationDate="
				+ creationDate + ", lastUpdate=" + lastUpdate + "]";
	}
	
}