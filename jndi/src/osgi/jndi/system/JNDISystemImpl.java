package osgi.jndi.system;

import java.io.*;
import java.util.*;

import javax.naming.*;
import javax.naming.spi.*;

import org.osgi.service.jndi.*;

/**
 * Represents the JNDI service.
 * 
 * This JNDI implementation sets itself as the InitialContextFactoryBuilder
 * (which is a singleton) and then allows containers to register themselves as
 * the InitialContextFactory for a specific thread.
 * 
 * A container can register itself at the top of the stack. If a client calls
 * new InitialContext(env), the JNDI service will be notified through the
 * InitialContextFactoryBuilder it registered with the naming manager. The
 * implementation will lookup the top registration associated with the current
 * thread.
 * 
 * This is a service factory, it separates the registrations for each bundle
 * using it.
 * 
 * This implementation maintains a list of registrations that are closed when
 * the service is ungotten.
 */
public class JNDISystemImpl implements JNDI {
	final static ThreadLocal<List<JNDIRegistration>>	threadRegistrations	= new ThreadLocal<List<JNDIRegistration>>();
	
	final List<Registration>							registrations		= new ArrayList<Registration>();

	/**
	 * Models the registration of a thread based InitialContextFactory. Each
	 * call to push will result in a registration. The close method will pop
	 * this registration.
	 */
	class Registration implements JNDIRegistration, Closeable {
		InitialContextFactory	factory;

		/**
		 * Remove any traces.
		 */
		public void close() {
			synchronized (threadRegistrations) {
				registrations.remove(this);
				factory = null;
			}
		}
	}

	/**
	 * Set the initial context factory at the top of the stack.
	 */
	public Registration pushInitialContextFactory(InitialContextFactory icf) {
		Registration r = new Registration();
		r.factory = icf;

		synchronized (threadRegistrations) {
			// Add to per bundle list
			registrations.add(r);

			// Check if we have a list associated with the current
			// thread.
			List<JNDIRegistration> rs = threadRegistrations.get();
			if (rs == null) {
				// We need a new list, first time usage.
				rs = new ArrayList<JNDIRegistration>();
				threadRegistrations.set(rs);
			}
			rs.add(r);
		}
		return r;
	}

	/**
	 * Close all registrations for this bundle.
	 */
	protected void deactivate() {
		synchronized (threadRegistrations) {
			for (Registration r : registrations)
				r.close();
		}
	}

	/**
	 * Set the infamous singleton ...
	 */
	static {
		try {
			NamingManager
					.setInitialContextFactoryBuilder(new InitialContextFactoryBuilder() {

						public InitialContextFactory createInitialContextFactory(
								Hashtable< ? , ? > environment)
								throws NamingException {
							List<JNDIRegistration> rs = threadRegistrations
									.get();
							if (rs == null || rs.isEmpty())
								throw new NoInitialContextException(
										"No thread associated with an InitialContextFactory");

							Registration r = (Registration) rs.get(0);
							while (r.factory == null && !rs.isEmpty())
								r = (Registration) rs.remove(0);

							if (r == null || r.factory==null)
								throw new NoInitialContextException(
										"Cannot find an appropriate InitialContextFactory");

							return r.factory;
						}
					});
		}
		catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
