/*
 *  Copyright 2012, 2014 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * An expenditure of any kind.
 * 
 * @author Thorsten Frank
 * @since  1.0
 * @see    ExpenseType
 */
public class Expense extends AbstractExpense {

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 2L;

    public static final String FIELD_ID = "id";
    public static final String FIELD_PAYMENT_DATE = "paymentDate";
    public static final String FIELD_DEPRECIATION_METHOD = "depreciationMethod";
    public static final String FIELD_DEPRECIATION_PERIOD = "depreciationPeriodInYears";
    public static final String FIELD_SALVAGE_VALUE = "salvageValue";
    public static final String FIELD_DEPRECIATION_SCHEDULE = "depreciationSchedule";
    
    private String id;
    
    private LocalDate paymentDate;
    
    private DepreciationMethod depreciationMethod;
    
    private Integer depreciationPeriodInYears;
    
    private BigDecimal salvageValue;
    
    private List<AnnualDepreciation> depreciationSchedule;
    
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
     * @return the paymentDate
     */
    public LocalDate getPaymentDate() {
    	return paymentDate;
    }

	/**
     * @param paymentDate the paymentDate to set
     */
    public void setPaymentDate(LocalDate paymentDate) {
    	this.paymentDate = paymentDate;
    }
    
	/**
	 * @return the depreciationMethod
	 */
	public DepreciationMethod getDepreciationMethod() {
		return depreciationMethod;
	}

	/**
	 * @param depreciationMethod the depreciationMethod to set
	 */
	public void setDepreciationMethod(DepreciationMethod depreciationMethod) {
		this.depreciationMethod = depreciationMethod;
	}

	/**
	 * @return the depreciationPeriodInYears
	 */
	public Integer getDepreciationPeriodInYears() {
		return depreciationPeriodInYears;
	}

	/**
	 * @param depreciationPeriodInYears the depreciationPeriodInYears to set
	 */
	public void setDepreciationPeriodInYears(Integer depreciationPeriodInYears) {
		this.depreciationPeriodInYears = depreciationPeriodInYears;
	}

	/**
	 * @return the salvageValue
	 */
	public BigDecimal getSalvageValue() {
		return salvageValue;
	}

	/**
	 * @param salvageValue the salvageValue to set
	 */
	public void setSalvageValue(BigDecimal salvageValue) {
		this.salvageValue = salvageValue;
	}
	
	/**
	 * @return the depreciationSchedule
	 */
	public List<AnnualDepreciation> getDepreciationSchedule() {
		return depreciationSchedule;
	}

	/**
	 * FIXME this is pretty fuckin' ugly, letting clients set the schedule manually...
	 * 
	 * @param depreciationSchedule the depreciationSchedule to set
	 */
	public void setDepreciationSchedule(List<AnnualDepreciation> depreciationSchedule) {
		this.depreciationSchedule = depreciationSchedule;
	}

	/**
	 * 
	 * {@inheritDoc}
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("Expense: ");
		sb.append(getDescription());
		sb.append(" / Category: ").append(getCategory());
		sb.append(" Type: ").append(getExpenseType() != null ? getExpenseType().toString() : "null");
		return sb.toString();
	}
}
