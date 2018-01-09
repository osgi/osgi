/*
 * Copyright (c) OSGi Alliance (2001, 2018). All Rights Reserved.
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

package org.osgi.service.metatype;

import java.io.IOException;
import java.io.InputStream;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * Description for the data type information of an objectclass.
 * 
 * @ThreadSafe
 * @author $Id$
 */
@ConsumerType
public interface ObjectClassDefinition {
	/**
	 * Argument for {@code getAttributeDefinitions(int)}.
	 * <p>
	 * {@code REQUIRED} indicates that only the required definitions are
	 * returned. The value is 1.
	 */
	public static final int	REQUIRED	= 1;
	/**
	 * Argument for {@code getAttributeDefinitions(int)}.
	 * <p>
	 * {@code OPTIONAL} indicates that only the optional definitions are
	 * returned. The value is 2.
	 */
	public static final int	OPTIONAL	= 2;
	/**
	 * Argument for {@code getAttributeDefinitions(int)}.
	 * <p>
	 * {@code ALL} indicates that all the definitions are returned. The value is
	 * -1.
	 */
	public static final int	ALL			= 0xFFFFFFFF;

	/**
	 * Return the name of this object class.
	 * 
	 * The name may be localized.
	 * 
	 * @return The name of this object class.
	 */
	public String getName();

	/**
	 * Return the id of this object class.
	 * <p>
	 * {@code ObjectDefintion} objects share a global namespace in the registry.
	 * They share this aspect with LDAP/X.500 attributes. In these standards the
	 * OSI Object Identifier (OID) is used to uniquely identify object classes.
	 * If such an OID exists, (which can be requested at several standard
	 * organizations and many companies already have a node in the tree) it can
	 * be returned here. Otherwise, a unique id should be returned which can be
	 * a Java class name (reverse domain name) or generated with a GUID
	 * algorithm. Note that all LDAP defined object classes already have an OID
	 * associated. It is strongly advised to define the object classes from
	 * existing LDAP schemes which will give the OID for free. Many such schemes
	 * exist ranging from postal addresses to DHCP parameters.
	 * 
	 * @return The id of this object class.
	 */
	public String getID();

	/**
	 * Return a description of this object class.
	 * 
	 * The description may be localized.
	 * 
	 * @return The description of this object class.
	 */
	public String getDescription();

	/**
	 * Return the attribute definitions for this object class.
	 * 
	 * <p>
	 * Return a set of attributes. The filter parameter can distinguish between
	 * {@code ALL},{@code REQUIRED} or the {@code OPTIONAL} attributes.
	 * 
	 * @param filter {@code ALL},{@code REQUIRED},{@code OPTIONAL}
	 * @return An array of attribute definitions or {@code null} if no
	 *         attributes are selected
	 */
	public AttributeDefinition[] getAttributeDefinitions(int filter);

	/**
	 * Return an {@code InputStream} object that can be used to create an icon
	 * from.
	 * 
	 * <p>
	 * Indicate the size and return an {@code InputStream} object containing an
	 * icon. The returned icon maybe larger or smaller than the indicated size.
	 * 
	 * <p>
	 * The icon may depend on the localization.
	 * 
	 * @param size Requested size of an icon. For example, a 16x16 pixel icon
	 *        has a size of 16
	 * @return An InputStream representing an icon or {@code null}
	 * @throws IOException If the {@code InputStream} cannot be returned.
	 */
	public InputStream getIcon(int size) throws IOException;
}
