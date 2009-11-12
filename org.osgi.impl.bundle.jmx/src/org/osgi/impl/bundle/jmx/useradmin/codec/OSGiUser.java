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

import org.osgi.jmx.service.useradmin.UserAdminMBean;
import org.osgi.service.useradmin.User;

/** 
 * 
 */
public class OSGiUser extends OSGiRole {

	/**
	 * Construct an instance from the supplied OSGi user
	 * 
	 * @param user
	 */
	public OSGiUser(User user) {
		super(user);
	}

	/**
	 * Construct an instance from the encoded composite data
	 * 
	 * @param data
	 */
	public OSGiUser(CompositeData data) {
		super(data);
	}

	/**
	 * Transform the receiver into its composite data representation
	 * 
	 * @return the composite data representation of the receiver
	 * @throws OpenDataException
	 */
	@SuppressWarnings("boxing")
	public CompositeData asCompositeData() throws OpenDataException {
		Map<String, Object> items = new HashMap<String, Object>();
		items.put(UserAdminMBean.NAME, name);
		items.put(UserAdminMBean.TYPE, type);
		return new CompositeDataSupport(UserAdminMBean.USER_TYPE, items);
	}

}
