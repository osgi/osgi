/*
 *
 * Copyright (c) IBM Corporation (2005)
 *
 * These materials have been contributed  to the OSGi Alliance as 
 * "MEMBER LICENSED MATERIALS" as defined in, and subject to the terms of, 
 * the OSGi Member Agreement, specifically including but not limited to, 
 * the license rights and warranty disclaimers as set forth in Sections 3.2 
 * and 12.1 thereof, and the applicable Statement of Work. 
 *
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.
 */

package org.eclipse.osgi.component;

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * This class provide implementation hooks into the Eclipse OSGi framework
 * implementation. This class must be modified to connect SCR with other
 * framework implementations.
 * 
 * @version $Revision$
 */
public class FrameworkHook extends AbstractReflector {
	/**
	 * Return the BundleContext for the specified bundle.
	 * 
	 * @param bundle The bundle whose BundleContext is desired.
	 * @return The BundleContext for the specified bundle.
	 */
	public BundleContext getBundleContext(final Bundle bundle) {
		if (System.getSecurityManager() == null) {
			return (BundleContext) invokeMethod(bundle, "getContext", null, null);
		}
		return (BundleContext) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				return invokeMethod(bundle, "getContext", null, null);
			}
		});
	}

	/**
	 * Publish a FrameworkEvent with the specified parameters.
	 * 
	 * @param type FrameworkEvent type.
	 * @param bundle Affected bundle.
	 * @param throwable Related exception or null.
	 */
	public void publishFrameworkEvent(final int type, final Bundle bundle, final Throwable throwable) {
		if (System.getSecurityManager() == null) {
			invokeMethod(getFieldValue(bundle, "framework"), "publishFrameworkEvent", publishFrameworkEventParams, new Object[] {new Integer(type), bundle, throwable});
		} else {
			AccessController.doPrivileged(new PrivilegedAction() {
				public Object run() {
					return invokeMethod(getFieldValue(bundle, "framework"), "publishFrameworkEvent", publishFrameworkEventParams, new Object[] {new Integer(type), bundle, throwable});
				}
			});
		}
	}

	/**
	 * Parameters for publishFrameworkEvent method.
	 */
	private static final Class[] publishFrameworkEventParams;
	static {
		publishFrameworkEventParams = new Class[] {Integer.TYPE, Bundle.class, Throwable.class};
	}

	/**
	 * Throws an IllegalStateException if the reflection logic cannot find what
	 * it is looking for. This probably means this class does not properly
	 * recognize the framework implementation.
	 * 
	 * @param e Exception which indicates the reflection logic is confused.
	 */
	protected void reflectionException(Exception e) {
		throw new IllegalStateException("FrameworkHook does not recognize the framework implementation: " + e.getMessage());
	}

}
