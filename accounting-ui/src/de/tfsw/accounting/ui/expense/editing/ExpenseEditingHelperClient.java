/*
 *  Copyright 2012, 2015 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.ui.expense.editing;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.tfsw.accounting.model.Expense;

/**
 * A callback interface for all clients using the {@link ExpenseEditHelper}.
 * 
 * <p>
 * This interface provides for necessary customization of UI widgets as well as for custom handling in case of changes
 * to the model by the client implementation.</p>
 * 
 * @author Thorsten Frank
 *
 */
public interface ExpenseEditingHelperClient {

	/**
	 * Called whenever the model data has changed - it is up to the implementation how to deal with these changes.
	 * This method is only called if actual changes have been made, i.e. the currently edited {@link Expense} is 
	 * actually in a different state than before, so clients do not have to check whether the entity is "dirty" (i.e.
	 * in need of saving) or not.
	 */
	void modelHasChanged();
	
	/**
	 * Called when a label with the supplied text needs to be created. Implementations are expected to supply a 
	 * {@link Label} that is "ready to go", i.e. no further action is taken by the caller to change either the look,
	 * layout or behavior of the label.
	 * 
	 * @param  parent	the parent composite for the label to be created
	 * @param  text		the label text, should be used as-is, without any further i18n
	 * @return the created label
	 */
	Label createLabel(Composite parent, String text);
	
	/**
	 * Called when a {@link Text} element needs to be created. The {@link ExpenseEditHelper} may or may not apply
	 * further layout changes.
	 * 
	 * @param  parent	the parent composite to be used
	 * @param  style		the style bits requested
	 * @return the {@link Text} element
	 */
	Text createText(Composite parent, int style);
}
