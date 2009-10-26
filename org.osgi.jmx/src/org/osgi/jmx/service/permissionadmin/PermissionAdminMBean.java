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

package org.osgi.jmx.service.permissionadmin;

import java.io.IOException;

import org.osgi.jmx.JmxConstants;

/**
 * This MBean represents the OSGi Permission Manager Service
 * 
 * @version $Revision$
 */
public interface PermissionAdminMBean {
	/**
	 * Permission Admin MBean object name.
	 */
	String	OBJECTNAME	= JmxConstants.OSGI_CORE
								+ ":service=permissionadmin,version=1.2";

	/**
	 * Answer the bundle locations that have permissions assigned to them
	 * 
	 * @return the bundle locations
	 * @throws IOException if the operation fails
	 */
	String[] listLocations() throws IOException;

	/**
	 * Answer the list of encoded permissions of the bundle specified by the
	 * bundle location
	 * 
	 * @param location location identifying the bundle
	 * @return the array of String encoded permissions
	 * @throws IOException if the operation fails
	 */
	String[] getPermissions(String location) throws IOException;

	/**
	 * Set the default permissions assigned to bundle locations that have no
	 * assigned permissions
	 * 
	 * @param encodedPermissions the string encoded permissions
	 * @throws IOException if the operation fails
	 */
	void setDefaultPermissions(String[] encodedPermissions) throws IOException;

	/**
	 * Answer the list of encoded permissions representing the default
	 * permissions assigned to bundle locations that have no assigned
	 * permissions
	 * 
	 * @return the array of String encoded permissions
	 * @throws IOException if the operation fails
	 */
	String[] listDefaultPermissions() throws IOException;

	/**
	 * Set the permissions on the bundle specified by the bundle location
	 * 
	 * @param location the location of the bundle
	 * @param encodedPermissions the string encoded permissions to set
	 * @throws IOException if the operation fails
	 */
	void setPermissions(String location, String[] encodedPermissions)
			throws IOException;
}
