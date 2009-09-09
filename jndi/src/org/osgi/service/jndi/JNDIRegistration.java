package org.osgi.service.jndi;

/**
 * Represents a push operation on the JNDI service. This registration can be
 * removed by closing it. This effectively removes it from the stack, even if it
 * is not on the top.
 * 
 * @ThreadSafe
 */
public interface JNDIRegistration {
	/**
	 * Close this registration. After this call, the associated Initial Context
	 * Factory is no longer associated with the thread. This method can be
	 * called on any Thread.
	 */
	void close();
}
