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

import static org.osgi.jmx.service.useradmin.UserManagerMBean.ROLE_NAMES;
import static org.osgi.jmx.service.useradmin.UserManagerMBean.USER_NAME;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import org.osgi.jmx.service.useradmin.UserManagerMBean;
import org.osgi.service.useradmin.Authorization;

/** 
 * 
 */
@SuppressWarnings("unchecked")
public class OSGiAuthorization {
	/**
	 * The members of an authorization
	 */
	public static final String[] AUTHORIZATION_ITEM_NAMES = { USER_NAME,
			ROLE_NAMES };

	private static final Logger log = Logger.getLogger(OSGiAuthorization.class
			.getCanonicalName());
	/**
	 * The name of the authorization
	 */
	protected String name;
	/**
	 * The roles
	 */
	protected String[] roles;

	/**
	 * Construct the instance from the supplied composite data
	 * 
	 * @param data
	 */
	public OSGiAuthorization(CompositeData data) {
		if (data != null) {
			this.name = (String) data.get(UserManagerMBean.USER_NAME);
			this.roles = (String[]) data.get(UserManagerMBean.ROLE_NAMES);
		}
	}

	/**
	 * Construct an instance from the OSGi authorization instance
	 * 
	 * @param authorization
	 */
	public OSGiAuthorization(Authorization authorization) {
		this(authorization.getName(), authorization.getRoles());
	}

	/**
	 * Construct and instance using the supplied name and role names
	 * 
	 * @param name
	 * @param roles
	 */
	public OSGiAuthorization(String name, String[] roles) {
		this.name = name;
		this.roles = roles;
	}

	private static CompositeType createAuthorizationType() {
		String description = "An authorization object defines which roles has a user got";
		String[] itemNames = AUTHORIZATION_ITEM_NAMES;
		String[] itemDescriptions = new String[2];
		itemDescriptions[0] = "The user name for this authorization object";
		itemDescriptions[1] = "The names of the roles encapsulated by this auth object";
		OpenType[] itemTypes = new OpenType[2];
		itemTypes[0] = SimpleType.STRING;
		itemTypes[1] = Util.STRING_ARRAY_TYPE;
		try {
			return new CompositeType("Authorization", description, itemNames,
					itemDescriptions, itemTypes);
		} catch (OpenDataException e) {
			log.log(Level.SEVERE, "cannot create authorization open data type",
					e);
			return null;
		}
	}

	/**
	 * Convert the receiver into the composite data it represents
	 * 
	 * @return the composite data representation of the receiver
	 * @throws OpenDataException
	 */
	public CompositeData asCompositeData() throws OpenDataException {
		Object[] itemValues = new Object[2];
		String[] itemNames = AUTHORIZATION_ITEM_NAMES;
		itemValues[0] = name;
		itemValues[1] = roles;
		return new CompositeDataSupport(AUTHORIZATION, itemNames, itemValues);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the roles
	 */
	public String[] getRoles() {
		return roles;
	}

	/**
	 * The composite type
	 * 
	 */
	public final static CompositeType AUTHORIZATION = createAuthorizationType();
}
