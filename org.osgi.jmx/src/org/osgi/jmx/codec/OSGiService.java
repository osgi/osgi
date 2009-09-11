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

package org.osgi.jmx.codec;

import static org.osgi.framework.Constants.OBJECTCLASS;
import static org.osgi.framework.Constants.SERVICE_ID;
import static org.osgi.jmx.codec.Util.LONG_ARRAY_TYPE;
import static org.osgi.jmx.codec.Util.LongArrayFrom;
import static org.osgi.jmx.codec.Util.STRING_ARRAY_TYPE;
import static org.osgi.jmx.codec.Util.longArrayFrom;
import static org.osgi.jmx.framework.ServiceStateMBean.BUNDLE_IDENTIFIER;
import static org.osgi.jmx.framework.ServiceStateMBean.OBJECT_CLASS;
import static org.osgi.jmx.framework.ServiceStateMBean.USING_BUNDLES;

import java.util.ArrayList;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

import org.osgi.framework.ServiceReference;
import org.osgi.jmx.framework.ServiceStateMBean;

/**
 * <p>
 * This class represents the CODEC for the composite data representing an OSGi
 * <link>ServiceReference</link>
 * <p>
 * It serves as both the documentation of the type structure and as the
 * codification of the mechanism to convert to/from the CompositeData.
 * <p>
 * The structure of the composite data is:
 * <table border="1">
 * <tr>
 * <td>Identifier</td>
 * <td>String</td>
 * </tr>
 * <tr>
 * <td>ObjectClass</td>
 * <td>Array of String</td>
 * </tr>
 * <tr>
 * <td>BundleIdentifier</td>
 * <td>long</td>
 * </tr>
 * <tr>
 * <td>UsingBundles</td>
 * <td>Array of long</td>
 * </tr>
 * </table>
 */
@SuppressWarnings("unchecked")
public class OSGiService {

	/**
	 * The item names in the CompositeData representing the service
	 */
	private static final String[] SERVICE_ITEM_NAMES = { SERVICE_ID,
			OBJECT_CLASS, BUNDLE_IDENTIFIER, USING_BUNDLES };

	/**
	 * Construct an OSGiService encoded in the <link>CompositeData</link>
	 * 
	 * @param data
	 *            - the <link>CompositeData</link> encoding the OSGiService
	 */
	public OSGiService(CompositeData data) {
		this((Long) data.get(ServiceStateMBean.SERVICE_ID), (String[]) data
				.get(ServiceStateMBean.OBJECT_CLASS), (Long) data
				.get(ServiceStateMBean.BUNDLE_IDENTIFIER),
				longArrayFrom((Long[]) data
						.get(ServiceStateMBean.USING_BUNDLES)));
	}

	/**
	 * Construct an OSGiService
	 * 
	 * @param identifier
	 * @param interfaces
	 * @param bundle
	 * @param usingBundles
	 */
	public OSGiService(long identifier, String[] interfaces, long bundle,
			long[] usingBundles) {
		this.identifier = identifier;
		this.interfaces = interfaces;
		this.bundle = bundle;
		this.usingBundles = usingBundles;
	}

	/**
	 * Construct an OSGiService from the underlying
	 * <link>ServiceReference</link>
	 * 
	 * @param reference
	 *            - the reference of the service
	 */
	public OSGiService(ServiceReference reference) {
		this((Long) reference.getProperty(SERVICE_ID), (String[]) reference
				.getProperty(OBJECTCLASS), reference.getBundle().getBundleId(),
				Util.bundleIds(reference.getUsingBundles()));
	}

	/**
	 * Construct the TabularData representing a list of services
	 * 
	 * @param services
	 *            - the list of services
	 * 
	 * @return the TabularData representing the list of OSGiServices
	 */
	public static TabularData tableFrom(ArrayList<OSGiService> services) {
		TabularDataSupport table = new TabularDataSupport(SERVICE_TABLE);
		for (OSGiService service : services) {
			table.put(service.asCompositeData());
		}
		return table;
	}

	private static TabularType createServiceTableType() {
		try {
			return new TabularType("Services", "The table of all services",
					SERVICE, new String[] { ServiceStateMBean.SERVICE_ID });
		} catch (OpenDataException e) {
			throw new IllegalStateException(
					"Cannot form services table open data", e);
		}
	}

	private static CompositeType createServiceType() {
		String description = "This type encapsulates an OSGi service";
		String[] itemNames = SERVICE_ITEM_NAMES;
		OpenType[] itemTypes = new OpenType[itemNames.length];
		String[] itemDescriptions = new String[itemNames.length];
		itemTypes[0] = SimpleType.LONG;
		itemTypes[1] = STRING_ARRAY_TYPE;
		itemTypes[2] = SimpleType.LONG;
		itemTypes[3] = LONG_ARRAY_TYPE;

		itemDescriptions[0] = "The identifier of the service";
		itemDescriptions[1] = "The interfaces of the service";
		itemDescriptions[2] = "The id of the bundle the service belongs to";
		itemDescriptions[3] = "The bundles using the service";

		try {
			return new CompositeType("Service", description, itemNames,
					itemDescriptions, itemTypes);
		} catch (OpenDataException e) {
			throw new IllegalStateException("Cannot form service open data", e);
		}

	}

	/**
	 * Answer the receiver encoded as CompositeData
	 * 
	 * @return the CompositeData encoding of the receiver.
	 */
	public CompositeData asCompositeData() {
		String[] itemNames = SERVICE_ITEM_NAMES;
		Object[] itemValues = new Object[4];
		itemValues[0] = identifier;
		itemValues[1] = interfaces;
		itemValues[2] = bundle;
		itemValues[3] = LongArrayFrom(usingBundles);

		try {
			return new CompositeDataSupport(SERVICE, itemNames, itemValues);
		} catch (OpenDataException e) {
			throw new IllegalStateException("Cannot form service open data", e);
		}
	}

	/**
	 * @return the identifier of the bundle the service belongs to
	 */
	public long getBundle() {
		return bundle;
	}

	/**
	 * @return the identifier of the service
	 */
	public long getIdentifier() {
		return identifier;
	}

	/**
	 * @return the interfaces implemented by the service
	 */
	public String[] getInterfaces() {
		return interfaces;
	}

	/**
	 * @return the identifiers of the bundles which are using the service
	 */
	public long[] getUsingBundles() {
		return usingBundles;
	}

	/**
	 * The CompositeType representation of the service
	 */
	public final static CompositeType SERVICE = createServiceType();

	/**
	 * The TabularType representation of a list of services
	 */
	public final static TabularType SERVICE_TABLE = createServiceTableType();

	private long bundle;

	private long identifier;

	private String[] interfaces;
	private long[] usingBundles;
}
