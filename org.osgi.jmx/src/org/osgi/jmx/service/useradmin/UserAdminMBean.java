/*
 * Copyright (c) OSGi Alliance (2009, 2010). All Rights Reserved.
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

package org.osgi.jmx.service.useradmin;

import java.io.IOException;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;

import org.osgi.jmx.Item;
import org.osgi.jmx.JmxConstants;

/**
 * This MBean provides the management interface to the OSGi User Manager Service
 * 
 * @version $Revision$
 * @ThreadSafe
 */
public interface UserAdminMBean {
	/**
	 * User Admin MBean object name.
	 */
	String			OBJECTNAME				= JmxConstants.OSGI_COMPENDIUM
													+ ":service=useradmin,version=1.1";

	/**
	 * The key NAME, used in {@link #NAME_ITEM}.
	 */
	String			NAME					= "Name";

	/**
	 * The item for the user name for an authorization object. The key is
	 * {@link #NAME} and the type is {@link SimpleType#STRING}.
	 */
	Item			NAME_ITEM				= new Item(
													NAME,
													"The user name for this authorization object",
													SimpleType.STRING);

	/**
	 * The key ROLES, used in {@link #ROLES_ITEM}.
	 */
	String			ROLES					= "Roles";

	/**
	 * The item containing the roles for this authorization object. The key is
	 * {@link #ROLES}. and the type is {@link JmxConstants#STRING_ARRAY_TYPE}.
	 */
	Item			ROLES_ITEM				= new Item(
													ROLES,
													"The names of the roles encapsulated by this auth object",
													JmxConstants.STRING_ARRAY_TYPE);

	/**
	 * The Composite Type for an Authorization object. It consists of the
	 * {@link #NAME_ITEM} and {@link #TYPE_ITEM} items.
	 */
	CompositeType	AUTORIZATION_TYPE		= Item
													.compositeType(
															"AUTHORIZATION",
															"An authorization object defines which roles has a user got",
															NAME_ITEM,
															ROLES_ITEM);
	/**
	 * The Role TYPE key, used in {@link #TYPE_ITEM}.
	 */
	String			TYPE					= "Type";

	/**
	 * The item containing the type of the roles encapsulated by this
	 * authorization object. The key is {@link #TYPE} and the type is
	 * {@link SimpleType#INTEGER}.
	 */
	Item			TYPE_ITEM				= new Item(
													TYPE,
													"An integer representing type of the role: {0=Role,1=user,2=group}",
													SimpleType.INTEGER);

	/**
	 * The PROPERTIES key, used in {@link #PROPERTIES_ITEM}.
	 */
	String			PROPERTIES				= "Properties";

	/**
	 * The item containing the properties of a Role. The key is
	 * {@link #PROPERTIES} and the type is {@link JmxConstants#PROPERTIES_TYPE}.
	 */
	Item			PROPERTIES_ITEM			= new Item(
													PROPERTIES,
													"A properties as defined by org.osgi.service.useradmin.Role",
													JmxConstants.PROPERTIES_TYPE);
	/**
	 * The Composite Type for a Role. It contains the following items:
	 * <ul>
	 * <li>{@link #NAME}</li>
	 * <li>{@link #TYPE}</li>
	 * <li>{@link #PROPERTIES}</li>
	 * </ul>
	 * 
	 */
	CompositeType	ROLE_TYPE				= Item
													.compositeType(
															"ROLE",
															"Mapping of org.osgi.service.useradmin.Role for remote management purposes. User and Group extend Role",
															NAME_ITEM,
															TYPE_ITEM);

	/**
	 * The CREDENTIALS key, used in {@link #CREDENTIALS_ITEM}.
	 */
	String			CREDENTIALS				= "Credentials";

	/**
	 * The item containing the credentials of a user. The key is
	 * {@link #CREDENTIALS} and the type is {@link JmxConstants#PROPERTIES_TYPE} .
	 */
	Item			CREDENTIALS_ITEM		= new Item(
													CREDENTIALS,
													"The credentials for this user",
													JmxConstants.PROPERTIES_TYPE);

	/**
	 * A Composite Type for a User. A User contains its Role description and
	 * adds the credentials. It extends {@link #ROLE_TYPE} and adds
	 * {@link #CREDENTIALS_ITEM}.
	 * 
	 * This type extends the {@link #ROLE_TYPE}. It adds:
	 * <ul>
	 * <li>{@link #CREDENTIALS}</li>
	 * </ul>
	 */
	CompositeType	USER_TYPE				= Item
													.extend(
															ROLE_TYPE,
															"USER",
															"Mapping of org.osgi.service.useradmin.User for remote management purposes. User extends Role");

	/**
	 * The MEMBERS key, used in {@link #MEMBERS_ITEM}.
	 */
	String			MEMBERS					= "Members";

	/**
	 * The item containing the members of a group. The key is {@link #MEMBERS}
	 * and the type is {@link JmxConstants#STRING_ARRAY_TYPE}. It is used in
	 * {@link #GROUP_TYPE}.
	 */
	Item			MEMBERS_ITEM			= new Item(
													MEMBERS,
													"The members of this group",
													JmxConstants.STRING_ARRAY_TYPE);

	/**
	 * The REQUIRED_MEMBERS key, used in {@link #REQUIRED_MEMBERS_ITEM}.
	 */
	String			REQUIRED_MEMBERS		= "RequiredMembers";

	/**
	 * The item containing the required members of a group. The key is
	 * {@link #REQUIRED_MEMBERS} and the type is
	 * {@link JmxConstants#STRING_ARRAY_TYPE}. It is used in
	 * {@link #GROUP_TYPE} .
	 */
	Item			REQUIRED_MEMBERS_ITEM	= new Item(
													REQUIRED_MEMBERS,
													"The required members of this group",
													JmxConstants.STRING_ARRAY_TYPE);

	/**
	 * The Composite Type for a Group. It extends {@link #USER_TYPE} and adds
	 * {@link #MEMBERS_ITEM}, and {@link #REQUIRED_MEMBERS_ITEM}.
	 * 
	 * This type extends the {@link #USER_TYPE}. It adds:
	 * <ul>
	 * <li>{@link #MEMBERS}</li>
	 * <li>{@link #REQUIRED_MEMBERS}</li>
	 * </ul>
	 */
	CompositeType	GROUP_TYPE				= Item
													.extend(
															USER_TYPE,
															"GROUP",
															"Mapping of org.osgi.service.useradmin.Group for remote management purposes. Group extends User which in turn extends Role",
															MEMBERS_ITEM,
															REQUIRED_MEMBERS_ITEM);

	/**
	 * Add credentials to a user, associated with the supplied key
	 * 
	 * @param key The key of the credential to add
	 * @param value The value of the credential to add
	 * @param username The name of the user that gets the credential.
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the user name is not a User
	 */
	void addCredential(String key, byte[] value, String username)
			throws IOException;

	/**
	 * Add credentials to a user, associated with the supplied key
	 * 
	 * @param key The key of the credential to add
	 * @param value The value of the credential to add
	 * @param username The name of the user that gets the credential.
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the username is not a User
	 */
	void addCredentialString(String key, String value, String username)
			throws IOException;

	/**
	 * Add a member to the group.
	 * 
	 * @param groupname The group name that receives the <code>rolename</code>
	 *        as member.
	 * @param rolename The <code>rolename</code> (User or Group) that must be
	 *        added.
	 * @return <code>true</code> if the role was added to the group
	 * @throws IOException if the operation fails
	 * 
	 */
	boolean addMember(String groupname, String rolename) throws IOException;

	/**
	 * Add or update a property on a role
	 * 
	 * @param key The key of the property to add
	 * @param value The value of the property to add (<code>String</code>)
	 * @param rolename The role name
	 * @throws IOException if the operation fails
	 */
	void addPropertyString(String key, String value, String rolename)
			throws IOException;

	/**
	 * Add or update a property on a role.
	 * 
	 * @param key The added property key
	 * @param value The added byte[] property value
	 * @param rolename The role name that receives the property
	 * @throws IOException if the operation fails
	 */
	void addProperty(String key, byte[] value, String rolename)
			throws IOException;

	/**
	 * Add a required member to the group
	 * 
	 * @param groupname The group name that is addded
	 * @param rolename The role that
	 * @return true if the role was added to the group
	 * @throws IOException if the operation fails
	 */
	boolean addRequiredMember(String groupname, String rolename)
			throws IOException;

	/**
	 * Create a User
	 * 
	 * @param name Name of the user to create
	 * @throws IOException if the operation fails
	 */
	void createUser(String name) throws IOException;

	/**
	 * Create a Group
	 * 
	 * @param name Name of the group to create
	 * @throws IOException if the operation fails
	 */
	void createGroup(String name) throws IOException;

	/**
	 * Create a Role
	 * 
	 * @param name of the role to create
	 * @throws IOException if the operation fails
	 */
	void createRole(String name) throws IOException;

	/**
	 * Answer the authorization for the user name.
	 * 
	 * The Composite Data is typed by {@link #AUTORIZATION_TYPE}.
	 * 
	 * @param user The user name
	 * @return the Authorization typed by {@link #AUTORIZATION_TYPE}.
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the user name is not a User
	 */
	CompositeData getAuthorization(String user) throws IOException;

	/**
	 * Answer the credentials associated with a user.
	 * 
	 * The returned Tabular Data is typed by
	 * {@link JmxConstants#PROPERTIES_TYPE}.
	 * 
	 * @param username The user name
	 * @return the credentials associated with the user, see
	 *         {@link JmxConstants#PROPERTIES_TYPE}
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the user name is not a User
	 */
	TabularData getCredentials(String username) throws IOException;

	/**
	 * Answer the Group associated with the group name.
	 * 
	 * The returned Composite Data is typed by {@link #GROUP_TYPE}
	 * 
	 * @param groupname The group name
	 * @return the Group, see {@link #GROUP_TYPE}
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the group name is not a Group
	 */
	CompositeData getGroup(String groupname) throws IOException;

	/**
	 * Answer the list of group names
	 * 
	 * @return The list of group names
	 * @throws IOException if the operation fails
	 */
	String[] listGroups() throws IOException;

	/**
	 * Answer the list of group names
	 * 
	 * @param filter The filter to apply
	 * @return The list of group names
	 * @throws IOException if the operation fails
	 */
	String[] getGroups(String filter) throws IOException;

	/**
	 * Answer the list of implied roles for a user
	 * 
	 * @param username The name of the user that has the implied roles
	 * @return The list of role names
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the username is not a User
	 */
	String[] getImpliedRoles(String username) throws IOException;

	/**
	 * Answer the the user names which are members of the group
	 * 
	 * @param groupname The name of the group to get the members from
	 * @return The list of user names
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the <code>groupname</code> is not a
	 *         group
	 */
	String[] getMembers(String groupname) throws IOException;

	/**
	 * Answer the properties associated with a role.
	 * 
	 * The returned Tabular Data is typed by
	 * {@link JmxConstants#PROPERTIES_TYPE}.
	 * 
	 * @param rolename The name of the role to get properties from
	 * @return the properties associated with the role, see
	 *         {@link JmxConstants#PROPERTIES_TYPE}
	 * @throws IOException if the operation fails
	 */
	TabularData getProperties(String rolename) throws IOException;

	/**
	 * Answer the list of user names which are required members of this group
	 * 
	 * @param groupname The name of the group to get the required members from
	 * @return The list of user names
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the group name is not a group
	 */
	String[] getRequiredMembers(String groupname) throws IOException;

	/**
	 * Answer the role associated with a name.
	 * 
	 * The returned Composite Data is typed by {@link #ROLE_TYPE}.
	 * 
	 * @param name The name of the role to get the data from
	 * @return the Role, see {@link #ROLE_TYPE}
	 * @throws IOException if the operation fails
	 */
	CompositeData getRole(String name) throws IOException;

	/**
	 * Answer the list of role names in the User Admin database
	 * 
	 * @return The list of role names
	 * @throws IOException if the operation fails
	 */
	String[] listRoles() throws IOException;

	/**
	 * Answer the list of role names which match the supplied filter
	 * 
	 * @param filter The string representation of the
	 *        <code>org.osgi.framework.Filter</code> that is used to filter
	 *        the roles by applying to the properties, if <code>null</code>
	 *        all roles are returned.
	 * 
	 * @return The list the role names
	 * @throws IOException if the operation fails
	 */
	String[] getRoles(String filter) throws IOException;

	/**
	 * Answer the User associated with the user name.
	 * 
	 * The returned Composite Data is typed by {@link #USER_TYPE}.
	 * 
	 * @param username The name of the requested user
	 * @return The User, see {@link #USER_TYPE}
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the <code>username</code> is not a
	 *         User
	 */
	CompositeData getUser(String username) throws IOException;

	/**
	 * Answer the user name with the given property key-value pair from the User
	 * Admin service database.
	 * 
	 * @param key The key to compare
	 * @param value The value to compare
	 * @return The User
	 * @throws IOException if the operation fails
	 */
	String getUserWithProperty(String key, String value) throws IOException;

	/**
	 * Answer the list of user names in the User Admin database
	 * 
	 * @return The list of user names
	 * @throws IOException if the operation fails
	 */
	String[] listUsers() throws IOException;

	/**
	 * Answer the list of user names in the User Admin database
	 * 
	 * @param filter The filter to apply
	 * @return The list of user names
	 * @throws IOException if the operation fails
	 */
	String[] getUsers(String filter) throws IOException;

	/**
	 * Remove the credential associated with the given user
	 * 
	 * @param key The key of the credential to remove
	 * @param username The name of the user for which the credential must be
	 *        removed
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the username is not a User
	 */
	void removeCredential(String key, String username) throws IOException;

	/**
	 * Remove a role from the group
	 * 
	 * @param groupname The group name
	 * @param rolename
	 * @return true if the role was removed from the group
	 * @throws IOException if the operation fails
	 * @throws IllegalArgumentException if the groupname is not a Group
	 */
	boolean removeMember(String groupname, String rolename) throws IOException;

	/**
	 * Remove a property from a role
	 * 
	 * @param key
	 * @param rolename
	 * @throws IOException if the operation fails
	 */
	void removeProperty(String key, String rolename) throws IOException;

	/**
	 * Remove the Role associated with the name
	 * 
	 * @param name
	 * @return true if the remove succeeded
	 * @throws IOException if the operation fails
	 */
	boolean removeRole(String name) throws IOException;

	/**
	 * Remove the Group associated with the name
	 * 
	 * @param name
	 * @return true if the remove succeeded
	 * @throws IOException if the operation fails
	 */
	boolean removeGroup(String name) throws IOException;

	/**
	 * Remove the User associated with the name
	 * 
	 * @param name
	 * @return true if the remove succeeded
	 * @throws IOException if the operation fails
	 */
	boolean removeUser(String name) throws IOException;

}