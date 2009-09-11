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

import static org.osgi.jmx.service.framework.BundleStateMBean.BUNDLE_ID;
import static org.osgi.jmx.service.framework.BundleStateMBean.BUNDLE_LOCATION;
import static org.osgi.jmx.service.framework.BundleStateMBean.BUNDLE_SYMBOLIC_NAME;
import static org.osgi.jmx.service.framework.BundleStateMBean.EVENT_TYPE;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import org.osgi.framework.BundleEvent;

/**
 * <p>
 * This class represents the CODEC for the composite data representing a OSGi
 * <link>BundleEvent</link>
 * <p>
 * It serves as both the documentation of the type structure and as the
 * codification of the mechanism to convert to/from the CompositeData.
 * <p>
 * The structure of the composite data is:
 * <table border="1">
 * <tr>
 * <td>Identifier</td>
 * <td>long</td>
 * </tr>
 * <tr>
 * <td>location</td>
 * <td>String</td>
 * </tr>
 * <tr>
 * <td>SymbolicName</td>
 * <td>String</td>
 * </tr>
 * <tr>
 * <td>EventType</td>
 * <td>int</td>
 * </tr>
 * </table>
 */
public class OSGiBundleEvent {

	/**
	 * The item names in the CompositeData representing the event raised for
	 * bundle events within the OSGi container by this bean
	 */
	private static final String[] BUNDLE_EVENT_ITEMS = { BUNDLE_ID,
			BUNDLE_LOCATION, BUNDLE_SYMBOLIC_NAME, EVENT_TYPE };

	/**
	 * Construct an OSGiBundleEvent from the supplied <ling>BundleEvent</link>
	 * 
	 * @param event
	 *            - the event to represent
	 */
	public OSGiBundleEvent(BundleEvent event) {
		this(event.getBundle().getBundleId(), event.getBundle().getLocation(),
				event.getBundle().getSymbolicName(), event.getType());
	}

	/**
	 * Construct an OSGiBundleEvent from the CompositeData representing the
	 * event
	 * 
	 * @param data
	 *            - the CompositeData representing the event.
	 */
	public OSGiBundleEvent(CompositeData data) {
		bundleId = (Long) data.get(BUNDLE_ID);
		location = (String) data.get(BUNDLE_LOCATION);
		symbolicName = (String) data.get(BUNDLE_SYMBOLIC_NAME);
		eventType = (Integer) data.get(EVENT_TYPE);
	}

	/**
	 * Construct the OSGiBundleEvent
	 * 
	 * @param bundleId
	 * @param location
	 * @param symbolicName
	 * @param eventType
	 */
	public OSGiBundleEvent(long bundleId, String location, String symbolicName,
			int eventType) {
		this.bundleId = bundleId;
		this.location = location;
		this.symbolicName = symbolicName;
		this.eventType = eventType;
	}

	@SuppressWarnings("unchecked")
	private static CompositeType createBundleEventType() {
		String description = "This type encapsulates OSGi bundle events";
		String[] itemNames = BUNDLE_EVENT_ITEMS;
		OpenType[] itemTypes = new OpenType[4];
		String[] itemDescriptions = new String[4];
		itemTypes[0] = SimpleType.LONG;
		itemTypes[1] = SimpleType.STRING;
		itemTypes[2] = SimpleType.STRING;
		itemTypes[3] = SimpleType.INTEGER;
		itemDescriptions[0] = "The ID of the bundle that generated this event";
		itemDescriptions[1] = "The location of the bundle that generated this event";
		itemDescriptions[2] = "The symbolic name of the bundle that generated this event";
		itemDescriptions[3] = "The type of the event: {INSTALLED=1, STARTED=2, STOPPED=4, UPDATED=8, UNINSTALLED=16}";
		try {
			return new CompositeType("BundleEvent", description, itemNames,
					itemDescriptions, itemTypes);
		} catch (OpenDataException e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Answer the receiver encoded as CompositeData
	 * 
	 * @return the CompositeData encoding of the receiver.
	 */
	public CompositeData asCompositeData() {
		String[] itemNames = BUNDLE_EVENT_ITEMS;
		Object[] itemValues = new Object[4];
		itemValues[0] = bundleId;
		itemValues[1] = location;
		itemValues[2] = symbolicName;
		itemValues[3] = eventType;
		try {
			return new CompositeDataSupport(BUNDLE_EVENT, itemNames, itemValues);
		} catch (OpenDataException e) {
			throw new IllegalStateException(
					"Cannot form bundle event open data", e);
		}
	}

	/**
	 * @return the identifier of the bundle for this event
	 */
	public long getBundleId() {
		return bundleId;
	}

	/**
	 * @return the type of the event
	 */
	public int getEventType() {
		return eventType;
	}

	/**
	 * @return the location of the bundle for this event
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @return the symbolic name of the bundle for this event
	 */
	public String getSymbolicName() {
		return symbolicName;
	}

	/**
	 * The CompositeType representation of the event
	 */
	public final static CompositeType BUNDLE_EVENT = createBundleEventType();

	private long bundleId;

	private int eventType;

	private String location;

	private String symbolicName;

}
