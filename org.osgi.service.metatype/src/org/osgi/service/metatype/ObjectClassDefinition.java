/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2001, 2004). All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS IS"
 * basis and OSGi Alliance DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.service.metatype;

import java.io.IOException;
import java.io.InputStream;

/**
 * Description for the data type information of an objectclass.
 * 
 * @version $Revision$
 */
public interface ObjectClassDefinition {
	/**
	 * Argument for <code>getAttributeDefinitions(int)</code>.
	 * <p>
	 * <code>REQUIRED</code> indicates that only the required definitions are
	 * returned. The value is 1.
	 */
	final int	REQUIRED	= 1;
	/**
	 * Argument for <code>getAttributeDefinitions(int)</code>.
	 * <p>
	 * <code>OPTIONAL</code> indicates that only the optional definitions are
	 * returned. The value is 2.
	 */
	final int	OPTIONAL	= 2;
	/**
	 * Argument for <code>getAttributeDefinitions(int)</code>.
	 * <p>
	 * <code>ALL</code> indicates that all the definitions are returned. The value
	 * is -1.
	 */
	final int	ALL			= 0xFFFFFFFF;

	/**
	 * Return the name of this object class.
	 * 
	 * The name may be localized.
	 * 
	 * @return The name of this object class.
	 */
	String getName();

	/**
	 * Return the id of this object class.
	 * 
	 * <p>
	 * <code>ObjectDefintion</code> objects share a global namespace in the
	 * registry. They share this aspect with LDAP/X.500 attributes. In these
	 * standards the OSI Object Identifier (OID) is used to uniquely identify
	 * object classes. If such an OID exists, (which can be requested at several
	 * standard organisations and many companies already have a node in the
	 * tree) it can be returned here. Otherwise, a unique id should be returned
	 * which can be a java class name (reverse domain name) or generated with a
	 * GUID algorithm. Note that all LDAP defined object classes already have an
	 * OID associated. It is strongly advised to define the object classes from
	 * existing LDAP schemes which will give the OID for free. Many such schemes
	 * exist ranging from postal addresses to DHCP parameters.
	 * 
	 * @return The id of this object class.
	 */
	String getID();

	/**
	 * Return a description of this object class.
	 * 
	 * The description may be localized.
	 * 
	 * @return The description of this object class.
	 */
	String getDescription();

	/**
	 * Return the attribute definitions for this object class.
	 * 
	 * <p>
	 * Return a set of attributes. The filter parameter can distinguish between
	 * <code>ALL</code>,<code>REQUIRED</code> or the <code>OPTIONAL</code>
	 * attributes.
	 * 
	 * @param filter <code>ALL</code>,<code>REQUIRED</code>,<code>OPTIONAL</code>
	 * @return An array of attribute definitions or <code>null</code> if no
	 *         attributes are selected
	 */
	AttributeDefinition[] getAttributeDefinitions(int filter);

	/**
	 * Return an <code>InputStream</code> object that can be used to create an
	 * icon from.
	 * 
	 * <p>
	 * Indicate the size and return an <code>InputStream</code> object containing
	 * an icon. The returned icon maybe larger or smaller than the indicated
	 * size.
	 * 
	 * <p>
	 * The icon may depend on the localization.
	 * 
	 * @param size Requested size of an icon, e.g. a 16x16 pixels icon then size =
	 *        16
	 * @return An InputStream representing an icon or <code>null</code>
	 */
	InputStream getIcon(int size) throws IOException;
}
