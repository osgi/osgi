package org.osgi.impl.service.useradmin;

/*
 * $Header$
 *
 * OSGi UserAdmin Reference Implementation.
 *
 * OSGi Confidential.
 *
 * (c) Copyright Gatespace AB 2000-2001.
 *
 * This source code is owned by Gatespace AB (www.gatespace.com), and is 
 * being distributed to OSGi MEMBERS as MEMBER LICENSED MATERIALS under
 * the terms of section 3.2 of the OSGi MEMBER AGREEMENT.
 *
 */
import java.util.Hashtable;
import org.osgi.framework.*;
import org.osgi.service.useradmin.UserAdmin;

public class Activator implements BundleActivator, ServiceFactory {
	protected BundleContext			bc;
	protected ServiceRegistration	uasr	= null;
	protected UserAdminImpl			ua;
	protected LogTracker			log;

	/*
	 * BundleActivator implementation.
	 */
	public void start(BundleContext bc) throws BundleException {
		try {
			this.bc = bc;
			log = new LogTracker(bc);
			ua = new UserAdminImpl(this);
			uasr = bc.registerService(UserAdmin.class.getName(), this,
					new Hashtable());
		}
		catch (Exception e) {
			log.error("Error starting UserAdmin service", e);
			throw new BundleException("Couldn't start UserAdmin service", e);
		}
	}

	public void stop(BundleContext bc) throws BundleException {
		// Kill the UserAdmin. Needed to invalidate objects handed out.
		ua.die();
	}

	/*
	 * ServiceFactory implementation.
	 */
	public Object getService(Bundle b, ServiceRegistration sr) {
		uasr = sr;
		return ua;
	}

	public void ungetService(Bundle b, ServiceRegistration sr, Object service) {
		// Ignore
	}
}
