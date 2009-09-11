/*
 * Copyright (c) OSGi Alliance (2009). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.jmx.framework;

import java.io.IOException;

import javax.management.openmbean.TabularData;

/**
 * <p>
 * This MBean represents the Service state of the framework. This MBean also
 * emits events that clients can use to get notified of the changes in the
 * service state of the framework.
 * <p>
 * See <link>OSGiBundleEvent</link> for the precise definition of the
 * CompositeData for the notification sent.
 */
public interface ServiceStateMBean {

	/**
	 * Answer the list of interfaces that this service implements
	 * 
	 * @param serviceId
	 *            - the identifier of the service
	 * @return the list of interfaces
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the service indicated does not exist
	 */
	public String[] getServiceInterfaces(long serviceId) throws IOException;

	/**
	 * Answer the bundle identifier of the bundle which registered the service
	 * 
	 * @param serviceId
	 *            - the identifier of the service
	 * @return the identifier for the bundle
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the service indicated does not exist
	 */
	long getBundle(long serviceId) throws IOException;

	/**
	 * Answer the map of credentials associated with this service
	 * <p>
	 * 
	 * @see org.osgi.jmx.codec.OSGiProperties for the details of the TabularType
	 *      <p>
	 *      For each propery entry, the following row is returned
	 *      <ul>
	 *      <li>Property Key - the string key</li>
	 *      <li>Property Value - the stringified version of the property value</li>
	 *      <li>Property Value Type - the type of the property value</li>
	 *      </ul>
	 * 
	 * @param serviceId
	 *            - the identifier of the service
	 * @return the table of credentials. These include the standard mandatory
	 *         service.id and objectClass credentials as defined in the
	 *         <code>org.osgi.framework.Constants</code> interface
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the service indicated does not exist
	 */
	TabularData getProperties(long serviceId) throws IOException;

	/**
	 * Answer the service state of the system in tabular form
	 * <p>
	 * 
	 * @see org.osgi.jmx.codec.OSGiService for the details of the TabularType
	 *      <p>
	 *      Each row of the returned table represents a single service. For each
	 *      service, the following row is returned
	 *      <ul>
	 *      <li>identifier - long</li>
	 *      <li>interfaces - String[]</li>
	 *      <li>bundle - long</li>
	 *      <li>using bundles - long[]</li>
	 *      </ul>
	 *      <p>
	 *      See <link>OSGiService</link> for the precise definition of the
	 *      CompositeType that defines each row of the table.
	 * 
	 * @return the tabular respresentation of the service state
	 */
	TabularData getServices();

	/**
	 * Answer the list of identifers of the bundles that use the service
	 * 
	 * @param serviceId
	 *            - the identifier of the service
	 * @return the list of bundle identifiers
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the service indicated does not exist
	 */
	long[] getUsingBundles(long serviceId) throws IOException;

	/**
	 * The name of the item containing the bundle identifier in the
	 * CompositeData
	 */
	String BUNDLE_IDENTIFIER = "BundleIdentifier";

	/**
	 * The name of the item containing the bundle location in the CompositeData
	 */
	String BUNDLE_LOCATION = "BundleLocation";

	/**
	 * The name of the item containing the event type in the CompositeData
	 */
	String EVENT_TYPE = "Type";

	/**
	 * The name of the item containing the interfaces of the service in the
	 * CompositeData
	 */
	String OBJECT_CLASS = "objectClass";

	/**
	 * The type of the JMX event raised in response to <link>ServiceEvent</link>
	 * in the underlying OSGi container
	 */
	String SERVICE_EVENT_TYPE = "org.osgi.jmx.serviceEvent";

	/**
	 * The name of the item containing the service identifier in the
	 * CompositeData
	 */
	String SERVICE_ID = "Identifier";

	/**
	 * The name of the item containing the bundles using the service in the
	 * CompositeData
	 */
	String USING_BUNDLES = "UsingBundles";
}
