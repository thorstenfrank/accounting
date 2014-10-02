/*
 *  Copyright 2010, 2014 Thorsten Frank (accounting@tfsw.de).
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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * An bill issued to a {@link Client} for services delivered.
 * 
 * @author thorsten frank
 * @see    InvoicePosition
 * @see    PaymentTerms
 * @since  1.0
 */
public class Invoice extends AbstractBaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	
	public static final String FIELD_NUMBER = "number";
	public static final String FIELD_STATE = "state";
	public static final String FIELD_CREATION_DATE = "creationDate";
	public static final String FIELD_INVOICE_DATE = "invoiceDate";
	public static final String FIELD_SENT_DATE = "sentDate";
	public static final String FIELD_PAYMENT_DATE = "paymentDate";
	public static final String FIELD_USER = "user";
	public static final String FIELD_CLIENT = "client";
	public static final String FIELD_PAYMENT_TERMS = "paymentTerms";
	public static final String FIELD_INVOICE_POSITIONS = "invoicePositions";
	
	private String number;
	private Date creationDate;
	private Date invoiceDate;
	private Date sentDate;
	private Date paymentDate;
	private Date cancelledDate;
	private User user;
	private Client client;
	private PaymentTerms paymentTerms;
	private List<InvoicePosition> invoicePositions;
	
	/**
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(String number) {
		this.number = number;
	}

	/**
	 * @return the state
	 */
	public InvoiceState getState() {
		if (creationDate == null) {
			return InvoiceState.UNSAVED;
		} else if (cancelledDate != null) {
			return InvoiceState.CANCELLED;
		} else if (paymentDate != null) {
			return InvoiceState.PAID;
		} else if (isOverdue()) {
			return InvoiceState.OVERDUE;
		} else if (sentDate != null) {
			return InvoiceState.SENT;
		} else {
			return InvoiceState.CREATED;
		}
	}

	/**
	 * 
	 * @return
	 */
	private boolean isOverdue() {
		if (sentDate != null) {
			final Date dueDate = getDueDate();
			if (dueDate != null && dueDate.before(new Date())) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * Do not call this method - the creation date is automatically set upon the first save of this instance!
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the invoiceDate
	 */
	public Date getInvoiceDate() {
		return invoiceDate;
	}

	/**
	 * @param invoiceDate the invoiceDate to set
	 */
	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	/**
	 * @return the dueDate
	 */
	public Date getDueDate() {
		Date dueDate = null;
	    if (invoiceDate != null && paymentTerms != null) {
        	Calendar cal = Calendar.getInstance();
        	cal.setTime(invoiceDate);
    		cal.add(Calendar.DAY_OF_MONTH, this.paymentTerms.getFullPaymentTargetInDays());
        	dueDate = cal.getTime();
    	}
		return dueDate;
	}

	/**
	 * @param dueDate the dueDate to set
	 * @deprecated the due date is calculated at runtime by {@link #getDueDate()}, so calling this method has absolutely
	 * no effect. 
	 */
	@Deprecated
	public void setDueDate(Date dueDate) {
		// nothing to do here...
	}

	/**
	 * @return the paymentDate
	 */
	public Date getPaymentDate() {
		return paymentDate;
	}

	/**
	 * @param paymentDate the paymentDate to set
	 */
	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	/**
	 * @return the sentDate
	 */
	public Date getSentDate() {
		return sentDate;
	}

	/**
	 * @param sentDate the sentDate to set
	 */
	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}

	/**
	 * @return the cancelledDate
	 */
	public Date getCancelledDate() {
		return cancelledDate;
	}

	/**
	 * @param cancelledDate the cancelledDate to set
	 */
	public void setCancelledDate(Date cancelledDate) {
		this.cancelledDate = cancelledDate;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the client
	 */
	public Client getClient() {
		return client;
	}

	/**
	 * @param client the client to set
	 */
	public void setClient(Client client) {
		this.client = client;
	}

	/**
     * @return the paymentTerms
     */
    public PaymentTerms getPaymentTerms() {
    	return paymentTerms;
    }

	/**
     * @param paymentTerms the paymentTerms to set
     */
    public void setPaymentTerms(PaymentTerms paymentTerms) {
    	this.paymentTerms = paymentTerms;
    }

	/**
	 * @return the invoicePositions
	 */
	public List<InvoicePosition> getInvoicePositions() {
		return invoicePositions;
	}

	/**
	 * @param invoicePositions the invoicePositions to set
	 */
	public void setInvoicePositions(List<InvoicePosition> invoicePositions) {
		this.invoicePositions = invoicePositions;
	}

	/**
	 * Returns whether this invoice may still be edited, which is only possible while in state 
	 * {@link InvoiceState#UNSAVED} or {@link InvoiceState#CREATED}.
	 * 
	 * @return	<code>true</code> if this invoice is editable, <code>false</code> if not
	 */
	public boolean canBeEdited() {
		switch (getState()) {
		case CREATED:
		case UNSAVED:
			return true;
		default:
			return false;
		}
	}
	
	/**
	 * Returns whether this invoice may be deleted, which is only possible while in state {@link InvoiceState#CREATED}.
	 * 
	 * <p>Once an invoice is {@link InvoiceState#SENT}, it may only be cancelled.</p>
	 * 
	 * @return <code>true</code> if this invoice may be deleted
	 */
	public boolean canBeDeleted() {
		return InvoiceState.CREATED.equals(getState());
	}
	
	/**
	 * Returns whether tihs invoice may be cancelled, which is only possible after it has been sent.
	 * 
	 * <p>More precisely, an invoice may be cancelled if it is in one of the following states:
	 * <ul>
	 * <li>{@link InvoiceState#SENT}</li>
	 * <li>{@link InvoiceState#PAID}</li>
	 * <li>{@link InvoiceState#OVERDUE}</li>
	 * </ul>
	 * </p>
	 * 
	 * @return <code>true</code> if this invoice may be cancelled
	 */
	public boolean canBeCancelled() {
		switch (getState()) {
		case SENT:
		case PAID:
		case OVERDUE:
			return true;
		default:
			return false;
		}
	}
	
	/**
	 * Returns whether this invoice may be marked as paid, which is only possible if in state {@link InvoiceState#SENT}
	 * or {@link InvoiceState#OVERDUE}.
	 * 
	 * @return <code>true</code> if this invoice may be marked as paid
	 */
	public boolean canBePaid() {
		switch (getState()) {
		case SENT:
		case OVERDUE:
			return true;
		default:
			return false;
		}
	}
	
	/**
	 * Returns whether this invoice can be exported.
	 * 
	 * <p>An invoice may be exported an arbitrary number of times, regardless of its state, but it must belong to a user
	 * and it must contain at least a single {@link InvoicePosition}. Additionally, it must be addressed to a 
	 * {@link Client} that has a valid {@link Address}.</p>
	 * 
	 * @return
	 */
	public boolean canBeExported() {
		if (getUser() != null && getClient() != null && getClient().getAddress() != null) {
			return getInvoicePositions() != null && getInvoicePositions().size() > 0;
		}
		return false;
	}
	
	/**
	 * Returns whether this invoice may be sent.
	 * 
	 * @return <code>true</code> if this invoice can be sent
	 */
	public boolean canBeSent() {
		if (InvoiceState.CREATED.equals(getState())) {
			return canBeExported(); // logic is the same - must have user, client and client address
		}
		
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((number == null) ? 0 : number.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
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
		Invoice other = (Invoice) obj;
		if (number == null) {
			if (other.number != null)
				return false;
		} else if (!number.equals(other.number))
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (number != null) {
			return number;
		}
		return super.toString();
	}
}
