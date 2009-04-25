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

package org.osgi.jmx.useradmin;

import java.io.IOException;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

/**
 * This MBean provides the management interface to the OSGi User Manager Service
 */
public interface UserManagerMBean {
	/**
	 * Add credentials to a user, associated with the supplied key
	 * 
	 * @param key
	 * @param value
	 * @param username
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the username is not a User
	 */
	void addCredential(String key, byte[] value, String username)
			throws IOException;

	/**
	 * Add credentials to a user, associated with the supplied key
	 * 
	 * @param key
	 * @param value
	 * @param username
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the username is not a User
	 */
	void addCredential(String key, String value, String username)
			throws IOException;

	/**
	 * Add a role to the group
	 * 
	 * @param groupname
	 * @param rolename
	 * @return true if the role was added to the group
	 * @throws IOException
	 *             if the operation fails
	 */
	boolean addMember(String groupname, String rolename) throws IOException;

	/**
	 * Add or update a property on a role
	 * 
	 * @param key
	 *            - the property key
	 * @param value
	 *            - the String property value
	 * @param rolename
	 *            - the role name
	 * @throws IOException
	 *             if the operation fails
	 */
	void addProperty(String key, String value, String rolename)
			throws IOException;

	/**
	 * Add or update a property on a role
	 * 
	 * @param key
	 *            - the property key
	 * @param value
	 *            - the byte[] property value
	 * @param rolename
	 *            - the role name
	 * @throws IOException
	 *             if the operation fails
	 */
	void addProperty(String key, byte[] value, String rolename)
			throws IOException;

	/**
	 * Add a required member to the group
	 * 
	 * @param groupname
	 * @param rolename
	 * @return true if the role was added to the group
	 * @throws IOException
	 *             if the operation fails
	 */
	boolean addRequiredMember(String groupname, String rolename)
			throws IOException;

	/**
	 * Create a User
	 * 
	 * @param name
	 *            - the user to create
	 * @throws IOException
	 *             if the operation fails
	 */
	void createUser(String name) throws IOException;

	/**
	 * Create a Group
	 * 
	 * @param name
	 *            - the group to create
	 * @throws IOException
	 *             if the operation fails
	 */
	void createGroup(String name) throws IOException;

	/**
	 * Answer the authorization for the user name
	 * <p>
	 * 
	 * @see org.osgi.jmx.codec.OSGiAuthorization for the details of the
	 *      CompositeType
	 *      <p>
	 * @param user
	 * @return the Authorization
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the username is not a User
	 */
	CompositeData getAuthorization(String user) throws IOException;

	/**
	 * Answer the credentials associated with a user
	 * <p>
	 * 
	 * @see org.osgi.jmx.codec.OSGiProperties for the details of the TabularType
	 *      <p>
	 * @param username
	 * @return the credentials associated with the user
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the username is not a User
	 */
	TabularData getCredentials(String username) throws IOException;

	/**
	 * Answer the Group associated with the groupname
	 * <p>
	 * 
	 * @see org.osgi.jmx.codec.OSGiGroup for the details of the CompositeType
	 *      <p>
	 * @param groupname
	 * @return the Group
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the groupname is not a Group
	 */
	CompositeData getGroup(String groupname) throws IOException;

	/**
	 * Answer the list of group names
	 * 
	 * @return the list of group names
	 * @throws IOException
	 *             if the operation fails
	 */
	String[] getGroups() throws IOException;

	/**
	 * Answer the list of group names
	 * 
	 * @param filter
	 *            - the filter to apply
	 * @return the list of group names
	 * @throws IOException
	 *             if the operation fails
	 */
	String[] getGroups(String filter) throws IOException;

	/**
	 * Answer the list of implied roles for a user
	 * 
	 * @param username
	 * @return the list of role names
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the username is not a User
	 */
	String[] getImpliedRoles(String username) throws IOException;

	/**
	 * Answer the the user names which are members of the group
	 * 
	 * @param groupname
	 * @return the list of user names
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the groupname is not a group
	 */
	String[] getMembers(String groupname) throws IOException;

	/**
	 * Answer the credentials associated with a role
	 * <p>
	 * 
	 * @see org.osgi.jmx.codec.OSGiProperties for the details of the TabularType
	 *      <p>
	 * @param rolename
	 * @return the credentials associated with the role
	 * @throws IOException
	 *             if the operation fails
	 */
	TabularData getProperties(String rolename) throws IOException;

	/**
	 * Answer the list of user names which are required members of this group
	 * 
	 * @param groupname
	 * @return the list of user names
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the groupname is not a group
	 */
	String[] getRequiredMembers(String groupname) throws IOException;

	/**
	 * Answer the role associated with a name
	 * <p>
	 * 
	 * @see org.osgi.jmx.codec.OSGiRole for the details of the CompositeType
	 *      <p>
	 * @param name
	 * @return the Role
	 * @throws IOException
	 *             if the operation fails
	 */
	CompositeData getRole(String name) throws IOException;

	/**
	 * Answer the list of role names in the User Admin database
	 * 
	 * @return the list of role names
	 * @throws IOException
	 *             if the operation fails
	 */
	String[] getRoles() throws IOException;

	/**
	 * Answer the list of role names which match the supplied filter
	 * 
	 * @param filter
	 *            - the string representation of the
	 *            <code>org.osgi.framework.Filter</code>
	 * @return the list the role names
	 * @throws IOException
	 *             if the operation fails
	 */
	String[] getRoles(String filter) throws IOException;

	/**
	 * Answer the User associated with the username
	 * <p>
	 * 
	 * @see org.osgi.jmx.codec.OSGiUser for the details of the CompositeType
	 *      <p>
	 * @param username
	 * @return the User
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the username is not a User
	 */
	CompositeData getUser(String username) throws IOException;

	/**
	 * Answer the user name with the given property key-value pair from the User
	 * Admin service database.
	 * 
	 * @param key
	 *            - the key to compare
	 * @param value
	 *            - the value to compare
	 * @return the User
	 * @throws IOException
	 *             if the operation fails
	 */
	String getUser(String key, String value) throws IOException;

	/**
	 * Answer the list of user names in the User Admin database
	 * 
	 * @return the list of user names
	 * @throws IOException
	 *             if the operation fails
	 */
	String[] getUsers() throws IOException;

	/**
	 * Answer the list of user names in the User Admin database
	 * 
	 * @param filter
	 *            - the filter to apply
	 * @return the list of user names
	 * @throws IOException
	 *             if the operation fails
	 */
	String[] getUsers(String filter) throws IOException;

	/**
	 * Remove the credentials associated with the key for the user
	 * 
	 * @param key
	 * @param username
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the username is not a User
	 */
	void removeCredential(String key, String username) throws IOException;

	/**
	 * Remove a role from the group
	 * 
	 * @param groupname
	 * @param rolename
	 * @return true if the role was removed from the group
	 * @throws IOException
	 *             if the operation fails
	 * @throws IllegalArgumentException
	 *             if the groupname is not a Group
	 */
	boolean removeMember(String groupname, String rolename) throws IOException;

	/**
	 * Remove a property from a role
	 * 
	 * @param key
	 * @param rolename
	 * @throws IOException
	 *             if the operation fails
	 */
	void removeProperty(String key, String rolename) throws IOException;

	/**
	 * Remove the Role associated with the name
	 * 
	 * @param name
	 * @return true if the remove succeeded
	 * @throws IOException
	 *             if the operation fails
	 */
	boolean removeRole(String name) throws IOException;

	/**
	 * The Role name for the user/group
	 */
	public static final String ROLE_NAME = "Name";
	/**
	 * The Role type
	 */
	public static final String ROLE_TYPE = "Type";
	/**
	 * The properties for the role
	 */
	public static final String ROLE_ENCODED_PROPERTIES = "Properties";
	/**
	 * The encoded user
	 */
	public static final String ENCODED_USER = "User";
	/**
	 * The members of the group
	 */
	public static final String GROUP_MEMBERS = "Members";
	/**
	 * The required members
	 */
	public static final String GROUP_REQUIRED_MEMBERS = "RequiredMembers";
	/**
	 * The user name
	 */
	public static final String USER_NAME = "UserName";
	/**
	 * The Role names
	 */
	public static final String ROLE_NAMES = "RoleNames";
	/**
	 * The Role
	 */
	public static final String ENCODED_ROLE = "Role";
	/**
	 * The encoded credentials
	 */
	public static final String ENCODED_CREDENTIALS = "Credentials";
	/**
	 * The members of an authorization
	 */
	public static final String[] AUTHORIZATION = { USER_NAME, ROLE_NAMES };
	/**
	 * The members of a user
	 */
	public static final String[] USER = { ENCODED_ROLE, ENCODED_CREDENTIALS };
	/**
	 * The members of a role
	 */
	public static final String[] ROLE = { ROLE_NAME, ROLE_TYPE,
			ROLE_ENCODED_PROPERTIES };
	/**
	 * The members of a group
	 */
	public static final String[] GROUP = { ENCODED_USER, GROUP_MEMBERS,
			GROUP_REQUIRED_MEMBERS };
}