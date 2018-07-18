/**
 * 
 */
package de.tfsw.accounting.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 * @author tfrank1
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Organisation extends AbstractBaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	private String name;
	private String description;
	private Address primaryAddress;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Address getPrimaryAddress() {
		return primaryAddress;
	}
	public void setPrimaryAddress(Address primaryAddress) {
		this.primaryAddress = primaryAddress;
	}
}
