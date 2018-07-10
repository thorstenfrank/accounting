package de.tfsw.accounting.ui.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import de.tfsw.accounting.ui.util.WidgetHelper;

/**
 * Doesn't really do anything yet, just shows a dialog.
 *
 */
public class AboutHandler {
	@Execute
	public void execute(Shell shell) {
//		MessageDialog.openInformation(shell, "About", "tfsw.de accounting");
		
		TitleAreaDialog dialog = new TitleAreaDialog(shell) {
			
			@Override
			public void create() {
				super.create();
				setTitle("About Title");
				setTitleImage(WidgetHelper.createImageFromFile("aboutimage.png"));
				setMessage("About Message");//\nAbout Message\nAbout Message");
			}
			
			@Override
			protected Control createDialogArea(Composite parent) {
				final Composite composite = (Composite) super.createDialogArea(parent);
				final GridLayout layout = new GridLayout();
				layout.marginTop = 10;
				layout.marginLeft = 10;
				layout.marginRight = 10;
				layout.marginBottom = 10;
				
				composite.setLayout(layout);
				
				return composite;
			}
			
			@Override
			protected void createButtonsForButtonBar(Composite parent) {
				createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
			}
		};
		
		
		dialog.open();
	}
}
