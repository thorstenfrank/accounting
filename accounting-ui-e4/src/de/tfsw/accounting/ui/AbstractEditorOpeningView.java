package de.tfsw.accounting.ui;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

import de.tfsw.accounting.ui.util.ApplicationModelHelper;

public abstract class AbstractEditorOpeningView {

	private ApplicationModelHelper appModelHelper;
	
	@PostConstruct
	public void buildAppModelHelper(IEclipseContext context) {
		appModelHelper = ContextInjectionFactory.make(ApplicationModelHelper.class, context);
	}

	/**
	 * @return the appModelHelper
	 */
	protected ApplicationModelHelper getAppModelHelper() {
		return appModelHelper;
	}
}
