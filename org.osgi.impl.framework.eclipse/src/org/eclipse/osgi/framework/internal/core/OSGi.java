/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.osgi.framework.internal.core;

import org.eclipse.osgi.framework.adaptor.FrameworkAdaptor;

/**
 * Main class for the OSGi framework. This class is used to start the framework for production use.
 * Objects of this class represent an instance of the OSGi framework and
 * can be used to control the framework.
 */
public class OSGi {
	protected Framework framework;

	/**
	 * Constructs an OSGi object with the specified FrameworkAdaptor.
	 * This method creates an OSGi framework.
	 *
	 * @param adaptor An adaptor object for the framework to use.
	 */
	public OSGi(FrameworkAdaptor adaptor) {
		framework = createFramework(adaptor);
	}

	/**
	 * Destroy the OSGi framework. This method stops the framework if
	 * it has been started. All resources associated with the framework
	 * are release and the OSGi object is no longer usable.
	 *
	 */
	public void close() {
		framework.close();
	}

	/**
	 * Start the framework.
	 *
	 * The framework is started as described in the OSGi Framework
	 * specification.
	 */
	public void launch() {
		framework.launch();
	}

	/**
	 * Stop the framework.
	 *
	 * The framework is stopped as described in the OSGi Framework
	 * specification.
	 */
	public void shutdown() {
		framework.shutdown();
	}

	/**
	 * This method returns the state of the OSGi framework.
	 *
	 * @return true of the framework is launched, false if shutdown.
	 */
	public boolean isActive() {
		return (framework.isActive());
	}

	/**
	 * Retrieve the BundleContext for the system bundle.
	 *
	 * @return The system bundle's BundleContext.
	 */
	public org.osgi.framework.BundleContext getBundleContext() {
		return (framework.systemBundle.getContext());
	}

	/**
	 * Create the internal framework object.
	 * This method can be overridden to create a secure framework.
	 *
	 * @param adaptor FrameworkAdaptor object for the framework.
	 * @return New Framework object.
	 */
	protected Framework createFramework(FrameworkAdaptor adaptor) {
		return (new Framework(adaptor));
	}

	/**
	 * Display the banner to System.out.
	 *
	 */
	protected void displayBanner() {
		System.out.println();
		System.out.print(Msg.ECLIPSE_OSGI_NAME); //$NON-NLS-1$
		System.out.print(" "); //$NON-NLS-1$
		System.out.println(Msg.ECLIPSE_OSGI_VERSION); //$NON-NLS-1$
		System.out.println();
		System.out.println(Msg.OSGI_VERSION); //$NON-NLS-1$
		System.out.println();
		System.out.println(Msg.ECLIPSE_COPYRIGHT); //$NON-NLS-1$
	}
}
