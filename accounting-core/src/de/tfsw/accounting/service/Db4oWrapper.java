/**
 * 
 */
package de.tfsw.accounting.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.db4o.ObjectContainer;
import com.db4o.config.BigMathSupport;
import com.db4o.config.Configuration;
import com.db4o.config.ObjectClass;
import com.db4o.config.ObjectField;
import com.db4o.config.encoding.StringEncodings;
import com.db4o.constraints.UniqueFieldValueConstraint;
import com.db4o.constraints.UniqueFieldValueConstraintViolationException;
import com.db4o.ext.DatabaseClosedException;
import com.db4o.ext.DatabaseFileLockedException;
import com.db4o.ext.DatabaseReadOnlyException;
import com.db4o.ext.Db4oException;
import com.db4o.ext.Db4oIOException;
import com.db4o.ext.IncompatibleFileFormatException;
import com.db4o.osgi.Db4oService;
import com.db4o.query.Predicate;

import de.tfsw.accounting.AccountingException;
import de.tfsw.accounting.Messages;
import de.tfsw.accounting.model.AbstractBaseEntity;
import de.tfsw.accounting.model.Client;
import de.tfsw.accounting.model.CurriculumVitae;
import de.tfsw.accounting.model.ExpenseTemplate;
import de.tfsw.accounting.model.Invoice;
import de.tfsw.accounting.model.User;

/**
 * @author tfrank1
 *
 */
final class Db4oWrapper implements Persistence {

	private static final Logger LOG = LogManager.getLogger(Db4oWrapper.class);
	
	private Db4oService db4oService;
	private ObjectContainer db4o;
	
	/**
	 * @param db4oService
	 */
	Db4oWrapper(Db4oService db4oService) {
		super();
		this.db4oService = db4oService;
	}
	
	/**
	 * 
	 * @param dbFileName
	 */
	@Override
	public void init(String dbFileName) {
		try {
			db4o = db4oService.openFile(createConfiguration(), dbFileName);
		} catch (DatabaseFileLockedException e) {
			LOG.error(String.format(
					"DB file [%s] is locked by another process - application is probably already running", //$NON-NLS-1$
					dbFileName), e); 
			
			throw new AccountingException(
			        Messages.bind(Messages.AccountingService_errorFileLocked, dbFileName), e);
		} catch (IncompatibleFileFormatException e) {
			LOG.error(String.format("File [%s] is not a valid data file format!", dbFileName), e); //$NON-NLS-1$
			throw new AccountingException(
					Messages.bind(Messages.AccountingService_errorIllegalFileFormat, dbFileName), e);
		} catch (Exception e) {
			LOG.error("Error setting up DB " + dbFileName, e); //$NON-NLS-1$
			throw new AccountingException(Messages.AccountingService_errorServiceInit, e);
		}
	}
	
	/**
	 * 
	 */
	@Override
	public void shutDown() {
		try {
			LOG.info("Closing object container."); //$NON-NLS-1$
			db4o.close();
		} catch (Db4oIOException e) {
			LOG.warn("Error closing DB file, will ignore", e); //$NON-NLS-1$
		} 
	}
	
	@Override
	public void storeEntity(final AbstractBaseEntity entity) {
		storeEntity(entity, true);
	}
	
	private void storeEntity(final AbstractBaseEntity entity, final boolean commit) {
		runDb4oOperation(db -> {
			db.store(entity);
			if (commit) {
				db.commit();
			}
		});
	}
	
	@Override
	public void storeEntities(Collection<? extends AbstractBaseEntity> entities) {
		runDb4oOperation(db -> {
			entities.forEach( e -> storeEntity(e, false));
			db4o.commit();			
		});
	}
	
	@Override
	public void deleteEntity(AbstractBaseEntity entity) {
		runDb4oOperation(db -> {
			db.delete(entity);
			db.commit();
		});
	}
	
	/**
	 * 
	 * @param targetType
	 * @return
	 */
	@Override
	public <T> Set<T> runFindQuery(Class<T> targetType) {
		return runDb4oOperation((ObjectContainer db) -> new HashSet<>(db.query(targetType)));
	}
	
	@Override
	public <T> Set<T> runFindQuery(Predicate<T> predicate) {
		return runDb4oOperation((ObjectContainer db) -> new HashSet<>(db.query(predicate)));
	}
	
	private <R> R runDb4oOperation(Function<ObjectContainer, R> function) {
		R returnValue = null;
		
		try {
			returnValue = function.apply(db4o);
		} catch (DatabaseClosedException e) {
			throwDbClosedException(e);
		} catch(Db4oIOException e) {
			throwDb4oIoException(e);
		}
		
		return returnValue;
	}
	
	private void runDb4oOperation(Consumer<ObjectContainer> consumer) {
		try {
			consumer.accept(db4o);
		} catch (DatabaseClosedException e) {
			throwDbClosedException(e);
		} catch (DatabaseReadOnlyException e) {
			throwDbReadOnlyException(e);
		} catch(Db4oIOException e) {
			throwDb4oIoException(e);
		} catch(UniqueFieldValueConstraintViolationException e) {
			db4o.rollback();
			throw e;
		} catch (Db4oException e) {
			LOG.error("Error writing to db4o DB", e); //$NON-NLS-1$
			db4o.rollback();
			throw new AccountingException("Unknown exception: " + e.toString(), e);
		}		
	}
	
	/**
	 * 
	 * @return
	 */
	private Configuration createConfiguration() {
		Configuration config = db4oService.newConfiguration();
		
		// make sure to use UTF-8
		config.stringEncoding(StringEncodings.utf8());
		
		// must ensure support for big decimal
		config.add(new BigMathSupport());
		
		// allow version upgrades...
		config.allowVersionUpdates(true);

		// config for User object graph cascade
		ObjectClass userClass = config.objectClass(User.class);
		userClass.cascadeOnUpdate(true);
		userClass.cascadeOnDelete(true);
		userClass.objectField(User.FIELD_NAME).indexed(true);
		config.add(new UniqueFieldValueConstraint(User.class, User.FIELD_NAME));

		// config for Client object graph cascade
		ObjectClass clientClass = config.objectClass(Client.class);
		clientClass.cascadeOnUpdate(true);
		clientClass.cascadeOnDelete(true);
		clientClass.objectField(Client.FIELD_NAME).indexed(true);
		clientClass.objectField(Client.FIELD_CLIENT_NUMBER).indexed(true);
		config.add(new UniqueFieldValueConstraint(Client.class, Client.FIELD_NAME));
		config.add(new UniqueFieldValueConstraint(Client.class, Client.FIELD_CLIENT_NUMBER));

		// config for Invoice object graph
		ObjectClass invoiceClass = config.objectClass(Invoice.class);
		ObjectField invoicePositionsField = invoiceClass.objectField(Invoice.FIELD_INVOICE_POSITIONS);
		invoicePositionsField.cascadeOnDelete(true);
		invoicePositionsField.cascadeOnUpdate(true);
		invoiceClass.objectField(Invoice.FIELD_NUMBER).indexed(true);
		config.add(new UniqueFieldValueConstraint(Invoice.class, Invoice.FIELD_NUMBER));

		ObjectClass cvClass = config.objectClass(CurriculumVitae.class);
		cvClass.cascadeOnUpdate(true);
		cvClass.cascadeOnDelete(true);
		
		ObjectClass expenseTemplateClass = config.objectClass(ExpenseTemplate.class);
		expenseTemplateClass.cascadeOnUpdate(true);
		expenseTemplateClass.cascadeOnDelete(true);
		
		return config;
	}
	
	/**
	 * 
	 * @param e
	 * @param rollback
	 */
	private void throwDbClosedException(DatabaseClosedException e) {
		LOG.error("Database is closed!", e); //$NON-NLS-1$
		throw new AccountingException(Messages.AccountingService_errorDatabaseClosed, e);
	}
	
	/**
	 * 
	 * @param e
	 */
	private void throwDb4oIoException(Db4oIOException e) {
		LOG.error("I/O error during DB operation", e); //$NON-NLS-1$		
		throw new AccountingException(Messages.AccountingService_errorIO, e);
	}
	
	/**
	 * 
	 * @param e
	 */
	private void throwDbReadOnlyException(DatabaseReadOnlyException e) {
		LOG.error("Database is read-only!", e);
		throw new AccountingException(Messages.AccountingService_errorDatabaseReadOnly, e);
	}
}
