/*
 * $Header$
 *
 * Copyright (c) The OSGi Alliance (2004). All Rights Reserved.
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

package org.osgi.service.component;

import java.util.Dictionary;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * A ComponentContext interface is used by a Service Component to interact with
 * it execution context including locating services by reference name. In order
 * to be notified when a component is activated and to obtain a
 * ComponentContext, the component’s implementation class must implement a
 * 
 * <pre>
 * protected void activate(ComponentContext context);
 * </pre>
 * 
 * method. However, the component is not required to implement this method.
 * <p>
 * In order to be called when the component is deactivated, a component's
 * implementation class must implement a
 * 
 * <pre>
 * protected void deactivate(ComponentContext context);
 * </pre>
 * 
 * method. However, the component is not required to implement this method.
 * <p>
 * These methods will be called by the Service Component Runtime using
 * reflection and may be private methods to avoid being public methods on the
 * component’s provided service object.
 * 
 * @version $Revision$
 */

public interface ComponentContext {
	/**
	 * Returns the component properties for this ComponentContext.
	 * 
	 * @return properties for this ComponentContext. The properties are read
	 *         only and cannot be modified.
	 */
	public Dictionary getProperties();

	/**
	 * Returns the service object for the specified service reference name.
	 * 
	 * @param name The name of a service reference as specified in a
	 *        <tt>reference</tt> element in this component's description.
	 * @return A service object for the referenced service or <tt>null</tt> if
	 *         the reference cardinality is <tt>0..1</tt> or <tt>0..n</tt>
	 *         and no matching service is available.
	 * @throws ComponentException If the Service Component Runtime catches an
	 *         exception while activating the target service.
	 */
	public Object locateService(String name);

	/**
	 * Returns the service objects for the specified service reference name.
	 * 
	 * @param name The name of a service reference as specified in a
	 *        <tt>reference</tt> element in this component's description.
	 * @return An array of service objects for the referenced service or
	 *         <tt>null</tt> if the reference cardinality is <tt>0..1</tt>
	 *         or <tt>0..n</tt> and no matching service is available.
	 * @throws ComponentException If the Service Component Runtime catches an
	 *         exception while activating a target service.
	 */
	public Object[] locateServices(String name);

	/**
	 * Returns the BundleContext of the bundle which contains this component.
	 * 
	 * @return The BundleContext of the bundle containing this component.
	 */
	public BundleContext getBundleContext();

	/**
	 * If the component is registered as a service using the
	 * <tt>servicefactory=&quot;true&quot;</tt> attribute, then this method returns
	 * the bundle using the service provided by this component.
	 * <p>
	 * This method will return <tt>null</tt> if the component is either:
	 * <ul>
	 * <li>Not a service, then no bundle can be using it as a service.
	 * <li>Is a service but did not specify the
	 * <tt>servicefactory=&quot;true&quot;</tt> attribute, then all bundles will use
	 * this component.
	 * </ul>
	 * 
	 * @return The bundle using this component as a service or <tt>null</tt>.
	 */
	public Bundle getUsingBundle();

	/**
	 * Enables the specified component name. The specified component name must
	 * be in the same bundle as this component.
	 * 
	 * @param name of a component or <tt>null</tt> to indicate all components
	 *        in the bundle.
	 */
	public void enableComponent(String name);

	/**
	 * Disables the specified component name. The specified component name must
	 * be in the same bundle as this component.
	 * 
	 * @param name of a component.
	 */
	public void disableComponent(String name);

	/**
	 * Returns the ComponentInstance object for this component.
	 * 
	 * @return The ComponentInstance object for this component.
	 */
	public ComponentInstance getComponentInstance();
}