/*
 * OSGi UserAdmin Reference Implementation.
 *
 * (c) Copyright Gatespace AB 2000-2001.
 *
 * This source code is owned by Gatespace AB (www.gatespace.com), and is 
 * being distributed to OSGi MEMBERS as MEMBER LICENSED MATERIALS under
 * the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 */
package org.osgi.impl.service.useradmin;

import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.useradmin.UserAdmin;

public class Activator implements BundleActivator, ServiceFactory<UserAdmin> {
	protected BundleContext			bc;
	protected ServiceRegistration<UserAdmin>	uasr	= null;
	protected UserAdminImpl			ua;
	protected LogTracker			log;

	/*
	 * BundleActivator implementation.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void start(@SuppressWarnings("hiding") BundleContext bc)
			throws BundleException {
		try {
			this.bc = bc;
			log = new LogTracker(bc);
			ua = new UserAdminImpl(this);
			uasr = (ServiceRegistration<UserAdmin>) bc.registerService(
					UserAdmin.class.getName(), this,
					new Hashtable<>());
		}
		catch (Exception e) {
			log.error("Error starting UserAdmin service", e);
			throw new BundleException("Couldn't start UserAdmin service", e);
		}
	}

	@Override
	public void stop(@SuppressWarnings("hiding") BundleContext bc)
			throws BundleException {
		// Kill the UserAdmin. Needed to invalidate objects handed out.
		ua.die();
	}

	/*
	 * ServiceFactory implementation.
	 */
	@Override
	public UserAdmin getService(Bundle b, ServiceRegistration<UserAdmin> sr) {
		uasr = sr;
		return ua;
	}

	@Override
	public void ungetService(Bundle b, ServiceRegistration<UserAdmin> sr,
			UserAdmin service) {
		// Ignore
	}
}
