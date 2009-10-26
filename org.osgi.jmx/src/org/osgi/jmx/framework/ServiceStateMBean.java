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

import javax.management.openmbean.CompositeType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularType;

import org.osgi.jmx.Item;
import org.osgi.jmx.JmxConstants;

/**
 * This MBean represents the Service state of the framework. This MBean also
 * emits events that clients can use to get notified of the changes in the
 * service state of the framework.
 * 
 */
public interface ServiceStateMBean {
	/**
	 * The fully qualified object name of this mbean.
	 */
	String			OBJECTNAME				= JmxConstants.OSGI_CORE
													+ ":type=serviceState,version=1.5";
	/**
	 * The key BUNDLE_IDENTIFIER, used in {@link #BUNDLE_IDENTIFIER_ITEM}.
	 */
	String			BUNDLE_IDENTIFIER		= "BundleIdentifier";
	/**
	 * The item containing the bundle identifier in {@link #SERVICE_TYPE}. The
	 * key is {@link #BUNDLE_IDENTIFIER} and the type is {@link SimpleType#LONG}
	 * .
	 */
	Item			BUNDLE_IDENTIFIER_ITEM	= new Item(
													BUNDLE_IDENTIFIER,
													"The identifier of the bundle the service belongs to",
													SimpleType.LONG);

	/**
	 * The key OBJECT_CLASS, used {@link #OBJECT_CLASS_ITEM}.
	 */
	String			OBJECT_CLASS			= "objectClass";

	/**
	 * The item containing the interfaces of the service in
	 * {@link #SERVICE_TYPE}. The key is {@link #OBJECT_CLASS} and the type is
	 * {@link JmxConstants#STRING_ARRAY_TYPE}.
	 */
	Item			OBJECT_CLASS_ITEM		= new Item(
													OBJECT_CLASS,
													"An string array containing the interfaces under which the service has been registered",
													JmxConstants.STRING_ARRAY_TYPE);

	/**
	 * The key IDENTIFIER, used {@link #IDENTIFIER_ITEM}.
	 */
	String			IDENTIFIER				= "Identifier";

	/**
	 * The item containing the service identifier in {@link #SERVICE_TYPE}. The
	 * key is {@link #IDENTIFIER} and the type is {@link SimpleType#LONG}.
	 */
	Item			IDENTIFIER_ITEM			= new Item(
													IDENTIFIER,
													"The identifier of the service",
													SimpleType.LONG);

	/**
	 * The key USING_BUNDLES, used in {@link #USING_BUNDLES_ITEM}.
	 */
	String			USING_BUNDLES			= "UsingBundles";

	/**
	 * The item containing the bundles using the service in
	 * {@link #SERVICE_TYPE}. The key is {@link #USING_BUNDLES} and the type is
	 * {@link JmxConstants#LONG_ARRAY_TYPE}.
	 */
	Item			USING_BUNDLES_ITEM		= new Item(
													USING_BUNDLES,
													"The bundles using the service",
													JmxConstants.LONG_ARRAY_TYPE);

	/**
	 * The key PROPERTIES, used in {@link #PROPERTIES_ITEM}.
	 */
	String			PROPERTIES				= "Properties";

	/**
	 * The item containing service properties. The key is {@link #PROPERTIES}
	 * and the type is {@link JmxConstants#PROPERTIES_TYPE}.
	 */
	Item			PROPERTIES_ITEM			= new Item(
													PROPERTIES,
													"The service properties",
													JmxConstants.PROPERTIES_TYPE);

	/**
	 * The item names in the CompositeData representing the service. This type
	 * consists of:
	 * <ul>
	 * <li>{@link #BUNDLE_IDENTIFIER}</li>
	 * <li>{@link #IDENTIFIER}</li>
	 * <li>{@link #OBJECT_CLASS}</li>
	 * <li>{@link #PROPERTIES}</li>
	 * <li>{@link #USING_BUNDLES}</li>
	 * </ul>
	 */
	CompositeType	SERVICE_TYPE			= Item
													.compositeType(
															"SERVICE",
															"This type encapsulates an OSGi service",
															BUNDLE_IDENTIFIER_ITEM,
															IDENTIFIER_ITEM,
															OBJECT_CLASS_ITEM,
															PROPERTIES_ITEM,
															USING_BUNDLES_ITEM);

	/**
	 * The Tabular Type for a Service table. The rows consists of
	 * {@link #SERVICE_TYPE} Composite Data and the index is {@link #IDENTIFIER}
	 * .
	 */
	TabularType		SERVICES_TYPE			= Item
													.tabularType(
															"SERVICES",
															"The table of all services",
															SERVICE_TYPE,
															IDENTIFIER);

	/**
	 * Answer the list of interfaces that this service implements
	 * 
	 * @param serviceId the identifier of the service
	 * @return the list of interfaces
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the service indicated does not exist
	 */
	public String[] getObjectClass(long serviceId) throws IOException;

	/**
	 * Answer the bundle identifier of the bundle which registered the service
	 * 
	 * @param serviceId the identifier of the service
	 * @return the identifier for the bundle
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the service indicated does not exist
	 */
	long getBundleIdentifier(long serviceId) throws IOException;

	/**
	 * Answer the map of properties associated with this service
	 * 
	 * @see JmxConstants#PROPERTIES_TYPE for the details of the TabularType
	 * 
	 * @param serviceId the identifier of the service
	 * @return the table of properties. These include the standard mandatory
	 *         service.id and objectClass properties as defined in the
	 *         <code>org.osgi.framework.Constants</code> interface
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the service indicated does not exist
	 */
	TabularData getProperties(long serviceId) throws IOException;

	/**
	 * Answer the service state of the system in tabular form.
	 * 
	 * @see #SERVICES_TYPE for the details of the TabularType
	 * 
	 * @return the tabular representation of the service state
	 * @throws IOException If the operation fails
	 * @throws IllegalArgumentException if the service indicated does not exist
	 */
	TabularData listServices() throws IOException;

	/**
	 * Answer the list of identifiers of the bundles that use the service
	 * 
	 * @param serviceId the identifier of the service
	 * @return the list of bundle identifiers
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the service indicated does not exist
	 */
	long[] getUsingBundles(long serviceId) throws IOException;

}
