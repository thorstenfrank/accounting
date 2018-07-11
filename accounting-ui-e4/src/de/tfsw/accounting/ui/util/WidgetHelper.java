/*
 *  Copyright 2018 Thorsten Frank (accounting@tfsw.de).
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
package de.tfsw.accounting.ui.util;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

/**
 * UI helper methods.
 * 
 * @author thorsten
 */
public final class WidgetHelper {

	private static final Logger LOG = LogManager.getLogger(WidgetHelper.class);
	
	private static final GridDataFactory GRAB_HORIZONTAL = GridDataFactory.fillDefaults().grab(true, false);
	
	private static final GridDataFactory GRAB_BOTH = GridDataFactory.fillDefaults().grab(true, true);
	
	/**
	 * Attempts to retrieve an {@link Image} from a file within this bundle.
	 * 
	 * Examples:
	 * <pre>
	 * createImageFromFile("root_of_bundle.png");
	 * createImageFromFile("icons/abouticon.gif");
	 * </pre>
	 * 
	 * @param relativePath must not start with a slash ("/") character
	 * @return the {@link Image} or <code>null</code> if no image can be retrieved
	 */
	public static Image createImageFromFile(final String relativePath) {
		Image result = null;
		try {
			result = ImageDescriptor
					.createFromURL(new URL("platform:/plugin/de.tfsw.accounting.ui/" + relativePath))
					.createImage();
		} catch (MalformedURLException e) {
			LOG.warn("Problems getting image", e);
		}
		return result;
	}
	
	/**
	 * Creates a new label with the specified parent and text.
	 * 
	 * @param parent
	 *            parent
	 * @param text
	 *            text to display, may be <code>null</code>
	 * @return the {@link Label}
	 */
	public static Label createLabel(Composite parent, String text) {
		Label label = new Label(parent, SWT.NULL);
		if (text != null) {
			label.setText(text);
		}
		return label;
	}

	/**
	 * Creates a simple text field with styles {@link SWT#SINGLE} and
	 * {@link SWT#BORDER}. Will grab horizontally in a grid layout.
	 * 
	 * @param parent parent composite
	 * @param text text for the widget, may be <code>null</code>
	 * @return the {@link Text}
	 */
	public static Text createSingleBorderText(Composite parent, String text) {
		Text theText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		if (text != null) {
			theText.setText(text);
		}
		grabHorizontal(theText);
		
		return theText;
	}

	/**
	 * The same as <code>GridDataFactory.fillDefaults().grab(true, false).applyTo(control)</code>.
	 * 
	 * @param control the {@link Control} to apply the grid layout data to
	 */
	public static void grabHorizontal(Control control) {
		GRAB_HORIZONTAL.applyTo(control);
	}
	
	/**
	 * The same as <code>GridDataFactory.fillDefaults().grab(true, true).applyTo(control)</code>.
	 * 
	 * @param control the {@link Control} to apply the grid layout data to
	 */
	public static void grabBoth(Control control) {
		GRAB_BOTH.applyTo(control);
	}
	
	/**
	 * 
	 * @param widget
	 * @param style
	 */
	public static void applyStyle(final Widget widget, final CssStyleClass style) {
		widget.setData("org.eclipse.e4.ui.css.CssClassName", style.name());
	}
	
	/** */
	private WidgetHelper() {
		// ...
	}
}
