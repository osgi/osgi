/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.service.application.meglet;

import java.util.Map;

import org.osgi.service.application.ApplicationDescriptor;
import org.osgi.service.application.ApplicationHandle;

/**
 * Specialization of the application descriptor. Represents a Meglet and
 * provides generic methods inherited from the application descriptor. It is a
 * service.
 */
public final class MegletDescriptor extends ApplicationDescriptor {

	/**
	 * Called by launch() to create and start a new instance in an application
	 * model specific way. It also creates and registeres the application handle
	 * to represent the newly created and started instance.
	 * 
	 * @param arguments
	 *            the startup parameters of the new application instance, may be
	 *            null
	 * 
	 * @return <code>MegletHandle</code> for the newly created and started instance.
	 * 
	 * @throws Exception
	 *             if any problem occures.
	 */
	protected ApplicationHandle launchSpecific(Map arguments) throws Exception {
		return delegate.launchSpecific( arguments );
	}

	protected Map getPropertiesSpecific(String locale) {
		return delegate.getPropertiesSpecific( locale );
	}

	protected void lockSpecific() {
		delegate.lockSpecific();
	}

	protected void unlockSpecific() {
		delegate.unlockSpecific();
	}
	
	protected  MegletDescriptor(String pid) {
		super( pid );

		try {
			delegate = (Delegate) implementation
					.newInstance();
			delegate.setMegletDescriptor( this );
		}
		catch (Exception e) {
			// Too bad ...
			e.printStackTrace();
			System.err
					.println("No implementation available for ApplicationDescriptor, property is: "
							+ cName);
		}
	}

	Delegate	delegate;
	String							pid;

	static Class					implementation;
	static String					cName;

	{
		try {
			cName = System
					.getProperty("org.osgi.vendor.application.meglet.MegletDescriptor");
			implementation = Class.forName(cName);
		}
		catch (Throwable t) {
			// Ignore
		}
	}

	public interface Delegate {
		void setMegletDescriptor( MegletDescriptor descriptor );
		ApplicationHandle launchSpecific(Map arguments) throws Exception;
		Map getPropertiesSpecific(String locale);
		void lockSpecific();
		void unlockSpecific();
	}
}
