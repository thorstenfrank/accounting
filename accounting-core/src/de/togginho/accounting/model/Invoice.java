/*
 *  Copyright 2010 thorsten frank (thorsten.frank@gmx.de).
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
import java.util.Date;
import java.util.List;

/**
 * An bill issued to a {@link Client} for services delivered.
 * 
 * @author thorsten frank
 */
public class Invoice implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -531042077805244303L;
	
	public static final String FIELD_NUMBER = "number";
	public static final String FIELD_STATE = "state";
	public static final String FIELD_CREATION_DATE = "creationDate";
	public static final String FIELD_INVOICE_DATE = "invoiceDate";
	public static final String FIELD_DUE_DATE = "dueDate";
	public static final String FIELD_SENT_DATE = "sentDate";
	public static final String FIELD_PAYMENT_DATE = "paymentDate";
	public static final String FIELD_USER = "user";
	public static final String FIELD_CLIENT = "client";
	public static final String FIELD_INVOICE_POSITIONS = "invoicePositions";
	
	private String number;
	private Date creationDate;
	private Date invoiceDate;
	private Date dueDate;
	private Date sentDate;
	private Date paymentDate;
	private Date cancelledDate;
	private User user;
	private Client client;
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
		} else if (sentDate != null && dueDate.before(new Date())) {
			return InvoiceState.OVERDUE;
		} else if (sentDate != null) {
			return InvoiceState.SENT;
		} else {
			return InvoiceState.CREATED;
		}
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
		return dueDate;
	}

	/**
	 * @param dueDate the dueDate to set
	 */
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
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
