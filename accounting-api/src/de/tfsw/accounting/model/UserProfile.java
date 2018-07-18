/**
 * 
 */
package de.tfsw.accounting.model;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

/**
 * @author tfrank1
 *
 */
@Entity
@PrimaryKeyJoinColumn(name = "Organisation", referencedColumnName = "name")
public class UserProfile extends Organisation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private BankAccount bankAccount;
	private String taxId;
	
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
