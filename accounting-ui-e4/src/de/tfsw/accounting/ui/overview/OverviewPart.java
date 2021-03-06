
package de.tfsw.accounting.ui.overview;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.tfsw.accounting.ui.util.WidgetHelper;

public class OverviewPart {
	
	private static final Logger LOG = LogManager.getLogger(OverviewPart.class);
	
//	@Inject
//	private AccountingInitService initService;
	
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
		
		Composite bordered2 = new Composite(parent, SWT.BORDER);
		bordered2.setLayout(new RowLayout());
		WidgetHelper.grabBoth(bordered2);
		Label label2 = new Label(bordered2, SWT.BORDER);
		label2.setText("Another blabla");
		
//		toolkit = new FormToolkit(parent.getDisplay());
//		
//		form = toolkit.createScrolledForm(parent);
//		toolkit.decorateFormHeading(form.getForm());
//				
//		form.getBody().setLayout(new GridLayout(2, true));
//		form.setText("Form overall blabla");
//		form.setMessage("Some Form Message, Lorem Ipsum trallallaaa.", IMessageProvider.NONE);
//		
//		GridDataFactory grabBoth = GridDataFactory.fillDefaults().grab(true, false);
//		
//		Section s1 = toolkit.createSection(form.getBody(), Section.DESCRIPTION | Section.TITLE_BAR);
//		Composite s1c = new Composite(s1, SWT.BORDER);
//		s1c.setLayout(new GridLayout(2, false));
//		s1.setDescription("Section description");
//		s1.setText("Section Text");
//		s1.setClient(s1c);
//		grabBoth.applyTo(s1);
//		toolkit.createLabel(s1c, "Some Label Text");
//		toolkit.createButton(s1c, "Push me, dude!", SWT.PUSH);
//		toolkit.createLabel(s1c, "Another label with more content than the first");
//		toolkit.createButton(s1c, "On or Off", SWT.CHECK);
//		//toolkit.paintBordersFor(s1c);
//		
//		ExpandableComposite ec = toolkit.createExpandableComposite(form.getBody(), Section.TWISTIE);
//		ec.setText("Expandable Composite Text");
//		Composite ecc = new Composite(ec, SWT.BORDER);
//		ecc.setLayout(new GridLayout());
//		ec.setClient(ecc);
//		grabBoth.applyTo(ec);
//		
//		Section s2 = toolkit.createSection(form.getBody(), Section.TWISTIE | Section.TITLE_BAR | Section.DESCRIPTION);
//		Composite s2c = new Composite(s2, SWT.NONE);
//		s2c.setLayout(new GridLayout());
//		s2.setText("Twistie Section Text");
//		s2.setDescription("Elaborate blabla for this section with many, many words. Many words. A lot of them, believe me!");
//		s2.setClient(s2c);
//		grabBoth.applyTo(s2);
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