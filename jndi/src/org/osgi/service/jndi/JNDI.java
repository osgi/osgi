package org.osgi.service.jndi;

import javax.naming.spi.*;

/**
 * Provides a service based interface to the javax.naming.
 * 
 * This service can be used to associate a thread with an Initial Context
 * Factory. in traditional Java, this can only be done with a VM global
 * singleton. This service allows a container to setup an Initial Context
 * Factory for a specific thread so that when its embedded client uses new
 * InitialContext(), the container can define what the actual context can be.
 * 
 * The model is stack based. That is, it is possible to push and pop
 * registrations, just to prevent yet another thread based singleton.
 * 
 * @ThreadSafe
 */
public interface JNDI {
	/**
	 * Push a new Initial Context Factory associated with the current thread.
	 * 
	 * The last pushed Initial Context Factory will be used to create a new
	 * Initial Context. For each push, the top Initial Context Factory should
	 * also be removed by closing the returned registration.
	 * 
	 * @param icf The Initial Context Factory to be associated with this thread
	 * @return The registration that can be used to pop
	 * @ThreadSafe
	 */
	JNDIRegistration pushInitialContextFactory(InitialContextFactory icf);
}
