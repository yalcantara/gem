package com.gem.config.ws.entities;

import org.bson.types.ObjectId;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;



/**
 * The persistent class for the keys database table.
 *
 */
@Entity
@Table(name = "keys")
public class Key implements Serializable {
	
	private static final long serialVersionUID = -2087932688014108583L;
	
	@Id
	private ObjectId _id;
	
	private String name;
	private String label;
	private String value;

	private Date creationDate;

	private Date lastUpdate;
	
	public Key() {
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
	
	public String getValue() {
		return this.value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Key{" +
				"_id=" + _id +
				", name='" + name + '\'' +
				", label='" + label + '\'' +
				", value='" + value + '\'' +
				", creationDate=" + creationDate +
				", lastUpdate=" + lastUpdate +

				'}';
	}
}