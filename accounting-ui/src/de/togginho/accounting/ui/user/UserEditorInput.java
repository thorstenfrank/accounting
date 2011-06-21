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
package de.togginho.accounting.ui.user;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import de.togginho.accounting.model.User;
import de.togginho.accounting.ui.AccountingUI;
import de.togginho.accounting.ui.Messages;

/**
 * @author tfrank1
 *
 */
class UserEditorInput implements IEditorInput {

	/**
	 * 
	 */
	private User user;
	
	/**
	 * 
	 * @param user
	 */
	protected UserEditorInput(User user) {
		if (user == null) {
			this.user = new User();
		} else {
			this.user = user;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	protected User getUser() {
		return user;
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
		return AccountingUI.getImageDescriptor(Messages.iconsUserEdit);
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.ui.IEditorInput#getName()
	 */
	@Override
	public String getName() {
		return user.getName();
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
		return Messages.UserEditor_header;
	}

	/**
	 * If the supplied object is of this class, then this method returns the result of comparing the two input's
	 * {@link User} objects.
	 * This is meant to prevent multiple
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof UserEditorInput) {
			return user.equals(((UserEditorInput)obj).user);
		}
		return super.equals(obj);
	}
	
	
}