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

import static org.osgi.jmx.useradmin.UserManagerMBean.ROLE_ENCODED_PROPERTIES;
import static org.osgi.jmx.useradmin.UserManagerMBean.ROLE_NAME;
import static org.osgi.jmx.useradmin.UserManagerMBean.ROLE_TYPE;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;

import org.osgi.jmx.useradmin.UserManagerMBean;
import org.osgi.service.useradmin.Role;

/** 
 * 
 */
@SuppressWarnings("unchecked")
public class OSGiRole {
	/**
	 * The members of a role
	 */
	private static final String[] ROLE_ITEM_NAMES = { ROLE_NAME, ROLE_TYPE,
			ROLE_ENCODED_PROPERTIES };

	/**
	 * The role name
	 */
	protected String name;
	/**
	 * The type of the role
	 */
	protected int type;
	/**
	 * The properties of the role
	 */
	protected Hashtable<String, Object> properties;

	/**
	 * Construct and instance from the composite data representation
	 * 
	 * @param data
	 */
	public OSGiRole(CompositeData data) {
		name = (String) data.get(UserManagerMBean.ROLE_NAME);
		type = (Integer) data.get(UserManagerMBean.ROLE_TYPE);
		properties = OSGiProperties.propertiesFrom((TabularData) data
				.get(UserManagerMBean.ROLE_ENCODED_PROPERTIES));
	}

	/**
	 * Construct and instance from the supplied OSGi role
	 * 
	 * @param role
	 */
	public OSGiRole(Role role) {
		name = role.getName();
		type = role.getType();
		properties = new Hashtable<String, Object>();
		Dictionary props = role.getProperties();
		for (Enumeration keys = props.keys(); keys.hasMoreElements();) {
			String key = (String) keys.nextElement();
			properties.put(key, props.get(key));
		}
	}

	private static CompositeType createRoleType() {
		String description = "Mapping of org.osgi.service.useradmin.Role for remote management purposes. User and Group extend Role";
		String[] RoleItemNames = ROLE_ITEM_NAMES;
		String[] itemDescriptions = new String[3];
		itemDescriptions[0] = "The name of the role. Can be either a group or a user";
		itemDescriptions[1] = "An integer representing type of the role: {0=Role,1=user,2=group}";
		itemDescriptions[2] = "A credentials list as defined by org.osgi.service.useradmin.Role";
		OpenType[] itemTypes = new OpenType[3];
		itemTypes[0] = SimpleType.STRING;
		itemTypes[1] = SimpleType.INTEGER;
		itemTypes[2] = OSGiProperties.PROPERTY_TABLE;
		try {

			return new CompositeType("Role", description, RoleItemNames,
					itemDescriptions, itemTypes);
		} catch (OpenDataException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Convert the receiver into the composite data that represents it
	 * 
	 * @return the
	 * @throws OpenDataException
	 */
	public CompositeData asCompositeData() throws OpenDataException {
		Object[] itemValues = new Object[3];
		itemValues[0] = name;
		itemValues[1] = type;
		itemValues[2] = OSGiProperties.tableFrom(properties);
		return new CompositeDataSupport(ROLE, ROLE_ITEM_NAMES, itemValues);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return the credentials
	 */
	public Map<String, Object> getProperties() {
		return properties;
	}

	/**
	 * The composite type of the role
	 */
	public final static CompositeType ROLE = createRoleType();
}
