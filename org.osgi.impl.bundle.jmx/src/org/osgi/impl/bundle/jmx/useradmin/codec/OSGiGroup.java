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

import java.util.HashMap;
import java.util.Map;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.OpenDataException;

import org.osgi.impl.bundle.jmx.codec.OSGiProperties;
import org.osgi.jmx.service.useradmin.UserAdminMBean;
import org.osgi.service.useradmin.Group;
import org.osgi.service.useradmin.Role;

/** 
 */
public class OSGiGroup extends OSGiUser {
	/**
	 * the members
	 */
	protected String[] members;
	/**
	 * the required members
	 */
	protected String[] requiredMembers;

	/**
	 * Construct an instance from the OSGi group
	 * 
	 * @param group
	 */
	public OSGiGroup(Group group) {
		super(group);
		Role[] m = group.getMembers();
		if (m != null) {
			members = new String[m.length];
			int i = 0;
			for (Role role : m) {
				members[i++] = role.getName();
			}
		} else {
			members = new String[0];
		}
		Role[] rm = group.getRequiredMembers();
		if (rm != null) {
			requiredMembers = new String[rm.length];
			int i = 0;
			for (Role role : rm) {
				requiredMembers[i++] = role.getName();
			}
		} else {
			requiredMembers = new String[0];
		}
	}

	/**
	 * Construct an instance from the supplied composite data
	 * 
	 * @param data
	 */
	public OSGiGroup(CompositeData data) {
		super(data);
		members = (String[]) data.get(UserAdminMBean.MEMBERS);
		requiredMembers = (String[]) data.get(UserAdminMBean.REQUIRED_MEMBERS);
	}

	/**
	 * Convert the receiver into the composite data representation
	 * 
	 * @return the composite data representation of the receiver
	 * @throws OpenDataException
	 */
	@SuppressWarnings("boxing")
	public CompositeData asCompositeData() throws OpenDataException {
		Map<String, Object> items = new HashMap<String, Object>();
		items.put(UserAdminMBean.NAME, name);
		items.put(UserAdminMBean.TYPE, type);
		items.put(UserAdminMBean.PROPERTIES, OSGiProperties
				.tableFrom(properties));
		items.put(UserAdminMBean.CREDENTIALS, OSGiProperties
				.tableFrom(credentials));
		items.put(UserAdminMBean.MEMBERS, members);
		items.put(UserAdminMBean.REQUIRED_MEMBERS, requiredMembers);
		return new CompositeDataSupport(UserAdminMBean.GROUP_TYPE, items);
	}

	/**
	 * @return the members
	 */
	public String[] getMembers() {
		return members;
	}

	/**
	 * @return the requiredMembers
	 */
	public String[] getRequiredMembers() {
		return requiredMembers;
	}
}
