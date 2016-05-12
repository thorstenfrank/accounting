/*
 *  Copyright 2014 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.ui.user;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

import de.tfsw.accounting.ui.AbstractAccountingEditor;
import de.tfsw.accounting.ui.Messages;

/**
 * @author Thorsten Frank
 *
 * @since 1.2
 */
public class MultiPageUserEditor extends MultiPageEditorPart {

	private static final Logger LOG = LogManager.getLogger(MultiPageUserEditor.class);
	
	private UserEditor userEditor;
	private CVEditor cvEditor;
		
	/**
	 * @see org.eclipse.ui.part.MultiPageEditorPart#createPages()
	 */
	@Override
	protected void createPages() {
		userEditor = new UserEditor();
		cvEditor = new CVEditor();
		
		try {
			addPage(0, userEditor, getEditorInput());
			addPage(1, cvEditor, getEditorInput());
			
			setPageText(0, Messages.MultiPageUserEditor_pageUser);
			setPageText(1, Messages.MultiPageUserEditor_pageProfile);
		} catch (PartInitException e) {
			LOG.error("Error creating editor pages", e);
		}
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#getEditorInput()
	 */
	@Override
	public UserEditorInput getEditorInput() {
		return (UserEditorInput) super.getEditorInput();
	}
	
	/**
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		LOG.debug("doSave");
		
		doSaveEditorPage(userEditor, monitor);
		doSaveEditorPage(cvEditor, monitor);
		
		if (userEditor.isDirty() || cvEditor.isDirty()) {
			LOG.warn("One of the editors is still marked dirty!");
		} else {
			firePropertyChange(PROP_DIRTY);
		}
	}
	
	/**
	 * 
	 * @param editorPage
	 */
	private void doSaveEditorPage(AbstractAccountingEditor editorPage, IProgressMonitor monitor) {
		LOG.debug("Saving page " + editorPage.getTitle());
		if (editorPage.isDirty()) {
			editorPage.doSave(monitor);
		} else {
			LOG.debug("Page is not dirty, skipping"); //$NON-NLS-1$
		}
	}
	
	/**
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		// nothing to do here
		LOG.warn("doSaveAs() was called but is not allowed!"); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}
}
