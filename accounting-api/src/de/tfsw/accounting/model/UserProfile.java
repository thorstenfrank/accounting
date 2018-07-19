/**
 * 
 */
package de.tfsw.accounting.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * @author tfrank1
 *
 */
@Entity
public class UserProfile extends AbstractBaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	private String name;
	
	private String description;
	
	@OneToOne
	@JoinColumn(name = "primaryAddress")
	private Address primaryAddress;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "bankAccount")
	private BankAccount bankAccount;
	
	private String taxId;
	
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
	public BankAccount getBankAccount() {
		return bankAccount;
	}
	public void setBankAccount(BankAccount bankAccount) {
		this.bankAccount = bankAccount;
	}
	public String getTaxId() {
		return taxId;
	}
	public void setTaxId(String taxId) {
		this.taxId = taxId;
	}
}
