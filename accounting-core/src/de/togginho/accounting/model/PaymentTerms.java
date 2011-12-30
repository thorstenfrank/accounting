/*
 *  Copyright 2011 thorsten frank (thorsten.frank@gmx.de).
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
package de.togginho.accounting.model;

import java.io.Serializable;

/**
 * Payment terms describe when and how an {@link Invoice} is to be paid by the {@link Client} to which is is issued.
 * 
 * @author thorsten
 *
 */
public class PaymentTerms implements Serializable {

	/**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 8912815234249967592L;
    
    /** Reasonable default. */
    public static final PaymentTerms DEFAULT = new PaymentTerms(PaymentType.TRADE_CREDIT, 30);
    
    public static final String FIELD_TYPE = "paymentType";
    public static final String FIELD_FULL_PAYMENT_TARGET = "fullPaymentTargetInDays";
    
    /**
     * 
     */
    private PaymentType paymentType;
    
    /**
     * 
     */
	private int fullPaymentTargetInDays;

	
	/*
	 * private double discountPercentage;
	 * private int discountedPaymentTargetInDays;
	 */
	
	/**
     * 
     */
    public PaymentTerms() {
	    // need a default constructor
    }
	
	/**
     * @param paymentType
     * @param fullPaymentTargetInDays
     */
    public PaymentTerms(PaymentType paymentType, int fullPaymentTargetInDays) {
	    this.paymentType = paymentType;
	    this.fullPaymentTargetInDays = fullPaymentTargetInDays;
    }

	/**
     * @return the fullPaymentTargetInDays
     */
    public int getFullPaymentTargetInDays() {
    	return fullPaymentTargetInDays;
    }

	/**
     * @return the paymentType
     */
    public PaymentType getPaymentType() {
    	return paymentType;
    }

	/**
     * @param paymentType the paymentType to set
     */
    public void setPaymentType(PaymentType paymentType) {
    	this.paymentType = paymentType;
    }

	/**
     * @param fullPaymentTargetInDays the fullPaymentTargetInDays to set
     */
    public void setFullPaymentTargetInDays(int fullPaymentTargetInDays) {
    	this.fullPaymentTargetInDays = fullPaymentTargetInDays;
    }

	/**
     * {@inheritDoc}.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + fullPaymentTargetInDays;
	    result = prime * result + ((paymentType == null) ? 0 : paymentType.hashCode());
	    return result;
    }

	/**
     * {@inheritDoc}.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	    if (this == obj)
		    return true;
	    if (obj == null)
		    return false;
	    if (getClass() != obj.getClass())
		    return false;
	    PaymentTerms other = (PaymentTerms) obj;
	    if (fullPaymentTargetInDays != other.fullPaymentTargetInDays)
		    return false;
	    if (paymentType != other.paymentType)
		    return false;
	    return true;
    }
}