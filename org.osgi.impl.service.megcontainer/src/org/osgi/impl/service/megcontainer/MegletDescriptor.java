package org.osgi.impl.service.megcontainer;

import java.util.*;
import java.util.Map;

import org.osgi.framework.*;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.application.ApplicationDescriptor;

/**
 * Specialization of the application descriptor. Represents a Meglet and
 * provides generic methods inherited from the application descriptor. It is a
 * service.
 */
public final class MegletDescriptor extends ApplicationDescriptor {

	/**
	 * @param bc
	 * @param props
	 * @param names
	 * @param icons
	 * @param defaultLanguage
	 * @param startClass
	 * @param bundle
	 * @param impl
	 */
	public MegletDescriptor(BundleContext bc, Properties props, Hashtable names, Hashtable icons, String defaultLanguage, String startClass, Bundle bundle, MegletContainerImpl impl) {
		// TODO Auto-generated constructor stub
		
		
	}

	/**
	 * Returns the unique identifier of the represented Meglet.
	 * 
	 * @return the unique identifier of the represented Meglet
	 * 
	 * @throws IllegalStateException
	 *             if the Meglet descriptor is unregistered
	 */
	public String getPID() {
		return null;
	}

	/**
	 * Called by launch() to create and start a new instance in an application
	 * model specific way. It also creates and registeres the application handle
	 * to represent the newly created and started instance.
	 * 
	 * @param arguments
	 *            the startup parameters of the new application instance, may be
	 *            null
	 * 
	 * @return the service reference to the application model specific
	 *         application handle for the newly created and started instance.
	 * 
	 * @throws Exception
	 *             if any problem occures.
	 */
	protected ServiceReference launchSpecific(Map arguments) throws Exception {
		return null;
	}

	/**
	 * Retrieves the bundle context of the container to which the specialization
	 * of the application descriptor belongs
	 * 
	 * @return the bundle context of the container
	 * 
	 * @throws IllegalStateException
	 *             if the Meglet descriptor is unregistered
	 */
	protected BundleContext getBundleContext() {
		return null;
	}

	/**
	 * @return
	 */
	public long getBundleId() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @return
	 */
	public String getStartClass() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return
	 */
	public boolean isSingleton() {
		// TODO Auto-generated method stub
		return false;
	}

}