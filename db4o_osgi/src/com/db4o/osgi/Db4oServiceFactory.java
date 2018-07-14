/* Copyright (C) 2007  Versant Inc.  http://www.db4o.com */

package com.db4o.osgi;

import org.osgi.framework.*;

/**
 * I added some generics... just to make the warnings shut up.
 */
class Db4oServiceFactory implements ServiceFactory<Db4oService> {

	public Db4oService getService(Bundle bundle, ServiceRegistration<Db4oService> registration) {
		return new Db4oServiceImpl(bundle);
	}

	public void ungetService(Bundle bundle, ServiceRegistration<Db4oService> registration, Db4oService service) {
		// this method doesn't do anything
	}

}
