
package de.tfsw.accounting.ui.overview;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;

import de.tfsw.accounting.ReportingService;
import de.tfsw.accounting.ui.util.WidgetHelper;

public class OverviewPart {
	
	private static final Logger LOG = LogManager.getLogger(OverviewPart.class);
	
	@Inject
	private ReportingService reportingService;
		
	@PostConstruct
	public void postConstruct(Composite parent) {
		LOG.debug("Building overview");
		
		parent.setLayout(new GridLayout(2, false));
		WidgetHelper.grabBoth(parent);
		
		Composite bordered = new Composite(parent, SWT.BORDER | SWT.SHADOW_OUT);
		bordered.setLayout(new RowLayout());
		WidgetHelper.grabBoth(bordered);
		Label label = new Label(bordered, SWT.NONE);
		label.setText("Wuppdi, oida!");
		
		Button exportButton = new Button(bordered, SWT.PUSH);
		exportButton.setText("Export...");
		exportButton.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> {
			FileDialog fd = new FileDialog(parent.getShell(), SWT.SAVE);
			fd.setText("Save report to...");
			fd.setFileName("outputtsen.pdf");
			final String target = fd.open();
			if (target != null) {
				LOG.debug("Saving exported report to {}", target);
				reportingService.test(target);
			}
		}));
		
		Composite bordered2 = new Composite(parent, SWT.BORDER);
		bordered2.setLayout(new RowLayout());
		WidgetHelper.grabBoth(bordered2);
		Label label2 = new Label(bordered2, SWT.BORDER);
		label2.setText("Another blabla");
	}

	@PreDestroy
	public void preDestroy() {
//		toolkit.dispose();
	}

	@Focus
	public void onFocus() {
//		form.setFocus();
	}

}