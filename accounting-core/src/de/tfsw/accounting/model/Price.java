/*
 *  Copyright 2011, 2014 Thorsten Frank (accounting@tfsw.de).
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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * A non-persistent container to hold complete price data, i.e. net, gross and tax amounts that make up a single price. 
 * {@link #getTax()} may be <code>null</code> if no tax is applicable, in which case net and gross price will be equal.
 * 
 * <p>This class is not meant to be used directly by clients, but should rather be obtained using the calculation
 * utility {@link de.tfsw.accounting.util.CalculationUtil}.</p>
 * 
 * <p>
 * Support for {@link PropertyChangeListener}s is provided.
 * </p>
 * 
 * @author Thorsten Frank
 * @see    de.tfsw.accounting.util.CalculationUtil#calculatePrice(InvoicePosition)
 * @see    de.tfsw.accounting.util.CalculationUtil#calculateTotalPrice(Invoice)
 * @see    PropertyChangeListener
 * @since  1.0
 */
public class Price implements Comparable<Price>, Serializable {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * FIELDS
     */
    public static final String FIELD_NET = "net";
    public static final String FIELD_TAX = "tax";
    public static final String FIELD_GROSS = "gross";
    
    /** **/
    private static final MathContext MATH_CONTEXT = new MathContext(34, RoundingMode.HALF_UP);
    private static final int SCALE = 2;
           
	private BigDecimal net;
	private BigDecimal tax;
	private BigDecimal gross;
	private PropertyChangeSupport propertyChangeSupport;
	
	/**
     * Creates a new price with no values.
     * 
     * <p>This constructor is the equivalent of <code>new Price(BigDecimal.ZERO, null, BigDecimal.ZERO);</code></p>
     */
    public Price() {
    	this (BigDecimal.ZERO, null, BigDecimal.ZERO);
    }
    
	/**
	 * Creates a new price with the specified values.
	 * 
     * @param net net price
     * @param tax tax amount
     * @param gross gross price
     */
    public Price(BigDecimal net, BigDecimal tax, BigDecimal gross) {
    	propertyChangeSupport = new PropertyChangeSupport(this);
	    this.net = net;
	    this.tax = tax;
	    this.gross = gross;
    }

	/**
     * @return the net price
     */
    public BigDecimal getNet() {
    	return net;
    }

	/**
     * @param net the net price
     */
    public void setNet(BigDecimal net) {
    	final BigDecimal oldValue = this.net;
    	this.net = net;
    	propertyChangeSupport.firePropertyChange(FIELD_NET, oldValue, net);
    }

	/**
     * @return the tax amount
     */
    public BigDecimal getTax() {
    	return tax;
    }

	/**
     * @param tax the tax amount
     */
    public void setTax(BigDecimal tax) {
    	final BigDecimal oldValue = this.tax;
    	this.tax = tax;
    	propertyChangeSupport.firePropertyChange(FIELD_TAX, oldValue, this.tax);
    }

	/**
     * @return the gross price
     */
    public BigDecimal getGross() {
    	return gross;
    }

	/**
     * @param gross the gross price
     */
    public void setGross(BigDecimal gross) {
    	final BigDecimal oldValue = this.gross;
    	this.gross = gross;
    	propertyChangeSupport.firePropertyChange(FIELD_GROSS, oldValue, this.gross);
    }
    
    /**
     * Adds the supplied amount to this price's net value.
     * 
     * @param net the addition to {@link #getNet()}
     */
    public void addToNet(BigDecimal net) {
    	if (net == null) {
    		return;
    	}
    	
    	if (this.net == null) {
    		setNet(BigDecimal.ZERO);
    	}
    	
    	setNet(this.net.add(net));
    }
    
    /**
     * Adds the supplied amount to this price's tax value.
     * 
     * @param tax the addition to {@link #getTax()}
     */
    public void addToTax(BigDecimal tax) {
    	if (tax == null) {
    		return;
    	}
    	
    	if (this.tax == null) {
    		setTax(BigDecimal.ZERO);
    	}
    	
    	setTax(this.tax.add(tax));
    }
    
    /**
     * Adds the supplied amount to this price's gross value.
     * 
     * @param gross the addition to {@link #getGross()}
     */
    public void addToGross(BigDecimal gross) {
    	if (gross == null) {
    		return;
    	}
    	
    	if (this.gross == null) {
    		setGross(BigDecimal.ZERO);
    	}
    	setGross(this.gross.add(gross));
    }
    
    /**
     * Adds the supplied price to this one. To be more specific, all three values of this price are adjusted such that
     * <code>this.value = this.value.add(price.value)</code>, where <code>value</code> is one of <code>net</code>,
     * <code>gross</code> or <code>tax</code>.
     * 
     * @param price	the price to add to this one
     * 
     * @see	  #addToNet(BigDecimal)
     * @see	  #addToGross(BigDecimal)
     * @see	  #addToTax(BigDecimal)
     */
    public void add(Price price) {
    	if (price == null) {
    		return;
    	}
    	addToNet(price.getNet());
    	addToTax(price.getTax());
    	addToGross(price.getGross());
    }
    
    /**
     * Subtracts the supplied price from this one. To be more specific, all three values of this price are adjusted 
     * such that <code>this.value = this.value.subtract(price.value)</code> where <code>value</code> is one of 
     * <code>net</code>, <code>gross</code> or <code>tax</code>.
     * 
     * <p>This method does not prohibit negative values.</p>
     *  
     * @param price	the price to subtract from this one
     */
    public void subtract(Price price) {
    	if (price == null) {
    		return;
    	}
    	
    	if (net == null) {
    		setNet(BigDecimal.ZERO);
    	}
    	if (price.getNet() != null) {
    		setNet(net.subtract(price.getNet()));
    	}
    	
    	if (gross == null) {
    		setGross(BigDecimal.ZERO);
    	}
    	if (price.getGross() != null) {
    		setGross(gross.subtract(price.getGross()));
    	}
    	
    	if (price.getTax() != null) {
    		if (tax == null) {
    			setTax(BigDecimal.ZERO);
    		}
    		setTax(tax.subtract(price.getTax()));
    	}
    }
    
    /**
     * Calculates the gross value of this price from a pre-existing net value using the supplied {@link TaxRate}.
     * 
     * <p>Calculation logic is as follows:
     * <ol>
     * 	<li>
     * 		if the supplied {@link TaxRate} (or {@link TaxRate#getRate()}) is <code>null</code>, this Price's gross is 
     * 		set to current net value
     *	</li>
     * 	<li>
     * 		if {@link #getNet()} is <code>null</code>, both the net and gross values are set to {@link BigDecimal#ZERO}
     * 		and the tax value is set to <code>null</code>
     * 	</li>
     * 	<li>
     * 		if neither this Price's net value nor the supplied tax rate is <code>null</code>, the tax value is 
     * 		calculated as <pre>{@link #getNet()} * {@link TaxRate#getRate()}</pre> and the gross value as 
     * 		<pre>{@link #getNet()} + {@link #getTax()}</pre>
     *	</li>
     * </ol>
     * </p>
     * 
     * @param taxRate the tax rate to apply to this price, may be <code>null</code>
     */
    public void calculateGrossFromNet(TaxRate taxRate) {
    	if (taxRate == null || taxRate.getRate() == null) {
    		setTax(null);
    		setGross(net);
    	} 
    	
    	if (net == null) {
    		setNet(BigDecimal.ZERO);
    		setGross(BigDecimal.ZERO);
    		setTax(null);
    	} else {
    		setTax(net.multiply(taxRate.getRate()));
    		setGross(net.add(tax));
    	}
    }
    
    /**
     * Calculates the net value of this price from a pre-existing gross value and the supplied {@link TaxRate}.
     * 
     * <p>Calculation logic is as follows:
     * <ol>
     * 	<li>
     * 		if the supplied {@link TaxRate} (or {@link TaxRate#getRate()}) is <code>null</code>, this Price's net is 
     * 		set to pre-existing gross value 		
     * 	</li>
     * 	<li>
     * 		if {@link #getGross} is <code>null</code>, both the net and gross values are set to {@link BigDecimal#ZERO}
     * 		and the tax value is set to <code>null</code> 
     * 	</li>
     * 	<li>
     * 		if neither this Price's gross value nor the supplied tax rate is <code>null</code>, the <b>net</b> value is
     * 		calculated as <pre>{@link #getGross} / (1 + {@link TaxRate#getRate()})</pre> and the tax amount as
     * 		<pre>{@link #getGross} - {@link #getNet()}</pre>
     *	</li>
     * </ol>
     * </p>
     * 
     * <p>Note that the division used during calculation of the net value uses a math context with a precision of 34
     * and {@link RoundingMode#HALF_UP}, after which a scale of 2 is applied (with the same rounding mode).</p>
     * 
     * @param taxRate the tax rate to apply to this price, may be <code>null</code>
     * 
     * @see RoundingMode#HALF_UP
     * @see MathContext
     * @see BigDecimal#setScale(int, RoundingMode)
     * @see BigDecimal#divide(BigDecimal, MathContext)
     */
    public void calculateNetFromGross(TaxRate taxRate) {
    	if (taxRate == null || taxRate.getRate() == null) {
    		setTax(null);
    		setNet(gross);
    	} 
    	
    	if (gross == null) {
    		setNet(BigDecimal.ZERO);
    		setGross(BigDecimal.ZERO);
    		setTax(null);
    	} else {
    		// Net = Gross / (1 + taxRate)
    		setNet(gross.divide(BigDecimal.ONE.add(taxRate.getRate()), MATH_CONTEXT).setScale(SCALE, MATH_CONTEXT.getRoundingMode()));
    		setTax(gross.subtract(net));
    	}
    }
    
	/**
     * Compares this Price's gross value to the supplied one's.
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     * @see java.math.BigDecimal#compareTo(BigDecimal)
     */
    @Override
    public int compareTo(Price o) {
	    return gross.compareTo(o.gross);
    }

	/**
	 * @param listener
	 * @see java.beans.PropertyChangeSupport#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * @param listener
	 * @see java.beans.PropertyChangeSupport#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/**
	 * @param propertyName
	 * @param listener
	 * @see java.beans.PropertyChangeSupport#addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * @param propertyName
	 * @param listener
	 * @see java.beans.PropertyChangeSupport#removePropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}
	
	/**
	 * 
	 * {@inheritDoc}
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getClass().getName());
		sb.append(FIELD_NET).append(" [").append(net.toString()).append("] ");
		if (tax != null) {
			sb.append(FIELD_TAX).append(" [").append(tax.toString()).append("] ");
		}
		sb.append(FIELD_GROSS).append(" [").append(gross.toString()).append("]");
		return sb.toString();
	}
}