package de.tfsw.accounting.service.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import de.tfsw.accounting.EventIds;
import de.tfsw.accounting.model.Client;
import de.tfsw.accounting.service.spi.ClientDao;

public class ClientServiceImplTest {

	@Mock(name = "dao")
	private ClientDao daoMock;
	
	@Mock
	private EventAdmin eventMock;
	
	@InjectMocks
	private ClientServiceImpl service;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@After
	public void tearDown() {
		Mockito.verifyNoMoreInteractions(daoMock, eventMock);
	}
	
	@Test
	public void testNewClient() {
		Client client = service.newClient();
		assertNull(client.getName());
		assertEquals("007", client.getClientNumber());
		assertNull(client.getPrimaryAddress());
	}
	
	@Test
	public void testGetClients() {
		Client c1 = new Client("One");
		Client c2 = new Client("Two");
		when(daoMock.getClients()).thenReturn(new HashSet<>(Arrays.asList(c1, c2)));
		
		Set<Client> clients = service.getClients();
		assertEquals(2, clients.size());
		assertTrue(clients.contains(c1));
		assertTrue(clients.contains(c2));
		
		verify(daoMock).getClients();
	}
	
	@Test
	public void testSaveClient() {
		Client c1 = new Client("One");
		service.saveClient(c1);
		verify(daoMock).save(c1);
		
		ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
		verify(eventMock).postEvent(eventCaptor.capture());
		assertEquals(EventIds.modelChangeTopicFor(Client.class), eventCaptor.getValue().getTopic());
	}
}
