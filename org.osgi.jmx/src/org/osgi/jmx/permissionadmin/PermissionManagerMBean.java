/*
 * Copyright 2008 Oracle Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.osgi.jmx.permissionadmin;

import java.io.IOException;

/**
 * This MBean represents the OSGi Permission Manager Service
 */
public interface PermissionManagerMBean {
	/**
	 * Answer the bundle locations that have permissions assigned to them
	 * 
	 * @return the bundle locations
	 * @throws IOException
	 *             if the operation fails
	 */
	String[] getLocations() throws IOException;

	/**
	 * Answer the list of encoded permissions of the bundle specified by the
	 * bundle location
	 * 
	 * @param location
	 *            - location identifying the bundle
	 * @return the array of String encoded permissions
	 * @throws IOException
	 *             if the operation fails
	 */
	String[] getPermissions(String location) throws IOException;

	/**
	 * Set the default permissions assiged to bundle locations that have no
	 * assigned permissions
	 * 
	 * @param encodedPermissions
	 *            - the string encoded permissions
	 * @throws IOException
	 *             if the operation fails
	 */
	void setDefaultPermissions(String[] encodedPermissions) throws IOException;

	/**
	 * Set the permissions on the bundle specified by the bundle location
	 * 
	 * @param location
	 *            - the location of the bundle
	 * @param encodedPermissions
	 *            - the string encoded permissions to set
	 * @throws IOException
	 *             if the operation fails
	 */
	void setPermissions(String location, String[] encodedPermissions)
			throws IOException;
}
