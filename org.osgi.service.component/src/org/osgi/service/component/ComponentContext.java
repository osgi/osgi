/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and OSGi Alliance DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
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
 * A ComponentContext object is used by a Service Component to interact with it
 * execution context including locating services by reference name.
 * 
 * <p>
 * A component's implementation class may optional implement an activate method:
 * 
 * <pre>
 * protected void activate(ComponentContext context);
 * </pre>
 * 
 * If a component implements this method, this method will be called when the
 * component is activated to provide the component's ComponentContext object.
 * 
 * <p>
 * A component's implementation class may optional implement a deactivate
 * method:
 * 
 * <pre>
 * protected void deactivate(ComponentContext context);
 * </pre>
 * 
 * If a component implements this method, this method will be called when the
 * component is deactivated.
 * 
 * <p>
 * The activate and deactivate methods will be called using reflection and must
 * be at least protected accessible. These methods do not need to be public
 * methods so that they do not appear as public methods on the component's
 * provided service object. The methods will be located by looking through the
 * component's implementation class hierarchy for the first declaration of the
 * method. If the method is declared protected or public, the method will
 * called.
 * 
 * @version $Revision$
 */
public interface ComponentContext {
	/**
	 * Returns the component properties for this ComponentContext.
	 * 
	 * @return The properties for this ComponentContext. The properties are read
	 *         only and cannot be modified.
	 */
	public Dictionary getProperties();

	/**
	 * Returns the service object for the specified service reference name.
	 * 
	 * @param name The name of a service reference as specified in a
	 *        <code>reference</code> element in this component's description.
	 * @return A service object for the referenced service or <code>null</code> if
	 *         the reference cardinality is <code>0..1</code> or <code>0..n</code>
	 *         and no matching service is available.
	 * @throws ComponentException If the Service Component Runtime catches an
	 *         exception while activating the target service.
	 */
	public Object locateService(String name);

	/**
	 * Returns the service objects for the specified service reference name.
	 * 
	 * @param name The name of a service reference as specified in a
	 *        <code>reference</code> element in this component's description.
	 * @return An array of service objects for the referenced service or
	 *         <code>null</code> if the reference cardinality is <code>0..1</code>
	 *         or <code>0..n</code> and no matching service is available.
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
	 * <code>servicefactory=&quot;true&quot;</code> attribute, then this method
	 * returns the bundle using the service provided by this component.
	 * <p>
	 * This method will return <code>null</code> if the component is either:
	 * <ul>
	 * <li>Not a service, then no bundle can be using it as a service.
	 * <li>Is a service but did not specify the
	 * <code>servicefactory=&quot;true&quot;</code> attribute, then all bundles
	 * will use this component.
	 * </ul>
	 * 
	 * @return The bundle using this component as a service or <code>null</code>.
	 */
	public Bundle getUsingBundle();

	/**
	 * Returns the ComponentInstance object for this component.
	 * 
	 * @return The ComponentInstance object for this component.
	 */
	public ComponentInstance getComponentInstance();

	/**
	 * Enables the specified component name. The specified component name must
	 * be in the same bundle as this component.
	 * 
	 * @param name The name of a component or <code>null</code> to indicate all
	 *        components in the bundle.
	 */
	public void enableComponent(String name);

	/**
	 * Disables the specified component name. The specified component name must
	 * be in the same bundle as this component.
	 * 
	 * @param name The name of a component.
	 */
	public void disableComponent(String name);
}