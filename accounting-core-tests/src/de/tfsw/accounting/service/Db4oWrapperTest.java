package de.tfsw.accounting.service;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.db4o.ObjectContainer;
import com.db4o.config.BigMathSupport;
import com.db4o.config.Configuration;
import com.db4o.config.ObjectClass;
import com.db4o.config.ObjectField;
import com.db4o.constraints.UniqueFieldValueConstraint;
import com.db4o.ext.DatabaseFileLockedException;
import com.db4o.ext.IncompatibleFileFormatException;
import com.db4o.internal.encoding.UTF8StringEncoding;
import com.db4o.osgi.Db4oService;

import de.tfsw.accounting.AccountingException;
import de.tfsw.accounting.model.Client;
import de.tfsw.accounting.model.CurriculumVitae;
import de.tfsw.accounting.model.ExpenseTemplate;
import de.tfsw.accounting.model.Invoice;
import de.tfsw.accounting.model.User;

public class Db4oWrapperTest {

	private static final String MOCK_DB_FILE_LOCATION = "dummy-file-name-for-db";
	private Db4oWrapper wrapper;
	
	private Db4oService db4oServiceMock;
	private ObjectContainer ocMock;
	
	// init mocks
	private Configuration configurationMock;
	private ObjectClass userClassMock;
	private ObjectField userNameFieldMock;
	private ObjectClass clientClassMock;
	private ObjectField clientNameMock;
	private ObjectField clientNumberMock;
	private ObjectClass invoiceClassMock;
	private ObjectField invoicePositionsMock;
	private ObjectField invoiceNumberMock;
	private ObjectClass cvClassMock;
	private ObjectClass expenseTemplateClassMock;
	
	@Test
	public void testInitSuccessFul() {
		setUpInitMocks(null);
		verifyInitMocks();
	}
	
	@Test(expected = AccountingException.class)
	public void testDatabaseFileLockException() {
		runTestWithExceptionDuringInit(new DatabaseFileLockedException("JUnit locked test"));
	}

	@Test(expected = AccountingException.class)
	public void testIncompatibleFileFormatException() {
		runTestWithExceptionDuringInit(new IncompatibleFileFormatException());
	}
	
	@Test(expected = AccountingException.class)
	public void testGeneralException() {
		runTestWithExceptionDuringInit(new IllegalArgumentException(""));
	}
	
	private void runTestWithExceptionDuringInit(Throwable t) {
		try {
			setUpInitMocks(t);
		} finally {
			verifyInitMocks();
		}		
	}
	
	private void setUpInitMocks(Throwable throwDuringOpeningFile) {
		db4oServiceMock = mock(Db4oService.class);
		ocMock = mock(ObjectContainer.class);
		
		configurationMock = mock(Configuration.class);
		when(db4oServiceMock.newConfiguration()).thenReturn(configurationMock);
		
		userClassMock = mock(ObjectClass.class);
		when(configurationMock.objectClass(User.class)).thenReturn(userClassMock);
		
		userNameFieldMock = mock(ObjectField.class);
		when(userClassMock.objectField(User.FIELD_NAME)).thenReturn(userNameFieldMock);
		
		clientClassMock = mock(ObjectClass.class);
		when(configurationMock.objectClass(Client.class)).thenReturn(clientClassMock);
		
		clientNameMock = mock(ObjectField.class);
		when(clientClassMock.objectField(Client.FIELD_NAME)).thenReturn(clientNameMock);
	
		clientNumberMock = mock(ObjectField.class);
		when(clientClassMock.objectField(Client.FIELD_CLIENT_NUMBER)).thenReturn(clientNumberMock);
		
		invoiceClassMock = mock(ObjectClass.class);
		when(configurationMock.objectClass(Invoice.class)).thenReturn(invoiceClassMock);
		
		invoicePositionsMock = mock(ObjectField.class);
		when(invoiceClassMock.objectField(Invoice.FIELD_INVOICE_POSITIONS)).thenReturn(invoicePositionsMock);
		invoiceNumberMock = mock(ObjectField.class);
		when(invoiceClassMock.objectField(Invoice.FIELD_NUMBER)).thenReturn(invoiceNumberMock);
		
		cvClassMock = mock(ObjectClass.class);
		when(configurationMock.objectClass(CurriculumVitae.class)).thenReturn(cvClassMock);
		
		expenseTemplateClassMock = mock(ObjectClass.class);
		when(configurationMock.objectClass(ExpenseTemplate.class)).thenReturn(expenseTemplateClassMock);
		
		if (throwDuringOpeningFile == null) {
			when(db4oServiceMock.openFile(configurationMock, MOCK_DB_FILE_LOCATION)).thenReturn(ocMock);
		} else {
			when(db4oServiceMock.openFile(configurationMock, MOCK_DB_FILE_LOCATION)).thenThrow(throwDuringOpeningFile);
		}
		
		
		wrapper = new Db4oWrapper(db4oServiceMock);
		wrapper.init(MOCK_DB_FILE_LOCATION);
	}
	
	private void verifyInitMocks() {
		verify(configurationMock).stringEncoding(any(UTF8StringEncoding.class));
		verify(configurationMock).add(isA(BigMathSupport.class));
		verify(configurationMock).allowVersionUpdates(true);
		
		verify(configurationMock).objectClass(User.class);
		verify(userClassMock).cascadeOnUpdate(true);
		verify(userClassMock).cascadeOnDelete(true);
		verify(userClassMock).objectField(User.FIELD_NAME);
		
		verify(userNameFieldMock).indexed(true);
		
		verify(configurationMock).objectClass(Client.class);
		verify(clientClassMock).cascadeOnUpdate(true);
		verify(clientClassMock).cascadeOnDelete(true);
		verify(clientClassMock).objectField(Client.FIELD_NAME);
		verify(clientClassMock).objectField(Client.FIELD_CLIENT_NUMBER);
		
		verify(clientNameMock).indexed(true);
		
		verify(clientNumberMock).indexed(true);
		
		verify(configurationMock).objectClass(Invoice.class);
		verify(invoiceClassMock).objectField(Invoice.FIELD_INVOICE_POSITIONS);
		verify(invoiceClassMock).objectField(Invoice.FIELD_NUMBER);
		
		verify(invoicePositionsMock).cascadeOnUpdate(true);
		verify(invoicePositionsMock).cascadeOnDelete(true);
		
		verify(invoiceNumberMock).indexed(true);
		
		verify(configurationMock).objectClass(CurriculumVitae.class);
		verify(cvClassMock).cascadeOnUpdate(true);
		verify(cvClassMock).cascadeOnDelete(true);
		
		verify(configurationMock).objectClass(ExpenseTemplate.class);
		verify(expenseTemplateClassMock).cascadeOnUpdate(true);
		verify(expenseTemplateClassMock).cascadeOnDelete(true);
		
		verify(configurationMock, times(4)).add(isA(UniqueFieldValueConstraint.class));
		
		verify(db4oServiceMock).openFile(configurationMock, MOCK_DB_FILE_LOCATION);
		
		verifyNoMoreInteractions(configurationMock, 
				userClassMock, userNameFieldMock, clientClassMock, clientNameMock, clientNumberMock, invoiceClassMock,
				invoicePositionsMock, invoiceNumberMock, cvClassMock, expenseTemplateClassMock);
	}
}
