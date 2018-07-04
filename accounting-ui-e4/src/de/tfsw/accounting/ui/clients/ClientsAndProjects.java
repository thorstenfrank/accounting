package de.tfsw.accounting.ui.clients;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import de.tfsw.accounting.ui.AbstractEditorOpeningView;

public class ClientsAndProjects extends AbstractEditorOpeningView {
	
	private static final Logger LOG = LogManager.getLogger(ClientsAndProjects.class);
	
	private TreeViewer viewer;
	
	@PostConstruct
	public void createComposite(Composite parent) {
		parent.setLayout(new GridLayout());
		
		List<TestClient> clients = new ArrayList<>();
		TestClient tc1 = new TestClient();
		tc1.name = "TC1";
		tc1.projects = new ArrayList<>();
		tc1.projects.add(new TestProject("TC1_Proj1", tc1));
		tc1.projects.add(new TestProject("TC1_Proj2", tc1));
		tc1.projects.add(new TestProject("TC1_Proj3", tc1));
		clients.add(tc1);
		
		TestClient tc2 = new TestClient();
		tc2.name = "TC2";
		clients.add(tc2);

		TestClient tc3 = new TestClient();
		tc3.name = "TC3";
		tc3.projects = new ArrayList<>();
		tc3.projects.add(new TestProject("TC3_Proj1", tc3));
		tc3.projects.add(new TestProject("TC3_Proj2", tc3));
		clients.add(tc3);
		
		viewer = new TreeViewer(parent);
		viewer.setContentProvider(new MyContentProvider());
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setLabelProvider(new LabelProvider());
		viewer.setInput(clients);
		viewer.addDoubleClickListener(event -> {
			if (event.getSelection() instanceof TreeSelection) {
				TreeSelection ts = (TreeSelection) event.getSelection();
				log.debug("DoubleClick from tree: {}", ts);
				
				if (ts.getFirstElement() instanceof TestClient) {
					String name = ((TestClient) ts.getFirstElement()).name;
					log.debug("Opening client editor for: {}", name);
					openExistingOrCreateNew(name);
				} else if (ts.getFirstElement() instanceof TestProject) {
					log.debug("Project: {}", ((TestProject) ts.getFirstElement()).name);
				} else {
					log.debug("Unkown type: {}", ts.getFirstElement().getClass().getName());
				}
				
				
			} else {
				log.debug("DoubleClick: {}", event.getSelection());
			}
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
			if (parentElement instanceof TestClient) {
				return ((TestClient) parentElement).projects.toArray();
			}
			return null;
		}

		@Override
		public Object getParent(Object element) {
			if (element instanceof TestProject) {
				return ((TestProject) element).client;
			}
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			if (element instanceof TestClient) {
				TestClient client = (TestClient) element;
				return client.projects != null && !client.projects.isEmpty();
			}
			return false;
		}
		
	}
	
	static class TestClient {
		private String name;
		private List<TestProject> projects;
		
		@Override
		public String toString() {
			return name;
		}
	}
	
	static class TestProject {
		private String name;
		private TestClient client;
		TestProject(String name, TestClient client) {
			super();
			this.name = name;
			this.client = client;
		}
		@Override
		public String toString() {
			return name;
		}
	}
}
