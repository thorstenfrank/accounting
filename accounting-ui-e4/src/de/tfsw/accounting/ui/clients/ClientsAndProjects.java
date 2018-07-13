package de.tfsw.accounting.ui.clients;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import de.tfsw.accounting.ClientService;
import de.tfsw.accounting.EventIds;
import de.tfsw.accounting.model.Client;
import de.tfsw.accounting.ui.AbstractEditorOpeningView;

public class ClientsAndProjects extends AbstractEditorOpeningView {
	
	private static final Logger LOG = LogManager.getLogger(ClientsAndProjects.class);
	
	@Inject
	private ClientService clientService;
	
	@Inject
	private IEventBroker eventBroker;
	
	private TreeViewer viewer;
	
	@PostConstruct
	public void createComposite(Composite parent) {
		parent.setLayout(new GridLayout());
		
		//clientService.getClientNames().forEach(System.out::println);
		
		viewer = new TreeViewer(parent);
		viewer.setContentProvider(new MyContentProvider());
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Client) {
					return ((Client) element).getName();
				}
				return super.getText(element);
			}
			
		});
		
		viewer.setInput(clientService.getClients());
		viewer.addDoubleClickListener(event -> {
			if (event.getSelection() instanceof TreeSelection) {
				TreeSelection ts = (TreeSelection) event.getSelection();
				LOG.trace("DoubleClick from tree: {}", ts);
				Object element = ts.getFirstElement();
				if (element instanceof Client) {
					final Client client = (Client) element;
					LOG.trace("Opening client editor for: {}", client.getName());
					getAppModelHelper().openExistingOrCreateNewEditor(ClientEditor.PART_ID, client.getName(), client);
				} else {
					LOG.warn("Unkown type: {}", element.getClass().getName());
				}
			} else {
				LOG.warn("DoubleClick is not a TreeSelection: {}", event.getSelection());
			}
		});
		
		eventBroker.subscribe(EventIds.modelChangeTopicFor(Client.class), e -> {
			viewer.setInput(clientService.getClients());
			viewer.refresh();
		});
	}
	
	@Focus
	public void focusGained() {
		viewer.getTree().setFocus();
	}
	
	static class MyContentProvider implements ITreeContentProvider {
		
		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Object[]) {
				LOG.debug("getElement for array of {}", inputElement);
				return (Object[]) inputElement;
			} else if (inputElement instanceof Collection<?>) {
				LOG.debug("getElement for collection of {}", inputElement);
				return ((Collection<?>) inputElement).toArray();
			} else {
				LOG.debug("getElement for type {}", inputElement.getClass().getSimpleName());
			}
			return new Object[0];
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			LOG.debug("getChildren for {}", parentElement);
//			if (parentElement instanceof TestClient) {
//				return ((TestClient) parentElement).projects.toArray();
//			}
			return null;
		}

		@Override
		public Object getParent(Object element) {
//			if (element instanceof TestProject) {
//				return ((TestProject) element).client;
//			}
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
//			if (element instanceof TestClient) {
//				TestClient client = (TestClient) element;
//				return client.projects != null && !client.projects.isEmpty();
//			}
			return false;
		}
		
	}
}
