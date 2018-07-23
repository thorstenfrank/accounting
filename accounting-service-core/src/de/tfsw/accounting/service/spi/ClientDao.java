/**
 * 
 */
package de.tfsw.accounting.service.spi;

import java.util.Set;

import de.tfsw.accounting.model.Client;

/**
 * @author thorsten
 *
 */
public interface ClientDao {

	Set<Client> getClients();
}
