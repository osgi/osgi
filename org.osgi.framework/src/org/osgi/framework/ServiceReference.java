/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2000, 2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.framework;

import java.util.Dictionary;

/**
 * A reference to a service.
 * 
 * <p>
 * The Framework returns <code>ServiceReference</code> objects from the
 * <code>BundleContext.getServiceReference</code> and
 * <code>BundleContext.getServiceReferences</code> methods.
 * <p>
 * A <code>ServiceReference</code> may be shared between bundles and can be used
 * to examine the properties of the service and to get the service object.
 * <p>
 * Every service registered in the Framework has a unique
 * <code>ServiceRegistration</code> object and may have multiple, distinct
 * <code>ServiceReference</code> objects referring to it.
 * <code>ServiceReference</code> objects associated with a
 * <code>ServiceRegistration</code> object have the same <code>hashCode</code> and
 * are considered equal (more specifically, their <code>equals()</code> method
 * will return <code>true</code> when compared).
 * <p>
 * If the same service object is registered multiple times,
 * <code>ServiceReference</code> objects associated with different
 * <code>ServiceRegistration</code> objects are not equal.
 * 
 * @version $Revision$
 * @see BundleContext#getServiceReference
 * @see BundleContext#getServiceReferences
 * @see BundleContext#getService
 */

public abstract interface ServiceReference {
	/**
	 * Returns the property value to which the specified property key is mapped
	 * in the properties <code>Dictionary</code> object of the service referenced
	 * by this <code>ServiceReference</code> object.
	 * 
	 * <p>
	 * Property keys are case-insensitive.
	 * 
	 * <p>
	 * This method must continue to return property values after the service has
	 * been unregistered. This is so references to unregistered services (for
	 * example, <code>ServiceReference</code> objects stored in the log) can still
	 * be interrogated.
	 * 
	 * @param key The property key.
	 * @return The property value to which the key is mapped; <code>null</code> if
	 *         there is no property named after the key.
	 */
	public abstract Object getProperty(String key);

	/**
	 * Returns an array of the keys in the properties <code>Dictionary</code>
	 * object of the service referenced by this <code>ServiceReference</code>
	 * object.
	 * 
	 * <p>
	 * This method will continue to return the keys after the service has been
	 * unregistered. This is so references to unregistered services (for
	 * example, <code>ServiceReference</code> objects stored in the log) can still
	 * be interrogated.
	 * 
	 * <p>
	 * This method is <i>case-preserving </i>; this means that every key in the
	 * returned array must have the same case as the corresponding key in the
	 * properties <code>Dictionary</code> that was passed to the
	 * {@link BundleContext#registerService(String[],Object,Dictionary)}or
	 * {@link ServiceRegistration#setProperties}methods.
	 * 
	 * @return An array of property keys.
	 */
	public abstract String[] getPropertyKeys();

	/**
	 * Returns the bundle that registered the service referenced by this
	 * <code>ServiceReference</code> object.
	 * 
	 * <p>
	 * This method will always return <code>null</code> when the service has been
	 * unregistered. This can be used to determine if the service has been
	 * unregistered.
	 * 
	 * @return The bundle that registered the service referenced by this
	 *         <code>ServiceReference</code> object; <code>null</code> if that
	 *         service has already been unregistered.
	 * @see BundleContext#registerService(String[],Object,Dictionary)
	 */
	public abstract Bundle getBundle();

	/**
	 * Returns the bundles that are using the service referenced by this
	 * <code>ServiceReference</code> object. Specifically, this method returns the
	 * bundles whose usage count for that service is greater than zero.
	 * 
	 * @return An array of bundles whose usage count for the service referenced
	 *         by this <code>ServiceReference</code> object is greater than zero;
	 *         <code>null</code> if no bundles are currently using that service.
	 * 
	 * @since 1.1
	 */
	public abstract Bundle[] getUsingBundles();

	/**
	 * Returns true if the bundle which registered the service referenced by this
	 * <tt>ServiceReference</tt> and the specified bundle use the same source for
	 * the package of the specified class name.
	 * <p>
	 * This method performs the following checks: <br>
	 * 1)  Get the package name from the className <br>
	 * 2)  For the bundle that registered the service referenced by this
	 * <tt>ServiceReference</tt> object (registrant bundle); find the source for the package.  
	 * If no source is found then return true if the registrant bundle is equal to 
	 * the specified bundle; otherwise return false; <br>
	 * 3)  If the package source of the registrant bundle is equal to the package source of the 
	 * specified bundle then return true; otherwise return false. <br>
	 * @param bundle the <tt>Bundle</tt> object to check the package source for
	 * @param className the class name to check the package source for
	 * @return true if the bundle which registered the service referenced by this
	 * <tt>ServiceReference</tt> and the specified bundle use the same source for
	 * the package of the specified class name; otherwise false is returned.
	 */
	public abstract boolean isAssignableTo(Bundle bundle, String className); 

}

