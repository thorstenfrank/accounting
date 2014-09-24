/*
 *  Copyright 2011 , 2014 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.ui.invoice;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import de.tfsw.accounting.model.Invoice;
import de.tfsw.accounting.ui.AccountingUI;
import de.tfsw.accounting.ui.Messages;

/**
 * @author thorsten
 *
 */
public class InvoiceEditorInput implements IEditorInput {

	private Invoice invoice;
	
	/**
	 * 
	 * @param invoice
	 */
	public InvoiceEditorInput(Invoice invoice) {
		this.invoice = invoice;
	}
	
	/**
	 * @return the invoice
	 */
	protected Invoice getInvoice() {
		return invoice;
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.IEditorInput#exists()
	 */
	@Override
	public boolean exists() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
	 */
	@Override
	public ImageDescriptor getImageDescriptor() {
		return AccountingUI.getImageDescriptor(Messages.iconsInvoiceEdit);
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.IEditorInput#getName()
	 */
	@Override
	public String getName() {
		return invoice.getNumber();
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.IEditorInput#getPersistable()
	 */
	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 */
	@Override
	public String getToolTipText() {
		return Messages.InvoiceEditor_header;
	}

	/**
	 * {@inheritDoc}
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof InvoiceEditorInput) {
			return invoice.equals(((InvoiceEditorInput) obj).getInvoice());
		}
		return super.equals(obj);
	}

}
