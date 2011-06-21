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
package de.togginho.accounting.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Simple helper class containing often used widget creation code.
 * 
 * @author tfrank1
 */
public class WidgetHelper {
	
	/**
	 * Private since this is only a utility class.
	 */
	private WidgetHelper() {
		
	}
		
	/**
	 * Creates a new label with the specified parent and text.
	 * 
	 * @param parent parent
	 * @param text text to display, may be <code>null</code>
	 * @return the finished label
	 */
	public static Label createLabel(Composite parent, String text) {
		Label label = new Label(parent, SWT.NULL);
		if (text != null) {
			label.setText(text);	
		}
		return label;
	}
		
	/**
	 * 
	 * @param parent
	 * @param text
	 * @return
	 */
	public static Text createSingleBorderText(Composite parent, String text) {
		Text theText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		if (text != null) {
			theText.setText(text);
		}
		return theText;
	}
}
