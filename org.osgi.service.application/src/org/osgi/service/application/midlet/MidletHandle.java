/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2004, 2005). All Rights Reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this 
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.
 */

package org.osgi.service.application.midlet;

import org.osgi.service.application.ApplicationHandle;

/**
 * This service represents a Midlet instance. It is a specialization of the
 * application handle and provides features specific to the Midlet model.
 */
public final class MidletHandle extends ApplicationHandle {

	protected MidletHandle(String instanceId, MidletDescriptor descriptor) {
		super(instanceId, descriptor );

		this.descriptor = descriptor;
		try {
			delegate = (Delegate) implementation.newInstance();
			delegate.setMidletHandle( this, descriptor, descriptor.delegate );
		} catch( Throwable t ) {
			// Too bad ...
			System.err
					.println("No implementation available for MidletHandle, property is: "
							+ cName);
		}
	}

	/**
	 * The Midlet instance is suspended.
	 */
	public final static String SUSPENDED = "SUSPENDED";

	/**
	 * Returns the state of the Midlet instance specific to the Midlet model.
	 * 
	 * @throws IllegalStateException
	 *             if the Midlet handle is unregistered
	 * 
	 * @return the state of the Midlet instance
	 */
	public String getState() {
		return delegate.getState();
	}

	/**
	 * Destroys a Midlet according to the Midlet model. It calls the associated
	 * Midlet instance's stop() method with null parameter.
	 *  
	 */
	protected void destroySpecific() throws Exception {
		delegate.destroySpecific();
	}

	/**
	 * Suspends the Melet instance. It calls the associated Midlet instance's
	 * stop() method and passes a non-null output stream as a parameter. It must
	 * preserve the contents of the output stream written by the Midlet instance
	 * even across device restarts. The same content must be provided to a
	 * resuming Midlet instance.
	 * 
	 * @throws SecurityException
	 *             if the caller doesn't have "manipulate"
	 *             ApplicationAdminPermission for the corresponding application.
	 * @throws IllegalStateException
	 *             if the Midlet handle is unregistered
	 */
	public void suspend() throws Exception {
		delegate.suspend();
	}

	/**
	 * Resumes the Midlet instance. It calls the associated Midlet instance's
	 * start() method and passes a non-null input stream as a parameter. It must
	 * have the same contents that was saved by the suspending Midlet instance.
	 * The same startup arguments must also be passed to the resuming Midlet
	 * instance that was passed to the first starting instance.
	 * 
	 * @throws SecurityException
	 *             if the caller doesn't have "manipulate"
	 *             ApplicationAdminPermission for the corresponding application.
	 * @throws IllegalStateException
	 *             if the Midlet handle is unregistered
	 */
	public void resume() throws Exception {
		delegate.resume();
	}

	MidletDescriptor		descriptor;
	Delegate						delegate;
	
	static Class				implementation;
	static String				cName;
	{
		try {
			cName = System
					.getProperty("org.osgi.vendor.application.midlet.MidletHandle");
			implementation = Class.forName(cName);
		}
		catch (Throwable t) {
			// Ignore
		}
	}
	
	public interface Delegate {
		void setMidletHandle( MidletHandle handle, MidletDescriptor megDesc, MidletDescriptor.Delegate delegate );
		String getState();
		void destroySpecific() throws Exception;
		void suspend() throws Exception;
		void resume() throws Exception;
	}
}
