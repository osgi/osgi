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

package org.osgi.impl.bundle.jmx.useradmin.codec;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.TabularData;

import org.osgi.impl.bundle.jmx.codec.OSGiProperties;
import org.osgi.jmx.service.useradmin.UserAdminMBean;
import org.osgi.service.useradmin.Role;

/** 
 * 
 */
@SuppressWarnings("unchecked")
public class OSGiRole {

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
	@SuppressWarnings("boxing")
	public OSGiRole(CompositeData data) {
		name = (String) data.get(UserAdminMBean.NAME);
		type = (Integer) data.get(UserAdminMBean.TYPE);
		properties = OSGiProperties.propertiesFrom((TabularData) data
				.get(UserAdminMBean.PROPERTIES));
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

	/**
	 * Convert the receiver into the composite data that represents it
	 * 
	 * @return the
	 * @throws OpenDataException
	 */
	@SuppressWarnings("boxing")
	public CompositeData asCompositeData() throws OpenDataException {
		Map<String, Object> items = new HashMap<String, Object>();
		items.put(UserAdminMBean.NAME, name);
		items.put(UserAdminMBean.TYPE, type);
		items.put(UserAdminMBean.PROPERTIES, OSGiProperties
				.tableFrom(properties));
		return new CompositeDataSupport(UserAdminMBean.ROLE_TYPE, items);
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
}
