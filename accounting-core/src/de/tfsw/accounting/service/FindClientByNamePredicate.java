package de.tfsw.accounting.service;

import com.db4o.query.Predicate;

import de.tfsw.accounting.model.Client;

class FindClientByNamePredicate extends Predicate<Client> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;

	FindClientByNamePredicate(String name) {
		this.name = name;
	}
	
	@Override
	public boolean match(Client candidate) {
		return name.equals(candidate.getName());
	}
	
}
