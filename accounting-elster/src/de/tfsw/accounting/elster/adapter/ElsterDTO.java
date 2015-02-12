/*
 *  Copyright 2015 Thorsten Frank (accounting@tfsw.de).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package de.tfsw.accounting.elster.adapter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.math.BigDecimal;

import de.tfsw.accounting.elster.Bundesland;

/**
 * @author Thorsten Frank
 *
 * @since 1.2
 */
public class ElsterDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private PropertyChangeSupport propertyChangeSupport;
	
	private TaxNumberGenerationStrategy taxNumberGenerationStrategy;
	
	private Bundesland finanzAmtBL;
	private String creationDate;
	private String timeFrameYear;
	private String timeFrameMonth;
	private String companyName;
	private String userFirstName;
	private String userLastName;
	private String companyTaxNumberOrig;
	private String companyTaxNumberGenerated;
	private String companyPhone;
	private String companyEmail;
	private String companyStreetName;
	private String companyStreetNumber;
	private String companyStreetAddendum;
	private String companyPostCode;
	private String companyCity;
	private String companyCountry;
	private BigDecimal revenue19;
	private BigDecimal revenue19tax;
	private BigDecimal revenue7;
	private BigDecimal revenue7tax;
	private BigDecimal inputTax;
	private BigDecimal taxSum;
	
	/**
	 * @param taxNumberGenerationStrategy
	 */
	ElsterDTO(TaxNumberGenerationStrategy taxNumberGenerationStrategy) {
		this.taxNumberGenerationStrategy = taxNumberGenerationStrategy;
		propertyChangeSupport = new PropertyChangeSupport(this);
	}
	
	/**
	 * Delegate method.
	 * 
	 * @param listener
	 * @see java.beans.PropertyChangeSupport#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * Delegate method.
	 * 
	 * @param listener
	 * @see java.beans.PropertyChangeSupport#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/**
	 * @return the finanzAmtBL
	 */
	public Bundesland getFinanzAmtBL() {
		return finanzAmtBL;
	}
	
	/**
	 * @param finanzAmtBL the finanzAmtBL to set
	 */
	public void setFinanzAmtBL(Bundesland finanzAmtBL) {
		this.finanzAmtBL = finanzAmtBL;
		generateTaxNumber();
	}

	/**
	 * 
	 */
	private void generateTaxNumber() {
		final String oldValue = companyTaxNumberGenerated;
		companyTaxNumberGenerated = taxNumberGenerationStrategy.generateTaxNumber(finanzAmtBL, companyTaxNumberOrig);
		propertyChangeSupport.firePropertyChange("companyTaxNumberGenerated", oldValue, companyTaxNumberGenerated);
	}
	
	/**
	 * @return the creationDate
	 */
	public String getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the timeFrameYear
	 */
	public String getTimeFrameYear() {
		return timeFrameYear;
	}

	/**
	 * @param timeFrameYear the timeFrameYear to set
	 */
	public void setTimeFrameYear(String timeFrameYear) {
		this.timeFrameYear = timeFrameYear;
	}

	/**
	 * @return the timeFrameMonth
	 */
	public String getTimeFrameMonth() {
		return timeFrameMonth;
	}

	/**
	 * @param timeFrameMonth the timeFrameMonth to set
	 */
	public void setTimeFrameMonth(String timeFrameMonth) {
		this.timeFrameMonth = timeFrameMonth;
	}

	/**
	 * @return the companyName
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * @param companyName the companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * @return the userFirstName
	 */
	public String getUserFirstName() {
		return userFirstName;
	}

	/**
	 * @param userFirstName the userFirstName to set
	 */
	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}

	/**
	 * @return the userLastName
	 */
	public String getUserLastName() {
		return userLastName;
	}

	/**
	 * @param userLastName the userLastName to set
	 */
	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}

	/**
	 * @return the companyTaxNumberOrig
	 */
	public String getCompanyTaxNumberOrig() {
		return companyTaxNumberOrig;
	}

	/**
	 * @param companyTaxNumberOrig the companyTaxNumberOrig to set
	 */
	public void setCompanyTaxNumberOrig(String companyTaxNumberOrig) {
		this.companyTaxNumberOrig = companyTaxNumberOrig;
		generateTaxNumber();
	}

	/**
	 * @return the companyTaxNumberGenerated
	 */
	public String getCompanyTaxNumberGenerated() {
		return companyTaxNumberGenerated;
	}
	
	/**
	 * @return the companyPhone
	 */
	public String getCompanyPhone() {
		return companyPhone;
	}

	/**
	 * @param companyPhone the companyPhone to set
	 */
	public void setCompanyPhone(String companyPhone) {
		this.companyPhone = companyPhone;
	}

	/**
	 * @return the companyEmail
	 */
	public String getCompanyEmail() {
		return companyEmail;
	}

	/**
	 * @param companyEmail the companyEmail to set
	 */
	public void setCompanyEmail(String companyEmail) {
		this.companyEmail = companyEmail;
	}

	/**
	 * @return the companyStreetName
	 */
	public String getCompanyStreetName() {
		return companyStreetName;
	}

	/**
	 * @param companyStreetName the companyStreetName to set
	 */
	public void setCompanyStreetName(String companyStreetName) {
		this.companyStreetName = companyStreetName;
	}

	/**
	 * @return the companyStreetNumber
	 */
	public String getCompanyStreetNumber() {
		return companyStreetNumber;
	}

	/**
	 * @param companyStreetNumber the companyStreetNumber to set
	 */
	public void setCompanyStreetNumber(String companyStreetNumber) {
		this.companyStreetNumber = companyStreetNumber;
	}

	/**
	 * @return the companyStreetAddendum
	 */
	public String getCompanyStreetAddendum() {
		return companyStreetAddendum;
	}

	/**
	 * @param companyStreetAddendum the companyStreetAddendum to set
	 */
	public void setCompanyStreetAddendum(String companyStreetAddendum) {
		this.companyStreetAddendum = companyStreetAddendum;
	}

	/**
	 * @return the companyPostCode
	 */
	public String getCompanyPostCode() {
		return companyPostCode;
	}

	/**
	 * @param companyPostCode the companyPostCode to set
	 */
	public void setCompanyPostCode(String companyPostCode) {
		this.companyPostCode = companyPostCode;
	}

	/**
	 * @return the companyCity
	 */
	public String getCompanyCity() {
		return companyCity;
	}

	/**
	 * @param companyCity the companyCity to set
	 */
	public void setCompanyCity(String companyCity) {
		this.companyCity = companyCity;
	}

	/**
	 * @return the companyCountry
	 */
	public String getCompanyCountry() {
		return companyCountry;
	}

	/**
	 * @param companyCountry the companyCountry to set
	 */
	public void setCompanyCountry(String companyCountry) {
		this.companyCountry = companyCountry;
	}

	/**
	 * @return the revenue19
	 */
	public BigDecimal getRevenue19() {
		return revenue19;
	}

	/**
	 * @param revenue19 the revenue19 to set
	 */
	public void setRevenue19(BigDecimal revenue19) {
		this.revenue19 = revenue19;
	}

	/**
	 * @return the revenue19tax
	 */
	public BigDecimal getRevenue19tax() {
		return revenue19tax;
	}

	/**
	 * @param revenue19tax the revenue19tax to set
	 */
	public void setRevenue19tax(BigDecimal revenue19tax) {
		this.revenue19tax = revenue19tax;
	}

	/**
	 * @return the revenue7
	 */
	public BigDecimal getRevenue7() {
		return revenue7;
	}

	/**
	 * @param revenue7 the revenue7 to set
	 */
	public void setRevenue7(BigDecimal revenue7) {
		this.revenue7 = revenue7;
	}

	/**
	 * @return the revenue7tax
	 */
	public BigDecimal getRevenue7tax() {
		return revenue7tax;
	}

	/**
	 * @param revenue7tax the revenue7tax to set
	 */
	public void setRevenue7tax(BigDecimal revenue7tax) {
		this.revenue7tax = revenue7tax;
	}

	/**
	 * @return the inputTax
	 */
	public BigDecimal getInputTax() {
		return inputTax;
	}

	/**
	 * @param inputTax the inputTax to set
	 */
	public void setInputTax(BigDecimal inputTax) {
		this.inputTax = inputTax;
	}

	/**
	 * @return the taxSum
	 */
	public BigDecimal getTaxSum() {
		return taxSum;
	}

	/**
	 * @param taxSum the taxSum to set
	 */
	public void setTaxSum(BigDecimal taxSum) {
		this.taxSum = taxSum;
	}
}
